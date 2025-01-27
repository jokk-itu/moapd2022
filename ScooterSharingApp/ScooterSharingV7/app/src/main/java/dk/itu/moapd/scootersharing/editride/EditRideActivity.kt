package dk.itu.moapd.scootersharing.editride

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.data.RidesDB

class EditRideActivity : AppCompatActivity() {


    companion object {
        lateinit var ridesDB: RidesDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_ride)
        ridesDB = RidesDB.get(this)

        val startRideFragment = supportFragmentManager.findFragmentById(R.id.fragment_edit_ride_container)

        if(startRideFragment == null) {
            val fragment = EditRideFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_edit_ride_container, fragment)
                .commit()
        }
    }

}