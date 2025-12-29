package com.ykt.musicplayer.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class NetworkMonitor @Inject constructor(app: Application) {
    private val connectivityManager =
        app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val isConnected = MutableStateFlow(true)

    init {
        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                isConnected.value = false
            }
            override fun onAvailable(network: Network) {
                isConnected.value = true
            }
        })
    }

}