package com.revoola.utils
 class RLConstants {
    companion object {
        const val LOGOUT_D = "4"
        const val EXIT = "5"
        const val CardData = "CARDDATA"
        const val FeedSelectTag = "FeedSelectTag"
        const val MIND = "Mind"
        const val BODY = "Body"
        const val CLASS_TYPE = "classtype"
        const val TYPE = "type"
        const val EFFORT = "effort"
        const val ELEVATION = "elevation"
        const val SPEED = "speed"
        const val PACE = "pace"
        const val DEVICE_NAME = "devicename"
        const val DEVICE_ADDRESS = "deviceAddress"
        const val HEART_SENSOR = "HEARTRATESENSOR"
        const val SPEED_SENSOR = "SPEEDSENSOR"
        const val NO_SENSOR = "NOSENSOR"
        const val weightInKg = 70.0

        //Realtime Database Table Name
        const val LIVE = "live"
        const val LIVE_USERS_EMAIL = "liveUsersEmail"


        const val REVOOLA_USER_EMAILS= "revoolaUserEmails"
        const val REVOOLA_USER_FOR_SEARCH= "revoolaUsersForSearch"
        const val REVOOLA_USER_SETTINGS= "revoolaUserSettings"
        const val BASIC_DATA= "basicData"
        const val REVOOLA_VIDEO_KEYS_MIND= "revoolaVideoKeysMind"
        const val REVOOLA_VIDEO_KEYS= "revoolaVideoKeys"
        const val REVOOLA_VIDEOS= "revoolaVideos"
        const val REVOOLA_VIDEOS_MIND= "revoolaVideosMind"
        const val CODE_SECTION= "codeSection"
        const val AVAILABLE_MENUS= "availableMenus"
        const val CHALLENGES= "challenges"
        const val FRIENDS= "friends"
        const val MAIN= "main"
        const val YOUR_WAY= "yourWay"

        const val FORALL= "forAll"
        const val FORENERGISE= "forEnergise"
        const val FORFOCUS= "forFocus"
        const val FORHAPPINESS= "forHappiness"
        const val FORMINDFULKMOVEMENT= "forMindfulmovement"
        const val FORRELAX= "forRelax"
        const val FORSLEEP= "forSleep"
        const val LISTOFVIDEOS= "listOfVideos"
        const val FORDANCE= "forDance"
        const val FORHIIT= "forHiit"
        const val FORPILATES= "forPilates"
        const val FORRIDE= "forRide"
        const val FORWARMUP= "forWarmup"
        const val FORYOGA= "forYoga"


        //ALL Get API Server
        const val BASE_URL: String = "https://video.revoola.com/" // _stuff/"
        const val URL_V3: String = "_stuff/getResponse_v3.php"
        const val URL_V2: String = "_stuff/getResponse_v2.php"
        const val insertJSONApi = "_stuff/insertJSON.php"
        const val mpfIfCWxBL_insert = "my_overview_thumb/mpfIfCWxBL_insert.php"
        const val insertGroup = "_stuff/_groups/mpfIfCWxBL_insert.php"

        //MOENAGE URL
        const val UPDATE_USER_INSIGHTLY = "https://us-central1-rideathome-9080e.cloudfunctions.net/insightly-updateAccount"
        const val UPDATE_MOENAGE_USER = "https://us-central1-rideathome-9080e.cloudfunctions.net/moengage-updateAccount"


        //Help Url ALl
        const val FaQS_URL:String = "https://www.revoola.com/faqs"
        const val Quick_Introduction_Url:String = "https://takeoff.jetstre.am/?account=revoola&file=AQuickTour5WebinPhone.mp4&type=streaming&service=wowza&protocol=https&output=playlist.m3u8"
        const val Connecting_HearRate_Help_Video:String =  "connecting_heart_rate_sensor"
        const val Connecting_Speed_Help_Video:String =  "connecting_speed_sensor"
        const val Connecting_Apple_Watch_Help_Video:String =  "connecting_apple_watch"
        const val A_Quick_Tour_of_Revoola_Help_Video:String =  "quick_tour"
        const val Name_You_Sensor_Help_Video:String =  "name_your_sensor"
        const val Troubleshooting_Cant_Find_My_Sensor_Help_Video:String =  "troubleshooting"


        const val currentUser: String="w2p8SQCvE3emjEEDo66f02eF6fG2"
        const val Revenuecat_Api_Key: String="goog_ezrENTHxAHwKwnqeIoNRBjxrgTE"

        //All Image Link
        const val Friends_Fab_SVG: String="https://video.revoola.com/v3/icons/misc/friends_fab.svg"
        const val Flags_Image_Url: String="https://video.revoola.com/flags/"
        const val PILATES_IMAGE="https://video.revoola.com/v2/images/iphone8landscape_pilates.png"
        const val RIDE_IMAGE="https://video.revoola.com/v2/images/iphone8landscape_ride.png"
        const val RUN_IMAGE="https://video.revoola.com/v2/images/iphone8landscape_run.png"
        const val WALK_IMAGE="https://video.revoola.com/v2/images/iphone8landscape_walk.png"
        const val WORKOUT_IMAGE="https://video.revoola.com/v2/images/iphone8landscape_workout.png"
        const val YOGA_IMAGE="https://video.revoola.com/v2/images/iphone8landscape_yoga.png"
        const val Img_Feed_Apple_Fitness = "https://video.revoola.com/v2/images/v3_app_apple.png"
        const val Img_Challenge_Start = "https://video.revoola.com/v2/start/challenges_start.jpg"
        const val APPLE_IMAGE="https://video.revoola.com/v2/images/iphone8landscape_metrics_apple.png"
        const val APPLE_32_IMAGE="https://video.revoola.com/v2/images/iphone8landscape_metrics_apple_3x2.png"
        const val img_feed_strava = "https://video.revoola.com/v2/images/v3_app_strava.png"
        const val img_feed_garmin = "https://video.revoola.com/v2/images/v3_app_connect.png"
        const val img_feed_oura = "https://video.revoola.com/v2/images/v3_app_oura.png"
        const val img_feed_whoop = "https://video.revoola.com/v2/images/v3_app_whoop.png"
        const val img_feed_bend = "https://video.revoola.com/v2/images/v3_app_bend.png"
        const val img_feed_coros = "https://video.revoola.com/v2/images/v3_app_coros.png"
        const val img_feed_fitbit = "https://video.revoola.com/v2/images/v3_app_fitbit.png"
        const val img_feed_google_fit = "https://video.revoola.com/v2/images/v3_app_gfit.png"
        const val img_feed_apple_health_app = "http://video.revoola.com/v2/images/v3_app_applehealth.png"
        const val img_feed_google_health_connect = "https://video.revoola.com/v2/images/v3_app_healthconnect.png"
        const val img_yourway_start = "https://video.revoola.com/v2/start/yourway_start.jpg"
        const val img_app_applehealth ="https://video.revoola.com/v2/images/_app_applehealth.png"
        const val GuestImg = "https://firebasestorage.googleapis.com/v0/b/rideathome-9080e.appspot.com/o/defaultProfileImg%2FdefaultProfileImg.jpeg?alt=media&token=798a91e9-81aa-4430-a91c-eb8ecc1b3a27"


        //Challenges List
        const val effort_custom_challenges ="effort-custom-challenges"
        const val steps_custom_challenges ="steps-custom-challenges"
        const val calories_custom_challenges ="calories-custom-challenges"
        const val duration_custom_challenges ="duration-custom-challenges"
        const val climbed_custom_challenges ="climbed-custom-challenges"
        const val distance_custom_challenges ="distance-custom-challenges"
        const val effort_daily_challenges ="effort-daily-challenges"
        const val steps_daily_challenges ="steps-daily-challenges"
        const val calories_daily_challenges ="calories-daily-challenges"
        const val duration_daily_challenges ="duration-daily-challenges"
        const val climbed_daily_challenges ="climbed-daily-challenges"
        const val distance_daily_challenges ="distance-daily-challenges"
        const val effort_weekly_challenges ="effort-weekly-challenges"
        const val steps_weekly_challenges ="steps-weekly-challenges"
        const val calories_weekly_challenges ="calories-weekly-challenges"
        const val duration_weekly_challenges ="duration-weekly-challenges"
        const val climbed_weekly_challenges ="climbed-weekly-challenges"
        const val distance_weekly_challenges ="distance-weekly-challenges"
        const val effort_monthly_challenges ="effort-monthly-challenges"
        const val steps_monthly_challenges ="steps-monthly-challenges"
        const val calories_monthly_challenges ="calories-monthly-challenges"
        const val duration_monthly_challenges ="duration-monthly-challenges"
        const val climbed_monthly_challenges ="climbed-monthly-challenges"
        const val distance_monthly_challenges ="distance-monthly-challenges"

        //Weight Array
        val valuesUsPounds = arrayOf(
            "40 lbs",
            "41 lbs", "42 lbs", "43 lbs", "44 lbs", "45 lbs","46 lbs", "47 lbs", "48 lbs", "49 lbs", "50 lbs",
            "51 lbs", "52 lbs", "53 lbs", "54 lbs", "55 lbs","56 lbs", "57 lbs", "58 lbs", "59 lbs", "60 lbs",
            "61 lbs", "62 lbs", "63 lbs", "64 lbs", "65 lbs","66 lbs", "67 lbs", "68 lbs", "69 lbs", "70 lbs",
            "71 lbs", "72 lbs", "73 lbs", "74 lbs", "75 lbs","76 lbs", "77 lbs", "78 lbs", "79 lbs", "80 lbs",
            "81 lbs", "82 lbs", "83 lbs", "84 lbs", "85 lbs","86 lbs", "87 lbs", "88 lbs", "89 lbs", "90 lbs",
            "91 lbs", "92 lbs", "93 lbs", "94 lbs", "95 lbs","96 lbs", "97 lbs", "98 lbs", "99 lbs", "100 lbs",
            "101 lbs", "102 lbs", "103 lbs", "104 lbs", "105 lbs","106 lbs", "107 lbs", "108 lbs", "109 lbs", "110 lbs",
            "111 lbs", "112 lbs", "113 lbs", "114 lbs", "115 lbs","116 lbs", "117 lbs", "118 lbs", "119 lbs", "120 lbs",
            "121 lbs", "122 lbs","123 lbs", "124 lbs", "125 lbs", "126 lbs", "127 lbs","128 lbs","129 lbs","130 lbs",
            "131 lbs", "132 lbs", "133 lbs", "134 lbs", "135 lbs","136 lbs", "137 lbs", "138 lbs", "139 lbs", "140 lbs",
            "141 lbs", "142 lbs", "143 lbs", "144 lbs", "145 lbs","146 lbs", "147 lbs", "148 lbs", "149 lbs", "150 lbs",
            "151 lbs", "152 lbs", "153 lbs", "154 lbs", "155 lbs","156 lbs", "157 lbs", "158 lbs", "159 lbs", "160 lbs",
            "161 lbs", "162 lbs", "163 lbs", "164 lbs", "165 lbs","166 lbs", "167 lbs", "168 lbs", "169 lbs", "170 lbs",
            "171 lbs", "172 lbs", "173 lbs", "174 lbs", "175 lbs","176 lbs", "177 lbs", "178 lbs", "179 lbs", "180 lbs",
            "181 lbs", "182 lbs", "183 lbs", "184 lbs", "185 lbs","186 lbs", "187 lbs", "188 lbs", "189 lbs", "190 lbs",
            "191 lbs", "192 lbs", "193 lbs", "194 lbs", "195 lbs","196 lbs", "197 lbs", "198 lbs", "199 lbs", "200 lbs",
            "201 lbs", "202 lbs", "203 lbs", "204 lbs", "205 lbs","206 lbs", "207 lbs", "208 lbs", "209 lbs", "210 lbs",
            "211 lbs", "212 lbs", "213 lbs", "214 lbs", "215 lbs","216 lbs", "217 lbs", "218 lbs", "219 lbs", "220 lbs",
            "221 lbs", "222 lbs","223 lbs", "224 lbs", "225 lbs", "226 lbs", "227 lbs","228 lbs","229 lbs","230 lbs",
            "231 lbs", "232 lbs", "233 lbs", "234 lbs", "235 lbs","236 lbs", "237 lbs", "238 lbs", "239 lbs", "240 lbs",
            "241 lbs", "242 lbs", "243 lbs", "244 lbs", "245 lbs","246 lbs", "247 lbs", "248 lbs", "249 lbs", "250 lbs",
            "251 lbs", "252 lbs", "253 lbs", "254 lbs", "255 lbs","256 lbs", "257 lbs", "258 lbs", "259 lbs", "260 lbs",
            "261 lbs", "262 lbs", "263 lbs", "264 lbs", "265 lbs","266 lbs", "267 lbs", "268 lbs", "269 lbs", "270 lbs",
            "271 lbs", "272 lbs", "273 lbs", "274 lbs", "275 lbs","276 lbs", "277 lbs", "278 lbs", "279 lbs", "280 lbs",
            "281 lbs", "282 lbs", "283 lbs", "284 lbs", "285 lbs","286 lbs", "287 lbs", "288 lbs", "289 lbs", "290 lbs",
            "291 lbs", "292 lbs", "293 lbs", "294 lbs", "295 lbs","296 lbs", "297 lbs", "298 lbs", "299 lbs", "300 lbs",
            "301 lbs", "302 lbs", "303 lbs", "304 lbs", "305 lbs","306 lbs", "307 lbs", "308 lbs", "309 lbs", "310 lbs",
            "311 lbs", "312 lbs", "313 lbs", "314 lbs", "315 lbs","316 lbs", "317 lbs", "318 lbs", "319 lbs", "320 lbs",
            "321 lbs", "322 lbs","323 lbs", "324 lbs", "325 lbs", "326 lbs", "327 lbs","328 lbs","329 lbs","330 lbs",
            "331 lbs")

        val valuesMatric = arrayOf(
            "18 kg", "19 kg", "20 kg", "21 kg", "22 kg","23 kg", "24 kg", "25 kg", "26 kg", "27 kg","28 kg","29 kg","30 kg",
            "31 kg", "32 kg", "33 kg", "34 kg", "35 kg","36 kg", "37 kg", "38 kg", "39 kg", "40 kg",
            "41 kg", "42 kg", "43 kg", "44 kg", "45 kg","46 kg", "47 kg", "48 kg", "49 kg", "50 kg",
            "51 kg", "52 kg", "53 kg", "54 kg", "55 kg","56 kg", "57 kg", "58 kg", "59 kg", "60 kg",
            "61 kg", "62 kg", "63 kg", "64 kg", "65 kg","66 kg", "67 kg", "68 kg", "69 kg", "70 kg",
            "71 kg", "72 kg", "73 kg", "74 kg", "75 kg","76 kg", "77 kg", "78 kg", "79 kg", "80 kg",
            "81 kg", "82 kg", "83 kg", "84 kg", "85 kg","86 kg", "87 kg", "88 kg", "89 kg", "90 kg",
            "91 kg", "92 kg", "93 kg", "94 kg", "95 kg","96 kg", "97 kg", "98 kg", "99 kg", "100 kg",
            "101 kg", "102 kg", "103 kg", "104 kg", "105 kg","106 kg", "107 kg", "108 kg", "109 kg", "110 kg",
            "111 kg", "112 kg", "113 kg", "114 kg", "115 kg","116 kg", "117 kg", "118 kg", "119 kg", "120 kg",
            "121 kg", "122 kg","123 kg", "124 kg", "125 kg", "126 kg", "127 kg","128 kg","129 kg","130 kg",
            "131 kg", "132 kg", "133 kg", "134 kg", "135 kg","136 kg", "137 kg", "138 kg", "139 kg", "140 kg",
            "141 kg", "142 kg", "143 kg", "144 kg", "145 kg","146 kg", "147 kg", "148 kg", "149 kg", "150 kg",
            "151 kg", "152 kg", "153 kg", "154 kg", "155 kg","156 kg", "157 kg", "158 kg", "159 kg", "160 kg",
            "161 kg", "162 kg", "163 kg", "164 kg", "165 kg","166 kg", "167 kg", "168 kg", "169 kg", "170 kg",
            "171 kg", "172 kg", "173 kg", "174 kg", "175 kg","176 kg", "177 kg", "178 kg", "179 kg", "180 kg",
            "181 kg", "182 kg", "183 kg", "184 kg", "185 kg","186 kg", "187 kg", "188 kg", "189 kg", "190 kg",
            "191 kg", "192 kg", "193 kg", "194 kg", "195 kg","196 kg", "197 kg", "198 kg", "199 kg", "200 kg")

        val valuesUkStonesSt = arrayOf(
            "1 st", "2 st", "3 st", "4 st", "5 st","6 st", "7 st", "8 st", "9 st", "10 st",
            "11 st", "12 st", "13 st", "14 st", "15 st","16 st", "17 st", "18 st", "19 st", "20 st",
            "21 st", "22 st","23 st", "24 st", "25 st", "26 st", "27 st","28 st","29 st","30 st",
            "31 st", "32 st", "33 st", "34 st", "35 st","36 st", "37 st", "38 st", "39 st", "40 st")

        val valuesUkStonesLb = arrayOf(
            "1 lb", "2 lb", "3 lb", "4 lb", "5 lb","6 lb", "7 lb", "8 lb", "9 lb", "10 lb",
            "11 lb", "12 lb", "13 lb")

        //Height Array
        val valuesFeet = arrayOf("1 Feet", "2 Feet", "3 Feet", "4 Feet", "5 Feet",
            "6 Feet", "7 Feet", "8 Feet", "9 Feet")

        val valuesInches = arrayOf("0 inches", "1 inches", "2 inches", "3 inches", "4 inches",
            "5 inches", "6 inches", "7 inches", "8 inches", "9 inches","10 inches", "11 inches")

        val valuesMatricHeight = arrayOf(
            "151 cm", "152 cm", "153 cm", "154 cm", "155 cm","156 cm", "157 cm", "158 cm", "159 cm", "160 cm",
            "161 cm", "162 cm", "163 cm", "164 cm", "165 cm","166 cm", "167 cm", "168 cm", "169 cm", "170 cm",
            "171 cm", "172 cm", "173 cm", "174 cm", "175 cm","176 cm", "177 cm", "178 cm", "179 cm", "180 cm",
            "181 cm", "182 cm", "183 cm", "184 cm", "185 cm","186 cm", "187 cm", "188 cm", "189 cm", "190 cm",
            "191 cm", "192 cm", "193 cm", "194 cm", "195 cm","196 cm", "197 cm", "198 cm", "199 cm", "200 cm",
            "201 cm", "202 cm", "203 cm", "204 cm", "205 cm","206 cm", "207 cm", "208 cm", "209 cm", "210 cm",
            "211 cm", "212 cm", "213 cm", "214 cm", "215 cm","216 cm", "217 cm", "218 cm", "219 cm", "220 cm",
            "221 cm", "222 cm","223 cm", "224 cm", "225 cm", "226 cm", "227 cm","228 cm","229 cm","230 cm",
            "231 cm", "232 cm", "233 cm", "234 cm", "235 cm","236 cm", "237 cm", "238 cm", "239 cm", "240 cm",
            "241 cm", "242 cm", "243 cm", "244 cm", "245 cm","246 cm", "247 cm", "248 cm", "249 cm", "250 cm",
            "251 cm", "252 cm", "253 cm", "254 cm", "255 cm","256 cm", "257 cm", "258 cm", "259 cm", "260 cm",
            "261 cm", "262 cm", "263 cm", "264 cm", "265 cm","266 cm", "267 cm", "268 cm", "269 cm", "270 cm",
            "271 cm", "272 cm", "273 cm", "274 cm", "275 cm","276 cm", "277 cm", "278 cm", "279 cm", "280 cm")

    }
 }