package np.edu.ismt.rishavchudal.ismt_2024_seca.utility

import android.graphics.Bitmap

class BitmapScalar {
    companion object {
        // scale and keep aspect ratio
        fun scaleToFitWidth(b: Bitmap, width: Int): Bitmap? {
            val factor = width / b.width.toFloat()
            return Bitmap.createScaledBitmap(b, width, (b.height * factor).toInt(), true)
        }


        // scale and keep aspect ratio
        fun scaleToFitHeight(b: Bitmap, height: Int): Bitmap? {
            val factor = height / b.height.toFloat()
            return Bitmap.createScaledBitmap(b, (b.width * factor).toInt(), height, true)
        }


        // scale and keep aspect ratio
        fun scaleToFill(b: Bitmap, width: Int, height: Int): Bitmap? {
            val factorH = height / b.width.toFloat()
            val factorW = width / b.width.toFloat()
            val factorToUse = if (factorH > factorW) factorW else factorH
            return Bitmap.createScaledBitmap(
                b,
                (b.width * factorToUse).toInt(),
                (b.height * factorToUse).toInt(),
                true
            )
        }


        // scale and don't keep aspect ratio
        fun stretchToFill(b: Bitmap, width: Int, height: Int): Bitmap? {
            val factorH = height / b.height.toFloat()
            val factorW = width / b.width.toFloat()
            return Bitmap.createScaledBitmap(
                b,
                (b.width * factorW).toInt(),
                (b.height * factorH).toInt(),
                true
            )
        }
    }
}