package com.finalyearproject.medicare.retrofit

import com.finalyearproject.medicare.models.ResponseModel
import com.finalyearproject.medicare.models.User
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthService {

    @POST("user/register/")
    fun userRegister(@Body user: User): Call<ResponseModel>

    @POST("user/login/")
    fun userLogIn(@Body requestData: JsonObject): Call<User>

    //key and value pair like {email,''} or {uid,''}
    @GET("user/{key}/{value}")
    fun getUserData(@Path("key") key: String, @Path("value") value: String): Call<User>
}