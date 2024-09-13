package com.example.myfirstapp.model

class RLWorkoutSessionSummaryModel {
    var visibilityflagforthatsession: Int = 0
    var avgSpeedForOneKm: Double? = 0.0
    var avgSpeedForOneMile: Double? = 0.0
    var maxSpeedForOneKm: Double? = 0.0
    var maxSpeedForOneMile: Double? = 0.0
    var avgCadence: Int = 0
    var avgBurntCalories: Int = 0
    var avgHr: Int = 0
    var remark: String = "android"
    var avgPower: Int = 0
    var avgPowerFromDevice: Int = 0
    var avgSpeed: Double = 0.0
    var avgPaceForKm: Double = 0.0
    var avgPaceForMile: Double = 0.0
    var maxCadence: Int = 0
    var maxBurntCalories: Int = 0
    var maxHr: Int = 0
    var minHr: Int = 0
    var maxPower: Int = 0
    var maxPowerFromDevice: Int = 0
    var maxSpeed: Double = 0.0
    var avgRevPercentage: Double = 0.0
    var burntCalories: Int = 0
    var totalPower: Int = 0
    var classType: String = ""
    var distance: Double = 0.0
    var isPowerDeviceConnected: Boolean = false
    var revPercentage: Double = 0.0
    var maxRevPercentage: Double = 0.0
    var minRevPercentage: Double = 0.0
    var className: String = ""
    var totalRev: Int = 0
    var totalTime: Int = 0
    var timestamp: Any? = null
    var totalSteps: Int = 0
    var classDate: Any? = ""
    var classImage: String = ""
    var classNote: String = ""
    var goal: String = ""
    var typeOfGoal: String = ""
    var isClass: Boolean = false
    var totalElevation: Double = 0.0
    var demsElevation: Double = -1.0
    var rms: Int = 0
    var videoKey: String = ""
    var imageLinkSmall: String = ""
    var imageLinkLarge: String = ""
    var location: String = ""
    var instructor: String = ""
    var duration: String = ""
    var rideTitle: String = ""
    var mainTitle: String = ""
    var isMindClass: Boolean = false
    var originalClassDate: String = ""
    var zone1: Zone = Zone()
    var zone2: Zone = Zone()
    var zone3: Zone = Zone()
    var zone4: Zone = Zone()
    var zone5: Zone = Zone()
    var zone6: Zone = Zone()
    var zone7: Zone = Zone()

    class Zone {
        var avgCadence: Int = 0
        var avgHr: Int = 0
        var avgPower: Int = 0
        var avgPowerFromDevice: Int = 0
        var avgSpeed: Double = 0.0
        var burntCalories: Int = 0
        var distance: Double = 0.0
        var seconds: Int = 0
        var totalRev: Int = 0
    }
}



