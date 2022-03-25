package dk.itu.moapd.scootersharing.startride

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.databinding.ActivityScooterSharingBinding
import dk.itu.moapd.scootersharing.databinding.ActivityStartRideBinding
import dk.itu.moapd.scootersharing.model.RidesDB

class StartRideActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartRideBinding

    companion object {
        lateinit var ridesDB: RidesDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartRideBinding.inflate(layoutInflater)

        ridesDB = RidesDB.get(this)

        val startRideFragment = supportFragmentManager.findFragmentById(binding.fragmentStartRideContainer.id)

        if(startRideFragment == null) {
            val fragment = StartRideFragment()
            supportFragmentManager
                .beginTransaction()
                .add(binding.fragmentStartRideContainer.id, fragment)
                .commit()
        }

        setContentView(binding.root)
    }
}