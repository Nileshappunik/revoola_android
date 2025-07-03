package com.capacitor.custom.notification.safevalueread

import com.google.gson.*
import java.lang.reflect.Type

class SafeLongAdapter : JsonDeserializer<Long> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Long {
        return try {
            if (json == null || json.isJsonNull) return 0L
            val value = json.asString.trim()
            if (value.isBlank()) 0L else value.toLong()
        } catch (e: Exception) {
            0L
        }
    }
}