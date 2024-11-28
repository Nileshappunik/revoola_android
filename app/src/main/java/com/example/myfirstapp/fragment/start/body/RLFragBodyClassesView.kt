package com.example.myfirstapp.fragment.start.body

import android.Manifest
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerRead
import com.example.myfirstapp.databinding.RlFragMindClassesViewBinding
import com.example.myfirstapp.fragment.start.classes.RLClassesSchedule
import com.example.myfirstapp.model.RLFulllVideoModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.google.gson.Gson
import java.util.UUID

class RLFragBodyClassesView : RLBaseFragment() {
    val TAG: String = RLFragBodyClassesView::class.java.simpleName
    lateinit var fragBinding: RlFragMindClassesViewBinding
    private val PERMISSION_REQUEST_CODE = 1001
    var videoLink=""

    private val binding by lazy {
        RlFragMindClassesViewBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragBodyClassesView()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_mind_classes_view, container) as RlFragMindClassesViewBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragBodyClassesView" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        val videoID=  requireArguments().getString("VIDEODATA","")
        val ride=  requireArguments().getBoolean("Ride")
        fragBinding.layWorklog.visibility=View.VISIBLE
        fragBinding.viewTimevideo.visibility=View.VISIBLE

        fragBinding.inlayWarmup.txtSubtitle.setText(R.string.warmup)
        fragBinding.inlayWarmup.imgIcon.setImageResource(R.drawable.ic_warmuptime)
        fragBinding.inlayWorkout.txtSubtitle.setText(R.string.workoutcaps)
        fragBinding.inlayWorkout.imgIcon.setImageResource(R.drawable.ic_work_time)
        fragBinding.inlayCooldown.txtSubtitle.setText(R.string.cooldown)
        fragBinding.inlayCooldown.imgIcon.setImageResource(R.drawable.ic_cooldown_time)

        fragBinding.inlaySchdual.txtTitle.setText(R.string.schedule)
        fragBinding.inlaySchdual.imgIcon.setImageResource(R.drawable.ic_calendar_today)
        fragBinding.inlayDownload.txtTitle.setText(R.string.download)
        fragBinding.inlayDownload.imgIcon.setImageResource(R.drawable.ic_download)
        fragBinding.inlayFavourite.txtTitle.setText(R.string.favourite)
        fragBinding.inlayFavourite.imgIcon.setImageResource(R.drawable.ic_saved)
        fragBinding.inlayShare.txtTitle.setText(R.string.share)
        fragBinding.inlayShare.imgIcon.setImageResource(R.drawable.ic_share)

        val databaseManager= RLDatabaseManagerRead()
        databaseManager.RLRevoolaVideosRead(videoID){ data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                val VideoData = gson.fromJson(jsonObject, RLFulllVideoModel::class.java)
                RLBodyUiSetup(VideoData)
                RLClickToSechedule(jsonObject,RLConstants.BODY,"")
                fragBinding.inlayButton.commonButton.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("VIDEODATA",jsonObject)
                    bundle.putString("videoID",videoID)
                    bundle.putBoolean("Ride",ride)
                    (context as RLMainActivityRL).RLloadFrag(RLFragBodyClassSensorChooes().newInstance(bundle), TAG, true,null, false)
                }
                fragBinding.inlayDownload.imgIcon.setOnClickListener {
                    videoLink=VideoData.videoLinkiPhonex.toString()
                    if (RLcheckPermissions()) {
                        RLDownloadVideo(VideoData.videoLinkiPhonex)
                    } else {
                        RlrequestPermissions()
                    }
                }
            }
        }

    }
    private fun RLClickToSechedule(data: String, classtype: String?, audioVideoType: String?) {
        fragBinding.inlaySchdual.relativeCommon.setOnClickListener {
            //RLshowSubscribeDialog()
            val bundle = Bundle()
            bundle.putString("VIDEODATA",data)
           // bundle.putString(RLConstants.CLASSTYPE,classtype)
            bundle.putString("AUDIOVIDEOTYPE",audioVideoType)
            bundle.putString("Message","")
            (context as RLMainActivityRL).RLloadFrag(RLClassesSchedule().newInstance(bundle), TAG, true,null, false)
        }
    }
    private fun RLBodyUiSetup(VideoData:RLFulllVideoModel){
        fragBinding.txtTitle.setText(VideoData.rideTitle)
        fragBinding.txtVideoTitle.setText(VideoData.rideTitle)
        fragBinding.txtNamewith.setText(VideoData.instructor)
        fragBinding.txtTrainerName.setText(VideoData.instructor)
        fragBinding.txtMinutes.setText(VideoData.duration+" CLASS")
        fragBinding.txtVideoDescription.setText(VideoData.rideDescription)
        fragBinding.txtTotalClass.setText(VideoData.instructorClasses+" CLASSES")

        fragBinding.inlayWarmup.txtTitle.setText(VideoData.minwarmup+" MIN")
        fragBinding.inlayCooldown.txtTitle.setText(VideoData.mincooldown+" MIN")
        fragBinding.inlayWorkout.txtTitle.setText(VideoData.mininstruction+" MIN")

        Glide.with(requireContext()).load(VideoData.imageLinkInstructor)
            //.placeholder(R.drawable.sample_user).error(R.drawable.sample_user)
            .into(fragBinding.imgTraner)
        Glide.with(requireContext()).load(VideoData.imageLinkSquareV2)
           // .placeholder(R.drawable.sample_user).error(R.drawable.sample_user)
            .into(fragBinding.imgMainBanner)

        fragBinding.txtVideo.setText(VideoData.difficulty)
        if(VideoData.difficulty.equals("Beginner")){
            fragBinding.imgVideo.setImageResource(R.drawable.ic_easy)
            fragBinding.txtVideo.setTextColor(resources.getColor(R.color.AppMainColor))
        }else if (VideoData.difficulty.equals("Advanced")){
            fragBinding.imgVideo.setImageResource(R.drawable.ic_hard)
            fragBinding.txtVideo.setTextColor(resources.getColor(R.color.AppRedColor))
        }else{
            fragBinding.imgVideo.setImageResource(R.drawable.ic_medium)
            fragBinding.txtVideo.setTextColor(resources.getColor(R.color.AppOrangeColor))
        }

    }
    private fun RLcheckPermissions(): Boolean {
        val writePermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return writePermission == PackageManager.PERMISSION_GRANTED
    }
    private fun RlrequestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                RLDownloadVideo(videoLink)
            }
        }
    }
    private fun RLDownloadVideo(url: String) {
        val uniqueFileName = "video_${UUID.randomUUID()}.mp4"
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Downloading video")
            .setDescription("Downloading a video file")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uniqueFileName)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    override fun onResume() {
        super.onResume()
        RLBottomHideShowSet(true)
    }
    private fun RLshowSubscribeDialog() {
        val sucDialog: Dialog = Dialog(requireContext())
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog.setContentView(R.layout.rl_dialog_subscribe)
        sucDialog.setCancelable(true)
        val tvCancel: TextView = sucDialog.findViewById(R.id.tvCancel)
        val tvSubscribe: TextView = sucDialog.findViewById(R.id.tvSubscribe)
        tvCancel.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })
        tvSubscribe.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })
        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
    }
}