package com.revoola.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.revoola.R
import com.revoola.base.RLBaseActivity
import com.revoola.databinding.RlActivitySplashBinding
import com.revoola.utils.RLPrefManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.revoola.RLBaseProgress
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databasefirebase.RLDatabaseManagerWrite
import com.revoola.databasefirebase.RLFirebaseManager
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.revoola.utils.RLConstants

class RLSplashActivityRL : RLBaseActivity() {
    val TAG: String = RLSplashActivityRL::class.java.simpleName
    lateinit var activityBinding: RlActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        RLScreenSet(false)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = RLinflateBindLayout(this, R.layout.rl_activity_splash) as RlActivitySplashBinding
        RLRemoteConfig()
        RLBaseProgress.RLShowProgressDialog(this)
        val userId= RLPrefManager.RLGetSomeStringValue(this, RLPrefManager.current_user,"")
        if (userId.isNullOrEmpty()){
            RLBaseProgress.RLhideProgressDialog()
            activityBinding.btnFullExperience.setOnClickListener {
                RLPrefManager.RLSetSomeBooleanValue(this,RLPrefManager.isGuestUser,false)
                startActivity(Intent(this, RLLoginActivityRL::class.java))
                finish()
            }
            activityBinding.btnGuestUser.setOnClickListener {
                RLBaseProgress.RLShowProgressDialog(this)
                RLPrefManager.RLSetSomeBooleanValue(this,RLPrefManager.isGuestUser,true)
                RLGuestUser()
            }
        }else{
            RLTools.RlLogEPrint(TAG,"USer: $userId")
            if (RLPrefManager.RLGetGuestUser(this)){
                RLBaseProgress.RLhideProgressDialog()
                startActivity(Intent(this, RLMainActivityRL::class.java))
                finish()
            }else{
                RLSetUsernameToFirebase(userId)
            }
        }
    }

    private fun RLGuestUser(){
        val databaseManager = RLDatabaseManagerWrite()
        val authManager = RLAuthManager()
        authManager.RLRegisterGuestUser(){ user, error ->
            if (user != null) {
                val userId = user.uid
                val email = "$userId@guestuser.com"
                val userModel = RLUser(displayImage = user.photoUrl?.toString() ?: "",emailId = email,name = "Guest",userId = userId)
                databaseManager.REVOOLAUSERFORSEARCHWrite(userId,userModel) { success, error ->
                    if (success) {
                        RLRevoolaUserSettingFirebaseEntry(userId,email)
                    }else{
                        RLBaseProgress.RLhideProgressDialog()
                    }
                }
            }else {
                RLBaseProgress.RLhideProgressDialog()
                RLTools.RlLogEPrint(TAG,"Registration failed: ${error?.message}")
            }
        }
    }

    private fun RLRemoteConfig() {

        val remoteConfig = FirebaseRemoteConfig.getInstance()

        // Set default values
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600) // 1 hour
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.rlremote_config_defaults)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this,OnCompleteListener { task ->
                if (task.isSuccessful) {
                    val start_top = remoteConfig.getString("start")
                    val friends_top = remoteConfig.getString("friends_top")
                    val challenge_selectFor = remoteConfig.getString("challenge_selectFor")
                    val challenge_selectTarget = remoteConfig.getString("challenge_selectTarget")
                    val challenge_selectName = remoteConfig.getString("challenge_selectName")
                    RLPrefManager.RLSetSomeStringValue(this, RLPrefManager.start_help_content,start_top.toString())
                    RLPrefManager.RLSetSomeStringValue(this, RLPrefManager.friends_help_content,friends_top.toString())
                    RLPrefManager.RLSetSomeStringValue(this, RLPrefManager.challenge_selectFor,challenge_selectFor.toString())
                    RLPrefManager.RLSetSomeStringValue(this, RLPrefManager.challenge_selectTarget,challenge_selectTarget.toString())
                    RLPrefManager.RLSetSomeStringValue(this, RLPrefManager.challenge_selectName,challenge_selectName.toString())
                } else {
                   RLTools.RlLogEPrint(TAG, "Fetch failed")
                }
            })
    }

    private fun RLSetUsernameToFirebase(userId:String){
        //Firebase To Fetch UserData
        RLDatabaseManagerRead().RlUserBasicDataRead(userId){ data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                val userData = gson.fromJson(jsonObject, RLRevoolaUsersSettingsModel::class.java)
                RLBaseProgress.RLhideProgressDialog()
                if (userData.isBasicDataAdded){

                    startActivity(Intent(this, RLMainActivityRL::class.java))
                    finish()
                }else{
                    startActivity(Intent(this, RLSignUpNameActivityRL::class.java).putExtra("IsNewUser",false))//.putExtra("EmailId",emailId).putExtra("Password",password))
                    finish()
                }
            }
        }

    }

    data class RLUser(
        val displayImage: String,
        val emailId: String,
        val name: String,
        val userId: String){
        //Convert User object to a Map for Firebase Realtime Database
        fun toMap(): Map<String, Any> {
            return mapOf(
                "displayImage" to displayImage,
                "emailId" to emailId,
                "name" to name,
                "firstName" to name,
                "remark" to "Android",
                "userId" to userId)
        }
    }

    private fun RLRevoolaUserSettingFirebaseEntry(userId:String,emailId:String) {
        val versionName: String = try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName ?: "0"
        } catch (e: Exception) {
            "0"
        }
        val firebaseManager = RLFirebaseManager()
        firebaseManager.RLRevoolaUserSettingFirebaseEntry(userId, emailId, versionName) { success ->
            if (success) {
                RLBaseProgress.RLhideProgressDialog()
                RLPrefManager.RLSetSomeStringValue(this, RLPrefManager.current_user, userId)
                RLPrefManager.RLSetSomeStringValue(this, RLPrefManager.current_user_email, emailId)
                startActivity(Intent(this, RLMainActivityRL::class.java))
                finish()
            } else {
                RLBaseProgress.RLhideProgressDialog()
                Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun RLInsertUserServer(userId:String,emailId:String) {
        val userGuest = mapOf(
            "displayImage" to RLConstants.GuestImg,
            "emailId" to emailId,
            "firstName" to "Guest",
            "lastName" to "Guest",
            "name" to "Guest",
            "userId" to userId)
    }
    private fun setMemberProfileAnonymousUser(userId:String,emailId:String) {
        val dob = "01/01/1970"
        val age = 29 //getAge(dob)
        val TMHR = 220 - age
        val AMHR = TMHR
        val RFMHR = TMHR

        val userData = mapOf(
            "height" to 170,
            "heightUnit" to "Metric",
            "weightkg" to 50,
            "weightUnit" to "Metric",
            "gender" to "Male",
            "dob" to dob,
            "TMHR" to TMHR,
            "AMHR" to AMHR,
            "RFMHR" to RFMHR)

        /*val setUpMyProfile = mapOf(
            "email" to emailId,
            "gender" to "Male",
            "date_of_birth" to "01/01/1970",
            "device_type" to "Android",
            "insightlyId" to 0,
            "paidortrial" to userService.subscriptionName,
            "next_payment_date" to userService.subscriptionTime,
            "subscription_end_date" to userService.subscriptionTime)

        val updateUserInsightlyMoe = mapOf(
            "email" to emailId,
            "uid" to userId,
            "gender" to "Male",
            "date_of_birth" to "01/01/1970",
            "device_type" to "Android",
            "next_payment_date" to userService.subscriptionTime,
            "subscription_end_date" to userService.subscriptionTime,
            "referrer" to //linkService.usercode ?: "peak",
            "Is_basic_data_added" to false,
            "paidortrial" to "peak")*/

    }
}