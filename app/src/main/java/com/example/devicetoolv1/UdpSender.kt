package com.example.devicetoolv1

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

object UdpSender {

    // YOUR CURRENT LAPTOP WIFI IP
    private const val SERVER_IP = "10.211.251.92"

    // RECEIVER PORT
    private const val SERVER_PORT = 5005

    suspend fun sendChannels(
        ch1: Int,
        ch2: Int,
        ch3: Int,
        ch4: Int
    ) {

        withContext(Dispatchers.IO) {

            var socket: DatagramSocket? = null

            try {

                val message = """
                    CH1:$ch1
                    CH2:$ch2
                    CH3:$ch3
                    CH4:$ch4
                """.trimIndent()

                val bytes = message.toByteArray()

                val address = InetAddress.getByName(SERVER_IP)

                val packet = DatagramPacket(
                    bytes,
                    bytes.size,
                    address,
                    SERVER_PORT
                )

                socket = DatagramSocket()

                socket.send(packet)

                Log.d("UDP_TEST", "Sent:\n$message")

            } catch (e: Exception) {

                Log.e(
                    "UDP_TEST",
                    "UDP Send Failed: ${e.message}"
                )

                e.printStackTrace()

            } finally {

                socket?.close()
            }
        }
    }
}