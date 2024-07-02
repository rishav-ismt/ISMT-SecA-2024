package np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.telephony.SmsManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import np.edu.ismt.rishavchudal.ismt_2024_seca.R
import np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard.db.SampleDatabase
import np.edu.ismt.rishavchudal.ismt_2024_seca.databinding.ActivityDetailViewBinding
import np.edu.ismt.rishavchudal.ismt_2024_seca.utility.AppConstants
import np.edu.ismt.rishavchudal.ismt_2024_seca.utility.BitmapScalar
import np.edu.ismt.rishavchudal.ismt_2024_seca.utility.GeoCoding
import np.edu.ismt.rishavchudal.ismt_2024_seca.utility.UiUtility
import java.io.IOException


class DetailViewActivity : AppCompatActivity() {
    private lateinit var detailViewBinding: ActivityDetailViewBinding
    private var receivedProduct: Product? = null
    private var tieContact: TextInputEditText? = null

    companion object {
        const val RESULT_CODE_CANCEL = 2001
        const val RESULT_CODE_REFRESH = 2002
        const val SMS_PERMISSIONS_REQUEST_CODE = 111
    }

    private lateinit var startAddItemActivity: ActivityResultLauncher<Intent>
    private lateinit var startContactActivityForResult: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailViewBinding = ActivityDetailViewBinding.inflate(layoutInflater)
        setContentView(detailViewBinding.root)
        bindAddOrUpdateActivityForResult()
        bindContactPickerActivityForResult()

        receivedProduct = intent.getParcelableExtra(AppConstants.KEY_PRODUCT)
        receivedProduct?.apply {
            populateDataToTheViews(this)
        }
        setUpButtons()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSIONS_REQUEST_CODE && areSmsPermissionsGranted(this)) {
            showSmsBottomSheetDialog()
        } else {
            UiUtility.showToast(this, "Please provide permission for SMS")
        }
    }

    private fun populateDataToTheViews(product: Product?) {
        detailViewBinding.tvTimestamp.text = product?.timeStamp
        detailViewBinding.productTitle.text = product?.name
        detailViewBinding.productPrice.text = product?.price
        detailViewBinding.productDescription.text = product?.description
        detailViewBinding.cbPurchased.isChecked = (product?.markAsPurchased == true)

        /**
         * Scaling the image into the view based on its width and heigh
         */
        product?.image?.apply {
            detailViewBinding.productImage.post {
                var bitmap: Bitmap?
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                        applicationContext.contentResolver,
                        Uri.parse(this)
                    )
                    bitmap = BitmapScalar.stretchToFill(
                        bitmap,
                        detailViewBinding.productImage.width,
                        detailViewBinding.productImage.height
                    )
                    detailViewBinding.productImage.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        /**
         * Splitting the latlng string into lat and lng
         * Based on the latlng, reverse geocoding to get the actual location
         */
        try {
            val lat = product?.storeLocationLat
            val lng = product?.storeLocationLng
            val geoCodedAddress = GeoCoding.reverseTheGeoCodeToAddress(
                this,
                lat ?: "",
                lng ?: ""
            )
            detailViewBinding.productLocation.text = geoCodedAddress
        } catch (exception: java.lang.Exception) {
            exception.printStackTrace()
        }
    }

    private fun setUpButtons() {
        setUpBackButton()
        setUpEditButton()
        setUpDeleteButton()
        setUpShareButton()
        setUpMarkAsPurchasedCheckbox()
    }

    private fun setUpBackButton() {
        detailViewBinding.ibBack.setOnClickListener {
            setResultWithFinish(RESULT_CODE_REFRESH)
        }
    }

    private fun setUpEditButton() {
        detailViewBinding.ibEdit.setOnClickListener {
            val intent = Intent(
                this,
                AddOrUpdateActivity::class.java
            ).apply {
                this.putExtra(AppConstants.KEY_PRODUCT, receivedProduct)
            }
            startAddItemActivity.launch(intent)
        }
    }

    private fun setUpDeleteButton() {
        detailViewBinding.ibDelete.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Alert")
                .setMessage("Do you want to delete this product?")
                .setPositiveButton(
                    "Yes",
                    DialogInterface.OnClickListener {
                            dialogInterface,
                            i -> deleteProduct()
                    })
                .setNegativeButton(
                    "No",
                    DialogInterface.OnClickListener {
                            dialogInterface,
                            i ->  dialogInterface.dismiss()

                    })
                .show()
        }
    }

    private fun setUpShareButton() {
        detailViewBinding.ibShare.setOnClickListener {
            if (areSmsPermissionsGranted(this)) {
                showSmsBottomSheetDialog()
            } else {
                requestPermissions(
                    smsPermissionsList().toTypedArray(),
                    SMS_PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    private fun setUpMarkAsPurchasedCheckbox() {
        detailViewBinding.cbPurchased.setOnCheckedChangeListener { _, isChecked ->
            handleCheckChangedForMarkAsPurchased(
                isChecked
            )
        }
    }

    private fun handleCheckChangedForMarkAsPurchased(isChecked: Boolean) {
        if (isChecked) {
            updateProductWithMarkAsPurchasedTrue()
        } else {
            updateProductWithMarkAsPurchasedFalse()
        }
    }

    private fun updateProductWithMarkAsPurchasedTrue() {
        receivedProduct?.markAsPurchased = true
        updateProductDataInDb(receivedProduct)
    }

    private fun updateProductWithMarkAsPurchasedFalse() {
        receivedProduct?.markAsPurchased = false
        updateProductDataInDb(receivedProduct)
    }

    private fun updateProductDataInDb(product: Product?) {
        val testDatabase = SampleDatabase.getInstance(this.applicationContext)
        val productDao = testDatabase.getProductDao()

        Thread {
            try {
                product?.apply {
                    productDao.updateProduct(this.toProductEntity())
                    runOnUiThread {
                        UiUtility.showToast(
                            this@DetailViewActivity,
                            "Product updated successfully"
                        )
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                runOnUiThread {
                    UiUtility.showToast(
                        this@DetailViewActivity,
                        "Cannot update product."
                    )
                }
            }
        }.start()
    }

    private fun deleteProduct() {
        val testDatabase = SampleDatabase.getInstance(this.applicationContext)
        val productDao = testDatabase.getProductDao()

        Thread {
            try {
                receivedProduct?.apply {
                    productDao.deleteProduct(this.toProductEntity())
                    runOnUiThread {
                        UiUtility.showToast(
                            this@DetailViewActivity,
                            "Product deleted successfully"
                        )
                        setResultWithFinish(RESULT_CODE_REFRESH)
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                runOnUiThread {
                    UiUtility.showToast(
                        this@DetailViewActivity,
                        "Cannot delete product."
                    )
                }
            }
        }.start()
    }

    private fun setResultWithFinish(resultCode: Int) {
        setResult(resultCode)
        finish()
    }

    private fun showSmsBottomSheetDialog() {
        val smsBottomSheetDialog = BottomSheetDialog(this)
        smsBottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_send_sms)

        val tilContact: TextInputLayout? = smsBottomSheetDialog.findViewById(R.id.til_contact)
        tieContact = smsBottomSheetDialog.findViewById(R.id.tie_contact)
        val sendSmsButton: MaterialButton? = smsBottomSheetDialog.findViewById(R.id.mb_send_sms)

        tilContact?.setEndIconOnClickListener {
            //TODO open Contact Activity
            val pickContact = Intent(Intent.ACTION_PICK)
            pickContact.setDataAndType(
                ContactsContract.Contacts.CONTENT_URI,
                ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            )
            startContactActivityForResult.launch(pickContact)
        }

        sendSmsButton?.setOnClickListener {
            val contact = tieContact?.text.toString()
            //TODO validation
            if (contact.isBlank()) {
                tilContact?.error = "Enter Contact"
            } else {
                sendSms(contact)
                smsBottomSheetDialog.dismiss()
            }
        }
        smsBottomSheetDialog.setCancelable(true)
        smsBottomSheetDialog.show()
    }

    private fun areSmsPermissionsGranted(context: Context): Boolean {
        var areAllPermissionGranted = false
        for (permission in smsPermissionsList()!!) {
            if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                areAllPermissionGranted = true
            } else {
                areAllPermissionGranted = false
                break
            }
        }
        return areAllPermissionGranted
    }
    private fun smsPermissionsList(): List<String> {
        val smsPermissions: MutableList<String> = ArrayList()
        smsPermissions.add(Manifest.permission.READ_SMS)
        smsPermissions.add(Manifest.permission.SEND_SMS)
        smsPermissions.add(Manifest.permission.READ_CONTACTS)
        return smsPermissions
    }

    private fun sendSms(contact: String) {
        Thread {
            try {
                val smsManager: SmsManager = SmsManager.getDefault()
                val message = """
            Item: ${receivedProduct!!.name}
            Price: ${receivedProduct!!.price}
            Description: ${receivedProduct!!.description}
            """.trimIndent()
                smsManager.sendTextMessage(
                    contact,
                    null,
                    message,
                    null,
                    null
                )
                runOnUiThread {
                    UiUtility.showToast(this, "SMS Sent. Please check your message app...")
                }
            } catch (exception: Exception) {
                runOnUiThread {
                    UiUtility.showToast(this, "Couldn't Send SMS...")
                }
            }
        }.start()


        //If the above SMS manager didn't work
//        openSmsAppToSendMessage(contact, message)
    }

    private fun openSmsAppToSendMessage(contact: String, message: String) {
        val sendIntent = Intent(Intent.ACTION_VIEW)
        sendIntent.data = Uri.parse("smsto:$contact")
        sendIntent.putExtra("sms_body", message)
        startActivity(intent)
    }

    private fun fetchContactNumberFromData(data: Intent) {
        val contactUri = data.data

        // Specify which fields you want
        // your query to return values for
        val queryFields = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        this.contentResolver
            .query(
                contactUri!!,
                null,
                null,
                null,
                null
            ).use { cursor ->
                // Double-check that you
                // actually got results
                if (cursor!!.count == 0) return

                // Pull out the first column of
                // the first row of data
                // that is your contact's name
                cursor.moveToFirst()
                val contactNumberIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val contactNumber = cursor.getString(contactNumberIndex).apply {
                //Replacing the brackets and hyphens with empty string as we don't need here
                    this.replace(
                        Regex("[()\\-\\s]+"),
                        ""
                    )
                }
                tieContact?.setText(contactNumber)
            }
    }

    private fun bindAddOrUpdateActivityForResult() {
        startAddItemActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == AddOrUpdateActivity.RESULT_CODE_COMPLETE) {
                val product = it.data?.getParcelableExtra<Product>(AppConstants.KEY_PRODUCT)
                populateDataToTheViews(product)
            }
        }
    }

    private fun bindContactPickerActivityForResult() {
        startContactActivityForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it != null) {
                fetchContactNumberFromData(it.data!!)
            }
        }
    }
}