package com.sssoyalan.newsapp.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.sssoyalan.newsapp.MainViewModel
import com.sssoyalan.newsapp.MainViewModelFactory
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.adapters.BorsaAdapter
import com.sssoyalan.newsapp.db.ArticleDatabase
import com.sssoyalan.newsapp.helpers.Constants
import com.sssoyalan.newsapp.helpers.Constants.*
import com.sssoyalan.newsapp.models.Resource
import com.sssoyalan.newsapp.models.borsa.Borsalar
import com.sssoyalan.newsapp.models.users.UserModel
import com.sssoyalan.newsapp.source.DataRepository
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_layout.*
import kotlinx.android.synthetic.main.toolbar_actionbar.*
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var viewModel: MainViewModel
    lateinit var navView: BottomNavigationView
    lateinit var appBar: AppBarLayout
    lateinit var actionBarDrawerToggle : ActionBarDrawerToggle
    private lateinit var drawerLayout : DrawerLayout
    private val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        var SPEED = 20
        var REVERSE = false
        var isActive = true
        var isEmail = "1"
        var isOnline = "1"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar_inc) as Toolbar?
        toolbar?.let {
            it.title=""
        }
        setSupportActionBar(toolbar)
        appBar = findViewById(R.id.app_bar)
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        drawerLayout = findViewById(R.id.my_drawer_layout)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val sharedPreferences = this.getSharedPreferences(
            packageName,
            android.content.Context.MODE_PRIVATE
        )
        val cityName = sharedPreferences.getString("city", null)
        val dataRepository = DataRepository(ArticleDatabase(this))
        val mainViewModelFactory =
            MainViewModelFactory(
                dataRepository
            )
        viewModel = ViewModelProvider(this, mainViewModelFactory).get(MainViewModel::class.java)

        if (cityName==null){
            getLastLocation()
        }else{
            goResfreshWeather(null, null, cityName)
        }

        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (googleSignInAccount!=null){
            USER_NAME = googleSignInAccount.displayName
            USER_EMAIL = googleSignInAccount.email
            USER_IMG_URL = googleSignInAccount.photoUrl
            USER_ID = googleSignInAccount.id
            firestore.collection("users").document(Constants.USER_ID)
                .get()
                .addOnSuccessListener { result ->
                    Log.d("TAGsss", "${result.id} => ${result.data}")
                    val userModel : UserModel? = result.toObject(UserModel::class.java)
                    userModel?.okunan?.okunanTotal?.let {
                        USER_TOTAL_OKUNAN = it
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("TAGsss", "Error getting documents.", exception)
                }

        }else {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        val headerView: View = nav.getHeaderView(0)
        val username = headerView.findViewById<TextView>(R.id.username)
        val email = headerView.findViewById<TextView>(R.id.userEmail)
        val photoUri = headerView.findViewById<ImageView>(R.id.profile_image)
        username.text= USER_NAME
        email.text= USER_EMAIL
        Glide.with(this).load(USER_IMG_URL).into(photoUri)
        nav.setNavigationItemSelectedListener(this)

        val mPrefs = this.getSharedPreferences("pref", Context.MODE_PRIVATE)
        SPEED =  mPrefs.getInt("speed", 20)
        REVERSE = mPrefs.getBoolean("reverse", false)

        navView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
        navView.itemIconTintList = null
        navView.itemTextColor = null
        circle_imageView_logo.setOnClickListener {
            val view: View = navView.findViewById(R.id.navigation_home)
            navView.animate().translationY(0f).setInterpolator(
                DecelerateInterpolator(
                    2f
                )
            ).start()
            view.performClick()
        }
        main_weather.setOnClickListener {
            val view: View = navView.findViewById(R.id.navigation_weather)
            navView.animate().translationY(0f).setInterpolator(
                DecelerateInterpolator(
                    2f
                )
            ).start()
            view.performClick()
        }
        goRefresh(false)
        lastSeenUpdate("1")
    }

    override fun onResume() {
        super.onResume()
        isActive =true
        lastSeenUpdate("1")
    }

    override fun onRestart() {
        super.onRestart()
        isActive =true
        lastSeenUpdate("1")
    }

    override fun onStart() {
        super.onStart()
        isActive=true
        lastSeenUpdate("1")
    }

    override fun onPause() {
        super.onPause()
        val time : Long = Timestamp.now().toDate().time
        lastSeenUpdate(time.toString())
    }

    fun goRefresh(isRefresh: Boolean) {
        val layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        var positionIndex : Int =0
        var topView : Int = 0
        if(isRefresh){
            positionIndex = (recyc_main.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
            val startView: View = recyc_main.getChildAt(0)
            topView = if (startView == null) 0 else startView.top - recyc_main.paddingTop
        }
        viewModel.aliveDataBorsa.observe(this) {
            recyc_main.adapter = BorsaAdapter(it)
            recyc_main.layoutManager = layoutManager
            recyc_main.isLoopEnabled = true
            startScroll()
            if (isRefresh){
                (recyc_main.layoutManager as LinearLayoutManager).scrollToPosition(positionIndex)
            }
            recyc_main.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == 1) {
                        if (isActive) {
                            val borsalar = Borsalar(it)
                            val intent = Intent(this@MainActivity, DetailActivity::class.java)
                            intent.putExtra("object", borsalar)
                            startActivity(intent)
                            isActive = false
                        }
                    }
                }
            })
        }
        viewModel.fetchBorsa()
    }

    private fun startScroll() {
        recyc_main.openAutoScroll(SPEED, REVERSE)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    private fun showDialog() {

        val bottomSheetDialog =
            BottomSheetDialog(this@MainActivity, R.style.BottomSheetDialogTheme)
        val bottomSheetView: View = LayoutInflater.from(applicationContext).inflate(
            R.layout.dialog_layout, findViewById<View>(
                R.id.bottomSheetContainer
            ) as? LinearLayout
        )
        val tv_speed = bottomSheetView.findViewById(R.id.txt_speed) as TextView
        val tv_yon = bottomSheetView.findViewById(R.id.txt_yon) as TextView
        val seekBar = bottomSheetView.findViewById(R.id.seekBar) as SeekBar
        val radioG = bottomSheetView.findViewById(R.id.radio_group) as RadioGroup
        val radioBtnLeft = bottomSheetView.findViewById(R.id.radioSol) as RadioButton
        val radioBtnRight = bottomSheetView.findViewById(R.id.radioSag) as RadioButton
        val cb_email = bottomSheetView.findViewById(R.id.cb_email) as CheckBox
        val cb_online = bottomSheetView.findViewById(R.id.cb_son_gorulme) as CheckBox
        val cancelBtn = bottomSheetView.findViewById(R.id.cancel_btn) as AppCompatImageButton
        val saveBtn = bottomSheetView.findViewById(R.id.save_btn) as AppCompatImageButton
        seekBar.progress = SPEED /10
        tv_speed.text = seekBar.progress.toString()+"x"
        val firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
        val docIdRef: DocumentReference = firestore.collection("users").document(USER_ID)
        docIdRef.get().addOnSuccessListener {
            if (it.exists()){
                cb_email.isChecked = it.getString("userEmailCheck")=="1"
                cb_online.isChecked = it.getString("onlineCheck")=="1"
            }
        }
        if (!REVERSE){
            tv_yon.text = "Sol"
            radioBtnLeft.setTextColor(resources.getColor(R.color.black2))
            radioBtnRight.setTextColor(resources.getColor(R.color.white))
            radioG.check(R.id.radioSol)
        } else{
            tv_yon.text = "Sağ"
            radioBtnRight.setTextColor(resources.getColor(R.color.black2))
            radioBtnLeft.setTextColor(resources.getColor(R.color.white))
            radioG.check(R.id.radioSag)
        }
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                tv_speed.text = i.toString() + "x"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        radioG.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId==R.id.radioSol){
                tv_yon.text = "Sol"
                REVERSE = false
                radioBtnLeft.setTextColor(resources.getColor(R.color.black2))
                radioBtnRight.setTextColor(resources.getColor(R.color.white))
            }else {
                tv_yon.text = "Sağ"
                REVERSE =true
                radioBtnRight.setTextColor(resources.getColor(R.color.black2))
                radioBtnLeft.setTextColor(resources.getColor(R.color.white))
            }
        }
        cancelBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        saveBtn.setOnClickListener {
            isEmail = if (cb_email.isChecked){
                "1"
            }else{
                "0"
            }
            isOnline = if (cb_online.isChecked){
                "1"
            }else{
                "0"
            }

            firestore.collection("users").document(USER_ID)
                .update(
                    mapOf(
                        "userEmailCheck" to isEmail,
                        "onlineCheck" to isOnline
                    )
                )
                .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }

            isActive=true
            SPEED = seekBar.progress*10
            startScroll()
            val prefences = getSharedPreferences("pref", Context.MODE_PRIVATE)
            val editor = prefences.edit()
            editor.putInt("speed", SPEED)
            editor.putBoolean("reverse", REVERSE)
            editor.apply()
            bottomSheetDialog.dismiss()
            recreate()
        }
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun lastSeenUpdate(s: String){
        firestore.collection("users").document(USER_ID)
                .update("online", s)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_account -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                val view: View = navView.findViewById(R.id.navigation_profile)
                navView.animate().translationY(0f).setInterpolator(
                    DecelerateInterpolator(
                        2f
                    )
                ).start()
                view.performClick()
            }
            R.id.nav_settings -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                showDialog()
            }
            R.id.nav_logout -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                FirebaseAuth.getInstance().signOut()
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                val googleSignInClient = GoogleSignIn.getClient(this, gso)
                googleSignInClient.signOut()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        goResfreshWeather(
                            location.latitude.toString(),
                            location.longitude.toString(),
                            null
                        )
                        Log.d(
                            "TAGsss",
                            "${location.latitude.toString()} => ${location.longitude.toString()}"
                        )
                    }
                }
            } else {

                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("SetTextI18n")
    fun goResfreshWeather(lat: String?, lon: String?, city: String?) {
        viewModel.getWeatherData(lat, lon, city)
        viewModel.aliveDataWeather.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.data != null) {
                        when {
                            it.data.weather[0].main.toString().contains("Clear") -> {
                                circle_imageView_effect.setBackgroundResource(R.drawable.sun_effect)
                            }
                            it.data.weather[0].main.toString().contains("Smoke") -> {
                                circle_imageView_effect.setBackgroundResource(R.drawable.smoke_effect)
                            }
                            it.data.weather[0].main.toString().contains("Cloud") -> {
                                circle_imageView_effect.setBackgroundResource(R.drawable.cloud_effect)
                            }
                            it.data.weather[0].main.toString().contains("Haze") -> {
                                circle_imageView_effect.setBackgroundResource(R.drawable.smoke_effect)
                            }
                        }

                        if (!it.data.weather[0].main.toString().contains("Clear")) {
                            Glide.with(this)
                                .load("https://openweathermap.org/img/wn/" + it.data.weather[0].icon.toString() + "@2x.png")
                                .into(
                                    circle_imageView_main
                                )
                        }
                        main_degree.text = String.format("%.1f ℃", it.data.main.temp - 272.15)
                    }
                }
                is Resource.Error -> {
                    it.message?.let {
                        Toast.makeText(
                            this,
                            "Bedava sürüm olduğundan dolayı haber limiti doldu , yarın düzelir :D",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            goResfreshWeather(
                mLastLocation.latitude.toString(),
                mLastLocation.longitude.toString(),
                null
            )
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
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

    fun isWeather() {
        navView.setBackgroundColor(Color.TRANSPARENT)
        main_borsa.visibility=View.GONE
    }

    fun isNotWeather() {
        navView.setBackgroundColor(Color.parseColor("#2b2b2b"))
        main_borsa.visibility=View.VISIBLE
    }


}





