package com.example.myfirstapp.fragment.start.classes

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
import com.example.myfirstapp.databinding.RlFragSessionCompleteBinding
import com.example.myfirstapp.fragment.overview.RLFragOverviewSession
import com.example.myfirstapp.fragment.start.adapter.RLSelectedImagesAdapter
import com.example.myfirstapp.fragment.start.classes.model.RLBodyVideoHRSensorWorkoutSessionDetailsModel
import com.example.myfirstapp.fragment.start.classes.model.RLBodyVideoWorkoutSessionDetailsModel
import com.example.myfirstapp.fragment.start.classes.model.RLMindAudioWorkoutSessionDetailsModel
import com.example.myfirstapp.model.RLFulllVideoModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import gun0912.tedimagepicker.builder.TedImagePicker

class RLFragClassWorkoutComplete : RLBaseFragment(){
    val TAG: String = RLFragClassWorkoutComplete::class.java.simpleName
    lateinit var fragBinding: RlFragSessionCompleteBinding
    var imgUriList = mutableListOf<Uri>()
    var currentUser =""

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragClassWorkoutComplete()
        fragment.arguments = bundle
        return fragment
    }
    private val binding by lazy {
        RlFragSessionCompleteBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_session_complete, container) as RlFragSessionCompleteBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragClassWorkoutComplete" )
        currentUser=  RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.current_user, "")
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        val data=  requireArguments().getString("VIDEODATA","")
        val gson = Gson()
        val VideoCardData = gson.fromJson(data, RLFulllVideoModel::class.java)
        fragBinding.edtSessionName.setText(VideoCardData.rideTitle)
        fragBinding.txtMainTitle.setText("ACTIVITY COMPLETE!")

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

        fragBinding.imgCancle.setOnClickListener {
            RLBottomHideShowSet(true)
            (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
            (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false,null, false)
        }
        fragBinding.inlayButton.commonButton.setText(R.string.save)
        fragBinding.inlayButton.commonButton.setOnClickListener {
            val classType=  requireArguments().getString(RLConstants.CLASSTYPE,"")
            val sensorType=  requireArguments().getString(RLConstants.HEARTSENSOR,"")
            val totalTime=  requireArguments().getString("totalTime","0")
            val heartRateList: ArrayList<Int>? = requireArguments().getIntegerArrayList("heartRateList")
            if (classType.equals(RLConstants.BODY)){

                val speedArray = arguments?.getDoubleArray("speedList")
                val distanceArray = arguments?.getDoubleArray("distanceList")
                val activeCaloriesArray = arguments?.getDoubleArray("activeCaloriesList")

                val climbedList: ArrayList<Int>? = requireArguments().getIntegerArrayList("climbedList")
                val speedList: MutableList<Double> = speedArray?.toMutableList() ?: mutableListOf()
                val distanceList: MutableList<Double> = distanceArray?.toMutableList() ?: mutableListOf()
                val activeCaloriesList: MutableList<Double> = activeCaloriesArray?.toMutableList() ?: mutableListOf()

                if (sensorType.equals(RLConstants.HEARTSENSOR)){
                    RlBodyHeartRateDataEntryToFirebase(classType,totalTime,heartRateList,VideoCardData,climbedList!!,speedList,distanceList,activeCaloriesList)
                }else  {
                    RlBodyNoSensorDataEntryToFirebase(classType,totalTime,heartRateList,VideoCardData,climbedList!!,speedList,distanceList,activeCaloriesList)
                }
            }else{
                RlMindNoSensorDataEntryToFirebase(classType,totalTime,VideoCardData,heartRateList)
            }

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

    //All Mind Video with Sensor or Without Sensor
    private fun RlMindNoSensorDataEntryToFirebase(classType: String, totalTime: String, videoCardData: RLFulllVideoModel, heartRateList: ArrayList<Int>?) {
        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()

        val entryWorkoutSessionDetails = RLMindAudioWorkoutSessionDetailsModel()

        entryWorkoutSessionDetails.MaxHrUsedForCalculation=0
        entryWorkoutSessionDetails.MaxHrUsedForCalculation_Last=0
        entryWorkoutSessionDetails.RestingHrUsedForCalculation=0
        entryWorkoutSessionDetails.RestingHrUsedForCalculation_Last=0

        //Array Entry Value
        entryWorkoutSessionDetails.arrHr= heartRateList!!


        //Normal Entry Value
        entryWorkoutSessionDetails.totalTime= totalTime.toInt()
        entryWorkoutSessionDetails.classType= videoCardData.classType// yourWayType
        entryWorkoutSessionDetails.className= fragBinding.edtSessionName.text.toString()
        entryWorkoutSessionDetails.classNote= fragBinding.edtAddNotes.text.toString()
        entryWorkoutSessionDetails.instructor=videoCardData.instructor
        entryWorkoutSessionDetails.remark="android"
        entryWorkoutSessionDetails.imageLinkLarge=videoCardData.imageLinkLarge
        entryWorkoutSessionDetails.imageLinkSmall=videoCardData.imageLinkSmall
        entryWorkoutSessionDetails.timestamp=currentTimestamp.toInt()
        entryWorkoutSessionDetails.videoKey=videoCardData.videoLinkiPhone
        entryWorkoutSessionDetails.rideTitle=videoCardData.rideTitle
        entryWorkoutSessionDetails.classDescription=videoCardData.rideDescription
        entryWorkoutSessionDetails.displayImage="https://firebasestorage.googleapis.com/v0/b/rideathome-9080e.appspot.com/o/user_profile_pictures%2Fw2p8SQCvE3emjEEDo66f02eF6fG2%2F1710150587477?alt=media&token=6b0382e2-70cc-4e7d-aa5d-03acf32b05ab"
        entryWorkoutSessionDetails.displayName="dhruv90"
        entryWorkoutSessionDetails.flagName= "United Kingdom"
        entryWorkoutSessionDetails.flagImage="flag-of-United-Kingdom.png"
        entryWorkoutSessionDetails.isClass=true
        entryWorkoutSessionDetails.originalClassDate=videoCardData.originalClassDate

        entryWorkoutSessionDetails.classDate=""
        entryWorkoutSessionDetails.classImage=""
        entryWorkoutSessionDetails.location=0
        entryWorkoutSessionDetails.rms=0
        entryWorkoutSessionDetails.visibilityflagforthatsession=0
        entryWorkoutSessionDetails.isMindClass=0
        entryWorkoutSessionDetails.mainTitle=""
        entryWorkoutSessionDetails.duration=videoCardData.duration




        RLMindNoAndSpeedSensorUserSessionDetailData(entryWorkoutSessionDetails)
    }
    //Body Video with Speed sensor or Without Sensor
    private fun RlBodyNoSensorDataEntryToFirebase(yourWayType: String, totalTime: String, heartRateList: ArrayList<Int>?, videoCardData: RLFulllVideoModel,
                                                  climbedList: ArrayList<Int>,
                                                  speedList: MutableList<Double>,
                                                  distanceList: MutableList<Double>,
                                                  activeCaloriesList: MutableList<Double>) {
        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()

        val entryWorkoutSessionDetails = RLBodyVideoWorkoutSessionDetailsModel()

        entryWorkoutSessionDetails.MaxHrUsedForCalculation=0
        entryWorkoutSessionDetails.MaxHrUsedForCalculation_Last=0
        entryWorkoutSessionDetails.RestingHrUsedForCalculation=0
        entryWorkoutSessionDetails.RestingHrUsedForCalculation_Last=0

        //Array Entry Value
        entryWorkoutSessionDetails.arrBurntCalories=activeCaloriesList
        entryWorkoutSessionDetails.arrCadence=climbedList
        entryWorkoutSessionDetails.arrSpeed=speedList
        entryWorkoutSessionDetails.arrDistance=distanceList
//        entryWorkoutSessionDetails.arrCumDistance=
//        entryWorkoutSessionDetails.arrCumSpeed=
//         entryWorkoutSessionDetails.arrAvgRevPercentage=
//        entryWorkoutSessionDetails.arrMaxRevPercentage=
//        entryWorkoutSessionDetails.arrPower=
//        entryWorkoutSessionDetails.arrPowerFromDevice=


        //Normal Entry Value
        entryWorkoutSessionDetails.totalTime= totalTime.toInt()
        entryWorkoutSessionDetails.classType= videoCardData.classType// yourWayType
        entryWorkoutSessionDetails.className= fragBinding.edtSessionName.text.toString()
        entryWorkoutSessionDetails.classNote= fragBinding.edtAddNotes.text.toString()
        entryWorkoutSessionDetails.remark="android"
        entryWorkoutSessionDetails.imageLinkLarge=videoCardData.imageLinkLarge
        entryWorkoutSessionDetails.imageLinkSmall=videoCardData.imageLinkSmall
        entryWorkoutSessionDetails.timestamp=currentTimestamp.toInt()
        entryWorkoutSessionDetails.videoKey=videoCardData.videoLinkiPhone
        entryWorkoutSessionDetails.classDescription=videoCardData.rideDescription
        entryWorkoutSessionDetails.displayImage="https://firebasestorage.googleapis.com/v0/b/rideathome-9080e.appspot.com/o/user_profile_pictures%2Fw2p8SQCvE3emjEEDo66f02eF6fG2%2F1710150587477?alt=media&token=6b0382e2-70cc-4e7d-aa5d-03acf32b05ab"
        entryWorkoutSessionDetails.displayName="dhruv90"
        entryWorkoutSessionDetails.flagName= "United Kingdom"
        entryWorkoutSessionDetails.flagImage="flag-of-United-Kingdom.png"
        entryWorkoutSessionDetails.isClass=true


        entryWorkoutSessionDetails.avgRevPercentage=0
        entryWorkoutSessionDetails.burntCalories=0.0
        entryWorkoutSessionDetails.classDate=""
        entryWorkoutSessionDetails.classImage=""
        entryWorkoutSessionDetails.demsElevation=0
        entryWorkoutSessionDetails.distance=0.0
        entryWorkoutSessionDetails.isPowerDeviceConnected=false
        entryWorkoutSessionDetails.location=0
        entryWorkoutSessionDetails.maxRevPercentage=0
        entryWorkoutSessionDetails.minRevPercentage=0
        entryWorkoutSessionDetails.revPercentage=0
        entryWorkoutSessionDetails.rms=0
        entryWorkoutSessionDetails.totalElevation=0
        entryWorkoutSessionDetails.totalPower=0
        entryWorkoutSessionDetails.totalRev=0.0
        entryWorkoutSessionDetails.visibilityflagforthatsession=0


        //Zone Entry Value
        entryWorkoutSessionDetails.zone1.remark= "android"
        entryWorkoutSessionDetails.zone1.burntCalories=0.0
        entryWorkoutSessionDetails.zone1.distance=0.0
        entryWorkoutSessionDetails.zone1.seconds=0
        entryWorkoutSessionDetails.zone1.totalRev=0

        RLBodyNoSensorUserSessionDetailData(entryWorkoutSessionDetails)


    }
    //Body Video with HR Sensor
    private fun RlBodyHeartRateDataEntryToFirebase(
        yourWayType: String,
        totalTime: String,
        heartRateList: ArrayList<Int>?,
        videoCardData: RLFulllVideoModel,
        climbedList: ArrayList<Int>,
        speedList: MutableList<Double>,
        distanceList: MutableList<Double>,
        activeCaloriesList: MutableList<Double>
    ) {
        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()

        val entryWorkoutSessionDetails = RLBodyVideoHRSensorWorkoutSessionDetailsModel()

        //Array Entry Value
        entryWorkoutSessionDetails.arrHr= heartRateList!!
        entryWorkoutSessionDetails.arrDistance=distanceList
        entryWorkoutSessionDetails.arrBurntCalories=activeCaloriesList
        entryWorkoutSessionDetails.arrCadence=climbedList
        entryWorkoutSessionDetails.arrSpeed=speedList
//        entryWorkoutSessionDetails.arrCumDistance=
//        entryWorkoutSessionDetails.arrCumSpeed=
//        entryWorkoutSessionDetails.arrAvgRevPercentage=
//        entryWorkoutSessionDetails.arrMaxRevPercentage=
//        entryWorkoutSessionDetails.arrPower=
//        entryWorkoutSessionDetails.arrPowerFromDevice=
//        entryWorkoutSessionDetails.arrSpeed=speedList
//        entryWorkoutSessionDetails.arrRevPercentage=
//        entryWorkoutSessionDetails.arrRevSecond=


        //Normal Entry Value
        entryWorkoutSessionDetails.totalTime= totalTime.toInt()
        entryWorkoutSessionDetails.classType= videoCardData.classType// yourWayType
        entryWorkoutSessionDetails.className= fragBinding.edtSessionName.text.toString()
        entryWorkoutSessionDetails.classNote= fragBinding.edtAddNotes.text.toString()
        entryWorkoutSessionDetails.remark="android"
        entryWorkoutSessionDetails.imageLinkLarge=videoCardData.imageLinkLarge
        entryWorkoutSessionDetails.imageLinkSmall=videoCardData.imageLinkSmall
        entryWorkoutSessionDetails.timestamp=currentTimestamp.toInt()
        entryWorkoutSessionDetails.videoKey=videoCardData.videoLinkiPhone
        entryWorkoutSessionDetails.classDescription=videoCardData.rideDescription
        entryWorkoutSessionDetails.displayImage="https://firebasestorage.googleapis.com/v0/b/rideathome-9080e.appspot.com/o/user_profile_pictures%2Fw2p8SQCvE3emjEEDo66f02eF6fG2%2F1710150587477?alt=media&token=6b0382e2-70cc-4e7d-aa5d-03acf32b05ab"
        entryWorkoutSessionDetails.displayName="dhruv90"
        entryWorkoutSessionDetails.flagName= "United Kingdom"
        entryWorkoutSessionDetails.flagImage="flag-of-United-Kingdom.png"
        entryWorkoutSessionDetails.isClass=true

        entryWorkoutSessionDetails.avgRevPercentage=0
        entryWorkoutSessionDetails.burntCalories=0.0
        entryWorkoutSessionDetails.classDate=""
        entryWorkoutSessionDetails.classImage=""
        entryWorkoutSessionDetails.demsElevation=0
        entryWorkoutSessionDetails.distance= 0.0
        entryWorkoutSessionDetails.isPowerDeviceConnected=false
        entryWorkoutSessionDetails.location=0
        entryWorkoutSessionDetails.maxRevPercentage=0
        entryWorkoutSessionDetails.minRevPercentage=0
        entryWorkoutSessionDetails.revPercentage=0
        entryWorkoutSessionDetails.rms=0
        entryWorkoutSessionDetails.totalElevation=0
        entryWorkoutSessionDetails.totalPower=0
        entryWorkoutSessionDetails.totalRev=0.0
        entryWorkoutSessionDetails.visibilityflagforthatsession=0

        entryWorkoutSessionDetails.MaxHrUsedForCalculation=0
        entryWorkoutSessionDetails.MaxHrUsedForCalculation_Last=0
        entryWorkoutSessionDetails.RestingHrUsedForCalculation=0
        entryWorkoutSessionDetails.RestingHrUsedForCalculation_Last=0


        //Zone Entry Value
        entryWorkoutSessionDetails.zone1.remark= "android"
        entryWorkoutSessionDetails.zone1.remark= "android"
        entryWorkoutSessionDetails.zone1.burntCalories=0.0
        entryWorkoutSessionDetails.zone1.distance=0.0
        entryWorkoutSessionDetails.zone1.seconds=0
        entryWorkoutSessionDetails.zone1.totalRev=0


        RLBodyHeartRateSensorUserSessionDetailData(entryWorkoutSessionDetails)

    }

    private fun RLBodyHeartRateSensorUserSessionDetailData(entry: RLBodyVideoHRSensorWorkoutSessionDetailsModel) {
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
    private fun RLBodyNoSensorUserSessionDetailData(entry: RLBodyVideoWorkoutSessionDetailsModel) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionDetailData/$currentUser")
        val entryId = (System.currentTimeMillis() / 1000).toString()
        entryId.let {
            databaseRef.child(it).setValue(entry)
                .addOnCompleteListener { task ->
                    Log.d("FirebaseDatabase", "Entry saved successfully!")
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
    private fun RLMindNoAndSpeedSensorUserSessionDetailData(entry: RLMindAudioWorkoutSessionDetailsModel) {
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
}