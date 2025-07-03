package com.capacitor.custom.notification.safevalueread

import com.google.gson.*
import java.lang.reflect.Type

class SafeStringAdapter : JsonDeserializer<String> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): String {
        return try {
            if (json == null || json.isJsonNull || json.asString.isNullOrBlank()) {
                ""
            } else {
                json.asString
            }
        } catch (e: Exception) {
            ""
        }
    }
}
