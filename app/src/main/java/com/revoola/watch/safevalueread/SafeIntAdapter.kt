package com.capacitor.custom.notification.safevalueread

import com.google.gson.*
import java.lang.reflect.Type

class SafeIntAdapter : JsonDeserializer<Int> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Int {
        return try {
            if (json == null || json.isJsonNull) return 0
            val value = json.asString.trim()
            if (value.isBlank()) 0 else value.toInt()
        } catch (e: Exception) {
            0
        }
    }
}
