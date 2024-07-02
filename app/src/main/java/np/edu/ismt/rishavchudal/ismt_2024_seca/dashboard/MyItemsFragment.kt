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
import np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.adapters.MyItemsVerticalRecyclerAdapter
import np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.db.SampleDatabase
import np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.db.toProduct
import np.edu.ismt.rishavchudal.ismt_2024_seca.databinding.FragmentMyItemsBinding
import np.edu.ismt.rishavchudal.ismt_2024_seca.utility.AppConstants
import np.edu.ismt.rishavchudal.ismt_2024_seca.utility.UiUtility


class MyItemsFragment : Fragment(), MyItemsVerticalRecyclerAdapter.MyItemsAdapterListener {
    private lateinit var binding: FragmentMyItemsBinding
    private lateinit var startAddOrUpdateActivityForResult: ActivityResultLauncher<Intent>
    private lateinit var startDetailViewActivity: ActivityResultLauncher<Intent>
    private lateinit var adapter: MyItemsVerticalRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startAddOrUpdateActivityForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AddOrUpdateActivity.RESULT_CODE_COMPLETE) {
                setUpRecyclerView()
            } else {
                //TODO Do nothing
            }
        }

        startDetailViewActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == DetailViewActivity.RESULT_CODE_REFRESH) {
                setUpRecyclerView()
            } else {
                //Do Nothing
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyItemsBinding.inflate(layoutInflater, container, false)
        setUpViews()
        return binding.root
    }

    private fun setUpViews() {
        setUpFloatingActionButton()
        setUpRecyclerView()
    }

    private fun setUpFloatingActionButton() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(requireActivity(), AddOrUpdateActivity::class.java)
            startAddOrUpdateActivityForResult.launch(intent)
        }
    }

    private fun setUpRecyclerView() {
        //TODO fetch data from source (remote server)
        val testDatabase = SampleDatabase.getInstance(requireActivity().applicationContext)
        val productDao = testDatabase.getProductDao()

        Thread {
            try {
                val products = productDao.getAllProducts().map { it.toProduct() }
                if (products.isEmpty()) {
                    requireActivity().runOnUiThread {
                        UiUtility.showToast(requireActivity(), "No Items Added...")
                        populateRecyclerView(emptyList())
                    }
                } else {
                    requireActivity().runOnUiThread {
                        populateRecyclerView(products)
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                requireActivity().runOnUiThread {
                    UiUtility.showToast(requireActivity(), "Couldn't load items.")
                }
            }
        }.start()
    }

    private fun populateRecyclerView(products: List<Product>) {
        adapter = MyItemsVerticalRecyclerAdapter(
            products,
            this,
            requireActivity().applicationContext
        )
        binding.rvMyItems.adapter = adapter
        binding.rvMyItems.layoutManager = LinearLayoutManager(requireActivity())
    }

    override fun onItemClicked(product: Product, position: Int) {
        val intent = Intent(requireActivity(), DetailViewActivity::class.java)
        intent.putExtra(AppConstants.KEY_PRODUCT, product)
        startDetailViewActivity.launch(intent)
    }
}