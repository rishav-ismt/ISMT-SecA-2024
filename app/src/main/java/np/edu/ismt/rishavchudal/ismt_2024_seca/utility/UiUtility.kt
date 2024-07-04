package np.edu.ismt.rishavchudal.ismt_2024_seca.utility

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class UiUtility {
    companion object {
        fun showToast(context: Context?, message: String) {
            context?.apply {
                Toast.makeText(this.applicationContext, message, Toast.LENGTH_LONG).show()
            }
        }

        fun getCurrentTimeStampWithActionSpecified(action: String): String {
            var timestamp = ""
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                timestamp = action.plus(dateFormat.format(Date()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return timestamp
        }

        fun loadImageToImageView(context: Context, imageUriPath: String?, imageView: ImageView?) {
            if (imageView == null) {
                return
            }

            if (imageUriPath.isNullOrEmpty()) {
                val drawable =
                    ContextCompat.getDrawable(context, android.R.drawable.ic_menu_gallery)
                imageView.setImageDrawable(drawable)
                return
            }

            imageView.post {
                var bitmap: Bitmap?
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                        context.contentResolver,
                        Uri.parse(imageUriPath)
                    )
                    bitmap = BitmapScalar.stretchToFill(
                        bitmap,
                        imageView.width,
                        imageView.height
                    )
                    imageView.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}