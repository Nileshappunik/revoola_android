package com.example.myfirstapp.firebaseModel

import android.os.Parcel
import android.os.Parcelable

data class RLLocationDetails(
    val deviceSpeed: Double,
    val speed: Double,
    val lat: Double,
    val state: Double,
    val long: Double,
    val elevation: Double) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
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
