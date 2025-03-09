package com.revoola.fragment.friends.model

import com.revoola.model.RLUserDataParcelable
import java.io.Serializable
import android.os.Parcel
import android.os.Parcelable

data class RLCreateGroupModel(
    var selectFriendList: List<RLUserDataParcelable> = mutableListOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(RLUserDataParcelable) ?: mutableListOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(selectFriendList)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<RLCreateGroupModel> {
        override fun createFromParcel(parcel: Parcel): RLCreateGroupModel = RLCreateGroupModel(parcel)
        override fun newArray(size: Int): Array<RLCreateGroupModel?> = arrayOfNulls(size)
    }
}
