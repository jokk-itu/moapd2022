package dk.itu.moapd.scootersharing.editride

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.databinding.FragmentEditRideBinding

class EditRideFragment : Fragment() {

    private lateinit var binding: FragmentEditRideBinding

    private lateinit var infoText: TextInputLayout
    private lateinit var nameText: TextInputLayout
    private lateinit var whereText: TextInputLayout

    private lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditRideBinding.inflate(layoutInflater)

        infoText = binding.infoText
        nameText = binding.nameText
        whereText = binding.whereText
        updateButton = binding.updateButton

        updateButton.setOnClickListener {
            if (nameText.editText!!.text.isNotEmpty()
                && whereText.editText!!.text.isNotEmpty()
            ) {
                val name = nameText.editText!!.text.toString().trim()
                val where = whereText.editText!!.text.toString().trim()
                val isUpdated = EditRideActivity.ridesDB.updateScooter(name, where)

                if(isUpdated)
                    updateUI()
                else {
                    Toast.makeText(activity, "$name does not exist", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        return binding.root
    }

    private fun updateUI() {
        infoText.editText!!.setText(EditRideActivity.ridesDB.getLastScooterInfo())
    }
}