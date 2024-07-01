package np.edu.ismt.rishavchudal.ismt_2024_seca.utility

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.common.util.CollectionUtils
import java.lang.Exception
import java.util.Locale

class GeoCoding {
    companion object {
        fun reverseTheGeoCodeToAddress(context: Context, latitude: String, longitude: String): String {
            var finalAddress = ""
            try {
                var countryName = ""
                var cityName = ""
                var address = ""
                var areaName = ""

                val addresses: MutableList<Address>?
                val geocoder = Geocoder(context, Locale.ENGLISH)
                addresses =
                    geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
                if (!CollectionUtils.isEmpty(addresses)) {
                    val fetchedAddress = addresses!![0]
                    if (fetchedAddress.maxAddressLineIndex > -1) {
                        address = fetchedAddress.getAddressLine(0)
                        fetchedAddress.countryName?.let {
                            countryName = it
                        }
                        fetchedAddress.locality?.let {
                            cityName = it
                        }
                        fetchedAddress.subLocality?.let {
                            areaName = it
                        }
                    }
                }
                finalAddress = areaName.plus(", ").plus(cityName).plus(", ").plus(countryName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return finalAddress
        }
    }
}