package com.finalyearproject.medicare.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.activities.AuthActivity
import com.finalyearproject.medicare.activities.AuthActivity.Companion.TAG
import com.finalyearproject.medicare.activities.DoctorHomeActivity
import com.finalyearproject.medicare.activities.LabHomeActivity
import com.finalyearproject.medicare.helpers.AppProgressDialog
import com.finalyearproject.medicare.helpers.AppSharedPreference
import com.finalyearproject.medicare.helpers.Constants
import com.finalyearproject.medicare.managers.UserManagement
import com.finalyearproject.medicare.models.User
import com.finalyearproject.medicare.retrofit.AuthService
import com.finalyearproject.medicare.retrofit.ServiceBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_log_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 */
class LogInFragment : Fragment() {

    private var mRequestData: JsonObject? = JsonObject()
    private var requestInterface: AuthService? = null
    private var mDialog: AppProgressDialog? = null
    private lateinit var notificationToken: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDialog = AppProgressDialog(context!!)
        generateFirebaseNotificationToken()

        backImageButton.setOnClickListener {
            (requireActivity() as AuthActivity).onBackPressed()
        }

        loginButton.setOnClickListener {
            if (getValidData()) {
                mDialog!!.show()
                userValidation()
            }
        }
    }

    private fun userValidation() {
        requestInterface = ServiceBuilder.buildService(AuthService::class.java)
        val requestCall = requestInterface!!.userLogIn(mRequestData!!)
        requestCall.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d(TAG, "onFailure(): $t")
                Toast.makeText(context!!, "Server Error!!", Toast.LENGTH_SHORT).show()
                mDialog!!.dismiss()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                mDialog!!.dismiss()
                when {
                    response.isSuccessful -> {
                        val responseData: User = response.body()!!

                        AppSharedPreference(context!!).saveString(
                            Constants.PREF_USER_ID,
                            responseData.uId!!
                        )
                        AppSharedPreference(context!!).saveString(
                            Constants.PREF_USER_NAME,
                            responseData.displayName!!
                        )
                        AppSharedPreference(context!!).saveString(
                            Constants.PREF_API_TOKEN,
                            responseData.token!!
                        )
                        AppSharedPreference(context!!).saveString(
                            Constants.PREF_USER_TYPE,
                            responseData.user_type!!
                        )

                        UserManagement.openHomeActivity(context!!, responseData.user_type!!)
                    }
                    response.code() == 401 -> {
                        Snackbar.make(view!!, "Invalid Email or Password", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    else -> {
                        Toast.makeText(
                            context!!,
                            "Please try again...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun generateFirebaseNotificationToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get new Instance ID token
                    val token = task.result?.token
                    notificationToken = token!!
                    // Log and toast
                    Log.d(TAG, token)
                }
            }
    }

    private fun getValidData(): Boolean {
        var isValid = false

        when {
            emailEditText.text!!.isEmpty() -> {
                emailInputField.error = "Enter valid email"
                emailInputField.requestFocus()
            }
            passwordEditText.text!!.isEmpty() -> {
                passwordInputField.error = "Enter valid password"
                passwordEditText.requestFocus()
            }
            else -> {
                mRequestData!!.addProperty("email", emailEditText.text!!.toString())
                mRequestData!!.addProperty("password", passwordEditText.text!!.toString())
                mRequestData!!.addProperty("notificationToken", notificationToken)
                isValid = true
            }
        }
        return isValid
    }
}
