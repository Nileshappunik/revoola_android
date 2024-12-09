package com.revoola.firebaseModel

 class RLHeartRateSensorWorkoutSessionDetailsModel {

    var MaxHrUsedForCalculation: Int = 0?:0
    var MaxHrUsedForCalculation_Last: Int = 0?:0
    var RestingHrUsedForCalculation: Int = 0?:0
    var RestingHrUsedForCalculation_Last: Int = 0?:0

    var arrBurntCalories: MutableList<Double> =  mutableListOf(0.0)?:mutableListOf(0.0)
    var arrCadence: MutableList<Double> =  mutableListOf(0.0)?:mutableListOf(0.0)
    var arrCumDistance: MutableList<Double> =  mutableListOf(0.0)?:mutableListOf(0.0)
    var arrCumElevation: MutableList<Double> =  mutableListOf(0.0)?:mutableListOf(0.0)
    var arrCumSpeed: MutableList<Double> =  mutableListOf(0.0)?:mutableListOf(0.0)
    var arrDistance: MutableList<Double> =  mutableListOf(0.0)?:mutableListOf(0.0)
    var arrElevation: MutableList<Double> =  mutableListOf(0.0)?:mutableListOf(0.0)
    var arrHRRecordedSecond: MutableList<Int> =  mutableListOf(0)?:mutableListOf(0)
    var arrHr: MutableList<Int> =  mutableListOf(0)?:mutableListOf(0)
    var arrPower: MutableList<Int> =  mutableListOf(0)?:mutableListOf(0)
    var arrPowerFromDevice: MutableList<Int> =  mutableListOf(0)?:mutableListOf(0)
    var arrRevPercentage: MutableList<Double> =  mutableListOf(0.0)?:mutableListOf(0.0)
    var arrRevSecond: MutableList<Double> =  mutableListOf(0.0)?:mutableListOf(0.0)
    var arrSpeed: MutableList<Double> =  mutableListOf(0.0)?:mutableListOf(0.0)
    var speedForOneKm: MutableList<Double> =  mutableListOf(0.0)?:mutableListOf(0.0)
    var speedForOneMile: MutableList<Double> =  mutableListOf(0.0)?:mutableListOf(0.0)


    var avgRevPercentage:Double=0.0?:0.0
    var burntCalories:Double=0.0?:0.0
    var classDate:String=""?:""
    var classDescription:String=""?:""
    var classImage:String=""?:""
    var className:String=""?:""
    var classNote:String=""?:""
    var classType:String=""?:""
    var demsElevation:Int=-1?:-1
    var distance:Double=0.0?:0.0
    var goal:String=""?:""
    val isClass:Boolean=false?:false
    val isPowerDeviceConnected:Boolean=false?:false
    var mapGeneratedUrl:String=""?:""
    var maxRevPercentage:Double=0.0?:0.0
    var minRevPercentage:Double=0.0?:0.0
    var remark:String="android"//Done
    var revPercentage:Double=0.0?:0.0
    var timestamp:Int=0?:0
    var totalElevation:Double=0.0?:0.0
    var totalPower:Int=0?:0
    var totalRev:Double=0.0?:0.0
    var totalSteps:Int=0?:0
    var totalTime:Int=0?:0
    var typeOfGoal:String=""?:""



    var zone1: ZoneNew = ZoneNew() //REV 0-30
    var zone2: ZoneNew = ZoneNew()
    var zone3: ZoneNew = ZoneNew()
    var zone4: ZoneNew = ZoneNew()
    var zone5: ZoneNew = ZoneNew()
    var zone6: ZoneNew = ZoneNew()
    var zone7: ZoneNew = ZoneNew()


    class ZoneNew {
       var burntCalories:Double= 0.0?:0.0
       var distance:Double = 0.0?:0.0
       var remark:String= "android"//Done
       var seconds:Int = 0?:0
       var totalRev:Double = 0.0?:0.0
    }
}



