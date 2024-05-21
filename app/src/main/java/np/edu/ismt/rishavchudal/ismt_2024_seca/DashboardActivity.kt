package np.edu.ismt.rishavchudal.ismt_2024_seca

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val receivedEmail = intent.getStringExtra(AppConstants.KEY_ENTERED_EMAIL)


        val test = intent.getParcelableExtra<Test>("testobject")
        Toast.makeText(
            this@DashboardActivity,
            "Variable 1: ".plus(test?.variable1),
            Toast.LENGTH_SHORT
        ).show()
    }
}