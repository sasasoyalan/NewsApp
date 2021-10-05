package com.sssoyalan.newsapp.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Html
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.matteobattilana.weather.PrecipType
import com.google.android.gms.location.*
import com.sssoyalan.newsapp.MainViewModel
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.adapters.CityAdapter
import com.sssoyalan.newsapp.databinding.FragmentWeatherBinding
import com.sssoyalan.newsapp.models.Resource
import com.sssoyalan.newsapp.ui.activities.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_weather.*
import kotlinx.android.synthetic.main.item_recyclerview.view.*
import java.text.SimpleDateFormat
import java.util.*


class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog: Dialog
    private lateinit var viewModel: MainViewModel
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var adapterCity: CityAdapter

    companion object {
        const val REQUEST_WRITE_PERMISSION = 100
    }

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
        (activity as MainActivity).isWeather()

        initCityDialog()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val sharedPreferences = requireContext().getSharedPreferences(
            requireContext().packageName,
            android.content.Context.MODE_PRIVATE
        )
        val cityName = sharedPreferences.getString("city", null)

        if (cityName==null){
            getLastLocation()
        }else{
            goResfresh(null, null, cityName)
        }

        binding.selectLocation.setOnClickListener {
            getLastLocation()
        }

        binding.selectCity.setOnClickListener {
            showCitydialog()
        }
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
                            showProgressBar()
                            goResfresh(
                                location.latitude.toString(),
                                location.longitude.toString(),
                                null
                            )
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Konum izni verilmemiş.İsterseniz şehir seçerek devam edebilirsiniz.",
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
    fun goResfresh(lat: String?, lon: String?, city: String?) {
        viewModel.getWeatherData(lat, lon, city)
        viewModel.aliveDataWeather.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.data != null) {
                        val c: Calendar = Calendar.getInstance()
                        val timeOfDay: Int = c.get(Calendar.HOUR_OF_DAY)

                        if (timeOfDay in 5..11) {
                            binding.gifBackground.setBackgroundResource(R.drawable.morning)
                        } else if (timeOfDay in 12..16) {
                            binding.gifBackground.setBackgroundResource(R.drawable.noon)
                        } else if (timeOfDay in 17..20) {
                            binding.gifBackground.setBackgroundResource(R.drawable.evening)
                        } else if (timeOfDay in 21..23 || timeOfDay in 0..4) {
                            binding.gifBackground.setBackgroundResource(R.drawable.night)
                        }
                        when {
                            it.data.weather[0].main.toString().contains("Clear") -> {
                                binding.weatherView.visibility = View.GONE
                            }
                            it.data.weather[0].main.toString()
                                .contains("Snow") -> binding.weatherView.setWeatherData(
                                PrecipType.SNOW
                            )
                            it.data.weather[0].main.toString().contains("Rain") -> {
                                binding.weatherView.visibility = View.GONE
                                binding.gifBackground.setBackgroundResource(R.drawable.rain)
                            }
                            it.data.weather[0].main.toString().contains("Drizzle") -> {
                                binding.weatherView.visibility = View.GONE
                                binding.gifBackground.setBackgroundResource(R.drawable.rain)
                            }
                            it.data.weather[0].main.toString().contains("Cloud") -> {
                                binding.weatherView.visibility = View.GONE
                                binding.gifBackground.setBackgroundResource(R.drawable.cloud)
                            }
                            it.data.weather[0].main.toString().contains("Smoke") -> {
                                binding.weatherView.visibility = View.GONE
                            }
                            it.data.weather[0].main.toString().contains("Haze") -> {
                                binding.weatherView.visibility = View.GONE
                            }
                            else -> {
                                binding.weatherView.visibility = View.INVISIBLE
                            }
                        }
                        val currentDate =
                            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                        val currentTime =
                            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                        val strArray: List<String> =
                            it.data.weather[0].description.toString().split(
                                " "
                            )
                        val builder = StringBuilder()
                        for (s in strArray) {
                            val cap = s.substring(0, 1).toUpperCase() + s.substring(1)
                            builder.append("$cap ")
                        }
                        if (it.data.weather[0].icon != "01n") {
                            if(it.data.weather[0].icon == "01d"){
                                binding.imgWeather.setBackgroundColor(Color.TRANSPARENT)
                                binding.imgEffectSun.visibility = View.VISIBLE
                            }else {
                                binding.imgEffectSun.visibility = View.GONE
                                Glide.with(requireContext())
                                        .load("https://openweathermap.org/img/wn/" + it.data.weather[0].icon.toString() + "@2x.png")
                                        .into(
                                                binding.imgWeather
                                        )
                            }
                        }
                        binding.txtSehir.text = it.data.name
                        binding.txtLastUpdate.text = "Son güncelleme : $currentTime $currentDate"
                        binding.hava.text = builder.toString()
                        binding.humidity.text = String.format("%d%%", it.data.main.humidity)
                        binding.degree.text = String.format("%.1f ℃", it.data.main.temp - 272.15)
                        binding.feeldegree.text = String.format(
                            "%.1f ℃",
                            it.data.main.feels_like - 272.15
                        )
                    }
                    hideProgressBar()
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
            goResfresh(mLastLocation.latitude.toString(), mLastLocation.longitude.toString(), null)
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            if (requestCode == REQUEST_WRITE_PERMISSION) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation()
                } else {
                    checkUserRequestedDontAskAgain()
                }
            }
        }
    }

    private fun checkUserRequestedDontAskAgain() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val rationalFalgREAD =
                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val rationalFalgWRITE =
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (!rationalFalgREAD && !rationalFalgWRITE) {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri = Uri.fromParts("package", activity?.packageName, null)
                intent.data = uri
                requireContext().startActivity(intent)
            }
        }
    }

    fun hideProgressBar() {
        binding.paginationProgressBarMain.visibility = View.INVISIBLE
    }
    fun showProgressBar() {
        binding.paginationProgressBarMain.visibility = View.VISIBLE
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initCityDialog(){
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_city)
        val width : Int = (resources.displayMetrics.widthPixels*0.90).toInt()
        val height : Int = (resources.displayMetrics.heightPixels*0.70).toInt()
        dialog.window?.setLayout(width, height)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setWindowAnimations(R.style.DialogSlideAnim)
        dialog.setCancelable(true)
        val city_seach = dialog.findViewById(R.id.city_search) as SearchView
        val recyc_city = dialog.findViewById(R.id.recyc_city) as RecyclerView

        val searchIcon = city_seach.findViewById<ImageView>(R.id.search_mag_icon)
        searchIcon.setColorFilter(Color.WHITE)
        city_seach.queryHint = "Şehir arayın.."
        city_seach.queryHint = Html.fromHtml("<font color = #9F9E9D>" + "Şehir arayın.." + "</font>")

        val cancelIcon = city_seach.findViewById<ImageView>(R.id.search_close_btn)
        cancelIcon.setColorFilter(Color.WHITE)

        val textView = city_seach.findViewById<TextView>(R.id.search_src_text)
        textView.setTextColor(Color.WHITE)

        val  linearLayoutManager : LinearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.stackFromEnd=true
        linearLayoutManager.scrollToPositionWithOffset(2, 20)
        recyc_city.apply {
            layoutManager = linearLayoutManager
        }
        recyc_city.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    hideKeyboard(city_seach)
                }
            }
            v?.onTouchEvent(event) ?: true
        }
        city_seach.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterCity.getFilter().filter(newText)
                return false
            }
        })

        getListofCities(recyc_city)
    }

    private fun getListofCities(recyc_city: RecyclerView) {
        viewModel.fetchCity(requireContext())
        viewModel.alLiveDataCity.observe(viewLifecycleOwner, Observer {
            adapterCity = CityAdapter(it.cities, this, activity as MainActivity)
            adapterCity.notifyDataSetChanged()
            recyc_city.adapter = adapterCity
        })
    }

    fun hideKeyboard(view: View){
        val imm : InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showCitydialog() {
        dialog.show()
    }

    fun hideCitydialog() {
        dialog.dismiss()
    }
}