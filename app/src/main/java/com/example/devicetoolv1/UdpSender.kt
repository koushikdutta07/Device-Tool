package com.example.devicetoolv1

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import android.util.Log

object UdpSender {

    suspend fun sendChannels(
        ch1: Int,
        ch2: Int,
        ch3: Int,
        ch4: Int
    ) {

        withContext(Dispatchers.IO) {

            try {

                val message = """
                    CH1:$ch1
                    CH2:$ch2
                    CH3:$ch3
                    CH4:$ch4
                """.trimIndent()

                val bytes = message.toByteArray()

                val ipAddress = InetAddress.getByName("192.168.0.116")

                val packet = DatagramPacket(
                    bytes,
                    bytes.size,
                    ipAddress,
                    5005
                )

                val socket = DatagramSocket()

                Log.d("UDP_TEST", message)

                socket.send(packet)

                socket.close()

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }
}