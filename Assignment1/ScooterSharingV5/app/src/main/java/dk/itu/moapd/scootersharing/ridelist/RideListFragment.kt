package dk.itu.moapd.scootersharing.ridelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.model.Scooter
import dk.itu.moapd.scootersharing.ScooterSharingActivity.Companion.ridesDB
import dk.itu.moapd.scootersharing.databinding.FragmentRideListBinding
import dk.itu.moapd.scootersharing.databinding.ListItemRideBinding

class RideListFragment : Fragment() {

    private lateinit var binding: FragmentRideListBinding
    private lateinit var rideRecyclerView: RecyclerView
    private lateinit var adapter: RideAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRideListBinding.inflate(layoutInflater);
        rideRecyclerView = binding.rideRecyclerView
        rideRecyclerView.layoutManager = LinearLayoutManager(context)

        adapter = RideAdapter()
        rideRecyclerView.adapter = adapter

        return binding.root;
    }

    override fun onStart() {
        super.onStart()
        adapter.notifyDataSetChanged()
    }

    private inner class RideHolder(binding: ListItemRideBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                Toast.makeText(context, ride.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        private lateinit var ride: Scooter

        private val nameTextView: TextView = binding.rideName
        private val whereTextView: TextView = binding.rideWhere
        private val timestampTextView: TextView = binding.rideTimestamp

        fun bind(ride: Scooter) {
            this.ride = ride
            nameTextView.text = this.ride.name
            whereTextView.text = this.ride.where
            timestampTextView.text = this.ride.readableTimestamp()
        }
    }

    private inner class RideAdapter : RecyclerView.Adapter<RideHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideHolder {
            return RideHolder(ListItemRideBinding.inflate(layoutInflater))
        }

        override fun getItemCount() = ridesDB.getScooters().size

        override fun onBindViewHolder(holder: RideHolder, position: Int) {
            val ride = ridesDB.getScooters()[position];
            holder.bind(ride)
        }
    }
}