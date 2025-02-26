package com.revoola.databasefirebase

object RevoolaFirebasePath {

    fun deviceRecordedDataPath(currentUser:String):String{
        return "/proposedstructure/dataForTesting/$currentUser/deviceRecordedData"
    }

    fun elevationDataPath(currentUser:String):String{
        return "/proposedstructure/dataForTesting/$currentUser/elevation"
    }
    fun locationDataPath(currentUser:String):String{
        return "/proposedstructure/dataForTesting/$currentUser/location"
    }
    fun gpxDataPath(currentUser:String):String{
        return "/proposedstructure/dataForTesting/$currentUser/gpx"
    }

    fun gpx_TDataPath(currentUser:String):String{
        return "/proposedstructure/dataForTesting/$currentUser/gpx_T"
    }

    fun gpx_T_ServerDataPath(currentUser:String):String{
        return "/proposedstructure/dataForTesting/$currentUser/gpx_T_Server"
    }

    fun gpx_T_Server_NDataPath(currentUser:String):String{
        return "/proposedstructure/dataForTesting/$currentUser/gpx_T_Server_N"
    }

     fun connectivityDataPath(currentUser:String):String{
        return "/proposedstructure/dataForTesting/$currentUser/connectivity"
    }

    fun ghostLastForClassDataPath(currentUser:String):String{
        return "/proposedstructure/revoolaUserSettings/$currentUser/ghostForClass/lastForClass"
    }

    fun ghostBestForClassDataPath(currentUser:String):String{
        return "/proposedstructure/revoolaUserSettings/$currentUser/ghostForClass/bestForClass"
    }

    fun summaryDataPath(currentUser:String):String{
        return "/proposedstructure/revoolaUserSessionSummaryData/$currentUser"
    }

    fun graphDataPath(currentUser:String):String{
        return "/proposedstructure/revoolaUserSessionSummaryGraphData/$currentUser"
    }


    fun detailDataPath(currentUser:String):String{
        return "/proposedstructure/revoolaUserSessionDetailData/$currentUser"
    }

}