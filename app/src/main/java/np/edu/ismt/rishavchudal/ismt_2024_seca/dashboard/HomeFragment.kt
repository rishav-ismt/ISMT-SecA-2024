package np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import np.edu.ismt.rishavchudal.ismt_2024_seca.R
import np.edu.ismt.rishavchudal.ismt_2024_seca.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(
            layoutInflater,
            container,
            false
        )

        return binding.root
    }

    fun add() {
        val a = 1
        val b = 2
    }

}