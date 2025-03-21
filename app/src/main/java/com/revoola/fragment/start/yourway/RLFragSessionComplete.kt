package com.revoola.fragment.start.yourway

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databasefirebase.RLDatabaseManagerWrite
import com.revoola.databinding.RlFragSessionCompleteBinding
import com.revoola.fragment.overview.RLFragOverviewSession
import com.revoola.fragment.start.adapter.RLSelectedImagesAdapter
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.revoola.RLBaseProgress
import com.revoola.api.RLApiClientRet
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databasefirebase.RevoolaFirebasePath
import com.revoola.databasefirebase.RevoolaKeys
import com.revoola.fragment.feed.RLFragSessionSummary
import com.revoola.model.RLClassLeaderboard
import com.revoola.model.RLGetElevationResponseModel
import com.revoola.model.RLInsightlyMoEngageResponse
import com.revoola.model.RLInsightlyMoengageApiPayload
import com.revoola.model.RLTextOverview
import com.revoola.model.RLYourWayApiPayload
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import gun0912.tedimagepicker.builder.TedImagePicker
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.Serializable
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
//import java.util.Base64
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class RLFragSessionComplete : RLBaseFragment(){
    val TAG: String = RLFragSessionComplete::class.java.simpleName
    private lateinit var fragBinding: RlFragSessionCompleteBinding
    private lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var imgUriList = mutableListOf<Uri>()
    var currentUser =""
    private var displayImage =""
    private var displayName =""
    private var visibilityflagforthatsession:Int =0
    private var shareMap:Int =1
    private val REQUEST_CODE_CHOOSE_IMAGE = 500
    private var server1Url="http://demsworld.revoola.com:10000/api/v1/lookup"
    private var server2Url="http://demsworld.revoola.com:10000/api/v1/lookup"
    private var mapBitmapImage: Bitmap? = null
    private var mapImageUri: Uri? = null
    private lateinit var cardData: RLSessionDataTransferModelNew
    private val httpClient by lazy { OkHttpClient() }


    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragSessionComplete()
        fragment.arguments = bundle
        return fragment
    }
    private val binding by lazy {
        RlFragSessionCompleteBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the Parcelable object from the Bundle
        arguments?.let {
            cardData = it.getParcelable("cardData")!! // Use !! only if you're sure it's not null
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_session_complete, container) as RlFragSessionCompleteBinding
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSessionComplete" )
        currentUser=  RLPrefManager.RLGetSomeStringValue(activity, RLPrefManager.current_user, "")
        if (currentUser.isEmpty()){
            currentUser=RLAuthManager().RlgetCurrentUser()?.uid?:""
        }
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing or show a message
            }
        })
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLfetchServerUrl()
        fragBinding.edtSessionName.setText("${cardData.yourWayType} Session")
        fragBinding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                shareMap = 1
            }else{
                shareMap = 0
            }
        }
        visibilityflagforthatsession=cardData.visibilityflagforthatsession
        when(visibilityflagforthatsession){
            0->{//EveryOne
                fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyEveryOneBGColor))
                fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyeveryone)
                fragBinding.tvShareTitle.setText(R.string.everyone)
                fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyEveryOneColor))
            }
            1->{//Private
                fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyPrivateBGColor))
                fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyprivate)
                fragBinding.tvShareTitle.setText(R.string.privatetx)
                fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyPrivateColor))
            }
            2->{//Friends
                fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyFriendsBGColor))
                fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyfriends)
                fragBinding.tvShareTitle.setText(R.string.friendstx)
                fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyFriendsColor))
            }
        }

        fragBinding.layPrivacy.setOnClickListener {
            val titleTxt:String=fragBinding.tvShareTitle.text.toString().toUpperCase()
            when(titleTxt){
                "FRIENDS"->{
                    visibilityflagforthatsession=0
                    fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyPrivateBGColor))
                    fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyprivate)
                    fragBinding.tvShareTitle.setText(R.string.privatetx)
                    fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyPrivateColor))
                    RLShareMapHide(false)
                }
                "EVERYONE"->{
                    visibilityflagforthatsession=2
                    fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyFriendsBGColor))
                    fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyfriends)
                    fragBinding.tvShareTitle.setText(R.string.friendstx)
                    fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyFriendsColor))
                    RLShareMapHide(true)
                }
                "PRIVATE"->{
                    visibilityflagforthatsession=1
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
            if(isAdded){
                RLBaseProgress.RLShowProgressDialog(requireActivity())
            }
            val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()
            RLMakeSensorData(cardData,currentTimestamp)
        }
        val yourWayType = cardData.yourWayType.toLowerCase()
        if (yourWayType.equals("walk")||yourWayType.equals("run")||yourWayType.equals("ride")){
            //Generate DemsElevation , generatedDistance , generatedElevation
            RLConvertLatLongOBj(cardData)
            //Generate MAP URL
            RLConvertToMapUrl(cardData)
        }

    }

    //Firebase To Fetch Server Data
    private fun RLfetchServerUrl() {
        // Firebase to fetch user data
        val path = RevoolaFirebasePath.worldUrlGetDataPath()
        RLDatabaseManagerRead().RlreadData(path) { data, error ->
            if (data != null) {
                val jason = Gson().toJson(data)
                val type = object : TypeToken<Map<String, String>>() {}.type
                val responseMap: Map<String, String> = Gson().fromJson(jason, type)
                 server1Url = responseMap["server1"] ?: "http://demsworld.revoola.com:10000/api/v1/lookup"
                 server2Url = responseMap["server2"] ?: "http://demsworld.revoola.com:10000/api/v1/lookup"
            }
        }
    }

    //Generate DemsElevation , generatedDistance , generatedElevation
    private fun RLConvertLatLongOBj(cardData:RLSessionDataTransferModelNew){
        val latLongList = mutableListOf<Map<String, Double>>()
        cardData.arrDataLocation.forEach {locationData->
            latLongList.add(mapOf("latitude" to locationData.latitude, "longitude" to locationData.longitude))
        }
        val objOfLatLong = JSONObject().apply {
            put("locations", JSONArray(latLongList))
        }
        lifecycleScope.launch {
            RLGetElevationServer1ApiCall(objOfLatLong,cardData)
        }
    }
    private suspend fun RLGetElevationServer1ApiCall(objOfLatLong: JSONObject, cardData: RLSessionDataTransferModelNew) {
        withContext(Dispatchers.IO) {
            try {
                RLTools.RlLogDPrint(TAG, "getElevation Request: $objOfLatLong")

                val requestBody = objOfLatLong.toString()
                    .toRequestBody("application/json".toMediaTypeOrNull())

                val request = Request.Builder()
                    .url(server1Url)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()
                    if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
                        RLTools.RlLogEPrint(TAG, "getElevation API Failed: HTTP ${response.code}, ${response.message}")
                        RLGetElevationServer2ApiCall(objOfLatLong, cardData)
                        return@use
                    }
                    RLTools.RlLogDPrint(TAG, "getElevation Response: $responseBody")
                    val apiResponse = Gson().fromJson(responseBody, RLGetElevationResponseModel::class.java)
                    withContext(Dispatchers.Main) {
                        if (apiResponse.results.isNotEmpty()) {
                            RLTools.RlLogDPrint(TAG, "getElevation Success: $apiResponse")
                            RLHandleElevationResponse(apiResponse, cardData, 1)
                        } else {
                            RLTools.RlLogEPrint(TAG, "getElevation Error: Empty Response")
                            RLGetElevationServer2ApiCall(objOfLatLong, cardData)
                        }
                    }
                }
            } catch (e: Exception) {
                RLGetElevationServer2ApiCall(objOfLatLong, cardData)
                RLTools.RlLogEPrint(TAG, "getElevation Exception: ${e.localizedMessage}")
            }
        }
    }
    private suspend fun RLGetElevationServer2ApiCall(objOfLatLong: JSONObject, cardData: RLSessionDataTransferModelNew) {
        withContext(Dispatchers.IO) {
            try {
                RLTools.RlLogDPrint(TAG, "getElevation Request: $objOfLatLong")
                val requestBody = objOfLatLong.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url(server2Url)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()
                    if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
                        RLTools.RlLogEPrint(TAG, "getElevation API Failed: HTTP ${response.code}, ${response.message}")
                        return@use
                    }

                    RLTools.RlLogDPrint(TAG, "getElevation Response: $responseBody")

                    val apiResponse = Gson().fromJson(responseBody, RLGetElevationResponseModel::class.java)

                    withContext(Dispatchers.Main) {
                        if (apiResponse.results.isNotEmpty()) {
                            RLHandleElevationResponse(apiResponse, cardData, 2)
                            RLTools.RlLogDPrint(TAG, "getElevation Success: $apiResponse")
                        } else {
                            RLTools.RlLogEPrint(TAG, "getElevation Error: Empty Response")
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    RLTools.RlLogEPrint(TAG, "getElevation Exception: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun RLHandleElevationResponse(apiResponse: RLGetElevationResponseModel, cardData:RLSessionDataTransferModelNew, server:Int) {
        val latitudeArray = mutableListOf<Double>()
        val longitudeArray = mutableListOf<Double>()
        val elevationArray = mutableListOf<Double>()
        val wKey  = (System.currentTimeMillis() / 1000)
        val newDate = wKey - cardData.totalTime.toLong()
        var gpxString = ""
        apiResponse.results.forEach { resultData->
            val elevation=resultData.elevation
            val latitude=resultData.latitude
            val longitude=resultData.longitude
            val timestamp = Date(newDate * 1000L)
            latitudeArray.add(latitude)
            longitudeArray.add(longitude)
            elevationArray.add(elevation.toDouble())
            val isoTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            gpxString += """
                    <trkpt lat="$latitude" lon="$longitude">
                        <ele>$elevation</ele>
                        <time>${isoTime.format(timestamp)}</time>
                    </trkpt>
                """.trimIndent()
        }

        cardData.gpxTServerString=gpxString
        RLGenerateDataForElevationAndGPX(cardData,wKey,elevationArray,latitudeArray,longitudeArray)
    }
    private fun RLCreateNormalisedElevation(elevationArray: MutableList<Double>,latitudeArray: MutableList<Double>,longitudeArray: MutableList<Double>): Map<String, List<Double>>{
        val elevationList = mutableListOf<Double>()
        val latitudeList = mutableListOf<Double>()
        val longitudeList = mutableListOf<Double>()

        var normised17 = 0.0
        var normised17Lat = 0.0
        var normised17Long = 0.0
        var lastVal = 0.0
        var lastValLat = 0.0
        var lastValLong = 0.0

        if (elevationArray.size > 16) {
            elevationArray.forEachIndexed { index, value ->
                if (index > 16) {
                    val normised17Value = normised17 - lastVal + value
                    normised17 = normised17Value
                    lastVal = elevationArray[index - 16]
                    elevationList.add(normised17Value / 17)

                    val normised17LatValue = normised17Lat - lastValLat + latitudeArray[index]
                    normised17Lat = normised17LatValue
                    lastValLat = latitudeArray[index - 16]
                    latitudeList.add(normised17LatValue / 17)

                    val normised17LongValue = normised17Long - lastValLong + longitudeArray[index]
                    normised17Long = normised17LongValue
                    lastValLong = longitudeArray[index - 16]
                    longitudeList.add(normised17LongValue / 17)

                } else if (index == 16) {
                    elevationList.add(value)
                    normised17 = elevationList.sum()
                    lastVal = elevationArray[0]

                    latitudeList.add(latitudeArray[index])
                    normised17Lat = latitudeList.sum()
                    lastValLat = latitudeArray[0]

                    longitudeList.add(longitudeArray[index])
                    normised17Long = longitudeList.sum()
                    lastValLong = longitudeArray[0]

                } else {
                    elevationList.add(value)
                    latitudeList.add(latitudeArray[index])
                    longitudeList.add(longitudeArray[index])
                }
            }
        } else {
            elevationList.addAll(elevationArray)
            latitudeList.addAll(latitudeArray)
            longitudeList.addAll(longitudeArray)
        }

        return mapOf(
            "_elevationList" to elevationList,
            "_latitudeList" to latitudeList,
            "_longitudeList" to longitudeList
        )
    }
    private fun RLGenerateDataForElevationAndGPX(cardData:RLSessionDataTransferModelNew, wKey: Long,elevationArray: MutableList<Double>,latitudeArray: MutableList<Double>, longitudeArray: MutableList<Double>) {
        val retVal = RLCreateNormalisedElevation(elevationArray,latitudeArray,longitudeArray)
        var newDate = wKey - cardData.totalTime.toInt()
        var gpxStringT = ""
        var genDistance = 0.0
        var genElevation = 0.0

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())

        val elevationList: List<Double> = retVal["_elevationList"]!!
        val latitudeList: List<Double> = retVal["_latitudeList"]!!
        val longitudeList: List<Double> = retVal["_longitudeList"]!!

        elevationList.forEachIndexed  { index, value ->
            newDate += 1
            val timestamp = Date(newDate * 1000L)
            val timeISO = dateFormat.format(timestamp)

            if (index > 0) {
                val elevationDiffrent = value - elevationList[index - 1]
                val pastLatitude = latitudeList.get(index - 1)
                val currentLatitude = latitudeList[index]
                val pastLongitude = longitudeList[index - 1]
                val currentLongitude = longitudeList[index]

                val distance = 3443.8985 *
                        Math.acos(
                            Math.sin(Math.toRadians(pastLatitude)) * Math.sin(Math.toRadians(currentLatitude)) +
                                    Math.cos(Math.toRadians(pastLatitude)) * Math.cos(Math.toRadians(currentLatitude)) *
                                    Math.cos(Math.toRadians(currentLongitude) - Math.toRadians(pastLongitude))
                        ) * 1.852

                if (distance > 0 && distance < 0.022) {
                    genDistance += distance
                }

                if (elevationDiffrent > 0) {
                    genElevation += elevationDiffrent
                }
            }

            gpxStringT += """
            <trkpt lat="${latitudeList[index]}" lon="${longitudeList[index]}">
                <ele>$value}</ele>
                <time>$timeISO</time>
            </trkpt>
        """.trimIndent()
        }

        if (genElevation > 0) {
            cardData.demsElevation = genElevation.roundToInt()
        }
        cardData.generatedDistance = genDistance
        cardData.generatedElevation = genElevation.roundToInt()
        cardData.gpxTServerNString = gpxStringT
    }

    //Generate MAP URL
    private fun RLConvertToMapUrl(cardData:RLSessionDataTransferModelNew) {
        val arrLocationDetails = cardData.arrLocationDetails
        val paths = mutableListOf<String>()
        var lastLng = 0.0
        var lastLat = 0.0

        var lastStatus = 0
        var divider = arrLocationDetails.size / 400

        if (divider < 1) {
            divider = 1
        } else {
            divider = divider.toInt()
        }
        arrLocationDetails.forEachIndexed { index, locationData ->
            if (index % divider == 0 || arrLocationDetails.size < 100) {
                val lat =locationData.lat
                val lng = locationData.long
                val state = locationData.state
                if (state == lastStatus && paths.isNotEmpty()) {
                    paths[paths.size - 1] += "|${lat.format(6)},${lng.format(6)}"
                } else {
                    val color = when (state) {
                        0 -> "0xF177A0FF"
                        1 -> "0xFFCF2FFF"
                        2 -> "0x2CAE2CFF"
                        3 -> "0x0099DAFF"
                        4 -> "0xFE6902FF"
                        5 -> "0x9900CCFF"
                        6 -> "0xED4541FF"
                        7 -> "0xED4541FF"
                        else -> "0xF177A0FF"
                    }
                    var path = ""
                    path = if (path.isNotEmpty()) {
                        "path=color:$color|weight:5|${lastLat.format(6)},${lastLng.format(6)}|${lat.format(6)},${lng.format(6)}"
                    } else {
                        "path=color:$color|weight:5|${lat.format(6)},${lng.format(6)}"
                    }
                    paths.add(path)
                    lastStatus = state
                }
                lastLng = lng
                lastLat = lat
            }

        }
        var joinedPaths = paths.joinToString("&")
        val mapKey = RLConstants.mapKey

        var googleMapUrl = "http://maps.googleapis.com/maps/api/staticmap?size=400x400&maptype=roadmap&$joinedPaths&key=$mapKey"

        if (googleMapUrl.length > 15000) {
            val removeParts = (joinedPaths.length - 15000) / 140
            joinedPaths = paths.dropLast(removeParts + 1).joinToString("&")
            googleMapUrl = "http://maps.googleapis.com/maps/api/staticmap?size=400x400&maptype=roadmap&$joinedPaths&key=$mapKey"
        }
        val mapImageUrl = "http://maps.googleapis.com/maps/api/staticmap?size=400x400&maptype=roadmap&$joinedPaths"
        lifecycleScope.launch {
            RLGetMapApiCall(googleMapUrl,mapImageUrl,cardData)
        }
    }
    private fun Double.format(digits: Int) = "%.${digits}f".format(this)

    private suspend fun RLGetMapApiCall(googleMapUrl: String, saveGraphUrl: String, cardData: RLSessionDataTransferModelNew) {
        withContext(Dispatchers.IO) {
            try {
                val formattedUrl = googleMapUrl.replace("http://", "https://")
                RLTools.RlLogDPrint(TAG, "getMap Request: $formattedUrl")

                val request = Request.Builder().url(formattedUrl).build()

                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        response.body?.byteStream()?.use { inputStream ->
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            mapBitmapImage = bitmap
                            // Convert Bitmap to Base64
                            val byteArrayOutputStream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                            val base64Data = "data:image/jpeg;base64,"+Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP)
                            cardData.mapGeneratedUrl = base64Data
                            RLTools.RlLogDPrint(TAG, "Map Successful Create")
                        }
                    } else {
                        RLTools.RlLogEPrint(TAG, "getMap Error: ${response.message}")
                        cardData.mapGeneratedUrl = saveGraphUrl
                    }
                }
            } catch (e: IOException) {
                RLTools.RlLogEPrint(TAG, "getMap Network Exception: ${e.localizedMessage}")
                cardData.mapGeneratedUrl = saveGraphUrl
            } catch (e: Exception) {
                RLTools.RlLogEPrint(TAG, "getMap Exception: ${e.localizedMessage}")
                cardData.mapGeneratedUrl = saveGraphUrl
            }
        }
    }

    private fun safeNumber(value: Double?): Double {
        return if (value == null || value.isNaN() || value.isInfinite()) 0.0 else value
    }

    private fun safeIntNumber(value: Int?): Int {
        return if (value == null || value < 0 ) 0 else value
    }

    private fun RLMakeSensorData(cardData: RLSessionDataTransferModelNew,currentTimestamp:String) {

        val remark ="android"

        val connectivityDataMap = hashMapOf(
            RevoolaKeys.cadence to cardData.arrCadence,
            RevoolaKeys.connection to cardData.arrConnection,
            RevoolaKeys.hr to cardData.arrHr,
            RevoolaKeys.power to cardData.arrPower,
            RevoolaKeys.remark to remark,
            RevoolaKeys.speed to cardData.arrSpeed
        )

        val deviceRecordedDataMap = hashMapOf(
            RevoolaKeys.distance to cardData.distance,
            RevoolaKeys.elevation to cardData.totalElevation)

        val elevationDataMap = hashMapOf(
            RevoolaKeys.data to cardData.arrDataLocation,
            RevoolaKeys.status to true)

        val gpxDataMap = hashMapOf(
            RevoolaKeys.classDate to currentTimestamp,
            RevoolaKeys.gpxString to cardData.gpxStringBuilder,
            RevoolaKeys.remark to "android")

        val gpx_TDataMap = hashMapOf<String,Any>(
            RevoolaKeys.classDate to currentTimestamp,
            RevoolaKeys.elevation to cardData.totalElevation,
            RevoolaKeys.gpxString to cardData.gpxStringBuilder,
            RevoolaKeys.remark to "android")

        val gpx_T_ServerDataMap = hashMapOf<String,Any>(
            RevoolaKeys.classDate to currentTimestamp,
            RevoolaKeys.generatedDisntace to cardData.generatedDistance,
            RevoolaKeys.generatedDisntace_T to cardData.generatedDistance,
            RevoolaKeys.generatedElevation to cardData.generatedElevation,
            RevoolaKeys.generatedElevation_T to cardData.generatedElevation,
            RevoolaKeys.gpxString to cardData.gpxTServerString,
            RevoolaKeys.remark to "android",
            RevoolaKeys.statusForElevationUpdate to true)

        val gpx_T_Server_NDataMap = hashMapOf<String,Any>(
            RevoolaKeys.classDate to currentTimestamp,
            RevoolaKeys.generatedDisntace_T to cardData.generatedDistance,
            RevoolaKeys.generatedElevation_T to cardData.generatedElevation,
            RevoolaKeys.gpxString to cardData.gpxTServerNString,
            RevoolaKeys.remark to "android")

        val locationDataMap = hashMapOf(
            RevoolaKeys.className to fragBinding.edtSessionName.text.toString(),
            RevoolaKeys.classNote to fragBinding.edtAddNotes.text.toString(),
            RevoolaKeys.classType to cardData.yourWayType,
            RevoolaKeys.elevationDic to cardData.arrElevation,//ARRAY
            RevoolaKeys.locationDic to cardData.arrLocationDetails,//ARRAY
            RevoolaKeys.speedDic to cardData.arrSpeed,//ARRAY
            RevoolaKeys.totalRev to cardData.totalRev)

        //Complete Data Structure Data For Testing
        val dataForTestingDataMap = hashMapOf<String,Any>(
            RevoolaKeys.connectivity to mapOf(currentTimestamp to connectivityDataMap),
            RevoolaKeys.deviceRecordedData to mapOf(currentTimestamp to deviceRecordedDataMap),
            RevoolaKeys.elevation to mapOf(currentTimestamp to elevationDataMap),
            RevoolaKeys.gpx to mapOf(currentTimestamp to gpxDataMap),
            RevoolaKeys.gpx_T to mapOf(currentTimestamp to gpx_TDataMap),
            RevoolaKeys.gpx_T_Server to mapOf(currentTimestamp to gpx_T_ServerDataMap),
            RevoolaKeys.gpx_T_Server_N to mapOf(currentTimestamp to gpx_T_Server_NDataMap),
            RevoolaKeys.location to mapOf(currentTimestamp to locationDataMap)
        )

        val ghostDataMap = hashMapOf(
            RevoolaKeys.restingHrUsedForCalculation_Last to cardData.RFMHR,
            RevoolaKeys.arrAvgCadence to cardData.arrAvgCadence,
            RevoolaKeys.arrAvgHr to cardData.arrAvgHr,
            RevoolaKeys.arrAvgPower to  mutableListOf(0),
            RevoolaKeys.arrAvgRevPercentage to cardData.arrAvgRevPercentage,
            RevoolaKeys.arrHr to cardData.arrHr,
            RevoolaKeys.arrMaxCadence to cardData.arrMaxCadence,
            RevoolaKeys.arrMaxHr to cardData.arrMaxHr,
            RevoolaKeys.arrMaxPower to mutableListOf(0),
            RevoolaKeys.arrMaxRevPercentage to cardData.arrMaxRevPercentage,
            RevoolaKeys.arrPower to mutableListOf(0),
            RevoolaKeys.arrPowerFromDevice to mutableListOf(0),
            RevoolaKeys.arrRevPercentage to cardData.arrRevPercentage,
            RevoolaKeys.arrRevSecond to cardData.arrRevSecond,
            RevoolaKeys.classDate to currentTimestamp.toLong(),
            RevoolaKeys.displayImage to displayImage,
            RevoolaKeys.displayName to displayName,
            RevoolaKeys.flagImage to  "flag-of-United-Kingdom.png",
            RevoolaKeys.flagName to "United Kingdom",
            RevoolaKeys.isPowerDeviceConnected to  false,
            RevoolaKeys.location to "",
            RevoolaKeys.maxHrUsedForCalculation to cardData.RFMHR,
            RevoolaKeys.maxHrUsedForCalculation_Last to cardData.RFMHR,
            RevoolaKeys.restingHrUsedForCalculation to cardData.RFMHR,
            RevoolaKeys.timestamp to currentTimestamp.toLong(),
            RevoolaKeys.totalRev to cardData.totalRev,
            RevoolaKeys.totalTime to cardData.totalTime.toInt(),
            RevoolaKeys.visibilityflagforthatsession to visibilityflagforthatsession
        )

        val summaryDataMap = hashMapOf(
            RevoolaKeys.avgBurntCalories to safeNumber(cardData.arrBurntCalories.average()),
            RevoolaKeys.avgCadence to safeNumber(cardData.arrCadence.average()),
            RevoolaKeys.avgHr to safeNumber(cardData.arrHr.average()),
            RevoolaKeys.avgPower to  0,
            RevoolaKeys.avgPowerFromDevice to  0,
            RevoolaKeys.avgRevPercentage to safeNumber(cardData.arrRevPercentage.average()),
            RevoolaKeys.avgSpeed to safeNumber(cardData.arrSpeed.average()),
            RevoolaKeys.avgSpeedForOneKm to  cardData.avgSpeedForOneKm,
            RevoolaKeys.avgSpeedForOneMile to  cardData.avgSpeedForOneMile,
            RevoolaKeys.burntCalories to  cardData.burntCalories,
            RevoolaKeys.classDate to  currentTimestamp,
            RevoolaKeys.classDescription to  "",
            RevoolaKeys.classImage to  "",
            RevoolaKeys.className to  fragBinding.edtSessionName.text.toString(),
            RevoolaKeys.classNote to  fragBinding.edtAddNotes.text.toString(),
            RevoolaKeys.classType to  cardData.yourWayType,
            RevoolaKeys.demsElevation to  cardData.demsElevation,
            RevoolaKeys.distance to  cardData.distance,
            RevoolaKeys.goal to  "",
            RevoolaKeys.isClass to  false,
            RevoolaKeys.isPowerDeviceConnected to  false,
            RevoolaKeys.maxBurntCalories to  cardData.maxBurntCalories,
            RevoolaKeys.maxCadence to  cardData.maxCadence,
            RevoolaKeys.maxHr to  cardData.maxHeartRate,
            RevoolaKeys.maxPower to  0,
            RevoolaKeys.maxPowerFromDevice to  0,
            RevoolaKeys.maxRevPercentage to  cardData.maxRevPercentage,
            RevoolaKeys.maxSpeed to  cardData.maxSpeed,
            RevoolaKeys.maxSpeedForOneKm to  cardData.maxSpeedForOneKm,
            RevoolaKeys.maxSpeedForOneMile to  cardData.maxSpeedForOneMile,
            RevoolaKeys.minHr to  cardData.minHeartRate,
            RevoolaKeys.minRevPercentage to  cardData.minRevPercentage,
            RevoolaKeys.remark to  "android",
            RevoolaKeys.revPercentage to  cardData.revPercentage,
            RevoolaKeys.timestamp to  currentTimestamp.toLong(),
            RevoolaKeys.totalElevation to  cardData.totalElevation,
            RevoolaKeys.totalPower to  0,
            RevoolaKeys.totalRev to  cardData.totalRev,
            RevoolaKeys.totalSteps to  cardData.totalSteps,
            RevoolaKeys.totalTime to  cardData.totalTime.toInt(),
            RevoolaKeys.typeOfGoal to  cardData.yourWayType,
            RevoolaKeys.visibilityflagforthatsession to  visibilityflagforthatsession,


            RevoolaKeys.Zone1 to cardData.zoneDataSummery[RevoolaKeys.Zone1],
            RevoolaKeys.Zone2 to cardData.zoneDataSummery[RevoolaKeys.Zone2],
            RevoolaKeys.Zone3 to cardData.zoneDataSummery[RevoolaKeys.Zone3],
            RevoolaKeys.Zone4 to cardData.zoneDataSummery[RevoolaKeys.Zone4],
            RevoolaKeys.Zone5 to cardData.zoneDataSummery[RevoolaKeys.Zone5],
            RevoolaKeys.Zone6 to cardData.zoneDataSummery[RevoolaKeys.Zone6],
            RevoolaKeys.Zone7 to cardData.zoneDataSummery[RevoolaKeys.Zone7]
        )

        val graphDataMapHeart = hashMapOf(
            RevoolaKeys.arrCadence to cardData.arrCadence,
            RevoolaKeys.arrPower to mutableListOf(0.0),
            RevoolaKeys.arrHr to cardData.arrHr,
            RevoolaKeys.arrSpeed to cardData.arrSpeed,
            RevoolaKeys.arrRevPercentage to cardData.arrRevPercentage,
            RevoolaKeys.remark to "android",
        )

        val graphDataMapSpeedAndNoSensor = hashMapOf(
            RevoolaKeys.arrCadence to cardData.arrCadence,
            RevoolaKeys.arrPower to mutableListOf(0.0),
            RevoolaKeys.arrSpeed to cardData.arrSpeed,
            RevoolaKeys.remark to "android",
        )
        when (cardData.SENSOR){
            RLConstants.HEART_SENSOR->{
                val detailsDataMap = hashMapOf(
                    RevoolaKeys.maxHrUsedForCalculation to cardData.RFMHR,
                    RevoolaKeys.maxHrUsedForCalculation_Last to cardData.RFMHR,
                    RevoolaKeys.restingHrUsedForCalculation to cardData.RestingHR.toInt(),
                    RevoolaKeys.restingHrUsedForCalculation_Last to cardData.RestingHR.toInt(),
                    RevoolaKeys.arrBurntCalories to  cardData.arrBurntCalories,
                    RevoolaKeys.arrCadence to  cardData.arrCadence,
                    RevoolaKeys.arrCumDistance to cardData.arrCumDistance,
                    RevoolaKeys.arrCumElevation to cardData.arrCumElevation,
                    RevoolaKeys.arrCumSpeed to cardData.arrCumSpeed,
                    RevoolaKeys.arrDistance to  cardData.arrDistance,
                    RevoolaKeys.arrElevation to cardData.arrElevation,
                    RevoolaKeys.arrHRRecordedSecond to  cardData.arrHRRecordedSecond,
                    RevoolaKeys.arrHr to  cardData.arrHr,
                    RevoolaKeys.arrRevPercentage to cardData.arrRevPercentage ,
                    RevoolaKeys.arrRevSecond to cardData.arrRevSecond,
                    RevoolaKeys.arrSpeed to  cardData.arrSpeed,
                    RevoolaKeys.speedForOneKm to  cardData.speedForOneKm,
                    RevoolaKeys.speedForOneMile to  cardData.speedForOneMile,
                    RevoolaKeys.avgRevPercentage to cardData.avgRevPercentage,
                    RevoolaKeys.burntCalories to cardData.burntCalories,
                    RevoolaKeys.classDate to currentTimestamp,
                    RevoolaKeys.classDescription to "",
                    RevoolaKeys.classImage to "",
                    RevoolaKeys.className to  fragBinding.edtSessionName.text.toString(),
                    RevoolaKeys.classNote to  fragBinding.edtAddNotes.text.toString(),
                    RevoolaKeys.classType to  cardData.yourWayType,
                    RevoolaKeys.demsElevation to cardData.demsElevation,
                    RevoolaKeys.distance to cardData.distance,
                    RevoolaKeys.goal to "",
                    RevoolaKeys.isClass to false,
                    RevoolaKeys.isPowerDeviceConnected to false,
                    RevoolaKeys.mapGeneratedUrl to cardData.mapGeneratedUrl,
                    RevoolaKeys.maxRevPercentage to cardData.maxRevPercentage,
                    RevoolaKeys.minRevPercentage to cardData.minRevPercentage,
                    RevoolaKeys.remark to "android",
                    RevoolaKeys.revPercentage to cardData.revPercentage,
                    RevoolaKeys.timestamp to currentTimestamp.toInt(),
                    RevoolaKeys.totalElevation to cardData.totalElevation,
                    RevoolaKeys.totalPower to 0,
                    RevoolaKeys.totalRev to cardData.totalRev,
                    RevoolaKeys.totalSteps to cardData.totalSteps,
                    RevoolaKeys.totalTime to  cardData.totalTime.toInt(),
                    RevoolaKeys.typeOfGoal to cardData.yourWayType,

                    RevoolaKeys.Zone1 to cardData.zoneDataDetail[RevoolaKeys.Zone1],
                    RevoolaKeys.Zone2 to cardData.zoneDataDetail[RevoolaKeys.Zone2],
                    RevoolaKeys.Zone3 to cardData.zoneDataDetail[RevoolaKeys.Zone3],
                    RevoolaKeys.Zone4 to cardData.zoneDataDetail[RevoolaKeys.Zone4],
                    RevoolaKeys.Zone5 to cardData.zoneDataDetail[RevoolaKeys.Zone5],
                    RevoolaKeys.Zone6 to cardData.zoneDataDetail[RevoolaKeys.Zone6],
                    RevoolaKeys.Zone7 to cardData.zoneDataDetail[RevoolaKeys.Zone7]
                )
                RLFirebaseEntry(dataForTestingDataMap,ghostDataMap,summaryDataMap,detailsDataMap,graphDataMapHeart,cardData,currentTimestamp)
            }
            RLConstants.SPEED_SENSOR->{
                val detailsDataMap = hashMapOf(
                    RevoolaKeys.maxHrUsedForCalculation to cardData.RFMHR,
                    RevoolaKeys.maxHrUsedForCalculation_Last to cardData.RFMHR,
                    RevoolaKeys.restingHrUsedForCalculation to cardData.RestingHR.toInt(),
                    RevoolaKeys.restingHrUsedForCalculation_Last to cardData.RestingHR.toInt(),

                    RevoolaKeys.arrBurntCalories to  cardData.arrBurntCalories,
                    RevoolaKeys.arrCadence to  cardData.arrCadence,
                    RevoolaKeys.arrCumElevation to cardData.arrCumElevation,
                    RevoolaKeys.arrDistance to  cardData.arrDistance,
                    RevoolaKeys.arrElevation to cardData.arrElevation,
                    RevoolaKeys.arrPower to mutableListOf(0),
                    RevoolaKeys.arrPowerFromDevice to mutableListOf(0),
                    RevoolaKeys.arrSpeed to cardData.arrSpeed,
                    RevoolaKeys.speedForOneKm to  cardData.speedForOneKm,
                    RevoolaKeys.speedForOneMile to  cardData.speedForOneMile,

                    RevoolaKeys.avgRevPercentage to cardData.avgRevPercentage,
                    RevoolaKeys.burntCalories to cardData.burntCalories,
                    RevoolaKeys.classDate to currentTimestamp,
                    RevoolaKeys.classDescription to "",
                    RevoolaKeys.classImage to "",
                    RevoolaKeys.className to  fragBinding.edtSessionName.text.toString(),
                    RevoolaKeys.classNote to  fragBinding.edtAddNotes.text.toString(),
                    RevoolaKeys.classType to  cardData.yourWayType,
                    RevoolaKeys.demsElevation to cardData.demsElevation,
                    RevoolaKeys.distance to cardData.distance,
                    RevoolaKeys.goal to "",
                    RevoolaKeys.isClass to false,
                    RevoolaKeys.isPowerDeviceConnected to false,
                    RevoolaKeys.mapGeneratedUrl to cardData.mapGeneratedUrl,
                    RevoolaKeys.maxRevPercentage to cardData.maxRevPercentage,
                    RevoolaKeys.minRevPercentage to cardData.minRevPercentage,
                    RevoolaKeys.remark to "android",
                    RevoolaKeys.revPercentage to cardData.revPercentage,
                    RevoolaKeys.timestamp to currentTimestamp.toInt(),
                    RevoolaKeys.totalElevation to cardData.totalElevation,
                    RevoolaKeys.totalPower to 0,
                    RevoolaKeys.totalRev to cardData.totalRev,
                    RevoolaKeys.totalSteps to cardData.totalSteps,
                    RevoolaKeys.totalTime to  cardData.totalTime.toInt(),
                    RevoolaKeys.typeOfGoal to cardData.yourWayType,

                    RevoolaKeys.Zone1 to cardData.zoneDataDetail[RevoolaKeys.Zone1],
                    RevoolaKeys.Zone2 to cardData.zoneDataDetail[RevoolaKeys.Zone2],
                    RevoolaKeys.Zone3 to cardData.zoneDataDetail[RevoolaKeys.Zone3],
                    RevoolaKeys.Zone4 to cardData.zoneDataDetail[RevoolaKeys.Zone4],
                    RevoolaKeys.Zone5 to cardData.zoneDataDetail[RevoolaKeys.Zone5],
                    RevoolaKeys.Zone6 to cardData.zoneDataDetail[RevoolaKeys.Zone6],
                    RevoolaKeys.Zone7 to cardData.zoneDataDetail[RevoolaKeys.Zone7]
                )
                RLFirebaseEntry(dataForTestingDataMap,ghostDataMap,summaryDataMap,detailsDataMap,graphDataMapSpeedAndNoSensor,cardData,currentTimestamp)

            }
            RLConstants.NO_SENSOR->{
                val detailsDataMap = hashMapOf(
                    RevoolaKeys.maxHrUsedForCalculation to cardData.RFMHR,
                    RevoolaKeys.maxHrUsedForCalculation_Last to cardData.RFMHR,
                    RevoolaKeys.restingHrUsedForCalculation to cardData.RestingHR.toInt(),
                    RevoolaKeys.restingHrUsedForCalculation_Last to cardData.RestingHR.toInt(),

                    RevoolaKeys.arrBurntCalories to  cardData.arrBurntCalories,
                    RevoolaKeys.arrCadence to  cardData.arrCadence,
                    RevoolaKeys.arrCumDistance to  cardData.arrCumDistance,
                    RevoolaKeys.arrCumSpeed to  cardData.arrCumSpeed,
                    RevoolaKeys.arrDistance to  cardData.arrDistance,
                    RevoolaKeys.arrElevation to cardData.arrElevation,
                    RevoolaKeys.arrPower to mutableListOf(0),
                    RevoolaKeys.arrPowerFromDevice to mutableListOf(0),
                    RevoolaKeys.arrSpeed to cardData.arrSpeed,
                    RevoolaKeys.speedForOneKm to  cardData.speedForOneKm,
                    RevoolaKeys.speedForOneMile to  cardData.speedForOneMile,
                    RevoolaKeys.arrCumElevation to cardData.arrCumElevation,

                    RevoolaKeys.avgRevPercentage to cardData.avgRevPercentage,
                    RevoolaKeys.burntCalories to cardData.burntCalories,
                    RevoolaKeys.classDate to currentTimestamp,
                    RevoolaKeys.classDescription to "",
                    RevoolaKeys.classImage to "",
                    RevoolaKeys.className to  fragBinding.edtSessionName.text.toString(),
                    RevoolaKeys.classNote to  fragBinding.edtAddNotes.text.toString(),
                    RevoolaKeys.classType to  cardData.yourWayType,
                    RevoolaKeys.demsElevation to cardData.demsElevation,
                    RevoolaKeys.distance to cardData.distance,
                    RevoolaKeys.goal to "",
                    RevoolaKeys.isClass to false,
                    RevoolaKeys.isPowerDeviceConnected to false,
                    RevoolaKeys.mapGeneratedUrl to cardData.mapGeneratedUrl,
                    RevoolaKeys.maxRevPercentage to cardData.maxRevPercentage,
                    RevoolaKeys.minRevPercentage to cardData.minRevPercentage,
                    RevoolaKeys.remark to "android",
                    RevoolaKeys.revPercentage to cardData.revPercentage,
                    RevoolaKeys.timestamp to currentTimestamp.toInt(),
                    RevoolaKeys.totalElevation to cardData.totalElevation,
                    RevoolaKeys.totalPower to 0,
                    RevoolaKeys.totalRev to cardData.totalRev,
                    RevoolaKeys.totalSteps to cardData.totalSteps,
                    RevoolaKeys.totalTime to  cardData.totalTime.toInt(),
                    RevoolaKeys.typeOfGoal to cardData.yourWayType,

                    RevoolaKeys.Zone1 to cardData.zoneDataDetail[RevoolaKeys.Zone1],
                    RevoolaKeys.Zone2 to cardData.zoneDataDetail[RevoolaKeys.Zone2],
                    RevoolaKeys.Zone3 to cardData.zoneDataDetail[RevoolaKeys.Zone3],
                    RevoolaKeys.Zone4 to cardData.zoneDataDetail[RevoolaKeys.Zone4],
                    RevoolaKeys.Zone5 to cardData.zoneDataDetail[RevoolaKeys.Zone5],
                    RevoolaKeys.Zone6 to cardData.zoneDataDetail[RevoolaKeys.Zone6],
                    RevoolaKeys.Zone7 to cardData.zoneDataDetail[RevoolaKeys.Zone7]
                )
                RLFirebaseEntry(dataForTestingDataMap,ghostDataMap,summaryDataMap,detailsDataMap,graphDataMapSpeedAndNoSensor,cardData,currentTimestamp)

            }
        }
    }

    private fun RLFirebaseEntry(dataForTestingDataMap: HashMap<String, Any>,
        ghostDataMap: HashMap<String, Any>,summaryDataMap: HashMap<String, Serializable?>,detailsDataMap: HashMap<String, Any?>,
        graphDataMap: HashMap<String, Any>,cardData: RLSessionDataTransferModelNew,currentTimestamp: String) {

        // Writing DataForTesting Data to Firebase
        RLDatabaseManagerWrite().RlWriteDataForTestingData(RevoolaFirebasePath.dataForTestingDataPath(currentUser),dataForTestingDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful DataForTesting Entry")
            }else {
                RLTools.RlLogEPrint(TAG,"Error DataForTesting Entry:- $error")
                printToast(TAG,"Error DataForTesting Entry:- $error")
            }
        }

        val justRide_=cardData.yourWayType+"_justRide_"

        //Entry GhostData lastForClass Walk_justRide_
        val databaseRefGhostLast = FirebaseDatabase.getInstance().getReference(RevoolaFirebasePath.ghostLastForClassDataPath(currentUser))
        databaseRefGhostLast.child(justRide_).setValue(ghostDataMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    RLTools.RlLogDPrint("FirebaseDatabase", "Entry  GhostData LastForClass saved successfully!")

                } else {
                    RLTools.RlLogEPrint("FirebaseDatabase", "Failed  GhostData LastForClass to save entry :- ${ task.exception}")
                    printToast("FirebaseDatabase", "Failed  GhostData LastForClass to save entry :- ${ task.exception}")
                }
            }

        //Entry GhostData bestForClass
        val databaseRefGhostBest = FirebaseDatabase.getInstance().getReference(RevoolaFirebasePath.ghostBestForClassDataPath(currentUser))
        databaseRefGhostBest.child(justRide_).setValue(ghostDataMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    RLTools.RlLogDPrint("FirebaseDatabase", "Entry  GhostData bestForClass saved successfully!")

                } else {
                    RLTools.RlLogEPrint("FirebaseDatabase", "Failed  GhostData bestForClass to save entry :- ${ task.exception}")
                    printToast("FirebaseDatabase", "Failed  GhostData bestForClass to save entry :- ${ task.exception}")
                }
            }

        //Entry Summery
        val databaseRefSummery = FirebaseDatabase.getInstance().getReference(RevoolaFirebasePath.summaryDataPath(currentUser))
        val entryIdSummery = currentTimestamp
        entryIdSummery.let {
            databaseRefSummery.child(it).setValue(summaryDataMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLTools.RlLogDPrint("FirebaseDatabase", "Entry saved successfully! revoolaUserSessionSummaryData")

                    } else {
                        RLTools.RlLogEPrint("FirebaseDatabase", "Failed to save entry revoolaUserSessionSummaryData :- ${ task.exception}")
                        printToast("FirebaseDatabase", "Failed to save entry revoolaUserSessionSummaryData :- ${ task.exception}")
                    }
                }
        }

        //entry Graph Data
        val databaseRefGraph = FirebaseDatabase.getInstance().getReference( RevoolaFirebasePath.graphDataPath(currentUser))
        val entryIdGraph = currentTimestamp
        entryIdGraph.let {
            databaseRefGraph.child(it).setValue(graphDataMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLTools.RlLogDPrint("FirebaseDatabase", "Entry saved successfully! revoolaUserSessionSummaryGraphData")
                    } else {
                        RLTools.RlLogEPrint("FirebaseDatabase", "Failed to save entry revoolaUserSessionSummaryGraphData :- ${ task.exception}")
                        printToast("FirebaseDatabase", "Failed to save entry revoolaUserSessionSummaryGraphData :- ${ task.exception}")
                    }
                }
        }

        //entry session detail data
        val databaseRef = FirebaseDatabase.getInstance().getReference(RevoolaFirebasePath.detailDataPath(currentUser))
        val entryId = currentTimestamp
        entryId.let {
            databaseRef.child(it).setValue(detailsDataMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLTools.RlLogDPrint("FirebaseDatabase", "Entry saved successfully revoolaUserSessionDetailData!")
                        RLInsertApiCall(cardData,currentTimestamp)
                    } else {
                        RLTools.RlLogEPrint("FirebaseDatabase", "Failed to save entry revoolaUserSessionDetailData :- ${ task.exception}")
                        printToast("FirebaseDatabase", "Failed to save entry revoolaUserSessionDetailData :- ${ task.exception}")
                    }
                }
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

    private fun printToast(TAG:String,Message:String){
        if (isAdded){
           // Toast.makeText(requireContext(),Message,Toast.LENGTH_SHORT).show()
        }

    }

    private fun createPayload(cardData: RLSessionDataTransferModelNew, currentTimestamp: String): String {
        val classLeaderboard = RLClassLeaderboard(
            userId = currentUser,
            classId = "",
            timestamp = currentTimestamp,
            timestampLocal = currentTimestamp,
            totalRev = cardData.totalRev,
            visibilityFlagForThatSession = visibilityflagforthatsession,
            discipline = cardData.yourWayType.toLowerCase(),
            duration = cardData.totalTime,
            calories = cardData.burntCalories,
            bmo = 2,
            rmm = 0,
            rms = 0,
            source = "android",
            goal = "all")

        val apiPayload = listOf(RLYourWayApiPayload(classLeaderboard))

        // Convert to JSON String
        return Gson().toJson(apiPayload)
    }
    private fun RLInsertApiCall(cardData: RLSessionDataTransferModelNew, currentTimestamp: String) {
        if (RLApiClientRetrofit.RLisConnected()) {
            val jsonPayload = createPayload(cardData,currentTimestamp)
            val request = Gson().fromJson(jsonPayload, Array<RLYourWayApiPayload>::class.java).toList()

            RLTools.RlLogDPrint(TAG,"YourWay Insert Request: $request")
            //Insert Api Call
            viewModel.RLInsertYourWayData(request) { result ->
                result.onSuccess { response ->
                    try {
                        if (response.type.equals("success")) {
                            RLInsertOverviewApiCall(cardData,currentTimestamp)
                            RLTools.RlLogDPrint(TAG, "YourWay Insert Success: ${response.text}")
                        } else {
                            RLBaseProgress.RLhideProgressDialog()
                            RLTools.RlLogEPrint(TAG, "YourWay Insert Fail: ${response.text}")
                            printToast(TAG, "YourWay Insert Fail: ${response.text}")
                        }
                    } catch (e: Exception) {
                        RLBaseProgress.RLhideProgressDialog()
                        e.printStackTrace()
                        RLTools.RlLogEPrint(TAG, "YourWay Insert Catch: ${e.message}" )
                        printToast(TAG, "YourWay Insert Catch: ${e.message}" )

                    }
                }.onFailure { error ->
                    RLBaseProgress.RLhideProgressDialog()
                    RLTools.RlLogEPrint(TAG, "YourWay Insert Error: ${error.localizedMessage}" )
                    printToast(TAG, "YourWay Insert Error: ${error.localizedMessage}" )
                }
            }
        }

    }

    private fun createOverviewPayloadNew(cardData: RLSessionDataTransferModelNew, currentTimestamp: String): Map<String, RequestBody> {
        val requestBodyMap = mutableMapOf<String, RequestBody>()
        // hrm -> 1 (is hr sensor is connected), 0 (if not connected)
        // Add text fields as form data
        requestBodyMap["data[myOverviewThumbnails][userid]"] = createRequestBody(currentUser)
        requestBodyMap["data[myOverviewThumbnails][className]"] = createRequestBody(fragBinding.edtSessionName.text.toString())
        requestBodyMap["data[myOverviewThumbnails][classType]"] = createRequestBody(cardData.yourWayType.toLowerCase())
        requestBodyMap["data[myOverviewThumbnails][timestamp]"] = createRequestBody(currentTimestamp)
        requestBodyMap["data[myOverviewThumbnails][timestamp_local]"] = createRequestBody(currentTimestamp)
        requestBodyMap["data[myOverviewThumbnails][imageLinkSmall]"] = createRequestBody("")
        requestBodyMap["data[myOverviewThumbnails][totalREV]"] = createRequestBody(safeNumber(cardData.totalRev).toString())
        requestBodyMap["data[myOverviewThumbnails][totalTime]"] = createRequestBody(cardData.totalTime.toString())

        val burntCalories = safeNumber(cardData.burntCalories) ?: 0

        requestBodyMap["data[myOverviewThumbnails][burntCalories]"] = createRequestBody(burntCalories.toString())

        requestBodyMap["data[myOverviewThumbnails][totalRMM]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][totalRMS]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][maxRevPercentage]"] = createRequestBody(safeNumber(cardData.maxRevPercentage).toString())
        requestBodyMap["data[myOverviewThumbnails][avgRevPercentage]"] = createRequestBody(safeNumber(cardData.avgRevPercentage).toString())

        requestBodyMap["data[myOverviewThumbnails][zone1Seconds]"] = createRequestBody(safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone1]?.seconds).toString())
        requestBodyMap["data[myOverviewThumbnails][zone2Seconds]"] = createRequestBody(safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone2]?.seconds).toString())
        requestBodyMap["data[myOverviewThumbnails][zone3Seconds]"] = createRequestBody(safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone3]?.seconds).toString())
        requestBodyMap["data[myOverviewThumbnails][zone4Seconds]"] = createRequestBody(safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone4]?.seconds).toString())
        requestBodyMap["data[myOverviewThumbnails][zone5Seconds]"] = createRequestBody(safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone5]?.seconds).toString())
        requestBodyMap["data[myOverviewThumbnails][zone6Seconds]"] = createRequestBody(safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone6]?.seconds).toString())
        requestBodyMap["data[myOverviewThumbnails][zone7Seconds]"] = createRequestBody(safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone7]?.seconds).toString())
        requestBodyMap["data[myOverviewThumbnails][medals]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][awards]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][visibilityflagforthatsession]"] = createRequestBody(visibilityflagforthatsession.toString())
        requestBodyMap["data[myOverviewThumbnails][bmo]"] = createRequestBody("2")
        requestBodyMap["data[myOverviewThumbnails][instructor]"] = createRequestBody("")
        requestBodyMap["data[myOverviewThumbnails][duration]"] = createRequestBody("")
        requestBodyMap["data[myOverviewThumbnails][rideTitle]"] = createRequestBody("")
        requestBodyMap["data[myOverviewThumbnails][mainTitle]"] = createRequestBody("")
        requestBodyMap["data[myOverviewThumbnails][originalClassDate]"] = createRequestBody("")
        requestBodyMap["data[myOverviewThumbnails][videoKey]"] = createRequestBody("")
        requestBodyMap["data[myOverviewThumbnails][goal]"] = createRequestBody("")
        requestBodyMap["data[myOverviewThumbnails][medals_gold]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][medals_silver]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][medals_bronze]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][elevation]"] = createRequestBody(safeNumber(cardData.totalElevation).toString())
        requestBodyMap["data[myOverviewThumbnails][power]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][hr]"] = createRequestBody(safeIntNumber(cardData.avgHr).toString())
        requestBodyMap["data[myOverviewThumbnails][steps]"] = createRequestBody(safeIntNumber(cardData.totalSteps).toString())
        requestBodyMap["data[myOverviewThumbnails][distance]"] = createRequestBody(safeNumber(cardData.distance).toString())
        requestBodyMap["data[myOverviewThumbnails][hrm]"] = createRequestBody(safeIntNumber(cardData.hrm).toString())
        requestBodyMap["data[myOverviewThumbnails][class_level]"] = createRequestBody("")
        requestBodyMap["data[myOverviewThumbnails][average_speed]"] = createRequestBody(safeNumber(cardData.avgSpeed).toString())
        requestBodyMap["data[myOverviewThumbnails][share_map]"] = createRequestBody(safeIntNumber(shareMap).toString())
        requestBodyMap["data[myOverviewThumbnails][from_third_party_source]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][map_url]"] = createRequestBody("")
        requestBodyMap["data[myOverviewThumbnails][mhr]"] = createRequestBody(safeIntNumber(cardData.maxHeartRate).toString())
        requestBodyMap["data[myOverviewThumbnails][rhr]"] =  createRequestBody(cardData.RestingHR)

        requestBodyMap["data[myOverviewThumbnails][avg_hr]"] = createRequestBody(safeIntNumber(cardData.avgHr).toString())
        requestBodyMap["data[myOverviewThumbnails][notes]"] = createRequestBody(fragBinding.edtAddNotes.text.toString())
        requestBodyMap["data[myOverviewThumbnails][source]"] = createRequestBody("android")


        return requestBodyMap
    }
    private fun RLInsertOverviewApiCall(cardData: RLSessionDataTransferModelNew, currentTimestamp: String) {
        if (RLApiClientRetrofit.RLisConnected()) {
            val dataMap  = createOverviewPayloadNew(cardData,currentTimestamp)
            val images=getUserImages()
            RLTools.RlLogDPrint(TAG,"Overview Insert Request: $dataMap")
            //Insert Api Call
            viewModel.RLInsertYourWayOverviewData(dataMap,images) { result ->
                result.onSuccess { response ->
                    try {
                        if (response.type.equals("success")) {
                            RLTools.RlLogDPrint(TAG, "Overview Insert Success: ${response.text}")
                            RLupdateUserInsightlyMoengageApiCall(cardData,currentTimestamp)
                        } else {
                            RLBaseProgress.RLhideProgressDialog()
                            RLTools.RlLogEPrint(TAG, "Overview Insert Fail: ${response.text}")
                            printToast(TAG, "Overview Insert Fail: ${response.text}")
                        }
                    } catch (e: Exception) {
                        RLBaseProgress.RLhideProgressDialog()
                        e.printStackTrace()
                        RLTools.RlLogEPrint(TAG, "Overview Insert Catch: ${e.message}" )
                        printToast(TAG, "Overview Insert Catch: ${e.message}" )

                    }
                }.onFailure { error ->
                    RLBaseProgress.RLhideProgressDialog()
                    RLTools.RlLogEPrint(TAG, "Overview Insert Error: ${error.message}" )
                    printToast(TAG, "Overview Insert Error: ${error.message}" )
                }
            }
        }

    }
    // Convert text to RequestBody
    private fun createRequestBody(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }
    private fun RLupdateUserInsightlyMoengageApiCall(cardData: RLSessionDataTransferModelNew,currentTimestamp:String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestApi = RLInsightlyMoengageApiPayload(
                    email=cardData.emailId,
                    uid= currentUser,
                    device_type= "Android",
                    Is_basic_data_added= cardData.isBasicDataAdded,
                    your_way= RLTools.RLgetCurrentISO8601())

                RLTools.RlLogDPrint(TAG, "Insightly Moengage requestApi: $requestApi")

                val client = OkHttpClient()
                val mediaType = "application/json".toMediaType()
                val body = Gson().toJson(requestApi).toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(RLConstants.UPDATE_MOENAGE_USER)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                // Log response on background thread
                RLTools.RlLogDPrint(TAG, "Insightly Moengage Response: $responseBody")
                val apiResponse = Gson().fromJson(responseBody, RLInsightlyMoEngageResponse::class.java)
                // If UI update needed, switch to Main Thread
                CoroutineScope(Dispatchers.Main).launch {
                    if (apiResponse.response.isNotEmpty() && apiResponse.response[0].success == "true") {
                        // Show success message in UI
                        // Handle UI updates if required (e.g., Toast message)
                        RLBaseProgress.RLhideProgressDialog()
                        RLTools.RlLogDPrint(TAG, "Insightly Moengage Insert Success: ${response}")
                        printToast(TAG, "Success")
                        RLAllProcessDone(cardData,currentTimestamp)
                       // RLBottomHideShowSet(true)
                       // (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                       // (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)

                    }else{
                        RLBaseProgress.RLhideProgressDialog()
                        RLTools.RlLogEPrint(TAG, "Moengage Error: ${apiResponse.response[0].status}")
                        printToast(TAG, "Moengage Error: ${apiResponse.response[0].status}")
                    }

                }

            } catch (e: Exception) {
                RLBaseProgress.RLhideProgressDialog()
                RLTools.RlLogEPrint(TAG, "Insightly Moengage Error: ${e.localizedMessage}")
            }
        }
    }
    private fun getUserImages(): List<MultipartBody.Part> {
        val imageParts = mutableListOf<MultipartBody.Part>()
        imgUriList.forEachIndexed { index, uri ->
            val imageFile = RLTools.RLGetFileFromUri(requireContext(), uri)
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("userImage[]", imageFile.name, requestFile)
            imageParts.add(imagePart)
        }
        if (mapBitmapImage!=null){
            val imageFile = RLsaveBitmapToFile(mapBitmapImage!!)
            if (imageFile!=null){
                val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("mapImage", imageFile.name, requestFile)
                imageParts.add(imagePart)
                mapImageUri = Uri.fromFile(imageFile)
                return imageParts
            }else{
                return imageParts
            }
        }else{
            return imageParts
        }
    }
    private fun RLsaveBitmapToFile(bitmap: Bitmap): File? {
        try {
            // Create a file to save the bitmap
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val imageFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
            // Write the bitmap data to the file
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return imageFile
        } catch (e: IOException) {
            e.printStackTrace()
            RLTools.RlLogEPrint("CAMERAIMAGHE","ERROR=="+e.localizedMessage)
            return null
        }
    }

    private fun RLAllProcessDone(cardData: RLSessionDataTransferModelNew,currentTimestamp:String){

        // Convert to a single comma-separated string
        var imgString: String=""
        var mapImageString: String=""
        if (imgUriList.isNotEmpty()){
            imgString = imgUriList.joinToString(separator = ",") { it.toString() }
        }
        if (mapImageUri!=null){
            // If imgString is empty, just assign mapImageUri.toString()
            mapImageString = mapImageUri.toString()
        }

        val  modelData= RLTextOverview(
            avatar =cardData.displayImage,
            username =cardData.displayName,
            first_name =cardData.displayName,
            ID =  0,
            userid =  currentUser,
            className =fragBinding.edtSessionName.text.toString(),
            classType =cardData.classType,
            timestamp =currentTimestamp,
            short_timestamp= currentTimestamp,
            timestamp_local=currentTimestamp,
            imageLinkSmall ="",
            totalREV =safeNumber(cardData.totalRev),
            totalTime= cardData.totalTime,
            burntCalories= safeNumber(cardData.burntCalories).toString(),
            totalRMM= "0",
            totalRMS ="0",
            maxRevPercentage= safeNumber(cardData.maxRevPercentage),
            avgRevPercentage= safeNumber(cardData.avgRevPercentage).toString(),
            zone1Seconds= safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone1]?.seconds).toString(),
            zone2Seconds =safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone2]?.seconds).toString(),
            zone3Seconds =safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone3]?.seconds).toString(),
            zone4Seconds =safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone4]?.seconds).toString(),
            zone5Seconds =safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone5]?.seconds).toString(),
            zone6Seconds =safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone6]?.seconds).toString(),
            zone7Seconds =safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone7]?.seconds).toString(),
            medals ="0",
            medals_gold =0,
            medals_silver =0,
            medals_bronze =0,
            awards ="0",
            visibilityFlagForThatSession =visibilityflagforthatsession,
            bmo =2,
            instructor ="",
            duration ="",
            rideTitle ="",
            mainTitle ="",
            originalClassDate ="",
            videoKey ="",
            goal ="all",
            avatarKudos ="",
            avatar_comments = "",
            total_kudos =0,
            total_comments =0,
            elevation =safeNumber(cardData.totalElevation).toInt(),
            power =0,
            hr =safeIntNumber(cardData.avgHr),
            steps =safeIntNumber(cardData.totalSteps),
            distance= safeNumber(cardData.distance),
            hrm= cardData.hrm,
            class_level ="",
            average_speed= safeNumber(cardData.avgSpeed),
            map_image = mapImageString,
            user_images =imgString,
            isDeleted =0,
            dems = "",
            spike_steps = "",
            spike_timestamp = "",
            share_map =shareMap,
            from_third_party_source =0,
            map_url = cardData.mapGeneratedUrl,
            dom =0,
            rhr =safeIntNumber(cardData.RestingHR.toInt()),
            mhr =cardData.maxHeartRate,
            avgHr= safeIntNumber(cardData.avgHr),
            notes = fragBinding.edtAddNotes.text.toString(),
            source ="android",
            isKudos= 0)

        val bundle = Bundle()
        bundle.putSerializable(RLConstants.CardData, modelData)
        bundle.putString(RLConstants.FeedSelectTag, "FRIENDS")
        bundle.putBoolean("isSessionComplete", true)
        (context as RLMainActivityRL).RLloadFrag(RLFragSessionSummary().newInstance(bundle), TAG, false, null, true)
    }

  //Below All Code ImagePicker
    private fun RLHandleSelectedImageList(imgUriList:MutableList<Uri>){
        if (imgUriList.size > 0) {
            RLimageListVisible(true)
        } else {
            RLimageListVisible(false)
        }
        fragBinding.rvSelectedImages.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val selectedImagesAdapter = RLSelectedImagesAdapter(imgUriList) { uri ->
            imgUriList.remove(uri)
            if (imgUriList.size > 0) {
                RLimageListVisible(true)
            } else {
                RLimageListVisible(false)
            }
        }
        fragBinding.rvSelectedImages.adapter = selectedImagesAdapter
    }
    private fun RLchooseFromGallery() {
        try {
            Matisse.from(this)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(5)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(GlideEngine())  // Requires implementation
                .forResult(REQUEST_CODE_CHOOSE_IMAGE)
        }catch (e:Exception){
            RLTools.RlLogEPrint(TAG,"Exception: ${e.localizedMessage}")
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
    // Handle in onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {
            val uriList = Matisse.obtainResult(data)
            // Handle the selected images here
            for (uri in uriList) {
                imgUriList.add(uri)
            }
            RLHandleSelectedImageList(imgUriList)
        }
    }

}


