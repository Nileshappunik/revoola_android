package com.revoola.moengage.push

import android.util.Log
import com.moengage.geofence.listener.OnGeofenceHitListener
import com.moengage.geofence.model.GeofenceData

class RLGeofenceHitListener: OnGeofenceHitListener {
    override fun geofenceHit(geofenceData: GeofenceData): Boolean {
        Log.e("RLGeofenceHitListener","geofenceHit() Geofence hit callback received. Callback data: $geofenceData")
        // process the intent.
        // return true if the app does not want the SDK to process the Geo-fence callback else false
        return false
    }
}