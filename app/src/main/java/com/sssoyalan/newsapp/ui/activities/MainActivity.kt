package com.sssoyalan.newsapp.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.sssoyalan.newsapp.MainViewModel
import com.sssoyalan.newsapp.MainViewModelFactory
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.adapters.BorsaAdapter
import com.sssoyalan.newsapp.db.ArticleDatabase
import com.sssoyalan.newsapp.generic.Constants
import com.sssoyalan.newsapp.generic.Constants.*
import com.sssoyalan.newsapp.models.Borsalar
import com.sssoyalan.newsapp.models.users.UserModel
import com.sssoyalan.newsapp.source.DataRepository
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_layout.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var viewModel: MainViewModel
    lateinit var navView: BottomNavigationView
    lateinit var appBar: AppBarLayout
    lateinit var actionBarDrawerToggle : ActionBarDrawerToggle
    private lateinit var drawerLayout : DrawerLayout

    companion object {
        var SPEED = 20
        var REVERSE = false
        var isActive = true
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

        drawerLayout = findViewById(R.id.my_drawer_layout)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (googleSignInAccount!=null){
            USER_NAME = googleSignInAccount.displayName
            USER_EMAIL = googleSignInAccount.email
            USER_IMG_URL = googleSignInAccount.photoUrl
            USER_ID = googleSignInAccount.id
            val firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
            firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
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

        val dataRepository = DataRepository(ArticleDatabase(this))
        val mainViewModelFactory =
            MainViewModelFactory(
                dataRepository
            )

        viewModel = ViewModelProvider(this, mainViewModelFactory).get(MainViewModel::class.java)
        val mPrefs = this.getSharedPreferences("pref", Context.MODE_PRIVATE)
        SPEED =  mPrefs.getInt("speed", 20)
        REVERSE = mPrefs.getBoolean("reverse", false)

        navView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
        navView.itemIconTintList = null
        navView.itemTextColor = null

        goRefresh()
    }

    override fun onResume() {
        super.onResume()
        isActive =true
    }

    override fun onRestart() {
        super.onRestart()
        isActive =true
    }

    override fun onStart() {
        super.onStart()
        isActive=true
    }


    fun goRefresh() {
        viewModel.aliveDataBorsa.observe(this) {
            recyc_main.adapter = BorsaAdapter(it)
            recyc_main.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            recyc_main.isLoopEnabled = true
            startScroll()
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
        val cancelBtn = bottomSheetView.findViewById(R.id.cancel_btn) as AppCompatImageButton
        val saveBtn = bottomSheetView.findViewById(R.id.save_btn) as AppCompatImageButton

        seekBar.progress = SPEED /10
        tv_speed.text = seekBar.progress.toString()+"x"

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
            isActive=true
            SPEED = seekBar.progress*10
            startScroll()
            val prefences = getSharedPreferences("pref", Context.MODE_PRIVATE)
            val editor = prefences.edit()
            editor.putInt("speed", SPEED)
            editor.putBoolean("reverse", REVERSE)
            editor.apply()
            bottomSheetDialog.dismiss()
            val intent = intent
            finish()
            startActivity(intent)
        }
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_account -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                val view : View = navView.findViewById(R.id.navigation_profile)
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
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}





