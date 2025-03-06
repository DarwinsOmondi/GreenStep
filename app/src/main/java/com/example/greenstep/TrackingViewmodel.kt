package com.example.greenstep

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow


class TrackingViewModel(context: Context) : ViewModel(), SensorEventListener {
    private val appContext = context.applicationContext // Prevent memory leaks
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)
    private val sensorManager = appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private var lastLocation: Location? = null
    private var _distanceCovered = MutableStateFlow(0f)
    val distanceCovered: MutableStateFlow<Float> = _distanceCovered

    private val _stepCount = MutableStateFlow(0)
    val stepCount: MutableStateFlow<Int> = _stepCount

    init {
        TrackDistanceCovered()
        TrackSteps()
    }

    fun TrackDistanceCovered() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(
                appContext, // Use application context here
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest = LocationRequest.Builder(5000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

            val locationCallBack = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        lastLocation?.let {
                            val distance = it.distanceTo(location)
                            _distanceCovered.value += distance
                        }
                        lastLocation = location
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallBack, null)
        } else {
            // Log or handle the case where permission is not granted
        }
    }

    fun TrackSteps() {
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            _stepCount.value = event.values[0].toInt()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}
