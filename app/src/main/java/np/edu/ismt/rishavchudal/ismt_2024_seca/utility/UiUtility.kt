package np.edu.ismt.rishavchudal.ismt_2024_seca.utility

import android.content.Context
import android.widget.Toast

class UiUtility {
    companion object {
        fun showToast(context: Context?, message: String) {
            context?.apply {
                Toast.makeText(this.applicationContext, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}