package com.capacitor.custom.notification.safevalueread

import com.google.gson.*
import java.lang.reflect.Type

class SafeDoubleAdapter : JsonDeserializer<Double> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Double {
        return try {
            if (json == null || json.isJsonNull) return 0.0
            val value = json.asString.trim()
            if (value.isBlank()) 0.0 else value.toDouble()
        } catch (e: Exception) {
            0.0
        }
    }
}