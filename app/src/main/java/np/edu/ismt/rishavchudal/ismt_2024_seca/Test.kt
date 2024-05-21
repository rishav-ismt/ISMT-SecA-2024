package np.edu.ismt.rishavchudal.ismt_2024_seca

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Test(
    val variable1: String,
    val variable2: Int
): Parcelable