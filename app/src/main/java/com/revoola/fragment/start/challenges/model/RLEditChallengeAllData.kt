package com.revoola.fragment.start.challenges.model

import com.revoola.model.RLuserData
import com.revoola.model.RLyourGroupDataModel
import java.io.Serializable

class RLEditChallengeAllData : Serializable {
     var selectGroupList: List<String> = mutableListOf()
     var ChallengeType:String = ""
     var CalenderType:String = ""
     var challengeForType:String = ""
     var TargetType:String = ""
     var selectedDate:String = ""
     var ChallengeGivenName:String = ""
     var stepCount:String = ""
     var isEditClass:Boolean=false
     var IsGroup:Boolean =false
     var toDate:String =""
     var fromDate:String =""
}


