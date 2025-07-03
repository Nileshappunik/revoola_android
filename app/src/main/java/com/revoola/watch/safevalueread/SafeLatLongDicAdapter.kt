package com.capacitor.custom.notification.safevalueread

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type


class SafeLatLongDicAdapter : JsonDeserializer<MutableList<HashMap<String, Any>>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): MutableList<HashMap<String, Any>> {
        val result = mutableListOf<HashMap<String, Any>>()
        try {
            if (json != null && json.isJsonArray) {
                json.asJsonArray.forEach { element ->
                    if (element.isJsonObject) {
                        val map = hashMapOf<String, Any>()
                        element.asJsonObject.entrySet().forEach { (key, value) ->
                            map[key] = when {
                                value.isJsonPrimitive -> when {
                                    value.asJsonPrimitive.isString -> value.asString
                                    value.asJsonPrimitive.isNumber -> value.asNumber
                                    value.asJsonPrimitive.isBoolean -> value.asBoolean
                                    else -> value.toString()
                                }
                                else -> value.toString()
                            }
                        }
                        result.add(map)
                    }
                }
            }
        } catch (e: Exception) {
            // return empty list
        }
        return result
    }
}
