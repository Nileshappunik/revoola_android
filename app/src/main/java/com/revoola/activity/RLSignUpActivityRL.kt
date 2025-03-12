package com.revoola.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
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
//import com.revoola.BuildConfig
import com.revoola.R
import com.revoola.ble.base.RLBaseActivity
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databasefirebase.RLDatabaseManagerWrite
import com.revoola.databinding.RlActivitySignUpBinding
import com.revoola.databinding.RlDialogHelpSigninBinding
import com.revoola.commonobject.RLTools
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.revoola.utils.RLPrefManager
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
    var  userId=""
    lateinit var  authManager: RLAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        RLScreenSet(false)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = RLinflateBindLayout(this, R.layout.rl_activity_sign_up) as RlActivitySignUpBinding
        RLUisetup()
    }
    private fun RLUisetup() {
        activityBinding.toolbarLogin.tvTitle.setText(R.string.signup)
        RLonBackPresAct(activityBinding.toolbarLogin.ivBack)
        RLGetFcmToken()

        firstName= intent.getStringExtra("firstName").toString()
        lastName= intent.getStringExtra("lastName").toString()
        nickName= intent.getStringExtra("nickName").toString()
        activityBinding.edFirstname.setText(firstName)
        activityBinding.edLastname.setText(lastName)
        activityBinding.edNickname.setText(nickName)

        //Read DataBase
        val databaseManager: RLDatabaseManagerRead = RLDatabaseManagerRead()
        authManager = RLAuthManager()
         userId = authManager.RlgetCurrentUser()!!.uid
        emailId = RLPrefManager.RLGetSomeStringValue(this, RLPrefManager.current_user_email,"")
        if (emailId.isNullOrEmpty()){
            emailId = authManager.RlgetCurrentUser()!!.email.toString()
        }
        activityBinding.tvLogin.setOnClickListener(View.OnClickListener {
            RLRevoolaUserSettingWrite()
        })

        activityBinding.imgHelp.setOnClickListener(View.OnClickListener {
            RLshowHelpDialog()
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

        activityBinding.edFirstname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 0) {
                    activityBinding.tvLogin.visibility=View.GONE
                    activityBinding.tvLoginNoClick.visibility=View.VISIBLE
                    activityBinding.edFirstname.setCompoundDrawablesWithIntrinsicBounds(null, null,null, null)
                }else{
                    val drawable = getDrawable(R.drawable.ic_check)
                    activityBinding.edFirstname.setCompoundDrawablesWithIntrinsicBounds(null, null,drawable, null)
                    RLvalidation()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        activityBinding.edLastname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 0) {
                    activityBinding.tvLogin.visibility=View.GONE
                    activityBinding.tvLoginNoClick.visibility=View.VISIBLE
                    activityBinding.edLastname.setCompoundDrawablesWithIntrinsicBounds(null, null,null, null)
                }else{
                    val drawable = getDrawable(R.drawable.ic_check)
                    activityBinding.edLastname.setCompoundDrawablesWithIntrinsicBounds(null, null,drawable, null)
                    RLvalidation()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        activityBinding.edNickname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 0) {
                    activityBinding.tvLogin.visibility=View.GONE
                    activityBinding.tvLoginNoClick.visibility=View.VISIBLE
                    activityBinding.edNickname.setCompoundDrawablesWithIntrinsicBounds(null, null,null, null)
                }else{
                    val drawable = getDrawable(R.drawable.ic_check)
                    activityBinding.edNickname.setCompoundDrawablesWithIntrinsicBounds(null, null,drawable, null)
                    RLvalidation()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
    private fun RLGetFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
            }else{
                // Get new FCM registration token
                tokenFCM = task.result
                // Log and toast
                RLTools.RlLogDPrint(TAG, "FCM Token: $tokenFCM")
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
        val valuesfeet= com.revoola.utils.RLConstants.valuesFeet
        val valuesinches= com.revoola.utils.RLConstants.valuesInches
        val valuesmatric= com.revoola.utils.RLConstants.valuesMatricHeight

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
            RLvalidation()
            if (displayheight.isEmpty()){
                activityBinding.edHeight.setCompoundDrawablesWithIntrinsicBounds(null, null,null, null)
            }
            sucDialog!!.dismiss()
        })

        tvYes.setOnClickListener(View.OnClickListener {
            activityBinding.edHeight.setText(displayheight)
            val drawable = getDrawable(R.drawable.ic_check)
            activityBinding.edHeight.setCompoundDrawablesWithIntrinsicBounds(null, null,drawable, null)
            RLvalidation()
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
        val valuesuspounds = com.revoola.utils.RLConstants.valuesUsPounds
        val valuesmatric = com.revoola.utils.RLConstants.valuesMatric
        val valuesukstonesst = com.revoola.utils.RLConstants.valuesUkStonesSt
        val valuesukstoneslb = com.revoola.utils.RLConstants.valuesUkStonesLb

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
            RLvalidation()
            if (displayweight.isEmpty()){
                activityBinding.edWeight.setCompoundDrawablesWithIntrinsicBounds(null, null,null, null)
            }
            sucDialog.dismiss()
        })

        tvYes.setOnClickListener(View.OnClickListener {
            activityBinding.edWeight.setText(displayweight)
            val drawable = getDrawable(R.drawable.ic_check)
            activityBinding.edWeight.setCompoundDrawablesWithIntrinsicBounds(null, null,drawable, null)
            RLvalidation()
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
            RLvalidation()
            if (selectedGender.isEmpty()){
                activityBinding.edGender.setCompoundDrawablesWithIntrinsicBounds(null, null,null, null)
            }
            sucDialog.dismiss()
        })

        tvYes.setOnClickListener(View.OnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            val radioButton = sucDialog.findViewById<RadioButton>(selectedId)
             selectedGender = radioButton.text.toString()
            activityBinding.edGender.setText(selectedGender)
            val drawable = getDrawable(R.drawable.ic_check)
            activityBinding.edGender.setCompoundDrawablesWithIntrinsicBounds(null, null,drawable, null)
            RLvalidation()
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
            activityBinding.tvLogin.visibility=View.GONE
            activityBinding.tvLoginNoClick.visibility=View.VISIBLE
            return false
        }else if (lastName.isEmpty()) {
            activityBinding.tvLogin.visibility=View.GONE
            activityBinding.tvLoginNoClick.visibility=View.VISIBLE
            return false
        }else if (nickName.isEmpty()) {
            activityBinding.tvLogin.visibility=View.GONE
            activityBinding.tvLoginNoClick.visibility=View.VISIBLE
            return false
        }else if (DateTime.isEmpty()) {
            activityBinding.tvLogin.visibility=View.GONE
            activityBinding.tvLoginNoClick.visibility=View.VISIBLE
            return false
        }else if (selectedGender.isEmpty()) {
            activityBinding.tvLogin.visibility=View.GONE
            activityBinding.tvLoginNoClick.visibility=View.VISIBLE
            return false
        }else if (displayheight.isEmpty()) {
            activityBinding.tvLogin.visibility=View.GONE
            activityBinding.tvLoginNoClick.visibility=View.VISIBLE
            return false
        }else if (displayweight.isEmpty()) {
            activityBinding.tvLogin.visibility=View.GONE
            activityBinding.tvLoginNoClick.visibility=View.VISIBLE
            return false
        }/*else if (chooseimagefile.isEmpty()) {
            activityBinding.tvLogin.visibility=View.GONE
            activityBinding.tvLoginNoClick.visibility=View.VISIBLE
            return false
        }*/
        activityBinding.tvLogin.visibility=View.VISIBLE
        activityBinding.tvLoginNoClick.visibility=View.GONE
        return true
    }
    //FIREBASE NEW USER ENTRY
    private fun RLRevoolaUserSettingWrite() {
        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()
       val databaseManager = RLDatabaseManagerWrite()
        val userId =authManager.RlgetCurrentUser()!!.uid
        val myAge= RLTools.RLCalculateAge(DateTime)?:0
        val versionName: String = try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName ?: "0"
        } catch (e: Exception) {
            "0"
        }
        //val versionName:String = BuildConfig.VERSION_NAME?:"0"

        //RevoolaUsersForSearch ENTRY
        val revoolaUserForSearchMap = hashMapOf(
            "displayImage" to chooseimagefile,
            "emailId" to emailId,
            "firstName" to firstName,
            "lastName" to lastName,
            "name" to nickName,
            "remark" to "Android",
            "userId" to userId)
        databaseManager.REVOOLAUSERFORSEARCHWrite(userId,revoolaUserForSearchMap) { success, error ->
            if (success) {
               RLTools.RlLogDPrint(TAG,"RevoolaUsersForSearch Successful Entry")
            }
        }

        //REVOOLAUSERSETTINGS ENTRY
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
            "timestamp" to currentTimestamp,
            "validDays" to 14)

        val revoolaUserSettingsMap = hashMapOf(
            "FCMToken" to tokenFCM,
            "RFMHR" to 220-myAge,
            "TMHR" to 220-myAge,
            "AMHR" to 220-myAge,
            "emailId" to emailId,
            "appUnit" to "Imperial",
            "currentGroup" to "freemium",
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
            "joiningDate" to currentTimestamp,//first time user create then date
            "lastHRChange" to 0,
            " lastHRChange90" to 0,
            " lastHRUsed" to 0,
            "lastLogin" to currentTimestamp,
            "lastName" to lastName,
            "gender" to selectedGender,
            "lastVersion" to versionName,//current app version
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
            val leaderboardPath = userId+"leaderboard.png"
            val mainPath = userId+"main.png"

            val leaderboardref = FirebaseStorage.getInstance().reference.child(leaderboardPath)
            val uploadleaderboard = leaderboardref.putFile(filePath)
            uploadleaderboard.addOnSuccessListener {
                leaderboardref.downloadUrl.addOnSuccessListener { uri ->
                    chooseimagefile = uri.toString()
                    RLvalidation()
                    RLTools.RlLogDPrint(TAG,"imageUrl leaderboardPath:- $chooseimagefile")
                }
            }.addOnFailureListener {
               RLopentoast("Failed to upload image")
            }

            val mainPathref = FirebaseStorage.getInstance().reference.child(mainPath)
            val uploadmainPath= mainPathref.putFile(filePath)
            uploadmainPath.addOnSuccessListener {
                mainPathref.downloadUrl.addOnSuccessListener { uri ->
                    RLTools.RlLogDPrint(TAG,"imageUrl mainPath:- $chooseimagefile")
                }
            }.addOnFailureListener {
                RLopentoast("Failed to upload image")
            }
        }
    }
    //DATE SELECT
    private fun RLdialogStartDatePicker() {
        val currentDate: Calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            this,
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
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
        val drawable = getDrawable(R.drawable.ic_check)
        activityBinding.edDateofbirth.setCompoundDrawablesWithIntrinsicBounds(null, null,drawable, null)
        RLvalidation()
    }
    private fun RLisStoragePermissionGranted(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return cameraPermission == PackageManager.PERMISSION_GRANTED && storagePermission == PackageManager.PERMISSION_GRANTED
    }
    private fun RLrequestStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
    }

    private fun RLshowHelpDialog() {
        val dialog: Dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: RlDialogHelpSigninBinding=
            RlDialogHelpSigninBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(false)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.rounded_dialog_background))

        dialogMainBinding.tvClose.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()

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