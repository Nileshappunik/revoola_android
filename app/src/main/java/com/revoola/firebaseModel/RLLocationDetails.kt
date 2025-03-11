package com.revoola.firebaseModel

import android.os.Parcel
import android.os.Parcelable

data class RLLocationDetails(
    val deviceSpeed: Double,
    val speed: Double,
    val lat: Double,
    val state: Int,
    val long: Double,
    val elevation: Double) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeDouble(deviceSpeed)
        parcel.writeDouble(speed)
        parcel.writeDouble(lat)
        parcel.writeInt(state)
        parcel.writeDouble(long)
        parcel.writeDouble(elevation)
    }

    companion object CREATOR : Parcelable.Creator<RLLocationDetails> {
        override fun createFromParcel(parcel: Parcel): RLLocationDetails {
            return RLLocationDetails(parcel)
        }

        override fun newArray(size: Int): Array<RLLocationDetails?> {
            return arrayOfNulls(size)
        }
    }
}
