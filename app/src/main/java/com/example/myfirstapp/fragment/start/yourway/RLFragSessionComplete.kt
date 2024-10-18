package com.example.myfirstapp.fragment.start.yourway

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databasefirebase.RLAuthManager
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerRead
import com.example.myfirstapp.databinding.RlFragSessionCompleteBinding
import com.example.myfirstapp.fragment.overview.RLFragOverviewSession
import com.example.myfirstapp.fragment.start.adapter.RLSelectedImagesAdapter
import com.example.myfirstapp.model.RLHeartRateSensorWorkoutSessionDetailsModel
import com.example.myfirstapp.model.RLWorkoutSessionDetailsModel
import com.example.myfirstapp.model.RLNoSensorWorkoutSessionDetailsModel
import com.example.myfirstapp.model.RLRevoolaUsersSettingsModel
import com.example.myfirstapp.model.RLSpeedSensorWorkoutSessionDetailsModel
import com.example.myfirstapp.model.RLWorkoutSessionSummaryModel
import com.example.myfirstapp.utils.RLConstants

import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTools
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import gun0912.tedimagepicker.builder.TedImagePicker
import java.util.UUID
import kotlin.math.roundToInt

class RLFragSessionComplete : RLBaseFragment(){
    val TAG: String = RLFragSessionComplete::class.java.simpleName
    lateinit var fragBinding: RlFragSessionCompleteBinding
    var imgUriList = mutableListOf<Uri>()
    var currentUser =""
    private var wsWeight="60"
    private var wsHeight="167"
    private var wsAge=25
    private var gender="Male"
    private var RFMHR=191
    private var RestingHR="50"

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
        currentUser=  RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.current_user, "")
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        //"Pilates","Ride","Run","Walk","Workout","Yoga"
        RLFirebaseToFatchUserData()

        val yourWayType = requireArguments().getString("YourWayType").toString().trim()
        val totalTime = requireArguments().getString("totalTime").toString().trim()
        val heartRateList: ArrayList<Int>? = requireArguments().getIntegerArrayList("heartRateList")
        val stepsList: ArrayList<Int>? = requireArguments().getIntegerArrayList("stepsList")
        val paceList: ArrayList<Int>? =requireArguments().getIntegerArrayList("paceList")
        val climbedList: ArrayList<Int>? = requireArguments().getIntegerArrayList("climbedList")
        val avgSpaceList: ArrayList<Int>? = requireArguments().getIntegerArrayList("avgSpaceList")
        val maxPaceList: ArrayList<Int>? = requireArguments().getIntegerArrayList("maxxPaceList")
        val SensorType: String = requireArguments().getString("SENSOR").toString()

        val speedArray = arguments?.getDoubleArray("speedList")
        val avgSpeedArray = arguments?.getDoubleArray("avgSpeedList")
        val maxSpeedArray = arguments?.getDoubleArray("maxsSpeedList")
        val distanceArray = arguments?.getDoubleArray("distanceList")
        val activeCaloriesArray = arguments?.getDoubleArray("activeCaloriesList")

        val speedList: MutableList<Double> = speedArray?.toMutableList() ?: mutableListOf()
        val avgSpeedList: MutableList<Double> = avgSpeedArray?.toMutableList() ?: mutableListOf()
        val maxSpeedList: MutableList<Double> = maxSpeedArray?.toMutableList() ?: mutableListOf()
        val distanceList: MutableList<Double> = distanceArray?.toMutableList() ?: mutableListOf()
        val activeCaloriesList: MutableList<Double> = activeCaloriesArray?.toMutableList() ?: mutableListOf()

        /*
        val sizeallarray="heartRateList:- ${heartRateList!!.size}  " +
                "stepsList:- ${stepsList!!.size}  "+
                "distanceList:- ${distanceList!!.size}  "+
                "paceList:- ${paceList!!.size}  "+
                "speedList:- ${speedList!!.size}  "+
                "activeCaloriesList:- ${activeCaloriesList!!.size}  "+
                "climbedList:- ${climbedList!!.size}  "+
                "avgSpeedList:- ${avgSpeedList!!.size}  "+
                "maxSpeedList:- ${maxSpeedList!!.size}  "+
                "avgSpaceList:- ${avgSpaceList!!.size}  "+
                "maxPaceList:- ${maxPaceList!!.size}"


        Log.e(TAG,"ALL ARRAY SIZE:- $sizeallarray")*/

        fragBinding.edtSessionName.setText("$yourWayType Session")
        fragBinding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
            // Handle checked change
        }
        fragBinding.layPrivacy.setOnClickListener {
            val titleTxt:String=fragBinding.tvShareTitle.text.toString().toUpperCase()
            when(titleTxt){
                "FRIENDS"->{
                    fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyPrivateBGColor))
                    fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyprivate)
                    fragBinding.tvShareTitle.setText(R.string.privatetx)
                    fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyPrivateColor))
                    RLShareMapHide(false)
                }
                "EVERYONE"->{
                    fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyFriendsBGColor))
                    fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyfriends)
                    fragBinding.tvShareTitle.setText(R.string.friendstx)
                    fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyFriendsColor))
                    RLShareMapHide(true)
                }
                "PRIVATE"->{
                    fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyEveryOneBGColor))
                    fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyeveryone)
                    fragBinding.tvShareTitle.setText(R.string.everyone)
                    fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyEveryOneColor))
                    RLShareMapHide(true)
                }
            }
        }
        fragBinding.txtAddPhoto.setOnClickListener {
            RLchooseFromGallery()
        }
        fragBinding.imgCancle.setOnClickListener {
            RLBottomHideShowSet(true)
            (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
            (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
        }
        fragBinding.inlayButton.commonButton.setText(R.string.save)
        fragBinding.inlayButton.commonButton.setOnClickListener {
           /* if (imgUriList.size>0){
                RLuploadImagesToFirebase(imgUriList)
            }*/
            when (SensorType){
                RLConstants.HEARTSENSOR->{
                    RlHeartRateDataEntryToFirebase(yourWayType,totalTime,heartRateList,stepsList,paceList,climbedList,avgSpaceList,maxPaceList,speedList,avgSpeedList,maxSpeedList,distanceList,activeCaloriesList)
                }
                RLConstants.SPEEDSENSOR->{
                    RlSpeedSensorDataEntryToFirebase(yourWayType,totalTime,heartRateList,stepsList,paceList,climbedList,avgSpaceList,maxPaceList,speedList,avgSpeedList,maxSpeedList,distanceList,activeCaloriesList)
                }
                RLConstants.NOSENSOR->{
                    RlNoSensorDataEntryToFirebase(yourWayType,totalTime,heartRateList,stepsList,paceList,climbedList,avgSpaceList,maxPaceList,speedList,avgSpeedList,maxSpeedList,distanceList,activeCaloriesList)

                }
            }

        }

    }

    private fun RlHeartRateDataEntryToFirebase(
        yourWayType: String,
        totalTime: String,
        heartRateList: ArrayList<Int>?,
        stepsList: ArrayList<Int>?,
        paceList: ArrayList<Int>?,
        climbedList: ArrayList<Int>?,
        avgSpaceList: ArrayList<Int>?,
        maxPaceList: ArrayList<Int>?,
        speedList: MutableList<Double>,
        avgSpeedList: MutableList<Double>,
        maxSpeedList: MutableList<Double>,
        distanceList: MutableList<Double>,
        activeCaloriesList: MutableList<Double>) {
        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()

        val entryWorkoutSessionDetails = RLHeartRateSensorWorkoutSessionDetailsModel()

        //Array Entry Value
        entryWorkoutSessionDetails.arrSpeed= speedList
        entryWorkoutSessionDetails.arrDistance= distanceList
        entryWorkoutSessionDetails.arrBurntCalories= activeCaloriesList
        entryWorkoutSessionDetails.arrHr= heartRateList!!
        entryWorkoutSessionDetails.arrCadence= climbedList!!
//        entryWorkoutSessionDetails.arrCadence=
//        entryWorkoutSessionDetails.arrCumElevation=
//        entryWorkoutSessionDetails.arrElevation=
//        entryWorkoutSessionDetails.arrPower=
//        entryWorkoutSessionDetails.arrPowerFromDevice=

        //Normal Entry Value
        entryWorkoutSessionDetails.totalTime= totalTime.toInt()?:0
        entryWorkoutSessionDetails.classType= yourWayType
        entryWorkoutSessionDetails.className= fragBinding.edtSessionName.text.toString()
        entryWorkoutSessionDetails.classNote= fragBinding.edtAddNotes.text.toString()
        entryWorkoutSessionDetails.remark="android"
        entryWorkoutSessionDetails.distance=distanceList.maxOrNull()!!.toDouble() ?: 0.0
        entryWorkoutSessionDetails.burntCalories=activeCaloriesList.maxOrNull()!!.toDouble() ?: 0.0
        entryWorkoutSessionDetails.totalSteps=stepsList!!.maxOrNull()!! ?: 0
        entryWorkoutSessionDetails.classDate=currentTimestamp
        entryWorkoutSessionDetails.isPowerDeviceConnected=false
        entryWorkoutSessionDetails.timestamp=currentTimestamp.toInt()
        entryWorkoutSessionDetails.totalPower=0
        entryWorkoutSessionDetails.maxRevPercentage=0
        entryWorkoutSessionDetails.isClass=false
        entryWorkoutSessionDetails.goal=""
        entryWorkoutSessionDetails.classImage=""

//        entryWorkoutSessionDetails.MaxHrUsedForCalculation=
//        entryWorkoutSessionDetails.MaxHrUsedForCalculation_Last=
//        entryWorkoutSessionDetails.RestingHrUsedForCalculation=
//        entryWorkoutSessionDetails.RestingHrUsedForCalculation_Last=
//

//        entryWorkoutSessionDetails.avgRevPercentage=
//        entryWorkoutSessionDetails.classDescription=
//        entryWorkoutSessionDetails.demsElevation=
//        entryWorkoutSessionDetails.mapGeneratedUrl=
//        entryWorkoutSessionDetails.minRevPercentage=
//        entryWorkoutSessionDetails.revPercentage=
//        entryWorkoutSessionDetails.totalElevation=
//        entryWorkoutSessionDetails.totalRev=
//        entryWorkoutSessionDetails.typeOfGoal=

        //Zone Entry Value
        entryWorkoutSessionDetails.zone1.remark= "android"
        entryWorkoutSessionDetails.zone1.distance= distanceList.maxOrNull()!!.toDouble() ?: 0.0
        entryWorkoutSessionDetails.zone1.burntCalories= activeCaloriesList.maxOrNull()!!.toDouble() ?: 0.0
//        entryWorkoutSessionDetails.zone1.seconds=
//        entryWorkoutSessionDetails.zone1.totalRev=

        RLHeartRateSensorUserSessionDetailData(entryWorkoutSessionDetails)
    }
    private fun RlNoSensorDataEntryToFirebase(
        yourWayType: String,
        totalTime: String,
        heartRateList: ArrayList<Int>?,
        stepsList: ArrayList<Int>?,
        paceList: ArrayList<Int>?,
        climbedList: ArrayList<Int>?,
        avgSpaceList: ArrayList<Int>?,
        maxPaceList: ArrayList<Int>?,
        speedList: MutableList<Double>,
        avgSpeedList: MutableList<Double>,
        maxSpeedList: MutableList<Double>,
        distanceList: MutableList<Double>,
        activeCaloriesList: MutableList<Double>) {
        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()

        val entryWorkoutSessionDetails = RLNoSensorWorkoutSessionDetailsModel()

        //Array Entry Value
        entryWorkoutSessionDetails.arrSpeed= speedList
        entryWorkoutSessionDetails.arrDistance= distanceList
        entryWorkoutSessionDetails.arrBurntCalories= activeCaloriesList
        entryWorkoutSessionDetails.arrCadence= climbedList!!
//        entryWorkoutSessionDetails.arrCadence=
//        entryWorkoutSessionDetails.arrCumElevation=
//        entryWorkoutSessionDetails.arrElevation=
//        entryWorkoutSessionDetails.arrPower=
//        entryWorkoutSessionDetails.arrPowerFromDevice=

        //Normal Entry Value
        entryWorkoutSessionDetails.MaxHrUsedForCalculation=RFMHR
        entryWorkoutSessionDetails.MaxHrUsedForCalculation_Last=RFMHR
        entryWorkoutSessionDetails.RestingHrUsedForCalculation=RestingHR.toInt()
        entryWorkoutSessionDetails.RestingHrUsedForCalculation_Last=RestingHR.toInt()

        //entryWorkoutSessionDetails.avgRevPercentage=//value count
        //entryWorkoutSessionDetails.burntCalories= // sum of every time calories plus
        entryWorkoutSessionDetails.classDate=currentTimestamp
        entryWorkoutSessionDetails.classDescription=""
        entryWorkoutSessionDetails.classImage=""
        entryWorkoutSessionDetails.className= fragBinding.edtSessionName.text.toString()
        entryWorkoutSessionDetails.classNote= fragBinding.edtAddNotes.text.toString()
        entryWorkoutSessionDetails.classType= yourWayType
        entryWorkoutSessionDetails.demsElevation=-1
        //entryWorkoutSessionDetails.distance = //// sum of every time distance plus
        entryWorkoutSessionDetails.goal=""
        entryWorkoutSessionDetails.isClass=false
        entryWorkoutSessionDetails.isPowerDeviceConnected=false
        entryWorkoutSessionDetails.mapGeneratedUrl=""
        //entryWorkoutSessionDetails.maxRevPercentage=//calculation of REV persentage
        //entryWorkoutSessionDetails.minRevPercentage=//calculation of REV persentage
        entryWorkoutSessionDetails.remark="android"
        //entryWorkoutSessionDetails.revPercentage=//last value of arrRevPercentage
        entryWorkoutSessionDetails.timestamp=currentTimestamp.toLong()
        //entryWorkoutSessionDetails.totalElevation=//every time elevation sum
        entryWorkoutSessionDetails.totalPower=0
        //entryWorkoutSessionDetails.totalRev= sum of revpersentage
        entryWorkoutSessionDetails.totalSteps=stepsList!!.maxOrNull()!! ?: 0
        entryWorkoutSessionDetails.totalTime= totalTime.toInt()?:0
        entryWorkoutSessionDetails.typeOfGoal=yourWayType


        //Zone Entry Value
        entryWorkoutSessionDetails.zone1.remark= "android"
        entryWorkoutSessionDetails.zone1.distance= distanceList.maxOrNull()!!.toDouble() ?: 0.0
        entryWorkoutSessionDetails.zone1.burntCalories= activeCaloriesList.maxOrNull()!!.toDouble() ?: 0.0
//        entryWorkoutSessionDetails.zone1.seconds=
//        entryWorkoutSessionDetails.zone1.totalRev=

        RLNoSensorUserSessionDetailData(entryWorkoutSessionDetails)

        //Multi Array value set entry to database
        /*
        //single value set entry to database
        val entryWorkoutSessionSummary = RLWorkoutSessionSummaryModel()
        entryWorkoutSessionSummary.totalTime=totalTime.toInt()
        entryWorkoutSessionSummary.classType= yourWayType
        entryWorkoutSessionSummary.className= fragBinding.edtSessionName.text.toString()
        entryWorkoutSessionSummary.classNote= fragBinding.edtAddNotes.text.toString()

        //entryWorkoutSessionSummary.zone1.avgHr= heartRateList.average().roundToInt()
        //entryWorkoutSessionSummary.zone1.avgSpeed= stepsList.map { it.toInt() }.toIntArray().average().toDouble()
        //entryWorkoutSessionSummary.zone1.distance= distanceList.maxOrNull()!!.toDouble()

       // RLRevoolaUserSessionSummaryData(entryWorkoutSessionSummary)*/
    }
    private fun RlSpeedSensorDataEntryToFirebase(
        yourWayType: String,
        totalTime: String,
        heartRateList: ArrayList<Int>?,
        stepsList: ArrayList<Int>?,
        paceList: ArrayList<Int>?,
        climbedList: ArrayList<Int>?,
        avgSpaceList: ArrayList<Int>?,
        maxPaceList: ArrayList<Int>?,
        speedList: MutableList<Double>,
        avgSpeedList: MutableList<Double>,
        maxSpeedList: MutableList<Double>,
        distanceList: MutableList<Double>,
        activeCaloriesList: MutableList<Double>) {
        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()

        val entryWorkoutSessionDetails = RLSpeedSensorWorkoutSessionDetailsModel()

        //Array Entry Value
        entryWorkoutSessionDetails.arrSpeed= speedList
        entryWorkoutSessionDetails.arrDistance= distanceList
        entryWorkoutSessionDetails.arrBurntCalories= activeCaloriesList
//        entryWorkoutSessionDetails.arrCadence=
//        entryWorkoutSessionDetails.arrCumElevation=
//        entryWorkoutSessionDetails.arrElevation=
//        entryWorkoutSessionDetails.arrPower=
//        entryWorkoutSessionDetails.arrPowerFromDevice=

        //Normal Entry Value
        entryWorkoutSessionDetails.totalTime= totalTime.toInt()?:0
        entryWorkoutSessionDetails.classType= yourWayType
        entryWorkoutSessionDetails.className= fragBinding.edtSessionName.text.toString()
        entryWorkoutSessionDetails.classNote= fragBinding.edtAddNotes.text.toString()
        entryWorkoutSessionDetails.remark="android"
        entryWorkoutSessionDetails.distance=distanceList.maxOrNull()!!.toDouble() ?: 0.0
        entryWorkoutSessionDetails.burntCalories=activeCaloriesList.maxOrNull()!!.toDouble() ?: 0.0
        entryWorkoutSessionDetails.totalSteps=stepsList!!.maxOrNull()!! ?: 0
        entryWorkoutSessionDetails.classDate=currentTimestamp
        entryWorkoutSessionDetails.isPowerDeviceConnected=false
        entryWorkoutSessionDetails.timestamp=currentTimestamp.toInt()
        entryWorkoutSessionDetails.totalPower=0
        entryWorkoutSessionDetails.maxRevPercentage=0
        entryWorkoutSessionDetails.isClass=false
        entryWorkoutSessionDetails.goal=""
        entryWorkoutSessionDetails.classImage=""

//        entryWorkoutSessionDetails.MaxHrUsedForCalculation=
//        entryWorkoutSessionDetails.MaxHrUsedForCalculation_Last=
//        entryWorkoutSessionDetails.RestingHrUsedForCalculation=
//        entryWorkoutSessionDetails.RestingHrUsedForCalculation_Last=
//

//        entryWorkoutSessionDetails.avgRevPercentage=
//        entryWorkoutSessionDetails.classDescription=
//        entryWorkoutSessionDetails.demsElevation=
//        entryWorkoutSessionDetails.mapGeneratedUrl=
//        entryWorkoutSessionDetails.minRevPercentage=
//        entryWorkoutSessionDetails.revPercentage=
//        entryWorkoutSessionDetails.totalElevation=
//        entryWorkoutSessionDetails.totalRev=
//        entryWorkoutSessionDetails.typeOfGoal=

        //Zone Entry Value
        entryWorkoutSessionDetails.zone1.remark= "android"
        entryWorkoutSessionDetails.zone1.distance= distanceList.maxOrNull()!!.toDouble() ?: 0.0
        entryWorkoutSessionDetails.zone1.burntCalories= activeCaloriesList.maxOrNull()!!.toDouble() ?: 0.0
//        entryWorkoutSessionDetails.zone1.seconds=
//        entryWorkoutSessionDetails.zone1.totalRev=

        RLSpeedSensorUserSessionDetailData(entryWorkoutSessionDetails)


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
                    RLimageListVisible(true)
                }else{
                    RLimageListVisible(false)
                }
                fragBinding.rvSelectedImages.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
               val selectedImagesAdapter = RLSelectedImagesAdapter(imgUriList) { uri ->
                   imgUriList.remove(uri)
                   if (imgUriList.size>0){
                       RLimageListVisible(true)
                   }else{
                       RLimageListVisible(false)
                   }
                }
                fragBinding.rvSelectedImages.adapter = selectedImagesAdapter
            }
    }
    private fun RLimageListVisible(isVisible: Boolean){
        if (isVisible){
            fragBinding.rvSelectedImages.visibility=View.VISIBLE
            fragBinding.txtAddPhoto.visibility=View.GONE
        }else{
            fragBinding.rvSelectedImages.visibility=View.GONE
            fragBinding.txtAddPhoto.visibility=View.VISIBLE
        }
    }

    //SELECTED IMAGE SENT TO SERVER
    private fun RLuploadImagesToFirebase(imageUris: List<Uri>) {
        val storageReference = FirebaseStorage.getInstance().reference
        val databaseReference = FirebaseDatabase.getInstance().reference.child("live")

        for (uri in imageUris) {
            val fileName = System.currentTimeMillis().toString() + ".jpg"
            val fileReference = storageReference.child("uploads/$fileName")

            fileReference.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    fileReference.downloadUrl.addOnSuccessListener { downloadUri ->
                        RLsaveImageUrlToDatabase(downloadUri.toString(), databaseReference)
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                    RLcommonToast( "Upload failed: ${exception.message}")
                }
        }
    }
    private fun RLsaveImageUrlToDatabase(downloadUrl: String, databaseReference: DatabaseReference) {
        val imageId = databaseReference.push().key // Generate a unique ID for each image
        imageId?.let {
            databaseReference.child(it).setValue(downloadUrl)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLBottomHideShowSet(true)
                        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                        (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
                        RLcommonToast("Image uploaded successfully!")
                    } else {
                        RLcommonToast("Failed to upload image URL to database.")
                    }
                }
        }
    }
    private fun RLuploadImageToFirebaseStorage(imageUri: Uri, text: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Get the URL of the uploaded image
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    // Once we have the image URL, save it with the text to the database

                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseStorage", "Image upload failed", e)
            }
    }
    private fun RLRevoolaUserSessionSummaryData(entry: RLWorkoutSessionSummaryModel) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionSummaryData/$currentUser")
        val entryId = (System.currentTimeMillis() / 1000).toString()
        entryId.let {
            databaseRef.child(it).setValue(entry)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "Entry saved successfully!")
                        RLBottomHideShowSet(true)
                        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                        (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
                    } else {
                        Log.e("FirebaseDatabase", "Failed to save entry", task.exception)
                    }
                }
        }
    }
    private fun RLRevoolaUserSessionDetailData(entry: RLWorkoutSessionDetailsModel) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionDetailData/$currentUser")
        val entryId = (System.currentTimeMillis() / 1000).toString()
        entryId.let {
            databaseRef.child(it).setValue(entry)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "Entry saved successfully!")
                        RLBottomHideShowSet(true)
                        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                        (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
                    } else {
                        Log.e("FirebaseDatabase", "Failed to save entry", task.exception)
                    }
                }
        }
    }

    private fun RLNoSensorUserSessionDetailData(entry: RLNoSensorWorkoutSessionDetailsModel) {
       // val databaseRef = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionDetailData/$currentUser")
        val databaseRef = FirebaseDatabase.getInstance().getReference("/${RLConstants.PROPOSEDSTRUCTURE}/${RLConstants.REVOOLAUSERSESSIONDETAILDATA}/$currentUser")
        val entryId = (System.currentTimeMillis() / 1000).toString()
        entryId.let {
            databaseRef.child(it).setValue(entry)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "Entry saved successfully!")
                        RLBottomHideShowSet(true)
                        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                        (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
                    } else {
                        Log.e("FirebaseDatabase", "Failed to save entry", task.exception)
                    }
                }
        }
    }
    private fun RLHeartRateSensorUserSessionDetailData(entry: RLHeartRateSensorWorkoutSessionDetailsModel) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionDetailData/$currentUser")
        val entryId = (System.currentTimeMillis() / 1000).toString()
        entryId.let {
            databaseRef.child(it).setValue(entry)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "Entry saved successfully!")
                        RLBottomHideShowSet(true)
                        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                        (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
                    } else {
                        Log.e("FirebaseDatabase", "Failed to save entry", task.exception)
                    }
                }
        }
    }
    private fun RLSpeedSensorUserSessionDetailData(entry: RLSpeedSensorWorkoutSessionDetailsModel) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionDetailData/$currentUser")
        val entryId = (System.currentTimeMillis() / 1000).toString()
        entryId.let {
            databaseRef.child(it).setValue(entry)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "Entry saved successfully!")
                        RLBottomHideShowSet(true)
                        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                        (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
                    } else {
                        Log.e("FirebaseDatabase", "Failed to save entry", task.exception)
                    }
                }
        }
    }

    //FIREBASE USERDATA GET
    private fun RLFirebaseToFatchUserData() {
        //Firebase To Fetch UserData
        val databaseManager: RLDatabaseManagerRead = RLDatabaseManagerRead()
        val authManager = RLAuthManager()
        val userId = authManager.RlgetCurrentUser()!!.uid
        val path ="/proposedstructure/revoolaUserSettings/$userId/basicData"
        databaseManager.RlreadData(path){ data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                val userData = gson.fromJson(jsonObject, RLRevoolaUsersSettingsModel::class.java)
                wsWeight=userData.weightkg
                wsHeight=userData.height
                wsAge= RLTools.RLCalculateAge(userData.dob)
                gender=userData.gender
                RFMHR=userData.RFMHR
                RestingHR=userData.restingHr
            }
        }
    }
}