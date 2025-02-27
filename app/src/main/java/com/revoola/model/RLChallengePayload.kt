package com.revoola.model

data class RLChallengePayload(
    var groupid_userid: List<String> = emptyList(), // Represents an array of strings
    var groupongroup: String = "", // String property
    var group: String = "", // String property
    var metric: String = "", // String property
    var creationdate: String = "", // String property (date)
    var startdate: String = "", // String property (date)
    var enddate: String = "", // String property (date)
    var goalvalue: String = "", // String property
    var max: String = "", // String property
    var challenger: String = "", // String property
    var displayImage: String = "", // String property
    var typeOfSelect: String = "", // String property
    var targetType: String = "", // String property
    var challenge_name: String = "", // String property
    var day_type: String = "", // String property
    var isChallengeEdit: Boolean = false // Boolean property
)
