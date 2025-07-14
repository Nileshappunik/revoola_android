package com.revoola.fragment.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.api.RLApiClientRet
import com.revoola.databinding.RlFragFeedCardLikeCommentViewBinding
import com.revoola.databinding.RlLayoutFeedListBinding
import com.revoola.fragment.feed.adapter.RLFeedCommentListAdapter
import com.revoola.model.RLTextOverview
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import kotlin.math.roundToInt

class RLFragFeedCardLikeCommentView : RLBaseFragment(){
    val TAG: String = RLFragFeedCardLikeCommentView::class.java.simpleName
   // lateinit var fragBinding: RlFragFeedCardLikeCommentViewBinding
    lateinit var apiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""
    var classType:String=""

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragFeedCardLikeCommentView()
        fragment.arguments = bundle
        return fragment
    }
    private val fragBinding by lazy {
        RlFragFeedCardLikeCommentViewBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        // fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_feed_card_like_comment_view, container) as RlFragFeedCardLikeCommentViewBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragFeedCardLikeCommentView" )
        currentUser= RLPrefManager.rl_getSomeStringValue(activity, RLPrefManager.current_user, "")
        // Api call
        apiClientRetrofit = RLApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)

        rl_uisetup()
        return fragBinding.root
    }

    private fun rl_uisetup() {
        fragBinding.ivBack.setOnClickListener {
            rl_closeFragment()
        }
        fragBinding.ivBackThumb.setOnClickListener {
            rl_closeFragment()
        }
        fragBinding.inlayMain.viewBottom.visibility=View.GONE
        fragBinding.inlayMainThumb.viewBottom.visibility=View.GONE

        val cardData = requireArguments().getSerializable(RLConstants.CardData) as RLTextOverview
        rl_dataSet(cardData)

        val dataList = listOf<String>()
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.rvCommentList.layoutManager = linearLayoutManager
        val adapter = RLFeedCommentListAdapter(dataList,activity)
        fragBinding.rvCommentList.adapter = adapter

    }
    private fun rl_commentThumbUiSet(cardData: RLTextOverview, inlayMain: RlLayoutFeedListBinding) {
        rl_commonDataSet(cardData, inlayMain)
        if (cardData.from_third_party_source == 0) {
            if (cardData.bmo == 0) {
                //BODY
                rl_bodyClassesBodySet(cardData, inlayMain)
            } else if (cardData.bmo == 1) {
                //MIND
                rl_mindClassBodySet(cardData, inlayMain)
            } else if (cardData.bmo == 2) {
                //OTHER
                rl_otherClassesBodySet(cardData, inlayMain)
            }
        }
        else if (cardData.from_third_party_source == 1) {
            rl_thirdPartyOneBodySet(cardData, inlayMain)
        } else if (cardData.from_third_party_source == 2) {
            rl_thirdPartyTwoBodySet(cardData, inlayMain)
        } else if (cardData.from_third_party_source > 10) {
            rl_thirdPartyTenBodySet(cardData, inlayMain)
        }
    }
    private fun rl_commonDataSet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding){
        if (cardData.classType.isNullOrEmpty()){
            classType=""
        }else{
            classType = cardData.classType!!
        }
        RLTools.rl_heightsetimageview(layoutBinding.imgMain)
        layoutBinding.layTime.viewCommon.visibility = View.GONE
        layoutBinding.layCalories.viewCommon.visibility = View.GONE
        layoutBinding.layAssumedeffort.viewCommon.visibility = View.GONE
        layoutBinding.laySteps.viewCommon.visibility = View.GONE
        layoutBinding.layBottom.visibility = View.GONE

        layoutBinding.txtUsername.setText(cardData.username.toString())
        layoutBinding.txtMyride.setText(cardData.className.toString())

        layoutBinding.imgMyride.setImageResource(RLTools.rl_geticon(classType))
        layoutBinding.txtUserdatetime.setText(RLTools.rl_convertTimestampToDateTime(cardData.timestamp.toLong()))

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
            Glide.with(requireContext()).load(RLTools.rl_getImage(classType)).into(layoutBinding.imgMain)
        }
    }
    private fun rl_thirdPartyTenBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding){
        layoutBinding.layTime.imgTime.setImageResource(R.drawable.ic_calendar_today)
        layoutBinding.layTime.txtTime.setText(R.string.challengesfor)
        if (cardData.duration.isNullOrEmpty()){
            layoutBinding.layTime.txtTimeNumber.setText(RLTools.rl_daytimeget(0))
        }else{
            layoutBinding.layTime.txtTimeNumber.setText(RLTools.rl_daytimeget((cardData.duration?:"0").toInt()?:0))
        }

        layoutBinding.layTime.relativeCard.visibility=View.GONE

        if (classType.toLowerCase().equals("challenge-effort")){
            layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layCalories.txtTime.setText(R.string.targeteffort)
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.goal.toDouble()?:0.0).toString())

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_heart)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.youachived)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.steps.toDouble()?:0.0).toString())

        }else if (classType.toLowerCase().equals("challenge-steps")){

            layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layCalories.txtTime.setText(R.string.targetsteps)
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.goal.toDouble()?:0.0).toString())

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_steps_green)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.youachived)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.steps.toDouble()?:0.0).toString())

        }else if (classType!!.toLowerCase().equals("challenge-calories")){
            layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layCalories.txtTime.setText(R.string.targetcalories)
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.goal.toDouble()?:0.0).toString())

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_calories_green)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.youachived)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.burntCalories.toDouble()?:0.0))

        }else if (classType!!.toLowerCase().equals("challenge-distance")){
            layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layCalories.txtTime.setText(R.string.targetdistance)
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.goal.toDouble()?:0.0).toString())

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_distance)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.distance)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.distance.toDouble()))

        }else if (classType!!.toLowerCase().equals("challenge-climbed")){
            layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layCalories.txtTime.setText(R.string.targetclimbed)
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.goal.toDouble()?:0.0).toString())

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_climb)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.distance)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.distance.toDouble()?:0.0))

        }else if (classType!!.toLowerCase().equals("challenge-duration")){
            layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layCalories.txtTime.setText(R.string.targettotalduration)
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.goal.toDouble()?:0.0).toString())

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_active_time_green)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.youachived)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.steps.toDouble()?:0.0).toString())

        }

        layoutBinding.laySteps.imgTime.setImageResource(R.drawable.ic_ranking)
        layoutBinding.laySteps.relativeCard.visibility=View.VISIBLE
        layoutBinding.layAssumedeffort.relativeCard.visibility=View.VISIBLE

        layoutBinding.laySteps.txtTime.setText(R.string.rank)
        layoutBinding.laySteps.txtTimeNumber.setText(cardData.hrm.toString()+ " of " +cardData.share_map.toString())

    }
    private fun rl_thirdPartyTwoBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding){
        layoutBinding.layTime.imgTime.setImageResource(R.drawable.fd_steps_green)
        layoutBinding.layTime.txtTime.setText(R.string.step)
        layoutBinding.layTime.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.steps.toDouble()))
        layoutBinding.layTime.relativeCard.visibility=View.VISIBLE

        layoutBinding.layCalories.imgTime.setImageResource(R.drawable.fd_calories_green)
        layoutBinding.layCalories.txtTime.setText(R.string.calorie)
        layoutBinding.layCalories.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.burntCalories.toDouble()))

        layoutBinding.laySteps.imgTime.setImageResource(R.drawable.ic_distance)
        layoutBinding.laySteps.txtTime.setText(R.string.distancemiles)
        layoutBinding.laySteps.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.distance.toDouble()))

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
    private fun rl_thirdPartyOneBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding){
        layoutBinding.layTime.imgTime.setImageResource(R.drawable.fd_active_time_green)
        layoutBinding.layTime.txtTime.setText(R.string.time)
        layoutBinding.layTime.txtTimeNumber.setText(RLTools.rl_daytimeget(cardData.totalTime.toInt()))
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
            layoutBinding.laySteps.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.distance).toString())

        }else{
            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_calories_green)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.activecalories)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.burntCalories.toDouble()))

        }

        if (!cardData.imageLinkSmall.isNullOrEmpty()){
            Glide.with(requireContext()).load(cardData.imageLinkSmall).into(layoutBinding.imgMain)
        }else if (!cardData.map_image.isNullOrEmpty()){
            Glide.with(requireContext()).load(cardData.map_image).into(layoutBinding.imgMain)
        }else{
            Glide.with(requireContext()).load(RLConstants.Img_Feed_Apple_Fitness).into(layoutBinding.imgMain)
        }

        layoutBinding.laySteps.relativeCard.visibility=View.GONE
        layoutBinding.layAssumedeffort.relativeCard.visibility=View.VISIBLE
    }
    private fun rl_otherClassesBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding){
        layoutBinding.layTime.imgTime.setImageResource(R.drawable.fd_active_time_green)
        layoutBinding.layTime.txtTime.setText(R.string.time)
        layoutBinding.layTime.txtTimeNumber.setText(RLTools.rl_daytimeget(cardData.totalTime.toInt()))
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
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.distance.toDouble()))

        }else{
            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_calories_green)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.calorie)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.burntCalories.toDouble()))

        }

        layoutBinding.laySteps.relativeCard.visibility=View.GONE
        layoutBinding.layAssumedeffort.relativeCard.visibility=View.VISIBLE
    }
    private fun rl_bodyClassesBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding){
        layoutBinding.layTime.imgTime.setImageResource(R.drawable.fd_active_time_green)
        layoutBinding.layTime.txtTime.setText(R.string.time)
        layoutBinding.layTime.txtTimeNumber.setText(RLTools.rl_daytimeget(cardData.totalTime.toInt()).toString())
        layoutBinding.layTime.relativeCard.visibility=View.VISIBLE

        layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_heart)
        layoutBinding.layCalories.txtTime.setText(R.string.effort)
        if (cardData.totalREV.roundToInt()>0){
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.totalREV.toDouble()).toString())
        }else{
            layoutBinding.layCalories.txtTimeNumber.setText("0")
        }

        layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_calories_green)
        layoutBinding.layAssumedeffort.txtTime.setText(R.string.calorie)
        layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.burntCalories.toDouble()).toString())

        layoutBinding.laySteps.relativeCard.visibility=View.GONE
        layoutBinding.layAssumedeffort.relativeCard.visibility=View.VISIBLE

    }
    private fun rl_mindClassBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding) {

        layoutBinding.layTime.imgTime.setImageResource(R.drawable.ic_mind_read)
        layoutBinding.layTime.txtTime.setText(R.string.mindfulminutes)
        layoutBinding.layTime.txtTimeNumber.setText(RLTools.rl_daytimeget(cardData.totalTime.toInt()))
        layoutBinding.layTime.relativeCard.visibility=View.VISIBLE

        layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_mind_read)
        layoutBinding.layCalories.txtTime.setText(R.string.relaxation)
        layoutBinding.layCalories.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.totalRMS.toDouble()).toString())

        layoutBinding.layAssumedeffort.relativeCard.visibility=View.GONE
        layoutBinding.laySteps.relativeCard.visibility=View.GONE

    }

    private fun rl_apiCall(overviewid:String) {
        viewModel.rl_getCommentsData("commentsNew",overviewid,10,0) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.rl_logDPrint(TAG,"Success= "+response.type)
                        if (response.text.size>0){
                            rl_dataSet(response.text[0])
                        }
                    }else {
                        RLTools.rl_logDPrint(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->
                RLTools.rl_logDPrint(TAG,"Error= "+error.message)
            }
        }
    }

    private fun rl_dataSet(cardData:RLTextOverview){
        val  clickType = requireArguments().getString(RLConstants.TYPE)
        if (clickType.equals("Comment")){
            fragBinding.relativeComment.visibility=View.VISIBLE
            fragBinding.relativeThumb.visibility=View.GONE
            RLTools.rl_heightsetimageview(fragBinding.inlayMain.imgMain)
            rl_commentThumbUiSet(cardData,fragBinding.inlayMain)
        }else if (clickType.equals("Thumb")){
            fragBinding.relativeComment.visibility=View.GONE
            fragBinding.relativeThumb.visibility=View.VISIBLE
            RLTools.rl_heightsetimageview(fragBinding.inlayMainThumb.imgMain)
            rl_commentThumbUiSet(cardData,fragBinding.inlayMainThumb)
        }
        fragBinding.btnSend.setOnClickListener {
            //Do Something
        }
    }

}