package com.finalyearproject.medicare.retrofit

import com.finalyearproject.medicare.models.ReportModel
import com.finalyearproject.medicare.models.ResponseModel
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LabServiceApi {

    @GET("laboratory/reports/{status}")
    fun getReports(@Path("status") status: String): Call<ArrayList<ReportModel>>

    @POST("laboratory/addReport")
    fun addReport(@Body requestData: JsonObject): Call<ResponseModel>
}