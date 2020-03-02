package com.finalyearproject.medicare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.models.Medicine
import kotlinx.android.synthetic.main.item_medicine.view.*

class MedicineAdapter(private val mContext: Context, private val mMedicineList: List<Medicine>) :
    RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        return MedicineViewHolder(
            LayoutInflater.from(mContext).inflate(
                R.layout.item_medicine,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mMedicineList.size
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        holder.itemView.medicine_name.text = mMedicineList[position].name
        holder.itemView.medicine_dose.text = mMedicineList[position].dose
        holder.itemView.medicine_time.text = mMedicineList[position].time
    }

    class MedicineViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {

    }
}