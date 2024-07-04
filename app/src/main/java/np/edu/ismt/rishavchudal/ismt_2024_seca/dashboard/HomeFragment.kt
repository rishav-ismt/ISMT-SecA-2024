package np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.adapters.MyItemsHorizontalAdapter
import np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.adapters.SuggestionsHorizontalAdapter
import np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.db.SampleDatabase
import np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.db.toProduct
import np.edu.ismt.rishavchudal.ismt_2024_seca.databinding.FragmentHomeBinding
import np.edu.ismt.rishavchudal.ismt_2024_seca.utility.AppConstants

class HomeFragment : Fragment(),
    SuggestionsHorizontalAdapter.SuggestionsHorizontalAdapterListener,
    MyItemsHorizontalAdapter.MyItemsHorizontalAdapterListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var startAddOrUpdateActivityForResult: ActivityResultLauncher<Intent>
    private lateinit var startDetailViewActivityForResult: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startAddOrUpdateActivityForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AddOrUpdateActivity.RESULT_CODE_COMPLETE) {
                setUpMyItemsRecyclerView()
                setUpSuggestionsRecyclerView()
            }
        }

        startDetailViewActivityForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == DetailViewActivity.RESULT_CODE_REFRESH) {
                setUpMyItemsRecyclerView()
                setUpSuggestionsRecyclerView()
            }
        }
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
        setUpWelcomeTextView()
        setUpMyItemsRecyclerView()
        setUpSuggestionsRecyclerView()
        setUpFabAdd()
        return binding.root
    }

    private fun setUpWelcomeTextView() {
        binding.tvGreetings.text = "Hi User,\n\nWelcome to Baby Buy!!"
    }

    private fun setUpMyItemsRecyclerView() {
        val database = SampleDatabase.getInstance(requireActivity().applicationContext)
        val productDao = database.getProductDao()
        Thread {
            val products = productDao.getAllProducts().map { it.toProduct() }
            if (products.isEmpty()) {
                requireActivity().runOnUiThread {
                    binding.clMyItems.visibility = View.GONE
                }
            } else {
                requireActivity().runOnUiThread {
                    binding.clMyItems.visibility = View.VISIBLE
                    val adapter = MyItemsHorizontalAdapter(
                        context = requireActivity(),
                        products = products,
                        listener = this
                    )
                    binding.rvMyItems.layoutManager = LinearLayoutManager(
                        requireActivity(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    binding.rvMyItems.adapter = adapter
                }
            }

        }.start()
    }

    private fun setUpSuggestionsRecyclerView() {
        val adapter = SuggestionsHorizontalAdapter(
            getSuggestions(),
            this
        )
        binding.rvSuggestions.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvSuggestions.adapter = adapter
    }

    private fun setUpFabAdd() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(requireActivity(), AddOrUpdateActivity::class.java)
            startAddOrUpdateActivityForResult.launch(intent)
        }
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

    override fun onViewSuggestionItemClicked(product: Product) {
        val intent = Intent(
            requireActivity(),
            DetailViewActivity::class.java
        )
        intent.putExtra(AppConstants.KEY_PRODUCT, product)
        intent.putExtra(AppConstants.KEY_IS_SUGGESTION, true)
        startDetailViewActivityForResult.launch(intent)
    }

    override fun onAddSuggestionItemClicked(product: Product) {
        val intent = Intent(
            requireActivity(),
            AddOrUpdateActivity::class.java
        )
        intent.putExtra(AppConstants.KEY_PRODUCT, product)
        intent.putExtra(AppConstants.KEY_IS_UPDATE, false)
        startAddOrUpdateActivityForResult.launch(intent)
    }

    override fun onViewMyItemClicked(product: Product) {
        val intent = Intent(
            requireActivity(),
            DetailViewActivity::class.java
        )
        intent.putExtra(AppConstants.KEY_PRODUCT, product)
        startDetailViewActivityForResult.launch(intent)
    }

}