package com.capacitor.custom.notification.safevalueread

import com.google.gson.*
import java.lang.reflect.Type

class SafeIntListAdapter : JsonDeserializer<MutableList<Int>> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): MutableList<Int> {
        return try {
            if (json == null || !json.isJsonArray) {
                mutableListOf(0)
            } else {
                val list = mutableListOf<Int>()
                json.asJsonArray.forEach {
                    try {
                        list.add(it.asInt)
                    } catch (e: Exception) {
                        list.add(0)
                    }
                }
                list
            }
        } catch (e: Exception) {
            mutableListOf(0)
        }
    }
}
