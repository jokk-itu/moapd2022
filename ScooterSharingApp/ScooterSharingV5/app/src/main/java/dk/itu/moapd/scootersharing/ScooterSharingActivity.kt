package dk.itu.moapd.scootersharing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dk.itu.moapd.scootersharing.model.RidesDB

class ScooterSharingActivity : AppCompatActivity() {

    companion object {
        lateinit var ridesDB: RidesDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scooter_sharing)
        ridesDB = RidesDB.get(this)

        val scooterSharingFragment = supportFragmentManager.findFragmentById(R.id.fragment_scooter_sharing_container)

        if(scooterSharingFragment == null) {
            val fragment = ScooterSharingFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_scooter_sharing_container, fragment)
                .commit()
        }
    }
}