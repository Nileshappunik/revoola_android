package com.revoola.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class RLYourFriendsModel(val type:String, val text: RLuser)

data class RLuser(val user:List<RLuserData>)
@Parcelize
data class RLuserData(val first_name:String, val last_name:String, val userid:String, val username:String,
                      val avatar:String, val myid:String, val myidstatus:String, val theirid:String,
                      val theiridstatus:String, var isSelected: Boolean = false) : Parcelable


data class RLSetsearch_userrequest(var search_user: RLSetsearch_user)
data class RLSetsearch_user(var get_friends: String, var limit: Int, var index:Int)
data class RLSetget_followersrequest(var search_user: RLSetget_followers)
data class RLSetget_followers(var get_followers: String, var limit: Int, var index:Int)

data class RLsearch_userrequest(var search_user: RLsearch_user_request)
data class RLsearch_user_request(var myid: String,var contact_status:Int, var limit: Int, var index:Int)








data class RLUserDataParcelable(
    val first_name: String,
    val last_name: String,
    val userid: String,
    val username: String,
    val avatar: String,
    val myid: String,
    val myidstatus: String,
    val theirid: String,
    val theiridstatus: String,
    var isSelected: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(first_name)
        parcel.writeString(last_name)
        parcel.writeString(userid)
        parcel.writeString(username)
        parcel.writeString(avatar)
        parcel.writeString(myid)
        parcel.writeString(myidstatus)
        parcel.writeString(theirid)
        parcel.writeString(theiridstatus)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RLUserDataParcelable> {
        override fun createFromParcel(parcel: Parcel): RLUserDataParcelable {
            return RLUserDataParcelable(parcel)
        }

        override fun newArray(size: Int): Array<RLUserDataParcelable?> {
            return arrayOfNulls(size)
        }
    }
}
