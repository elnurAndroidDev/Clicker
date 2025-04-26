package com.isayevapps.clicker.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.isayevapps.clicker.data.network.ApiService
import com.isayevapps.clicker.data.network.Login
import com.isayevapps.clicker.data.network.Result
import com.isayevapps.clicker.data.network.safeApiCall
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import javax.inject.Inject
import javax.inject.Singleton

// 4. HostChecker.kt — вынесенный сервис
@Singleton
class NetworkScanner @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) {

    suspend fun findFirstHost(
        deviceName: String
    ): String? = withContext(Dispatchers.IO) {
        val prefix = getNetworkPrefix() ?: throw NoWifiException()

        for (i in 1..254) {
            val ip = "$prefix.$i"
            if (checkHost(ip, deviceName)) {
                return@withContext ip
            }
        }
        return@withContext null
    }


    suspend fun checkHost(ip: String, deviceName: String): Boolean {
        ensureWifi()
        val url = "http://$ip/$deviceName"
        Log.d("checkHost", url)
        val response = withContext(Dispatchers.IO) { safeApiCall { apiService.login(url) } }
        Log.d("checkHost", response.toString())
        return when (response) {
            is Result.Success -> true
            is Result.Error -> false
        }
    }

    private fun ensureWifi() {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as? ConnectivityManager
            ?: throw NoWifiException()
        val nw = cm.activeNetwork
            ?: throw NoWifiException()
        val caps = cm.getNetworkCapabilities(nw)
            ?: throw NoWifiException()
        if (!caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            throw NoWifiException()
        }
    }

    fun getNetworkPrefix(): String? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as? ConnectivityManager ?: return null
        val network = cm.activeNetwork ?: return null
        val caps = cm.getNetworkCapabilities(network) ?: return null
        if (!caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return null
        val linkProps = cm.getLinkProperties(network) ?: return null
        val ipv4 = linkProps.linkAddresses
            .asSequence()
            .map { it.address }
            .filterIsInstance<Inet4Address>()
            .firstOrNull()
            ?.hostAddress
            ?: return null
        return ipv4.split('.').take(3).joinToString(".")
    }
}
