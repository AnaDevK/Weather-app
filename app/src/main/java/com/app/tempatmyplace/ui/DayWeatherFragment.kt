package com.app.tempatmyplace.ui

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tempatmyplace.R
import com.app.tempatmyplace.databinding.FragmentDayWeatherBinding
import com.app.tempatmyplace.model.CityWeather
import com.app.tempatmyplace.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.*
import java.util.*
import kotlin.math.roundToInt

class DayWeatherFragment : Fragment() {
    companion object {
        private const val PERMISSION_ID = 22
        private const val TAG = "DayWeatherFragment"
    }

    private lateinit var binding: FragmentDayWeatherBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var model: SharedViewModel
    private lateinit var units: String
    private lateinit var navController: NavController

    var lat: Double = 0.0
    var long: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDayWeatherBinding.inflate(inflater, container, false)
        val view = binding.root
        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowTitleEnabled(true)
        (activity as AppCompatActivity?)?.supportActionBar?.setLogo(R.drawable.ic_location)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayUseLogoEnabled(true)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowHomeEnabled(true)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        navController = Navigation.findNavController(requireActivity(), R.id.navFragment)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        //get location
        requestPermission()
        model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        units = getSettings(requireActivity()) ?: getString(R.string.metric)
    }

    private fun loadWeatherData(weather: CityWeather?) {
        if (weather == null) {
            binding.mainContainer.visibility = View.GONE
            Toast.makeText(context, getString(R.string.error_getting_data), Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Something went wrong, weather data is null")
        } else {
            //fill the data
            //keep sun icon when clear sky
            if (weather.current.weather[0].icon != "01d") {
                val img = "openweathermap.org/img/wn/${weather.current.weather[0].icon}.png"
                val imgUri = img.toUri().buildUpon().scheme("http").build()
                Glide.with(binding.imageStatus.context)
                    .load(imgUri)
                    .apply(
                        RequestOptions().placeholder(R.drawable.ic_sunrise)
                            .error(R.drawable.ic_smile)
                    )
                    .into(binding.imageStatus)
            }

            binding.updatedAt.text = getString(R.string.updated_at) + dateFormatDate(weather.current.dt)
            binding.temp.text = weather.current.temp.roundToInt().toString()+"°"
            binding.status.text = weather.current.weather[0].description
            binding.feelsLike.text = getString(R.string.feels_like) + weather.current.feels_like.roundToInt().toString()+"°"
            binding.sunrise.text = dateFormatHour(weather.current.sunrise)
            binding.sunset.text = dateFormatHour(weather.current.sunset)
            binding.uvi.text = uviIndexValue(weather.current.uvi.roundToInt())
            val windSpeedUnit = if(units == getString(R.string.metric)) "m/s" else "mi/h"
            binding.wind.text = weather.current.wind_speed.roundToInt().toString() + windSpeedUnit
            binding.pressure.text = weather.current.pressure.toString() + " mbar"
            binding.humidity.text = weather.current.humidity.toString() + "%"

            binding.rvHours.adapter = HourAdapter(requireContext(), weather)
            binding.rvHours.setHasFixedSize(true)
            binding.rvHours.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocationAndData()
            }
        }
    }

    private fun getCityName(lat: Double, long: Double): String {
        var cityName = ""
        //var countryName: String
        var geoCoder = Geocoder(activity, Locale.getDefault())
        var address = geoCoder.getFromLocation(lat, long, 3)
        cityName = address.get(0).locality
        //countryName = address.get(0).countryName
        return cityName
    }

    private fun getLastLocationAndData() {
        if (isLocationEnabled()) {
            if (checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    context,
                    getString(R.string.permission_location),
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                var location: Location? = task.result
                if (location == null) {
                    newLocationData()
                } else {
                    val cityName = getCityName(location.latitude, location.longitude)
                    if(cityName == "") {
                        Toast.makeText(context, getString(R.string.location_error), Toast.LENGTH_SHORT).show()
                    } else {
                        (activity as AppCompatActivity?)?.supportActionBar?.setTitle(cityName)
                        lat = location.latitude
                        long = location.longitude
                        units = getSettings(requireActivity()) ?: getString(R.string.metric)
                        if (!isNetworkAvailable(requireContext())) {
                            Toast.makeText(context, getString(R.string.no_internet), Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            model.getWeather(lat, long, units, Locale.getDefault().language)!!
                                .observe(viewLifecycleOwner,
                                    { serviceWeather ->
                                        loadWeatherData(serviceWeather)
                                        serviceWeather?.let { model.setWeeklyWeather(it.daily) }
                                    })
                        }
                    }
                }
            }
        } else {
            Toast.makeText(context, getString(R.string.ask_location_enabled), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun newLocationData() {
        var locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        if (checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                context,
                getString(R.string.ask_permission_location),
                Toast.LENGTH_LONG
            )
                .show()
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            (activity as AppCompatActivity?)?.supportActionBar?.setTitle(getCityName(lastLocation.latitude, lastLocation.longitude))
            lat = lastLocation.latitude
            long = lastLocation.longitude
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miRefresh -> {
                getLastLocationAndData()
            }
            R.id.miSettings -> {
                val dialogBuilder = AlertDialog.Builder(requireContext())
                val view = layoutInflater.inflate(R.layout.units, null)
                val rg = view.findViewById<RadioGroup>(R.id.rgTemp)
                when (getSettings(requireActivity()) ?: getString(R.string.metric)) {
                    getString(R.string.metric) -> rg.check(R.id.rbC)
                    getString(R.string.imperial) -> rg.check(R.id.rbF)
                }
                dialogBuilder.setView(view)
                dialogBuilder.setTitle(getString(R.string.temperature_unit))
                dialogBuilder.setPositiveButton(
                    getString(R.string.ok),
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, id: Int) {
                             units = when (rg.checkedRadioButtonId) {
                                R.id.rbC -> getString(R.string.metric)
                                else -> getString(R.string.imperial)
                            }

                            model.getWeather(lat, long, units, Locale.getDefault().language)!!
                                .observe(viewLifecycleOwner,
                                    { serviceWeather ->
                                        loadWeatherData(serviceWeather)
                                        serviceWeather?.let { model.setWeeklyWeather(it.daily) }
                                    })
                            saveSettings(requireActivity(), units)
                        }
                    })
                dialogBuilder.setNegativeButton(
                    getString(R.string.cancel),
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                        }
                    })

                val alertDialog = dialogBuilder.create()
                alertDialog.show()
                return true
            }
            R.id.miWeek -> {
                navController.navigate(R.id.action_dayWeatherFragment_to_weeklyWeatherFragment)
                return true
            }
        }
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }
}