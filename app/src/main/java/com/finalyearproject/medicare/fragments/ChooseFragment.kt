package com.finalyearproject.medicare.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.finalyearproject.medicare.R
import kotlinx.android.synthetic.main.fragment_choose.*

/**
 * A simple [Fragment] subclass.
 */
class ChooseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login_button.setOnClickListener {
            setupFragment(LogInFragment())
        }

        signup_button.setOnClickListener {
            setupFragment(SignUpFragment())
        }
    }

    private fun setupFragment(fragment: Fragment) {
        val fragmentTransaction = fragmentManager!!.beginTransaction()
            .setCustomAnimations(
                R.anim.sliding_in_left,
                R.anim.sliding_out_right,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
        fragmentTransaction.replace(R.id.auth_fragment, fragment, fragment.tag)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
