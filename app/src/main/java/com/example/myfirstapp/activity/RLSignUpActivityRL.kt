package com.example.myfirstapp.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.DatePicker
import android.widget.NumberPicker
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.myfirstapp.R
import com.example.myfirstapp.base.RLBaseActivity
import com.example.myfirstapp.databasefirebase.RLAuthManager
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerRead
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerWrite
import com.example.myfirstapp.databinding.RlActivitySignUpBinding
import com.example.myfirstapp.model.RLRevoolaSearchUserModel
import com.example.myfirstapp.utils.RLConstants
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.util.*

class RLSignUpActivityRL : RLBaseActivity(),DatePickerDialog.OnDateSetListener   {
    val TAG: String = RLSignUpActivityRL::class.java.simpleName
    lateinit var activityBinding: RlActivitySignUpBinding
    private val STORAGE_PERMISSION_REQUEST_CODE = 100
    var chooseimagefile:String=""
    var day: Int = 0
    var month: Int = 0
    var year: Int = 0
    var DateTime: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var nickName: String = ""
    var emailId: String = ""
    var tokenFCM=""
    var selectedGender=""
    var displayheight:String=""
    var displayweight:String=""
    var heightUnit:String=""
    var weightUnit:String=""
    lateinit var  authManager:RLAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = RLinflateBindLayout(this, R.layout.rl_activity_sign_up) as RlActivitySignUpBinding
        RLUisetup()
    }
    private fun RLUisetup() {
        activityBinding.toolbarLogin.tvTitle.setText(R.string.signup)
        activityBinding.toolbarLogin.ivBack.visibility= View.VISIBLE
        RLonBackPresAct(activityBinding.toolbarLogin.ivBack)
        RLGetFcmToken()
        //Read DataBase
        val databaseManager:RLDatabaseManagerRead= RLDatabaseManagerRead()
        authManager = RLAuthManager()
        val userId = authManager.RlgetCurrentUser()!!.uid
        databaseManager.RLREVOOLAUSERFORSEARCHREADDATE(userId){ data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                val userData = gson.fromJson(jsonObject, RLRevoolaSearchUserModel::class.java)
                activityBinding.edFirstname.setText(userData.firstName)
                activityBinding.edLastname.setText(userData.lastName)
                activityBinding.edNickname.setText(userData.name)
                emailId=userData.emailId
                Log.e(TAG,"Response:- "+jsonObject)
            } else {
                Toast.makeText(this, "Read failed: ${error?.message}", Toast.LENGTH_SHORT).show()
            }
        }
        activityBinding.tvLogin.setOnClickListener(View.OnClickListener {
            if (RLvalidation()) {
                RLRevoolaUserSettingWrite()
            }
        })
        activityBinding.imgUseriamge.setOnClickListener {
            RLopencameragallerydialog()
        }
        activityBinding.edDateofbirth.setOnClickListener {
            RLdialogStartDatePicker()
        }
        activityBinding.edGender.setOnClickListener {
            RLshowGenderDialog()
        }
        activityBinding.edHeight.setOnClickListener {
            RLshowHeightDialog()
        }
        activityBinding.edWeight.setOnClickListener {
            RLshowWeightDialog(activity)
        }
    }
    private fun RLGetFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
            }else{
                // Get new FCM registration token
                tokenFCM = task.result
                // Log and toast
                Log.d(TAG, "FCM Token: $tokenFCM")
                // Send token to your server or use it as needed
            }
        }
    }
    private fun RLshowHeightDialog() {
        var feet:String=""
        var inches:String=""
        val sucDialog: Dialog  = Dialog(activity)
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog.setContentView(R.layout.rl_dialog_height_selection)
        sucDialog.setCancelable(true)

        val tvNo: TextView = sucDialog.findViewById(R.id.tvNo)
        val tvYes: TextView = sucDialog.findViewById(R.id.tvYes)
        val radioGroup = sucDialog.findViewById<RadioGroup>(R.id.radioGroup)
        val numberPicker = sucDialog.findViewById<NumberPicker>(R.id.numberPicker)
        val numberPickerfeet = sucDialog.findViewById<NumberPicker>(R.id.numberPickerfeet)
        val numberPickerinches = sucDialog.findViewById<NumberPicker>(R.id.numberPickerinches)
        val rv_poundmatric = sucDialog.findViewById<RelativeLayout>(R.id.rv_poundmatric)
        val rv_ukstones = sucDialog.findViewById<RelativeLayout>(R.id.rv_ukstones)
        // Optionally set a default selection
        radioGroup.check(R.id.radioButtonfeetandinches)
        val valuesfeet=RLConstants.valuesFeet
        val valuesinches=RLConstants.valuesInches
        val valuesmatric=RLConstants.valuesMatricHeight

        rv_poundmatric.visibility=View.GONE
        rv_ukstones.visibility=View.VISIBLE
        numberPickerfeet.minValue = 0
        numberPickerfeet.maxValue = valuesfeet.size - 1
        numberPickerfeet.displayedValues = valuesfeet
        numberPickerfeet.setOnValueChangedListener { picker, oldVal, newVal ->
            //  val  selectedValueTextView = "Selected Value: ${values[newVal]}"
            feet=valuesfeet[newVal]
            displayheight=feet +" "+inches
            heightUnit="FeetInch"
        }
        numberPickerfeet.value = 4

        numberPickerinches.minValue = 0
        numberPickerinches.maxValue = valuesinches.size - 1
        numberPickerinches.displayedValues = valuesinches
        numberPickerinches.setOnValueChangedListener { picker, oldVal, newVal ->
            //  val  selectedValueTextView = "Selected Value: ${values[newVal]}"
            inches=valuesinches[newVal]
            displayheight=feet +" "+inches
            heightUnit="FeetInch"
        }
        numberPickerinches.value = 4

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId.equals(R.id.radioButtonfeetandinches)){
                heightUnit="FeetInch"
                rv_poundmatric.visibility=View.GONE
                rv_ukstones.visibility=View.VISIBLE
                numberPickerfeet.minValue = 0
                numberPickerfeet.maxValue = valuesfeet.size - 1
                numberPickerfeet.displayedValues = valuesfeet
                numberPickerfeet.setOnValueChangedListener { picker, oldVal, newVal ->
                    //  val  selectedValueTextView = "Selected Value: ${values[newVal]}"
                    feet=valuesfeet[newVal]
                    displayheight=feet +" "+inches
                }
                numberPickerfeet.value = 4

                numberPickerinches.minValue = 0
                numberPickerinches.maxValue = valuesinches.size - 1
                numberPickerinches.displayedValues = valuesinches
                numberPickerinches.setOnValueChangedListener { picker, oldVal, newVal ->
                    //  val  selectedValueTextView = "Selected Value: ${values[newVal]}"
                    inches=valuesinches[newVal]
                    displayheight=feet +" "+inches
                }
                numberPickerinches.value = 4
            }else if (checkedId.equals(R.id.radioButtonmetric)){
                rv_poundmatric.visibility=View.VISIBLE
                rv_ukstones.visibility=View.GONE
                numberPicker.minValue = 0
                numberPicker.maxValue = valuesmatric.size - 1
                numberPicker.displayedValues = valuesmatric
                numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                    displayheight=valuesmatric[newVal]

                    heightUnit="Metric"
                }
                numberPicker.value = 4
            }
        }



        tvNo.setOnClickListener(View.OnClickListener {
            sucDialog!!.dismiss()
        })

        tvYes.setOnClickListener(View.OnClickListener {
            activityBinding.edHeight.setText(displayheight)
            sucDialog!!.dismiss()
        })

        sucDialog!!.show()
        sucDialog!!.window!!.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
    }
    private fun RLshowWeightDialog(activity: Activity) {
        var st:String=""
        var lb:String=""
        var sucDialog: Dialog = Dialog(activity)
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog.setContentView(R.layout.rl_dialog_weight_selection)
        sucDialog.setCancelable(true)

        val tvNo: TextView = sucDialog.findViewById(R.id.tvNo)
        val tvYes: TextView = sucDialog.findViewById(R.id.tvYes)
        val radioGroup = sucDialog.findViewById<RadioGroup>(R.id.radioGroup)
        val numberPicker = sucDialog.findViewById<NumberPicker>(R.id.numberPicker)
        val numberPickerst = sucDialog.findViewById<NumberPicker>(R.id.numberPickerst)
        val numberPickerlb = sucDialog.findViewById<NumberPicker>(R.id.numberPickerlb)
        val rv_poundmatric = sucDialog.findViewById<RelativeLayout>(R.id.rv_poundmatric)
        val rv_ukstones = sucDialog.findViewById<RelativeLayout>(R.id.rv_ukstones)
        // Optionally set a default selection
        radioGroup.check(R.id.radioButtonPounds)
        // Define a string array for the NumberPicker with numbers and RLText
        val valuesuspounds =RLConstants.valuesUsPounds
        val valuesmatric =RLConstants.valuesMatric
        val valuesukstonesst =RLConstants.valuesUkStonesSt
        val valuesukstoneslb =RLConstants.valuesUkStonesLb

        rv_poundmatric.visibility=View.VISIBLE
        rv_ukstones.visibility=View.GONE
        numberPicker.minValue = 0
        numberPicker.maxValue = valuesuspounds.size - 1
        numberPicker.displayedValues = valuesuspounds
        numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            //  val  selectedValueTextView = "Selected Value: ${values[newVal]}"
            displayweight=valuesuspounds[newVal]
            activityBinding.edWeight.setText(valuesuspounds[newVal])
            weightUnit="Us Pounds"
        }
        numberPicker.value = 4

        // Set a listener to handle RadioGroup selection changes
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId.equals(R.id.radioButtonPounds)){
                rv_poundmatric.visibility=View.VISIBLE
                rv_ukstones.visibility=View.GONE
                numberPicker.minValue = 0
                numberPicker.maxValue = valuesuspounds.size - 1
                numberPicker.displayedValues = valuesuspounds
                numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                    displayweight=valuesuspounds[newVal]
                    weightUnit="Us Pounds"
                }
                numberPicker.value = 4
            }else if (checkedId.equals(R.id.radioButtonStones)){
                rv_poundmatric.visibility=View.GONE
                rv_ukstones.visibility=View.VISIBLE
                numberPickerst.minValue = 0
                numberPickerst.maxValue = valuesukstonesst.size - 1
                numberPickerst.displayedValues = valuesukstonesst
                numberPickerst.setOnValueChangedListener { picker, oldVal, newVal ->
                    st=valuesukstonesst[newVal]
                    displayweight=st+" "+lb
                    weightUnit="UK Stones"
                }
                numberPickerst.value = 4

                numberPickerlb.minValue = 0
                numberPickerlb.maxValue = valuesukstoneslb.size - 1
                numberPickerlb.displayedValues = valuesukstoneslb
                numberPickerlb.setOnValueChangedListener { picker, oldVal, newVal ->
                    lb=valuesukstoneslb[newVal]
                    displayweight=st+" "+lb
                    weightUnit="UK Stones"
                }
                numberPicker.value = 4
            }else if (checkedId.equals(R.id.radioButtonMetric)){
                rv_poundmatric.visibility=View.VISIBLE
                rv_ukstones.visibility=View.GONE
                numberPicker.minValue = 0
                numberPicker.maxValue = valuesmatric.size - 1
                numberPicker.displayedValues = valuesmatric
                numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                    displayweight=valuesmatric[newVal]
                    weightUnit="Metric"
                }
                numberPicker.value = 4
            }
        }


        tvNo.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })

        tvYes.setOnClickListener(View.OnClickListener {
            activityBinding.edWeight.setText(displayweight)
            sucDialog.dismiss()
        })
        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
    }
    private fun RLshowGenderDialog() {
        val sucDialog: Dialog = Dialog(activity)
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog.setContentView(R.layout.rl_dialog_gender_selection)
        sucDialog.setCancelable(true)

        val tvNo: TextView = sucDialog.findViewById(R.id.tvNo)
        val tvYes: TextView = sucDialog.findViewById(R.id.tvYes)
        val radioGroup = sucDialog.findViewById<RadioGroup>(R.id.radioGroup)
        radioGroup.check(R.id.radioButtonFemale)

        tvNo.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })

        tvYes.setOnClickListener(View.OnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            val radioButton = sucDialog.findViewById<RadioButton>(selectedId)
             selectedGender = radioButton.text.toString()
            activityBinding.edGender.setText(selectedGender)
            sucDialog.dismiss()
        })

        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
    }
    private fun RLopentoast(messageprint: String) {
        Toast.makeText(this,messageprint, Toast.LENGTH_SHORT).show()
    }
    private fun RLvalidation(): Boolean {
        firstName = activityBinding.edFirstname.text.toString().trim()
        lastName = activityBinding.edLastname.text.toString().trim()
        nickName = activityBinding.edNickname.text.toString().trim()

        if (firstName.isEmpty()) {
            activityBinding.edFirstname.setError("Please Enter a FirstName")
            activityBinding.edFirstname.requestFocus()
            return false
        }else if (lastName.isEmpty()) {
            activityBinding.edLastname.setError("Please Enter a LastName")
            activityBinding.edLastname.requestFocus()
            return false
        }else if (nickName.isEmpty()) {
            activityBinding.edNickname.setError("Please Enter a NickName")
            activityBinding.edNickname.requestFocus()
            return false
        }else if (DateTime.isEmpty()) {
            RLopentoast("Please Select a Date of Birth")
            return false
        }else if (selectedGender.isEmpty()) {
            RLopentoast("Please Select a Gender")
            return false
        }else if (displayheight.isEmpty()) {
            RLopentoast("Please Select a Height")
            return false
        }else if (displayweight.isEmpty()) {
            RLopentoast("Please Select a Weight")
            return false
        }else if (chooseimagefile.isEmpty()) {
            RLopentoast("Please Select a Profile Photo")
            return false
        }
        return true
    }
    private fun RLRevoolaUserSettingWrite() {
       val databaseManager = RLDatabaseManagerWrite()
        val userId =authManager.RlgetCurrentUser()!!.uid
        val currentSubscriptionMap = hashMapOf(
            "validDaysMonth" to 0,
            "inviteUserSubsModel" to "0",
            "referrerTag" to "Android",
            "permissionLevelAfterTrial" to "Free",
            "isTrialTaken" to true,
            "isSubscriptionRequired" to true,
            "commisionFlag" to "",
            "discountPeriodMonth" to 0,
            "remark" to "Android",
            "familyPrice" to 0,
            "onGoingPriceType" to "none",
            "onGoingPrice" to 0,
            "discountedPriceType" to "none",
            "discountedPrice" to 0,
            "isSubscriptionCheckRequired" to true,
            "subscriptionName" to "Trial-Premium",
            "inviteUserType" to "NormalUser",
            "plan" to "",
            "timestamp" to 1718872384,
            "validDays" to 14,)

        val revoolaUserSettingsMap = hashMapOf(
            "FCMToken" to tokenFCM,
            "RFMHR" to 176,
            "TMHR" to 176,
            "emailId" to emailId,
            "appUnit" to "Imperial",
            "currentGroup" to "premium",
            "displayImage" to chooseimagefile,
            "displayName" to nickName,
            "dob" to DateTime,
            "firstName" to firstName,
            "flagImage" to "flag-of-United-Kingdom.png",
            "flagName" to "United Kingdom",
            "heartRate" to 0,
            "height" to displayheight,
            "heightUnit" to heightUnit,
            "isBasicDataAdded" to true,
            "joiningDate" to 1718872384,
            "lastLogin" to 1718872633,
            "lastName" to lastName,
            "lastVersion" to "2.215",
            "leaderBoardImage" to chooseimagefile,
            "currentSubscription" to currentSubscriptionMap,
            "location" to "United Kingdom",
            "numberOfGhost" to "1",
            "power" to 0,
            "referUser" to "AndroidPlayStore",
            "referalCode" to "",
            "remark" to "Android",
            "restingHr" to "60",
            "totalRev" to 0,
            "visibilityflagforthatsession" to 0,
            "weightUnit" to weightUnit,
            "weightkg" to displayweight)
        databaseManager.REVOOLAUSERSETTINGSWrite(userId,revoolaUserSettingsMap) { success, error ->
            if (success) {
                startActivity(Intent(this, RLMainActivityRL::class.java))
                finish()
            } else {
                RLopentoast("User write operation failed: ${error?.message}")
            }
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
        if (!RLisStoragePermissionGranted()) {
            // Request the permission
            RLrequestStoragePermission()
        } else {
            // Permission is already granted, you can proceed with your code
            val pickImg = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            RLchangeImageCamera.launch(pickImg)
        }
    }
    private  fun RLchooseFromGallery() {
        val pickImg = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        RLchangeImage.launch(pickImg)
    }
    //Image select
    val RLchangeImage =registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            val imgUri= data?.data
            activityBinding.imgUseriamge.setImageURI(imgUri)
            RluploadImage(imgUri!!)
        }
    }
    val RLchangeImageCamera =registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            val imageBitmap = data?.extras?.get("data") as Bitmap
            activityBinding.imgUseriamge.setImageBitmap(imageBitmap)
            val tempUri = RLgetImageUri(this, imageBitmap)
            RluploadImage(tempUri)
        }
    }
    private fun RLgetImageUri(inContext: Activity, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }
    private fun RluploadImage(filePath:Uri) {
        if (filePath != null) {
            val ref = FirebaseStorage.getInstance().reference.child("images/" + UUID.randomUUID().toString())
            val uploadTask = ref.putFile(filePath!!)
            uploadTask.addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    chooseimagefile = uri.toString()
                    Log.d(TAG,"imageUrl:- $chooseimagefile")
                }
            }.addOnFailureListener {
               RLopentoast("Failed to upload image")
            }
        }
    }
    //DATE SELECT
    private fun RLdialogStartDatePicker() {
        val calendar: Calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
        val datePickerDialog = DatePickerDialog(this, this, day, month, year)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        var myMonth: Int = 0
        var Month: String = ""
        var Day: String = ""
        myMonth = month + 1
        Month = if (myMonth < 10) {
            "0$myMonth"
        } else {
            myMonth.toString()
        }
        Day = if (day < 10) {
            "0$day"
        } else {
            day.toString()
        }
         DateTime = "" + Day + "/" + Month + "/" + year
        activityBinding.edDateofbirth.setText(DateTime).toString()
    }
    private fun RLisStoragePermissionGranted(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return cameraPermission == PackageManager.PERMISSION_GRANTED && storagePermission == PackageManager.PERMISSION_GRANTED
    }
    private fun RLrequestStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
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
                RLopentoast("Permission is denied")
            }
        }
    }
}