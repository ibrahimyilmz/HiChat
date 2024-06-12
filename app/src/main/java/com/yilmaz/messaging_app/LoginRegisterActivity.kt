package com.yilmaz.messaging_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.yilmaz.messaging_app.databinding.ActivityLoginRegisterBinding


class LoginRegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            replaceFragment(Login())
        }

        binding.registerButton.setOnClickListener(){
            replaceFragment(Register())
            binding.textView.setText("Already have an account?")
            binding.registerButton.visibility = Button.GONE
            binding.loginButton.visibility = Button.VISIBLE
            /*binding.registerButton.isEnabled = false
            binding.loginButton.isEnabled = true*/
        }
        binding.loginButton.setOnClickListener(){
            replaceFragment(Login())
            binding.textView.setText("Don't have an account?")
            binding.registerButton.visibility = Button.VISIBLE
            binding.loginButton.visibility = Button.GONE
            /*binding.registerButton.isEnabled = true
            binding.loginButton.isEnabled = false*/

        }
    }

    fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }
}