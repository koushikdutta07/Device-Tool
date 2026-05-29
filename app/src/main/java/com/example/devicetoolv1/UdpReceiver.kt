package com.example.devicetoolv1

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket

object UdpReceiver {

    private const val TELEMETRY_PORT = 6006
    private const val TAG = "UDP_RECEIVER"

    suspend fun startListening(
        onTelemetryReceived: (String) -> Unit
    ) {
        // Outer retry loop — if socket dies for any reason, restart after 2 seconds
        while (true) {

            var socket: DatagramSocket? = null

            try {

                withContext(Dispatchers.IO) {

                    socket = DatagramSocket(TELEMETRY_PORT)
                    socket!!.soTimeout = 0  // block forever until packet arrives

                    Log.d(TAG, "Socket bound. Listening on port $TELEMETRY_PORT")

                    while (true) {

                        val buffer = ByteArray(1024)
                        val packet = DatagramPacket(buffer, buffer.size)

                        socket!!.receive(packet)

                        // Trim null bytes that pad the fixed-size buffer
                        val message = String(packet.data, 0, packet.length).trim()

                        Log.d(TAG, "Received: '$message'")

                        // Switch to Main thread before updating Compose state
                        withContext(Dispatchers.Main) {
                            onTelemetryReceived(message)
                        }
                    }
                }

            } catch (e: Exception) {

                Log.e(TAG, "Socket error: ${e.message} — restarting in 2s")
                e.printStackTrace()

            } finally {

                socket?.close()
                Log.d(TAG, "Socket closed")
            }

            // Wait before retrying so we don't spin-loop on a hard error
            delay(2000)
        }
    }
}