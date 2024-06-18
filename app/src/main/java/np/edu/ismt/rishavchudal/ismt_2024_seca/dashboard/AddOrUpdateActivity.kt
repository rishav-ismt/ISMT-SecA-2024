package np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import np.edu.ismt.rishavchudal.ismt_2024_seca.R
import np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.db.SampleDatabase
import np.edu.ismt.rishavchudal.ismt_2024_seca.databinding.ActivityAddOrUpdateBinding

class AddOrUpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddOrUpdateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddOrUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mbAddUpdate.setOnClickListener {
            //TODO connect to database

            val sampleDatabase = SampleDatabase.getInstance(this.applicationContext)
            val productDao = sampleDatabase.getProductDao()
        }
    }
}