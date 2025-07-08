package com.revoola.model

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

data class RLRevoolaUsersSettingsModel (val AMHR: Int,
                                        val FCMToken: String,
                                        val RFMHR: Int,
                                        val TMHR: Int,
                                        val appUnit: String,
                                        val currentGroup: String,
                                        val currentSubscription: CurrentSubscription,
                                        val displayImage: String,
                                        val displayName: String,
                                        val dob: String,
                                        val emailId: String,
                                        val insightlyId: Int,
                                        val firstName: String,
                                        val flagImage: String,
                                        val flagName: String,
                                        val gender: String,
                                        val heartRate: Int,
                                        val height: String,
                                        val heightUnit: String,
                                        val isBasicDataAdded: Boolean,
                                        val joiningDate: Long,
                                        val lastHRChange: Int,
                                        val lastHRChange90: Int,
                                        val lastHRUsed: Long,
                                        val lastLogin: String,
                                        val lastName: String,
                                        val lastVersion: String,
                                        val leaderBoardImage: String,
                                        val location: String,
                                        val numberOfGhost: String,
                                        val power: Int,
                                        val referUser: String,
                                        val referalCode: String ="",
                                        val remark: String,
                                        val restingHr: String,
                                        val totalRev: Int,
                                        val visibilityflagforthatsession: Int,
                                        val weightUnit: String,
                                        val weightkg: String)

data class CurrentSubscription(
    val commisionFlag: String,
    val discountPeriodMonth: Int,
    val discountedPrice: Int,
    val discountedPriceType: String,
    val familyPrice: Int,
    val inviteUserSubsModel: String,
    val inviteUserType: String,
    val isSubscriptionCheckRequired: Boolean,
    val isSubscriptionRequired: Boolean,
    val isTrialTaken: Boolean,
    val onGoingPrice: Int,
    val onGoingPriceType: String,
    val permissionLevelAfterTrial: String,
    val plan: String,
    val referrerTag: String,
    val remark: String,
    val subscriptionName: String,
    val timestamp: Long,
    val validDays: Int,
    val validDaysMonth: Int
)


// Long Deserializer
class LongDeserializer : JsonDeserializer<Long> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Long {
        return when {
            json?.isJsonPrimitive == true -> {
                val primitive = json.asJsonPrimitive
                when {
                    primitive.isNumber -> primitive.asDouble.toLong()
                    primitive.isString -> primitive.asString.toDoubleOrNull()?.toLong() ?: 0L
                    else -> 0L
                }
            }
            else -> 0L
        }
    }
}

// Int Deserializer
class IntDeserializer : JsonDeserializer<Int> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Int {
        return when {
            json?.isJsonPrimitive == true -> {
                val primitive = json.asJsonPrimitive
                when {
                    primitive.isNumber -> primitive.asDouble.toInt()
                    primitive.isString -> primitive.asString.toDoubleOrNull()?.toInt() ?: 0
                    else -> 0
                }
            }
            else -> 0
        }
    }
}

// String Deserializer
class StringDeserializer : JsonDeserializer<String> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): String {
        return when {
            json?.isJsonPrimitive == true -> {
                val primitive = json.asJsonPrimitive
                when {
                    primitive.isString -> primitive.asString
                    primitive.isNumber -> primitive.asString
                    primitive.isBoolean -> primitive.asBoolean.toString()
                    else -> ""
                }
            }
            json?.isJsonNull == true -> ""
            else -> ""
        }
    }
}


// Boolean Deserializer
class BooleanDeserializer : JsonDeserializer<Boolean> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Boolean {
        return when {
            json?.isJsonPrimitive == true -> {
                val primitive = json.asJsonPrimitive
                when {
                    primitive.isBoolean -> primitive.asBoolean
                    primitive.isString -> {
                        val str = primitive.asString.lowercase()
                        str == "true" || str == "1" || str == "yes"
                    }
                    primitive.isNumber -> primitive.asInt != 0
                    else -> false
                }
            }
            else -> false
        }
    }
}

// Double Deserializer
class DoubleDeserializer : JsonDeserializer<Double> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Double {
        return when {
            json?.isJsonPrimitive == true -> {
                val primitive = json.asJsonPrimitive
                when {
                    primitive.isNumber -> primitive.asDouble
                    primitive.isString -> primitive.asString.toDoubleOrNull() ?: 0.0
                    else -> 0.0
                }
            }
            else -> 0.0
        }
    }
}

// Float Deserializer
class FloatDeserializer : JsonDeserializer<Float> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Float {
        return when {
            json?.isJsonPrimitive == true -> {
                val primitive = json.asJsonPrimitive
                when {
                    primitive.isNumber -> primitive.asFloat
                    primitive.isString -> primitive.asString.toFloatOrNull() ?: 0f
                    else -> 0f
                }
            }
            else -> 0f
        }
    }
}



// Alternative: Individual field validation with safe parsing
object SafeJsonParser {

    fun safeLong(json: JsonElement?): Long {
        return when {
            json?.isJsonPrimitive == true -> {
                val primitive = json.asJsonPrimitive
                when {
                    primitive.isNumber -> primitive.asDouble.toLong()
                    primitive.isString -> primitive.asString.toDoubleOrNull()?.toLong() ?: 0L
                    else -> 0L
                }
            }
            else -> 0L
        }
    }

    fun safeInt(json: JsonElement?): Int {
        return when {
            json?.isJsonPrimitive == true -> {
                val primitive = json.asJsonPrimitive
                when {
                    primitive.isNumber -> primitive.asDouble.toInt()
                    primitive.isString -> primitive.asString.toDoubleOrNull()?.toInt() ?: 0
                    else -> 0
                }
            }
            else -> 0
        }
    }

    fun safeString(json: JsonElement?): String {
        return when {
            json?.isJsonPrimitive == true -> {
                val primitive = json.asJsonPrimitive
                when {
                    primitive.isString -> primitive.asString
                    primitive.isNumber -> primitive.asString
                    primitive.isBoolean -> primitive.asBoolean.toString()
                    else -> ""
                }
            }
            json?.isJsonNull == true -> ""
            else -> ""
        }
    }

    fun safeBoolean(json: JsonElement?): Boolean {
        return when {
            json?.isJsonPrimitive == true -> {
                val primitive = json.asJsonPrimitive
                when {
                    primitive.isBoolean -> primitive.asBoolean
                    primitive.isString -> {
                        val str = primitive.asString.lowercase()
                        str == "true" || str == "1" || str == "yes"
                    }
                    primitive.isNumber -> primitive.asInt != 0
                    else -> false
                }
            }
            else -> false
        }
    }
}

// Enhanced Deserializer with logging (for debugging)
class LoggingLongDeserializer : JsonDeserializer<Long> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Long {
        return when {
            json?.isJsonPrimitive == true -> {
                val primitive = json.asJsonPrimitive
                when {
                    primitive.isNumber -> {
                        val result = primitive.asDouble.toLong()
                        println("Parsed Long: ${primitive.asString} -> $result")
                        result
                    }
                    primitive.isString -> {
                        val result = primitive.asString.toDoubleOrNull()?.toLong() ?: 0L
                        println("Parsed Long from String: ${primitive.asString} -> $result")
                        result
                    }
                    else -> {
                        println("Could not parse Long from: ${primitive.asString}, defaulting to 0")
                        0L
                    }
                }
            }
            else -> {
                println("Null or non-primitive JSON element for Long, defaulting to 0")
                0L
            }
        }
    }
}


