package com.revoola.databasefirebase

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.revoola.RLBaseProgress
import com.revoola.activity.RLMainActivityRL
import com.revoola.api.RLApiClientRet
import com.revoola.commonobject.RLTools
import com.revoola.commonobject.RLYourWayCalvulation
import com.revoola.firebaseModel.RLElevationPoint
import com.revoola.firebaseModel.RLLocationDetails
import com.revoola.fragment.feed.RLFragSessionSummary
import com.revoola.fragment.start.yourway.RLSessionDataTransferModelNew
import com.revoola.model.RLClassLeaderboard
import com.revoola.model.RLInsightlyMoEngageResponse
import com.revoola.model.RLInsightlyMoengageApiPayload
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.revoola.model.RLTextOverview
import com.revoola.model.RLYourWayApiPayload
import com.revoola.utils.RLConstants
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.Serializable
import kotlin.math.roundToInt

class RLFirebaseManager {
    private val TAG ="FirebaseManager"
    val databaseManager = RLDatabaseManagerWrite()
    val databaseRead = RLDatabaseManagerRead()
    private val currentUser = RLAuthManager().RlgetCurrentUser()?.uid ?: ""
    private lateinit var apiClientRetrofit: RLApiClientRet

     fun RLRevoolaUserSettingFirebaseEntry(userId:String,emailId:String,versionName:String,callback: (Boolean) -> Unit) {
        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()
        val currentSubscriptionMap = hashMapOf(
            "validDaysMonth" to 0,
            "inviteUserSubsModel" to "0",
            "referrerTag" to "Android",
            "permissionLevelAfterTrial" to "Free",
            "isTrialTaken" to true,
            "isSubscriptionRequired" to true,
            "commisionFlag" to "",
            "discountPeriodMonth" to 0,
            "remark" to "Android",
            "familyPrice" to 0,
            "onGoingPriceType" to "none",
            "onGoingPrice" to 0,
            "discountedPriceType" to "none",
            "discountedPrice" to 0,
            "isSubscriptionCheckRequired" to true,
            "subscriptionName" to "Trial-Premium",
            "inviteUserType" to "NormalUser",
            "plan" to "",
            "timestamp" to currentTimestamp,
            "validDays" to 14)

        val revoolaUserSettingsMap = hashMapOf(
            "FCMToken" to "",
            "RFMHR" to 196,
            "TMHR" to 196,
            "AMHR" to 196,
            "emailId" to emailId,
            "appUnit" to "Imperial",
            "currentGroup" to "freemium",
            "displayImage" to "none",
            "displayName" to "Guest",
            "dob" to "00/00/0000",
            "firstName" to "Guest",
            "flagImage" to "flag-of-United-Kingdom.png",
            "flagName" to "United Kingdom",
            "heartRate" to 0,
            "height" to "167",
            "heightUnit" to "FeetInch",
            "isBasicDataAdded" to false,
            "joiningDate" to currentTimestamp,//first time user create then date
            "lastHRChange" to 0,
            " lastHRChange90" to 0,
            " lastHRUsed" to 0,
            "lastLogin" to currentTimestamp,
            "lastName" to "",
            "gender" to "none",
            "lastVersion" to versionName,//current app version
            "leaderBoardImage" to "none",
            "currentSubscription" to currentSubscriptionMap,
            "location" to "United Kingdom",
            "numberOfGhost" to "1",
            "power" to 0,
            "referUser" to "AndroidPlayStore",
            "referalCode" to "",
            "remark" to "Android",
            "restingHr" to "60",
            "totalRev" to 0,
            "visibilityflagforthatsession" to 0,
            "weightUnit" to "Metric",
            "weightkg" to "77")
        databaseManager.REVOOLAUSERSETTINGSWrite(userId,revoolaUserSettingsMap) { success, error ->
            callback(success)
        }
    }

    fun readWatchData(userData: RLRevoolaUsersSettingsModel, context: Context) {
        val path = "/proposedstructure/revoolaUserSessionDetailDataWatch/$currentUser"
        databaseRead.RlreadData(path) { success, error ->
            if (success != null && success is Map<*, *>) {
                success.forEach { (key, value) ->
                    val sessionValueMap = value as? Map<String, Any>
                    sessionValueMap?.let {
                        val sessionKey = key.toString()
                        val jsonString = Gson().toJson(it)
                        Log.d(TAG,"sessionKey:- $sessionKey")
                        try {
                            val sessionData = Gson().fromJson(jsonString, RlWatchSessionDataClass::class.java)
                            // Api call
                            apiClientRetrofit = RLApiClientRet(context)
                            makeDataToFirebase(sessionData,userData,sessionKey)
                        }catch (e:Exception){
                            Log.d(TAG,"Exception:- ${e.localizedMessage}")
                            databaseManager.RlUpdateAllData("/proposedstructure/revoolaUserSessionDetailDataWatch/$currentUser/$sessionKey/singleValueData/speedForOneKm", listOf(0))
                            databaseManager.RlUpdateAllData("/proposedstructure/revoolaUserSessionDetailDataWatch/$currentUser/$sessionKey/singleValueData/speedForOneMile", listOf(0))
                        }

                    }
                }
            } else {
                Log.e(TAG, "Failed to read data or unexpected format: $error")
            }
        }
    }

    private fun makeDataToFirebase(response:RlWatchSessionDataClass,userData: RLRevoolaUsersSettingsModel,currentTimestamp:String) {

        val cardData = RLSessionDataTransferModelNew()

        cardData.yourWayType = response.singleValueData.classType
        cardData.totalTime = response.singleValueData.totalTime
        cardData.gpxStringBuilder = ""
        cardData.gpxTServerString= ""
        cardData.gpxTServerNString= ""

        cardData.SENSOR = RLConstants.HEART_SENSOR

        cardData.avgRevPercentage = RLYourWayCalvulation.noNanValueDouble(response.normalArrayData.arrRevPercentage.average()?:0.00)
        cardData.burntCalories = RLYourWayCalvulation.noNanValueDouble(response.singleValueData.burntCalories?:0.0)
        cardData.distance = RLYourWayCalvulation.noNanValueDouble(response.singleValueData.distance?:0.0)
        cardData.maxRevPercentage = RLYourWayCalvulation.noNanValueDouble(response.normalArrayData.arrRevPercentage.max()?:0.00?:0.00)
        cardData.minRevPercentage = RLYourWayCalvulation.noNanValueDouble(response.normalArrayData.arrRevPercentage.min()?:0.00?:0.00)
        cardData.revPercentage = RLYourWayCalvulation.noNanValueDouble(response.normalArrayData.arrRevPercentage.average()?:0.00)
        cardData.totalElevation = RLYourWayCalvulation.noNanValueDouble(response.singleValueData.totalElevation?:0.0)
        cardData.totalRev = RLYourWayCalvulation.noNanValueDouble(response.singleValueData.totalRev?:0.0)
        cardData.totalSteps = response.singleValueData.totalSteps
        cardData.maxSpeed =  response.normalArrayData.arrSpeed.max()?:0
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
        cardData.arrHRRecordedSecond =  mutableListOf(0)
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
        cardData.wsAge = RLTools.RLCalculateAge(userData.dob)
        cardData.gender = userData.gender
        cardData.RFMHR = userData.RFMHR
        cardData.RestingHR = userData.restingHr
        cardData.appUnit = userData.appUnit
        cardData.emailId = userData.emailId
        cardData.isBasicDataAdded = userData.isBasicDataAdded
        cardData.visibilityflagforthatsession = userData.visibilityflagforthatsession

       //finish here
        makeSensorData(cardData,currentTimestamp)
    }

    private fun makeSensorData(cardData: RLSessionDataTransferModelNew,currentTimestamp:String) {

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

    private fun firebaseEntry(dataForTestingDataMap : HashMap<String, Any>,
                                ghostDataMap: HashMap<String, Any>, summaryDataMap : HashMap<String, Serializable?>, detailsDataMap: HashMap<String, Any?>,
                                graphDataMap: HashMap<String, Any>, cardData: RLSessionDataTransferModelNew, currentTimestamp: String) {


        // Writing DataForTesting Data to Firebase
        RLDatabaseManagerWrite().RlWriteDataForTestingData(
            RevoolaFirebasePath.dataForTestingDataPath(
                currentUser
            ), dataForTestingDataMap
        ) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG, "Successful DataForTesting Entry")
            } else {
                RLTools.RlLogEPrint(TAG, "Error DataForTesting Entry:- $error")
            }
        }

        val justRide_ = cardData.yourWayType + "_justRide_"

        //Entry GhostData lastForClass Walk_justRide_
        val databaseRefGhostLast = FirebaseDatabase.getInstance()
            .getReference(RevoolaFirebasePath.ghostLastForClassDataPath(currentUser))
        databaseRefGhostLast.child(justRide_).setValue(ghostDataMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    RLTools.RlLogDPrint(TAG, "Entry  GhostData LastForClass saved successfully!")

                } else {
                    RLTools.RlLogEPrint(
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
                    RLTools.RlLogDPrint(TAG, "Entry  GhostData bestForClass saved successfully!")

                } else {
                    RLTools.RlLogEPrint(
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
                        RLTools.RlLogDPrint(
                            TAG,
                            "Entry saved successfully! revoolaUserSessionSummaryData"
                        )

                    } else {
                        RLTools.RlLogEPrint(
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
                            RLTools.RlLogDPrint(
                                TAG,
                                "Entry saved successfully! revoolaUserSessionSummaryGraphData"
                            )
                        } else {
                            RLTools.RlLogEPrint(
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
                            RLInsertApiCall(cardData, currentTimestamp)
                            RLTools.RlLogDPrint(
                                TAG,
                                "Entry saved successfully revoolaUserSessionDetailData!"
                            )
                        } else {
                            RLTools.RlLogEPrint(
                                TAG,
                                "Failed to save entry revoolaUserSessionDetailData :- ${task.exception}"
                            )
                            RLInsertApiCall(cardData, currentTimestamp)
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
    private fun RLInsertApiCall(cardData: RLSessionDataTransferModelNew, currentTimestamp: String) {
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)

        if (apiClientRetrofit.RLisConnected()) {
            val jsonPayload = createPayload(cardData,currentTimestamp)
            val request = Gson().fromJson(jsonPayload, Array<RLYourWayApiPayload>::class.java).toList()

            RLTools.RlLogDPrint(TAG,"YourWay Insert Request: $request")
            //Insert Api Call
            userRepository.RLInsertYourWayData(request) { result ->
                result.onSuccess { response ->
                    try {
                        if (response.type.equals("success")) {
                            RLInsertOverviewApiCall(cardData,currentTimestamp)
                            RLTools.RlLogDPrint(TAG, "YourWay Insert Success: ${response.text}")
                        } else {
                            RLInsertOverviewApiCall(cardData,currentTimestamp)
                            RLTools.RlLogEPrint(TAG, "YourWay Insert Fail: ${response.text}")
                        }
                    } catch (e: Exception) {
                        RLInsertOverviewApiCall(cardData,currentTimestamp)
                        e.printStackTrace()
                        RLTools.RlLogEPrint(TAG, "YourWay Insert Catch: ${e.message}" )

                    }
                }.onFailure { error ->
                    RLInsertOverviewApiCall(cardData,currentTimestamp)
                    RLTools.RlLogEPrint(TAG, "YourWay Insert Error: ${error.localizedMessage}" )
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
    private fun RLInsertOverviewApiCall(cardData: RLSessionDataTransferModelNew, currentTimestamp: String) {
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        if (apiClientRetrofit.RLisConnected()) {
            val dataMap  = createOverviewPayloadNew(cardData,currentTimestamp)
            val imageParts = mutableListOf<MultipartBody.Part>()
            RLTools.RlLogDPrint(TAG,"Overview Insert Request: $dataMap")
            //Insert Api Call
            userRepository.RLInsertYourWayOverviewData(dataMap,imageParts) { result ->
                result.onSuccess { response ->
                    try {
                        if (response.type.equals("success")) {
                            RLTools.RlLogDPrint(TAG, "Overview Insert Success: ${response.text}")
                            RLupdateUserInsightlyMoengageApiCall(cardData,currentTimestamp)
                        } else {
                            RLTools.RlLogEPrint(TAG, "Overview Insert Fail: ${response.text}")
                            RLupdateUserInsightlyMoengageApiCall(cardData,currentTimestamp)
                        }
                    } catch (e: Exception) {
                        RLTools.RlLogEPrint(TAG, "Overview Insert Catch: ${e.message}" )
                        RLupdateUserInsightlyMoengageApiCall(cardData,currentTimestamp)
                    }
                }.onFailure { error ->
                    RLTools.RlLogEPrint(TAG, "Overview Insert Error: ${error.message}" )
                    RLupdateUserInsightlyMoengageApiCall(cardData,currentTimestamp)
                }
            }
        }

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
                        RLTools.RlLogDPrint(TAG, "Insightly Moengage Insert Success: ${response}")
                        RLAllProcessDone(currentTimestamp)
                    }else{
                        RLTools.RlLogEPrint(TAG, "Moengage Error: ${apiResponse.response[0].status}")
                        RLAllProcessDone(currentTimestamp)
                    }

                }

            } catch (e: Exception) {
                RLTools.RlLogEPrint(TAG, "Insightly Moengage Error: ${e.localizedMessage}")
                RLAllProcessDone(currentTimestamp)
            }
        }
    }

    private fun safeNumber(value: Double?): Double {
        return if (value == null || value.isNaN() || value.isInfinite()) 0.0 else value
    }

    private fun safeIntNumber(value: Int?): Int {
        return if (value == null || value < 0 ) 0 else value
    }

    private fun RLAllProcessDone(currentTimestamp:String){
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