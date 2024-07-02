package np.edu.ismt.rishavchudal.ismt_2024_seca.utility

import android.content.Context
import android.widget.Toast
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
    }
}