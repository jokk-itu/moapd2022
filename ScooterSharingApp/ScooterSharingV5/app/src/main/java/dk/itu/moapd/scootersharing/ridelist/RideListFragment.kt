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
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.model.Scooter
import dk.itu.moapd.scootersharing.ScooterSharingActivity.Companion.ridesDB

class RideListFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_ride_list, container, false)
        rideRecyclerView =
            view.findViewById(R.id.ride_recycler_view) as RecyclerView
        rideRecyclerView.layoutManager = LinearLayoutManager(context)

        adapter = RideAdapter()
        rideRecyclerView.adapter = adapter

        return view
    }

    private inner class RideHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                Toast.makeText(context, ride.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        private lateinit var ride: Scooter

        private val nameTextView: TextView = itemView.findViewById(R.id.ride_name)
        private val whereTextView: TextView = itemView.findViewById(R.id.ride_where)
        private val timestampTextView: TextView = itemView.findViewById(R.id.ride_timestamp)

        fun bind(ride: Scooter) {
            this.ride = ride
            nameTextView.text = this.ride.name
            whereTextView.text = this.ride.where
            timestampTextView.text = this.ride.readableTimestamp()
        }
    }

    private inner class RideAdapter : RecyclerView.Adapter<RideHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideHolder {
            val view = layoutInflater.inflate(R.layout.list_item_ride, parent, false)
            return RideHolder(view)
        }

        override fun getItemCount() = ridesDB.getScooters().size

        override fun onBindViewHolder(holder: RideHolder, position: Int) {
            val ride = ridesDB.getScooters()[position];
            holder.bind(ride)
        }
    }

}