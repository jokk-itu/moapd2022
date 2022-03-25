package dk.itu.moapd.scootersharing.editride

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.databinding.ActivityEditRideBinding
import dk.itu.moapd.scootersharing.model.RidesDB

class EditRideActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditRideBinding

    companion object {
        lateinit var ridesDB: RidesDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditRideBinding.inflate(layoutInflater)
        ridesDB = RidesDB.get(this)

        val startRideFragment = supportFragmentManager.findFragmentById(binding.fragmentEditRideContainer.id)

        if(startRideFragment == null) {
            val fragment = EditRideFragment()
            supportFragmentManager
                .beginTransaction()
                .add(binding.fragmentEditRideContainer.id, fragment)
                .commit()
        }

        setContentView(binding.root)
    }

}