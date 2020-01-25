package com.finalyearproject.medicare.retrofit

import com.finalyearproject.medicare.models.ResponseModel
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface DoctorServiceApi {

    @POST("doctor/reqReport")
    fun requestReport(@Body requestData: JsonObject): Call<ResponseModel>
}