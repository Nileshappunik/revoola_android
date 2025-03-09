package com.revoola.fragment.friends

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databinding.RlFragFriendsBinding
import com.revoola.enumclass.RLStartAllMenuModel
import com.revoola.fragment.friends.adapter.RLFriendListAdapter
import com.revoola.utils.RLConstants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.revoola.RLBaseProgress
import com.revoola.activity.RLMainActivityRL
import com.revoola.api.RLApiClientRet
import com.revoola.commonobject.RLTools
import com.revoola.databinding.RlFragCreateGroupBinding
import com.revoola.fragment.friends.adapter.RLCreateFriendListAdapter
import com.revoola.fragment.friends.adapter.RLSelectedFriendListAdapter
import com.revoola.fragment.friends.adapter.RLYourGroupListAdapter
import com.revoola.fragment.friends.model.RLCreateGroupModel
import com.revoola.fragment.start.challenges.RLFragChallengesFor
import com.revoola.model.RLUserDataParcelable
import com.revoola.model.RLrequestgroup_dataset
import com.revoola.model.RLsetgroup_data
import com.revoola.model.RLuserData
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class RLFragCreateGroup : RLBaseFragment() {
    val TAG: String = RLFragCreateGroup::class.java.simpleName
    lateinit var fragBinding: RlFragCreateGroupBinding
    lateinit var apiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""
    private var selectUserdata: List<RLUserDataParcelable> = mutableListOf()
    var imgUriList = mutableListOf<File>()

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragCreateGroup()
        fragment.arguments = bundle
        return fragment
    }
    companion object{
        private const val STORAGE_PERMISSION_REQUEST_CODE = 1001
    }

    private val binding by lazy {
        RlFragFriendsBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_create_group, container) as RlFragCreateGroupBinding
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragCreateGroup" )
        currentUser= RLPrefManager.RLGetSomeStringValue(activity, RLPrefManager.current_user, "")
        // Api call
        apiClientRetrofit = RLApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        RLuisetupNew()
        return fragBinding.root
    }
    private fun RLuisetupNew() {
        RLonBackPresAct(fragBinding.toolbar.ivBack)
        fragBinding.toolbar.tvTitle.setText(getString(R.string.creategroup))
        val cardData = requireArguments().getParcelable<RLCreateGroupModel>("cardData") as RLCreateGroupModel
        fragBinding.tvselectedCount.setText("${ cardData.selectFriendList.size.toString() } SELECTED")
        val linearLayoutMain = LinearLayoutManager(activity)
        fragBinding.ivFriendList.layoutManager = linearLayoutMain
        selectUserdata = cardData.selectFriendList
        val adapter = RLCreateFriendListAdapter(activity,cardData.selectFriendList){ cardData ->
            selectUserdata -= listOf(cardData)
            fragBinding.tvselectedCount.setText("${ selectUserdata.size.toString() } SELECTED")
        }
        fragBinding.ivFriendList.adapter = adapter

        fragBinding.tvCreateClick.setOnClickListener {
            if (fragBinding.ivGroupName.text.toString().isEmpty()){
                RLshowAlertDialog("Enter Group Name!")
            }else if (selectUserdata.isEmpty()){
                RLshowAlertDialog("No Friend Select!")
            }else{
                if(isAdded){
                    RLBaseProgress.RLShowProgressDialog(requireActivity())
                }
                RLCreateGroupApiCall()
            }
        }
        fragBinding.ivGroupImage.setOnClickListener {
            RLopencameragallerydialog()
        }

    }
    private fun RLCreateGroupApiCall() {
        val dataMap  = createGroupPayload()
        val images=getUserImages()
        RLTools.RlLogDPrint(TAG,"Create Group Request: $dataMap")
        viewModel.RLInsertGroupData(dataMap,images) { result ->
            result.onSuccess { response ->
                RLBaseProgress.RLhideProgressDialog()
                try {
                    if (response.type.equals("success")) {
                        (context as RLMainActivityRL).RLloadFrag(RLFragFriends(), TAG, false, null, false)
                        RLTools.RlLogDPrint(TAG, "Create Group Success: ${response.text}")
                    } else {
                        RLTools.RlLogEPrint(TAG, "Create Group Fail: ${response.text}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    RLTools.RlLogEPrint(TAG, "Create Group Catch: ${e.message}" )

                }
            }.onFailure { error ->
                RLBaseProgress.RLhideProgressDialog()
                RLTools.RlLogEPrint(TAG, "Create Group Error: ${error.message}" )
            }
        }
    }
    private fun createGroupPayload(): Map<String, RequestBody> {
        val groupName=fragBinding.ivGroupName.text.toString()
        val requestBodyMap = mutableMapOf<String, RequestBody>()
        // Add text fields as form data
        requestBodyMap["data[create_group][group_name]"] = createRequestBody(groupName)
        requestBodyMap["data[create_group][group_id]"] = createRequestBody(currentUser+generateUniqueKey())
        requestBodyMap["data[create_group][child_user][$currentUser]"] = createRequestBody("1")

        // Add selected friends
        for (friend in selectUserdata) {
            if (friend.userid != currentUser) {
                requestBodyMap["data[create_group][child_user][${friend.userid}]"] = createRequestBody("0")
            }
        }
        return requestBodyMap
    }
    private fun createRequestBody(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }
    private fun generateUniqueKey(): String {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 32)
    }
    private fun getUserImages(): List<MultipartBody.Part> {
        val imageParts = mutableListOf<MultipartBody.Part>()
        if (imgUriList.isEmpty()){
            return imageParts
        }else{
            imgUriList.forEachIndexed { index, imageFile ->
                val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("avatar_image", imageFile.name, requestFile)
                imageParts.add(imagePart)
            }
            return imageParts
        }
    }
    private fun RLopencameragallerydialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        AlertDialog.Builder(activity)
            .setTitle("Choose Image")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> RLtakePhoto()
                    1 -> RLchooseFromGallery()
                }
                dialog.dismiss()
            }
            .show()
    }
    private fun RLtakePhoto() {
        if (!RLisStoragePermissionGrantedd()) {
            // Request the permission
            RLrequestStoragePermissionn()
        } else {
            // Permission is already granted, you can proceed with your code
            val pickImg = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            RLchangeImageCamera.launch(pickImg)
        }
    }
    private  fun RLchooseFromGallery() {
        val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        RLchangeImage.launch(pickImg)
    }
    //Image select
    private val RLchangeImage =registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            val imgUri= data?.data
            val file=RLuriToFile(requireActivity(),imgUri)
            if (file!=null){
                imgUriList.add(file)
            }
            fragBinding.ivGroupImage.setImageURI(imgUri)

        }
    }
    private val RLchangeImageCamera =registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val file=RLsaveBitmapToFile(imageBitmap)
            if (file!=null){
                imgUriList.add(file)
            }
            fragBinding.ivGroupImage.setImageBitmap(imageBitmap)
        }
    }
    //Uri to File converter
    private fun RLuriToFile(context: FragmentActivity?, uri: Uri?): File? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context?.contentResolver?.query(uri!!, projection, null, null, null) ?: return null
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val filePath = cursor.getString(columnIndex)
        cursor.close()
        return File(filePath)
    }
    //Bitmapimage to File converter
    private fun RLsaveBitmapToFile(bitmap: Bitmap): File? {
        try {
            // Create a file to save the bitmap
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val imageFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
            // Write the bitmap data to the file
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return imageFile
        } catch (e: IOException) {
            e.printStackTrace()
            RLTools.RlLogEPrint("CAMERAIMAGHE","ERROR=="+e.localizedMessage)
            return null
        }
    }
    private fun RLisStoragePermissionGrantedd(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
        val storagePermission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return cameraPermission == PackageManager.PERMISSION_GRANTED && storagePermission == PackageManager.PERMISSION_GRANTED
    }
    private fun RLrequestStoragePermissionn() {
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),STORAGE_PERMISSION_REQUEST_CODE)
    }
    private fun RLopentoast(messageprint: String) {
        Toast.makeText(requireContext(),messageprint, Toast.LENGTH_SHORT).show()
    }
    // Handle permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, you can proceed with your code
                val pickImg = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                RLchangeImageCamera.launch(pickImg)
            } else {
                // Permission is denied
                // You may want to show a message or handle the case where the RLuser denies the permission
                RLopentoast("Permission is denied")
            }
        }
    }
    private fun RLshowAlertDialog(message:String) {
        val sucDialog: Dialog = Dialog(requireContext())
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog.setContentView(R.layout.rl_alertdialog_custom_layout)
        sucDialog.setCancelable(false)
        val iv_ok: TextView = sucDialog.findViewById(R.id.iv_ok)
        val iv_title: TextView = sucDialog.findViewById(R.id.iv_title)
        val iv_description: TextView = sucDialog.findViewById(R.id.iv_description)
        val view_v: View = sucDialog.findViewById(R.id.view_v)

        iv_title.visibility=View.GONE
        view_v.visibility=View.VISIBLE
        iv_description.setText(message)
        iv_ok.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })
        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
    }

}