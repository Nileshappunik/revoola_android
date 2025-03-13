package com.revoola.fragment.start.yourway

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import com.revoola.databasefirebase.RLZoneDataDetails
import com.revoola.databasefirebase.RLZoneDataSummery
import com.revoola.firebaseModel.RLElevationPoint
import com.revoola.firebaseModel.RLLocationDetails


class RLSessionDataTransferModelNew(
    var yourWayType: String = "",
    var totalTime: String = "0",
    var gpxStringBuilder: String = "",
    var gpxTServerNString: String = "",
    var gpxTServerString: String = "",
    var SENSOR: String = "",
    var generatedDistance: Double = 0.0,
    var generatedElevation: Int = -1,
    var wsWeight: String = "60",
    var wsHeight: String = "167",
    var wsAge: Int = 25,
    var gender: String = "Male",
    var RFMHR: Int = 191,
    var RestingHR: String = "50",
    var appUnit: String = "",
    var emailId: String = "",
    var isBasicDataAdded: Boolean = true,
    var displayImage: String = "",
    var displayName: String = "",
    var joiningDate: Long = 0,
    var VIDEODATA: String = "",
    var classType: String = "",
    var videoID: String = "",
    var mapGeneratedUrl: String = "",
    var avgHr: Int = 0,
    var rms: Double = 0.0,
    var avgRevPercentage: Double = 0.0,
    var burntCalories: Double = 0.0,
    var distance: Double = 0.0,
    var maxRevPercentage: Double = 0.0,
    var minRevPercentage: Double = 0.0,
    var revPercentage: Double = 0.0,
    var totalElevation: Double = 0.0,
    var totalRev: Double = 0.0,
    var totalSteps: Int = 0,
    var maxSpeed: Int = 0,
    var maxHeartRate: Int = 0,
    var maxCadence: Int = 0,
    var avgBurntCalories: Double = 0.0,
    var maxBurntCalories: Int = 0,
    var minHeartRate: Int = 0,
    var avgCadence: Double = 0.0,
    var avgSpeed: Double = 0.0,
    var maxSpeedForOneKm: Double = 0.0,
    var maxSpeedForOneMile: Double = 0.0,
    var avgSpeedForOneKm: Double = 0.0,
    var avgSpeedForOneMile: Double = 0.0,
    var demsElevation: Int = -1,
    var arrConnection: MutableList<Boolean> = mutableListOf(),
    var arrBurntCalories: MutableList<Double> = mutableListOf(),
    var arrCadence: MutableList<Double> = mutableListOf(),
    var arrDistance: MutableList<Double> = mutableListOf(),
    var arrElevation: MutableList<Double> = mutableListOf(),
    var arrHRRecordedSecond: MutableList<Int> = mutableListOf(),
    var arrHr: MutableList<Int> = mutableListOf(),
    var arrPower: MutableList<Int> = mutableListOf(),
    var arrPowerFromDevice: MutableList<Int> = mutableListOf(),
    var arrRevPercentage: MutableList<Double> = mutableListOf(),
    var arrRevSecond: MutableList<Double> = mutableListOf(),
    var arrSpeed: MutableList<Double> = mutableListOf(),
    var arrCumDistance: MutableList<Double> = mutableListOf(),
    var arrCumElevation: MutableList<Double> = mutableListOf(),
    var arrCumSpeed: MutableList<Double> = mutableListOf(),
    var arrAvgCadence: MutableList<Double> = mutableListOf(),
    var arrAvgHr: MutableList<Int> = mutableListOf(),
    var arrAvgRevPercentage: MutableList<Int> = mutableListOf(),
    var arrMaxCadence: MutableList<Int> = mutableListOf(),
    var arrMaxHr: MutableList<Int> = mutableListOf(),
    var arrMaxRevPercentage: MutableList<Double> = mutableListOf(),
    var speedForOneKm: MutableList<Double> = mutableListOf(),
    var speedForOneMile: MutableList<Double> = mutableListOf(),
    var arrDataLocation: MutableList<RLElevationPoint> = mutableListOf(),
    var arrLocationDetails: MutableList<RLLocationDetails> = mutableListOf(),
    var zoneDataSummery: Map<String, RLZoneDataSummery> = mapOf(),
    var zoneDataDetail: Map<String, RLZoneDataDetails> = mapOf(),
    var hrm:Int =0
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "0",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString() ?: "60",
        parcel.readString() ?: "167",
        parcel.readInt(),
        parcel.readString() ?: "Male",
        parcel.readInt(),
        parcel.readString() ?: "50",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.createBooleanArray()?.toMutableList() ?: mutableListOf(),
        parcel.createDoubleArray()?.toMutableList() ?: mutableListOf(),
        parcel.createDoubleArray()?.toMutableList() ?: mutableListOf(),
        parcel.createDoubleArray()?.toMutableList() ?: mutableListOf(),
        parcel.createDoubleArray()?.toMutableList() ?: mutableListOf(),
        parcel.createIntArray()?.toMutableList() ?: mutableListOf(),
        parcel.createIntArray()?.toMutableList() ?: mutableListOf(),
        parcel.createIntArray()?.toMutableList() ?: mutableListOf(),
        parcel.createIntArray()?.toMutableList() ?: mutableListOf(),
        parcel.createDoubleArray()?.toMutableList() ?: mutableListOf(),
        parcel.createDoubleArray()?.toMutableList() ?: mutableListOf(),
        parcel.createDoubleArray()?.toMutableList() ?: mutableListOf(),
        parcel.createDoubleArray()?.toMutableList() ?: mutableListOf(),
        parcel.createDoubleArray()?.toMutableList() ?: mutableListOf(),
        parcel.createDoubleArray()?.toMutableList() ?: mutableListOf(),
        parcel.createDoubleArray()?.toMutableList() ?: mutableListOf(),
        parcel.createIntArray()?.toMutableList() ?: mutableListOf(),
        parcel.createIntArray()?.toMutableList() ?: mutableListOf(),
        parcel.createIntArray()?.toMutableList() ?: mutableListOf(),
        parcel.createIntArray()?.toMutableList() ?: mutableListOf(),
        parcel.createDoubleArray()?.toMutableList() ?: mutableListOf(),
        parcel.createDoubleArray()?.toMutableList() ?: mutableListOf(),
        parcel.createDoubleArray()?.toMutableList() ?: mutableListOf(),
        mutableListOf<RLElevationPoint>().apply { parcel.readTypedList(this, RLElevationPoint.CREATOR) },
        mutableListOf<RLLocationDetails>().apply { parcel.readTypedList(this, RLLocationDetails.CREATOR) },
        parcel.readHashMap(RLZoneDataSummery::class.java.classLoader) as Map<String, RLZoneDataSummery>,
        parcel.readHashMap(RLZoneDataDetails::class.java.classLoader) as Map<String, RLZoneDataDetails>,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(yourWayType)
        parcel.writeString(totalTime)
        parcel.writeString(gpxStringBuilder)
        parcel.writeString(gpxTServerNString)
        parcel.writeString(gpxTServerString)
        parcel.writeString(SENSOR)
        parcel.writeDouble(generatedDistance)
        parcel.writeInt(generatedElevation)
        parcel.writeString(wsWeight)
        parcel.writeString(wsHeight)
        parcel.writeInt(wsAge)
        parcel.writeString(gender)
        parcel.writeInt(RFMHR)
        parcel.writeString(RestingHR)
        parcel.writeString(appUnit)
        parcel.writeString(emailId)
        parcel.writeByte(if (isBasicDataAdded) 1 else 0)
        parcel.writeString(displayImage)
        parcel.writeString(displayName)
        parcel.writeLong(joiningDate)
        parcel.writeString(VIDEODATA)
        parcel.writeString(classType)
        parcel.writeString(videoID)
        parcel.writeString(mapGeneratedUrl)
        parcel.writeInt(avgHr)
        parcel.writeDouble(rms)
        parcel.writeDouble(avgRevPercentage)
        parcel.writeDouble(burntCalories)
        parcel.writeDouble(distance)
        parcel.writeDouble(maxRevPercentage)
        parcel.writeDouble(minRevPercentage)
        parcel.writeDouble(revPercentage)
        parcel.writeDouble(totalElevation)
        parcel.writeDouble(totalRev)
        parcel.writeInt(totalSteps)
        parcel.writeInt(maxSpeed)
        parcel.writeInt(maxHeartRate)
        parcel.writeInt(maxCadence)
        parcel.writeDouble(avgBurntCalories)
        parcel.writeInt(maxBurntCalories)
        parcel.writeInt(minHeartRate)
        parcel.writeDouble(avgCadence)
        parcel.writeDouble(avgSpeed)
        parcel.writeDouble(maxSpeedForOneKm)
        parcel.writeDouble(maxSpeedForOneMile)
        parcel.writeDouble(avgSpeedForOneKm)
        parcel.writeDouble(avgSpeedForOneMile)
        parcel.writeInt(demsElevation)
        parcel.writeBooleanArray(arrConnection.toBooleanArray())
        parcel.writeDoubleArray(arrBurntCalories.toDoubleArray())
        parcel.writeDoubleArray(arrCadence.toDoubleArray())
        parcel.writeDoubleArray(arrDistance.toDoubleArray())
        parcel.writeDoubleArray(arrElevation.toDoubleArray())
        parcel.writeIntArray(arrHRRecordedSecond.toIntArray())
        parcel.writeIntArray(arrHr.toIntArray())
        parcel.writeIntArray(arrPower.toIntArray())
        parcel.writeIntArray(arrPowerFromDevice.toIntArray())
        parcel.writeDoubleArray(arrRevPercentage.toDoubleArray())
        parcel.writeDoubleArray(arrRevSecond.toDoubleArray())
        parcel.writeDoubleArray(arrSpeed.toDoubleArray())
        parcel.writeDoubleArray(arrCumDistance.toDoubleArray())
        parcel.writeDoubleArray(arrCumElevation.toDoubleArray())
        parcel.writeDoubleArray(arrCumSpeed.toDoubleArray())
        parcel.writeDoubleArray(arrAvgCadence.toDoubleArray())
        parcel.writeIntArray(arrAvgHr.toIntArray())
        parcel.writeIntArray(arrAvgRevPercentage.toIntArray())
        parcel.writeIntArray(arrMaxCadence.toIntArray())
        parcel.writeIntArray(arrMaxHr.toIntArray())
        parcel.writeDoubleArray(arrMaxRevPercentage.toDoubleArray())
        parcel.writeDoubleArray(speedForOneKm.toDoubleArray())
        parcel.writeDoubleArray(speedForOneMile.toDoubleArray())
        parcel.writeTypedList(arrDataLocation)
        parcel.writeTypedList(arrLocationDetails)
        parcel.writeMap(zoneDataSummery)
        parcel.writeMap(zoneDataDetail)
        parcel.writeInt(hrm)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RLSessionDataTransferModelNew> {
        override fun createFromParcel(parcel: Parcel): RLSessionDataTransferModelNew {
            return RLSessionDataTransferModelNew(parcel)
        }

        override fun newArray(size: Int): Array<RLSessionDataTransferModelNew?> {
            return arrayOfNulls(size)
        }
    }
}


