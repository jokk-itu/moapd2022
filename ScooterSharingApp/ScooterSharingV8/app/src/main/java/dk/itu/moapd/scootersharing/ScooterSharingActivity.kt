package dk.itu.moapd.scootersharing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.databinding.ActivityScooterSharingBinding

class ScooterSharingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScooterSharingBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScooterSharingBinding.inflate(layoutInflater)
        setupBottomNav()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        binding.topNav.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.signOut -> {
                    auth.signOut()
                    binding.navHostFragment.getFragment<NavHostFragment>().navController.navigate(R.id.signInFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.setupWithNavController(binding.navHostFragment.getFragment<NavHostFragment>().navController)
    }
}