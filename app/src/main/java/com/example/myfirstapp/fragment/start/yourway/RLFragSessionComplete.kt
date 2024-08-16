package com.example.myfirstapp.fragment.start.yourway

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlFragSessionCompleteBinding
import com.example.myfirstapp.fragment.overview.RLFragOverview
import com.example.myfirstapp.fragment.overview.RLFragOverviewSession
import com.example.myfirstapp.fragment.start.adapter.RLSelectedImagesAdapter

import com.example.myfirstapp.utils.RLPrefManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import gun0912.tedimagepicker.builder.TedImagePicker

class RLFragSessionComplete : RLBaseFragment(){
    val TAG: String = RLFragSessionComplete::class.java.simpleName
    lateinit var fragBinding: RlFragSessionCompleteBinding
    var imgUriList = mutableListOf<Uri>()
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragSessionComplete()
        fragment.arguments = bundle
        return fragment
    }
    private val binding by lazy {
        RlFragSessionCompleteBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_session_complete, container) as RlFragSessionCompleteBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSessionComplete" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        //"Pilates","Ride","Run","Walk","Workout","Yoga" ic_pace ic_speeed
        val yourWayType = requireArguments().getString("YourWayType").toString().trim()
        fragBinding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
            // Handle checked change
        }
        fragBinding.layPrivacy.setOnClickListener {
            val titletxt:String=fragBinding.tvShareTitle.text.toString().toUpperCase()

            if (titletxt.equals("FRIENDS")){
                fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyPrivateBGColor))
                fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyprivate)
                fragBinding.tvShareTitle.setText(R.string.privatetx)
                fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyPrivateColor))
                RLShareMapHide(false)
            }else if (titletxt.equals("EVERYONE")){
                fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyFriendsBGColor))
                fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyfriends)
                fragBinding.tvShareTitle.setText(R.string.friendstx)
                fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyFriendsColor))
                RLShareMapHide(true)
            }else if (titletxt.equals("PRIVATE")){
                fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyEveryOneBGColor))
                fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyeveryone)
                fragBinding.tvShareTitle.setText(R.string.everyone)
                fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyEveryOneColor))
                RLShareMapHide(true)
            }

        }

        fragBinding.imgCancle.setOnClickListener {
            RLBottomHideShowSet(true)
            (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
            (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
        }
        fragBinding.tvSave.setOnClickListener {
            RLBottomHideShowSet(true)
            (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
            (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
        }
        fragBinding.txtAddPhoto.setOnClickListener {
            RLchooseFromGallery()
        }
    }
    private fun RLShareMapHide(isVisible:Boolean){
        if (isVisible){
            fragBinding.txtShareMap.visibility=View.VISIBLE
            fragBinding.switchCompat.visibility=View.VISIBLE
        }else{
            fragBinding.txtShareMap.visibility=View.GONE
            fragBinding.switchCompat.visibility=View.GONE
        }
    }
    private fun RLchooseFromGallery() {
        TedImagePicker.with(requireContext())
            .max(5, "maximum limit to 5 images") // Set the maximum limit to 5 images
            .showCameraTile(false)
            .startMultiImage { uriList ->
                // Handle the selected images here

                for (uri in uriList) {
                    imgUriList.add(uri)
                }
                if (imgUriList.size>0){
                    imageListVisible(true)
                }else{
                    imageListVisible(false)
                }
                fragBinding.rvSelectedImages.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
               val selectedImagesAdapter = RLSelectedImagesAdapter(imgUriList) { uri ->
                   imgUriList.remove(uri)
                   if (imgUriList.size>0){
                       imageListVisible(true)
                   }else{
                       imageListVisible(false)
                   }
                }
                fragBinding.rvSelectedImages.adapter = selectedImagesAdapter

            }
    }
    private fun imageListVisible(isVisible: Boolean){
        if (isVisible){
            fragBinding.rvSelectedImages.visibility=View.VISIBLE
            fragBinding.txtAddPhoto.visibility=View.GONE
        }else{
            fragBinding.rvSelectedImages.visibility=View.GONE
            fragBinding.txtAddPhoto.visibility=View.VISIBLE
        }
    }
    private fun uploadImagesToFirebase(imageUris: List<Uri>) {
        val storageReference = FirebaseStorage.getInstance().reference
        val databaseReference = FirebaseDatabase.getInstance().reference.child("images")

        for (uri in imageUris) {
            val fileName = System.currentTimeMillis().toString() + ".jpg"
            val fileReference = storageReference.child("uploads/$fileName")

            fileReference.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    fileReference.downloadUrl.addOnSuccessListener { downloadUri ->
                        saveImageUrlToDatabase(downloadUri.toString(), databaseReference)
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                    RLcommonToast( "Upload failed: ${exception.message}")
                }
        }
    }
    private fun saveImageUrlToDatabase(downloadUrl: String, databaseReference: DatabaseReference) {
        val imageId = databaseReference.push().key // Generate a unique ID for each image
        imageId?.let {
            databaseReference.child(it).setValue(downloadUrl)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLcommonToast("Image uploaded successfully!")
                    } else {
                        RLcommonToast("Failed to upload image URL to database.")
                    }
                }
        }
    }

}