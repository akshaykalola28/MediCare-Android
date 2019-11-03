package com.finalyearproject.medicare.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.finalyearproject.medicare.R

/**
 * A simple [Fragment] subclass.
 */
class LogInFragment : Fragment() {

    var mainView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_log_in, container, false)



        return mainView
    }


}
