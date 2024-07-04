package com.example.myfirstapp.fragment.start.mind

import android.Manifest
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
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlFragMindClassesViewBinding
import com.example.myfirstapp.fragment.start.classes.RLClassesSchedule
import com.example.myfirstapp.model.RLFulllVideoModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.google.gson.Gson
import java.util.UUID

class RLFragMindClassesView : RLBaseFragment() {
    val TAG: String = RLFragMindClassesView::class.java.simpleName
    lateinit var fragBinding: RlFragMindClassesViewBinding
    private val PERMISSION_REQUEST_CODE = 1001
    var videoLink=""
    private val binding by lazy {
        RlFragMindClassesViewBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragMindClassesView()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_mind_classes_view, container) as RlFragMindClassesViewBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragMindClassesView" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        val data=  requireArguments().getString("VIDEODATA","")
        val gson = Gson()
        val VideoCardData = gson.fromJson(data, RLFulllVideoModel::class.java)
        fragBinding.layWorklog.visibility=View.GONE
        fragBinding.viewTimevideo.visibility=View.GONE
        fragBinding.imgFavourite.setImageResource(R.drawable.ic_saved)
        fragBinding.txtVideo.setTextColor(resources.getColor(R.color.AppTextGrayColor))
        val audioVideoType=  requireArguments().getString("AUDIOVIDEOTYPE","")
        fragBinding.txtVideo.setText(audioVideoType)
        if (audioVideoType.equals("Video")){
            fragBinding.imgVideo.setImageResource(R.drawable.ic_video)
        }else{
            fragBinding.imgVideo.setImageResource(R.drawable.ic_audio)
        }
        RLMindUiSetup(VideoCardData)
        RLClickToSechedule(data,RLConstants.MIND,audioVideoType)
        fragBinding.btnStartclass.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("VIDEODATA",data)
            bundle.putString("AUDIOVIDEOTYPE",audioVideoType)
            (context as RLMainActivityRL).RLloadFrag(RLFragMindClassSensorChooes().newInstance(bundle), TAG, true,null, false)
        }
        fragBinding.imgDownload.setOnClickListener {
            videoLink=VideoCardData.videoLinkiPhonex.toString()
            if (checkPermissions()) {
                downloadVideo(VideoCardData.videoLinkiPhonex)
            } else {
                requestPermissions()
            }
        }
    }
    private fun RLClickToSechedule(data: String, classtype: String?, audioVideoType: String?) {
        fragBinding.rlSchdual.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("VIDEODATA",data)
            bundle.putString(RLConstants.CLASSTYPE,classtype)
            bundle.putString("AUDIOVIDEOTYPE",audioVideoType)
            (context as RLMainActivityRL).RLloadFrag(RLClassesSchedule().newInstance(bundle), TAG, true,null, false)
        }
    }
    private fun RLMindUiSetup(VideoData:RLFulllVideoModel){
        fragBinding.txtTitle.setText(VideoData.rideTitle)
        fragBinding.txtVideoTitle.setText(VideoData.rideTitle)
        fragBinding.txtNamewith.setText(VideoData.instructor)
        fragBinding.txtTrainerName.setText(VideoData.instructor)
        fragBinding.txtTotalClass.setText(VideoData.instructorClasses+" CLASSES")
        fragBinding.txtVideoDescription.setText(VideoData.rideDescription)
        fragBinding.txtMinutes.setText(VideoData.duration)
        Glide.with(requireContext()).load(VideoData.imageLinkInstructor)
            //.placeholder(R.drawable.wellcome).error(R.drawable.wellcome)
            .into(fragBinding.imgTraner)
        Glide.with(requireContext()).load(VideoData.imageLinkSquareV2)
            //.placeholder(R.drawable.wellcome).error(R.drawable.wellcome)
            .into(fragBinding.imgMainBanner)
    }
    private fun checkPermissions(): Boolean {
        val writePermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return writePermission == PackageManager.PERMISSION_GRANTED
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadVideo(videoLink)
            }
        }
    }
    private fun downloadVideo(url: String) {
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

}