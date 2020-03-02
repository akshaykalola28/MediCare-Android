package com.finalyearproject.medicare.models

import com.google.gson.annotations.SerializedName

data class Medicine(

    @SerializedName("name") val name: String,
    @SerializedName("dose") val dose: String,
    @SerializedName("time") val time: String
)
