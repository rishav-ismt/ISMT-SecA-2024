package np.edu.ismt.rishavchudal.ismt_2024_seca

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.DashboardActivity
import np.edu.ismt.rishavchudal.ismt_2024_seca.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private lateinit var binding:ActivityLoginBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate: ")
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.tvLogin.text = "Login Page"
        binding.btnLogin.setOnClickListener {
            val email = binding.tietEmail.text.toString()
            val password = binding.tietPassword.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(
                    this@LoginActivity,
                    "Please enter an email",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(
                    this@LoginActivity,
                    "Please enter an valid email",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password.isEmpty()) {
                Toast.makeText(
                    this@LoginActivity,
                    "Please enter a password",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // TODO validate email & password in server or local db

                //Storing in SharedPreferences
                val sharedPreferences = this@LoginActivity.getSharedPreferences(
                    "app",
                    Context.MODE_PRIVATE
                )
                val sharedPrefEditor = sharedPreferences.edit()
                sharedPrefEditor.putBoolean("isLoggedIn", true)
                sharedPrefEditor.apply()

                //Navigating to Dashboard Activity
                val test = Test(
                    variable1 = "test",
                    variable2 = 7
                )

                val intent = Intent(
                    this@LoginActivity,
                    DashboardActivity::class.java
                )
                intent.putExtra(AppConstants.KEY_ENTERED_EMAIL, email)
                intent.putExtra("testobject", test)
                startActivity(intent)
                finish()

            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
    }
}