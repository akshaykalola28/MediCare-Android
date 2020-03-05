package com.finalyearproject.medicare.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.adapters.TreatmentAdapter
import com.finalyearproject.medicare.models.Treatment
import kotlinx.android.synthetic.main.fragment_treatment_list.*


/**
 * A simple [Fragment] subclass.
 */
class TreatmentListFragment(private val treatments: List<Treatment>) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_treatment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TREATMENT", treatments[0].toString())
        treatment_recycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = TreatmentAdapter(
                context,
                this@TreatmentListFragment,
                treatments as ArrayList<Treatment>
            )
        }
    }
}
