package dk.itu.moapd.scootersharing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ScooterSharingActivity : AppCompatActivity() {

    private lateinit var startRideButton: Button
    private lateinit var editRideButton: Button
    private lateinit var listRidesButton : Button

    companion object {
        lateinit var ridesDB: RidesDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scooter_sharing)

        startRideButton = findViewById(R.id.start_ride_button)
        editRideButton = findViewById(R.id.edit_ride_button)
        listRidesButton = findViewById(R.id.list_rides_button)

        ridesDB = RidesDB.get(this)

        startRideButton.setOnClickListener {
            val intent = Intent(this, StartRideActivity::class.java)
            startActivity(intent)
        }

        editRideButton.setOnClickListener {
            val intent = Intent(this, EditRideActivity::class.java)
            startActivity(intent)
        }

        listRidesButton.setOnClickListener {
            val listRidesFragment =
                supportFragmentManager.findFragmentById(R.id.fragment_container)

            if(listRidesFragment == null) {
                val fragment = RideListFragment()
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit()
            }
        }
    }
}