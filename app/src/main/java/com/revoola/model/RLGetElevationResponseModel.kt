package com.revoola.model

import com.google.gson.annotations.SerializedName

data class RLGetElevationResponseModel(
    @SerializedName("results") val results: List<RLGetElevationResult>
)

data class RLGetElevationResult(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("elevation") val elevation: Int
)

