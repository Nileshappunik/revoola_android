package com.revoola.model

data class RLOverViewModel(val type: String, val text: List<RLText>)

data class RLGetUserAggregatedDataRequest(val getUserAggregatedData: RLGetUserAggregatedData)
data class RLGetUserAggregatedData(val userid: String, val classtype: String, val timestampfrom: Long, val timestampto: Long)

data class RLText(val aggregated: List<RLAggregated>)

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

data class RLOverviewGraphDataRequest(val overview_graph: RLOverview_graphData)

data class RLOverview_graphData(val user: String, val timestampfrom: Int, val timestampto: Int, val classtype: String,val fromthirdparty:String)

