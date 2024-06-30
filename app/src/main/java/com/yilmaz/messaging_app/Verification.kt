package com.yilmaz.messaging_app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class Verification : Fragment(R.layout.fragment_verification) {

    private lateinit var verificationEditText: EditText
    private lateinit var resendSmsButton: Button
    private lateinit var confirmButton: Button
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        verificationEditText= view.findViewById(R.id.verificationEditText)
        resendSmsButton= view.findViewById(R.id.resendSMSButton)
        confirmButton= view.findViewById(R.id.confirmSMSButton)

        resendSmsButton.setOnClickListener {
          TODO("Implement resend SMS functionality")
        }
        confirmButton.setOnClickListener() {
        TODO("Implement confirm SMS functionality")
        }
    }

}