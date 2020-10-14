package com.peagainc.hutech

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.SphericalUtil
import java.math.BigDecimal
import java.math.RoundingMode
import com.peagainc.hutech.adapter.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import com.peagainc.hutech.adapter.PermissionUtils.isPermissionGranted
import com.peagainc.hutech.adapter.PermissionUtils.requestPermission

class CheckLocationActivity : AppCompatActivity(), GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback{
    private var permissionDenied = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap
    private var distance : Double = 0.0
    private  lateinit var txtCampusName: TextView
    private  lateinit var txtDistanceFrom: TextView
    private  lateinit var txtDistance: TextView
    private  lateinit var txtCurrentLocation: TextView
    private  lateinit var cirNextStep: CircularProgressButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_location)
        initView()
        addEvent()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun addEvent() {
        cirNextStep.setOnClickListener{
            val intent = Intent(this, DetectFaceActivity::class.java).apply {  }
            startActivity(intent)
        }

    }
    private fun initView() {
            txtCampusName = findViewById(R.id.txtCampusName)
            txtDistanceFrom = findViewById(R.id.txtDistanceFrom)
            txtDistance= findViewById(R.id.txtDistance)
            txtCurrentLocation= findViewById(R.id.txtCurrentLocation)
            cirNextStep = findViewById(R.id.cirNextStep)
            cirNextStep.isEnabled = false
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
        enableMyLocation()
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)
        getLastLocation(googleMap)
        computeDistance(googleMap)
    }

    private fun getLastLocation(googleMap: GoogleMap?) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location : Location ->
//                Toast.makeText(this, location.toString(),Toast.LENGTH_LONG).show()
                if (googleMap != null) {
                    var currentLatLng:LatLng = LatLng(location.latitude, location.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM.toFloat()))
                }
            }
    }
    private fun computeDistance(googleMap: GoogleMap?)
    {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location : Location ->
//                Toast.makeText(this, location.toString(),Toast.LENGTH_LONG).show()
                if (googleMap != null) {
                    var currentLatLng:LatLng = LatLng(location.latitude, location.longitude)
                    distance = SphericalUtil.computeDistanceBetween(currentLatLng, LatLng(10.855104, 106.785955))
                    googleMap.addMarker(MarkerOptions().position(LatLng(10.855104, 106.785955)).title("Hutech"))
                    Toast.makeText(this, distance.toString(), Toast.LENGTH_SHORT).show()
                    updateView(currentLatLng, distance)
                }
            }
    }
    private  fun updateView(currentLatLng: LatLng, distance: Double)
    {
        txtCurrentLocation.text = "Your location: ${currentLatLng.latitude}, ${currentLatLng.longitude}"
        if (distance <100){
            txtDistance.text = BigDecimal(distance).setScale(1, RoundingMode.HALF_EVEN).toString()+"m"
            txtDistance.setTextColor(getColor(R.color.valid))
            cirNextStep.isEnabled =true
            cirNextStep.background = getDrawable(R.drawable.location_button_nextstep_ena)
        }else if(distance < 9999){
            txtDistance.text = BigDecimal(distance/1000).setScale(1, RoundingMode.HALF_EVEN).toString()+"km"
            txtDistance.setTextColor(getColor(R.color.invalid))
            Toast.makeText(this, "You are outside the checkin region", Toast.LENGTH_LONG).show()
        }else{
            txtDistance.text =getString(R.string.infinity)
            txtDistance.setTextColor(getColor(R.color.invalid))
            Toast.makeText(this, "You are outside the checkin region", Toast.LENGTH_LONG).show()
        }
    }
    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */

    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            requestPermission(
                this, LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION, true
            )
        }
        // [END maps_check_location_permission]
    }
    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
        computeDistance(map)

        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG).show()
    }

    // [START maps_check_location_permission_result]
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        if (isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation()
        } else {
            // Permission was denied. Display an error message
            // [START_EXCLUDE]
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
        }
    }

    // [END maps_check_location_permission_result]
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private fun showMissingPermissionError() {
        newInstance(true).show(supportFragmentManager, "dialog")
    }

    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val DEFAULT_ZOOM = 17
    }

}

