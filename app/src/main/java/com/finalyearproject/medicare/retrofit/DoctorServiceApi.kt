package com.finalyearproject.medicare.retrofit

import com.finalyearproject.medicare.models.HistoryModel
import com.finalyearproject.medicare.models.ResponseModel
import com.finalyearproject.medicare.models.Treatment
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DoctorServiceApi {

    @POST("doctor/reqReport")
    fun requestReport(@Body requestData: JsonObject): Call<ResponseModel>

    @POST("doctor/addTreatment")
    fun addTreatment(@Body treatment: Treatment): Call<ResponseModel>

    @GET("doctor/checkHistory/{patientId}")
    fun checkHistoryOfPatient(@Path("patientId") patientId: String): Call<HistoryModel>
}