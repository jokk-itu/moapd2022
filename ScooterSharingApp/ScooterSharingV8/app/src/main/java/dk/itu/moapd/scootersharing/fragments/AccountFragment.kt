package dk.itu.moapd.scootersharing.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.data.model.Ride
import dk.itu.moapd.scootersharing.databinding.FragmentAccountBinding
import dk.itu.moapd.scootersharing.databinding.ListItemRideBinding
import dk.itu.moapd.scootersharing.viewmodels.LocationViewModel
import dk.itu.moapd.scootersharing.viewmodels.RideViewModel

class AccountFragment : Fragment() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: FragmentAccountBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RideAdapter
    private lateinit var rideViewModel: RideViewModel
    private lateinit var locationViewModel: LocationViewModel

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

        recyclerView = binding.rideRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        rideViewModel.getRides().observe(viewLifecycleOwner) {
            adapter = RideAdapter(it)
            recyclerView.adapter = adapter
        }

        return binding.root
    }

    private inner class RideHolder(private val binding: ListItemRideBinding) :
        RecyclerView.ViewHolder(binding.root) {

        lateinit var ride: Ride

        fun bind(ride: Ride) {
            this.ride = ride
            binding.rideName.text = ride.scooterId.toString()
            binding.rideWhere.text = locationViewModel.toLocation(ride.currentLat, ride.currentLon)
            binding.rideTimestamp.text = ride.start.toString()
            binding.rideEnd.isEnabled = ride.end == null && ride.endLat == null && ride.endLon == null
        }
    }

    private inner class RideAdapter(private val rides: List<Ride>) :
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