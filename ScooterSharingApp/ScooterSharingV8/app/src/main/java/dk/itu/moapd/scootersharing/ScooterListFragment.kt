package dk.itu.moapd.scootersharing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.data.model.Scooter
import dk.itu.moapd.scootersharing.databinding.FragmentScooterListBinding
import dk.itu.moapd.scootersharing.databinding.ListItemScooterBinding
import dk.itu.moapd.scootersharing.viewmodels.LocationViewModel
import dk.itu.moapd.scootersharing.viewmodels.ScooterViewModel

class ScooterListFragment : Fragment() {

    private lateinit var binding: FragmentScooterListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScooterAdapter
    private lateinit var scooterViewModel: ScooterViewModel
    private lateinit var locationViewModel: LocationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScooterListBinding.inflate(layoutInflater)
        scooterViewModel = ViewModelProvider(this)[ScooterViewModel::class.java]
        locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]
        recyclerView = binding.scooterRecyclerView
        scooterViewModel.getAll().observe(viewLifecycleOwner) {
            adapter = ScooterAdapter(it)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)
        }
        return binding.root
    }

    private inner class ScooterHolder(private val binding: ListItemScooterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        lateinit var scooter: Scooter

        fun bind(scooter: Scooter) {
            this.scooter = scooter
            binding.name.text = scooter.name
            binding.where.text = locationViewModel.toLocation(scooter.lat, scooter.lon) ?: "Address is unavailable"
        }
    }

    private inner class ScooterAdapter(private val scooters: List<Scooter>) :
        RecyclerView.Adapter<ScooterHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScooterHolder {
            val binding = ListItemScooterBinding.inflate(layoutInflater)
            return ScooterHolder(binding)
        }

        override fun getItemCount() = scooters.size

        override fun onBindViewHolder(holder: ScooterHolder, position: Int) {
            holder.bind(scooters[position])
        }
    }
}