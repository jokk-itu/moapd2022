package dk.itu.moapd.scootersharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class EditRideActivity : AppCompatActivity() {

    private lateinit var infoText: EditText
    private lateinit var nameText: EditText
    private lateinit var whereText: EditText

    private lateinit var updateButton: Button

    private val scooter: Scooter = Scooter()

    companion object {
        lateinit var ridesDB: RidesDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_ride)

        infoText = findViewById(R.id.info_text)
        nameText = findViewById(R.id.name_text)
        whereText = findViewById(R.id.where_text)
        updateButton = findViewById(R.id.update_button)
        ridesDB = RidesDB.get(this)

        updateButton.setOnClickListener {
            if (nameText.text.isNotEmpty()
                && whereText.text.isNotEmpty()
            ) {
                scooter.apply {
                    this.name = nameText.text.toString().trim();
                    this.where = whereText.text.toString().trim()
                }
                updateUI()
            }
        }
    }

    private fun updateUI() {
        infoText.setText(scooter.toString())
    }
}