package com.finalyearproject.medicare.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.models.Treatment
import kotlinx.android.synthetic.main.item_treatment.view.*

class TreatmentAdapter(
    private val mContext: Context,
    private val mFragment: Fragment?,
    private val mItems: List<Treatment>
) : RecyclerView.Adapter<TreatmentAdapter.TreatmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreatmentViewHolder {
        return TreatmentViewHolder(
            LayoutInflater.from(mContext).inflate(
                R.layout.item_treatment,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: TreatmentViewHolder, position: Int) {
        try {
            holder.itemView.treatment_patient_name.text = mItems[position].patientName
            holder.itemView.treatment_doctor_name.text = mItems[position].doctorName
            holder.itemView.treatment_id.text = mItems[position].treatmentId
            holder.itemView.treatment_desc.text = mItems[position].treatmentDesc
            holder.itemView.treatment_title.text = mItems[position].treatmentTitle
            holder.itemView.treatment_date.text = mItems[position].date
            holder.itemView.collection_status.text = mItems[position].collectingStatus.capitalize()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class TreatmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}