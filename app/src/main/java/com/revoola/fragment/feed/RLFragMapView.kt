package com.revoola.fragment.feed


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.revoola.R
import com.revoola.RLBaseFragment
import com.revoola.commonobject.RLTools
import com.revoola.databinding.RlDialogMapBinding
import com.revoola.fragment.start.challenges.RLFragChallengesFor
import com.revoola.fragment.start.challenges.model.RLEditChallengeAllData
import com.revoola.utils.RLPrefManager
import org.json.JSONObject

class RLFragMapView : RLBaseFragment() , OnMapReadyCallback {
    val TAG: String = RLFragMapView::class.java.simpleName
    private lateinit var mMap: GoogleMap
    private var mapFragment: SupportMapFragment? = null
    private var jsonDataString: String = ""

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragMapView()
        fragment.arguments = bundle
        return fragment
    }

    private val fragBinding by lazy {
        RlDialogMapBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragMapView" )
       // RLuisetup()
        return fragBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        jsonDataString = requireArguments().getString("jsonDataString").toString()
        // Set up back button click listener
       // val ivBack = view.findViewById<ImageView>(R.id.ivBack)
        RLonBackPresAct(fragBinding.ivBack)

        // Add a small delay to ensure the view is fully inflated
        view.post {
            mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
            mapFragment?.getMapAsync(this)
        }
    }


//    private fun RLuisetup() {
//        RLonBackPresAct(fragBinding.ivBack)
//        jsonDataString = requireArguments().getString("jsonDataString").toString()
//        view?.post {
//            mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
//            mapFragment?.getMapAsync(this)
//        }
//    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        RLTools.RlLogEPrint("RLMapFragment", "Map ready, starting initialization...")

        try {
            // Test if map is actually working with a simple marker first
//            val testLocation = LatLng(37.7749, -122.4194) // San Francisco
//            mMap.addMarker(
//                com.google.android.gms.maps.model.MarkerOptions()
//                    .position(testLocation)
//                    .title("Test Marker")
//            )
          //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(testLocation, 10f))

            // Configure map settings
            mMap.uiSettings.apply {
                isZoomControlsEnabled = true  // Enable for debugging
                isMapToolbarEnabled = false
                isMyLocationButtonEnabled = false
                isCompassEnabled = false
                isRotateGesturesEnabled = true
                isScrollGesturesEnabled = true
                isZoomGesturesEnabled = true
                isTiltGesturesEnabled = false
            }

            // Set map type to ensure visibility
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            // Log map state
            RLTools.RlLogEPrint("RLMapFragment", "Map type: ${mMap.mapType}")
            RLTools.RlLogEPrint("RLMapFragment", "Map camera position: ${mMap.cameraPosition}")

            // Set map style (optional)
            mMap.setMapStyle(null)

            // Add a delay before initializing data to ensure map is fully loaded
            view?.postDelayed({
                initializeMap()
            }, 1000) // 1 second delay

        } catch (e: Exception) {
            RLTools.RlLogEPrint("RLMapFragment", "Error in onMapReady: $e")
        }
    }

    private fun initializeMap() {
        if (!::mMap.isInitialized || jsonDataString.isEmpty()) {
            RLTools.RlLogEPrint("RLMapFragment", "Map not ready or no location data")
            return
        }

        try {
            val bounds = LatLngBounds.Builder()

            val jsonObject = JSONObject(jsonDataString)
            val locationDicRaw = jsonObject.optString("locationDic")

            // Clean the string to make it a valid JSON-like array of objects
            val cleaned = locationDicRaw
                .replace("\\\"", "")
                .replace("\"\"", "\"")
                .replace("[[", "[")
                .replace("]]", "]")
                .replace("], [", "]|[")
                .removeSurrounding("\"")

            val locationArray = cleaned.split("|")
            val pathPoints = mutableListOf<LatLng>()
            val colors = mutableListOf<Int>()

            locationArray.forEachIndexed { index, item ->
                try {
                    val fixedItem = item.replace("[", "{").replace("]", "}")
                    val jsonItem = JSONObject(fixedItem)

                    val lat = jsonItem.getDouble("lat")
                    val lng = jsonItem.getDouble("long")
                    val state = jsonItem.getInt("state")

                    val point = LatLng(lat, lng)
                    pathPoints.add(point)
                    bounds.include(point)
                    colors.add(getStateColor(state))

                } catch (e: Exception) {
                    RLTools.RlLogEPrint("RLMapFragment", "Parse error at $index: $e")
                }
            }

            // Draw polylines
            if (pathPoints.isNotEmpty() && colors.isNotEmpty()) {
                var currentColor = colors[0]
                var currentSegment = mutableListOf<LatLng>()
                currentSegment.add(pathPoints[0])

                for (i in 1 until pathPoints.size) {
                    if (colors[i] == currentColor) {
                        currentSegment.add(pathPoints[i])
                    } else {
                        if (currentSegment.size > 1) {
                            val polylineOptions = PolylineOptions()
                                .addAll(currentSegment)
                                .color(currentColor)
                                .width(10f)
                                .geodesic(true)
                            mMap.addPolyline(polylineOptions)
                        }

                        currentSegment = mutableListOf(pathPoints[i - 1], pathPoints[i])
                        currentColor = colors[i]
                    }
                }

                // Add last segment
                if (currentSegment.size > 1) {
                    val polylineOptions = PolylineOptions()
                        .addAll(currentSegment)
                        .color(currentColor)
                        .width(10f)
                        .geodesic(true)
                    mMap.addPolyline(polylineOptions)
                }

                // Move camera with animation and add callback
                val boundsToFit = bounds.build()
                try {
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(boundsToFit, 100),
                        2000, // 2 seconds animation
                        object : GoogleMap.CancelableCallback {
                            override fun onFinish() {
                                RLTools.RlLogEPrint("RLMapFragment", "Camera animation finished")
                            }
                            override fun onCancel() {
                                RLTools.RlLogEPrint("RLMapFragment", "Camera animation cancelled")
                            }
                        }
                    )
                } catch (e: Exception) {
                    // Fallback to immediate camera move
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsToFit, 100))
                }

                RLTools.RlLogEPrint("RLMapFragment", "Map initialized with ${pathPoints.size} points")
            } else {
                RLTools.RlLogEPrint("RLMapFragment", "pathPoints and colors are empty")
            }

        } catch (e: Exception) {
            RLTools.RlLogEPrint("RLMapFragment", "Error initializing map: $e")
        }
    }

    private fun getStateColor(state: Int): Int {
        return when (state) {
            0 -> Color.parseColor("#F177A0")
            1 -> Color.parseColor("#FFCF2F")
            2 -> Color.parseColor("#2CAE2C")
            3 -> Color.parseColor("#0099DA")
            4 -> Color.parseColor("#FE6902")
            5 -> Color.parseColor("#9900CC")
            6 -> Color.parseColor("#ED4541")
            7 -> Color.parseColor("#ED4541")
            else -> Color.parseColor("#F177A0")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mapFragment = null
    }


}