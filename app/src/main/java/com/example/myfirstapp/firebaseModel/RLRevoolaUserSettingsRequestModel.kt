package com.example.myfirstapp.firebaseModel

class RLRevoolaUserSettingsRequestModel {
    var AMHR:Int = 191
    var FCMToken:String  = ""
    var RFMHR:Int  = 191
    var TMHR:Int  = 191
    var appUnit:String  = "Imperial"
    var  currentGroup:String  = "premium"
    var currentSubscription: RLCurrentSubscriptionModel = RLCurrentSubscriptionModel()
    var displayImage:String  = "https: =//firebasestorage.googleapis.com: =443/v0/b/my-first-app-51df4.appspot.com/o/YMKbyIJ9ZdeRDSUWWIaNbz7fQI12main.png?alt=media&token=3f465f90-1755-456d-8045-2b3d0d47ef0e"
    var displayName:String  = "Guest"
    var dob:String  ="00/00/0000"
    var  emailId:String  = ""
    var firstName:String  = "Guest"
    var flagImage:String  = "flag-of-United-Kingdom.png"
    var  flagName:String = "United Kingdom"
    var  gender:String = "none"
    var heartRate:Int  = 0
    var  height:String  = "167"
    var  heightUnit:String  = "FeetInch"
    var  isBasicDataAdded:Boolean  = true
    var  joiningDate:Long  = 1728996469
    var  lastHRChange:Int  = 0
    var  lastHRChange90:Int  = 0
    var lastHRUsed:Long  = 1729082617
    var lastLogin:Long  = 1729166550
    var lastName:String  = ""
    var  lastVersion:String  = "2.218"
    var  leaderBoardImage:String  = "https: =//firebasestorage.googleapis.com: =443/v0/b/my-first-app-51df4.appspot.com/o/YMKbyIJ9ZdeRDSUWWIaNbz7fQI12leaderboard.png?alt=media&token=f323c591-e2ee-4dea-8f0f-2512fe6d1e08"
    var  location: String = "United Kingdom"
    var numberOfGhost:String = "1"
    var  power:Int  = 0
    var  referUser: String = "AndroidPlayStore "
    var referalCode:String  = ""
    var  remark:String  = "android"
    var  restingHr:String  = "60"
    var  totalRev:Int  = 0
    var  visibilityflagforthatsession:Int  = 2
    var weightUnit:String  = "Metric"
    var weightkg:String  = "77"
}

class RLCurrentSubscriptionModel{
    var  commisionFlag:String  = ""
    var discountPeriodMonth:Int  = 0
    var discountedPrice:Int  = 0
    var discountedPriceType:String  = "none"
    var familyPrice:Int = 0
    var inviteUserSubsModel:String  = "0"
    var inviteUserType:String = "NormalUser"
    var isSubscriptionCheckRequired:Boolean  = true
    var isSubscriptionRequired:Boolean  = true
    var isTrialTaken:Boolean  = true
    var onGoingPrice:Int  = 0
    var onGoingPriceType:String = "none"
    var permissionLevelAfterTrial:String  = "free"
    var plan:String  = ""
    var referrerTag:String  = "android"
    var remark: String = "android"
    var subscriptionName:String  = "Trial-Premium"
    var timestamp:Long  = 1728996469
    var validDays:Int  = 14
    var validDaysMonth:Int  = 0
}