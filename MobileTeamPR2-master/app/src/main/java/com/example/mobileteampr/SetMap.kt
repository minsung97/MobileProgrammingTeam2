package com.example.mobileteampr

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.mobileteampr.databinding.ActivityMainBinding
import com.example.mobileteampr.databinding.ActivitySetMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.firebase.database.DatabaseReference

class SetMap : AppCompatActivity() {
    lateinit var binding: ActivitySetMapBinding
    lateinit var googleMap: GoogleMap
    var loc = LatLng(37.543627, 127.077364)
    var loc1 = LatLng(37.5398, 127.0710)
    var loc2 = LatLng(37.5354, 127.0944)
    var loc3 = LatLng(37.5460, 127.0751)
    var loc4 = LatLng(37.5442, 127.0840)
    var destination = LatLng(37.543627, 127.077364)
    val arrLoc = ArrayList<LatLng>()

    lateinit var rdb: DatabaseReference
    lateinit var urdb : DatabaseReference
    lateinit var curId:String
    lateinit var curTitle:String
    lateinit var result_place: String
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    var startupdate = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initmap()
    }

    fun initlocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(location: LocationResult) {
                if(location.locations.size==0) return
                loc = LatLng(location.locations[location.locations.size-1].latitude,
                    location.locations[location.locations.size-1].longitude)
                setCurrentLocation(loc)
                Log.i("location","LocationCallback()")
            }
        }
    }
    fun getLastLocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), 100)
        }else{
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                if (it != null){
                    loc = LatLng(it.latitude, it.longitude)
                    setCurrentLocation(loc)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                startLocationUpdates()
            }else{
                Toast.makeText(this, "위치정보를 제공하셔야 합니다.", Toast.LENGTH_SHORT).show()
                setCurrentLocation(loc)
            }
        }
    }

    fun startLocationUpdates(){
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }else{
            if (!checkLocationServicesStatus()){
                showLocationServicesSetting()
            }else {
                startupdate = true
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper()
                )
                Log.i("location", "startLocationUpdates()")
            }
        }
    }

    fun checkLocationServicesStatus(): Boolean{
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
    }

    fun showLocationServicesSetting(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 허용하겠습니까?")
        builder.setPositiveButton("설정", DialogInterface.OnClickListener{ dialog, id -> val GpsSettingIntent = Intent(
            Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(GpsSettingIntent, 1000)
        })
        builder.setNegativeButton("취소",
            DialogInterface.OnClickListener { dialog, id -> dialog.dismiss()
                setCurrentLocation(loc)
            })
        builder.create().show()
    }

    fun setCurrentLocation(location:LatLng){
        val option = MarkerOptions()
        option.position(destination)
        option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        googleMap.addMarker(option)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 14.0f))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1000-> {
                if(checkLocationServicesStatus()){
                    Toast.makeText(this,"GPS 활성화 되었음", Toast.LENGTH_SHORT).show()
                    startLocationUpdates()
                }
            }
        }
    }
    private fun stopLocationUpdate(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        startupdate=false
        Log.i("location", "stopLocationUpdate()")
    }

    override fun onResume() {
        super.onResume()
        Log.i("location", "onResume()")
        if(!startupdate)
            startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        Log.i("location", "onPause()")
        stopLocationUpdate()
    }


    private fun initmap(){
        initlocation()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync{
            googleMap = it
            initMapListener()
        }
    }
    private fun initMapListener(){

        googleMap.clear()
        val option = MarkerOptions()
        option.position(destination)
        option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        option.title("도착지")
        option.snippet("건국대학교 새천년관")
        googleMap.addMarker(option)

        val user1 = MarkerOptions()
        user1.position(loc1)
        user1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        user1.title("chbae")
        //user1.snippet("사용자1정보")
        googleMap.addMarker(user1)

        val user2 = MarkerOptions()
        user2.position(loc2)
        user2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        user2.title("lim")
        // user2.snippet("사용자2정보")
        googleMap.addMarker(user2)

        val user3 = MarkerOptions()
        user3.position(loc3)
        user3.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        user3.title("mskim")
        //user3.snippet("사용자3정보")
        googleMap.addMarker(user3)

        val user4 = MarkerOptions()
        user4.position(loc4)
        user4.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        user4.title("park")
        //user4.snippet("사용자4정보")
        googleMap.addMarker(user4)

    }
}