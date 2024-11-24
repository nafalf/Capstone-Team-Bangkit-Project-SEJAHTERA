package com.example.capstonesejahtera

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class halamanhome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.halamanhome)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Navigasi kembali ke MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

        // Tidak memanggil super.onBackPressed() untuk mencegah aksi default
        // (Menutup aktivitas saat ini secara bawaan).
    }
}
