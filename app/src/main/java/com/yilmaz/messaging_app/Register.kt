package com.yilmaz.messaging_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.yilmaz.messaging_app.databinding.FragmentRegisterBinding

class Register : Fragment(R.layout.fragment_register) {

    private lateinit var phoneNumberEditText: EditText
    private lateinit var registerButton: Button
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phoneNumberEditText= view.findViewById(R.id.phoneNumber)
        registerButton= view.findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val phoneNumber = phoneNumberEditText.text.toString()
            if (phoneNumber.isNotEmpty()) {
                Toast.makeText(requireContext(), "Registering in with: $phoneNumber", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), MainActivity::class.java).putExtra("phoneNumber", phoneNumber))
            } else {
                Toast.makeText(requireContext(), "Please enter your phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }
}