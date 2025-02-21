package com.revoola.fragment.start.adapter

data class RLBleListModel(val devicename:String, val deviceAddress:String, val deviceType:String,
                          var lastconnected:Boolean,val isWatchDevice:Boolean)
