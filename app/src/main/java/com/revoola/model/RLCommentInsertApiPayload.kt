package com.revoola.model

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.reflect.Type

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


data class RLCommentGetApiPayload(
    val get_comments: CommentGetParent
)

data class CommentGetParent(
    val overviewid: Int,
    val limit: Int,
    val index: Int
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

