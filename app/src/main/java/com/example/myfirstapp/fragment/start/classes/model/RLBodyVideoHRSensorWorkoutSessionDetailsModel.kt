package com.example.myfirstapp.fragment.start.classes.model

 class RLBodyVideoHRSensorWorkoutSessionDetailsModel {

    var MaxHrUsedForCalculation: Int = 0
    var MaxHrUsedForCalculation_Last: Int = 0
    var RestingHrUsedForCalculation: Int = 0
    var RestingHrUsedForCalculation_Last: Int = 0

    var arrAvgRevPercentage: MutableList<Int> =  mutableListOf(0)
    var arrBurntCalories: MutableList<Double> =  mutableListOf(0.0)//Done
    var arrCadence: MutableList<Int> =  mutableListOf(0)
    var arrCumDistance: MutableList<Double> =  mutableListOf(0.0)
    var arrCumSpeed: MutableList<Double> =  mutableListOf(0.0)
    var arrDistance: MutableList<Double> =  mutableListOf(0.0)//Done
    var arrHr: MutableList<Int> =  mutableListOf(0)
    var arrMaxRevPercentage: MutableList<Int> =  mutableListOf(0)
    var arrPower: MutableList<Int> =  mutableListOf(0)
    var arrPowerFromDevice: MutableList<Int> =  mutableListOf(0)
    var arrRevPercentage: MutableList<Int> =  mutableListOf(0)
    var arrRevSecond: MutableList<Int> =  mutableListOf(0)
    var arrSpeed: MutableList<Double> =  mutableListOf(0.0)//Done


    var avgRevPercentage:Int=0
    var burntCalories:Double=0.0//Done
    var classDate:String=""
    var classDescription:String=""
    var classImage:String=""
    var className:String=""//Done
    var classNote:String=""//Done
    var classType:String=""//Done
    var demsElevation:Int=-1
    var displayImage:String=""
    var displayName:String=""
    var distance:Double=0.0//Done
    var flagImage:String=""
    var flagName:String=""
    var imageLinkLarge:String = ""
    var imageLinkSmall:String = ""
    var isClass:Boolean=false
    var isPowerDeviceConnected:Boolean=false
    var location:Int=0
    var maxRevPercentage:Int=0
    var minRevPercentage:Int=0
    var remark:String="android"//Done
    var revPercentage:Int=0
    var rms:Int=0
    var timestamp:Int=0
    var totalElevation:Int=0
    var totalPower:Int=0
    var totalRev:Double=0.0
    var totalTime:Int=0//Done
    var videoKey:String=""
    var visibilityflagforthatsession:Int=0//Done





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



