package dk.itu.moapd.scootersharing.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.viewmodels.RideViewModel
import dk.itu.moapd.scootersharing.data.model.Ride
import dk.itu.moapd.scootersharing.databinding.FragmentAccountBinding
import dk.itu.moapd.scootersharing.databinding.ListItemRideBinding
import dk.itu.moapd.scootersharing.viewmodels.LocationViewModel
import java.util.*

class AccountFragment : Fragment() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: FragmentAccountBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RideAdapter
    private lateinit var rideViewModel: RideViewModel
    private lateinit var locationViewModel: LocationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(layoutInflater)
        binding.email.text = auth.currentUser!!.email
        rideViewModel = ViewModelProvider(this)[RideViewModel::class.java]
        locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]
        rideViewModel.getLiveCurrentRide().observe(requireActivity()) {
            if(it == null)
                return@observe

            val relativeLayout = RelativeLayout(requireContext())
            val scooterIdTextView = TextView(requireContext())
            val addressTextView = TextView(requireContext())
            val endRideButton = Button(requireContext())
            scooterIdTextView.text = it.scooterId.toString()
            addressTextView.text = locationViewModel.toLocation(it.currentLat, it.currentLon)
            endRideButton.setOnClickListener {
                val argument = R.string.end_ride_event.toString()
                val action = AccountFragmentDirections.actionAccountFragmentToScanFragment(argument)
                binding.root.findNavController().navigate(action)
            }
            relativeLayout.addView(scooterIdTextView)
            relativeLayout.addView(addressTextView)
            relativeLayout.addView(endRideButton)
            binding.rideView.addView(relativeLayout)
            binding.rideView.layoutParams.height = 20
            binding.rideView.layoutParams.width = binding.root.layoutParams.width
        }

        recyclerView = binding.rideRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        rideViewModel.getRides().observe(viewLifecycleOwner) {
            adapter = RideAdapter(it)
            recyclerView.adapter = adapter
        }

        return binding.root
    }

    private inner class RideHolder(binding: ListItemRideBinding) :
        RecyclerView.ViewHolder(binding.root) {

        lateinit var ride: Ride

        private val nameTextView: TextView = itemView.findViewById(R.id.ride_name)
        private val whereTextView: TextView = itemView.findViewById(R.id.ride_where)
        private val timestampTextView: TextView = itemView.findViewById(R.id.ride_timestamp)

        fun bind(ride: Ride) {
            this.ride = ride
            nameTextView.text = ride.scooterId.toString()
            whereTextView.text = ""
            timestampTextView.text = Calendar.getInstance().time.toString()
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

        fun remove(ride: Ride, position: Int) {
            rideViewModel.deleteRide(ride)
            notifyItemRemoved(position)
        }
    }
}