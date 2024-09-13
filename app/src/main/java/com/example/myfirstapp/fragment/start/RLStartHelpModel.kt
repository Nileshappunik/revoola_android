package com.example.myfirstapp.fragment.start

data class RLStartHelpModel(val visible: Boolean,val data: List<RLStartHelpModelData>)
data class RLStartHelpModelData(
    val text: String,
    val type: Int,
    val title: String,
    val image: String)
