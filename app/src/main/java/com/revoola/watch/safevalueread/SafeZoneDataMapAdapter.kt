package com.capacitor.custom.notification.safevalueread


import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.revoola.databasefirebase.RLZoneDataDetails
import java.lang.reflect.Type

class SafeZoneDataMapAdapter : JsonDeserializer<Map<String, RLZoneDataDetails>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Map<String, RLZoneDataDetails> {
        return try {
            if (json != null && json.isJsonObject) {
                val result = mutableMapOf<String, RLZoneDataDetails>()
                json.asJsonObject.entrySet().forEach { (key, element) ->
                    try {
                        val value = context?.deserialize<RLZoneDataDetails>(element, RLZoneDataDetails::class.java)
                        if (value != null) result[key] = value
                    } catch (e: Exception) {
                        // skip bad entry
                    }
                }
                result
            } else {
                emptyMap()
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
