package com.example.myfirstapp.fragment.start.yourway

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlFragSessionCompleteBinding
import com.example.myfirstapp.fragment.overview.RLFragOverview
import com.example.myfirstapp.fragment.overview.RLFragOverviewSession

import com.example.myfirstapp.utils.RLPrefManager

class RLFragSessionComplete : RLBaseFragment(){
    val TAG: String = RLFragSessionComplete::class.java.simpleName
    lateinit var fragBinding: RlFragSessionCompleteBinding

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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_session_complete, container) as RlFragSessionCompleteBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSessionComplete" )
        RLuisetup()
        (context as RLMainActivityRL).RLhidebottombarcolorwhite()
        return fragBinding.root
    }

    private fun RLuisetup() {
        //"Pilates","Ride","Run","Walk","Workout","Yoga" ic_pace ic_speeed
     //   val yourWayType = requireArguments().getString("YourWayType").toString().trim()
        fragBinding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
            // Handle checked change
        }
        fragBinding.layPrivacy.setOnClickListener {
            val titletxt:String=fragBinding.tvShareTitle.text.toString().toUpperCase()

            if (titletxt.equals("FRIENDS")){
                fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyPrivateBGColor))
                fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyprivate)
                fragBinding.tvShareTitle.setText(R.string.privatetx)
                fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyPrivateColor))
                RLShareMapHide(false)
            }else if (titletxt.equals("EVERYONE")){
                fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyFriendsBGColor))
                fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyfriends)
                fragBinding.tvShareTitle.setText(R.string.friendstx)
                fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyFriendsColor))
                RLShareMapHide(true)
            }else if (titletxt.equals("PRIVATE")){
                fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyEveryOneBGColor))
                fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyeveryone)
                fragBinding.tvShareTitle.setText(R.string.everyone)
                fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyEveryOneColor))
                RLShareMapHide(true)
            }

        }

        fragBinding.imgCancle.setOnClickListener {
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
            (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
        }
        fragBinding.tvSave.setOnClickListener {
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
            (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
        }
        fragBinding.relativeAddPhotos.setOnClickListener {
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
    private  fun RLchooseFromGallery() {
        val pickImagesIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickImagesIntent.type = "image/*"
        pickImagesIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allow multiple selections
        RLchangeImage.launch(pickImagesIntent)

    }

    val RLchangeImage =registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            val imgUriList = mutableListOf<Uri>()
            data?.data?.let { uri ->
                imgUriList.add(uri)
            }
          
        }
    }
}