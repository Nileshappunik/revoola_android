package com.revoola.model


import com.google.gson.annotations.SerializedName

data class RLCommentInsertApiPayload(
    val insertparent: List<InsertParent>
)
// For the inner object
data class InsertParent(
    val userid: String,
    val overviewid: Int,
    val timestamp: Long,
    val comment: String
)

data class RLCommentReplyInsertApiPayload(
    val insertreply: List<InsertReplyParent>
)
// For the inner object
data class InsertReplyParent(
    val userid: String,
    val commentid: Int,
    val overviewid: Int,
    val timestamp: Long,
    val comment: String
)


data class RLCommentGetApiPayload(
    val get_comments: CommentGetParent
)

data class CommentGetParent(
    val overviewid: Int,
    val limit: Int,
    val index: Int
)

data class RLCommentDeleteApiPayload(
    val deleteparent: List<CommentDeleteParent> = emptyList()
)

data class CommentDeleteParent(
    val overviewid: Int,
    val commentid: Int
)

data class RLCommentReplyDeleteApiPayload(
    val deletereply: List<CommentReplyDeleteParent> = emptyList()
)

data class CommentReplyDeleteParent(
    val overviewid: Int,
    val commentid: Int
)


data class RLCommentsApiResponse(
    val type: String,
    val text: CommentsPayload
)
data class CommentsPayload(
    val comments: List<CommentItem> = emptyList()
)
data class CommentItem(
    val id: Int,
    val userid: String,
    val avatar: String,
    val username: String,
    val comment: String,
    val timestamp: Long,
    @SerializedName("total_replies")
    val totalReplies: Int?
)

