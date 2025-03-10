package com.revoola.fragment.friends.model

import com.google.gson.annotations.SerializedName


data class RLFriendsInsertApiPayload(
    @SerializedName("users_contacts_mk2") val users_contacts_mk2: RLUsersContactsMk2
)

data class RLUsersContactsMk2(
    @SerializedName("myid") val myid: String,
    @SerializedName("contact_data") val contact_data: List<RLInsertContactData>
)

data class RLInsertContactData(
    @SerializedName("myidstatus") val myidstatus: String?,
    @SerializedName("contact_userid") val contact_userid: String,
    @SerializedName("contact_email") val contact_email: String,
    @SerializedName("contact_status") val contact_status: Int
)



