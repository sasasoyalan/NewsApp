package com.sssoyalan.newsapp.adapters

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.generic.LastSeenTime
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

        val param = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        if (position==mList.size-1){
            param.setMargins(30,30,30,250)
            holder.itemView.layoutParams = param
        }else{
            param.setMargins(30,30,30,0)
            holder.itemView.layoutParams = param
        }
        holder.itemView.apply {
            user_name.text=item.userName
            if (item.userEmailCheck.toString() == "1"){
                user_email.text=item.userEmail
            }
            if (item.online=="1"){
                txt_son_gorulme.text = "Online"
                txt_son_gorulme.setTextColor(ContextCompat.getColor(context,R.color.green_400))
            } else{
                if (item.onlineCheck=="1"){
                    txt_son_gorulme.text = LastSeenTime.getTimeAgo(item.online.toLong(), context)
                }
                txt_son_gorulme.setTextColor(Color.parseColor("#ffffff"))
            }
            Glide.with(context).load(Uri.parse(item.userPhotoUrl)).into(user_img)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

}
