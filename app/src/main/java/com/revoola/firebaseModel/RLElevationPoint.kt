package com.revoola.firebaseModel

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
         return 0

     }

     override fun writeToParcel(parcel: Parcel, p1: Int) {
         parcel.writeDouble(elevation)
         parcel.writeDouble(latitude)
         parcel.writeDouble(longitude)
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




