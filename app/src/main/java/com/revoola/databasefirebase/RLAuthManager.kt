package com.revoola.databasefirebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RLAuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun rl_registerUser(email: String, password: String, callback: (FirebaseUser?, Exception?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(auth.currentUser, null)
                } else {
                    callback(null, task.exception)
                }
            }
    }


    fun rl_registerGuestUser(callback: (FirebaseUser?, Exception?) -> Unit) {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(auth.currentUser, null)
                } else {
                    callback(null, task.exception)
                }
            }
    }

    fun rl_loginUser(email: String, password: String, callback: (FirebaseUser?, Exception?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(auth.currentUser, null)
                } else {
                    callback(null, task.exception)
                }
            }
    }

    fun rl_forgotPasswordUser(email: String, callback: (String?, Exception?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback("Password reset email sent successfully", null)
                } else {
                    callback(null, task.exception)
                }
            }
    }

    fun rl_getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }



    fun rl_signOut() {
        auth.signOut()
    }


}