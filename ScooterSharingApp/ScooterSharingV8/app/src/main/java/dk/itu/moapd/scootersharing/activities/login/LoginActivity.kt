package dk.itu.moapd.scootersharing.activities.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.activities.ScooterSharingActivity
import dk.itu.moapd.scootersharing.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), LoginListener {

    private lateinit var binding: ActivityLoginBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onLogin() {
        val intent = Intent(this, ScooterSharingActivity::class.java)
        startActivity(intent)
        finish()
    }
}