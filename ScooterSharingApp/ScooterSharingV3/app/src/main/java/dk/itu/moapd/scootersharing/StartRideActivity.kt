package dk.itu.moapd.scootersharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class StartRideActivity : AppCompatActivity() {

    private lateinit var infoText: EditText
    private lateinit var nameText: EditText
    private lateinit var whereText: EditText

    private lateinit var startButton: Button

    private val scooter: Scooter = Scooter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_ride)

        infoText = findViewById(R.id.info_text)
        nameText = findViewById(R.id.name_text)
        whereText = findViewById(R.id.where_text)
        startButton = findViewById(R.id.start_button)

        startButton.setOnClickListener {
            if (nameText.text.isNotEmpty()
                && whereText.text.isNotEmpty()) {
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