package dk.itu.moapd.scootersharing.startride

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.data.RidesDB

class StartRideActivity : AppCompatActivity() {

    companion object {
        lateinit var ridesDB: RidesDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_ride)

        ridesDB = RidesDB.get(this)

        val startRideFragment = supportFragmentManager.findFragmentById(R.id.fragment_start_ride_container)

        if(startRideFragment == null) {
            val fragment = StartRideFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_start_ride_container, fragment)
                .commit()
        }
    }
}