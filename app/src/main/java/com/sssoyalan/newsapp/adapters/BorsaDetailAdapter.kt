package com.sssoyalan.newsapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.models.borsa.modelInside
import kotlinx.android.synthetic.main.item_recyc_detail.view.*

class BorsaDetailAdapter(private val mList: List<modelInside>) : RecyclerView.Adapter<BorsaDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recyc_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        holder.itemView.apply {
            birim.text=itemsViewModel.isim
            fiyat.text = itemsViewModel.satis
            degisim.text = itemsViewModel.degisim+"%"
            val degisim_value = itemsViewModel.degisim.replace(',', '.')

            if (degisim_value.toDouble() > 0){
                img_yon.setBackgroundResource(R.drawable.arrow_up)
                degisim.setBackgroundResource(R.drawable.green_backgorund)
                fiyat.setTextColor(Color.parseColor("#2acb8d"))
            } else {
                img_yon.setBackgroundResource(R.drawable.arrow_down)
                degisim.setBackgroundResource(R.drawable.red_backgorund)
                fiyat.setTextColor(Color.parseColor("#D13B3B"))
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView)
}