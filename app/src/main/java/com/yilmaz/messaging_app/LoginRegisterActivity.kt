package com.yilmaz.messaging_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.yilmaz.messaging_app.databinding.ActivityLoginRegisterBinding
import java.util.concurrent.TimeUnit

class LoginRegisterActivity : AppCompatActivity() {

    // Define a TAG for logging specific to this Activity
    companion object {
        private const val TAG = "LoginRegisterActivity"
    }

    lateinit var loginFragment : Login
    val verificationFragment = Verification()
    lateinit var registerFragment : Register

    lateinit var binding: ActivityLoginRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var storedVerificationId: String? = null
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize Phone Auth callbacks
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
                Toast.makeText(applicationContext, "Verification Completed", Toast.LENGTH_LONG).show()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)
                Toast.makeText(applicationContext, "Verification Failed", Toast.LENGTH_LONG).show()

                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        // Invalid request
                        Toast.makeText(applicationContext, "Invalid request", Toast.LENGTH_SHORT).show()
                    }
                    is FirebaseTooManyRequestsException -> {
                        // SMS quota exceeded
                        Toast.makeText(applicationContext, "SMS quota exceeded", Toast.LENGTH_SHORT).show()
                    }
                    is FirebaseAuthMissingActivityForRecaptchaException -> {
                        // reCAPTCHA verification issue
                        Toast.makeText(applicationContext, "reCAPTCHA issue", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // General error
                        Toast.makeText(applicationContext, "Verification error", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d(TAG, "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token

                // TODO: Navigate to the OTP verification fragment or screen
            }
        }

        // Start with the initial fragment or activity logic
        replaceFragment(Login())
    }
    fun startPhoneNumberVerification(phoneNumber: String) {
        Toast.makeText(applicationContext,"startPhoneNumberVerification", LENGTH_LONG).show()
        // [START start_phone_auth]
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END start_phone_auth]
    }
    override fun onStart() {
        super.onStart()
        // Check if the user is already signed in and update UI accordingly
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is signed in, maybe navigate to the main activity
            navigateToMainActivity()
        }
    }
    private fun verifyPhoneNumberWithCode(verificationId: String?, smsCode: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, smsCode)
        signInWithPhoneAuthCredential(credential)
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(applicationContext, "Login Successful", Toast.LENGTH_LONG).show()
                    val user = task.result?.user
                    if (user != null) {
                        checkUserExists(user)
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(applicationContext, "Invalid SMS code", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }
    private fun checkUserExists(user: FirebaseUser) {
        val userDocRef = db.collection("users").document(user.uid)
        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // User exists, navigate to HomeActivity
                    Toast.makeText(this, "User exists", Toast.LENGTH_SHORT).show()
                    navigateToMainActivity()
                } else {
                    // User does not exist, navigate to FillProfileActivity
                    Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error checking user existence", exception)
                Toast.makeText(this, "Error checking user existence", Toast.LENGTH_SHORT).show()
            }
    }
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.addToBackStack(null) // Optionally add to backstack
        transaction.commit()
    }
}
