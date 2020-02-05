package com.finalyearproject.medicare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.models.ReportModel
import kotlinx.android.synthetic.main.item_report.view.*

class ReportAdapter(
    var context: Context,
    var fragment: Fragment?,
    var reports: ArrayList<ReportModel>
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

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

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]

        holder.itemView.report_patient_id_text.text = reports[position].patientId
        holder.itemView.report_doctor_id_text.text = reports[position].doctorId
        holder.itemView.report_patient_name_text.text = reports[position].patientName
        holder.itemView.report_doctor_name_text.text = reports[position].doctorName
        holder.itemView.report_id_text.text = reports[position].reportId.toString()
        holder.itemView.report_desc_text.text = reports[position].reportDescription
        holder.itemView.report_title_text.text = reports[position].reportTitle
        holder.itemView.report_date_text.text = reports[position].date
        holder.itemView.report_status_text.text = reports[position].collectingStatus
    }


    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}