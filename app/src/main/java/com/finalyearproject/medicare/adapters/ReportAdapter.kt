package com.finalyearproject.medicare.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.activities.LabHomeActivity
import com.finalyearproject.medicare.helpers.Constants
import com.finalyearproject.medicare.models.Report
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.item_report.view.*
import java.util.*


open class ReportAdapter(
    var context: Context,
    var fragment: Fragment?,
    var reports: ArrayList<Report>
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    private val requestData = JsonObject()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        return ReportViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_report,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return reports.size
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {

        holder.itemView.report_patient_id_text.text = reports[position].patientId
        holder.itemView.report_doctor_id_text.text = reports[position].doctorId
        holder.itemView.report_patient_name_text.text = reports[position].patientName
        holder.itemView.report_doctor_name_text.text = reports[position].doctorName
        holder.itemView.report_id_text.text = reports[position].reportId.toString()
        holder.itemView.report_desc_text.text = reports[position].reportDescription
        holder.itemView.report_title_text.text = reports[position].reportTitle
        holder.itemView.report_date_text.text = reports[position].date
        holder.itemView.report_status_text.text = reports[position].collectingStatus.capitalize()

        requestData.addProperty("patientId", reports[position].patientId)
        requestData.addProperty("reportId", reports[position].reportId.toString())

        if (context is LabHomeActivity) {
            holder.itemView.setOnClickListener {
                if (reports[position].collectingStatus == Constants.STATUS_PENDING) {
                    val callback = context as ReportCallbackInterface
                    callback.onAddReportClick(requestData)
                } else {
                    Toast.makeText(context, "Already Uploaded.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    interface ReportCallbackInterface {
        fun onAddReportClick(requestData: JsonObject)
    }
}