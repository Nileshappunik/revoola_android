package com.revoola.firebaseModel

class RLGhostDataModel {
     var RestingHrUsedForCalculation_Last: Int=0
     var arrAvgCadence: MutableList<Double> = mutableListOf(0.0)
     var arrAvgHr: MutableList<Int> = mutableListOf(0)
     var arrAvgPower: MutableList<Int> = mutableListOf(0)
     var arrAvgRevPercentage: MutableList<Int> = mutableListOf(0)
     var arrHr: MutableList<Int> = mutableListOf(0)
     var arrMaxCadence: MutableList<Int> = mutableListOf(0)
     var arrMaxHr: MutableList<Int> = mutableListOf(0)
     var arrMaxPower: MutableList<Int> = mutableListOf(0)
     var arrMaxRevPercentage: MutableList<Double> = mutableListOf(0.0)
     var arrPower: MutableList<Int> = mutableListOf(0)
     var arrPowerFromDevice: MutableList<Int> = mutableListOf(0)
     var arrRevPercentage: MutableList<Double> = mutableListOf(0.0)
     var arrRevSecond: MutableList<Double> = mutableListOf(0.0)
     var classDate: Long =0
     var displayImage: String = ""
     var displayName: String = ""
     var flagImage: String = "flag-of-United-Kingdom.png"
     var flagName: String = "United Kingdom"
     var isPowerDeviceConnected: Boolean = false
     var location: String  = ""
     var maxHrUsedForCalculation: Int = 0
     var maxHrUsedForCalculation_Last: Int = 0
     var restingHrUsedForCalculation: Int = 0
     var timestamp: Long = 0
     var totalRev: Double = 0.0
     var totalTime: Int = 0
     var visibilityflagforthatsession: Int = 0
 }

