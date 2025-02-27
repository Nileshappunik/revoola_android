package com.revoola.databasefirebase



object RevoolaFirebasePath {

    const val basePath ="proposedstructure"

    fun deviceRecordedDataPath(currentUser:String):String{
        return "/$basePath/dataForTesting/$currentUser/deviceRecordedData"
    }

    fun elevationDataPath(currentUser:String):String{
        return "/$basePath/dataForTesting/$currentUser/elevation"
    }
    fun locationDataPath(currentUser:String):String{
        return "/$basePath/dataForTesting/$currentUser/location"
    }
    fun gpxDataPath(currentUser:String):String{
        return "/$basePath/dataForTesting/$currentUser/gpx"
    }

    fun gpx_TDataPath(currentUser:String):String{
        return "/$basePath/dataForTesting/$currentUser/gpx_T"
    }

    fun gpx_T_ServerDataPath(currentUser:String):String{
        return "/$basePath/dataForTesting/$currentUser/gpx_T_Server"
    }

    fun gpx_T_Server_NDataPath(currentUser:String):String{
        return "/$basePath/dataForTesting/$currentUser/gpx_T_Server_N"
    }

     fun connectivityDataPath(currentUser:String):String{
        return "/$basePath/dataForTesting/$currentUser/connectivity"
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



}