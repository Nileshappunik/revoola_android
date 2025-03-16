package com.revoola.fragment.start.yourway

import android.graphics.Bitmap
import com.revoola.databasefirebase.RLZoneDataDetails
import com.revoola.databasefirebase.RLZoneDataSummery
import com.revoola.firebaseModel.RLElevationPoint
import com.revoola.firebaseModel.RLLocationDetails
import com.revoola.model.RLRevoolaUsersSettingsModel
import java.io.Serializable

class RLSessionDataTransferModel : Serializable {
    var yourWayType: String =""
    var totalTime: String ="0"
    var gpxStringBuilder: String =""
    var gpxTServerNString: String =""
    var gpxTServerString: String =""
    var SENSOR: String =""

    var generatedDistance: Double = 0.0
    var generatedElevation: Int = -1


    var wsWeight="60"
    var wsHeight="167"
    var wsAge=25
    var gender="Male"
    var RFMHR=191
    var RestingHR="50"
    var appUnit=""
    var emailId=""
    var isBasicDataAdded=true
     var displayImage = ""
     var displayName = ""
     var  joiningDate: Long = 0

    var VIDEODATA=""
    var classType=""
    var videoID: String =""
    var mapGeneratedUrl: String =""
    var mapBitmapImage: Bitmap?=null
    //var CLASS_TYPE: String =""
    var avgHr: Int =0
    var rms: Double =0.0

    var avgRevPercentage: Double = 0.0
    var burntCalories: Double = 0.0
    var distance: Double = 0.0
    var hrm: Int = 0
    var maxRevPercentage: Double = 0.0
    var minRevPercentage: Double = 0.0
    var revPercentage: Double = 0.0
    var totalElevation: Double = 0.0
    var totalRev: Double = 0.0


    var totalSteps: Int = 0
    var maxSpeed: Int = 0
    var maxHeartRate: Int = 0
    var maxCadence: Int = 0
    var avgBurntCalories: Double = 0.0
    var maxBurntCalories: Int = 0
    var minHeartRate: Int = 0

    var avgCadence: Double = 0.0
    var avgSpeed: Double = 0.0

    var maxSpeedForOneKm:  Double = 0.0
    var maxSpeedForOneMile:  Double = 0.0
    var avgSpeedForOneKm:  Double = 0.0
    var avgSpeedForOneMile:  Double = 0.0

    var demsElevation:  Int = -1

    var arrConnection: MutableList<Boolean> = mutableListOf()
    var arrBurntCalories:MutableList<Double> = mutableListOf()
    var arrCadence:MutableList<Double> = mutableListOf()
    var arrDistance:MutableList<Double> = mutableListOf()
    var arrElevation:MutableList<Double> = mutableListOf()
    var arrHRRecordedSecond:MutableList<Int> = mutableListOf()
    var arrHr:MutableList<Int> = mutableListOf()
    var arrPower:MutableList<Int> = mutableListOf()
    var arrPowerFromDevice:MutableList<Int> = mutableListOf()
    var arrRevPercentage:MutableList<Double> = mutableListOf()
    var arrRevSecond:MutableList<Double> = mutableListOf()
    var arrSpeed:MutableList<Double> = mutableListOf()
    var arrCumDistance:MutableList<Double> = mutableListOf()
    var arrCumElevation:MutableList<Double> = mutableListOf()
    var arrCumSpeed:MutableList<Double> = mutableListOf()

    var arrAvgCadence:MutableList<Double> = mutableListOf()
    var arrAvgHr:MutableList<Int> = mutableListOf()
    var arrAvgRevPercentage:MutableList<Int> = mutableListOf()
    var arrMaxCadence:MutableList<Int> = mutableListOf()
    var arrMaxHr:MutableList<Int> = mutableListOf()
    var arrMaxRevPercentage:MutableList<Double> = mutableListOf()

     var speedForOneKm:MutableList<Double> = mutableListOf()
     var speedForOneMile:MutableList<Double> = mutableListOf()

     var arrDataLocation:MutableList<RLElevationPoint> = mutableListOf()
     var arrLocationDetails:MutableList<RLLocationDetails> = mutableListOf()

    var zoneDataSummery: Map<String, RLZoneDataSummery> = mapOf()
    var zoneDataDetail: Map<String, RLZoneDataDetails> = mapOf()

}

