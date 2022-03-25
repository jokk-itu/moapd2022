package dk.itu.moapd.scootersharing

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import dk.itu.moapd.scootersharing.databinding.FragmentScooterSharingBinding
import dk.itu.moapd.scootersharing.editride.EditRideActivity
import dk.itu.moapd.scootersharing.ridelist.RideListFragment
import dk.itu.moapd.scootersharing.startride.StartRideActivity

class ScooterSharingFragment : Fragment() {

    private lateinit var binding: FragmentScooterSharingBinding
    private lateinit var startRideButton: Button
    private lateinit var editRideButton: Button
    private lateinit var listRidesButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScooterSharingBinding.inflate(layoutInflater)

        startRideButton = binding.startRideButton
        editRideButton = binding.editRideButton
        listRidesButton = binding.listRidesButton

        startRideButton.setOnClickListener {
            val intent = Intent(activity, StartRideActivity::class.java)
            startActivity(intent)
        }

        editRideButton.setOnClickListener {
            val intent = Intent(activity, EditRideActivity::class.java)
            startActivity(intent)
        }

        listRidesButton.setOnClickListener {
            val listRidesFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.fragment_ride_list_container)

            if(listRidesFragment == null) {
                val fragment = RideListFragment()
                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.add(R.id.fragment_ride_list_container, fragment)
                    ?.commit()
            }
        }

        return binding.root
    }
}