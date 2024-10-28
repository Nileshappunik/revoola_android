package com.example.myfirstapp.firebaseModel

 class RLSpeedSensorWorkoutSessionDetailsModel {

    var MaxHrUsedForCalculation: Int = 0
    var MaxHrUsedForCalculation_Last: Int = 0
    var RestingHrUsedForCalculation: Int = 0
    var RestingHrUsedForCalculation_Last: Int = 0

    var arrBurntCalories: MutableList<Double> =  mutableListOf(0.0)//Done
    var arrCadence: MutableList<Double> =  mutableListOf(0.0)
    var arrCumElevation: MutableList<Double> =  mutableListOf(0.0)
    var arrDistance: MutableList<Double> =  mutableListOf(0.0)//Done
    var arrElevation: MutableList<Double> =  mutableListOf(0.0)
    var arrPower: MutableList<Int> =  mutableListOf(0)
    var arrPowerFromDevice: MutableList<Int> =  mutableListOf(0)
    var arrSpeed: MutableList<Double> =  mutableListOf(0.0)//Done

    var speedForOneKm: MutableList<Double> =  mutableListOf(0.0)?:mutableListOf(0.0)
    var speedForOneMile: MutableList<Double> =  mutableListOf(0.0)?:mutableListOf(0.0)


    var avgRevPercentage:Int=0
    var burntCalories:Double=0.0//Done
    var classDate:String=""
    var classDescription:String=""
    var classImage:String=""
    var className:String=""//Done
    var classNote:String=""//Done
    var classType:String=""//Done
    var demsElevation:Int=-1
    var distance:Double=0.0//Done
    var goal:String=""
    var isClass:Boolean=false
    var isPowerDeviceConnected:Boolean=false
    var mapGeneratedUrl:String=""
    var maxRevPercentage:Int=0
    var minRevPercentage:Int=0
    var remark:String="android"//Done
    var revPercentage:Int=0
    var timestamp:Int=0
    var totalElevation:Double=0.0
    var totalPower:Int=0
    var totalRev:Double=0.0
    var totalSteps:Int=0//Done
    var totalTime:Int=0//Done
    var typeOfGoal:String=""



    var zone1: ZoneNew = ZoneNew() //REV 0-30
    var zone2: ZoneNew = ZoneNew()
    var zone3: ZoneNew = ZoneNew()
    var zone4: ZoneNew = ZoneNew()
    var zone5: ZoneNew = ZoneNew()
    var zone6: ZoneNew = ZoneNew()
    var zone7: ZoneNew = ZoneNew()


    class ZoneNew {
       var burntCalories:Double= 0.0//Done
       var distance:Double = 0.0//Done
       var remark:String= "android"//Done
       var seconds:Int = 0
       var totalRev:Int = 0
    }
}



