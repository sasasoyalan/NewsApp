package com.sssoyalan.newsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.models.modelInside
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
