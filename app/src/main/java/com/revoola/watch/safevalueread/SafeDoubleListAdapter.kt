package com.capacitor.custom.notification.safevalueread

import com.google.gson.*
import java.lang.reflect.Type

class SafeDoubleListAdapter : JsonDeserializer<MutableList<Double>> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): MutableList<Double> {
        return try {
            if (json == null || !json.isJsonArray) {
                mutableListOf(0.0)
            } else {
                val list = mutableListOf<Double>()
                json.asJsonArray.forEach {
                    try {
                        list.add(it.asDouble)
                    } catch (e: Exception) {
                        list.add(0.0)
                    }
                }
                list
            }
        } catch (e: Exception) {
            mutableListOf(0.0)
        }
    }
}
