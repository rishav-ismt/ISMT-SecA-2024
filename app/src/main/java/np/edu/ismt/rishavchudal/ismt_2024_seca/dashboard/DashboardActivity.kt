package np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import np.edu.ismt.rishavchudal.ismt_2024_seca.R
import np.edu.ismt.rishavchudal.ismt_2024_seca.databinding.ActivityDashboardBinding


class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private val fragmentManager = supportFragmentManager
    private val homeFragment = HomeFragment()
    private val myItemsFragment = MyItemsFragment()
    private val suggestionsFragment = SuggestionsFragment()
    private val profileFragment = ProfileFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadRespectiveFragment(homeFragment)
        binding.bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadRespectiveFragment(homeFragment)
                    true
                }
                R.id.my_items -> {
                    loadRespectiveFragment(myItemsFragment)
                    true
                }
                R.id.suggestions -> {
                    loadRespectiveFragment(suggestionsFragment)
                    true
                }
                else -> {
                    loadRespectiveFragment(profileFragment)
                    true
                }
            }
        }
    }

    private fun loadRespectiveFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .replace(
                binding.fragmentContainerView.id,
                fragment,
                null
            )
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }
}