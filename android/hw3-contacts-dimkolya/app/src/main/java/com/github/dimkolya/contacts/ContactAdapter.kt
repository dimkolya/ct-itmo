package com.github.dimkolya.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private val contacts: List<Contact>,
    private val onClickPhone: (Contact) -> Unit,
    private val onClickSMS: (Contact) -> Unit
) :
    RecyclerView.Adapter<ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val holder = ContactViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        )
        holder.nameView.setOnClickListener {
            onClickPhone(contacts[holder.adapterPosition])
        }
        holder.button.setOnClickListener {
            onClickSMS(contacts[holder.adapterPosition])
        }
        return holder
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) =
        holder.bind(contacts[position])

    override fun getItemCount(): Int {
        return contacts.size
    }
}