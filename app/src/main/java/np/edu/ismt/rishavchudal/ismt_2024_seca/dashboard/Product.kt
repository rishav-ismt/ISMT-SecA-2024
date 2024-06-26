package np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.db.ProductEntity

@Parcelize
data class Product(
    var id: Long = 0,
    var name: String,
    var price: String,
    var description: String,
    var image: String? = null,
    var category: String? = null,
    var storeLocationLat: String? = null,
    var storeLocationLng: String? = null,
    var markAsPurchased: Boolean = false,
    var timeStamp: String? = null
): Parcelable


fun Product.toProductEntity(): ProductEntity {
    return ProductEntity(
        id = this.id,
        name = this.name,
        price = this.price,
        description = this.description,
        image = this.image,
        category = this.category,
        storeLocationLat = this.storeLocationLat,
        storeLocationLng = this.storeLocationLng,
        markAsPurchased = this.markAsPurchased,
        timeStamp = this.timeStamp
    )
}
