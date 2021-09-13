package com.sssoyalan.newsapp.adapters

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.models.Article
import com.sssoyalan.newsapp.ui.DetailActivity
import kotlinx.android.synthetic.main.item_recyclerview.view.*
import java.io.Serializable

class NewsAdapter(private val mList: List<Article> , private val context: Context) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        holder.itemView.apply {
            if (itemsViewModel.source.name!= null){
                txt_newsId.text = itemsViewModel.source.name
            }else {
                txt_newsId.visibility = View.GONE
            }
            Glide.with(context).load(itemsViewModel.urlToImage).into(newImage)
            txt_newTıtle.text=itemsViewModel.title
            txt_news_desc.text = itemsViewModel.description
            user_link
            setOnClickListener{
                val intent = Intent(context,DetailActivity::class.java)
                intent.putExtra("object", itemsViewModel)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView)
}
