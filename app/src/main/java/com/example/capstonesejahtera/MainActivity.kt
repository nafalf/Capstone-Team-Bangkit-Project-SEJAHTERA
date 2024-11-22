package com.example.capstonesejahtera

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
//andre
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()

        val emailEditText: EditText = findViewById(R.id.edit_email)
        val passwordEditText: EditText = findViewById(R.id.edit_password)

        val signInTextView: TextView = findViewById(R.id.text_sign_in)
        signInTextView.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ambil data user dari Firestore berdasarkan email
            db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val user = documents.documents[0]
                        val storedPassword = user.getString("password")

                        // Cocokkan password
                        if (storedPassword == password) {
                            // Navigasi ke halaman home
                            val intent = Intent(this, halamanhome::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Password salah", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Email tidak terdaftar", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show()
                }
        }

        // Listener untuk klik "Sign Up"
        val signUpTextView: TextView = findViewById(R.id.text_sign_up_prompt)
        signUpTextView.setOnClickListener {
            val intent = Intent(this, Registrasi::class.java)
            startActivity(intent)
        }
    }
}
