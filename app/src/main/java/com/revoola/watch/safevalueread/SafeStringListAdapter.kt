package com.capacitor.custom.notification.safevalueread

import com.google.gson.*
import java.lang.reflect.Type

class SafeStringListAdapter : JsonDeserializer<MutableList<String>> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): MutableList<String> {
        return try {
            if (json == null || !json.isJsonArray) {
                mutableListOf("")
            } else {
                val list = mutableListOf<String>()
                json.asJsonArray.forEach {
                    try {
                        val str = it.asString
                        list.add(str.ifBlank { "" })
                    } catch (e: Exception) {
                        list.add("")
                    }
                }
                list
            }
        } catch (e: Exception) {
            mutableListOf("")
        }
    }
}
