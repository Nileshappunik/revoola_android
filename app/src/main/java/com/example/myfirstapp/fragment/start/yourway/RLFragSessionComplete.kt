package com.example.myfirstapp.fragment.start.yourway

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import com.example.myfirstapp.model.RLWorkoutDataModel
import com.example.myfirstapp.model.RLWorkoutSessionDetailsModel
import com.example.myfirstapp.model.RLWorkoutSessionDetailsModelNew
import com.example.myfirstapp.model.RLWorkoutSessionSummaryModel

import com.example.myfirstapp.utils.RLPrefManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import gun0912.tedimagepicker.builder.TedImagePicker
import java.util.UUID
import kotlin.math.roundToInt

class RLFragSessionComplete : RLBaseFragment(){
    val TAG: String = RLFragSessionComplete::class.java.simpleName
    lateinit var fragBinding: RlFragSessionCompleteBinding
    var imgUriList = mutableListOf<Uri>()
    var currentUser =""

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
        val yourWayType = requireArguments().getString("YourWayType").toString().trim()
        val totalTime = requireArguments().getString("totalTime").toString().trim()
        val heartRateList: ArrayList<Int>? = requireArguments().getIntegerArrayList("heartRateList")
        val stepsList: ArrayList<Int>? = requireArguments().getIntegerArrayList("stepsList")
        val paceList: ArrayList<Int>? =requireArguments().getIntegerArrayList("paceList")
        val climbedList: ArrayList<Int>? = requireArguments().getIntegerArrayList("climbedList")
        val avgSpaceList: ArrayList<Int>? = requireArguments().getIntegerArrayList("avgSpaceList")
        val maxPaceList: ArrayList<Int>? = requireArguments().getIntegerArrayList("maxxPaceList")

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
            val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()

            val entryWorkoutSessionDetails = RLWorkoutSessionDetailsModelNew()

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
            entryWorkoutSessionDetails.totalSteps=stepsList!!.maxOrNull()!! ?: 0

            //Zone Entry Value
            entryWorkoutSessionDetails.zone1.remark= "android"
            entryWorkoutSessionDetails.zone1.distance= distanceList.maxOrNull()!!.toDouble() ?: 0.0
            entryWorkoutSessionDetails.zone1.burntCalories= activeCaloriesList.maxOrNull()!!.toDouble() ?: 0.0

            RLRevoolaUserSessionDetailDataNew(entryWorkoutSessionDetails)

            //Multi Array value set entry to database
            /*val entryWorkoutSessionDetails = RLWorkoutSessionDetailsModel()
            entryWorkoutSessionDetails.arrSpeed= speedList!!
            entryWorkoutSessionDetails.arrCadence= stepsList!!
            entryWorkoutSessionDetails.arrDistance= distanceList!!
            entryWorkoutSessionDetails.arrElevation= paceList!!
            entryWorkoutSessionDetails.arrHr= heartRateList!!
            entryWorkoutSessionDetails.arrCumDistance= distanceList
            entryWorkoutSessionDetails.arrBurntCalories= activeCaloriesList!!
            entryWorkoutSessionDetails.totalTime= totalTime.toInt()
            entryWorkoutSessionDetails.displayName= "dhruv90"
            entryWorkoutSessionDetails.displayImage= "https://firebasestorage.googleapis.com/v0/b/rideathome-9080e.appspot.com/o/user_profile_pictures%2Fw2p8SQCvE3emjEEDo66f02eF6fG2%2F1710150587477?alt=media&token=6b0382e2-70cc-4e7d-aa5d-03acf32b05ab"
            entryWorkoutSessionDetails.classType= yourWayType
            entryWorkoutSessionDetails.className= fragBinding.edtSessionName.text.toString()
            entryWorkoutSessionDetails.classNote= fragBinding.edtAddNotes.text.toString()
            entryWorkoutSessionDetails.flagName= "United Kingdom"
            entryWorkoutSessionDetails.flagImage="flag-of-United-Kingdom.png"

            entryWorkoutSessionDetails.zone1.arrHr= heartRateList
            entryWorkoutSessionDetails.zone1.arrCadence= stepsList
            entryWorkoutSessionDetails.zone1.arrSpeed= speedList

            RLRevoolaUserSessionDetailData(entryWorkoutSessionDetails)


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

    private fun RLRevoolaUserSessionDetailDataNew(entry: RLWorkoutSessionDetailsModelNew) {
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