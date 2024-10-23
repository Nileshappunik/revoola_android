package com.example.myfirstapp.firebaseModel

 class RLWorkoutSessionDetailsModel {
    var visibilityflagforthatsession = 0

    var arrRevSecond: MutableList<Any> = mutableListOf(0)
    var arrRevPercentage: MutableList<Any> = mutableListOf(0)

    //FOCUS ON THIS ONLY
    var arrSpeed: MutableList<Double> = mutableListOf(0.0)
    var arrCadence: MutableList<Int> =  mutableListOf(0)
    var arrDistance: MutableList<Double> =  mutableListOf(0.0)
    var arrElevation: MutableList<Int> = mutableListOf(0)
    var arrHr: MutableList<Int> =  mutableListOf(0)


    var arrCumDistance: MutableList<Double> = mutableListOf(0.0) //EACH SEC TOTAL DISTANCE
    var arrCumSpeed: MutableList<Any> = mutableListOf(0) // PENDING FROM DHRUV
    var arrCumElevation: MutableList<Any> = mutableListOf(0) //EACH SEC TOTAL ELEVATION

    var arrAvgHr: MutableList<Any> = mutableListOf(0) //AVG OF MAIN ARRAY
    var arrAvgCadence: MutableList<Any> = mutableListOf(0)
    var arrAvgPower: MutableList<Any> = mutableListOf(0)
    var arrMaxCadence: MutableList<Any> = mutableListOf(0) //MAX OF MAIN ARRAY
    var arrMaxHr: MutableList<Any> = mutableListOf(0)
    var arrMaxPower: MutableList<Any> = mutableListOf(0)


    var arrBurntCalories: MutableList<Int> = mutableListOf(0) ////EACH SEC CALORIES


    var speedForOneKm: MutableList<Any> = mutableListOf()
    var speedForOneMile: MutableList<Any> = mutableListOf()



    var arrAvgRevPercentage = mutableListOf(0.0)
    var arrMaxRevPercentage = mutableListOf(0.0)


    var arrPower: MutableList<Any> = mutableListOf(0)
    var arrPowerFromDevice: MutableList<Any> = mutableListOf(0)


    var avgRevPercentage = 0.0
    var burntCalories = 0
    var totalPower = 0
    var classType = ""
    var distance = 0.0
    var displayName = ""
    var displayImage = ""
    var flagName = ""
    var flagImage = ""
    var isPowerDeviceConnected = false
    var location = ""
    var revPercentage = 0.0
    var maxRevPercentage = 0.0
    var minRevPercentage = 0.0
    var rms = 0.0
    var classDate: Any = ""
    var classNote = ""
    var classImage = ""
    var className:String = ""
    var remark = "android"
    var totalRev = 0
    var totalTime = 0 //TOTAL TIME
    var totalSteps = 0 //TOTAL STEPS
    var videoKey = ""
    var imageLinkSmall = ""
    var imageLinkLarge = ""
    var timestamp: Any? = null
    var isClass = false
    var totalElevation = 0.0  //TOTAL ELEVATION
    var instructor = ""
    var duration = ""
    var rideTtitle = ""
    var mainTitle = ""
    var originalClassDate = ""
    var isMindClass = false
    var goal = ""
    var typeOfGoal = ""
    var share_map = 0
    var from_third_party_source = 0

    var zone1: Zone = Zone() //REV 0-30
    var zone2: Zone = Zone()
    var zone3: Zone = Zone()
    var zone4: Zone = Zone()
    var zone5: Zone = Zone()
    var zone6: Zone = Zone()
    var zone7: Zone = Zone()

    var restingHrUsedForCalculation: Any = 0
    var maxHrUsedForCalculation: Any = 0
    var restingHrUsedForCalculation_Last: Any = 0
    var maxHrUsedForCalculation_Last: Any = 0

    class Zone {
       var arrCadence: MutableList<Int> = mutableListOf(0)
       var arrHr: MutableList<Int> = mutableListOf(0)
       var arrPower: MutableList<Any> = mutableListOf(0)
       var arrPowerFromDevice: MutableList<Any> = mutableListOf(0)
       var arrSpeed: MutableList<Double> = mutableListOf(0.0)

       var burntCalories = 0.0
       var distance = 0.0
       var seconds = 0
       var totalRev = 0
    }
}



