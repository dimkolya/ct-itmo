package com.github.dimkolya.contacts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_CONTACTS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_CONTACTS),
                CONTACTS_REQUEST_ID
            )
        } else {
            showContacts()
        }
    }

    private fun abstractIntentMaker(contact: Contact, token: String, intentID: String) {
        val intent = Intent(intentID)
        intent.data = Uri.parse(token + contact.phoneNumber)
        startActivity(intent)
    }

    private fun showContacts() {
        val myRecyclerView = findViewById<RecyclerView>(R.id.recyclerviewContacts)
        val viewManager = LinearLayoutManager(this)
        val myAdapter = ContactAdapter(fetchAllContacts(), {
            abstractIntentMaker(it, "tel:", Intent.ACTION_DIAL)
        }, {
            abstractIntentMaker(it, "sms:", Intent.ACTION_VIEW)
        })
        myRecyclerView.apply {
            layoutManager = viewManager
            adapter = myAdapter
        }
        Toast.makeText(
            this,
            resources.getQuantityString(
                R.plurals.number_of_contacts,
                myAdapter.itemCount,
                myAdapter.itemCount
            ),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CONTACTS_REQUEST_ID -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showContacts()
                } else {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.contacts_permission_request),
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }

    companion object {
        private const val CONTACTS_REQUEST_ID = 0
    }
}