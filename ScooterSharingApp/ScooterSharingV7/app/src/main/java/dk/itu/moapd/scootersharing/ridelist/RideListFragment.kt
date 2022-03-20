package dk.itu.moapd.scootersharing.ridelist

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.ScooterSharingActivity.Companion.ridesDB
import dk.itu.moapd.scootersharing.data.model.Scooter

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

        setItemTouchHelper()

        return view
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
                if(direction == ItemTouchHelper.RIGHT) {
                    AlertDialog.Builder(requireActivity()).setMessage(R.string.dialog_delete_scooter_message).setPositiveButton(
                        R.string.dialog_delete_scooter_yes
                    ) { _, _ ->
                        adapter.remove(viewHolder.adapterPosition)
                    }.setNegativeButton(
                        R.string.dialog_delete_scooter_no
                    ) { _, _ -> adapter.notifyItemChanged(viewHolder.adapterPosition)}.create().apply { show() }
                }
            }
        }).apply { attachToRecyclerView(rideRecyclerView) }
    }

    private inner class RideHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                Toast.makeText(context, scooter.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        private lateinit var scooter: Scooter

        private val nameTextView: TextView = itemView.findViewById(R.id.ride_name)
        private val whereTextView: TextView = itemView.findViewById(R.id.ride_where)
        private val timestampTextView: TextView = itemView.findViewById(R.id.ride_timestamp)

        fun bind(scooter: Scooter) {
            this.scooter = scooter
            nameTextView.text = this.scooter.name
            whereTextView.text = this.scooter.where
            timestampTextView.text = this.scooter.readableTimestamp()
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

        fun remove(position : Int) {
            if(position < 0 || position < itemCount)
                return

            ridesDB.deleteScooter(position)
            notifyItemRemoved(position)
        }
    }

}