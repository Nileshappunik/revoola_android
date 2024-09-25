package com.example.myfirstapp.fragment.start.classes

import android.content.pm.ActivityInfo
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
import com.example.myfirstapp.fragment.overview.RLFragOverview
import com.example.myfirstapp.fragment.overview.RLFragOverviewSession
import com.example.myfirstapp.fragment.start.adapter.RLSelectedImagesAdapter
import com.example.myfirstapp.fragment.start.classes.model.RLBodyVideoHRSensorWorkoutSessionDetailsModel
import com.example.myfirstapp.fragment.start.classes.model.RLBodyVideoWorkoutSessionDetailsModel
import com.example.myfirstapp.fragment.start.classes.model.RLMindAudioWorkoutSessionDetailsModel
import com.example.myfirstapp.model.RLFulllVideoModel
import com.example.myfirstapp.model.RLHeartRateSensorWorkoutSessionDetailsModel
import com.example.myfirstapp.model.RLNoSensorWorkoutSessionDetailsModel
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
        val classType=  requireArguments().getString(RLConstants.CLASSTYPE,"")
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
            if (classType.equals(RLConstants.BODY)){
                //RlBodyNoSensorDataEntryToFirebase(classType,VideoCardData)
            }else{
                //RlMindNoSensorDataEntryToFirebase(classType,VideoCardData)
            }

        }
        fragBinding.txtAddPhoto.setOnClickListener {
            RLchooseFromGallery()
        }

    }

    private fun RlMindNoSensorDataEntryToFirebase(classType:String,videoCardData: RLFulllVideoModel) {
        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()

        val entryWorkoutSessionDetails = RLMindAudioWorkoutSessionDetailsModel()

        //Array Entry Value
       // entryWorkoutSessionDetails.arrHr= heartRateList!!


        //Normal Entry Value
      //  entryWorkoutSessionDetails.totalTime= totalTime.toInt()
        entryWorkoutSessionDetails.classType= classType
        entryWorkoutSessionDetails.className= fragBinding.edtSessionName.text.toString()
        entryWorkoutSessionDetails.classNote= fragBinding.edtAddNotes.text.toString()
        entryWorkoutSessionDetails.remark="android"
        entryWorkoutSessionDetails.instructor=videoCardData.instructor
        entryWorkoutSessionDetails.videoKey=videoCardData.videoLinkiPhone

        RLMindNoAndSpeedSensorUserSessionDetailData(entryWorkoutSessionDetails)
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

    private fun RlMindNoSensorDataEntryToFirebase1(yourWayType: String, totalTime: String, heartRateList: ArrayList<Int>?,videoCardData: RLFulllVideoModel) {
        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()

        val entryWorkoutSessionDetails = RLMindAudioWorkoutSessionDetailsModel()

        //Array Entry Value
        entryWorkoutSessionDetails.arrHr= heartRateList!!


        //Normal Entry Value
        entryWorkoutSessionDetails.totalTime= totalTime.toInt()
        entryWorkoutSessionDetails.classType= yourWayType
        entryWorkoutSessionDetails.className= fragBinding.edtSessionName.text.toString()
        entryWorkoutSessionDetails.classNote= fragBinding.edtAddNotes.text.toString()
        entryWorkoutSessionDetails.remark="android"
        entryWorkoutSessionDetails.instructor="android"

        RLMindNoAndSpeedSensorUserSessionDetailData(entryWorkoutSessionDetails)


    }

    private fun RlBodyNoSensorDataEntryToFirebase(
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
        videoCardData: RLFulllVideoModel,
        activeCaloriesList: MutableList<Double>) {
        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()

        val entryWorkoutSessionDetails = RLBodyVideoWorkoutSessionDetailsModel()

        //Array Entry Value
        entryWorkoutSessionDetails.arrSpeed= speedList
        entryWorkoutSessionDetails.arrDistance= distanceList
        entryWorkoutSessionDetails.arrBurntCalories= activeCaloriesList

        //Normal Entry Value
        entryWorkoutSessionDetails.totalTime= totalTime.toInt()
        entryWorkoutSessionDetails.classType= yourWayType
        entryWorkoutSessionDetails.className= fragBinding.edtSessionName.text.toString()
        entryWorkoutSessionDetails.classNote= fragBinding.edtAddNotes.text.toString()
        entryWorkoutSessionDetails.remark="android"
        entryWorkoutSessionDetails.distance=distanceList.maxOrNull()!!.toDouble() ?: 0.0
        entryWorkoutSessionDetails.burntCalories=activeCaloriesList.maxOrNull()!!.toDouble() ?: 0.0


        //Zone Entry Value
        entryWorkoutSessionDetails.zone1.remark= "android"
        entryWorkoutSessionDetails.zone1.distance= distanceList.maxOrNull()!!.toDouble() ?: 0.0
        entryWorkoutSessionDetails.zone1.burntCalories= activeCaloriesList.maxOrNull()!!.toDouble() ?: 0.0

        RLBodyNoSensorUserSessionDetailData(entryWorkoutSessionDetails)


    }

    private fun RlBodyHeartRateDataEntryToFirebase(yourWayType: String,
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
                                                   videoCardData: RLFulllVideoModel,
                                                   activeCaloriesList: MutableList<Double>) {
        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()

        val entryWorkoutSessionDetails = RLBodyVideoHRSensorWorkoutSessionDetailsModel()

        //Array Entry Value
        entryWorkoutSessionDetails.arrSpeed= speedList
        entryWorkoutSessionDetails.arrDistance= distanceList
        entryWorkoutSessionDetails.arrBurntCalories= activeCaloriesList
        entryWorkoutSessionDetails.arrHr= heartRateList!!


        //Normal Entry Value
        entryWorkoutSessionDetails.totalTime= totalTime.toInt()
        entryWorkoutSessionDetails.classType= yourWayType
        entryWorkoutSessionDetails.className= fragBinding.edtSessionName.text.toString()
        entryWorkoutSessionDetails.classNote= fragBinding.edtAddNotes.text.toString()
        entryWorkoutSessionDetails.remark="android"


        //Zone Entry Value
        entryWorkoutSessionDetails.zone1.remark= "android"


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