package com.example.myfirstapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myfirstapp.R
import com.example.myfirstapp.utils.RLPrefManager
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class RLSplashFirstActivity : AppCompatActivity() {
    val TAG: String = RLSplashFirstActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rl_activity_splash_first)
        supportActionBar?.hide()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        FirebaseApp.initializeApp(this)
        /*val userId= RLPrefManager.RLgetSomeStringValue(this, RLPrefManager.current_user,"")
        RLPrefManager.RLsetSomeStringValue(this, RLPrefManager.current_user,"w2p8SQCvE3emjEEDo66f02eF6fG2")
        if (userId.isNullOrEmpty()){
            startActivity(Intent(this, RLLoginActivityRL::class.java))
        }else{
            startActivity(Intent(this, RLMainActivityRL::class.java))
        }*/
    }
}