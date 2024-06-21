package com.example.myfirstapp.databasefirebase

import android.content.Intent
import android.util.Log
import com.example.myfirstapp.activity.RLVerificationCodeActivityRL
import com.example.myfirstapp.model.RLUserModel
import com.example.myfirstapp.utils.RLConstants
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class RLMyDatabase {
    private lateinit var RLdatabase: DatabaseReference

    fun RLinitializeDatabase() {
        RLdatabase = FirebaseDatabase.getInstance().reference
    }

  /*  //Cloud Firestore Database
    private fun RLloginapicall1(){
        // Initialize Firestore
        val database = Firebase.firestore
        val db = FirebaseFirestore.getInstance()
        // Initialize Realtime Database
        val realtimedatabase = FirebaseDatabase.getInstance().reference

        // Write a RLuser to Firestore
        val user = RLUserModel(emailID,password)
        database.collection(RLConstants.USERS).add(user)
            .addOnSuccessListener { documentReference ->
                realtimedatabase.child(RLConstants.USERS).child(documentReference.id).setValue(user)
                startActivity(Intent(this, RLVerificationCodeActivityRL::class.java))
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        // Read a RLuser from
        *//* db.collection(RLConstants.USERS).addSnapshotListener { snapshot, exception ->
             if (exception != null) {
                 Log.w(TAG, "Listen failed", exception)
                 RLopentoast("UserID And Password dose not match")
                 return@addSnapshotListener
             }
             if (snapshot != null) {
                 for (doc in snapshot.documents) {
                     // Access document data as a map
                     val getemail = doc.getString("email")
                     val getpassword = doc.getString("password")
                     if (emailID.equals(getemail)&&password.equals(getpassword)){
                         Log.d(TAG, "Login Successful")
                         startActivity(Intent(this, RLVerificationCodeActivityRL::class.java))
                     }
                 }
             } else {
                RLopentoast("UserID And Password dose not match")
             }
         }*//*

    }

    //Realtime Database
    private fun RLloginapicall(){
        // Initialize Realtime Database
        val database = FirebaseDatabase.getInstance().reference
        //writeNewUser
        val user = RLUserModel(emailID, password)
        *//*  database.child(RLConstants.USERS).setValue(RLuser)
              .addOnCompleteListener { task ->
                  if (task.isSuccessful) {
                      Log.d(TAG, "Login Successful")
                      startActivity(Intent(this, RLVerificationCodeActivityRL::class.java))
                  } else {
                      Log.d(TAG, "User write operation failed: ${task.exception?.message}")
                  }
              }
              .addOnSuccessListener {
                  Log.d(TAG, "Login Successful")
                  startActivity(Intent(this, RLVerificationCodeActivityRL::class.java))
              }
              .addOnFailureListener { exception ->
                  Log.d(TAG, "User write operation failed: ${exception.message}")
              }*//*

        // ReadUser
        database.child(RLConstants.LIVE).child(RLConstants.LIVEUSERSEMAIL).get().addOnSuccessListener { response ->
            val gson = Gson()
            // val json = gson.toJson(response.value)

            Log.d(TAG, "Login Successful:-  "+response.value)
            startActivity(Intent(this, RLVerificationCodeActivityRL::class.java))
            *//* val user = gson.fromJson(json, RLUserModel::class.java)
             if (emailID.equals(user?.email)&&password.equals(user?.password)){
                 Log.d(TAG, "Login Successful")
                 startActivity(Intent(this, RLVerificationCodeActivityRL::class.java))
             }else{
                 RLopentoast("UserID And Password dose not match")
             }*//*
        }.addOnFailureListener {
            RLopentoast("UserID And Password dose not match")
        }
    }*/

}