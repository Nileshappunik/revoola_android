package com.revoola.moengage

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.moengage.core.MoEngage
//import com.moengage.core.model.Gender
//import com.moengage.core.model.MoEngageProperties
//import com.moengage.inapp.MoEngageInApp
//import com.moengage.inapp.MoEngageInAppDelegate
//import com.moengage.inapp.enums.NudgePosition
//import com.moengage.inbox.MoEngageInbox
//import com.moengage.inbox.model.MoEngageInboxMessage
import java.text.SimpleDateFormat
import java.util.*
object RELMoengageManager {

}
//object RELMoengageManager {
//
//    private lateinit var moEngage: MoEngage
//    private lateinit var inbox: MoEngageInbox
//    private lateinit var inApp: MoEngageInApp
//
//    /** Initialize MoEngage SDK instances */
//    fun initialize(context: Context) {
//        moEngage = MoEngage.getInstance()
//        inbox = MoEngageInbox.getInstance(context)
//        inApp = MoEngageInApp.getInstance(context)
//    }
//
//    /** Set up basic user data and attributes */
//    fun setupInitialData(context: Context) {
//        val user = RELAccountManager.getInstance().getUser()
//
//        moEngage.setUniqueId(user.globalUid)
//        moEngage.setFirstName(user.firstName)
//        moEngage.setLastName(user.lastName)
//        moEngage.setEmail(user.emailID.lowercase(Locale.getDefault()))
//        moEngage.setGender(
//            if (user.gender.lowercase(Locale.getDefault()) == "male") Gender.MALE else Gender.FEMALE
//        )
//
//        // Custom attributes
//        moEngage.setUserAttribute(RELAnalyticsProperty.Photo.value, user.displayImage)
//        moEngage.setUserAttribute(RELAnalyticsProperty.Height.value, user.height.toDoubleOrNull() ?: 80.0)
//        moEngage.setUserAttribute(RELAnalyticsProperty.Weight.value, user.weight.toDoubleOrNull() ?: 50.0)
//
//        val dob = RELAccountManager.getInstance().getDob().second
//        dob?.let {
//            val previousMonthStart = Date().startOfPreviousMonth()
//            if (it.before(previousMonthStart)) {
//                moEngage.setDateOfBirth(it)
//            }
//        }
//
//        moEngage.setUserAttribute(RELAnalyticsProperty.UnitOfMeasure.value, user.appUnit.lowercase(Locale.getDefault()))
//        moEngage.setUserAttribute(RELAnalyticsProperty.Device.value, "Android")
//        moEngage.setUserAttribute(RELAnalyticsProperty.DeviceType.value, "Android")
//    }
//
//    /** Reset MoEngage user on logout */
//    fun logoutUser() {
//        moEngage.resetUser()
//    }
//
//    /** Add a single user attribute */
//    fun setUserAttributeInMoengage(key: String, value: Any) {
//        moEngage.setUserAttribute(key, value)
//    }
//
//    /** Add multiple attributes at once */
//    fun addUserAttributesToMoengage(attributes: Map<String, Any>) {
//        attributes.forEach { (key, value) ->
//            moEngage.setUserAttribute(key, value)
//        }
//    }
//
//    /** Set current in-app context and show nudge */
//    fun setAppContext(contexts: List<String>, controller: Any) {
//        inApp.setCurrentInAppContexts(contexts)
//        contexts.firstOrNull()?.let { setUserAttributeInMoengage("current_page", it) }
//        showInApp(controller)
//    }
//
//    /** Remove in-app context */
//    fun removeAppContext() {
//        inApp.invalidateInAppContexts()
//    }
//
//    /** Track event in MoEngage */
//    fun createEventInMoengage(id: String, parameters: Map<String, Any>) {
//        val properties = MoEngageProperties().apply {
//            parameters.forEach { (k, v) -> this.addAttribute(k, v) }
//        }
//        moEngage.trackEvent(id, properties)
//    }
//
//    /** Mark an inbox notification as read */
//    fun markNotificationAsRead(entry: MoEngageInboxMessage) {
//        entry.campaignId?.let { inbox.markInboxNotificationClicked(it) }
//    }
//
//    /** Clear all inbox notifications */
//    fun clearNotification() {
//        inbox.removeInboxMessages()
//    }
//
//    /** Get unread notification count */
//    fun getUnreadNotificationCount(callback: (Int) -> Unit) {
//        inbox.getUnreadNotificationCount { count, _ ->
//            callback(count)
//        }
//    }
//
//    /** Set up MoEngage data on server for initial user */
//    fun setupInitialUser() {
//        val user = FirebaseAuth.getInstance().currentUser ?: return
//        val email = user.email ?: return
//        val dob = RELAccountManager.getInstance().getDob().second?.toISOString() ?: ""
//        val params = mapOf(
//            "email" to email,
//            "uid" to RELAccountManager.getInstance().getUser().globalUid,
//            "device_type" to "android",
//            "gender" to RELAccountManager.getInstance().getUser().gender,
//            "date_of_birth" to "${dob}T00:00:00Z"
//        )
//        REVAPICaller.getInstance().postRequestForUpdateMoengage(params) { _, _, _, _ -> }
//    }
//
//    /** Show in-app messages */
//    fun showInApp(controller: Any) {
//        inApp.showNudge(NudgePosition.TOP)
//        inApp.showNudge(NudgePosition.BOTTOM)
//        inApp.showInApp()
//        if (controller is MoEngageInAppDelegate) {
//            inApp.setInAppDelegate(controller)
//        }
//    }
//}

/** Extension function to convert Date to ISO string */
fun Date.toISOString(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(this)
}

/** Extension function to get start of previous month */
fun Date.startOfPreviousMonth(): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.MONTH, -1)
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.time
}
