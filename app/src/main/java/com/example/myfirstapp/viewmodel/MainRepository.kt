package com.example.myfirstapp.viewmodel
import android.util.Log
import com.example.myfirstapp.api.NetworkService
import com.example.myfirstapp.model.CommonAddModel
import com.example.myfirstapp.model.LogindModel
import com.example.myfirstapp.utils.Constants

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.HashMap
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class MainRepository(private val apiService: NetworkService) {
    val token:String= Constants.TOKEN
    //Login
    fun login(username: String, password: String,device_id:String, callback: (Result<LogindModel>) -> Unit) {
        val params = mapOf(
            "token" to  token,
            "userid" to username,
            "password" to password,
            //"device_id" to device_id)
            "device_id" to "android")

        Log.d("MainRepository","login= "+params)

        apiService.login(params).enqueue(object : Callback<LogindModel> {
            override fun onResponse(call: Call<LogindModel>, response: Response<LogindModel>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    // Handle unsuccessful response
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<LogindModel>, t: Throwable) {
                // Handle failure
                callback(Result.failure(t))
            }
        })
    }

    //leadAdd
    fun leadAdd(session_token: String, full_name: String, mobile: String, mobile_2: String, email: String,
                country: String, state: String, city: String, lead_type_Id: String, lead_source_Id: String,
                kilowatt_recruitment: String, address: String, lightt_bill: File, assign_to: String, lead_status: String,
                notes: String, image: File, callback: (Result<CommonAddModel>) -> Unit) {

        val descriptionList: MutableList<MultipartBody.Part> = java.util.ArrayList()
        var photo1: MultipartBody.Part? = null
        val requestBody1 =RequestBody.create("image/jpeg".toMediaTypeOrNull(),lightt_bill)
        photo1 = MultipartBody.Part.createFormData("lightt_bill", lightt_bill.name, requestBody1)
        descriptionList.add(photo1)
        var photo: MultipartBody.Part? = null
        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(),image)
        photo = MultipartBody.Part.createFormData("image", image.name, requestBody)
        descriptionList.add(photo)

        val map: HashMap<String, RequestBody> = HashMap()
        map.put("token",RequestBody.create("multipart/form-data".toMediaTypeOrNull(), Constants.TOKEN))
        map.put("session_token",RequestBody.create("multipart/form-data".toMediaTypeOrNull(), session_token))
        map.put("full_name",RequestBody.create("multipart/form-data".toMediaTypeOrNull(), full_name))
        map.put("mobile",RequestBody.create("multipart/form-data".toMediaTypeOrNull(), mobile))
        map.put("mobile_2",RequestBody.create("multipart/form-data".toMediaTypeOrNull(), mobile_2))
        map.put("email",RequestBody.create("multipart/form-data".toMediaTypeOrNull(), email))
        map.put("country",RequestBody.create("multipart/form-data".toMediaTypeOrNull(), country))
        map.put("state",RequestBody.create("multipart/form-data".toMediaTypeOrNull(), state))
        map.put("city",RequestBody.create("multipart/form-data".toMediaTypeOrNull(), city))
        map.put("lead_type_Id",RequestBody.create("multipart/form-data".toMediaTypeOrNull(), lead_type_Id))
        map.put("lead_source_Id",RequestBody.create("multipart/form-data".toMediaTypeOrNull(), lead_source_Id))
        map.put("kilowatt_recruitment",RequestBody.create("multipart/form-data".toMediaTypeOrNull(), kilowatt_recruitment))
        map.put("address",RequestBody.create("multipart/form-data".toMediaTypeOrNull(), address))
        map.put("assign_to",RequestBody.create("multipart/form-data".toMediaTypeOrNull(), assign_to))
        map.put("lead_status",RequestBody.create("multipart/form-data".toMediaTypeOrNull(), lead_status))
        map.put("notes",RequestBody.create("multipart/form-data".toMediaTypeOrNull(), notes))

        val mapp: HashMap<String, String> = HashMap()
        mapp.put("token", token)
        mapp.put("session_token", session_token)
        mapp.put("full_name", full_name)
        mapp.put("mobile", mobile)
        mapp.put("mobile_2", mobile_2)
        mapp.put("email",email)
        mapp.put("country", country)
        mapp.put("state", state)
        mapp.put("city", city)
        mapp.put("lead_type_Id", lead_type_Id)
        mapp.put("lead_source_Id", lead_source_Id)
        mapp.put("kilowatt_recruitment",kilowatt_recruitment)
        mapp.put("address", address)
        mapp.put("assign_to", assign_to)
        mapp.put("lead_status", lead_status)
        mapp.put("notes", notes)
        mapp.put("image", image.toString())
        mapp.put("lightt_bill", lightt_bill.toString())

        Log.d("MainRepository","leadAdd= "+mapp)

        apiService.lead_add(map,descriptionList).enqueue(object : Callback<CommonAddModel> {
            override fun onResponse(call: Call<CommonAddModel>, response: Response<CommonAddModel>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    // Handle unsuccessful response
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<CommonAddModel>, t: Throwable) {
                // Handle failure
                callback(Result.failure(t))
                Log.d("MainRepository","failure= "+t.toString())
            }
        })
    }


}