package com.yilmaz.messaging_app

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit
import android.content.Intent
import android.os.CountDownTimer
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG

class MainActivity : AppCompatActivity() {
    // Declare Auth and Firestore variables
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    companion object {
        private const val TAG = "LoginPage: PhoneAuthActivity"
    }
    // End declaring Auth and Firestore variables

    // Xml variables
    lateinit var smsCodeEditText: EditText
    lateinit var confirmSMSButton: Button
    lateinit var resendSMSButton: Button
    // End Xml variables

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge mode
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        // End initialize Firebase Auth and Firestore

        // Initialize Xml variables
        smsCodeEditText = findViewById(R.id.smsCodeEditText)
        confirmSMSButton = findViewById(R.id.confirmSMSButton)
        confirmSMSButton.setOnClickListener {
            val smsCode = smsCodeEditText.text.toString()
            verifyPhoneNumberWithCode(storedVerificationId, smsCode)
        }
        resendSMSButton = findViewById(R.id.resendSMSButton)
        resendSMSButton.setOnClickListener {
            resendVerificationCode(auth.currentUser!!.phoneNumber!!, resendToken)
        }
        // End initialize Xml variables


        // Initialize Phone Auth callbacks
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
                Toast.makeText(applicationContext,"onVerificationCompleted",LENGTH_LONG).show();
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)
                Toast.makeText(applicationContext,"onVerificationFailed",LENGTH_LONG).show();

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
                }

                // TODO: Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }
        }


        val phoneNumber = intent.getStringExtra("phoneNumber");
        Log.d(TAG, "Phone number: $phoneNumber")
        startPhoneNumberVerification(phoneNumber!!)
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
    }
    // [END on_start_check_user]

    private fun startPhoneNumberVerification(phoneNumber: String) {
        Toast.makeText(applicationContext,"startPhoneNumberVerification",LENGTH_LONG).show()
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

    private fun verifyPhoneNumberWithCode(verificationId: String?, smsCode: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, smsCode)
        signInWithPhoneAuthCredential(credential)
        // [END verify_with_code]
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(applicationContext,"Login Successful",LENGTH_LONG).show();

                    val user = task.result?.user
                    checkUserExists(user!!)
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(applicationContext,"SMS code is invalid",LENGTH_LONG).show();
                    }
                    // TODO: Update UI
                }
            }
    }

    // [START resend_verification]
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?,
    ) {

        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // (optional) Activity for callback binding
            // If no activity is passed, reCAPTCHA verification can not be used.
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())

        Toast.makeText(applicationContext,"Re-Sending SMS code",LENGTH_LONG).show();

        // Start the timer for the resend button
        startResendButtonCooldown()
    }
    // [END resend_verification]

    /**
     * Disables the resend SMS button for a 60-second cooldown period.
     * Updates the button text to show the remaining cooldown time.
     * Re-enables the button and resets the text after the cooldown period.
     */
    private fun startResendButtonCooldown() {
        // Disable the resend SMS button
        resendSMSButton.isEnabled = false

        // Create a CountDownTimer to handle the cooldown period
        object : CountDownTimer(60000, 1000) {
            // Called every second to update the countdown timer
            override fun onTick(millisUntilFinished: Long) {
                // Update the button text to show the remaining time in seconds
                resendSMSButton.text = "Resend SMS (${millisUntilFinished / 1000})"
            }

            // Called when the countdown timer finishes
            override fun onFinish() {
                // Re-enable the resend SMS button
                resendSMSButton.isEnabled = true

                // Reset the button text to its original state
                resendSMSButton.text = "Resend SMS"
            }
        }.start() // Start the countdown timer
    }

    /**
     * Checks if a user exists in the Firestore database.
     * If the user exists, navigates to HomeActivity.
     * If the user does not exist, navigates to FillProfileActivity.
     * Handles errors by logging and showing a toast message.
     *
     * @param user The FirebaseUser object representing the currently authenticated user.
     */
    private fun checkUserExists(user: FirebaseUser) {
        val userDocRef = db.collection("users").document(user.uid)

        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // User exists, navigate to HomeActivity
                    Toast.makeText(this, "User exists", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // User does not exist, navigate to FillProfileActivity
                    Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error checking user existence", exception)
                Toast.makeText(this, "Error checking user existence", Toast.LENGTH_SHORT).show()
            }
    }
}