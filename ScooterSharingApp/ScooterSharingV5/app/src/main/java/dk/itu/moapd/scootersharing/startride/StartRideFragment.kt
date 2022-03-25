package dk.itu.moapd.scootersharing.startride

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.databinding.FragmentStartRideBinding
import dk.itu.moapd.scootersharing.startride.StartRideActivity.Companion.ridesDB

class StartRideFragment : Fragment() {

    private lateinit var binding: FragmentStartRideBinding
    private lateinit var infoText: TextInputLayout
    private lateinit var nameText: TextInputLayout
    private lateinit var whereText: TextInputLayout

    private lateinit var addButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentStartRideBinding.inflate(layoutInflater)

        infoText = binding.infoText
        nameText = binding.nameText
        whereText = binding.whereText
        addButton = binding.addButton

        addButton.setOnClickListener {
            if (nameText.editText!!.text.isNotEmpty()
                && whereText.editText!!.text.isNotEmpty()) {
                ridesDB.addScooter(
                    nameText.editText!!.text.toString().trim(),
                    whereText.editText!!.text.toString().trim())
                updateUI()
            }
        }

        return binding.root
    }

    private fun updateUI() {
        infoText.editText!!.setText(ridesDB.getLastScooterInfo())
    }
}