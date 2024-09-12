package com.example.myfirstapp.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import com.example.myfirstapp.activity.RLMainActivityRL
import com.google.android.material.snackbar.Snackbar

class RlNetworkChangeReceiver(private val rootView: View) : BroadcastReceiver() {

    private var isConnected = false

    override fun onReceive(context: Context, intent: Intent?) {
        val hasInternet = isInternetAvailable(context)

        if (hasInternet && !isConnected) {
            isConnected = true
            showSnackbar("Internet is reconnected", true)
        } else if (!hasInternet && isConnected) {
            isConnected = false
            showSnackbar("Internet is disconnected", false)
        }
    }

    private fun showSnackbar(message: String, isConnected: Boolean) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).apply {
            if (isConnected) {
                setBackgroundTint(rootView.context.getColor(android.R.color.holo_green_light))
            } else {
                setBackgroundTint(rootView.context.getColor(android.R.color.holo_red_light))
            }
            show()
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }
}
