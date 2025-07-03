package com.capacitor.custom.notification.safevalueread

import com.google.gson.*
import java.lang.reflect.Type

class SafeBooleanAdapter : JsonDeserializer<Boolean> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Boolean {
        return try {
            when {
                json == null || json.isJsonNull -> false
                json.isJsonPrimitive -> {
                    val value = json.asJsonPrimitive.asString.trim().lowercase()
                    value == "true" || value == "1"
                }
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }
}
