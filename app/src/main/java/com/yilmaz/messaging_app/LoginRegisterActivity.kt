package com.yilmaz.messaging_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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






        fun replaceFragment(fragment: Fragment){
        Log.d("LoginRegisterActivity", "binding: $binding")
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }
    }
}
