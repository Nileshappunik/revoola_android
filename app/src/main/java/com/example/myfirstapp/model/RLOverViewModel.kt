package com.example.myfirstapp.model

data class RLOverViewModel(val type: String, val RLText: List<RLText>)

data class RLGetUserAggregatedDataRequest(val RLGetUserAggregatedData: RLGetUserAggregatedData)
data class RLGetUserAggregatedData(val userid: String, val classtype: String, val timestampfrom: Long, val timestampto: Long)

data class RLText(val RLAggregated: List<RLAggregated>)

data class RLAggregated(
    val session: Int,
    val calorie: Double,
    val rev: Double,
    val rms: Double,
    val rmm: Int,
    val totalTime: Int,
    val bmo: Int,
    val m_gold: Int,
    val m_silver: Int,
    val m_bronze: Int,
    val elevation: Int,
    val power: Int,
    val hr: Int,
    val steps: Int,
    val distance: Double)


