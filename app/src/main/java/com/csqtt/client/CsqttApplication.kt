package com.csqtt.client

import android.app.Application
import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
class CsqttApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DeployManager.init(this)

        // The QS tile and the widgets follow the tunnel from process start, not from MainActivity:
        // a tile or widget tap starts the process too, and the tile is refreshed only on request.
        TunnelManager.initObservers(this)

        val settingsStore = SettingsStore(this)
        CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
            try {
                settingsStore.loggingEnabled.collect { enabled ->
                    TunnelManager.isLoggingEnabled = enabled
                }
            } catch (e: Exception) {
                Log.e("CsqttApp", "Не удалось отслеживать флаг логирования: ${e.message}")
            }
        }
    }
}
