package com.revoola.moengage

import android.content.Context
import com.google.gson.Gson
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.*
import com.moengage.core.analytics.MoEAnalyticsHelper
import com.revoola.RLBaseProgress
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.model.RLInsightlyMoEngageResponse
import com.revoola.utils.RLConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


object RELMoengageManager {

    private val TAG = "RLFragYourFriends"
    fun setUserAttributeInMoengage(context : Context, key: String, value: Any) {
        MoEAnalyticsHelper.setUserAttribute(context, key, value)
    }

    fun sendRequest(uid: String,getFullName: String) {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date())

        val moParams: Map<String, Any> = mapOf(
            "uid" to uid,
            "fr_date" to date,
            "fr_asked" to getFullName
        )
       postRequestForUpdateMoengage(moParams)

        val eventParams: Map<String, Any> = mapOf(
            "customer_id" to uid,
            "action" to "fr_asked",
            "datetime_recd" to date,
            "sender_id" to "NA"
        )

       postRequestForCreateMoengageEvent(eventParams)
        setPendigRequest(uid, outstanding = true, otherUid = RLAuthManager().rl_getCurrentUser()?.uid?:"")
    }

    fun setPendigRequest(uid: String, outstanding: Boolean, otherUid: String) {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date())

        val moParams = mapOf(
            "uid" to uid,
            "fr_date" to date,
            "fr_outstanding" to outstanding,
            "fr_requester" to otherUid
        )
       postRequestForUpdateMoengage(moParams)

        val eventParams = mapOf(
            "action" to "fr_outstanding",
            "datetime_recd" to date,
            "sender_id" to uid
        )

      postRequestForCreateMoengageEvent(eventParams)
    }

    fun acceptRequest(uid: String,context: Context,getFullName: String) {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date())

        val moParams = mapOf(
            "uid" to uid,
            "fr_date" to date,
            "fr_accepted" to getFullName
        )
         postRequestForUpdateMoengage(moParams)

        val eventParams = mapOf(
            "customer_id" to uid,
            "action" to "fr_accepted",
            "datetime_recd" to date,
            "sender_id" to "NA"
        )

        postRequestForCreateMoengageEvent(eventParams)
        setUserAttributeInMoengage(context ,"fr_outstanding", false)
    }

    private fun postRequestForUpdateMoengage( moParams: Map<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val mediaType = "application/json".toMediaType()
                val body = Gson().toJson(moParams).toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(RLConstants.baseUrlUpdateMoengage)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                // Log response on background thread
                RLTools.rl_logDPrint(TAG, "Update Moengage Response: $responseBody")
                val apiResponse = Gson().fromJson(responseBody, RLInsightlyMoEngageResponse::class.java)
                // If UI update needed, switch to Main Thread
                CoroutineScope(Dispatchers.Main).launch {
                    if (apiResponse.response.isNotEmpty() && apiResponse.response[0].success == "true") {
                        // Show success message in UI
                        // Handle UI updates if required (e.g., Toast message)
                        RLTools.rl_logDPrint(TAG, "Update Moengage Success: ${apiResponse.response[0].status}")
                    }else{
                        RLBaseProgress.rl_hideProgressDialog()
                        RLTools.rl_logEPrint(TAG, "Update Moengage Error: ${apiResponse.response[0].status}")
                    }

                }

            } catch (e: Exception) {
                RLBaseProgress.rl_hideProgressDialog()
                RLTools.rl_logEPrint(TAG, "Update Moengage Exception: ${e.localizedMessage}")
            }
        }
    }

    private fun postRequestForCreateMoengageEvent( moParams: Map<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val mediaType = "application/json".toMediaType()
                val body = Gson().toJson(moParams).toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(RLConstants.baseUrlCreateEventMoengage)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                // Log response on background thread
                RLTools.rl_logDPrint(TAG, "Moengage Event Response: $responseBody")
                val apiResponse = Gson().fromJson(responseBody, RLInsightlyMoEngageResponse::class.java)
                // If UI update needed, switch to Main Thread
                CoroutineScope(Dispatchers.Main).launch {
                    if (apiResponse.response.isNotEmpty() && apiResponse.response[0].success == "true") {
                        // Show success message in UI
                        // Handle UI updates if required (e.g., Toast message)
                        RLTools.rl_logDPrint(TAG, "Moengage EventSuccess: ${apiResponse.response[0].status}")
                    }else{
                        RLBaseProgress.rl_hideProgressDialog()
                        RLTools.rl_logEPrint(TAG, "Moengage Event Error: ${apiResponse.response[0].status}")
                    }

                }

            } catch (e: Exception) {
                RLBaseProgress.rl_hideProgressDialog()
                RLTools.rl_logEPrint(TAG, "Moengage Event Exception: ${e.localizedMessage}")
            }
        }
    }
}



