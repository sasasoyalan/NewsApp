package com.sssoyalan.newsapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.models.Article
import com.sssoyalan.newsapp.ui.activities.DetailActivity
import com.sssoyalan.newsapp.ui.fragments.NewsFragment
import kotlinx.android.synthetic.main.item_recyclerview.view.*


class NewsAdapter(val mList: List<Article>, private val homeFragment: NewsFragment? = null) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        if (position==mList.size-1||position==mList.size-2){
            val param = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0,0,0,250)
            holder.itemView.layoutParams = param
        }

        holder.itemView.apply {
            if (itemsViewModel.source?.name!= null){
                txt_newsId.text = itemsViewModel.source.name
            }else {
                txt_newsId.visibility = View.GONE
            }
            if (homeFragment==null){
                txt_fav.visibility=View.GONE
            }

            txt_fav.setOnClickListener {
                itemsViewModel.fav=true
                homeFragment?.savefav(it,true,itemsViewModel)
            }

            Glide.with(context).load(itemsViewModel.urlToImage).into(newImage)
            txt_newTıtle.text=itemsViewModel.title
            txt_news_desc.text = itemsViewModel.description
            user_link
            setOnClickListener{
                val intent = Intent(context, DetailActivity::class.java)
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
