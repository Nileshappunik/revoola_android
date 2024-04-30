package com.example.myfirstapp.model

data class LogindModel(var status_code:String,var message:String,var data:List<DataLogin>)

data class DataLogin(var  session_token:String,var access_token:String,var id:String,var guard:String,var f_name:String,var l_name:String,var mobile:String,var email:String,var image:String,var status:String,var department_id:String)
