package com.yilmaz.messaging_app

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class Login : Fragment(R.layout.fragment_login) {
    private var activityReference: LoginRegisterActivity? = null
    private lateinit var phoneNumberEditText: EditText
    private lateinit var enterButton: Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LoginRegisterActivity) {
            activityReference = context
        } else {
            throw RuntimeException(context.toString() + " must be LoginRegisterActivity")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phoneNumberEditText = view.findViewById(R.id.phoneNumber)
        enterButton = view.findViewById(R.id.enterButton)

        enterButton.setOnClickListener {
            val phoneNumber = phoneNumberEditText.text.toString()
            if (phoneNumber.isNotEmpty()) {

                activityReference?.startPhoneNumberVerification(phoneNumber)
                activityReference?.verificationFragment?.let { fragment ->
                    if (fragment is Fragment) {
                        activityReference?.replaceFragment(fragment)
                    }
                }
                Toast.makeText(requireContext(), "Verification code sent to $phoneNumber", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please enter your phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        activityReference = null
    }
}