package com.finalyearproject.medicare.models

import com.google.gson.annotations.SerializedName

data class HistoryModel(
    @SerializedName("medicinesData") var treaments: List<Treatment>,
    @SerializedName("reportsData") var reports: List<Report>
)
