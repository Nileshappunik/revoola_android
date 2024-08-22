package com.example.myfirstapp.fragment.feed

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.api.RLApiClientRet
import com.example.myfirstapp.databinding.RlFragFeedCardLikeCommentViewBinding
import com.example.myfirstapp.databinding.RlLayoutFeedListBinding
import com.example.myfirstapp.fragment.feed.adapter.RLFeedCommentListAdapter
import com.example.myfirstapp.model.RLTextOverview
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTools
import com.example.myfirstapp.viewmodel.RLMainRepository
import com.example.myfirstapp.viewmodel.RLMainViewModel
import com.example.myfirstapp.viewmodel.RLMainViewModelFactory
import kotlin.math.roundToInt

class RLFragFeedCardLikeCommentView : RLBaseFragment(){
    val TAG: String = RLFragFeedCardLikeCommentView::class.java.simpleName
    lateinit var fragBinding: RlFragFeedCardLikeCommentViewBinding
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""
    var classType:String=""
    lateinit var cardData: RLTextOverview
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragFeedCardLikeCommentView()
        fragment.arguments = bundle
        return fragment
    }
    private val binding by lazy {
        RlFragFeedCardLikeCommentViewBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_feed_card_like_comment_view, container) as RlFragFeedCardLikeCommentViewBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragFeedCardLikeCommentView" )
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
        fragBinding.ivBack.setOnClickListener {
            RLcloseFragment()
        }
        fragBinding.ivBackThumb.setOnClickListener {
            RLcloseFragment()
        }
        fragBinding.inlayMain.viewBottom.visibility=View.GONE
        fragBinding.inlayMainThumb.viewBottom.visibility=View.GONE
        cardData = requireArguments().getSerializable(RLConstants.CardData) as RLTextOverview
        val  clickType = requireArguments().getString(RLConstants.TYPE)
        if (clickType.equals("Comment")){
            fragBinding.relativeComment.visibility=View.VISIBLE
            fragBinding.relativeThumb.visibility=View.GONE
            RLTools.RLheightsetimageview(fragBinding.inlayMain.imgMain)
            RLCommentThumbUiSet(cardData,fragBinding.inlayMain)
        }else if (clickType.equals("Thumb")){
            fragBinding.relativeComment.visibility=View.GONE
            fragBinding.relativeThumb.visibility=View.VISIBLE
            RLTools.RLheightsetimageview(fragBinding.inlayMainThumb.imgMain)
            RLCommentThumbUiSet(cardData,fragBinding.inlayMainThumb)
        }
        fragBinding.btnSend.setOnClickListener {
            //Do Something
        }
        val dataList = listOf<String>()
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.rvCommentList.layoutManager = linearLayoutManager
        val adapter = RLFeedCommentListAdapter(dataList,activity)
        fragBinding.rvCommentList.adapter = adapter
    }
    private fun RLCommentThumbUiSet(cardData: RLTextOverview, inlayMain: RlLayoutFeedListBinding) {
        RLcommonDataSet(cardData, inlayMain)
        if (cardData.from_third_party_source == 0) {
            if (cardData.bmo == 0) {
                //BODY
                RLbodyClassesBodySet(cardData, inlayMain)
            } else if (cardData.bmo == 1) {
                //MIND
                RLmindClassBodySet(cardData, inlayMain)
            } else if (cardData.bmo == 2) {
                //OTHER
                RLotherClassesBodySet(cardData, inlayMain)
            }
        }
        else if (cardData.from_third_party_source == 1) {
            RLthirdPartyOneBodySet(cardData, inlayMain)
        } else if (cardData.from_third_party_source == 2) {
            RLthirdPartyTwoBodySet(cardData, inlayMain)
        } else if (cardData.from_third_party_source > 10) {
            RLthirdPartyTenBodySet(cardData, inlayMain)
        }
    }
    private fun RLcommonDataSet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding){
        if (cardData.classType.isNullOrEmpty()){
            classType=""
        }else{
            classType = cardData.classType!!
        }
        RLTools.RLheightsetimageview(layoutBinding.imgMain)
        layoutBinding.layTime.viewCommon.visibility = View.GONE
        layoutBinding.layCalories.viewCommon.visibility = View.GONE
        layoutBinding.layAssumedeffort.viewCommon.visibility = View.GONE
        layoutBinding.laySteps.viewCommon.visibility = View.GONE
        layoutBinding.layBottom.visibility = View.GONE

        layoutBinding.txtUsername.setText(cardData.username.toString())
        layoutBinding.txtMyride.setText(cardData.className.toString())

        layoutBinding.imgMyride.setImageResource(RLTools.RLgeticon(classType))
        layoutBinding.txtUserdatetime.setText(RLTools.RLconvertTimestampToDateTime(cardData.timestamp.toLong()))

        if (currentUser.equals(cardData.userid)){
            layoutBinding.imgThreedot.visibility=View.VISIBLE
        }else{
            layoutBinding.imgThreedot.visibility=View.GONE
        }

        Glide.with(requireContext()).load(cardData.avatar)
            .placeholder(R.drawable.sample_user)
            .error(R.drawable.sample_user)
            .into(layoutBinding.imgUser)

        if (!cardData.imageLinkSmall.isNullOrEmpty()){
            Glide.with(requireContext()).load(cardData.imageLinkSmall).into(layoutBinding.imgMain)
        }else if (!cardData.map_image.isNullOrEmpty()){
            Glide.with(requireContext()).load(cardData.map_image).into(layoutBinding.imgMain)
        }else{
            Glide.with(requireContext()).load(RLTools.RLgetImage(classType)).into(layoutBinding.imgMain)
        }
    }
    private fun RLthirdPartyTenBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding){
        layoutBinding.layTime.imgTime.setImageResource(R.drawable.ic_calendar_today)
        layoutBinding.layTime.txtTime.setText(R.string.challengesfor)
        layoutBinding.layTime.txtTimeNumber.setText(RLTools.RLdaytimeget(cardData.duration.toInt()))
        layoutBinding.layTime.relativeCard.visibility=View.GONE

        if (classType.toLowerCase().equals("challenge-effort")){
            layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layCalories.txtTime.setText(R.string.targeteffort)
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.goal.toDouble()).toString())

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_heart)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.youachived)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.steps.toDouble()).toString())

        }else if (classType.toLowerCase().equals("challenge-steps")){

            layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layCalories.txtTime.setText(R.string.targetsteps)
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.goal.toDouble()).toString())

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_steps_green)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.youachived)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.steps.toDouble()).toString())

        }else if (classType!!.toLowerCase().equals("challenge-calories")){
            layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layCalories.txtTime.setText(R.string.targetcalories)
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.goal.toDouble()).toString())

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_calories_green)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.youachived)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.burntCalories.toDouble()))

        }else if (classType!!.toLowerCase().equals("challenge-distance")){
            layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layCalories.txtTime.setText(R.string.targetdistance)
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.goal.toDouble()).toString())

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_distance)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.distance)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.distance.toDouble()))

        }else if (classType!!.toLowerCase().equals("challenge-climbed")){
            layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layCalories.txtTime.setText(R.string.targetclimbed)
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.goal.toDouble()).toString())

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_climb)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.distance)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.distance.toDouble()))

        }else if (classType!!.toLowerCase().equals("challenge-duration")){
            layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layCalories.txtTime.setText(R.string.targettotalduration)
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.goal.toDouble()).toString())

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_active_time_green)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.youachived)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.steps.toDouble()).toString())

        }

        layoutBinding.laySteps.imgTime.setImageResource(R.drawable.ic_ranking)
        layoutBinding.laySteps.relativeCard.visibility=View.VISIBLE
        layoutBinding.layAssumedeffort.relativeCard.visibility=View.VISIBLE

        layoutBinding.laySteps.txtTime.setText(R.string.rank)
        layoutBinding.laySteps.txtTimeNumber.setText(cardData.hrm.toString()+ " of " +cardData.share_map.toString())

    }
    private fun RLthirdPartyTwoBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding){
        layoutBinding.layTime.imgTime.setImageResource(R.drawable.fd_steps_green)
        layoutBinding.layTime.txtTime.setText(R.string.step)
        layoutBinding.layTime.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.steps.toDouble()))
        layoutBinding.layTime.relativeCard.visibility=View.VISIBLE

        layoutBinding.layCalories.imgTime.setImageResource(R.drawable.fd_calories_green)
        layoutBinding.layCalories.txtTime.setText(R.string.calorie)
        layoutBinding.layCalories.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.burntCalories.toDouble()))

        layoutBinding.laySteps.imgTime.setImageResource(R.drawable.ic_distance)
        layoutBinding.laySteps.txtTime.setText(R.string.distancemiles)
        layoutBinding.laySteps.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.distance.toDouble()))

        layoutBinding.laySteps.relativeCard.visibility=View.VISIBLE
        layoutBinding.layAssumedeffort.relativeCard.visibility=View.GONE

        layoutBinding.imgThreedot.visibility=View.GONE

        if (!cardData.imageLinkSmall.isNullOrEmpty()){
            Glide.with(requireContext()).load(cardData.imageLinkSmall).into(layoutBinding.imgMain)
        }else if (!cardData.map_image.isNullOrEmpty()){
            Glide.with(requireContext()).load(cardData.map_image).into(layoutBinding.imgMain)
        }else{
            Glide.with(requireContext()).load(R.drawable.healthheart).into(layoutBinding.imgMain)
        }

    }
    private fun RLthirdPartyOneBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding){
        layoutBinding.layTime.imgTime.setImageResource(R.drawable.fd_active_time_green)
        layoutBinding.layTime.txtTime.setText(R.string.time)
        layoutBinding.layTime.txtTimeNumber.setText(RLTools.RLdaytimeget(cardData.totalTime.toInt()))
        layoutBinding.layTime.relativeCard.visibility=View.VISIBLE

        layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_heart)
        layoutBinding.layCalories.txtTime.setText(R.string.revoolaeffort)
        if (cardData.totalREV.roundToInt()>0){
            layoutBinding.layCalories.txtTimeNumber.setText(cardData.totalREV.roundToInt().toString())
        }else{
            layoutBinding.layCalories.txtTimeNumber.setText("0")
        }

        if (classType!!.toLowerCase().equals("ride")||classType!!.toLowerCase().equals("run")||classType!!.toLowerCase().equals("walk")){
            layoutBinding.laySteps.imgTime.setImageResource(R.drawable.ic_distance)
            layoutBinding.laySteps.txtTime.setText(R.string.distance)
            layoutBinding.laySteps.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.distance).toString())

        }else{
            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_calories_green)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.activecalories)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.burntCalories.toDouble()))

        }


        if (!cardData.imageLinkSmall.isNullOrEmpty()){
            Glide.with(requireContext()).load(cardData.imageLinkSmall).into(layoutBinding.imgMain)
        }else if (!cardData.map_image.isNullOrEmpty()){
            Glide.with(requireContext()).load(cardData.map_image).into(layoutBinding.imgMain)
        }else{
            Glide.with(requireContext()).load(RLConstants.img_feed_apple_fitness).into(layoutBinding.imgMain)
        }

        layoutBinding.laySteps.relativeCard.visibility=View.GONE
        layoutBinding.layAssumedeffort.relativeCard.visibility=View.VISIBLE
    }
    private fun RLotherClassesBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding){
        layoutBinding.layTime.imgTime.setImageResource(R.drawable.fd_active_time_green)
        layoutBinding.layTime.txtTime.setText(R.string.time)
        layoutBinding.layTime.txtTimeNumber.setText(RLTools.RLdaytimeget(cardData.totalTime.toInt()))
        layoutBinding.layTime.relativeCard.visibility=View.VISIBLE

        layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_heart)
        layoutBinding.layCalories.txtTime.setText(R.string.effort)
        if (cardData.totalREV.roundToInt()>0){
            layoutBinding.layCalories.txtTimeNumber.setText(cardData.totalREV.roundToInt().toString())
        }else{
            layoutBinding.layCalories.txtTimeNumber.setText("0")
        }

        if (classType.toLowerCase().equals("ride")||classType!!.toLowerCase().equals("run")||classType!!.toLowerCase().equals("walk")){
            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_distance)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.distancemiles)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.distance.toDouble()))

        }else{
            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_calories_green)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.calorie)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.burntCalories.toDouble()))

        }

        layoutBinding.laySteps.relativeCard.visibility=View.GONE
        layoutBinding.layAssumedeffort.relativeCard.visibility=View.VISIBLE
    }
    private fun RLbodyClassesBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding){
        layoutBinding.layTime.imgTime.setImageResource(R.drawable.fd_active_time_green)
        layoutBinding.layTime.txtTime.setText(R.string.time)
        layoutBinding.layTime.txtTimeNumber.setText(RLTools.RLdaytimeget(cardData.totalTime.toInt()).toString())
        layoutBinding.layTime.relativeCard.visibility=View.VISIBLE

        layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_heart)
        layoutBinding.layCalories.txtTime.setText(R.string.effort)
        if (cardData.totalREV.roundToInt()>0){
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.totalREV.toDouble()).toString())
        }else{
            layoutBinding.layCalories.txtTimeNumber.setText("0")
        }

        layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_calories_green)
        layoutBinding.layAssumedeffort.txtTime.setText(R.string.calorie)
        layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.burntCalories.toDouble()).toString())

        layoutBinding.laySteps.relativeCard.visibility=View.GONE
        layoutBinding.layAssumedeffort.relativeCard.visibility=View.VISIBLE

    }
    private fun RLmindClassBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding) {

        layoutBinding.layTime.imgTime.setImageResource(R.drawable.ic_mind_read)
        layoutBinding.layTime.txtTime.setText(R.string.mindfulminutes)
        layoutBinding.layTime.txtTimeNumber.setText(RLTools.RLdaytimeget(cardData.totalTime.toInt()))
        layoutBinding.layTime.relativeCard.visibility=View.VISIBLE

        layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_mind_read)
        layoutBinding.layCalories.txtTime.setText(R.string.relaxation)
        layoutBinding.layCalories.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.totalRMS.toDouble()).toString())

        layoutBinding.layAssumedeffort.relativeCard.visibility=View.GONE
        layoutBinding.laySteps.relativeCard.visibility=View.GONE

    }

}