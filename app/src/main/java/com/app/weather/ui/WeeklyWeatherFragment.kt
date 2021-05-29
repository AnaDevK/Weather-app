package com.app.weather.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.weather.R
import com.app.weather.databinding.FragmentWeekWeatherBinding
import com.app.weather.utils.DayAdapter

class WeeklyWeatherFragment : Fragment() {
    private lateinit var model: SharedViewModel
    private lateinit var binding: FragmentWeekWeatherBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowTitleEnabled(true)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowHomeEnabled(false)
        (activity as AppCompatActivity?)?.supportActionBar?.setTitle(getString(R.string.next_seven_days))
        setHasOptionsMenu(true)
        binding = FragmentWeekWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        model.weeklyLiveData.observe(viewLifecycleOwner, {
            binding.rvDays.adapter = DayAdapter(requireContext(), it)
            binding.rvDays.setHasFixedSize(true)
            binding.rvDays.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.hasVisibleItems()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.miWeek).setVisible(false)
        menu.findItem(R.id.miSettings).setVisible(false)
        menu.findItem(R.id.miRefresh).setVisible(false)
    }
}
