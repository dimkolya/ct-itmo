package com.github.dimkolya.simplexml

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    private lateinit var textViewError: TextView
    private lateinit var buttonLogin: Button
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var textInputLayoutEmail: TextInputLayout
    private lateinit var textInputLayoutPassword: TextInputLayout

    private val emailRegex: Regex =
        Regex("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)" +
                "*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\" +
                "[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])" +
                "?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-" +
                "9]|[1-9]?[0-9])\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])" +
                "|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]" +
                "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])")
    private val passwordRegex: Regex = Regex("^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{8,}$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewError = findViewById(R.id.textViewError)
        buttonLogin = findViewById(R.id.buttonLogin)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail)
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword)

        buttonLogin.setOnClickListener {
            textViewError.text = ""
            textInputLayoutEmail.isErrorEnabled = false
            textInputLayoutPassword.isErrorEnabled = false
            var errorOccurred = false
            if (editTextEmail.text.isEmpty()) {
                errorOccurred = true
                textInputLayoutEmail.error = getString(R.string.errorEmptyEmail)
            } else if (!editTextEmail.text.matches(emailRegex)) {
                errorOccurred = true
                textInputLayoutEmail.error = getString(R.string.errorIncorrectEmail)
            }
            if (editTextPassword.text.isEmpty()) {
                errorOccurred = true
                textInputLayoutPassword.error = getString(R.string.errorEmptyPassword)
            } else if (!editTextPassword.text.matches(passwordRegex)) {
                errorOccurred = true
                textInputLayoutPassword.error = getString(R.string.errorIncorrectPassword)
            }
            if (!errorOccurred) {
                textViewError.text = getString(R.string.errorIncorrectEmailOrPassword)
            }
        }

        editTextPassword.addTextChangedListener {
            textInputLayoutPassword.isErrorEnabled = false
        }
        editTextEmail.addTextChangedListener {
            textInputLayoutPassword.isErrorEnabled = false
        }
    }
}