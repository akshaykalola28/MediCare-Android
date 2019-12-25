package com.finalyearproject.medicare.retrofit

import com.finalyearproject.medicare.models.ResponseModel
import com.finalyearproject.medicare.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("user/register/")
    fun userRegister(@Body user: User): Call<ResponseModel>
}