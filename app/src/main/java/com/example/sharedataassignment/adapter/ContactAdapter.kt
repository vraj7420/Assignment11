package com.example.sharedataassignment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedataassignment.R
import com.example.sharedataassignment.model.ContactModel

class ContactAdapter(private var ctx:Context,private var contactList:ArrayList<ContactModel>): RecyclerView.Adapter<ContactAdapter.ContactHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val layoutInflater=LayoutInflater.from(ctx)
        return ContactHolder(layoutInflater.inflate(R.layout.item_contact,parent,false))
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        val contact=contactList[position]
        holder.tvContactName.text=contact.ContactName
        holder.tvContactNumber.text=contact.ContactNumber
        holder.imvContact.setImageURI(contact.imgUri.toUri())
    }

    override fun getItemCount(): Int {
        return contactList.size
    }
    class ContactHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvContactName: TextView = itemView.findViewById(R.id.tvContactName)
        var tvContactNumber: TextView = itemView.findViewById(R.id.tvContactNumber)
        var imvContact:ImageView=itemView.findViewById(R.id.imvContact)
    }
}