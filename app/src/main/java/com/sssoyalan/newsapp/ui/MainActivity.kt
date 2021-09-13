package com.sssoyalan.newsapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sssoyalan.newsapp.MainViewModel
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.adapters.BorsaAdapter
import com.sssoyalan.newsapp.models.Borsalar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
            recyc_main.startAutoScroll()
            recyc_main.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        // Scrolling up
                    } else {
                        // Scrolling down
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == 1) {
                        val borsalar = Borsalar(it)
                        val intent = Intent(this@MainActivity,DetailActivity::class.java)
                        intent.putExtra("object", borsalar)
                        startActivity(intent)
                    }
                }
            })
        }
        viewModel.fetchBorsa()
    }

}





