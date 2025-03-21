package com.revoola.databasefirebase



object RevoolaFirebasePath {

    const val basePath ="proposedstructure"

    fun dataForTestingDataPath(currentUser:String):String{
        return "/$basePath/dataForTesting/$currentUser"
    }

    fun ghostLastForClassDataPath(currentUser:String):String{
        return "/$basePath/revoolaUserSettings/$currentUser/ghostForClass/lastForClass"
    }

    fun ghostBestForClassDataPath(currentUser:String):String{
        return "/$basePath/revoolaUserSettings/$currentUser/ghostForClass/bestForClass"
    }

    fun summaryDataPath(currentUser:String):String{
        return "/$basePath/revoolaUserSessionSummaryData/$currentUser"
    }

    fun graphDataPath(currentUser:String):String{
        return "/$basePath/revoolaUserSessionSummaryGraphData/$currentUser"
    }


    fun detailDataPath(currentUser:String):String{
        return "/$basePath/revoolaUserSessionDetailData/$currentUser"
    }

    fun userCompletedVideosDataPath(currentUser:String):String{
        return "/$basePath/revoolaUserSettings/$currentUser/revoolaUserCompletedVideos"
    }

    fun assumedCaloriesDataPath():String{
        return "/$basePath/codeSection/assumedCalories"
    }
    fun assumedRevDataPath():String{
        return "/$basePath/codeSection/assumedRev"
    }
    fun basicDataDataPath(userId:String):String{
        return "/$basePath/revoolaUserSettings/$userId/basicData"
    }
    fun getStartedVideosDataPath(HelpType:String):String{
        return "/$basePath/codeSection/getStartedVideos/$HelpType"
    }

    fun classLeaderBoardsDataPath(viedoId:String):String{
        return "/$basePath/revoolaClassLeaderBoards/$viedoId"
    }

    fun worldUrlGetDataPath():String{
        return "/$basePath/codeSection/demsSettings/world"
    }

    fun sessionSummaryDataPathRead(currentUser:String,timeStamp:String):String{
        return "/$basePath/revoolaUserSessionSummaryData/$currentUser/$timeStamp"
    }

    fun sessionDetailDataPathRead(currentUser:String,timeStamp:String):String{
        return "/$basePath/revoolaUserSessionDetailData/$currentUser/$timeStamp"
    }

    fun basicDataPathWrite(endPoint:String):String{
        val userId =  RLAuthManager().RlgetCurrentUser()?.uid ?:""
        return "/$basePath/revoolaUserSettings/$userId/basicData/$endPoint"
    }



}