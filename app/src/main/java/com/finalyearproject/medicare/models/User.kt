package com.finalyearproject.medicare.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("uid") var uId: String? = null,
    @SerializedName("firstName") var firstName: String? = null,
    @SerializedName("lastName") var lastName: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("phoneNumber") var phoneNumber: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("displayName") var displayName: String? = null,
    @SerializedName("user_type") var user_type: String? = null
)