package com.sssoyalan.newsapp.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.models.users.UserModel
import kotlinx.android.synthetic.main.item_user.view.*

class UsersAdapter(private val mList: List<UserModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mList[position]
        holder.itemView.apply {
            user_name.text=item.userName
            user_email.text=item.userEmail
            Glide.with(context).load(Uri.parse(item.userPhotoUrl)).into(user_img)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

}
