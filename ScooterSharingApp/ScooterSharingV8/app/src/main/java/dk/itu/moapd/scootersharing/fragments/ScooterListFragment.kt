package dk.itu.moapd.scootersharing.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.data.model.Scooter
import dk.itu.moapd.scootersharing.databinding.FragmentScooterListBinding
import dk.itu.moapd.scootersharing.databinding.ListItemScooterBinding
import dk.itu.moapd.scootersharing.viewmodels.LocationViewModel
import dk.itu.moapd.scootersharing.viewmodels.RideViewModel
import dk.itu.moapd.scootersharing.viewmodels.ScooterViewModel

class ScooterListFragment : Fragment() {

    private lateinit var binding: FragmentScooterListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScooterAdapter
    private lateinit var scooterViewModel: ScooterViewModel
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var rideViewModel: RideViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScooterListBinding.inflate(layoutInflater)
        scooterViewModel = ViewModelProvider(requireActivity())[ScooterViewModel::class.java]
        locationViewModel = ViewModelProvider(requireActivity())[LocationViewModel::class.java]
        rideViewModel = ViewModelProvider(requireActivity())[RideViewModel::class.java]
        recyclerView = binding.scooterRecyclerView
        scooterViewModel.getAvailableScooters().observe(viewLifecycleOwner) {
            adapter = ScooterAdapter(it)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)
        }
        return binding.root
    }

    private inner class ScooterHolder(
        private val binding: ListItemScooterBinding,
        private val disableStartRideButton: Boolean
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.startRide.setOnClickListener {
                val ride = rideViewModel.getCurrentRide()
                if (ride != null) {
                    Toast.makeText(context, "You can only have one ride", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val argument = R.string.start_ride_event.toString()
                val action =
                    ScooterListFragmentDirections.actionScooterFragmentToScanFragment(argument)
                binding.root.findNavController().navigate(action)
            }
        }

        lateinit var scooter: Scooter

        fun bind(scooter: Scooter) {
            this.scooter = scooter
            binding.name.text = scooter.name
            binding.where.text =
                locationViewModel.toLocation(scooter.lat, scooter.lon) ?: "Address is unavailable"
            binding.startRide.isEnabled = !disableStartRideButton
        }
    }

    private inner class ScooterAdapter(private val scooters: List<Scooter>) :
        RecyclerView.Adapter<ScooterHolder>() {

        private val hasCurrentRide: Boolean = rideViewModel.getCurrentRide() != null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScooterHolder {
            val binding = ListItemScooterBinding.inflate(layoutInflater)
            return ScooterHolder(binding, hasCurrentRide)
        }

        override fun getItemCount() = scooters.size

        override fun onBindViewHolder(holder: ScooterHolder, position: Int) {
            holder.bind(scooters[position])
        }
    }
}