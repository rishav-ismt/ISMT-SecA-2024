package np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.Product

@Dao
interface ProductDao {
    @Insert
    fun insertProduct(productEntity: ProductEntity)

    @Insert
    fun insertProducts(products: List<ProductEntity>)

    @Update
    fun updateProduct(productEntity: ProductEntity)

    @Delete
    fun deleteProduct(productEntity: ProductEntity)

    @Query("select * from product_table")
    fun getAllProducts(): List<ProductEntity>
}