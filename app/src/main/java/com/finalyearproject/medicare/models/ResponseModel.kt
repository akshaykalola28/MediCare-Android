package com.finalyearproject.medicare.models

import com.google.gson.JsonObject

data class ResponseModel(
    val auth: Boolean = false,
    val responseSuccess: Boolean = false,
    val data: String? = null
)