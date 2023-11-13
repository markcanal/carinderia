package com.example.carinderia.data.repository

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepository @Inject constructor(private val connectivityManager: ConnectivityManager) {
    private val NetworkCapabilities.hasValidatedInternet: Boolean
        get() {
            val hasInternet = hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val isValidated = hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            return hasInternet && isValidated
        }

    private val NetworkCapabilities.isConnected: Boolean
        get() {
            val isCellular = hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            val isWiFi = hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            return isCellular || isWiFi
        }

    val isConnected: Boolean
        get() {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false
            return capabilities.hasValidatedInternet && capabilities.isConnected
        }

    val flow = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                val isAvailable = capabilities.hasValidatedInternet && capabilities.isConnected
                if (isAvailable) Timber.tag(TAG).d("Network connection has internet access")
                else Timber.tag(TAG).d("Network connection has no internet access")
                trySend(isAvailable)
            }

            override fun onUnavailable() {
                Timber.tag(TAG).d("Network connection unavailable")
                trySend(false)
            }

            override fun onLost(network: Network) {
                Timber.tag(TAG).d("Network connection lost")
                trySend(false)
            }
        }
        Timber.tag(TAG).d("Registering default network callback")
        connectivityManager.registerDefaultNetworkCallback(callback)
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }

    private companion object {
        const val TAG = "NetworkRepository"
    }
}