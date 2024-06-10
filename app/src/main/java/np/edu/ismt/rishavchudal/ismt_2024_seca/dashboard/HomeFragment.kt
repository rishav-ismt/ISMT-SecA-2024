package np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import np.edu.ismt.rishavchudal.ismt_2024_seca.R
import np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.adapters.SuggestionsHorizontalAdapter
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
        setUpSuggestionsRecyclerView()
        return binding.root
    }

    private fun setUpSuggestionsRecyclerView() {
        val adapter = SuggestionsHorizontalAdapter(
            getSuggestions()
        )
        binding.rvSuggestions.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvSuggestions.adapter = adapter
    }

    private fun getSuggestions(): List<Product> {
        val suggestions = mutableListOf<Product>()

        val suggestion1 = Product(
            name = "Item 1",
            price = "400",
            description = "Description 1"
        )
        suggestions.add(suggestion1)

        val suggestion2 = Product(
            name = "Item 2",
            price = "500",
            description = "Description 2"
        )
        suggestions.add(suggestion2)

        val suggestion3 = Product(
            name = "Item 3",
            price = "600",
            description = "Description 3"
        )
        suggestions.add(suggestion3)

        val suggestion4 = Product(
            name = "Item 4",
            price = "700",
            description = "Description 4"
        )
        suggestions.add(suggestion4)

        val suggestion5 = Product(
            name = "Item 5",
            price = "800",
            description = "Description 5"
        )
        suggestions.add(suggestion5)

        val suggestion6 = Product(
            name = "Item 6",
            price = "900",
            description = "Description 6"
        )
        suggestions.add(suggestion6)

        return suggestions
    }

}