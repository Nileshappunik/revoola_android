package com.revoola.fragment.more

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
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
import android.view.Window
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.*
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databasefirebase.RLDatabaseManagerWrite
import com.revoola.databasefirebase.RevoolaFirebasePath
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.revoola.utils.RLConstants
import com.revoola.utils.RLPrefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class RLFragProfile : RLBaseFragment(), DatePickerDialog.OnDateSetListener  {
    val TAG: String = RLFragProfile::class.java.simpleName
   // lateinit var fragBinding: RlFragProfileBinding
    private val STORAGE_PERMISSION_REQUEST_CODE = 1001
    private var chooseimagefile: File? =null
    private var userBasicDataCard: RLRevoolaUsersSettingsModel? =null
    private var myDay: Int = 0
    private var myMonth: Int = 0
    private var myYear: Int = 0
    private var Month: String = ""
    private var Day: String = ""
    private var DateTime: String = ""

    private val fragBinding by lazy {
        RlFragProfileBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
       // fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_profile, container) as RlFragProfileBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSetting" )
        rl_uiSetUp()
        return fragBinding.root
    }

    //All Design Setup Like Button Click And All
    private fun rl_uiSetUp() {
        rl_onBackPresAct(fragBinding.ivBack)
        //Firebase To Fetch UserData
        rl_firebaseToFetchUserData { userData ->
            if (userData != null) {
                userBasicDataCard=userData
                fragBinding.layFirstname.txtUsername.setText(userData.firstName)
                fragBinding.laySurname.txtUsername.setText(userData.lastName)
                fragBinding.layNickname.txtUsername.setText(userData.displayName)
                Glide.with(requireContext()).load(userData.displayImage)
                    .placeholder(R.drawable.sample_user).error(R.drawable.sample_user).into(fragBinding.layAvatar.imgUser)
                fragBinding.layEmail.txtUsername.setText(userData.emailId)
                fragBinding.layGender.txtUsername.setText(userData.gender)
                fragBinding.layDateofbirth.txtUsername.setText(userData.dob)

                fragBinding.layWeight.txtUsername.setText(convertToInt(userData.weightkg).toString())

                fragBinding.layHeight.txtUsername.setText(userData.height)
                fragBinding.layMaxheartrate.txtUsername.setText(userData.RFMHR.toString())//max hearrate
                fragBinding.layRestingheartrate.txtUsername.setText(userData.restingHr)//base heartrate


            } else {
                RLTools.rl_logEPrint(TAG, "Error fetching user data")
            }
        }

        fragBinding.layFirstname.txtusertitle.setText(R.string.firstname)
        fragBinding.layFirstname.imgEdit.setOnClickListener {
            fragBinding.layFirstname.txtUsername.visibility=View.GONE
            fragBinding.layFirstname.imgEdit.visibility=View.GONE
            fragBinding.layFirstname.imgDone.visibility=View.VISIBLE
            fragBinding.layFirstname.edtUsername.visibility=View.VISIBLE
            fragBinding.layFirstname.edtUsername.setText(fragBinding.layFirstname.txtUsername.text.toString())
        }
        fragBinding.layFirstname.imgDone.setOnClickListener {
            val firstName= fragBinding.layFirstname.edtUsername.text.toString()
            if (firstName.isNotBlank()){
                fragBinding.layFirstname.txtUsername.visibility=View.VISIBLE
                fragBinding.layFirstname.imgEdit.visibility=View.VISIBLE
                fragBinding.layFirstname.imgDone.visibility=View.GONE
                fragBinding.layFirstname.edtUsername.visibility=View.GONE
                fragBinding.layFirstname.txtUsername.setText(firstName)
                rl_basicDataUpdateToFirebase("firstName",firstName)
                rl_userForSearchUpdateToFirebase("firstName",firstName)
            }else{
                fragBinding.layFirstname.edtUsername.error = "First name cannot be empty."
            }

        }


        fragBinding.laySurname.txtusertitle.setText(R.string.surname)
        fragBinding.laySurname.imgEdit.setOnClickListener {
            fragBinding.laySurname.txtUsername.visibility=View.GONE
            fragBinding.laySurname.imgEdit.visibility=View.GONE
            fragBinding.laySurname.imgDone.visibility=View.VISIBLE
            fragBinding.laySurname.edtUsername.visibility=View.VISIBLE
            fragBinding.laySurname.edtUsername.setText(fragBinding.laySurname.txtUsername.text.toString())
        }
        fragBinding.laySurname.imgDone.setOnClickListener {
            val surName=fragBinding.laySurname.edtUsername.text.toString()
            if (surName.isNotBlank()){
                fragBinding.laySurname.txtUsername.visibility=View.VISIBLE
                fragBinding.laySurname.imgEdit.visibility=View.VISIBLE
                fragBinding.laySurname.imgDone.visibility=View.GONE
                fragBinding.laySurname.edtUsername.visibility=View.GONE
                fragBinding.laySurname.txtUsername.setText(surName)
                rl_basicDataUpdateToFirebase("lastName",surName)
                rl_userForSearchUpdateToFirebase("lastName",surName)
            }else{
                fragBinding.laySurname.edtUsername.error = "Surname name cannot be empty."
            }
        }

        fragBinding.layNickname.txtusertitle.setText(R.string.nickname)
        fragBinding.layNickname.imgEdit.setOnClickListener {
            fragBinding.layNickname.txtUsername.visibility=View.GONE
            fragBinding.layNickname.imgEdit.visibility=View.GONE
            fragBinding.layNickname.imgDone.visibility=View.VISIBLE
            fragBinding.layNickname.edtUsername.visibility=View.VISIBLE
            fragBinding.layNickname.edtUsername.setText(fragBinding.layNickname.txtUsername.text.toString())
        }
        fragBinding.layNickname.imgDone.setOnClickListener {
            val nickName=fragBinding.layNickname.edtUsername.text.toString()
            if (nickName.isNotBlank()){
                fragBinding.layNickname.txtUsername.visibility=View.VISIBLE
                fragBinding.layNickname.imgEdit.visibility=View.VISIBLE
                fragBinding.layNickname.imgDone.visibility=View.GONE
                fragBinding.layNickname.edtUsername.visibility=View.GONE
                fragBinding.layNickname.txtUsername.setText(nickName)
                rl_basicDataUpdateToFirebase("displayName",nickName)
                rl_userForSearchUpdateToFirebase("name",nickName)
            }else{
                fragBinding.layNickname.edtUsername.error = "Nickname name cannot be empty."
            }

        }

        fragBinding.layAvatar.txtusertitle.setText(R.string.avatar)
        fragBinding.layAvatar.txtUsername.visibility=View.GONE
        fragBinding.layAvatar.imgUser.visibility=View.VISIBLE
        fragBinding.layAvatar.imgEdit.setOnClickListener {
            rl_opencameragallerydialog()
        }

        fragBinding.layEmail.txtusertitle.setText(R.string.email)
        fragBinding.layEmail.imgEdit.visibility=View.GONE

        fragBinding.layGender.txtusertitle.setText(R.string.gender)
        fragBinding.layGender.imgEdit.setOnClickListener {
            rl_showGenderDialog()
        }

        fragBinding.layDateofbirth.txtusertitle.setText(R.string.dateofbirth)
        fragBinding.layDateofbirth.imgEdit.setOnClickListener {
            rl_dialogStartDatePicker(userBasicDataCard?.dob)
        }

        fragBinding.layWeight.txtusertitle.setText(R.string.weight)
        fragBinding.layWeight.imgEdit.setOnClickListener {
            rl_showWeightDialog()
        }

        fragBinding.layHeight.txtusertitle.setText(R.string.height)
        fragBinding.layHeight.imgEdit.setOnClickListener {
            rl_showHeightDialog()
        }

        fragBinding.layMaxheartrate.txtusertitle.setText(R.string.maxheartrateestimated)
        fragBinding.layMaxheartrate.imgEdit.setOnClickListener {
            rl_showRestingHrDialog(true)
        }

        fragBinding.layRestingheartrate.txtusertitle.setText(R.string.restingheartrate)
        fragBinding.layRestingheartrate.imgEdit.setOnClickListener {
            rl_showRestingHrDialog(false)
        }

        fragBinding.layChangeYourPassword.txtusertitle.setText(R.string.changeyourpassword)
        fragBinding.layChangeYourPassword.txtUsername.visibility=View.GONE
        fragBinding.layChangeYourPassword.imgEdit.setImageResource(R.drawable.ic_chevron_right)
        fragBinding.layChangeYourPassword.relayUser.setOnClickListener {
            (context as RLMainActivityRL).rl_loadFrag(RLFragChangePassword(), TAG, true, null, false)
        }

        fragBinding.layRequestToDeleteYourData.txtusertitle.setText(R.string.requesttodeleteyourdata)
        fragBinding.layRequestToDeleteYourData.txtUsername.visibility=View.GONE
        fragBinding.layRequestToDeleteYourData.imgEdit.setImageResource(R.drawable.ic_chevron_right)
        fragBinding.layRequestToDeleteYourData.relayUser.setOnClickListener {
            rl_showDialog(RLConstants.EXIT,getString(R.string.areyousurewanttodeletedata))
        }
    }

    //Firebase One By One BasicData Update
    private fun rl_basicDataUpdateToFirebase(endPoint:String, data:Any,) {
        val firebasePath = RevoolaFirebasePath.basicDataPathWrite(endPoint)
        // Firebase to Update BasicData
        RLDatabaseManagerWrite().rl_write_Basic_Data_Update(firebasePath,data) { isSuccessful, error ->
            if (isSuccessful){
                RLTools.rl_logDPrint(TAG,"BasicData Update Successfully")
            }else{
                RLTools.rl_logEPrint(TAG, "Error Update BasicData: $error")
            }
        }
    }
    private fun rl_userForSearchUpdateToFirebase(endPoint:String, data:Any,) {
        val firebasePath = RevoolaFirebasePath.userForSearchPathWrite(endPoint)
        // Firebase to Update BasicData
        RLDatabaseManagerWrite().rl_write_Basic_Data_Update(firebasePath,data) { isSuccessful, error ->
            if (isSuccessful){
                RLTools.rl_logDPrint(TAG,"BasicData Update Successfully")
            }else{
                RLTools.rl_logEPrint(TAG, "Error Update BasicData: $error")
            }
        }
    }
    //Calender View For BirthDate Dialog Open
    private fun rl_dialogStartDatePicker(dob: String?) {
        val calendar: Calendar = Calendar.getInstance()

        if (!dob.isNullOrEmpty()) {
            try {
                val parts = dob.split("/")
                if (parts.size == 3) {
                    val dayOfMonth = parts[0].toInt()
                    val month = parts[1].toInt() - 1 // Month is 0-based
                    val yearValue = parts[2].toInt()

                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.YEAR, yearValue)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // fallback to current date if parsing fails
            }
        }

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val datePickerDialog = DatePickerDialog(requireContext(), this, year, month, day)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }
    //Calender View For BirthDate Dialog Open
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        myYear = year
        myMonth = month + 1
        RLTools.rl_logDPrint("myMonth", "" + myMonth)
        myDay = day
        RLTools.rl_logDPrint("myDay", "" + myDay)

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

        DateTime = "" + Day + "/" + Month + "/" + myYear
        rl_basicDataUpdateToFirebase("dob",DateTime)
        fragBinding.layDateofbirth.txtUsername.setText(DateTime).toString()
    }
    //Height Change Dialog
    private fun rl_showHeightDialog() {
        var displayheight:String="5 Feet 4 inches"
        var feet:String="5 Feet"
        var inches:String="4 inches"
        var heightType:String="FeetInch"
        val sucDialog: Dialog = Dialog(requireActivity())
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
        val valuesfeet = arrayOf("1 Feet", "2 Feet", "3 Feet", "4 Feet", "5 Feet",
            "6 Feet", "7 Feet", "8 Feet", "9 Feet")
        val valuesinches = arrayOf("0 inches", "1 inches", "2 inches", "3 inches", "4 inches",
            "5 inches", "6 inches", "7 inches", "8 inches", "9 inches","10 inches", "11 inches")
        val valuesmatric = arrayOf(
            "151 cm", "152 cm", "153 cm", "154 cm", "155 cm","156 cm", "157 cm", "158 cm", "159 cm", "160 cm",
            "161 cm", "162 cm", "163 cm", "164 cm", "165 cm","166 cm", "167 cm", "168 cm", "169 cm", "170 cm",
            "171 cm", "172 cm", "173 cm", "174 cm", "175 cm","176 cm", "177 cm", "178 cm", "179 cm", "180 cm",
            "181 cm", "182 cm", "183 cm", "184 cm", "185 cm","186 cm", "187 cm", "188 cm", "189 cm", "190 cm",
            "191 cm", "192 cm", "193 cm", "194 cm", "195 cm","196 cm", "197 cm", "198 cm", "199 cm", "200 cm",
            "201 cm", "202 cm", "203 cm", "204 cm", "205 cm","206 cm", "207 cm", "208 cm", "209 cm", "210 cm",
            "211 cm", "212 cm", "213 cm", "214 cm", "215 cm","216 cm", "217 cm", "218 cm", "219 cm", "220 cm",
            "221 cm", "222 cm","223 cm", "224 cm", "225 cm", "226 cm", "227 cm","228 cm","229 cm","230 cm",
            "231 cm", "232 cm", "233 cm", "234 cm", "235 cm","236 cm", "237 cm", "238 cm", "239 cm", "240 cm",
            "241 cm", "242 cm", "243 cm", "244 cm", "245 cm","246 cm", "247 cm", "248 cm", "249 cm", "250 cm",
            "251 cm", "252 cm", "253 cm", "254 cm", "255 cm","256 cm", "257 cm", "258 cm", "259 cm", "260 cm",
            "261 cm", "262 cm", "263 cm", "264 cm", "265 cm","266 cm", "267 cm", "268 cm", "269 cm", "270 cm",
            "271 cm", "272 cm", "273 cm", "274 cm", "275 cm","276 cm", "277 cm", "278 cm", "279 cm", "280 cm")

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

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId.equals(R.id.radioButtonfeetandinches)){
                displayheight="5 Feet 4 inches"
                heightType = "FeetInch"
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
                displayheight="155 cm"
                heightType="Metric"
                rv_poundmatric.visibility=View.VISIBLE
                rv_ukstones.visibility=View.GONE
                numberPicker.minValue = 0
                numberPicker.maxValue = valuesmatric.size - 1
                numberPicker.displayedValues = valuesmatric
                numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                    displayheight=valuesmatric[newVal]
                }
                numberPicker.value = 4
            }
        }

        tvNo.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })

        tvYes.setOnClickListener(View.OnClickListener {
            if (heightType.equals("FeetInch")){
                val convertHeight = convertHeightCm(displayheight)
                rl_basicDataUpdateToFirebase("heightUnit","FeetInch")
                rl_basicDataUpdateToFirebase("height",convertHeight)
                fragBinding.layHeight.txtUsername.setText(convertHeight.toString())
            }else{
                val convertHeight = displayheight.replace("cm", "", ignoreCase = true).trim().toInt()
                rl_basicDataUpdateToFirebase("heightUnit","Metric")
                rl_basicDataUpdateToFirebase("height",convertHeight)
                fragBinding.layHeight.txtUsername.setText(convertHeight.toString())
            }

            sucDialog.dismiss()
        })

        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
    }
    //Weight Change Dialog
    private fun rl_showWeightDialog() {
        var displayweight:String="44 lbs"
        var st:String="5 st"
        var lb:String="1 lb"
        var weightType:String="USPound"

        val sucDialog: Dialog = Dialog(requireActivity())
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
        val valuesuspounds = arrayOf(
            "40 lbs",
            "41 lbs", "42 lbs", "43 lbs", "44 lbs", "45 lbs","46 lbs", "47 lbs", "48 lbs", "49 lbs", "50 lbs",
            "51 lbs", "52 lbs", "53 lbs", "54 lbs", "55 lbs","56 lbs", "57 lbs", "58 lbs", "59 lbs", "60 lbs",
            "61 lbs", "62 lbs", "63 lbs", "64 lbs", "65 lbs","66 lbs", "67 lbs", "68 lbs", "69 lbs", "70 lbs",
            "71 lbs", "72 lbs", "73 lbs", "74 lbs", "75 lbs","76 lbs", "77 lbs", "78 lbs", "79 lbs", "80 lbs",
            "81 lbs", "82 lbs", "83 lbs", "84 lbs", "85 lbs","86 lbs", "87 lbs", "88 lbs", "89 lbs", "90 lbs",
            "91 lbs", "92 lbs", "93 lbs", "94 lbs", "95 lbs","96 lbs", "97 lbs", "98 lbs", "99 lbs", "100 lbs",
            "101 lbs", "102 lbs", "103 lbs", "104 lbs", "105 lbs","106 lbs", "107 lbs", "108 lbs", "109 lbs", "110 lbs",
            "111 lbs", "112 lbs", "113 lbs", "114 lbs", "115 lbs","116 lbs", "117 lbs", "118 lbs", "119 lbs", "120 lbs",
            "121 lbs", "122 lbs","123 lbs", "124 lbs", "125 lbs", "126 lbs", "127 lbs","128 lbs","129 lbs","130 lbs",
            "131 lbs", "132 lbs", "133 lbs", "134 lbs", "135 lbs","136 lbs", "137 lbs", "138 lbs", "139 lbs", "140 lbs",
            "141 lbs", "142 lbs", "143 lbs", "144 lbs", "145 lbs","146 lbs", "147 lbs", "148 lbs", "149 lbs", "150 lbs",
            "151 lbs", "152 lbs", "153 lbs", "154 lbs", "155 lbs","156 lbs", "157 lbs", "158 lbs", "159 lbs", "160 lbs",
            "161 lbs", "162 lbs", "163 lbs", "164 lbs", "165 lbs","166 lbs", "167 lbs", "168 lbs", "169 lbs", "170 lbs",
            "171 lbs", "172 lbs", "173 lbs", "174 lbs", "175 lbs","176 lbs", "177 lbs", "178 lbs", "179 lbs", "180 lbs",
            "181 lbs", "182 lbs", "183 lbs", "184 lbs", "185 lbs","186 lbs", "187 lbs", "188 lbs", "189 lbs", "190 lbs",
            "191 lbs", "192 lbs", "193 lbs", "194 lbs", "195 lbs","196 lbs", "197 lbs", "198 lbs", "199 lbs", "200 lbs",
            "201 lbs", "202 lbs", "203 lbs", "204 lbs", "205 lbs","206 lbs", "207 lbs", "208 lbs", "209 lbs", "210 lbs",
            "211 lbs", "212 lbs", "213 lbs", "214 lbs", "215 lbs","216 lbs", "217 lbs", "218 lbs", "219 lbs", "220 lbs",
            "221 lbs", "222 lbs","223 lbs", "224 lbs", "225 lbs", "226 lbs", "227 lbs","228 lbs","229 lbs","230 lbs",
            "231 lbs", "232 lbs", "233 lbs", "234 lbs", "235 lbs","236 lbs", "237 lbs", "238 lbs", "239 lbs", "240 lbs",
            "241 lbs", "242 lbs", "243 lbs", "244 lbs", "245 lbs","246 lbs", "247 lbs", "248 lbs", "249 lbs", "250 lbs",
            "251 lbs", "252 lbs", "253 lbs", "254 lbs", "255 lbs","256 lbs", "257 lbs", "258 lbs", "259 lbs", "260 lbs",
            "261 lbs", "262 lbs", "263 lbs", "264 lbs", "265 lbs","266 lbs", "267 lbs", "268 lbs", "269 lbs", "270 lbs",
            "271 lbs", "272 lbs", "273 lbs", "274 lbs", "275 lbs","276 lbs", "277 lbs", "278 lbs", "279 lbs", "280 lbs",
            "281 lbs", "282 lbs", "283 lbs", "284 lbs", "285 lbs","286 lbs", "287 lbs", "288 lbs", "289 lbs", "290 lbs",
            "291 lbs", "292 lbs", "293 lbs", "294 lbs", "295 lbs","296 lbs", "297 lbs", "298 lbs", "299 lbs", "300 lbs",
            "301 lbs", "302 lbs", "303 lbs", "304 lbs", "305 lbs","306 lbs", "307 lbs", "308 lbs", "309 lbs", "310 lbs",
            "311 lbs", "312 lbs", "313 lbs", "314 lbs", "315 lbs","316 lbs", "317 lbs", "318 lbs", "319 lbs", "320 lbs",
            "321 lbs", "322 lbs","323 lbs", "324 lbs", "325 lbs", "326 lbs", "327 lbs","328 lbs","329 lbs","330 lbs",
            "331 lbs",
        )

        val valuesmatric = arrayOf(
            "18 kg", "19 kg", "20 kg", "21 kg", "22 kg","23 kg", "24 kg", "25 kg", "26 kg", "27 kg","28 kg","29 kg","30 kg",
            "31 kg", "32 kg", "33 kg", "34 kg", "35 kg","36 kg", "37 kg", "38 kg", "39 kg", "40 kg",
            "41 kg", "42 kg", "43 kg", "44 kg", "45 kg","46 kg", "47 kg", "48 kg", "49 kg", "50 kg",
            "51 kg", "52 kg", "53 kg", "54 kg", "55 kg","56 kg", "57 kg", "58 kg", "59 kg", "60 kg",
            "61 kg", "62 kg", "63 kg", "64 kg", "65 kg","66 kg", "67 kg", "68 kg", "69 kg", "70 kg",
            "71 kg", "72 kg", "73 kg", "74 kg", "75 kg","76 kg", "77 kg", "78 kg", "79 kg", "80 kg",
            "81 kg", "82 kg", "83 kg", "84 kg", "85 kg","86 kg", "87 kg", "88 kg", "89 kg", "90 kg",
            "91 kg", "92 kg", "93 kg", "94 kg", "95 kg","96 kg", "97 kg", "98 kg", "99 kg", "100 kg",
            "101 kg", "102 kg", "103 kg", "104 kg", "105 kg","106 kg", "107 kg", "108 kg", "109 kg", "110 kg",
            "111 kg", "112 kg", "113 kg", "114 kg", "115 kg","116 kg", "117 kg", "118 kg", "119 kg", "120 kg",
            "121 kg", "122 kg","123 kg", "124 kg", "125 kg", "126 kg", "127 kg","128 kg","129 kg","130 kg",
            "131 kg", "132 kg", "133 kg", "134 kg", "135 kg","136 kg", "137 kg", "138 kg", "139 kg", "140 kg",
            "141 kg", "142 kg", "143 kg", "144 kg", "145 kg","146 kg", "147 kg", "148 kg", "149 kg", "150 kg",
            "151 kg", "152 kg", "153 kg", "154 kg", "155 kg","156 kg", "157 kg", "158 kg", "159 kg", "160 kg",
            "161 kg", "162 kg", "163 kg", "164 kg", "165 kg","166 kg", "167 kg", "168 kg", "169 kg", "170 kg",
            "171 kg", "172 kg", "173 kg", "174 kg", "175 kg","176 kg", "177 kg", "178 kg", "179 kg", "180 kg",
            "181 kg", "182 kg", "183 kg", "184 kg", "185 kg","186 kg", "187 kg", "188 kg", "189 kg", "190 kg",
            "191 kg", "192 kg", "193 kg", "194 kg", "195 kg","196 kg", "197 kg", "198 kg", "199 kg", "200 kg",
        )

        val valuesukstonesst = arrayOf(
            "1 st", "2 st", "3 st", "4 st", "5 st","6 st", "7 st", "8 st", "9 st", "10 st",
            "11 st", "12 st", "13 st", "14 st", "15 st","16 st", "17 st", "18 st", "19 st", "20 st",
            "21 st", "22 st","23 st", "24 st", "25 st", "26 st", "27 st","28 st","29 st","30 st",
            "31 st", "32 st", "33 st", "34 st", "35 st","36 st", "37 st", "38 st", "39 st", "40 st",
        )
        val valuesukstoneslb = arrayOf(
            "1 lb", "2 lb", "3 lb", "4 lb", "5 lb","6 lb", "7 lb", "8 lb", "9 lb", "10 lb",
            "11 lb", "12 lb", "13 lb",
        )

        rv_poundmatric.visibility=View.VISIBLE
        rv_ukstones.visibility=View.GONE
        numberPicker.minValue = 0
        numberPicker.maxValue = valuesuspounds.size - 1
        numberPicker.displayedValues = valuesuspounds
        numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            //  val  selectedValueTextView = "Selected Value: ${values[newVal]}"
            displayweight=valuesuspounds[newVal]
            fragBinding.layWeight.txtUsername.setText(valuesuspounds[newVal])
        }
        numberPicker.value = 4

        // Set a listener to handle RadioGroup selection changes
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId.equals(R.id.radioButtonPounds)){
                displayweight="44 lbs"
                weightType = "USPound"
                rv_poundmatric.visibility=View.VISIBLE
                rv_ukstones.visibility=View.GONE
                numberPicker.minValue = 0
                numberPicker.maxValue = valuesuspounds.size - 1
                numberPicker.displayedValues = valuesuspounds
                numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                    displayweight=valuesuspounds[newVal]
                }
                numberPicker.value = 4
            }else if (checkedId.equals(R.id.radioButtonStones)){
                displayweight="5 st 1 lb"
                weightType = "UKStone"
                rv_poundmatric.visibility=View.GONE
                rv_ukstones.visibility=View.VISIBLE
                numberPickerst.minValue = 0
                numberPickerst.maxValue = valuesukstonesst.size - 1
                numberPickerst.displayedValues = valuesukstonesst
                numberPickerst.setOnValueChangedListener { picker, oldVal, newVal ->
                    st=valuesukstonesst[newVal]
                    displayweight=st+" "+lb
                }
                numberPickerst.value = 4

                numberPickerlb.minValue = 0
                numberPickerlb.maxValue = valuesukstoneslb.size - 1
                numberPickerlb.displayedValues = valuesukstoneslb
                numberPickerlb.setOnValueChangedListener { picker, oldVal, newVal ->
                    lb=valuesukstoneslb[newVal]
                    displayweight=st+" "+lb
                }
                numberPicker.value = 4
            }else if (checkedId.equals(R.id.radioButtonMetric)){
                displayweight="22 kg"
                weightType = "Metric"
                rv_poundmatric.visibility=View.VISIBLE
                rv_ukstones.visibility=View.GONE
                numberPicker.minValue = 0
                numberPicker.maxValue = valuesmatric.size - 1
                numberPicker.displayedValues = valuesmatric
                numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                    displayweight=valuesmatric[newVal]
                }
                numberPicker.value = 4
            }
        }

        tvNo.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })


        tvYes.setOnClickListener(View.OnClickListener {
            if (weightType.equals("USPound")){
                val convertedWeight=parseWeightToKg(displayweight)
                rl_basicDataUpdateToFirebase("weightUnit","USPound")
                rl_basicDataUpdateToFirebase("weightkg",convertedWeight)
            }else if (weightType.equals("UKStone")){
                val convertedWeight=parseWeightToKg(displayweight)
                rl_basicDataUpdateToFirebase("weightUnit","UKStone")
                rl_basicDataUpdateToFirebase("weightkg",convertedWeight)
            }else{
                val convertedWeight=parseWeightToKg(displayweight)
                rl_basicDataUpdateToFirebase("weightUnit","Metric")
                rl_basicDataUpdateToFirebase("weightkg",convertedWeight)
            }
            fragBinding.layWeight.txtUsername.setText(displayweight)
            sucDialog.dismiss()
        })

        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
    }
    //Gender Change Dialog
    private fun rl_showGenderDialog() {
        val sucDialog: Dialog = Dialog(requireActivity())
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog.setContentView(R.layout.rl_dialog_gender_selection)
        sucDialog.setCancelable(true)

        val tvNo: TextView = sucDialog.findViewById(R.id.tvNo)
        val tvYes: TextView = sucDialog.findViewById(R.id.tvYes)
        val radioGroup = sucDialog.findViewById<RadioGroup>(R.id.radioGroup)
        val gender = userBasicDataCard?.gender?:"".toLowerCase()
        when(gender){
            "male"->{
                radioGroup.check(R.id.radioButtonMale)
            }
            "female"->{
                radioGroup.check(R.id.radioButtonFemale)
            }
            "other"->{
                radioGroup.check(R.id.radioButtonOther)
            }
            else-> {
                radioGroup.check(R.id.radioButtonMale)
            }
        }

        tvNo.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })

        tvYes.setOnClickListener(View.OnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            val radioButton = sucDialog.findViewById<RadioButton>(selectedId)
            val selectedGender = radioButton.text.toString()
            when(selectedGender){
                "MALE"->{
                    rl_basicDataUpdateToFirebase("gender","Male")
                    fragBinding.layGender.txtUsername.setText("Male")
                }
                "FEMALE"->{
                    rl_basicDataUpdateToFirebase("gender","Female")
                    fragBinding.layGender.txtUsername.setText("Female")
                }
                "PREFER NOT TO SAY"->{
                    rl_basicDataUpdateToFirebase("gender","Other")
                    fragBinding.layGender.txtUsername.setText("Other")
                }
            }
            sucDialog.dismiss()
        })

        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
    }
    // Max HeartRate And Base HeartRate Dialog
    private fun rl_showRestingHrDialog(isMaxHeartrate:Boolean) {
        val sucDialog: Dialog = Dialog(requireActivity())
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog.setContentView(R.layout.rl_dialog_resting_hr)
        sucDialog.setCancelable(true)

        val tvNo: TextView = sucDialog.findViewById(R.id.tvNo)
        val tvYes: TextView = sucDialog.findViewById(R.id.tvYes)
        val ivHeartRateEdit: EditText = sucDialog.findViewById(R.id.edt_restinghr)

        if (isMaxHeartrate){
            ivHeartRateEdit.setHint("Range(120-250)")
        }else{
            ivHeartRateEdit.setHint("Range(35-80)")
        }

        tvNo.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })

        tvYes.setOnClickListener(View.OnClickListener {
            val restingHr=convertToInt(ivHeartRateEdit.text.toString())
            val rangeBase = 35..80
            val rangeMax = 120..250
            if (isMaxHeartrate){
                if (restingHr in rangeMax){
                    rl_basicDataUpdateToFirebase("RFMHR",restingHr)
                    fragBinding.layMaxheartrate.txtUsername.setText(restingHr.toString())
                }else{
                    rl_alert("Heart Rate must be in range of 120-250",requireContext())
                }

            }else{
                if (restingHr in rangeBase){
                    rl_basicDataUpdateToFirebase("restingHr",restingHr)
                    fragBinding.layRestingheartrate.txtUsername.setText(restingHr.toString())
                }else{
                    rl_alert("Heart Rate must be in range of 35-80",requireContext())
                }
            }
            sucDialog.dismiss()
        })

        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
    }

    fun  rl_alert(message: String, contextt: Context){
        AlertDialog.Builder(contextt)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
    //User Image PickUp All Dialog
    private fun rl_opencameragallerydialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        AlertDialog.Builder(activity)
            .setTitle("Choose Image")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        //Camera Image Click
                        if (!rl_isStoragePermissionGranted()) {
                            // Request the permission
                            rl_requestStoragePermission()
                        } else {
                            // Permission is already granted, you can proceed with your code
                            val pickImg = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            rl_cameraImage.launch(pickImg)
                        }
                    }
                    1 -> {
                        //Gallery Pick Up Image
                        val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                        rl_galleryImage.launch(pickImg)
                    }
                }
                dialog.dismiss()
            }
            .show()
    }
    //Gallery to Pickup User Image
    private val rl_galleryImage =registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            val imgUri= data?.data
            chooseimagefile=rl_uriToFile(requireActivity(),imgUri)
            fragBinding.layAvatar.imgUser.setImageURI(imgUri)
            rl_uploadUserImage(imgUri)
        }
    }
    //Camera to Click User Image
    private val rl_cameraImage =registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            val imageBitmap = data?.extras?.get("data") as Bitmap
            chooseimagefile=rl_saveBitmapToFile(imageBitmap)
            fragBinding.layAvatar.imgUser.setImageBitmap(imageBitmap)
            val tempUri = rl_getImageUri(requireActivity(), imageBitmap)
            rl_uploadUserImage(tempUri)
        }
    }
    //UriImage  to File converter
    private fun rl_uriToFile(context: FragmentActivity?, uri: Uri?): File? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context?.contentResolver?.query(uri!!, projection, null, null, null) ?: return null
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val filePath = cursor.getString(columnIndex)
        cursor.close()
        return File(filePath)
    }
    //BitmapImage to File converter
    private fun rl_saveBitmapToFile(bitmap: Bitmap): File? {
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
            RLTools.rl_logEPrint("CAMERAIMAGHE","ERROR=="+e.localizedMessage)
            return null
        }
    }
    //Storage Permission Check
    private fun rl_isStoragePermissionGranted(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
        val storagePermission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return cameraPermission == PackageManager.PERMISSION_GRANTED && storagePermission == PackageManager.PERMISSION_GRANTED
    }
    //Storage Permission Request
    private fun rl_requestStoragePermission() {
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),STORAGE_PERMISSION_REQUEST_CODE)
    }
    // Handle permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, you can proceed with your code
                val pickImg = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                rl_cameraImage.launch(pickImg)
            } else {
                // Permission is denied
                // You may want to show a message or handle the case where the RLuser denies the permission
                rl_opentoast("Permission is denied")
            }
        }
    }
    //Common Toast
    private fun rl_opentoast(messageprint: String) {
        Toast.makeText(requireContext(),messageprint, Toast.LENGTH_SHORT).show()
    }
    //String to convert Int
    private fun convertToInt(value: Any): Int {
        return when (value) {
            is Double -> value.roundToInt()
            is Float -> value.roundToInt()
            is Int -> value
            is String -> value.toDoubleOrNull()?.roundToInt() ?: 0
            else -> 0 // Default fallback for unsupported types
        }
    }
    //Height Convert Feet inch to cm
    private fun convertHeightCm(height: String): Int {
        val regex = Regex("(\\d+)\\s*Feet\\s*(\\d+)\\s*inches", RegexOption.IGNORE_CASE)
        val match = regex.find(height)
        if (match != null) {
            val (feetStr, inchesStr) = match.destructured
            val feet = feetStr.toInt()
            val inches = inchesStr.toInt()

            val heightInCm = (feet * 30.48) + (inches * 2.54)
            return heightInCm.roundToInt()
        } else {
            return 0
        }
    }
    //weight Convert to KG
    private fun parseWeightToKg(weight: String): Double {
        val lbsRegex = Regex("(\\d+)\\s*lbs?", RegexOption.IGNORE_CASE)
        val stLbRegex = Regex("(\\d+)\\s*st\\s*(\\d+)\\s*lb", RegexOption.IGNORE_CASE)
        val kgRegex = Regex("(\\d+)\\s*kg", RegexOption.IGNORE_CASE)

        return when {
            stLbRegex.matches(weight) -> {
                val (stStr, lbStr) = stLbRegex.find(weight)!!.destructured
                val stones = stStr.toInt()
                val pounds = lbStr.toInt()
                (stones * 6.35029) + (pounds * 0.453592)
            }
            lbsRegex.matches(weight) -> {
                val pounds = lbsRegex.find(weight)!!.groupValues[1].toInt()
                pounds * 0.453592
            }
            kgRegex.matches(weight) -> {
                kgRegex.find(weight)!!.groupValues[1].toDouble()
            }
            else -> 0.0 // Unknown format
        }
    }
    //BitMap Image To Uri Image
    private fun rl_getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }
    //user Image upload to Firebase
    private fun rl_uploadUserImage(filePath:Uri?) {
        if (filePath != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val userId=RLAuthManager().rl_getCurrentUser()?.uid?:""
                    val leaderboardPath = userId+"leaderboard.png"
                    val mainPath = userId+"main.png"

                    val leaderboardref = FirebaseStorage.getInstance().reference.child(leaderboardPath)
                    val uploadleaderboard = leaderboardref.putFile(filePath)
                    uploadleaderboard.addOnSuccessListener {
                        leaderboardref.downloadUrl.addOnSuccessListener { uri ->
                            val imageFile = uri.toString()
                            //Basic Data
                            rl_basicDataUpdateToFirebase("leaderBoardImage",imageFile)
                            rl_basicDataUpdateToFirebase("displayImage",imageFile)
                            //Users For Search
                            rl_userForSearchUpdateToFirebase("leaderBoardImage",imageFile)
                            rl_userForSearchUpdateToFirebase("displayImage",imageFile)
                            RLTools.rl_logDPrint(TAG,"imageUrl leaderboardPath:- $imageFile")
                        }
                    }.addOnFailureListener {
                        rl_opentoast("Failed to upload image")
                    }

                    val mainPathref = FirebaseStorage.getInstance().reference.child(mainPath)
                    val uploadmainPath= mainPathref.putFile(filePath)
                    uploadmainPath.addOnSuccessListener {
                        mainPathref.downloadUrl.addOnSuccessListener { uri ->
                            val imageFile = uri.toString()
                            RLTools.rl_logDPrint(TAG,"imageUrl mainPath:- $imageFile")
                        }
                    }.addOnFailureListener {
                        rl_opentoast("Failed to upload image")
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        rl_opentoast("Failed to upload image: ${e.message}")
                    }
                }
            }

        }
    }

    private fun rl_showDialog(type: String, message: String) {
        val sucDialog: Dialog = Dialog(requireContext())
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog.setContentView(R.layout.rl_layout_dailog)
        sucDialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(sucDialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val tvNo: TextView = sucDialog.findViewById(R.id.tvNo)
        val tvYes: TextView = sucDialog.findViewById(R.id.tvYes)
        val tvTitle: TextView = sucDialog.findViewById(R.id.tvTitle)
        val tvSubTitle: TextView = sucDialog.findViewById(R.id.tvSubTitle)
        tvSubTitle.setText(message)

        tvNo.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })

        tvYes.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
            if (type.equals(RLConstants.LOGOUT_D)){
                rl_signOut()
                /* Firebase.auth.signOut()
                  MoECoreHelper.logoutUser(requireContext())
               // RLPrefManager.RLsetSomeStringValue(requireContext(), RLPrefManager.current_user,"")
                RLPrefManager.RLClear_all(requireContext())
                 val intent = Intent(requireContext(), RLSplashActivityRL::class.java)
                 startActivity(intent)
                 activity?.finish()*/
            }
        })
        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }

}