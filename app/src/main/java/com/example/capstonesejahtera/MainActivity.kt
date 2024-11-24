package com.example.capstonesejahtera

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private val RC_SIGN_IN = 9001  // Code for Google Sign-In

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        val emailEditText: EditText = findViewById(R.id.edit_email)
        val passwordEditText: EditText = findViewById(R.id.edit_password)

        val signInTextView: TextView = findViewById(R.id.button_sign_in)
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

        // Setup Google Sign-In
        val googleSignInButton: TextView = findViewById(R.id.text_google_login)
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Add your client ID here
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Signed in successfully
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign-in failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        if (account != null) {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign-in success, navigate to home
                        val intent = Intent(this, halamanhome::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
