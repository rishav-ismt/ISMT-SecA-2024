package np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import np.edu.ismt.rishavchudal.ismt_2024_seca.R
import np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.Product
import np.edu.ismt.rishavchudal.ismt_2024_seca.databinding.LayoutItemProductExpandedBinding
import np.edu.ismt.rishavchudal.ismt_2024_seca.databinding.LayoutSuggestionProductExpandedBinding
import np.edu.ismt.rishavchudal.ismt_2024_seca.utility.UiUtility

class SuggestionsVerticalRecyclerAdapter(
    private val products: List<Product>,
    private val listener: MyItemsAdapterListener,
    private val applicationContext: Context
): RecyclerView.Adapter<SuggestionsVerticalRecyclerAdapter.ViewHolder>() {

    class ViewHolder(
        val binding: LayoutSuggestionProductExpandedBinding
    ): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutSuggestionProductExpandedBinding.inflate(
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
        val image = products[position].image
        UiUtility.loadImageToImageView(
            applicationContext,
            image,
            holder.binding.ivItemImage
        )

        holder.binding.tvItemTitle.text = products[position].name
        holder.binding.tvItemDescription.text = products[position].description
        holder.binding.itemRootLayout.setOnClickListener {
            listener.onItemClicked(products[position])
        }
        if (products[position].markAsPurchased) {
            val drawable = ContextCompat.getDrawable(applicationContext, R.drawable.ic_check_circle)
            holder.binding.tvItemTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        } else {
            holder.binding.tvItemTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }

        holder.binding.mbAdd.setOnClickListener {
            listener.onAddItemClicked(products[position])
        }
    }

    interface MyItemsAdapterListener {
        fun onItemClicked(product: Product)
        fun onAddItemClicked(product: Product)
    }
}