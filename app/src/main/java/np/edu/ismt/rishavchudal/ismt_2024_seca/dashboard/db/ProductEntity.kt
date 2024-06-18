package np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_table")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var name: String,
    var price: String,
    var description: String,
    var image: String? = null,
    var category: String? = null,
    @ColumnInfo(name = "store_location_lat") var storeLocationLat: String? = null,
    @ColumnInfo(name = "store_location_lng") var storeLocationLng: String? = null,
    @ColumnInfo(name = "mark_as_purchased") var markAsPurchased: Boolean = false
)
