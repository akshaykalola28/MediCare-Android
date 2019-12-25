package com.finalyearproject.medicare.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.activities.AuthActivity
import com.finalyearproject.medicare.helpers.AppProgressDialog
import com.finalyearproject.medicare.models.ResponseModel
import com.finalyearproject.medicare.models.User
import com.finalyearproject.medicare.retrofit.AuthService
import com.finalyearproject.medicare.retrofit.ServiceBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_sign_up.*
import retrofit2.Call
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class SignUpFragment : Fragment() {

    private val mNewUser = User()
    private var requestInterface: AuthService? = null
    var mDialog: AppProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDialog = AppProgressDialog(context!!)

        backImageButton.setOnClickListener {
            (requireActivity() as AuthActivity).onBackPressed()
        }

        signupButton.setOnClickListener {
            if (getValidData()) {
                mDialog!!.showDialog("Please Wait...")
                registerNewUser()
            }
        }
    }

    private fun registerNewUser() {
        requestInterface = ServiceBuilder.buildService(AuthService::class.java)

        val call = requestInterface!!.userRegister(mNewUser)
        call.enqueue(object : retrofit2.Callback<ResponseModel> {
            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                mDialog!!.dismissDialog()
                Log.d("SIGN UP", "Error: $t")
            }

            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                Log.d("SIGN UP", "" + response + "")
                if (response.isSuccessful) {

                    val responseModel: ResponseModel = response.body()!!
                    Log.d("SIGN UP", "" + responseModel.responseSuccess + "")
                    if (responseModel.responseSuccess) {

                        mDialog!!.dismissDialog()
                        Snackbar.make(view!!, responseModel.data!!, Snackbar.LENGTH_INDEFINITE)
                            .setAction("Log In") {
                                (requireActivity() as AuthActivity).onBackPressed()
                            }.show()
                    } else {

                        Log.d("SIGNUP", "Demo: Called...")
                        Snackbar.make(
                            view!!,
                            "User already exists with this email or number. Please Login.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun getValidData(): Boolean {
        var isValid = false

        firstNameInputField.isErrorEnabled = false
        lastNameInputField.isErrorEnabled = false
        nickNameInputField.isErrorEnabled = false
        emailInputField.isErrorEnabled = false
        mobileInputField.isErrorEnabled = false
        passwordInputField.isErrorEnabled = false
        confirmPasswordInputField.isErrorEnabled = false

        when {
            firstNameEditText.text.toString() == "" -> {
                firstNameInputField.error = "Enter first name"
                firstNameInputField.requestFocus()
            }
            lastNameEditText.text.toString() == "" -> {
                lastNameInputField.error = "Enter last name"
                lastNameInputField.requestFocus()
            }
            nickNameEditText.text.toString() == "" -> {
                nickNameInputField.error = "Enter nick name"
                nickNameInputField.requestFocus()
            }
            emailEditText.text.toString() == "" -> {
                emailInputField.error = "Enter email"
                emailEditText.requestFocus()
            }
            mobileEditText.text!!.length != 10 -> {
                mobileInputField.error = "Mobile number should be a length of 10"
                mobileEditText.requestFocus()
            }
            passwordEditText.text!!.length < 6 -> {
                passwordInputField.error = "Password must be a length of 6"
                passwordEditText.requestFocus()
            }
            confirmPasswordEditText.text.toString() != passwordEditText.text.toString() -> {
                confirmPasswordInputField.error = "Confirm password must be same"
                confirmPasswordEditText.requestFocus()
            }
            else -> {
                mNewUser.firstName = firstNameEditText.text.toString()
                mNewUser.lastName = lastNameEditText.text.toString()
                mNewUser.displayName = nickNameEditText.text.toString()
                mNewUser.email = emailEditText.text.toString()
                mNewUser.phoneNumber = mobileEditText.text.toString()
                mNewUser.password = passwordEditText.text.toString()
                mNewUser.user_type = "patient"

                isValid = true
            }
        }

        return isValid
    }
}
