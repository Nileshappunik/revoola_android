package com.example.myfirstapp.databasefirebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RLAuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun RLRegisterUser(email: String, password: String, callback: (FirebaseUser?, Exception?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(auth.currentUser, null)
                } else {
                    callback(null, task.exception)
                }
            }
    }

    fun RlloginUser(email: String, password: String, callback: (FirebaseUser?, Exception?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(auth.currentUser, null)
                } else {
                    callback(null, task.exception)
                }
            }
    }

    fun RLForgotPasswordUser(email: String, callback: (String?, Exception?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback("Password reset email sent successfully", null)
                } else {
                    callback(null, task.exception)
                }
            }
    }

    fun RlgetCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun RLsignOut() {
        auth.signOut()
    }


}