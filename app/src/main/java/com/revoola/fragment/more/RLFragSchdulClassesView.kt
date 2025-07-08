package com.revoola.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.model.RLFulllVideoModel
import com.google.gson.Gson
import com.revoola.RLBaseProgress
import com.revoola.commonobject.RLTools
import com.revoola.databinding.RlFragSchduleClassesViewBinding
import com.revoola.utils.RLPrefManager

class RLFragSchdulClassesView : RLBaseFragment() {
    val TAG: String = RLFragSchdulClassesView::class.java.simpleName
    lateinit var fragBinding: RlFragSchduleClassesViewBinding

    private val binding by lazy {
        RlFragSchduleClassesViewBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragSchdulClassesView()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_schdule_classes_view, container) as RlFragSchduleClassesViewBinding
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSchdulClassesView" )
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        val videoID=  requireArguments().getString("videoID","")
        val createdBy=  requireArguments().getString("createdBy","")
        val dateOfChallenge=  requireArguments().getString("dateOfChallenge","")
        val isMindClass=  requireArguments().getString("isMindClass","")
        val date = RLTools.RLconvertTimestampToSchdualDAte(dateOfChallenge.toString().toLong())
        fragBinding.txtMisseddate.setText(date)
        RLDatabaseManagerRead().RlUserBasicDataRead(createdBy) { data, error ->
            if (data != null) {
                val userData = RLTools.parseUserData(data)
                if (userData != null) {
                    val organizerName = userData.displayName
                    val organizerImage = userData.displayImage
                    fragBinding.txtUsernam.setText(organizerName)
                    Glide.with(requireContext()).load(organizerImage).into(fragBinding.imgUser)
                }
            } else {
                RLBaseProgress.RLhideProgressDialog()
                RLTools.RlLogEPrint(TAG, "Error Fetch Scheduled Request Data: ${error?.message}")
            }
        }
        if (isMindClass.equals("false")){
            RLDatabaseManagerRead().RLRevoolaVideosRead(videoID) { data, error ->
                if (data != null) {
                    val gson = Gson()
                    val jsonObject = gson.toJson(data)
                    val VideoData = gson.fromJson(jsonObject, RLFulllVideoModel::class.java)
                    RLMindUiSetup(VideoData)
                    fragBinding.joinButton.setOnClickListener {
                        //Join Button Click Set Here
                    }
                }
            }
        }else{
            RLDatabaseManagerRead().RLRevoolaVideosMindRead(videoID) { data, error ->
                if (data != null) {
                    val gson = Gson()
                    val jsonObject = gson.toJson(data)
                    val VideoData = gson.fromJson(jsonObject, RLFulllVideoModel::class.java)
                    RLMindUiSetup(VideoData)
                    fragBinding.joinButton.setOnClickListener {
                        //Join Button Click Set Here
                    }
                }
            }
        }

    }
    private fun RLMindUiSetup(VideoData: RLFulllVideoModel){
        fragBinding.txtTitle.setText(VideoData.rideTitle)
        fragBinding.txtVideoTitle.setText(VideoData.rideTitle)
        fragBinding.txtNamewith.setText(VideoData.instructor)
        fragBinding.txtTrainerName.setText(VideoData.instructor)
        fragBinding.txtTotalClass.setText(VideoData.instructorClasses+" CLASSES")
        fragBinding.txtVideoDescription.setText(VideoData.rideDescription)
        fragBinding.txtMinutes.setText(VideoData.duration+" CLASS")
        Glide.with(requireContext()).load(VideoData.imageLinkInstructor).into(fragBinding.imgTraner)
        Glide.with(requireContext()).load(VideoData.imageLinkrectangleV2).into(fragBinding.imgMainBanner)

        fragBinding.txtWarmupMin.setText(safeString(VideoData.minwarmup)+" MIN")
        fragBinding.txtWorkoutMin.setText(safeString(VideoData.mincooldown)+" MIN")
        fragBinding.txtCooldownMin.setText(safeString(VideoData.mininstruction)+" MIN")

        fragBinding.txtEasy.setText(VideoData.difficulty)
        if(VideoData.difficulty.equals("Beginner")){
            fragBinding.imgEasy.setImageResource(R.drawable.ic_easy)
            fragBinding.txtEasy.setTextColor(resources.getColor(R.color.AppMainColor))
        }else if (VideoData.difficulty.equals("Advanced")){
            fragBinding.imgEasy.setImageResource(R.drawable.ic_hard)
            fragBinding.txtEasy.setTextColor(resources.getColor(R.color.AppRedColor))
        }else{
            fragBinding.imgEasy.setImageResource(R.drawable.ic_medium)
            fragBinding.txtEasy.setTextColor(resources.getColor(R.color.AppOrangeColor))
        }
        fragBinding.joinButton.visibility=View.VISIBLE

    }
    private fun safeString(value:String?):String{
        return if (value.isNullOrEmpty()) return "" else value
    }
    override fun onPause() {
        super.onPause()
        RLBottomHideShowSet(true)
    }

}