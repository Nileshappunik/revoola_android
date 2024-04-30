package com.example.myfirstapp.viewmodel
import androidx.lifecycle.ViewModel
import com.example.myfirstapp.model.CommonAddModel
import com.example.myfirstapp.model.LogindModel

import okhttp3.MultipartBody
import java.io.File

class MainViewModel(val mainRepository:MainRepository): ViewModel() {

    //Login
    fun login(username: String, password: String,device_id:String, callback: (Result<LogindModel>) -> Unit) {
        mainRepository.login(username, password,device_id, callback)
    }

    //leadAdd
    fun leadAdd(session_token: String, full_name: String, mobile: String, mobile_2: String, email: String,
                country: String, state: String, city: String, lead_type_Id: String, lead_source_Id: String,
                kilowatt_recruitment: String, address: String, lightt_bill: File, assign_to: String,
                lead_status: String, notes: String, image:File, callback: (Result<CommonAddModel>) -> Unit) {
        mainRepository.leadAdd(session_token,full_name,mobile,mobile_2,email,
            country,state,city,lead_type_Id,lead_source_Id,
            kilowatt_recruitment,address,lightt_bill,assign_to,lead_status, notes,image, callback)
    }


}