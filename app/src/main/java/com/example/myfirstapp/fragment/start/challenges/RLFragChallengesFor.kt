package com.example.myfirstapp.fragment.start.challenges

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.api.RLApiClientRet
import com.example.myfirstapp.databinding.RlDialogFriendChallengesBinding
import com.example.myfirstapp.databinding.RlDialogHelpChallengesForBinding
import com.example.myfirstapp.databinding.RlFragChallengesForBinding
import com.example.myfirstapp.fragment.start.challenges.adapter.RLChallengeForFriendListAdapter
import com.example.myfirstapp.fragment.start.challenges.adapter.RLChallengeForGroupListAdapter
import com.example.myfirstapp.model.RLSetsearch_user
import com.example.myfirstapp.model.RLSetsearch_userrequest
import com.example.myfirstapp.model.RLrequestgroup_dataset
import com.example.myfirstapp.model.RLsetgroup_data
import com.example.myfirstapp.model.RLuserData
import com.example.myfirstapp.model.RLyourGroupDataModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTools
import com.example.myfirstapp.viewmodel.RLMainRepository
import com.example.myfirstapp.viewmodel.RLMainViewModel
import com.example.myfirstapp.viewmodel.RLMainViewModelFactory

class RLFragChallengesFor : RLBaseFragment() {
    val TAG: String = RLFragChallengesFor::class.java.simpleName
    lateinit var fragBinding: RlFragChallengesForBinding
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""
    var challengeType=""
    var calenderType=""

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragChallengesFor()
        fragment.arguments = bundle
        return fragment
    }
    private val binding by lazy {
        RlFragChallengesForBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_challenges_for, container) as RlFragChallengesForBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChallengesFor" )
        currentUser=  RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.current_user, "")
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.RLNetworkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(), RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        fragBinding.inlayTop.ivBack.setOnClickListener {
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLbottombarcolorwhite()
            RLcloseFragment()
        }
        fragBinding.inlayTop.ivTitle.setText(R.string.challengesfor)
        fragBinding.inlayTop.ivDescription.setText(R.string.challengeforyouorwithothers)
        fragBinding.inlayTop.ivhelp.setOnClickListener {
            RLshowHelpDialog()
        }
        RLUIBottom()
    }
    private fun RLUIBottom() {

         challengeType = requireArguments().getString("ChallengeType").toString().trim()
         calenderType = requireArguments().getString("CalenderType").toString().trim()

        RLTools.RLheightsetstartimage(fragBinding.relayYou.cardChalengesst,requireActivity())
        RLTools.RLheightsetstartimage(fragBinding.relayFriends.cardChalengesst,requireActivity())
        RLTools.RLheightsetstartimage(fragBinding.relayGroup.cardChalengesst,requireActivity())
        RLTools.RLheightsetstartimage(fragBinding.relayGroupVGroup.cardChalengesst,requireActivity())

        fragBinding.relayYou.imgType.setImageResource(R.drawable.you)
        fragBinding.relayYou.txtTypeTitle.setText(R.string.you)

        fragBinding.relayFriends.imgType.setImageResource(R.drawable.ic_friends)
        fragBinding.relayFriends.txtTypeTitle.setText(R.string.friends)

        fragBinding.relayGroup.imgType.setImageResource(R.drawable.ic_groups)
        fragBinding.relayGroup.txtTypeTitle.setText(R.string.group)

        fragBinding.relayGroupVGroup.imgType.visibility=View.GONE
        fragBinding.relayGroupVGroup.imgTypeFull.visibility=View.VISIBLE
        fragBinding.relayGroupVGroup.imgTypeFull.setImageResource(R.drawable.ic_group_v_group)
        fragBinding.relayGroupVGroup.txtTypeTitle.setText(R.string.groupvgroup)

        fragBinding.relayYou.cardChalengesst.setOnClickListener {
            var bundle: Bundle = Bundle()
            bundle.putString("ChallengeType",challengeType )
            bundle.putString("CalenderType",calenderType )
            (context as RLMainActivityRL).RLhidebottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragChallengesForName().newInstance(bundle), TAG, true,null, false)

        }
        fragBinding.relayFriends.cardChalengesst.setOnClickListener {
            RLshowFriend()
        }
        fragBinding.relayGroup.cardChalengesst.setOnClickListener {
            RLshowGroup(false)
        }
        fragBinding.relayGroupVGroup.cardChalengesst.setOnClickListener {
            RLshowGroup(true)
        }

    }
    private fun RLshowHelpDialog() {
        val dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: RlDialogHelpChallengesForBinding =
            RlDialogHelpChallengesForBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(true)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        dialogMainBinding.tvClose.setOnClickListener {
            dialog.dismiss()
        }



        dialogMainBinding.layYou.txtHeader.setText(getString(R.string.you)+":")
        dialogMainBinding.layYou.txtHeaderDescription.setText("SET YOURSELF A PERSONAL CHALLENGE.")
        dialogMainBinding.layYou.imgHelpChallenges.setImageResource(R.drawable.fd_steps_green)

        dialogMainBinding.layFriends.txtHeader.setText(getString(R.string.friends)+":")
        val friendDescription="SET A CHALLENGE FOR YOU AND YOUR FRIENDS. WHERE EACH OF YOU ARE CHALLENGED TO ACHIEVE THE SAME GOAL."
        dialogMainBinding.layFriends.txtHeaderDescription.setText(friendDescription)
        dialogMainBinding.layFriends.imgHelpChallenges.setImageResource(R.drawable.fd_active_time_green)

        dialogMainBinding.layGroups.txtHeader.setText(getString(R.string.groups)+":")
        val groupDescription="JUST LIKE A FRIENDS CHALLENGE YOU CAN CHALLENGE ALL THE MEMBERS OF ANY OF THE GROUPS THAT YOU ARE A MEMBER OF, TO ACHIEVE THE SAME GOAL."
        dialogMainBinding.layGroups.txtHeaderDescription.setText(groupDescription)
        dialogMainBinding.layGroups.imgHelpChallenges.setImageResource(R.drawable.fd_active_time_green)

        dialogMainBinding.layGroupsVGroups.txtHeader.setText(getString(R.string.groupvgroup)+":")
        val groupvDescription="GROUP V GROUP CHALLENGES ARE WHERE A GROUP IS COLLECTIVELY TRYING TO BEAT THE OTHER GROUP TO ACHIEVE THE GOAL. FOR INSTANCE, CAN GROUP AACHIEVE 100,000 STEPS BEFORE GROUP B?"
        dialogMainBinding.layGroupsVGroups.txtHeaderDescription.setText(groupvDescription)
        dialogMainBinding.layGroupsVGroups.imgHelpChallenges.setImageResource(R.drawable.fd_active_time_green)


        dialog.show()

    }

    private fun RLshowGroup(isGroupVGroup:Boolean) {
        val dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: RlDialogFriendChallengesBinding =
            RlDialogFriendChallengesBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(true)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        dialogMainBinding.tvTitle.setText("Select Groups")
        dialogMainBinding.tvClose.setOnClickListener {
            dialog.dismiss()
        }

        RLgroupApiCall(dialogMainBinding,isGroupVGroup,dialog)
        dialog.show()

    }
    private fun RLgroupApiCall(dialogMainBinding: RlDialogFriendChallengesBinding,isGroupVGroup:Boolean, dialog: Dialog ) {
        val request = listOf(
            RLrequestgroup_dataset(
            group_data = RLsetgroup_data(userid = currentUser,limit = 100, index=0)
            )
        )
        Log.d(TAG,"setgroupdata= "+request)

        viewModel.RLyourGroupData(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        Log.d(TAG,"Success= "+response.type)
                        RLresponsehandleGroupsApi(response.text,dialogMainBinding,isGroupVGroup,dialog)
                    }else {
                        Log.d(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){ e.printStackTrace()
                    Log.d(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->
                RLcommonToast(RLConstants.SERVER_PROBLEM)
                Log.d(TAG,"Error= "+error.message)
            }
        }
    }
    private fun RLresponsehandleGroupsApi(groupdata: List<RLyourGroupDataModel>,dialogMainBinding: RlDialogFriendChallengesBinding,isGroupVGroup:Boolean, dialog: Dialog ) {
        var selectGroupdata: List<RLyourGroupDataModel> = mutableListOf()
        val linearLayoutManager = LinearLayoutManager(activity)
        dialogMainBinding.recyclerFriend.layoutManager = linearLayoutManager
        val adaptergroup = RLChallengeForGroupListAdapter(activity,groupdata){ cardData ->
            // Handle selection
            if (cardData.isSelected){
                selectGroupdata += listOf(cardData)
            }else{
                selectGroupdata -= listOf(cardData)
            }

        }
        dialogMainBinding.recyclerFriend.adapter = adaptergroup

        dialogMainBinding.edtFriendSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adaptergroup.RLfilter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        dialogMainBinding.tvSelect.setOnClickListener {
            if (isGroupVGroup){
                if (selectGroupdata.size>1){
                    RLNextFragmentOpen(true)
                    dialog.dismiss()
                }else{
                    dialog.dismiss()
                    RLshowAlertDialog("Please Select Multi Groups")
                }
            }else  if ( selectGroupdata.size>0){
                RLNextFragmentOpen(true)
                dialog.dismiss()
            }else{
                dialog.dismiss()
                RLshowAlertDialog("Please Select One Group")
            }


        }
    }
    private fun RLshowFriend() {
        val dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: RlDialogFriendChallengesBinding =
            RlDialogFriendChallengesBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(true)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        dialogMainBinding.tvClose.setOnClickListener {
            dialog.dismiss()
        }

        RLfriendsApiCall(dialogMainBinding,dialog)
        //dialogMainBinding.layYou.txtHeader.setText(getString(R.string.you)+":")
        dialog.show()

    }
    private fun RLfriendsApiCall(dialogMainBinding: RlDialogFriendChallengesBinding,dialog: Dialog) {
        val request = listOf(RLSetsearch_userrequest(search_user = RLSetsearch_user(get_friends = currentUser,limit = 100, index=0)))
        Log.d(TAG,"setyouFollowdata= "+request)

        viewModel.RLfriendsYouFollow(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        Log.d(TAG,"Success= "+response.type)
                        RLresponsehandlefriendsApi(response.text.user,dialogMainBinding,dialog)
                    }else {
                        Log.d(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){ e.printStackTrace()
                    Log.d(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->
                RLcommonToast(RLConstants.SERVER_PROBLEM)
                Log.d(TAG,"Error= "+error.message)
            }
        }
    }
    private fun RLresponsehandlefriendsApi(userdata: List<RLuserData>,dialogMainBinding: RlDialogFriendChallengesBinding,dialog: Dialog) {
        var selectUserdata: List<RLuserData> = mutableListOf()

        val linearLayoutManager = LinearLayoutManager(activity)
        dialogMainBinding.recyclerFriend.layoutManager = linearLayoutManager

        val adapter = RLChallengeForFriendListAdapter(activity, userdata) { cardData ->
            // Handle selection
            if (cardData.isSelected){
                selectUserdata += listOf(cardData)
            }else{
                selectUserdata -= listOf(cardData)
            }

        }
        dialogMainBinding.recyclerFriend.adapter = adapter
        dialogMainBinding.edtFriendSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.RLfilter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        dialogMainBinding.tvSelect.setOnClickListener {
           if (selectUserdata.size>0){
               RLNextFragmentOpen(false)
                dialog.dismiss()
           }else{
              RLshowAlertDialog("Please Select friends")
               dialog.dismiss()
           }

        }

    }
    private fun RLNextFragmentOpen(isGroup:Boolean){
        val bundle: Bundle = Bundle()
        bundle.putString("ChallengeType",challengeType )
        bundle.putString("CalenderType",calenderType )
        bundle.putBoolean("IsGroup",isGroup )
        (context as RLMainActivityRL).RLhidebottombarcolorwhite()
        (context as RLMainActivityRL).RLloadFrag(RLFragChallengesForType().newInstance(bundle), TAG, true,null, false)

    }
    private fun RLshowAlertDialog(message:String) {
        val sucDialog: Dialog = Dialog(requireContext())
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog.setContentView(R.layout.rl_alertdialog_custom_layout)
        sucDialog.setCancelable(false)
        val iv_ok: TextView = sucDialog.findViewById(R.id.iv_ok)
        val iv_title: TextView = sucDialog.findViewById(R.id.iv_title)
        val iv_description: TextView = sucDialog.findViewById(R.id.iv_description)
        val view_v: View = sucDialog.findViewById(R.id.view_v)

        iv_title.visibility=View.GONE
        view_v.visibility=View.VISIBLE
        iv_description.setText(message)
        iv_ok.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })
        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
    }

}