package com.yilmaz.messaging_app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast


class Login : Fragment(R.layout.fragment_login){

    private lateinit var phoneNumberEditText: EditText
    private lateinit var loginButton: Button
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phoneNumberEditText= view.findViewById(R.id.phoneNumber)
        loginButton= view.findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val phoneNumber = phoneNumberEditText.text.toString()
            if (phoneNumber.isNotEmpty()) {
                Toast.makeText(requireContext(), "Logging in with: $phoneNumber", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), MainActivity::class.java).putExtra("phoneNumber", phoneNumber))
            } else {
                Toast.makeText(requireContext(), "Please enter your phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }

}