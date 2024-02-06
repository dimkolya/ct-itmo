package com.github.dimkolya.contacts

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactViewHolder(root: View) : RecyclerView.ViewHolder(root) {
    val nameView = root.findViewById<TextView>(R.id.contact_name)
    val button = root.findViewById<ImageButton>(R.id.smsButton)
    private val phoneNumberView = root.findViewById<TextView>(R.id.contact_phone_number)

    fun bind(contact: Contact) {
        nameView.text = contact.name
        phoneNumberView.text = contact.phoneNumber
    }
}