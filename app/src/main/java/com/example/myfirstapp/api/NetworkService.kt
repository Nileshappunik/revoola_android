package com.example.myfirstapp.api

import com.example.myfirstapp.model.CommonAddModel
import com.example.myfirstapp.model.LogindModel
import com.example.myfirstapp.utils.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface NetworkService {

    //LOGIN & LOGOUT
    @FormUrlEncoded
    @POST(Constants.LOGIN)
    fun login(@FieldMap params: Map<String, String>): Call<LogindModel>

    @Multipart
    @POST(Constants.LEAD_ADD)
    fun lead_add(@PartMap hashMap: HashMap<String, RequestBody>, @Part descriptionList: MutableList<MultipartBody.Part>): Call<CommonAddModel>

}