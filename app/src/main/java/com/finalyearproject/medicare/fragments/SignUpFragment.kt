package com.finalyearproject.medicare.fragments


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.activities.AuthActivity
import com.finalyearproject.medicare.activities.AuthActivity.Companion.TAG
import com.akshaykalola.skydialog.SkyDialog
import com.finalyearproject.medicare.managers.UploadManagement
import com.finalyearproject.medicare.models.ResponseModel
import com.finalyearproject.medicare.models.User
import com.finalyearproject.medicare.retrofit.AuthService
import com.finalyearproject.medicare.retrofit.ServiceBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_sign_up.*
import retrofit2.Call
import retrofit2.Response
import kotlin.math.round


/**
 * A simple [Fragment] subclass.
 */
@Suppress("DEPRECATION")
class SignUpFragment : Fragment() {

    private val mNewUser = User()
    private var requestInterface: AuthService? = null
    var mDialog: SkyDialog? = null

    private var profileImageLink: String? = null
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //This should be add for scroll fragment while keybard is opens.
        activity!!.window
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDialog = SkyDialog(context!!)

        backImageButton.setOnClickListener {
            (requireActivity() as AuthActivity).onBackPressed()
        }

        userProfileImage.setOnClickListener {
            val i = Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(i, 100)
        }

        signupButton.setOnClickListener {
            if (getValidData()) {
                mDialog!!.show("Please Wait...")
                registerNewUser()
            }
        }
    }

    private fun registerNewUser() {
        requestInterface = ServiceBuilder.buildService(AuthService::class.java)

        val call = requestInterface!!.userRegister(mNewUser)
        call.enqueue(object : retrofit2.Callback<ResponseModel> {
            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                mDialog!!.dismiss()
                Log.d("SIGN UP", "Error: $t")
            }

            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                Log.d("SIGN UP", "" + response + "")
                if (response.isSuccessful) {
                    mDialog!!.dismiss()
                    val responseModel: ResponseModel = response.body()!!
                    Log.d("SIGN UP", "" + responseModel.status + "")
                    if (responseModel.status == 200) {
                        Snackbar.make(
                            view!!,
                            responseModel.message!!.toString(),
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction("Log In") {
                            (requireActivity() as AuthActivity).onBackPressed()
                        }.show()
                    } else {
                        Snackbar.make(
                            view!!,
                            responseModel.message.toString(),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                } else if (response.code() == 409) {
                    mDialog!!.dismiss()
                    val gson = GsonBuilder().create()
                    val resData: ResponseModel =
                        gson.fromJson(response.errorBody()!!.string(), ResponseModel::class.java)
                    Toast.makeText(
                        context,
                        resData.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
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
            selectedImageUri == null -> {
                Toast.makeText(context, "Please select profile image", Toast.LENGTH_SHORT).show()
            }
            profileImageLink == null && selectedImageUri != null -> {
                uploadProfileImage(selectedImageUri!!)
            }
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
                mNewUser.profileUrl = profileImageLink

                Log.d("User Data", mNewUser.toString())

                isValid = true
            }
        }

        return isValid
    }

    private fun uploadProfileImage(uri: Uri) {
        mDialog!!.show()
        UploadManagement
            .uploadImageFromUri(uri, UploadManagement.UPLOAD_PATH_PROFILE)
            .addOnProgressListener {
                val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                mDialog!!.message("Upload is ${round(progress)}% done.")
            }
            .addOnSuccessListener {
                mDialog!!.dismiss()
                val result = it.metadata!!.reference!!.downloadUrl
                result.addOnSuccessListener { uri ->
                    profileImageLink = uri.toString()
                    signupButton.performClick()
                    Log.d(TAG, "Success $profileImageLink")
                }
            }.addOnFailureListener {
                mDialog!!.dismiss()
                Toast.makeText(context, "Upload task failed. Please try again.", Toast.LENGTH_SHORT)
                    .show()
                it.printStackTrace()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            userProfileImage.setImageURI(selectedImageUri)
        }
    }
}
