package com.finalyearproject.medicare.retrofit

import com.finalyearproject.medicare.models.HistoryModel
import com.finalyearproject.medicare.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PatientServiceApi {

    @GET("patient/checkHistory/{patientId}")
    fun checkHistoryOfPatient(@Path("patientId") patientId: String): Call<HistoryModel>

    @POST("patient/showDoctor")
    fun getDoctorList(): Call<List<User>>
}