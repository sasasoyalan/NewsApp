package com.sssoyalan.newsapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sssoyalan.newsapp.MainViewModel
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.adapters.BorsaAdapter
import com.sssoyalan.newsapp.models.Borsalar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_layout.*


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    lateinit var navView: BottomNavigationView

    companion object {
        var SPEED = 20
        var REVERSE = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar?
        if (toolbar != null) {
            toolbar.title = ""
        }
        setSupportActionBar(toolbar)

        val mPrefs = this.getSharedPreferences("pref", Context.MODE_PRIVATE)
        SPEED =  mPrefs.getInt("speed",20)
        REVERSE = mPrefs.getBoolean("reverse",false)

        navView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
        navView.itemIconTintList = null
        navView.itemTextColor = null

        goRefresh()
    }


    fun goRefresh() {
        viewModel = ViewModelProviders.of(this)[MainViewModel::class.java]
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
                    super.onScrolled(recyclerView, dx, dy) }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == 1) {
                        val borsalar = Borsalar(it)
                        val intent = Intent(this@MainActivity, DetailActivity::class.java)
                        intent.putExtra("object", borsalar)
                        startActivity(intent)
                    }
                }
            })
        }
        viewModel.fetchBorsa()
    }

    private fun startScroll() {
        recyc_main.openAutoScroll(SPEED, REVERSE)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.custom_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_settings -> {
                showDialog()
            }
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
        val cancelBtn = bottomSheetView.findViewById(R.id.cancel_btn) as ImageButton
        val saveBtn = bottomSheetView.findViewById(R.id.save_btn) as ImageButton

        seekBar.progress = SPEED/10
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
                REVERSE=true
                radioBtnRight.setTextColor(resources.getColor(R.color.black2))
                radioBtnLeft.setTextColor(resources.getColor(R.color.white))
            }
        }

        cancelBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        saveBtn.setOnClickListener {
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

     class sharedmodel (val speed: Int = SPEED, val reverse: Boolean = REVERSE)

}





