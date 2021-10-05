package com.sssoyalan.newsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sssoyalan.newsapp.R
import com.sssoyalan.newsapp.helpers.Constants
import com.sssoyalan.newsapp.helpers.LastSeenTime
import com.sssoyalan.newsapp.models.message.MessageModel
import kotlinx.android.synthetic.main.item_chat.view.*

class MessageAdapter(private val mList: List<MessageModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mList[position]

        holder.itemView.apply {
            if (item.senderId==Constants.USER_ID){
                sender(holder.itemView)
                sender_messsage_text.text=item.message
                sender_zaman.text = LastSeenTime.getTimeAgoMessage(item.messageTime.toLong(), context)
            }else{
                receiver(holder.itemView)
                receiver_message_text.text=item.message
                receiver_name.text=item.senderName
                receiver_zaman.text = LastSeenTime.getTimeAgoMessage(item.messageTime.toLong(), context)
            }
        }
    }

    private fun sender(itemView: View) {
        itemView.layout_sender.visibility=View.VISIBLE
        itemView.layout_receiver.visibility=View.GONE
    }

    private fun receiver(itemView: View) {
        itemView.layout_sender.visibility=View.GONE
        itemView.layout_receiver.visibility=View.VISIBLE
    }


    override fun getItemCount(): Int {
        return mList.size
    }

}