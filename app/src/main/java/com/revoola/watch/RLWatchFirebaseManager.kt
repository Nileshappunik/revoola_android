package com.revoola.watch

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Base64
import android.util.Log
import com.capacitor.custom.notification.safevalueread.SafeBooleanAdapter
import com.capacitor.custom.notification.safevalueread.SafeDoubleAdapter
import com.capacitor.custom.notification.safevalueread.SafeDoubleListAdapter
import com.capacitor.custom.notification.safevalueread.SafeIntAdapter
import com.capacitor.custom.notification.safevalueread.SafeIntListAdapter
import com.capacitor.custom.notification.safevalueread.SafeLongAdapter
import com.capacitor.custom.notification.safevalueread.SafeStringAdapter
import com.capacitor.custom.notification.safevalueread.SafeStringListAdapter
import com.capacitor.custom.notification.safevalueread.SafeZoneDataMapAdapter
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.revoola.api.RLApiClientRet
import com.revoola.commonobject.RLTools
import com.revoola.commonobject.RLYourWayCalvulation
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databasefirebase.RLDatabaseManagerWrite
import com.revoola.databasefirebase.RLZoneDataDetails
import com.revoola.databasefirebase.RLZoneDataSummery
import com.revoola.databasefirebase.RevoolaFirebasePath
import com.revoola.databasefirebase.RevoolaKeys
import com.revoola.databasefirebase.RlWatchSessionDataClass
import com.revoola.firebaseModel.RLElevationPoint
import com.revoola.firebaseModel.RLLocationDetails
import com.revoola.fragment.start.yourway.RLSessionDataTransferModelNew
import com.revoola.model.RLClassLeaderboard
import com.revoola.model.RLGetElevationResponseModel
import com.revoola.model.RLInsightlyMoEngageResponse
import com.revoola.model.RLInsightlyMoengageApiPayload
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.revoola.model.RLYourWayApiPayload
import com.revoola.viewmodel.RLMainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class RLWatchFirebaseManager {
    private val TAG ="RLWatchFirebaseManager"
    val databaseManager = RLDatabaseManagerWrite()
    val databaseRead = RLDatabaseManagerRead()
    private var currentUser = ""
    private lateinit var apiClientRetrofit: RLApiClientRet
    private val httpClient by lazy { OkHttpClient() }
    private var mapBitmapImage: Bitmap? = null
    private var mapGeneratedUrl: String = ""

    private var  demsElevation = -1
    private var  generatedDistance = 0.00
    private var  generatedElevation = 0
    private var  gpxTServerNString = ""
    private var  gpxTServerString = ""

    private var server1Url="http://demsworld.revoola.com:10000/api/v1/lookup"
    private var server2Url="http://demsworld.revoola.com:10000/api/v1/lookup"


    //Fetch Watch data and Save As normal Session
    fun fetchWatchSessionAndSaveToFirebase(userId:String,context: Context){
        RLDatabaseManagerRead().rl_userBasicDataRead(userId) { data, error ->
            if (data != null) {
                val gson = GsonBuilder()
                    .registerTypeAdapter(Int::class.java, SafeIntAdapter())
                    .registerTypeAdapter(Double::class.java, SafeDoubleAdapter())
                    .registerTypeAdapter(Long::class.java, SafeLongAdapter())
                    .registerTypeAdapter(Boolean::class.java, SafeBooleanAdapter())
                    .registerTypeAdapter(String::class.java, SafeStringAdapter())
                    .create()

                val jsonObject = gson.toJson(data)
                val userData = gson.fromJson(jsonObject, RLRevoolaUsersSettingsModel::class.java)

                // Firebase Operations
               readWatchData(userData,context,userId)
            }else{
                Log.e("FirebaseManager","Basic Data Null")
            }
        }
    }

    //Below Code is WatchData Session Data Get
    private fun readWatchData(userData: RLRevoolaUsersSettingsModel, context: Context, userId:String) {
        rl_fetchServerUrl()
        currentUser = userId
        Log.d(TAG,"currentUser:- $currentUser")
        val path = "/proposedstructure/revoolaUserSessionDetailDataWatch/$currentUser"
        databaseRead.rl_readData(path) { success, error ->
            if (success != null && success is Map<*, *>) {
                success.forEach { (key, value) ->
                    val sessionValueMap = value as? Map<String, Any>
                    sessionValueMap?.let {
                        val sessionKey = key.toString()
                        val jsonString = Gson().toJson(it)
                        Log.d(TAG,"sessionKey:- $sessionKey")
                        try {
                            val gson = GsonBuilder()
                                .registerTypeAdapter(Int::class.java, SafeIntAdapter())
                                .registerTypeAdapter(Double::class.java, SafeDoubleAdapter())
                                .registerTypeAdapter(Long::class.java, SafeLongAdapter())
                                .registerTypeAdapter(String::class.java, SafeStringAdapter())
                                .registerTypeAdapter(object : TypeToken<MutableList<Int>>() {}.type, SafeIntListAdapter())
                                .registerTypeAdapter(object : TypeToken<MutableList<Double>>() {}.type, SafeDoubleListAdapter())
                                .registerTypeAdapter(object : TypeToken<MutableList<String>>() {}.type, SafeStringListAdapter())
                                .registerTypeAdapter(object : TypeToken<Map<String, RLZoneDataDetails>>() {}.type, SafeZoneDataMapAdapter())
                                .create()


                            val sessionData = gson.fromJson(jsonString, RlWatchSessionDataClass::class.java)
                            // Api call
                            apiClientRetrofit = RLApiClientRet(context)

                            val yourWayType = sessionData.singleValueData.classType .toLowerCase()
                            if (yourWayType.equals("walk")||yourWayType.equals("run")||yourWayType.equals("ride")){
                                //Generate MAP URL
                                if (sessionData.dataForDetails.latLongDic.isNullOrEmpty()){
                                    makeDataToFirebase(sessionData,userData,sessionKey)
                                }else{
                                    generateElevationDems(sessionData,userData,sessionKey)
                                }
                            }else{
                                makeDataToFirebase(sessionData,userData,sessionKey)
                            }

                        }catch (e:Exception){
                            Log.d(TAG,"read Exception:- ${e.localizedMessage}")
                            databaseManager.rl_update_All_Data("/proposedstructure/revoolaUserSessionDetailDataWatch/$currentUser/$sessionKey/singleValueData/speedForOneKm", listOf(0))
                            databaseManager.rl_update_All_Data("/proposedstructure/revoolaUserSessionDetailDataWatch/$currentUser/$sessionKey/singleValueData/speedForOneMile", listOf(0))
                        }

                    }
                }
            } else {
                Log.e(TAG, "Failed to read data or unexpected format: $error")
            }
        }
    }

   private fun generateElevationDems(sessionData: RlWatchSessionDataClass, userData: RLRevoolaUsersSettingsModel, sessionKey: String) {
        rl_convertLatLongOBj(sessionData)

        GlobalScope.launch(Dispatchers.Main) {
            delay(8000L) // Delay for 8 seconds
            rl_convertToMapUrl(sessionData, userData, sessionKey)
        }
    }

    //Firebase To Fetch Server Data
    private fun rl_fetchServerUrl() {
        // Firebase to fetch user data
        val path = RevoolaFirebasePath.worldUrlGetDataPath()
        RLDatabaseManagerRead().rl_readData(path) { data, error ->
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
    private fun rl_convertLatLongOBj(cardData:RlWatchSessionDataClass){
        val latLongList = mutableListOf<Map<String, Double>>()
        val arrLocationDetails: MutableList<RLElevationPoint> = cardData.dataForDetails.latLongDic.mapNotNull { item ->
            try {
                RLElevationPoint(
                    latitude = (item["lat"] as? Number)?.toDouble() ?: 0.0,
                    longitude = (item["long"] as? Number)?.toDouble() ?: 0.0,
                    elevation = (item["elevation"] as? Number)?.toDouble() ?: 0.0)
            } catch (e: Exception) {
                null // Skip malformed entries
            }
        }.toMutableList()
        arrLocationDetails.forEach {locationData->
            latLongList.add(mapOf("latitude" to locationData.latitude, "longitude" to locationData.longitude))
        }
        val objOfLatLong = JSONObject().apply {
            put("locations", JSONArray(latLongList))
        }
        GlobalScope.launch {
            rl_getElevationServer1ApiCall(objOfLatLong,cardData)
        }
    }
    private suspend fun rl_getElevationServer1ApiCall(objOfLatLong: JSONObject, cardData: RlWatchSessionDataClass) {
        withContext(Dispatchers.IO) {
            try {
                RLTools.rl_logDPrint(TAG, "getElevation Request: $objOfLatLong")

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
                        RLTools.rl_logEPrint(TAG, "getElevation API Failed: HTTP ${response.code}, ${response.message}")
                        rl_getElevationServer2ApiCall(objOfLatLong, cardData)
                        return@use
                    }
                    RLTools.rl_logDPrint(TAG, "getElevation Response: $responseBody")
                    val apiResponse = Gson().fromJson(responseBody, RLGetElevationResponseModel::class.java)
                    withContext(Dispatchers.Main) {
                        if (apiResponse.results.isNotEmpty()) {
                            RLTools.rl_logDPrint(TAG, "getElevation Success: $apiResponse")
                            rl_handleElevationResponse(apiResponse, cardData, 1)
                        } else {
                            RLTools.rl_logEPrint(TAG, "getElevation Error: Empty Response")
                            rl_getElevationServer2ApiCall(objOfLatLong, cardData)
                        }
                    }
                }
            } catch (e: Exception) {
                rl_getElevationServer2ApiCall(objOfLatLong, cardData)
                RLTools.rl_logEPrint(TAG, "getElevation Exception: ${e.localizedMessage}")
            }
        }
    }
    private suspend fun rl_getElevationServer2ApiCall(objOfLatLong: JSONObject, cardData: RlWatchSessionDataClass) {
        withContext(Dispatchers.IO) {
            try {
                RLTools.rl_logDPrint(TAG, "getElevation Request: $objOfLatLong")
                val requestBody = objOfLatLong.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url(server2Url)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()
                    if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
                        RLTools.rl_logEPrint(TAG, "getElevation API Failed: HTTP ${response.code}, ${response.message}")
                        return@use
                    }

                    RLTools.rl_logDPrint(TAG, "getElevation Response: $responseBody")

                    val apiResponse = Gson().fromJson(responseBody, RLGetElevationResponseModel::class.java)

                    withContext(Dispatchers.Main) {
                        if (apiResponse.results.isNotEmpty()) {
                            rl_handleElevationResponse(apiResponse, cardData, 2)
                            RLTools.rl_logDPrint(TAG, "getElevation Success: $apiResponse")
                        } else {
                            RLTools.rl_logEPrint(TAG, "getElevation Error: Empty Response")
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    RLTools.rl_logEPrint(TAG, "getElevation Exception: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun rl_handleElevationResponse(apiResponse: RLGetElevationResponseModel, cardData:RlWatchSessionDataClass, server:Int) {
        val latitudeArray = mutableListOf<Double>()
        val longitudeArray = mutableListOf<Double>()
        val elevationArray = mutableListOf<Double>()
        val wKey  = (System.currentTimeMillis() / 1000)
        val newDate = wKey - safeStringToInt(cardData.singleValueData.totalTime).toLong()
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

       gpxTServerString=gpxString
        rl_generateDataForElevationAndGPX(cardData,wKey,elevationArray,latitudeArray,longitudeArray)
    }
    private fun rl_createNormalisedElevation(elevationArray: MutableList<Double>, latitudeArray: MutableList<Double>, longitudeArray: MutableList<Double>): Map<String, List<Double>>{
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
    private fun rl_generateDataForElevationAndGPX(cardData:RlWatchSessionDataClass, wKey: Long, elevationArray: MutableList<Double>, latitudeArray: MutableList<Double>, longitudeArray: MutableList<Double>) {
        val retVal = rl_createNormalisedElevation(elevationArray,latitudeArray,longitudeArray)
        var newDate = wKey - safeStringToInt(cardData.singleValueData.totalTime)
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
            demsElevation = genElevation.roundToInt()
        }
        generatedDistance = genDistance
       generatedElevation = genElevation.roundToInt()
        gpxTServerNString = gpxStringT
    }

    // Below Code IS Map Generate
    private fun rl_convertToMapUrl(response:RlWatchSessionDataClass, userData: RLRevoolaUsersSettingsModel, currentTimestamp:String) {
        val arrLocationDetails: MutableList<RLLocationDetails> = response.dataForDetails.latLongDic.mapNotNull { item ->
            try {
                RLLocationDetails(
                    deviceSpeed = (item["speed"] as? Number)?.toDouble() ?: 0.0,
                    speed = (item["speed"] as? Number)?.toDouble() ?: 0.0,
                    lat = (item["lat"] as? Number)?.toDouble() ?: 0.0,
                    state = (item["state"] as? Number)?.toInt() ?: 0,
                    long = (item["long"] as? Number)?.toDouble() ?: 0.0,
                    elevation = (item["elevation"] as? Number)?.toDouble() ?: 0.0
                )
            } catch (e: Exception) {
                null // Skip malformed entries
            }
        }.toMutableList()
      //  val arrLocationDetails = response.dataForDetails.latLongDic
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
        //MAP KEY AND API
         val mapKey = "AIzaSyBUc1JOJWSpJJtGIge4xc1LBcTT_m3w1FU"
        var googleMapUrl = "http://maps.googleapis.com/maps/api/staticmap?size=400x400&maptype=roadmap&$joinedPaths&key=$mapKey"

        if (googleMapUrl.length > 15000) {
            val removeParts = (joinedPaths.length - 15000) / 140
            joinedPaths = paths.dropLast(removeParts + 1).joinToString("&")
            googleMapUrl = "http://maps.googleapis.com/maps/api/staticmap?size=400x400&maptype=roadmap&$joinedPaths&key=$mapKey"
        }

        val mapImageUrl = "http://maps.googleapis.com/maps/api/staticmap?size=400x400&maptype=roadmap&$joinedPaths"
        GlobalScope.launch {
            rl_getMapApiCall(googleMapUrl,mapImageUrl,response,userData,currentTimestamp)
        }
    }
    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
    private suspend fun rl_getMapApiCall(googleMapUrl: String, saveGraphUrl: String, sessionData: RlWatchSessionDataClass,
                                         userData: RLRevoolaUsersSettingsModel, sessionKey: String) {
        withContext(Dispatchers.IO) {
            try {
                val formattedUrl = googleMapUrl.replace("http://", "https://")
                RLTools.rl_logDPrint(TAG, "getMap Request: $formattedUrl")

                val request = Request.Builder().url(formattedUrl).build()

                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        response.body?.byteStream()?.use { inputStream ->
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            mapBitmapImage = bitmap
                            // Convert Bitmap to Base64
                            val byteArrayOutputStream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                            val base64Data = "data:image/jpeg;base64,"+ Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP)
                            mapGeneratedUrl = base64Data
                            RLTools.rl_logDPrint(TAG, "Map Successful Create")
                            makeDataToFirebase(sessionData,userData,sessionKey)
                        }
                    } else {
                        RLTools.rl_logEPrint(TAG, "getMap Error: ${response.message}")
                        mapGeneratedUrl = saveGraphUrl
                        makeDataToFirebase(sessionData,userData,sessionKey)
                    }
                }
            } catch (e: IOException) {
                RLTools.rl_logEPrint(TAG, "getMap Network Exception: ${e.localizedMessage}")
                mapGeneratedUrl = saveGraphUrl
                makeDataToFirebase(sessionData,userData,sessionKey)
            } catch (e: Exception) {
                RLTools.rl_logEPrint(TAG, "getMap Exception: ${e.localizedMessage}")
                mapGeneratedUrl = saveGraphUrl
                makeDataToFirebase(sessionData,userData,sessionKey)
            }
        }
    }

    //Below Code Firebase Store start
    private fun safeStringToInt(value: String?): Int {
        return value?.toIntOrNull() ?: 0
    }
    private fun makeDataToFirebase(response:RlWatchSessionDataClass,userData: RLRevoolaUsersSettingsModel,currentTimestamp:String) {
        val cardData = RLSessionDataTransferModelNew()

        cardData.yourWayType = response.singleValueData.classType
        cardData.totalTime = response.singleValueData.totalTime
        cardData.gpxStringBuilder = gpxTServerNString
        cardData.gpxTServerString= gpxTServerString
        cardData.gpxTServerNString= gpxTServerNString
        cardData.generatedDistance = generatedDistance
        cardData.generatedElevation = generatedElevation
        cardData.demsElevation = demsElevation

        cardData.SENSOR = "HEARTRATESENSOR"

        cardData.avgRevPercentage = RLYourWayCalvulation.noNanValueDouble(response.normalArrayData.arrRevPercentage.average()?:0.00)
        cardData.burntCalories = RLYourWayCalvulation.noNanValueDouble(response.singleValueData.burntCalories?:0.0)
        cardData.distance = RLYourWayCalvulation.noNanValueDouble(response.singleValueData.distance?:0.0)
        cardData.maxRevPercentage = RLYourWayCalvulation.noNanValueDouble(response.normalArrayData.arrRevPercentage.max()?:0.00?:0.00)
        cardData.minRevPercentage = RLYourWayCalvulation.noNanValueDouble(response.normalArrayData.arrRevPercentage.min()?:0.00?:0.00)
        cardData.revPercentage = RLYourWayCalvulation.noNanValueDouble(response.normalArrayData.arrRevPercentage.average()?:0.00)
        cardData.totalElevation = RLYourWayCalvulation.noNanValueDouble(response.singleValueData.totalElevation?:0.0)
        cardData.totalRev = RLYourWayCalvulation.noNanValueDouble(response.singleValueData.totalRev?:0.0)
        cardData.totalSteps = response.singleValueData.totalSteps
        cardData.maxSpeed =  (response.normalArrayData.arrSpeed.max()?:0).toInt()
        cardData.maxHeartRate = response.normalArrayData.arrHr.max()?:0
        cardData.maxCadence = 0
        cardData.maxBurntCalories = response.normalArrayData.arrBurntCalories.max().roundToInt()?:0
        cardData.minHeartRate =response.normalArrayData.arrHr.min()?:0
        cardData.avgSpeed =  RLYourWayCalvulation.noNanValueDouble(response.normalArrayData.arrSpeed.average()?:0.0)
        cardData.hrm = 1

        cardData.maxSpeedForOneKm = RLYourWayCalvulation.noNanValueDouble(response.singleValueData.speedForOneKm.max()?:0.0)
        cardData.maxSpeedForOneMile = RLYourWayCalvulation.noNanValueDouble(response.singleValueData.speedForOneMile.max()?:0.0)
        cardData.avgSpeedForOneKm =  RLYourWayCalvulation.noNanValueDouble(response.singleValueData.speedForOneKm.average()?:0.0)
        cardData.avgSpeedForOneMile =  RLYourWayCalvulation.noNanValueDouble(response.singleValueData.speedForOneMile.average()?:0.0)

        cardData.arrConnection =  mutableListOf(true)

        cardData.arrBurntCalories = response.normalArrayData.arrBurntCalories
        cardData.arrCadence = response.normalArrayData.arrCadence.map { it.toDouble() }.toMutableList()
        cardData.arrDistance = response.normalArrayData.arrDistance
        cardData.arrElevation = response.normalArrayData.arrElevation
        cardData.arrHRRecordedSecond =  mutableListOf()
        cardData.arrHr = response.normalArrayData.arrHr
        cardData.arrRevPercentage = response.normalArrayData.arrRevPercentage
        cardData.arrRevSecond = response.normalArrayData.arrRevSecond
        cardData.arrSpeed = response.normalArrayData.arrSpeed.map { it.toDouble() }.toMutableList()
        cardData.arrCumDistance = response.cumArrayData.arrCumDistance
        cardData.arrCumElevation = response.cumArrayData.arrCumElevation
        cardData.arrCumSpeed = response.cumArrayData.arrCumSpeed

        cardData.arrAvgCadence = mutableListOf(0.0)
        cardData.arrAvgHr = mutableListOf(0)
        cardData.arrAvgRevPercentage =  mutableListOf(0)
        cardData.arrMaxCadence =  mutableListOf(0)
        cardData.arrMaxHr =  mutableListOf(0)
        cardData.arrMaxRevPercentage =  mutableListOf(0.0)

        cardData.speedForOneKm = response.singleValueData.speedForOneKm
        cardData.speedForOneMile = response.singleValueData.speedForOneMile

        val arrDataLocation = mutableListOf<RLElevationPoint>()
        val arrLocationDetails = mutableListOf<RLLocationDetails>()

        response.dataForDetails.latLongDic.forEach { entry ->
            val elevation = (entry["elevation"] as? Number)?.toDouble() ?: 0.0
            val latitude = (entry["lat"] as? Number)?.toDouble() ?: 0.0
            val longitude = (entry["long"] as? Number)?.toDouble() ?: 0.0
            val speed = (entry["speed"] as? Number)?.toDouble() ?: 0.0
            val state = (entry["state"] as? Number)?.toInt() ?: 0

            arrDataLocation.add(RLElevationPoint(elevation, latitude, longitude))
            arrLocationDetails.add(RLLocationDetails(speed, speed, latitude, state, longitude, elevation))
        }

        cardData.arrDataLocation = arrDataLocation
        cardData.arrLocationDetails = arrLocationDetails

        val zoneDataSummery: Map<String, RLZoneDataSummery> =  response.zoneData.mapValues { (_, detail) ->
            RLZoneDataSummery(
                avgCadence = 0.0,
                avgHr = 0,
                avgPower = 0,
                avgPowerFromDevice = 0,
                avgSpeed = 0.0,
                burntCalories =  RLYourWayCalvulation.noNanValueDouble(detail.burntCalories?:0.0),
                distance =  RLYourWayCalvulation.noNanValueDouble(detail.distance?:0.0),
                remark = detail.remark,
                seconds = detail.seconds,
                totalRev =  RLYourWayCalvulation.noNanValueDouble(detail.totalRev?:0.0)
            )
        }

        cardData.zoneDataSummery = zoneDataSummery
        cardData.zoneDataDetail =  response.zoneData

        cardData.wsWeight = userData.weightkg
        cardData.wsHeight = userData.height
        cardData.wsAge = RLTools.rl_calculateAge(userData.dob)
        cardData.gender = userData.gender
        cardData.RFMHR = userData.RFMHR
        cardData.RestingHR = userData.restingHr
        cardData.appUnit = userData.appUnit?:"Imperial"
        cardData.emailId = userData.emailId
        cardData.isBasicDataAdded = userData.isBasicDataAdded
        cardData.visibilityflagforthatsession = userData.visibilityflagforthatsession
        cardData.mapGeneratedUrl = mapGeneratedUrl

       //finish here
        makeSensorData(cardData,currentTimestamp)
    }

    private fun makeSensorData(cardData: RLSessionDataTransferModelNew, currentTimestamp:String) {

        val remark ="android"
        val className ="${cardData.yourWayType} Session"

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
            RevoolaKeys.className to className,
            RevoolaKeys.classNote to "",
            RevoolaKeys.classType to cardData.yourWayType,
            RevoolaKeys.elevationDic to cardData.arrElevation.toString(), // Convert to JSON string format
            RevoolaKeys.locationDic to convertLocationDetailsToString(cardData.arrLocationDetails), // Convert to JSON string format
            RevoolaKeys.speedDic to cardData.arrSpeed.toString(), // Convert to JSON string format
            RevoolaKeys.totalRev to cardData.totalRev
        )

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
            RevoolaKeys.displayImage to "",
            RevoolaKeys.displayName to "",
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
            RevoolaKeys.visibilityflagforthatsession to 0
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
            RevoolaKeys.className to  className,
            RevoolaKeys.classNote to  "",
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
            RevoolaKeys.visibilityflagforthatsession to  0,


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
               // RevoolaKeys.arrHRRecordedSecond to  cardData.arrHRRecordedSecond,
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
                RevoolaKeys.className to  className,
                RevoolaKeys.classNote to  "",
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
        firebaseEntry(dataForTestingDataMap,ghostDataMap,summaryDataMap,detailsDataMap,graphDataMapHeart,cardData,currentTimestamp)

    }

    private fun convertLocationDetailsToString(locationDetails: List<RLLocationDetails>): String {
        val locationStrings = locationDetails.map { location ->
            "\"lat\": ${location.lat}, \"speed\": ${location.speed}, \"state\": ${location.state}, \"long\": ${location.long}, \"elevation\": ${location.elevation}"
        }

        return "[${locationStrings.joinToString(", ") { "[$it]" }}]"
    }

    private fun firebaseEntry(dataForTestingDataMap : HashMap<String, Any>,
                                ghostDataMap: HashMap<String, Any>, summaryDataMap : HashMap<String, Serializable?>, detailsDataMap: HashMap<String, Any?>,
                                graphDataMap: HashMap<String, Any>, cardData: RLSessionDataTransferModelNew, currentTimestamp: String) {


        // Writing DataForTesting Data to Firebase
        RLDatabaseManagerWrite().rl_write_Data_For_Testing_Data(RevoolaFirebasePath.dataForTestingDataPath(currentUser), dataForTestingDataMap) { success, error ->
            if (success) {
                RLTools.rl_logDPrint(TAG, "Successful DataForTesting Entry")
            } else {
                RLTools.rl_logEPrint(TAG, "Error DataForTesting Entry:- $error")
            }
        }

        val justRide_ = cardData.yourWayType + "_justRide_"

        //Entry GhostData lastForClass Walk_justRide_
        val databaseRefGhostLast = FirebaseDatabase.getInstance()
            .getReference(RevoolaFirebasePath.ghostLastForClassDataPath(currentUser))
        databaseRefGhostLast.child(justRide_).setValue(ghostDataMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    RLTools.rl_logDPrint(TAG, "Entry  GhostData LastForClass saved successfully!")

                } else {
                    RLTools.rl_logEPrint(
                        TAG,
                        "Failed  GhostData LastForClass to save entry :- ${task.exception}"
                    )
                }
            }

        //Entry GhostData bestForClass
        val databaseRefGhostBest = FirebaseDatabase.getInstance()
            .getReference(RevoolaFirebasePath.ghostBestForClassDataPath(currentUser))
        databaseRefGhostBest.child(justRide_).setValue(ghostDataMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    RLTools.rl_logDPrint(TAG, "Entry  GhostData bestForClass saved successfully!")

                } else {
                    RLTools.rl_logEPrint(
                        TAG,
                        "Failed  GhostData bestForClass to save entry :- ${task.exception}"
                    )
                }
            }

        //Entry Summery
        val databaseRefSummery = FirebaseDatabase.getInstance()
            .getReference(RevoolaFirebasePath.summaryDataPath(currentUser))
        val entryIdSummery = currentTimestamp
        entryIdSummery.let {
            databaseRefSummery.child(it).setValue(summaryDataMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLTools.rl_logDPrint(
                            TAG,
                            "Entry saved successfully! revoolaUserSessionSummaryData"
                        )

                    } else {
                        RLTools.rl_logEPrint(
                            TAG,
                            "Failed to save entry revoolaUserSessionSummaryData :- ${task.exception}"
                        )
                    }
                }

            //entry Graph Data
            val databaseRefGraph = FirebaseDatabase.getInstance()
                .getReference(RevoolaFirebasePath.graphDataPath(currentUser))
            val entryIdGraph = currentTimestamp
            entryIdGraph.let {
                databaseRefGraph.child(it).setValue(graphDataMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            RLTools.rl_logDPrint(
                                TAG,
                                "Entry saved successfully! revoolaUserSessionSummaryGraphData"
                            )
                        } else {
                            RLTools.rl_logEPrint(
                                TAG,
                                "Failed to save entry revoolaUserSessionSummaryGraphData :- ${task.exception}"
                            )
                        }
                    }
            }

            //entry session detail data
            val databaseRef = FirebaseDatabase.getInstance()
                .getReference(RevoolaFirebasePath.detailDataPath(currentUser))
            val entryId = currentTimestamp
            entryId.let {
                databaseRef.child(it).setValue(detailsDataMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) { // 2. Insert API
                            rl_insertApiCall(cardData, currentTimestamp)
                            RLTools.rl_logDPrint(
                                TAG,
                                "Entry saved successfully revoolaUserSessionDetailData!"
                            )
                        } else {
                            RLTools.rl_logEPrint(
                                TAG,
                                "Failed to save entry revoolaUserSessionDetailData :- ${task.exception}"
                            )
                            rl_insertApiCall(cardData, currentTimestamp)
                        }
                    }
            }

        }
    }

    private fun createPayload(cardData: RLSessionDataTransferModelNew, currentTimestamp: String): String {
        val classLeaderboard = RLClassLeaderboard(
            userId = currentUser,
            classId = "",
            timestamp = currentTimestamp,
            timestampLocal = currentTimestamp,
            totalRev = cardData.totalRev,
            visibilityFlagForThatSession = 0,
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
    private fun rl_insertApiCall(cardData: RLSessionDataTransferModelNew, currentTimestamp: String) {
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)

        if (apiClientRetrofit.rl_isConnected()) {
            val jsonPayload = createPayload(cardData,currentTimestamp)
            val request = Gson().fromJson(jsonPayload, Array<RLYourWayApiPayload>::class.java).toList()

            RLTools.rl_logDPrint(TAG,"YourWay Insert Request: $request")
            //Insert Api Call
            userRepository.rl_insertYourWayData(request) { result ->
                result.onSuccess { response ->
                    try {
                        if (response.type.equals("success")) {
                            rl_insertOverviewApiCall(cardData,currentTimestamp)
                            RLTools.rl_logDPrint(TAG, "YourWay Insert Success: ${response.text}")
                        } else {
                            rl_insertOverviewApiCall(cardData,currentTimestamp)
                            RLTools.rl_logEPrint(TAG, "YourWay Insert Fail: ${response.text}")
                        }
                    } catch (e: Exception) {
                        rl_insertOverviewApiCall(cardData,currentTimestamp)
                        e.printStackTrace()
                        RLTools.rl_logEPrint(TAG, "YourWay Insert Catch: ${e.message}" )

                    }
                }.onFailure { error ->
                    rl_insertOverviewApiCall(cardData,currentTimestamp)
                    RLTools.rl_logEPrint(TAG, "YourWay Insert Error: ${error.localizedMessage}" )
                }
            }
        }

    }

    private fun createRequestBody(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }
    private fun createOverviewPayloadNew(cardData: RLSessionDataTransferModelNew, currentTimestamp: String): Map<String, RequestBody> {
        val requestBodyMap = mutableMapOf<String, RequestBody>()
        val className ="${cardData.yourWayType} Session"
        // hrm -> 1 (is hr sensor is connected), 0 (if not connected)
        // Add text fields as form data
        requestBodyMap["data[myOverviewThumbnails][userid]"] = createRequestBody(currentUser)
        requestBodyMap["data[myOverviewThumbnails][className]"] = createRequestBody(className)
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
        requestBodyMap["data[myOverviewThumbnails][visibilityflagforthatsession]"] = createRequestBody("0")
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
        requestBodyMap["data[myOverviewThumbnails][share_map]"] = createRequestBody("1")
        requestBodyMap["data[myOverviewThumbnails][from_third_party_source]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][map_url]"] = createRequestBody("")
        requestBodyMap["data[myOverviewThumbnails][mhr]"] = createRequestBody(safeIntNumber(cardData.maxHeartRate).toString())
        requestBodyMap["data[myOverviewThumbnails][rhr]"] =  createRequestBody(cardData.RestingHR)

        requestBodyMap["data[myOverviewThumbnails][avg_hr]"] = createRequestBody(safeIntNumber(cardData.avgHr).toString())
        requestBodyMap["data[myOverviewThumbnails][notes]"] = createRequestBody("")
        requestBodyMap["data[myOverviewThumbnails][source]"] = createRequestBody("android")


        return requestBodyMap
    }
    private fun rl_insertOverviewApiCall(cardData: RLSessionDataTransferModelNew, currentTimestamp: String) {
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        if (apiClientRetrofit.rl_isConnected()) {
            val dataMap  = createOverviewPayloadNew(cardData,currentTimestamp)
            val imageParts=getUserImages()
           // val imageParts = mutableListOf<MultipartBody.Part>()
            RLTools.rl_logDPrint(TAG,"Overview Insert Request: $dataMap")
            //Insert Api Call
            userRepository.rl_insertYourWayOverviewData(dataMap,imageParts) { result ->
                result.onSuccess { response ->
                    try {
                        if (response.type.equals("success")) {
                            RLTools.rl_logDPrint(TAG, "Overview Insert Success: ${response.text}")
                            rl_updateUserInsightlyMoengageApiCall(cardData,currentTimestamp)
                        } else {
                            RLTools.rl_logEPrint(TAG, "Overview Insert Fail: ${response.text}")
                            rl_updateUserInsightlyMoengageApiCall(cardData,currentTimestamp)
                        }
                    } catch (e: Exception) {
                        RLTools.rl_logEPrint(TAG, "Overview Insert Catch: ${e.message}" )
                        rl_updateUserInsightlyMoengageApiCall(cardData,currentTimestamp)
                    }
                }.onFailure { error ->
                    RLTools.rl_logEPrint(TAG, "Overview Insert Error: ${error.message}" )
                    rl_updateUserInsightlyMoengageApiCall(cardData,currentTimestamp)
                }
            }
        }

    }
    private fun getUserImages(): List<MultipartBody.Part> {
        val imageParts = mutableListOf<MultipartBody.Part>()
        if (mapBitmapImage!=null){
            val imageFile = rl_saveBitmapToFile(mapBitmapImage!!)
            if (imageFile!=null){
                val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("mapImage", imageFile.name, requestFile)
                imageParts.add(imagePart)
               // mapImageUri = Uri.fromFile(imageFile)
                return imageParts
            }else{
                return imageParts
            }
        }else{
            return imageParts
        }
    }
    private fun rl_saveBitmapToFile(bitmap: Bitmap): File? {
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
            RLTools.rl_logEPrint("CAMERAIMAGHE","ERROR=="+e.localizedMessage)
            return null
        }
    }

    private fun rl_updateUserInsightlyMoengageApiCall(cardData: RLSessionDataTransferModelNew, currentTimestamp:String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestApi = RLInsightlyMoengageApiPayload(
                    email=cardData.emailId,
                    uid= currentUser,
                    device_type= "Android",
                    Is_basic_data_added= cardData.isBasicDataAdded,
                    your_way= RLTools.rl_getCurrentISO8601())

                RLTools.rl_logDPrint(TAG, "Insightly Moengage requestApi: $requestApi")

                val client = OkHttpClient()
                val mediaType = "application/json".toMediaType()
                val body = Gson().toJson(requestApi).toRequestBody(mediaType)
                val request = Request.Builder()
                    .url("https://us-central1-rideathome-9080e.cloudfunctions.net/moengage-updateAccount")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                // Log response on background thread
                RLTools.rl_logDPrint(TAG, "Insightly Moengage Response: $responseBody")
                val apiResponse = Gson().fromJson(responseBody, RLInsightlyMoEngageResponse::class.java)
                // If UI update needed, switch to Main Thread
                CoroutineScope(Dispatchers.Main).launch {
                    if (apiResponse.response.isNotEmpty() && apiResponse.response[0].success == "true") {
                        // Show success message in UI
                        // Handle UI updates if required (e.g., Toast message)
                        RLTools.rl_logDPrint(TAG, "Insightly Moengage Insert Success: ${response}")
                        rl_allProcessDone(currentTimestamp)
                    }else{
                        RLTools.rl_logEPrint(TAG, "Moengage Error: ${apiResponse.response[0].status}")
                        rl_allProcessDone(currentTimestamp)
                    }

                }

            } catch (e: Exception) {
                RLTools.rl_logEPrint(TAG, "Insightly Moengage Error: ${e.localizedMessage}")
                rl_allProcessDone(currentTimestamp)
            }
        }
    }

    private fun safeNumber(value: Double?): Double {
        return if (value == null || value.isNaN() || value.isInfinite()) 0.0 else value
    }

    private fun safeIntNumber(value: Int?): Int {
        return if (value == null || value < 0 ) 0 else value
    }

    private fun rl_allProcessDone(currentTimestamp:String){
        val path = "/proposedstructure/revoolaUserSessionDetailDataWatch/$currentUser/$currentTimestamp"
        databaseRead.deleteFirebaseData(path){success,error->
            if (success!=null){
                Log.d(TAG,"Success:- $success")
            }else{
                Log.e(TAG,"Error:- ${error?.localizedMessage}")
            }

        }
    }

}
