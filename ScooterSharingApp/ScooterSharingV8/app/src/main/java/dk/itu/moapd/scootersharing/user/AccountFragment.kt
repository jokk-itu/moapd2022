package dk.itu.moapd.scootersharing.user

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.viewmodels.RideViewModel
import dk.itu.moapd.scootersharing.data.model.Ride
import dk.itu.moapd.scootersharing.databinding.FragmentAccountBinding
import dk.itu.moapd.scootersharing.databinding.ListItemRideBinding
import java.util.*

class AccountFragment : Fragment() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: FragmentAccountBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RideAdapter
    private lateinit var rideViewModel: RideViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(layoutInflater)

        rideViewModel = ViewModelProvider(this)[RideViewModel::class.java]

        recyclerView = binding.rideRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        rideViewModel.getRides().observe(viewLifecycleOwner) {
            adapter = RideAdapter(it)
            recyclerView.adapter = adapter
        }

        setItemTouchHelper()

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

    private fun setItemTouchHelper() {
        ItemTouchHelper(object : ItemTouchHelper.Callback() {

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(0, ItemTouchHelper.RIGHT)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.RIGHT) {
                    AlertDialog.Builder(requireActivity())
                        .setMessage(R.string.dialog_delete_scooter_message).setPositiveButton(
                            R.string.dialog_delete_scooter_yes
                    ) { _, _ ->
                        val rideHolder = viewHolder as? RideHolder
                        adapter.remove(rideHolder!!.ride, viewHolder.adapterPosition)
                    }.setNegativeButton(
                            R.string.dialog_delete_scooter_no
                    ) { _, _ -> adapter.notifyItemChanged(viewHolder.adapterPosition) }.create()
                        .apply {
                            setCanceledOnTouchOutside(false)
                            show()
                        }
                }
            }
        }).apply { attachToRecyclerView(recyclerView) }
    }

}