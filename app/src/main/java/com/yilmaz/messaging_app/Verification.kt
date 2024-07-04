package com.yilmaz.messaging_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class Verification : Fragment(R.layout.fragment_verification) {

    private var activityReference: LoginRegisterActivity? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LoginRegisterActivity) {
            activityReference = context
        } else {
            throw RuntimeException(context.toString() + " must be LoginRegisterActivity")
        }
    }
    private lateinit var verificationEditText: EditText
    private lateinit var resendSmsButton: Button
    private lateinit var confirmButton: Button
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        verificationEditText= view.findViewById(R.id.verificationEditText)
        resendSmsButton= view.findViewById(R.id.resendSMSButton)
        confirmButton= view.findViewById(R.id.confirmSMSButton)

        resendSmsButton.setOnClickListener {
          activityReference?.resendSmsCode()
            startResendButtonCooldown()
        }
        confirmButton.setOnClickListener() {
            val smsCode = verificationEditText.text.toString()
            activityReference?.confirmSmsCode(smsCode)
        }
    }
    override fun onDetach() {
        super.onDetach()
        activityReference = null
    }
    fun startResendButtonCooldown() {
        // Disable the resend SMS button
        resendSmsButton.isEnabled = false

        // Create a CountDownTimer to handle the cooldown period
        object : CountDownTimer(60000, 1000) {
            // Called every second to update the countdown timer
            override fun onTick(millisUntilFinished: Long) {
                // Update the button text to show the remaining time in seconds
                resendSmsButton.text = "Resend SMS (${millisUntilFinished / 1000})"
            }

            // Called when the countdown timer finishes
            override fun onFinish() {
                // Re-enable the resend SMS button
                resendSmsButton.isEnabled = true

                // Reset the button text to its original state
                resendSmsButton.text = "Resend SMS"
            }
        }.start() // Start the countdown timer
    }

}