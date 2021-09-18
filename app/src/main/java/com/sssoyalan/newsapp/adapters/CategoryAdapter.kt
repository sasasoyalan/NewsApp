package com.sssoyalan.newsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.models.CategoryInside
import com.sssoyalan.newsapp.ui.fragments.NewsFragment
import kotlinx.android.synthetic.main.item_category.view.*

class CategoryAdapter(private val mList: List<CategoryInside>, val homeFragment: NewsFragment) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.apply{
            txt_categoryName.text=mList[position].name
            setOnClickListener {
                homeFragment.goResfresh(CategoryInside(mList[position].id,mList[position].name,mList[position].tag))
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
    }
}