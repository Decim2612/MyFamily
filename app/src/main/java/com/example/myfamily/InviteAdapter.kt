package com.example.myfamily

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfamily.databinding.ItemInviteBinding

class InviteAdapter(private val listContacts:List<ContactModel>) : RecyclerView.Adapter<InviteAdapter.ViewHolder>() {
    class ViewHolder(private val item: ItemInviteBinding):RecyclerView.ViewHolder(item.root) {
       val name=item.name


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val item =ItemInviteBinding.inflate(inflater,parent,false)
        return InviteAdapter.ViewHolder(item)
    }

    override fun getItemCount():Int {
        return listContacts.size
    }

    override fun onBindViewHolder(holder:InviteAdapter.ViewHolder,position: Int) {
        val item=listContacts[position]
        holder.name.text=item.name
    }


}