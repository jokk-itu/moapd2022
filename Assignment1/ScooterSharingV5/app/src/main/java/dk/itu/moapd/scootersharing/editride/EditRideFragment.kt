package dk.itu.moapd.scootersharing.editride

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import dk.itu.moapd.scootersharing.R

class EditRideFragment : Fragment() {

    private lateinit var infoText: EditText
    private lateinit var nameText: EditText
    private lateinit var whereText: EditText

    private lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_ride, container, false)

        infoText = view.findViewById(R.id.info_text)
        nameText = view.findViewById(R.id.name_text)
        whereText = view.findViewById(R.id.where_text)
        updateButton = view.findViewById(R.id.update_button)

        updateButton.setOnClickListener {
            if (nameText.text.isNotEmpty()
                && whereText.text.isNotEmpty()
            ) {
                val name = nameText.text.toString().trim()
                val where = whereText.text.toString().trim()
                val isUpdated = EditRideActivity.ridesDB.updateScooter(name, where)

                if(isUpdated)
                    updateUI()
                else {
                    Toast.makeText(activity, "$name does not exist", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        return view
    }

    private fun updateUI() {
        infoText.setText(EditRideActivity.ridesDB.getLastScooterInfo())
    }
}