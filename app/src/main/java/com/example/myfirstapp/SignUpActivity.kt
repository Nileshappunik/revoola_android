package com.example.myfirstapp

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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.activity.ForgotPasswordActivity
import com.example.myfirstapp.activity.LoginEmailActivity
import com.example.myfirstapp.activity.MainActivity
import com.example.myfirstapp.api.ApiClientRet
import com.example.myfirstapp.base.BaseActivity
import com.example.myfirstapp.databinding.ActivityLoginEmailBinding
import com.example.myfirstapp.databinding.ActivitySignUpBinding
import com.example.myfirstapp.utils.Tools
import com.example.myfirstapp.viewmodel.MainRepository
import com.example.myfirstapp.viewmodel.MainViewModel
import com.example.myfirstapp.viewmodel.MainViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SignUpActivity : BaseActivity(),DatePickerDialog.OnDateSetListener   {
    val TAG: String = SignUpActivity::class.java.simpleName
    lateinit var activityBinding: ActivitySignUpBinding
    private lateinit var viewModel: MainViewModel
    private val STORAGE_PERMISSION_REQUEST_CODE = 100
    var chooseimagefile: File? =null
    var day: Int = 0
    var month: Int = 0
    var year: Int = 0
    var myDay: Int = 0
    var myMonth: Int = 0
    var myYear: Int = 0
    var Month: String = ""
    var Day: String = ""
    var DateTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = inflateBindLayout(this, R.layout.activity_sign_up) as ActivitySignUpBinding
        // Api call
        apiClientRetrofit = ApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = MainRepository(apiService)
        viewModel = ViewModelProvider(this, MainViewModelFactory(userRepository)).get(MainViewModel::class.java)
        Uisetup()
    }
    private fun Uisetup() {
        activityBinding.toolbarLogin.tvTitle.setText(R.string.signup)
        activityBinding.toolbarLogin.ivBack.visibility= View.VISIBLE
        onBackPresAct(activityBinding.toolbarLogin.ivBack)
        activityBinding.tvLogin.setOnClickListener(View.OnClickListener {
            /*if (validation()) {
                if (apiClientRetrofit.isConnected) {

                } else {
                    //showDialogFullscreen()
                }
            }*/
            startActivity(Intent(this,MainActivity::class.java))
        })
        activityBinding.imgUseriamge.setOnClickListener {
            opencameragallerydialog()
        }

        activityBinding.edDateofbirth.setOnClickListener {
            dialogStartDatePicker()
        }

        activityBinding.edGender.setOnClickListener {
            showGenderDialog()
        }

        activityBinding.edHeight.setOnClickListener {
            showHeightDialog()
        }

        activityBinding.edWeight.setOnClickListener {
            showWeightDialog()
        }

    }

    private fun showWeightDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_weight_selection, null)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)
        //val poundsLayout = dialogView.poundsLayout


        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
            .setTitle("Select Weight")
            .setPositiveButton("OK") { dialog, _ ->
                val selectedId = radioGroup.checkedRadioButtonId
                val radioButton = dialogView.findViewById<RadioButton>(selectedId)
                val selectedUnit = radioButton.text.toString()

               /* val weight = when (selectedUnit) {
                    "Pounds" -> {
                        val pounds = poundsLayout.poundsEditText.text.toString().toIntOrNull() ?: 0
                        "$pounds lbs"
                    }
                    "Stones (UK)" -> {
                        val stones = stonesLayout.stonesEditText.text.toString().toIntOrNull() ?: 0
                        val pounds = stones * 14
                        "$stones stones ($pounds lbs)"
                    }
                    "Metric (kg)" -> {
                        val kilograms = metricLayout.kilogramsEditText.text.toString().toIntOrNull() ?: 0
                        "$kilograms kg"
                    }
                    else -> ""
                }*/

                //Toast.makeText(this, "Selected weight: $weight", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)

        val dialog = builder.create()
        dialog.show()
    }
    private fun showHeightDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_height_selection, null)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)
      //  val feetAndInchesLayout = dialogView.feetAndInchesLayout

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
            .setTitle("Select Height")
            .setPositiveButton("OK") { dialog, _ ->
                val selectedId = radioGroup.checkedRadioButtonId
                val radioButton = dialogView.findViewById<RadioButton>(selectedId)
                val selectedUnit = radioButton.text.toString()

                /*val height = when (selectedUnit) {
                    "Feet & Inches" -> {
                        val feet = feetAndInchesLayout.feetEditText.text.toString().toIntOrNull() ?: 0
                        val inches = feetAndInchesLayout.inchesEditText.text.toString().toIntOrNull() ?: 0
                        "$feet feet $inches inches"
                    }
                    "Metric" -> {
                        val centimeters = metricLayout.centimetersEditText.text.toString().toIntOrNull() ?: 0
                        "$centimeters cm"
                    }
                    else -> ""
                }*/

              //  Toast.makeText(this, "Selected height: $height", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)

        val dialog = builder.create()
        dialog.show()
    }

    private fun showGenderDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_gender_selection, null)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
            .setTitle("Gender")
            .setPositiveButton("OK") { dialog, _ ->
                val selectedId = radioGroup.checkedRadioButtonId
                val radioButton = dialogView.findViewById<RadioButton>(selectedId)
                val selectedGender = radioButton.text.toString()
                activityBinding.edGender.setText(selectedGender)
               // Perform any action based on the selected gender
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)

        val dialog = builder.create()
        dialog.show()
    }

    private fun opentoast(messageprint: String) {
        Toast.makeText(this,messageprint, Toast.LENGTH_SHORT).show()
    }

    private fun validation(): Boolean {
       /* emailID = activityBinding.etemailid.text.toString().trim()
        password = activityBinding.etPassword.text.toString().trim()

        if (emailID.isEmpty()) {
            activityBinding.etemailid.setError("Please Enter a EmailId")
            activityBinding.etemailid.requestFocus()
            return false
        }else if (!Tools.isEmailValid(emailID)) {
            activityBinding.etemailid.setError("Please Enter a valid EmailId")
            activityBinding.etemailid.requestFocus()
            return false
        } else if (password.isEmpty()) {
            activityBinding.etPassword.setError("Please Enter a Password")
            activityBinding.etPassword.requestFocus()
            return false
        }*/
        return true
    }

    private fun opencameragallerydialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        AlertDialog.Builder(activity)
            .setTitle("Choose Image")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> takePhoto()
                    1 -> chooseFromGallery()
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun takePhoto() {
        if (!isStoragePermissionGranted()) {
            // Request the permission
            requestStoragePermission()
        } else {
            // Permission is already granted, you can proceed with your code
            val pickImg = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            changeImageCamera.launch(pickImg)
        }
    }

    private  fun chooseFromGallery() {
        val pickImg = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        changeImage.launch(pickImg)
    }

    //Image select
    val changeImage =registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            val imgUri= data?.data
            chooseimagefile=uriToFile(this,imgUri)
            activityBinding.imgUseriamge.setImageURI(imgUri)

        }
    }
    val changeImageCamera =registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            val imageBitmap = data?.extras?.get("data") as Bitmap
            chooseimagefile=saveBitmapToFile(imageBitmap)
            activityBinding.imgUseriamge.setImageBitmap(imageBitmap)

        }
    }
    //Uri to File converter
    fun uriToFile(context: FragmentActivity?, uri: Uri?): File? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context?.contentResolver?.query(uri!!, projection, null, null, null) ?: return null
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val filePath = cursor.getString(columnIndex)
        cursor.close()
        return File(filePath)
    }
    //Bitmapimage to File converter
    fun saveBitmapToFile(bitmap: Bitmap): File? {
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
            Log.e("CAMERAIMAGHE","ERROR=="+e.localizedMessage)
            return null
        }
    }

    //DATE SELECT
    private fun dialogStartDatePicker() {
        val calendar: Calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
        val datePickerDialog = DatePickerDialog(this, this, day, month, year)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        myYear = year
        myMonth = month + 1
        Log.d("myMonth", "" + myMonth)
        myDay = day
        Log.d("myDay", "" + myDay)

        Month = if (myMonth < 10) {
            "0$myMonth"
        } else {
            myMonth.toString()
        }

        Day = if (myDay < 10) {
            "0$myDay"
        } else {
            myDay.toString()
        }

         DateTime = "" + Day + "-" + Month + "-" + myYear

        activityBinding.edDateofbirth.setText(DateTime).toString()
    }

    open fun isStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    open fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, you can proceed with your code
                val pickImg = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                changeImageCamera.launch(pickImg)
            } else {
                // Permission is denied
                // You may want to show a message or handle the case where the user denies the permission
               opentoast("Permission is denied")
            }
        }
    }
}