package com.finalyearproject.medicare.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_doctor.view.*

class DoctorAdapter : RecyclerView.Adapter<DoctorAdapter.DoctorVH>() {

    private val differCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uId == newItem.uId
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    val doctorList = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorVH {
        return DoctorVH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_doctor, parent, false)
        )
    }

    override fun getItemCount(): Int = doctorList.currentList.size

    override fun onBindViewHolder(holder: DoctorVH, position: Int) {
        val user = doctorList.currentList[position]
        holder.itemView.apply {
            Picasso.get()
                .load(if (user.profileUrl.isNullOrEmpty()) "https://" else user.profileUrl)
                .error(R.drawable.medicare_logo)
                .into(item_doctor_image)
            item_doctor_name.text = user.displayName
            item_doctor_email.text = user.email
            phone_number.text = user.phoneNumber
        }
    }

    class DoctorVH(itemView: View) : RecyclerView.ViewHolder(itemView)
}