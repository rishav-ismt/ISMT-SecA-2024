package np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.Product
import np.edu.ismt.rishavchudal.ismt_2024_seca.databinding.LayoutSuggestionItemBinding

class MyItemsHorizontalAdapter(
    private val products: List<Product>,
    private val listener: MyItemsHorizontalAdapterListener
): RecyclerView.Adapter<MyItemsHorizontalAdapter.ViewHolder>() {

    class ViewHolder(
        val binding: LayoutSuggestionItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutSuggestionItemBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val product = products[position]
        holder.binding.tvSuggestionName.text = product.name
        holder.binding.tvSuggestionDescription.text = product.description
        holder.binding.mbSuggestionAdd.visibility = View.GONE
        holder.binding.mbSuggestionView.setOnClickListener {
            listener.onViewMyItemClicked(product)
        }
    }

    interface MyItemsHorizontalAdapterListener {
        fun onViewMyItemClicked(product: Product)
    }
}