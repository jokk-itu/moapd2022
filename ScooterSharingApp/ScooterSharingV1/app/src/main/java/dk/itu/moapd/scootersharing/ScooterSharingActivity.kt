package dk.itu.moapd.scootersharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class ScooterSharingActivity : AppCompatActivity() {

    private lateinit var lastAddedText: EditText
    private lateinit var nameText: EditText
    private lateinit var whereText: EditText

    private lateinit var addButton: Button

    private val scooter: Scooter = Scooter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scooter_sharing)

        lastAddedText = findViewById(R.id.last_added_text)
        nameText = findViewById(R.id.name_text)
        whereText = findViewById(R.id.where_text)
        addButton = findViewById(R.id.add_button)

        addButton.setOnClickListener { view: View ->
            if (nameText.text.isNotEmpty()
                && whereText.text.isNotEmpty()) {
                val name = nameText.text.toString().trim()
                val where = whereText.text.toString().trim()
                scooter.name = name
                scooter.where = where

                updateUI()
            }
        }
    }

    private fun updateUI() {
        lastAddedText.setText(scooter.toString())
    }
}