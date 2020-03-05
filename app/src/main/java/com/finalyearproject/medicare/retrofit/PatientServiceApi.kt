package com.finalyearproject.medicare.retrofit

import com.finalyearproject.medicare.models.HistoryModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PatientServiceApi {

    @GET("patient/checkHistory/{patientId}")
    fun checkHistoryOfPatient(@Path("patientId") patientId: String): Call<HistoryModel>
}