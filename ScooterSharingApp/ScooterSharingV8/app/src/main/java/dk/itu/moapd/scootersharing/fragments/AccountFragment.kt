package dk.itu.moapd.scootersharing.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.data.model.Ride
import dk.itu.moapd.scootersharing.databinding.FragmentAccountBinding
import dk.itu.moapd.scootersharing.databinding.ListItemRideBinding
import dk.itu.moapd.scootersharing.viewmodels.LocationViewModel
import dk.itu.moapd.scootersharing.viewmodels.RideViewModel
import dk.itu.moapd.scootersharing.viewmodels.ScooterViewModel
import java.text.SimpleDateFormat
import java.util.*

class AccountFragment : Fragment() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: FragmentAccountBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RideAdapter
    private lateinit var rideViewModel: RideViewModel
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var scooterViewModel: ScooterViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(layoutInflater)

        binding.name.text = "Hello, ${auth.currentUser!!.displayName}!"
        binding.name.textSize = 18f

        rideViewModel = ViewModelProvider(requireActivity())[RideViewModel::class.java]
        locationViewModel = ViewModelProvider(requireActivity())[LocationViewModel::class.java]
        scooterViewModel = ViewModelProvider(requireActivity())[ScooterViewModel::class.java]

        recyclerView = binding.rideRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        rideViewModel.getRides().observe(viewLifecycleOwner) {
            adapter = RideAdapter(it)
            recyclerView.adapter = adapter
        }

        return binding.root
    }

    inner class RideHolder(private val binding: ListItemRideBinding) :
        RecyclerView.ViewHolder(binding.root) {

        lateinit var ride: Ride

        fun getDate(time: Long) : String {
            val format = "dd/MM/yyyy"
            val formatter = SimpleDateFormat(format, Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = time
            return formatter.format(calendar.time)
        }

        @SuppressLint("SetTextI18n")
        fun bind(ride: Ride) {
            val scooter = scooterViewModel.getScooter(ride.scooterId)
            this.ride = ride
            val startDate = getDate(ride.start)
            val endDate = if(ride.end == null) "Not ended" else getDate(ride.end)
            val price = if(ride.price == null) "Not ended" else ride.price.toString()
            binding.rideName.text = scooter.name
            binding.rideWhere.text = locationViewModel.toAddress(scooter.lat, scooter.lon)
            binding.rideTimestamp.text = "Start: $startDate, End: $endDate"
            binding.rideEnd.isEnabled = ride.end == null && ride.endLat == null && ride.endLon == null && ride.price == null
            binding.ridePrice.text = "Cost: $price"
            if(binding.rideEnd.isEnabled) {
                binding.rideEnd.setOnClickListener {
                    val argument = R.string.end_ride_event.toString()
                    val action =
                        AccountFragmentDirections.actionAccountFragmentToScanFragment(argument)
                    binding.root.findNavController().navigate(action)
                }
            }
        }
    }

    inner class RideAdapter(private val rides: List<Ride>) :
        RecyclerView.Adapter<RideHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideHolder {
            val binding = ListItemRideBinding.inflate(layoutInflater)
            return RideHolder(binding)
        }

        override fun getItemCount() = rides.size

        override fun onBindViewHolder(holder: RideHolder, position: Int) {
            holder.bind(rides[position])
        }
    }
}