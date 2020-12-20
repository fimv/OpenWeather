package com.example.openweather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GpsActivity : AppCompatActivity() {
    private var tvGpsLocation: TextView? = null
    private var weatherGpsLocation: TextView? = null

    private var locationHelper: GPSHelper? = null
    private var locationCallback: LocationCallback? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gps_activity)

        locationHelper = GPSHelper(this)

        tvGpsLocation = findViewById(R.id.textView)
        weatherGpsLocation = findViewById(R.id.weather_textView)

      //   val gmsbutton: Button = findViewById(R.id.getLocation)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE), 2)
        } else {
            locationHelper?.getLocation {
                tvGpsLocation?.text = it
            }
        }

        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationHelper?.locationRequest == null) {
                    tvGpsLocation?.text = "locationRequest == null"
                    return
                }

                val location = locationResult?.lastLocation

                tvGpsLocation?.text = "Latitude: ${location?.latitude} Longitude: ${location?.longitude}"
                val httpState = "lat=${location?.latitude}&lon=${location?.longitude}"
                request(httpState)

                if (locationResult != null) {
                    for (loc in locationResult.locations) {
                        tvGpsLocation?.text = "Latitude: ${loc.latitude} Longitude: ${loc.longitude}"
                        val httpState = "lat=${location?.latitude}&lon=${location?.longitude}"
                        request(httpState)
                    }
                }
            }
        }


    }

    override fun onResume() {
        super.onResume()
        locationHelper?.startLocationUpdates(locationCallback)
    }

    override fun onPause() {
        super.onPause()
        locationHelper?.stopLocationUpdates(locationCallback)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            2 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                    locationHelper?.getLocation {
                        tvGpsLocation?.text = it
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
            }
        }
    }



    fun request(httpState: String) {
        var weather: WeatherRequest? = null
        weather = WeatherRequest()

        //запускаем корутину
        GlobalScope.launch {
            //получаем результат выполнения функции makeRequest()
            val result = weather.makeRequest(httpState)
            val  jsonStr = weather.jsonElement(result)
            //при помощи метода withContext в который передаем диспетчер основного потока Dispatchers.Main
            //передаем полученные данные в наш Text View
            withContext(Dispatchers.Main) {

                weatherGpsLocation?.text = jsonStr
            }
        }
    }




}