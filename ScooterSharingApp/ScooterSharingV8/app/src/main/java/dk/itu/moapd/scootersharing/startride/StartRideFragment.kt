package dk.itu.moapd.scootersharing.startride

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import dk.itu.moapd.scootersharing.R

class StartRideFragment : Fragment() {

    private lateinit var infoText: EditText
    private lateinit var nameText: EditText
    private lateinit var whereText: EditText

    private lateinit var addButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start_ride, container, false)

        infoText = view.findViewById(R.id.info_text)
        nameText = view.findViewById(R.id.name_text)
        whereText = view.findViewById(R.id.where_text)
        addButton = view.findViewById(R.id.add_button)

        addButton.setOnClickListener {
            if (nameText.text.isNotEmpty()
                && whereText.text.isNotEmpty()) {
                /*ridesDB.addScooter(
                    nameText.text.toString().trim(),
                    whereText.text.toString().trim())*/
            }
        }

        return view
    }
}