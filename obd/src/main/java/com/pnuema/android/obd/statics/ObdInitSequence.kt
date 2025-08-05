package com.pnuema.android.obd.statics

import android.bluetooth.BluetoothSocket
import android.util.Log
import com.pnuema.android.obd.commands.OBDCommand
import com.pnuema.android.obd.enums.ObdModes
import com.pnuema.android.obd.enums.ObdProtocols
import com.pnuema.android.obd.models.PID
import com.pnuema.android.obd.statics.ObdLibrary.TAG
import com.pnuema.android.obd.statics.PIDUtils.getPid
import com.pnuema.android.obd.statics.PersistentStorage.clearAll
import java.io.IOException

@Suppress("unused")
class ObdInitSequence {
    companion object {
        private const val ECU_RESPONSE_TIMEOUT = 25
        private const val MODE_AT = "AT"

        /**
         * Will run the connection sequence to setup the connection with the ELM327 device
         * given a connected bluetooth socket with the device
         * @param socket Bluetooth socket that is already connected to the ELM327 device
         * @return True if connection sequence succeeded and the test PID was retrieved successfully
         * from the device, false otherwise.
         */
        fun run(socket: BluetoothSocket, printDebug: bool = false): Boolean {
            var connected: Boolean
            try {
                if (printDebug) {
                    Log.d(TAG, "Socket connected")
                }
                clearAll()
                var initPid: PID = getPid(ObdModes.MODE_01, "00") ?: return false
                val inputStream = socket.inputStream
                val outputStream = socket.outputStream

                //set defaults
                initPid.mode = MODE_AT
                initPid.PID = "D"
                var cmd = OBDCommand(initPid).setIgnoreResult(true).run(inputStream, outputStream)
                printIfDebug("Set defaults sent (" + initPid.mode + " " + initPid.PID + ") Received: " + cmd.rawResult, printDebug)

                //resets the ELM327
                initPid.mode = MODE_AT
                initPid.PID = "Z"
                cmd = OBDCommand(initPid).setIgnoreResult(true).run(inputStream, outputStream)
                printIfDebug("Reset command sent (" + initPid.mode + " " + initPid.PID + ") Received: " + cmd.rawResult, printDebug)

                //extended responses off
                initPid.mode = MODE_AT
                initPid.PID = "E0"
                cmd = OBDCommand(initPid).setIgnoreResult(true).run(inputStream, outputStream)
                printIfDebug("Extended Responses Off (" + initPid.mode + " " + initPid.PID + ") Received: " + cmd.rawResult, printDebug)

                //line feeds off
                initPid.mode = MODE_AT
                initPid.PID = "L0"
                cmd = OBDCommand(initPid).setIgnoreResult(true).run(inputStream, outputStream)
                printIfDebug("Turn Off Line Feeds (" + initPid.mode + " " + initPid.PID + ") Received: " + cmd.rawResult, printDebug)

                //printing of spaces off
                initPid.mode = MODE_AT
                initPid.PID = "S0"
                cmd = OBDCommand(initPid).setIgnoreResult(true).run(inputStream, outputStream)
                printIfDebug("Printing Spaces Off (" + initPid.mode + " " + initPid.PID + ") Received: " + cmd.rawResult, printDebug)

                //headers off
                initPid.mode = MODE_AT
                initPid.PID = "H0"
                cmd = OBDCommand(initPid).setIgnoreResult(true).run(inputStream, outputStream)
                printIfDebug("Headers Off (" + initPid.mode + " " + initPid.PID + ") Received: " + cmd.rawResult, printDebug)

                //set protocol
                initPid.mode = "$MODE_AT SP"
                initPid.PID = ObdProtocols.AUTO.value.toString()
                cmd = OBDCommand(initPid).setIgnoreResult(true).run(inputStream, outputStream)
                printIfDebug("Select Protocol (" + initPid.mode + " " + initPid.PID + ") Received: " + cmd.rawResult, printDebug)

                //set timeout for response from the ECU
                initPid.mode = "$MODE_AT ST"
                initPid.PID = Integer.toHexString(0xFF and ECU_RESPONSE_TIMEOUT)
                cmd = OBDCommand(initPid).setIgnoreResult(true).run(inputStream, outputStream)
                printIfDebug("Set timeout (" + initPid.mode + " " + initPid.PID + ") Received: " + cmd.rawResult, printDebug)

                if (socket.isConnected) {
                    initPid = getPid(ObdModes.MODE_01, "00") ?: return false
                    printIfDebug(
                        "Mode 1 PID 00: " + OBDCommand(initPid).run(
                            inputStream,
                            outputStream
                        ).formattedResult,
                        printDebug
                    )
                    connected = initPid.calculatedResultString != null
                } else {
                    Log.e(TAG, "Bluetooth socket disconnected during connection init")
                    connected = false
                }
            } catch (e: IOException) {
                Log.e(TAG, "IOException on init commands: " + e.message)
                connected = false
            } catch (e: InterruptedException) {
                Log.e(TAG, "InterruptedException on init commands: " + e.message)
                connected = false
            }
            return connected
        }

        private fun printIfDebug(message: String, shouldPrint: bool) {
            if (shouldPrint) {
                Log.d(TAG, message)
            }
        }
    }
}