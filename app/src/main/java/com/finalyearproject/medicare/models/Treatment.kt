package com.finalyearproject.medicare.models

import com.google.gson.annotations.SerializedName

data class Treatment(
    @SerializedName("patientId") val patientId: String,
    @SerializedName("doctorId") val doctorId: String,
    @SerializedName("doctorName") val doctorName: String,
    @SerializedName("patientName") val patientName: String,
    @SerializedName("treatmentTitle") val treatmentTitle: String,
    @SerializedName("treatmentDetail") val treatmentDesc: String,
    @SerializedName("medicines") val medicines: List<Medicine>,
    @SerializedName("reportId") val reportId: String,
    @SerializedName("medicalStoreEmail") val medicalStoreEmail: String,
    @SerializedName("collectingStatus") val collectingStatus: String,
    @SerializedName("treatmentId") val treatmentId: String? = null
)