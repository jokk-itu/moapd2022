package dk.itu.moapd.scootersharing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dk.itu.moapd.scootersharing.databinding.ActivityScooterSharingBinding
import dk.itu.moapd.scootersharing.model.RidesDB

class ScooterSharingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScooterSharingBinding

    companion object {
        lateinit var ridesDB: RidesDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScooterSharingBinding.inflate(layoutInflater)
        ridesDB = RidesDB.get(this)

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
}