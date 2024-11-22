package com.example.capstonesejahtera

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.capstonesejahtera.databinding.RegistrasiakunBinding
import com.google.firebase.firestore.FirebaseFirestore

class Registrasi : AppCompatActivity() {
    private lateinit var binding: RegistrasiakunBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegistrasiakunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase Firestore
        firestore = FirebaseFirestore.getInstance()

        binding.buttonSignUp.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            val confirmPassword = binding.editTextConfirmPassword.text.toString().trim()

            // Validasi input
            if (email.isEmpty()) {
                binding.editTextEmail.error = "Email tidak boleh kosong"
                binding.editTextEmail.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.editTextEmail.error = "Email tidak valid"
                binding.editTextEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.editTextPassword.error = "Password tidak boleh kosong"
                binding.editTextPassword.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.editTextPassword.error = "Password harus memiliki minimal 6 karakter"
                binding.editTextPassword.requestFocus()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                binding.editTextConfirmPassword.error = "Password tidak cocok"
                binding.editTextConfirmPassword.requestFocus()
                return@setOnClickListener
            }

            // Menyimpan data ke Firestore
            binding.progressBar.visibility = View.VISIBLE

            val user = hashMapOf(
                "email" to email,
                "password" to password // Jangan simpan password secara plain text di produksi
            )

            firestore.collection("users")
                .add(user)
                .addOnSuccessListener {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                    finish() // Menutup aktivitas ini
                }
                .addOnFailureListener { exception ->
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Registrasi gagal: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}
