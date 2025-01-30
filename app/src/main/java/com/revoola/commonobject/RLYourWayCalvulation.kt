package com.revoola.commonobject

object RLYourWayCalvulation {

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
    private fun isValidValue(value: Any?): Boolean {
        return value!= null && value!= "" &&!value.toString().matches(Regex("\\d+"))
    }
    private fun getIsImperial(appUnit:String):Boolean {
        if (appUnit.equals("Imperial")){
            return true;
        }else if (appUnit.equals("Metric")){
            return true;
        }else{
            return false;
        }
    }

}