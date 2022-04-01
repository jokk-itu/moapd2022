package dk.itu.moapd.scootersharing

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.databinding.ActivityScooterSharingBinding
import dk.itu.moapd.scootersharing.login.LoginActivity

class ScooterSharingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScooterSharingBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScooterSharingBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.signOut -> {
                    auth.signOut()
                    startLoginActivity()
                    true
                }
                else -> false
            }
        }

        val scooterSharingFragment = supportFragmentManager.findFragmentById(R.id.fragment_scooter_sharing_container)

        if(scooterSharingFragment == null) {
            val fragment = ScooterSharingFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_scooter_sharing_container, fragment)
                .commit()
        }

        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        if(auth.currentUser == null)
            startLoginActivity()

        val user = auth.currentUser
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}