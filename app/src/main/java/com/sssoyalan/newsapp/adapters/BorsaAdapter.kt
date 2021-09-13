package com.sssoyalan.newsapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.models.Article
import com.sssoyalan.newsapp.models.Borsalar
import com.sssoyalan.newsapp.models.modelInside
import com.sssoyalan.newsapp.ui.DetailActivity
import com.sssoyalan.newsapp.ui.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.item_recyclerview.view.*
import kotlinx.android.synthetic.main.item_recyclerview_borsa.view.*

class BorsaAdapter(private val mList: List<modelInside>) : RecyclerView.Adapter<BorsaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recyclerview_borsa, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        holder.itemView.apply {
            item_name.text=itemsViewModel.isim
            item_value.text = itemsViewModel.satis
            item_degisim.text = itemsViewModel.degisim+"%"
            val degisim = itemsViewModel.degisim.replace(',', '.')

            if (degisim.toDouble() > 0){
                item_img_degisim.setBackgroundResource(R.drawable.increase)
            } else {
                item_img_degisim.setBackgroundResource(R.drawable.decrease)
            }

        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView)
}
