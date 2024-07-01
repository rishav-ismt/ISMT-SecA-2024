package np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import np.edu.ismt.rishavchudal.ismt_2024_seca.R
import np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.db.SampleDatabase
import np.edu.ismt.rishavchudal.ismt_2024_seca.databinding.ActivityAddOrUpdateBinding
import np.edu.ismt.rishavchudal.ismt_2024_seca.utility.AppConstants
import np.edu.ismt.rishavchudal.ismt_2024_seca.utility.BitmapScalar
import np.edu.ismt.rishavchudal.ismt_2024_seca.utility.GeoCoding
import java.io.IOException


class AddOrUpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddOrUpdateBinding
    private var receivedProduct: Product? = null
    private var isForUpdate = false
    private var imageUriPath = ""
    private var productLatitude = ""
    private var productLongitude = ""

    private lateinit var startCustomCameraActivityForResult: ActivityResultLauncher<Intent>
    private lateinit var startGalleryActivityForResult: ActivityResultLauncher<Array<String>>
    private lateinit var startMapActivityForResult: ActivityResultLauncher<Intent>

    companion object {
        const val RESULT_CODE_COMPLETE = 1001
        const val RESULT_CODE_CANCEL = 1002
        const val GALLERY_PERMISSION_REQUEST_CODE = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddOrUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setProductCategories("")
        bindCustomCameraActivityForResult()
        bindMapsActivityForResult()
        bindGalleryActivityForResult()

        binding.ibBack.setOnClickListener {
            setResultWithFinish(RESULT_CODE_CANCEL, null)
        }

        binding.ivProductImage.setOnClickListener {
            handleImageAddButtonClicked()
        }

        binding.mbProductLocation.setOnClickListener {
            startMapActivity()
        }

        binding.mbAddUpdate.setOnClickListener {
            validate()
        }
    }

    private fun setProductCategories(selectedItem: String?) {
        val myAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.product_categories)
        )
        binding.actvSpinnerProductCategory.setAdapter(myAdapter)

        if (!selectedItem.isNullOrBlank()) {
            binding.actvSpinnerProductCategory.setText(selectedItem, false)
        }
    }

    private fun validate() {
        val productName = binding.tietProductName.text.toString()
        val productPrice = binding.tietProductPrice.text.toString()
        val productDescription = binding.tietProductDescription.text.toString()
        val category = binding.actvSpinnerProductCategory.text.toString()

        if (productName.isBlank()) {
            Toast.makeText(
                this@AddOrUpdateActivity,
                "Please enter a product name",
                Toast.LENGTH_SHORT
            ).show()
        } else if(productPrice.isBlank()) {
            Toast.makeText(
                this@AddOrUpdateActivity,
                "Please enter a product price",
                Toast.LENGTH_SHORT
            ).show()
        } else if (productDescription.isBlank()) {
            Toast.makeText(
                this@AddOrUpdateActivity,
                "Please enter a product description",
                Toast.LENGTH_SHORT
            ).show()
        } else if (category.isBlank()) {
            Toast.makeText(
                this@AddOrUpdateActivity,
                "Please enter a product category",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val product = Product(
                name = productName,
                price = productPrice,
                description = productDescription,
                category = category
            )
            val sampleDatabase = SampleDatabase.getInstance(this.applicationContext)
            val productDao = sampleDatabase.getProductDao()
            Thread {
                try {
                    productDao.insertProduct(product.≠toProductEntity())
                    runOnUiThread {
                        Toast.makeText(this@AddOrUpdateActivity, "Product Inserted...", Toast.LENGTH_SHORT).show()
                        clearFieldsData()
                        setResultWithFinish(RESULT_CODE_COMPLETE, product)
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }.start()
        }
    }

    private fun clearFieldsData() {
        binding.tietProductName.text?.clear()
        binding.tietProductPrice.text?.clear()
        binding.tietProductDescription.text?.clear()
    }

    private fun setResultWithFinish(resultCode: Int, product: Product?) {
        val intent = Intent()
        intent.putExtra(AppConstants.KEY_PRODUCT, product)
        setResult(resultCode, intent)
        finish()
    }

    private fun handleImageAddButtonClicked() {
        val pickImageBottomSheetDialog = BottomSheetDialog(this)
        pickImageBottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_pick_image)

        val linearLayoutPickByCamera: LinearLayout = pickImageBottomSheetDialog
            .findViewById(R.id.ll_pick_by_camera)!!
        val linearLayoutPickByGallery: LinearLayout = pickImageBottomSheetDialog
            .findViewById(R.id.ll_pick_by_gallery)!!

        linearLayoutPickByCamera.setOnClickListener {
            pickImageBottomSheetDialog.dismiss()
            startCameraActivity()
        }
        linearLayoutPickByGallery.setOnClickListener {
            pickImageBottomSheetDialog.dismiss()
            startGalleryToPickImage()
        }

        pickImageBottomSheetDialog.setCancelable(true)
        pickImageBottomSheetDialog.show()
    }

    private fun startCameraActivity() {
        val intent = Intent(this, CustomCameraActivity::class.java)
        startCustomCameraActivityForResult.launch(intent)
    }

    private fun allPermissionForGalleryGranted(): Boolean {
        var granted = false
        for (permission in getPermissionsRequiredForCamera()) {
            if (ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                granted = true
            }
        }
        return granted
    }

    private fun getPermissionsRequiredForCamera(): List<String> {
        val permissions: MutableList<String> = ArrayList()
        permissions.add(Manifest.permission.CAMERA)
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return permissions
    }

    private fun startGalleryToPickImage() {
        if (allPermissionForGalleryGranted()) {
            startActivityForResultFromGalleryToPickImage()
        } else {
            requestPermissions(
                getPermissionsRequiredForCamera().toTypedArray(),
                GALLERY_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun startActivityForResultFromGalleryToPickImage() {
        val intent = Intent(
            Intent.ACTION_OPEN_DOCUMENT,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
//        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startGalleryActivityForResult.launch(arrayOf("image/*"))
    }

    private fun loadThumbnailImage(imageUriPath: String) {
        binding.ivProductImage.post(Runnable {
            var bitmap: Bitmap?
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                    contentResolver,
                    Uri.parse(imageUriPath)
                )
                bitmap = BitmapScalar.stretchToFill(
                    bitmap,
                    binding.ivProductImage.width,
                    binding.ivProductImage.height
                )
                binding.ivProductImage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
    }

    private fun startMapActivity() {
        val intent = Intent(this, MapsActivity::class.java)
        startMapActivityForResult.launch(intent)
    }

    private fun onLocationDataFetched() {
        if (productLatitude.isBlank() || productLongitude.isBlank()) {
            return
        }

        try {
            val lat = productLatitude
            val lng = productLongitude
            val geoCodedAddress = GeoCoding.reverseTheGeoCodeToAddress(this, lat, lng)
            binding.mbProductLocation.text = geoCodedAddress
        } catch (exception: java.lang.Exception) {
            exception.printStackTrace()
        }
    }

    private fun bindCustomCameraActivityForResult() {
        startCustomCameraActivityForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == CustomCameraActivity.CAMERA_ACTIVITY_SUCCESS_RESULT_CODE) {
                imageUriPath = it.data?.getStringExtra(CustomCameraActivity.CAMERA_ACTIVITY_OUTPUT_FILE_PATH)!!
                loadThumbnailImage(imageUriPath)
            } else {
                imageUriPath = "";
                binding.ivProductImage.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    private fun bindGalleryActivityForResult() {
        startGalleryActivityForResult = registerForActivityResult(
            ActivityResultContracts.OpenDocument()) {
            if (it != null) {
                imageUriPath = it.toString()
                contentResolver.takePersistableUriPermission(
                    Uri.parse(imageUriPath),
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                loadThumbnailImage(imageUriPath)
            } else {
                imageUriPath = "";
                binding.ivProductImage.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    private fun bindMapsActivityForResult() {
        startMapActivityForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == MapsActivity.MAPS_ACTIVITY_SUCCESS_RESULT_CODE) {
                productLatitude = it.data?.getStringExtra(AppConstants.KEY_PRODUCT_LATITUDE).toString()
                productLongitude = it.data?.getStringExtra(AppConstants.KEY_PRODUCT_LONGITUDE).toString()
                onLocationDataFetched()
            }
        }
    }
    override fun onBackPressed() {
        setResultWithFinish(RESULT_CODE_CANCEL, null)
    }
}