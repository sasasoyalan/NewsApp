package com.sssoyalan.newsapp.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.sssoyalan.newsapp.R
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.github.matteobattilana.weather.PrecipType
import com.google.android.gms.location.*
import com.sssoyalan.newsapp.MainViewModel
import com.sssoyalan.newsapp.databinding.FragmentWeatherBinding
import com.sssoyalan.newsapp.models.Resource
import com.sssoyalan.newsapp.ui.activities.MainActivity
import kotlinx.android.synthetic.main.item_recyclerview.view.*
import java.text.SimpleDateFormat
import java.util.*


class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = (activity as MainActivity).viewModel

        val slideUp: Animation = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.slide_up_appbar_
        )
        (activity as MainActivity).appBar.visibility = View.VISIBLE
        (activity as MainActivity).appBar.startAnimation(slideUp)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        getLastLocation()

        return view
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
//                        findViewById<TextView>(R.id.latTextView).text = location.latitude.toString()
//                        findViewById<TextView>(R.id.lonTextView).text = location.longitude.toString()
                        /*Buradan istek atmalıyım mal değilsem eğer.*/
                            showProgressBar()
                            goResfresh(location.latitude.toString(), location.longitude.toString())
                        Log.d(
                            "TAGsss",
                            "${location.latitude.toString()} => ${location.longitude.toString()}"
                        )
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Konum bilgisini açmanız gerekli",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("SetTextI18n")
    fun goResfresh(lat: String, lon: String) {
        viewModel.getWeatherData(lat, lon)
        viewModel.aliveDataWeather.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    if (it.data != null) {
                        when  {
                            it.data.weather[0].main.toString().contains("Clear") -> {
                                binding.weatherView.setWeatherData(PrecipType.CLEAR)
                                binding.imgEffectSun.setBackgroundResource(R.drawable.sun_effect)
                            }
                            it.data.weather[0].main.toString().contains("Snow") -> binding.weatherView.setWeatherData(PrecipType.SNOW)
                            it.data.weather[0].main.toString().contains("Rain") -> binding.weatherView.setWeatherData(PrecipType.RAIN)
                            it.data.weather[0].main.toString().contains("Cloud") -> binding.baclgroundEffect.setBackgroundResource(R.drawable.cloud_effect)
                            it.data.weather[0].main.toString().contains("Smoke") -> {
                                binding.baclgroundEffect.setBackgroundResource(R.drawable.smoke_effect)
                                binding.baclgroundEffect.alpha = 0.1f
                            }
                        }
                        val currentDate =
                            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                        val currentTime =
                            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                        val strArray: List<String> = it.data.weather[0].description.toString().split(" ")
                        val builder = StringBuilder()
                        for (s in strArray) {
                            val cap = s.substring(0, 1).toUpperCase() + s.substring(1)
                            builder.append("$cap ")
                        }
                        Glide.with(requireContext())
                            .load("https://openweathermap.org/img/wn/" + it.data.weather[0].icon.toString() + "@2x.png")
                            .into(
                                binding.img
                            )
                        binding.txtSehir.text = it.data.name
                        binding.txtLastUpdate.text = "Son güncelleme : $currentTime $currentDate"
                        binding.hava.text = builder.toString()
                        binding.humidity.text= String.format("%d%%",it.data.main.humidity)
                        binding.degree.text= String.format("%.2f ℃",it.data.main.temp-272.15)
                        binding.feeldegree.text= String.format("%.2f ℃",it.data.main.feels_like-272.15)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    it.message?.let {
                        Toast.makeText(
                            context,
                            "Bedava sürüm olduğundan dolayı haber limiti doldu , yarın düzelir :D",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is Resource.Loading ->
                    showProgressBar()
            }
        })
        NewsFragment.instance =true
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            goResfresh(mLastLocation.latitude.toString(), mLastLocation.longitude.toString())
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    fun hideProgressBar() {
        binding.paginationProgressBarMain.visibility = View.INVISIBLE
    }
    fun showProgressBar() {
        binding.paginationProgressBarMain.visibility = View.VISIBLE
    }
}