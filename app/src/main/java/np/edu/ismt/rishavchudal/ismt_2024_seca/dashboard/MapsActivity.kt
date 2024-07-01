package np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnTokenCanceledListener
import np.edu.ismt.rishavchudal.ismt_2024_seca.R
import np.edu.ismt.rishavchudal.ismt_2024_seca.databinding.ActivityMapsBinding
import np.edu.ismt.rishavchudal.ismt_2024_seca.utility.AppConstants
import np.edu.ismt.rishavchudal.ismt_2024_seca.utility.UiUtility


class MapsActivity : AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMyLocationClickListener,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMarkerDragListener
{

    private lateinit var mapsBinding: ActivityMapsBinding
    private var marker: Marker? = null
    private lateinit var googleMap: GoogleMap
    private val fusedLocationProviderClient: FusedLocationProviderClient? = null
    companion object {
        const val MAPS_ACTIVITY_SUCCESS_RESULT_CODE = 3014
        const val MAPS_ACTIVITY_FAILURE_RESULT_CODE = 3015
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapsBinding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(mapsBinding.root)

        val supportMapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        mapsBinding.ibBack.setOnClickListener {
            setResultWithFinishOnFailure(MAPS_ACTIVITY_FAILURE_RESULT_CODE)
        }

        mapsBinding.mbSaveLocation.setOnClickListener {
            if (marker == null) {
                UiUtility.showToast(
                    this,
                    "Please add a location marker first. Long press on the map to set it"
                )
            } else {
                setResultWithFinishOnSuccess(MAPS_ACTIVITY_SUCCESS_RESULT_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && allPermissionForLocationGranted()) {
            enableMyLocation()
        } else {
            UiUtility.showToast(this, "Please provide permission for SMS")
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap.setOnMyLocationClickListener(this)
        this.googleMap.setOnMyLocationButtonClickListener(this)
        this.googleMap.setOnMarkerDragListener(this)
        this.googleMap.setOnMapLongClickListener {
            marker?.remove()
            marker = this.googleMap.addMarker(
                MarkerOptions().position(it).draggable(true)
            )
        }
        enableMyLocation()
        locateMapToDefaultLocation()
    }

    override fun onMyLocationClick(location: Location) {
        marker?.apply {
            this.remove()
        }
        locateMarkerToCurrentLocation(location)
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMarkerDrag(p0: Marker) {
        this.marker = p0
    }

    override fun onMarkerDragEnd(p0: Marker) {
        this.marker = p0
    }

    override fun onMarkerDragStart(p0: Marker) {
        this.marker = p0
    }

    private fun locateMarkerToCurrentLocation(currentLocation: Location) {
        val currentLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        marker?.apply {
            remove()
        }
        marker = googleMap.addMarker(
            MarkerOptions()
                .position(currentLatLng)
                .title("You are here")
                .draggable(true)
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
    }

    private fun locateMapToDefaultLocation() {
        val kathmandu = LatLng(27.7172, 85.3240)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kathmandu, 15f))
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (allPermissionForLocationGranted()) {
            fusedLocationProviderClient?.getCurrentLocation(
                LocationRequest.QUALITY_BALANCED_POWER_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(onTokenCanceledListener: OnTokenCanceledListener): CancellationToken {
                        return this
                    }

                    override fun isCancellationRequested(): Boolean {
                        return false
                    }
                }
            )?.addOnSuccessListener(OnSuccessListener<Location?> { location ->
                locateMarkerToCurrentLocation(
                    location!!
                )
            })?.addOnFailureListener(
                OnFailureListener { e ->
                    e.printStackTrace()
                    locateMapToDefaultLocation()
                })
            googleMap.isMyLocationEnabled = true
            return
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    getPermissionsRequiredForLocation().toTypedArray(),
                    101
                )
            }
        }
    }

    private fun allPermissionForLocationGranted(): Boolean {
        var granted = false
        for (permission in getPermissionsRequiredForLocation()) {
            if (ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                granted = true
            }
        }
        return granted
    }

    private fun getPermissionsRequiredForLocation(): List<String> {
        val permissions: MutableList<String> = ArrayList()
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        return permissions
    }

    private fun setResultWithFinishOnSuccess(resultCode: Int) {
        val intent = Intent()
        val latitude = (marker?.position?.latitude).toString()
        val longitude = (marker?.position?.longitude).toString()
        intent.putExtra(AppConstants.KEY_PRODUCT_LATITUDE, latitude)
        intent.putExtra(AppConstants.KEY_PRODUCT_LONGITUDE, longitude)
        setResult(resultCode, intent)
        finish()
    }

    private fun setResultWithFinishOnFailure(resultCode: Int) {
        setResult(resultCode)
        finish()
    }
}