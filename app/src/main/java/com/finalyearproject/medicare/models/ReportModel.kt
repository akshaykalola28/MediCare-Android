package com.finalyearproject.medicare.models

import com.google.gson.annotations.SerializedName

data class ReportModel(
    @SerializedName("reportId") val reportId: Long,
    @SerializedName("reportTitle") val reportTitle: String,
    @SerializedName("doctorId") val doctorId: String,
    @SerializedName("collectingStatus") val collectingStatus: String,
    @SerializedName("doctorName") val doctorName: String,
    @SerializedName("patientId") val patientId: String,
    @SerializedName("reportDescription") val reportDescription: String,
    @SerializedName("date") val date: String,
    @SerializedName("laboratoryEmail") val laboratoryEmail: String,
    @SerializedName("patientName") val patientName: String
)