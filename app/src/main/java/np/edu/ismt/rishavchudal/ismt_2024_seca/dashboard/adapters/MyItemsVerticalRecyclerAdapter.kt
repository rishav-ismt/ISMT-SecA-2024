package np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.adapters

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import np.edu.ismt.rishavchudal.ismt_2024_seca.R
import np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.Product
import np.edu.ismt.rishavchudal.ismt_2024_seca.databinding.LayoutItemProductExpandedBinding
import np.edu.ismt.rishavchudal.ismt_2024_seca.utility.BitmapScalar
import java.io.IOException

class MyItemsVerticalRecyclerAdapter(
    private val products: List<Product>,
    private val listener: MyItemsAdapterListener,
    private val applicationContext: Context
): RecyclerView.Adapter<MyItemsVerticalRecyclerAdapter.ViewHolder>() {

    class ViewHolder(
        val binding: LayoutItemProductExpandedBinding
    ): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemProductExpandedBinding.inflate(
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
        image?.apply {
            holder.binding.ivItemImage.post {
                var bitmap: Bitmap?
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                        applicationContext.contentResolver,
                        Uri.parse(products[position].image)
                    )
                    bitmap = BitmapScalar.stretchToFill(
                        bitmap,
                        holder.binding.ivItemImage.width,
                        holder.binding.ivItemImage.height
                    )
                    holder.binding.ivItemImage.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        holder.binding.tvItemTitle.text = products[position].name
        holder.binding.tvItemDescription.text = products[position].description
        holder.binding.itemRootLayout.setOnClickListener {
            listener.onItemClicked(products[position], position)
        }
        if (products[position].markAsPurchased) {
            val drawable = ContextCompat.getDrawable(applicationContext, R.drawable.ic_check_circle)
            holder.binding.tvItemTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        } else {
            holder.binding.tvItemTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
        holder.binding.tvTimestamp.text = products[position].timeStamp
    }

    interface MyItemsAdapterListener {
        fun onItemClicked(product: Product, position: Int)
    }
}