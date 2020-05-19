package com.mindorks.ridesharing.ui.maps

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.mindorks.ridesharing.R
import com.mindorks.ridesharing.data.network.NetworkService
import com.mindorks.ridesharing.utils.PermissonUtils
import com.mindorks.ridesharing.utils.ViewUtils


class MapsActivity : AppCompatActivity(),MapsView, OnMapReadyCallback {
    companion object {
        private  const val TAG="MapsActvty"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
        private const val PICKUP_REQUEST_CODE = 1
        private const val DROP_REQUEST_CODE = 2

    }
    private lateinit var mMap:GoogleMap
    private  lateinit var mapspresenter: MapsPresenter
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var locationCallback: LocationCallback
   private var currentLatLng: LatLng? = null
    private var pickUpLatLng: LatLng? = null
    private var dropLatLng: LatLng? = null
    /*private val nearbyCabMarkerList = arrayListOf<Marker>()
    private var destinationMarker: Marker? = null
    private var originMarker: Marker? = null
    private var greyPolyLine: Polyline? = null
    private var blackPolyline: Polyline? = null
    private var previousLatLngFromServer: LatLng? = null
    private var currentLatLngFromServer: LatLng? = null
    private var movingCabMarker: Marker? = null*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        ViewUtils.enableTransparentStatusBar(window)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapspresenter= MapsPresenter(NetworkService())
        mapspresenter.onAttach(this)
        //setUpClickListener() setUpLocationListener()
        val result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)
        Log.e("Maps", "Result int value::$result")
        when (result) {
            ConnectionResult.SUCCESS -> Log.e("Maps", "RESULT:: SUCCESS")
            ConnectionResult.DEVELOPER_ERROR -> Log.e("Maps", "RESULT:: DE")
            ConnectionResult.INTERNAL_ERROR -> Log.e("Maps", "RESULT:: IE")
            ConnectionResult.INVALID_ACCOUNT -> Log.e("Maps", "RESULT:: IA")
            ConnectionResult.NETWORK_ERROR -> Log.e("Maps", "RESULT:: NE")
            ConnectionResult.RESOLUTION_REQUIRED -> Log.e("Maps", "RESULT:: RR")
            ConnectionResult.SERVICE_DISABLED -> Log.e("Maps", "RESULT:: SD")
            ConnectionResult.SERVICE_INVALID -> Log.e("Maps", "RESULT:: SI")
            ConnectionResult.SERVICE_MISSING -> Log.e("Maps", "RESULT:: SM")
            ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED -> Log.e(
                "Maps",
                "RESULT:: SVUR"
            )
            ConnectionResult.SIGN_IN_REQUIRED -> Log.e("Maps", "RESULT:: SIR")
            else -> {
            }
        }
    }
    override fun onStart() {
        super.onStart()
            when {
                PermissonUtils.isAccessFineLocationGranted(this) -> {
                    when {
                        PermissonUtils.isLocationEnabled(this) -> {
                            //setUpLocationListener()
                            setUpLocationListener()
                        }
                        else -> {
                            PermissonUtils.showGPSNotEnabledDialog(this)
                        }
                    }
                }
                else -> {
                    PermissonUtils.reqAccessFineLocPermission(
                        this,
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            }
        }
    private fun setUpLocationListener() {
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        // for getting the current location update after every 2 seconds
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                if (currentLatLng == null) {
                    for (location in locationResult.locations) {
                        if (currentLatLng == null) {
                            currentLatLng = LatLng(location.latitude, location.longitude)
                           // setCurrentLocationAsPickUp()
                            enableMyLocationOnMap()
                            moveCamera(currentLatLng)
                            animateCamera(currentLatLng)
                            //presenter.requestNearbyCabs(currentLatLng!!)
                            Log.d("LOCRESULT", "clicked")

                        }
                    }
                }
                // Few more things we can do here:
                // For example: Update the location of user on server
            }
        }
        fusedLocationProviderClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private fun enableMyLocationOnMap() {
        mMap.setPadding(0, ViewUtils.dpToPx(48f), 0, 0)
        mMap.isMyLocationEnabled = true
        Log.d("enable", "clicked")
    }

    private fun moveCamera(latLng: LatLng?) {
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        Log.d("move", "clicked")
    }
    private fun animateCamera(latLng: LatLng?) {
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(15.5f).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        Log.d("anmate", "clicked")

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap=googleMap
        Log.d("mapready", "clicked")


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            LOCATION_PERMISSION_REQUEST_CODE->{
                if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    when{
                        PermissonUtils.isLocationEnabled(this)->{
                            Log.d("LOCSETUP", "clicked")
                            setUpLocationListener()


                        }
                        else->{
                            PermissonUtils.showGPSNotEnabledDialog(this)
                        }
                    }
                }else{
                    Toast.makeText(this,"Locatn permsssnn not granted", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    override fun onDestroy() {
        mapspresenter.onDetach()
        //fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

}

