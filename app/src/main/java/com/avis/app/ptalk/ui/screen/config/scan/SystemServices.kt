package com.avis.app.ptalk.ui.screen.config.scan

import android.bluetooth.BluetoothManager
import android.content.Context
import android.location.LocationManager

/**
 * Lightweight read of Bluetooth + Location service state.
 * Returns (bluetoothEnabled, locationEnabled).
 */
internal fun checkSystemServices(context: Context): Pair<Boolean, Boolean> {
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    val btEnabled = bluetoothManager?.adapter?.isEnabled == true
    val locEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
        locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
    return Pair(btEnabled, locEnabled)
}
