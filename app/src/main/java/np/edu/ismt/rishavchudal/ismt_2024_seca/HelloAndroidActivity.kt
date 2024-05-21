package np.edu.ismt.rishavchudal.ismt_2024_seca

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HelloAndroidActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hello_android)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Navigating to another activity
        Handler().postDelayed(
            {
                //Fetching Shared Preferences Data
                val sharedPreferences = this@HelloAndroidActivity.getSharedPreferences(
                    "app",
                    Context.MODE_PRIVATE
                )
                val isLoggedIn = sharedPreferences.getBoolean(
                    "isLoggedIn",
                    false
                )

                val intent: Intent
                if (isLoggedIn) {
                    //navigating to dashboard page
                    intent = Intent(this, DashboardActivity::class.java)
                } else {
                    //navigating to login page
                    intent = Intent(this, LoginActivity::class.java)
                }
                startActivity(intent)
                finish()
            },
            2000
        )

    }
}