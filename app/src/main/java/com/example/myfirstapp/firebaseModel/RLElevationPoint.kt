package com.example.myfirstapp.firebaseModel

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable


 data class RLElevationPoint(val elevation: Double,
                             val latitude: Double,
                             val longitude: Double) : Parcelable {
     constructor(parcel: Parcel) : this(
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

     companion object CREATOR : Parcelable.Creator<RLElevationPoint> {
         override fun createFromParcel(parcel: Parcel): RLElevationPoint {
             return RLElevationPoint(parcel)
         }

         override fun newArray(size: Int): Array<RLElevationPoint?> {
             return arrayOfNulls(size)
         }
     }
 }




