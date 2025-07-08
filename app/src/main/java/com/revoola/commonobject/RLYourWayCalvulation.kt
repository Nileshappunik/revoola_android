package com.revoola.commonobject

import com.revoola.enumclass.RLValueOvName
import com.revoola.model.RlOverviewGraphData
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlin.math.roundToInt

object RLYourWayCalvulation {

     fun safeInt(value: Any?): Int {
        return when (value) {
            is Int -> value
            is Number -> value.toInt()
            is String -> value.toIntOrNull() ?: 0
            else -> 0
        }
    }


    public fun calculateREVPer(heartRate: Int, weight: Double, height: Double, age: Int, gender: String,RestingHR:String,RFMHR:Int): Double {

        val currentDI = 1.0
        val RH = RestingHR.toInt()

        val BPM = heartRate
        val BMI = (weight / (height * height)) * 10000
        val BMV = when {
            BMI > 39.99 -> BMI * 0.05
            BMI > 24.99 -> (BMI - 24.99) / 3
            BMI < 18.51 -> (18.51 - BMI) / 3
            else -> 0.0
        }

        val RI = 1.0

        val TMHRM = RFMHR
        val TMHRF = RFMHR

        val RITMHRM = TMHRM * RI
        val RITMHRF = TMHRF * RI

        val REVPer = if (gender.uppercase() == "MALE") {
            val DIACTTMHRM = RITMHRM * currentDI
            val DIACTHRR = DIACTTMHRM - RH
            val per = ((BPM - RH) / DIACTHRR) * 100
            per * (1 + BMV / 100)
        } else {
            val DIACTTMHRF = RITMHRF * currentDI
            val DIACTHRR = DIACTTMHRF - RH
            val per = ((BPM - RH) / DIACTHRR) * 100
            per * (1 + BMV / 100)
        }

        return REVPer
    }

    public fun RLmax(previous: Int, next: Int): Int {
        return when {
            previous > next -> previous
            else ->next
        }
    }

    public fun RLmin(previous: Int, next: Int): Int {
        return when {
            next == 0 -> previous
            previous == 0 -> next
            else -> next
        }
    }

    public fun avgOfArray(array: List<Double>): Double {
        val sum = array?.sumOf { if (!it.isNaN() && it.isFinite()) it else 0.0 } ?: 0.0
        return if (array?.size ?: 0 <= 1) 0.0 else sum / (array.size - 1)
    }

    public fun calculateCurrentCalories(gender: String, age: Int, weight: Double, heartRate: Double,RestingHR:String,RFMHR:Int): Double {
        if (heartRate != 0.0) {
            val total = when (gender.toLowerCase()) {
                "male" -> {
                    val maxHrVal = 0.6309 * RFMHR
                    val weightVal = weight * 0.1988
                    val ageVal = age * 0.2017
                    (-55.0969 + maxHrVal + weightVal + ageVal) / 4.184
                }
                else -> {
                    val maxHrVal = 0.4472 * RFMHR
                    val weightVal = weight * 0.1263
                    val ageVal = age * 0.074
                    (-20.4022 + maxHrVal + weightVal + ageVal) / 4.184
                }
            }

            val maxCaloriesHour = total * 36
            val maxCaloriesMin = maxCaloriesHour / 60
            val maxCaloriesSec = maxCaloriesMin / 60

            val rh = RestingHR.toInt()

            val hrRange = heartRate - rh
            val hrRangeMax = RFMHR - rh
            val revPer = hrRange / hrRangeMax

            return maxCaloriesSec * revPer
        }
        return 0.0
    }

    public fun noNanValueDouble(value:Double):Double{
        if (value.isNaN()){
            return 0.00
        }else{
            return  value
        }
    }

    public fun RlGetValueInt(value:String):Int{
        if (value.isNullOrEmpty()){
            return 0
        }else if(value.toDouble() < 0) {
            return 0
        }else{
            return value.toDouble().toInt()
        }
    }

    public fun RlGetValueDouble(value:String):Double{
        if (value.isNullOrEmpty()){
            return 0.0
        } else if(value.toDouble() < 0) {
            return 0.0
        }else{
            return value.toDouble()
        }
    }

    public fun RLformatElapsedTime(elapsedTime: Long): String {
        val seconds = (elapsedTime / 1000) % 60
        val minutes = (elapsedTime / (1000 * 60)) % 60
        val hours = (elapsedTime / (1000 * 60 * 60)) % 24
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    public fun calculatePace(speed: Float): Int {
        // Cadence is typically measured in revolutions per minute (RPM)
        // We can estimate cadence based on speed, assuming a typical stride length
        // of 2.5 meters per revolution
        val strideLength = 2.5f
        val cadence = (speed / strideLength) * 60
        return cadence.toInt()
    }

    public fun getClimbData(elevation: Number,appUnit:String): String {
        if (!isValidValue(elevation)) return "0"
        val convertedElevation = if (getIsImperial(appUnit)) elevation.toDouble() * 3.281 else elevation.toDouble()
        val climbData=convertedElevation?:0
       val clm= "%.2f".format(climbData).toString()
        return clm.toString()
    }
    // check the valid value or not return 0
     fun isValidValue(value: Any?): Boolean {
        return value!= null && value!= "" &&!value.toString().matches(Regex("\\d+"))
    }
     fun getIsImperial(appUnit:String):Boolean {
        if (appUnit.equals("Imperial")){
            return true;
        }else if (appUnit.equals("Metric")){
            return true;
        }else{
            return false;
        }
    }


     fun RLGetValueForTitle(title: String, cardOvData: RlOverviewGraphData,appUnit:String): String {
        val isImperial = RLTools.RLGetIsImperial(appUnit)

        return when (title) {
            RLValueOvName.Effort  -> RLTools.RLformatCommasInt(convertToInt(cardOvData.total_REV))
            RLValueOvName.MaxEffort  -> RLTools.RLformatCommasInt(convertToInt(cardOvData.max_REV_per_session))
            RLValueOvName.AvgEffort  -> RLTools.RLformatCommasInt(convertToInt(cardOvData.avg_REV_per_session))

            RLValueOvName.Awards  -> {
                val totalAwards = cardOvData.medals_bronze + cardOvData.medals_silver + cardOvData.medals_gold
                if (totalAwards != 0) totalAwards.toString() else "0"
            }
            RLValueOvName.Steps  -> RLTools.RLformatCommasInt(convertToInt(cardOvData.total_steps))
            RLValueOvName.MaxSteps  -> RLTools.RLformatCommasInt(convertToInt(cardOvData.max_steps_per_session))
            RLValueOvName.AvgSteps  -> RLTools.RLformatCommasInt(convertToInt(cardOvData.avg_steps_per_session))
            RLValueOvName.MaxDailySteps  -> RLTools.RLformatCommasInt(convertToInt(cardOvData.max_daily_steps))
            RLValueOvName.AvgDailySteps  -> RLTools.RLformatCommasInt(convertToInt(cardOvData.avg_daily_steps))

            RLValueOvName.Distance  -> RLConvertDistance(isImperial,cardOvData)
            RLValueOvName.DistanceNormal  -> RLTools.RLformatCommas(convertToDouble(cardOvData.total_distance))
            RLValueOvName.MaxDistance  -> RLTools.RLformatCommas(convertToDouble(cardOvData.max_distance_per_session) )
            RLValueOvName.AvgDistance  -> RLTools.RLformatCommas(convertToDouble(cardOvData.avg_distance_per_session))

            RLValueOvName.Relaxation -> RLTools.RLminutesget(convertToInt(cardOvData.total_rmm))
            RLValueOvName.MaxRelaxation -> convertToInt(cardOvData.max_rmm_per_session).toString()
            RLValueOvName.AvgRelaxation -> convertToInt(cardOvData.avg_rmm_per_session).toString()
            RLValueOvName.TotalCalories -> convertToInt(cardOvData.total_calories).toString()
            RLValueOvName.ActiveCalories -> convertToInt(cardOvData.active_calories).toString()

            RLValueOvName.AvgClimbed -> convertToInt(cardOvData.avg_elevation_per_session).toString()
            RLValueOvName.MaxClimbed -> convertToInt(cardOvData.max_elevation_per_session).toString()
            RLValueOvName.ClimbedNormal -> RLTools.RLformatCommasInt(convertToInt(cardOvData.total_elevation))
            RLValueOvName.Climbed  -> {
                val demsElevation:Int = convertToInt(cardOvData.total_elevation?:-1)
                val elevation = if (!isImperial) {
                    if (demsElevation == -1) "Pending" else RLTools.RLformatCommasInt(demsElevation)
                } else {
                    if (demsElevation == -1) "Pending" else RLTools.RLformatCommasInt((demsElevation * 3.28084).toInt())
                }
                elevation.toString()
            }
            RLValueOvName.Session -> convertToInt(cardOvData.session).toString()
            RLValueOvName.AvgSession -> RLTools.RLminutesget(convertToInt(cardOvData.avg_time_per_session))
            RLValueOvName.LongestSession -> RLTools.RLminutesget(convertToInt(cardOvData.max_time_per_session))
            RLValueOvName.MaxCalories -> convertToInt(cardOvData.max_total_calories_per_session).toString()
            RLValueOvName.AvgCalories -> convertToInt(cardOvData.avg_total_calories_per_session).toString()
            RLValueOvName.MaxDailyTotal -> convertToInt(cardOvData.max_daily_total_calories).toString()
            RLValueOvName.MaxDailyActive -> convertToInt(cardOvData.max_daily_active_calories).toString()
            RLValueOvName.AvgDailyTotal -> convertToInt(cardOvData.avg_daily_total_calories).toString()
            RLValueOvName.AvgDailyActive -> convertToInt(cardOvData.avg_daily_active_calories).toString()
            else -> "0"
        }

    }
     fun RLConvertDistance(isImperial: Boolean, cardOvData: RlOverviewGraphData): String {
        val distance=convertToDouble(cardOvData.total_distance)
        if (!isImperial) {
            return RLTools.RLformatCommas(distance)
        } else{
            return  RLTools.RLformatCommas(distance * 0.621371)
        }
    }
     fun convertToInt(value: Any): Int {
        return when (value) {
            is Double -> value.roundToInt()
            is Float -> value.roundToInt()
            is Int -> value
            is String -> value.toDoubleOrNull()?.roundToInt() ?: 0
            else -> 0 // Default fallback for unsupported types
        }
    }
     fun convertToDouble(value: Any): Double {
        return when (value) {
            is Double -> if (value.isFinite()) value else 0.0
            is Float -> if (value.isFinite()) value.toDouble() else 0.0
            is Int -> value.toDouble()
            is Long -> value.toDouble()
            is String -> value.toDoubleOrNull()?.takeIf { it.isFinite() } ?: 0.0
            else -> 0.0
        }
    }
     fun RLGetKmMiles(km:String,miles:String,appUnit:String):String{
        val isImperial = RLTools.RLGetIsImperial(appUnit)
        return if (isImperial) miles else km
    }
     data class DateRange(val fromDate: Long?, val toDate: Long?)
    fun getDateRangeForPeriod(period: String): DateRange {
        val today = LocalDate.now()
        val fromDate: LocalDate?
        val toDate: LocalDate?

        when (period) {
            "This Month" -> {
                // Get the previous month
                val previousMonth = today.minusMonths(1)
                fromDate = previousMonth.withDayOfMonth(1)
                toDate = today.withDayOfMonth(1) // First day of current month
            }
            "Last 3 Months" -> {
                // Quarter-based logic: get the previous quarter
                val currentQuarter = ((today.monthValue - 1) / 3) + 1 // 1, 2, 3, or 4
                val previousQuarter = if (currentQuarter == 1) 4 else currentQuarter - 1
                val targetYear = if (currentQuarter == 1) today.year - 1 else today.year

                val startMonth = (previousQuarter - 1) * 3 + 1 // Q1=1, Q2=4, Q3=7, Q4=10
                fromDate = LocalDate.of(targetYear, startMonth, 1)

                // toDate is the start of current quarter
                val currentQuarterStartMonth = (currentQuarter - 1) * 3 + 1
                toDate = LocalDate.of(today.year, currentQuarterStartMonth, 1)
            }
            "Last 6 Months" -> {
                // Half-year logic: if in 2nd half (Jul-Dec), take 1st half (Jan-Jun)
                // if in 1st half (Jan-Jun), take 2nd half of previous year (Jul-Dec)
                val currentHalfYear = if (today.monthValue <= 6) 1 else 2

                if (currentHalfYear == 2) {
                    // Currently in 2nd half, take 1st half of same year
                    fromDate = LocalDate.of(today.year, 1, 1)
                    // toDate is start of current half (July 1st)
                    toDate = LocalDate.of(today.year, 7, 1)
                } else {
                    // Currently in 1st half, take 2nd half of previous year
                    fromDate = LocalDate.of(today.year - 1, 7, 1)
                    // toDate is start of current half (January 1st)
                    toDate = LocalDate.of(today.year, 1, 1)
                }
            }
            "This Year" -> {
                fromDate = today.minusYears(1).withDayOfYear(1) // First day of previous year
                toDate = today.withDayOfYear(1) // First day of current year
            }
            else -> {
                fromDate = null
                toDate = null
            }
        }

        RLTools.RlLogDPrint("RLFragOverviewSession","period: $period   fromDate: $fromDate , toDate: $toDate")
        return DateRange(
            fromDate = fromDate?.atStartOfDay()?.atOffset(ZoneOffset.UTC)?.toEpochSecond(),
            toDate = toDate?.atTime(23, 59, 59)?.atOffset(ZoneOffset.UTC)?.toEpochSecond()
        )
    }
     fun convertDateStringToTimestamp(dateString: String): Long {
        if (dateString.isEmpty()) return 0
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy", Locale.getDefault())
        val localDate = LocalDate.parse(dateString, formatter)
        // Convert to start of day in UTC and get timestamp
        return localDate.atStartOfDay().atOffset(ZoneOffset.UTC).toEpochSecond()
    }
}