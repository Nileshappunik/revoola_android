package com.example.myfirstapp.firebaseModel

class RLWorkoutSessionSummaryModel {

    var avgBurntCalories: Double = 0.0
    var avgCadence: Double = 0.0
    var avgHr: Int = 0
    var avgPower: Int = 0
    var avgPowerFromDevice: Int = 0
    var avgRevPercentage: Double = 0.0
    var avgSpeed: Double = 0.0
    var avgSpeedForOneKm: Double = 0.0
    var avgSpeedForOneMile: Double = 0.0
    var burntCalories: Double = 0.0
    var classDate: String = ""
    var classDescription: String = ""
    var classImage: String = ""
    var className: String = ""
    var classNote: String = ""
    var classType: String = ""
    var demsElevation: Int = -1
    var distance: Double = 0.0
    var goal: String = ""
    var isClass: Boolean = false
    var isPowerDeviceConnected: Boolean = false
    var maxBurntCalories: Int = 0
    var maxCadence: Int = 0
    var maxHr: Int = 0
    var maxPower: Int = 0
    var maxPowerFromDevice: Int = 0
    var maxRevPercentage: Double = 0.0
    var maxSpeed: Int = 0
    var maxSpeedForOneKm: Double = 0.0
    var maxSpeedForOneMile: Double = 0.0
    var minHr: Int = 0
    var minRevPercentage: Double = 0.0
    var remark: String = "android"
    var revPercentage: Double = 0.0
    var timestamp: Long = 0
    var totalElevation: Double = 0.0
    var totalPower: Int = 0
    var totalRev: Double = 0.0
    var totalSteps: Int = 0
    var totalTime: Int = 0
    var typeOfGoal: String = ""
    var visibilityflagforthatsession: Int = 0


    var zone1: Zone = Zone()
    var zone2: Zone = Zone()
    var zone3: Zone = Zone()
    var zone4: Zone = Zone()
    var zone5: Zone = Zone()
    var zone6: Zone = Zone()
    var zone7: Zone = Zone()

    class Zone {
        var avgCadence: Double = 0.0
        var avgHr: Int = 0
        var avgPower: Int = 0
        var avgPowerFromDevice: Int = 0
        var avgSpeed: Double = 0.0
        var burntCalories: Double = 0.0
        var distance: Double = 0.0
        var remark:String="Android"
        var seconds: Int = 0
        var totalRev: Double = 0.0
    }
}



