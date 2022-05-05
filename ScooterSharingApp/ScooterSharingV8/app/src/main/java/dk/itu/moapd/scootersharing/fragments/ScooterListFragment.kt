package dk.itu.moapd.scootersharing.fragments

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.data.model.Scooter
import dk.itu.moapd.scootersharing.databinding.DialogStartRideBinding
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
    private var currentLocation: Location? = null
    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val latitude = intent?.getDoubleExtra("latitude", 0f.toDouble())
            val longitude = intent?.getDoubleExtra("longitude", 0f.toDouble())

            if (latitude == null || latitude == 0f.toDouble())
                return

            if (longitude == null || longitude == 0f.toDouble())
                return

            val location = Location(LocationManager.GPS_PROVIDER)
            location.latitude = latitude
            location.longitude = longitude
            currentLocation = location
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(locationReceiver, IntentFilter(R.string.location_event.toString()))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(locationReceiver)
    }

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
        rideViewModel.getLiveCurrentRide().observe(viewLifecycleOwner) {
            if(it == null)
                binding.startClosestRideButton.isEnabled = true

            binding.startClosestRideButton.setOnClickListener {
                startClosestRide()
            }
        }
        return binding.root
    }

    inner class ScooterHolder(
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

        @SuppressLint("SetTextI18n")
        fun bind(scooter: Scooter) {
            this.scooter = scooter
            binding.picture.setImageResource(resources.getIdentifier(scooter.picture, "drawable", requireActivity().packageName)) //imageView.setImageResource(R.drawable.my_image);
            binding.name.text = scooter.name
            binding.where.text =
                locationViewModel.toAddress(scooter.lat, scooter.lon) ?: "Address is unavailable"
            binding.startRide.isEnabled = !disableStartRideButton
            binding.battery.text = "${scooter.battery}% power"
        }
    }

    inner class ScooterAdapter(private val scooters: List<Scooter>) :
        RecyclerView.Adapter<ScooterHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScooterHolder {
            val binding = ListItemScooterBinding.inflate(layoutInflater)
            val hasCurrentRide = rideViewModel.getCurrentRide() != null
            return ScooterHolder(binding, hasCurrentRide)
        }

        override fun getItemCount() = scooters.size

        override fun onBindViewHolder(holder: ScooterHolder, position: Int) {
            holder.bind(scooters[position])
        }
    }

    @SuppressLint("SetTextI18n")
    private fun startClosestRide() {
        var retries = 6
        while (currentLocation == null) {
            if(retries == 0) {
                Toast.makeText(requireContext(), "Enable location permissions", Toast.LENGTH_SHORT).show()
                return
            }
            Thread.sleep(1000)
            retries--
        }
        val dialogBinding = DialogStartRideBinding.inflate(layoutInflater)
        val closestScooter = scooterViewModel.getClosestScooter(currentLocation!!)
            ?: return

        dialogBinding.name.text = closestScooter.name
        dialogBinding.where.text = locationViewModel.toAddress(closestScooter.lat, closestScooter.lon) ?: "Address is unavailable"
        dialogBinding.battery.text = "${closestScooter.battery}% power"
        dialogBinding.picture.setImageResource(resources.getIdentifier(closestScooter.picture, "drawable", requireActivity().packageName))

        AlertDialog.Builder(requireActivity()).setMessage(R.string.dialog_start_ride_message).setView(dialogBinding.root)
            .setPositiveButton(R.string.dialog_start_ride_yes) { _, _ ->
                val argument = R.string.start_ride_event.toString()
                val action =
                    ScooterListFragmentDirections.actionScooterFragmentToScanFragment(argument)
                binding.root.findNavController().navigate(action)
            }.setNegativeButton(
                R.string.dialog_start_ride_no
            ) { _, _ ->
                //Do nothing
            }.create().apply { show() }
    }
}