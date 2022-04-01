package dk.itu.moapd.scootersharing.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.ScooterSharingActivity
import dk.itu.moapd.scootersharing.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivityLoginBinding.inflate(layoutInflater)

        binding.loginButton.setOnClickListener {
            val username = binding.usernameText.text.toString()
            val password = binding.passwordText.text.toString()
            if(username.isNotBlank() || password.isNotBlank())
                auth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d("Login", "Successfully logged in")
                            startScooterSharingActivity()
                        }
                        else {
                            Log.w("Login", "Error logging in")
                        }
                    }
        }
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null)
            startScooterSharingActivity()
    }

    private fun startScooterSharingActivity() {
        val intent = Intent(this, ScooterSharingActivity::class.java)
        startActivity(intent)
        finish()
    }
}