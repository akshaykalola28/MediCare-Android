package com.finalyearproject.medicare.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.adapters.ReportAdapter
import com.finalyearproject.medicare.models.Report
import kotlinx.android.synthetic.main.fragment_report_list.*

/**
 * A simple [Fragment] subclass.
 */
class ReportListFragment(private val reports: List<Report>) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        report_recycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = ReportAdapter(
                context,
                this@ReportListFragment,
                reports as ArrayList<Report>
            )
        }
    }

}
