package com.revoola.fragment.friends.model

import com.google.gson.annotations.SerializedName
import com.revoola.model.RLContactModel

data class RLEmailFilterRequestModel(
    @SerializedName("sync_contact_new")
    val syncContactNew: RLSyncContactFilterModel
)

data class RLSyncContactFilterModel(
    @SerializedName("current_user")
    val currentUser: String,

    @SerializedName("email")
    val email: List<String>
)

data class EmailFilterApiResponse(
    @SerializedName("type")
    val type: String,

    @SerializedName("text")
    val text: EmailFilterInviteData
)

data class EmailFilterInviteData(
    @SerializedName("uids_to_invite")
    val uidsToInvite: List<EmailFilterUserInvite>,

    @SerializedName("emails_to_invite")
    val emailsToInvite: List<String>
)

data class EmailFilterUserInvite(
    @SerializedName("userid")
    val userId: String,

    @SerializedName("avatar")
    val avatar: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("theiridstatus")
    var theirIdStatus: String?,

    @SerializedName("myidstatus")
    val myIdStatus: String?
)

sealed class RLFindOnRevoolaInviteItem {
    data class RLFollow(val user: EmailFilterUserInvite) : RLFindOnRevoolaInviteItem()
    data class RLInvite(val contact: RLContactModel) : RLFindOnRevoolaInviteItem()
}

