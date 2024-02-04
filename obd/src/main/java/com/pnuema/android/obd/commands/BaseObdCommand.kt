/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
@file:Suppress("unused")

package com.pnuema.android.obd.commands

import com.pnuema.android.obd.models.PID
import com.pnuema.android.obd.statics.PersistentStorage
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import kotlin.system.measureTimeMillis

/**
 * Base OBD command class for communicating with an ELM327 device.
 */
abstract class BaseObdCommand {
    /**
     * @return a list of integers
     */
    protected var buffer: ArrayList<Int> = ArrayList()
    private var cmd: String? = null
    private var useImperialUnits = false
    private var rawData: String? = null
    internal var mIgnoreResult: Boolean = false

    companion object {
        const val NODATA = "NODATA"
        const val SEARCHING = "SEARCHING"
        const val DATA = "DATA"
        const val ELM327 = "ELM327"

        internal lateinit var mPid: PID

        private fun readPersistent() {
            if (mPid.isPersistent && PersistentStorage.containsPid(mPid)) {
                mPid.calculatedResult = PersistentStorage.getElement(mPid)?.calculatedResult ?: 0f
                mPid.calculatedResultString = PersistentStorage.getElement(mPid)?.calculatedResultString
                mPid.data = PersistentStorage.getElement(mPid)?.data ?: ArrayList()
            }
        }

        private fun storePersistent() {
            if (mPid.isPersistent && !PersistentStorage.containsPid(mPid)) {
                PersistentStorage.addElement(mPid)
            }
        }
    }

    /**
     * @return [String] representation of the formatted command response.
     */
    abstract val formattedResult: String

    /**
     * @return the raw command response in string representation.
     */
    val rawResult: String
        get() {
            rawData = if (rawData == null || rawData!!.contains(SEARCHING) || rawData!!.contains(DATA) || rawData!!.contains(ELM327))
                NODATA
            else
                rawData

            return rawData!!
        }

    /**
     * @return the OBD command name.
     */
    abstract val name: String

    @Suppress("RemoveEmptySecondaryConstructorBody")
    private constructor() {}

    /**
     * Default ctor to use
     *
     * @param command the command to send
     */
    private constructor(command: String?) {
        this.cmd = command
    }

    internal constructor(command: String, pid: PID) : this(command.trim { it <= ' ' }) {
        mPid = pid
    }

    /**
     * Copy constructor.
     *
     * @param other the ObdCommand to copy.
     */
    constructor(other: BaseObdCommand) : this(other.cmd)

    /**
     * This method exists so that for each command, there must be a method that is
     * called only once to perform calculations.
     */
    protected abstract fun performCalculations()

    /**
     * Sends the OBD-II request and deals with the response.
     *
     * This method CAN be overridden in fake commands.
     * @param inputStream [InputStream] to read the result of the requested PID.
     * @param out [OutputStream] on which to send the request.
     * @throws IOException thrown if IO is unable to be performed
     * @throws InterruptedException thrown if the process is interrupted
     * @return Current instance of [BaseObdCommand]
     */
    @Throws(IOException::class, InterruptedException::class)
    fun run(inputStream: InputStream, out: OutputStream): BaseObdCommand {
        synchronized(BaseObdCommand::class.java) {
            mPid.retrievalTime = measureTimeMillis {
                if (mPid.isPersistent && PersistentStorage.containsPid(mPid)) {
                    readPersistent()
                } else {
                    sendCommand(out)
                    readResult(inputStream)
                }
            }
        }
        return this
    }

    /**
     * Sends the OBD-II request.
     *
     * This method may be overridden in subclasses
     *
     * @param out The output stream.
     */
    @Throws(IOException::class, InterruptedException::class)
    private fun sendCommand(out: OutputStream) {
        // write to OutputStream (i.e.: a BluetoothSocket) with an added carriage return
        out.write((cmd!! + "\r").toByteArray())
        out.flush()
    }

    /**
     * Reads the OBD-II response.
     *
     *
     * This method may be overridden in subclasses, such as ObdMultiCommand.
     */
    @Throws(IOException::class)
    private fun readResult(inputStream: InputStream) {
        readRawData(inputStream)

        if (!mIgnoreResult) {
            fillBuffer()
            performCalculations()
            storePersistent()
        }
    }

    /**
     * Fills the buffer from the raw data.
     */
    private fun fillBuffer() {
        // read string each two chars
        buffer.clear()
        rawData?.chunked(2)?.forEach {
            try {
                buffer.add(Integer.decode("0x$it"))
            } catch (e: NumberFormatException) { return }
        }
    }

    @Throws(IOException::class)
    private fun readRawData(inputStream: InputStream) {
        val reader = BufferedReader(InputStreamReader(inputStream))

        val res = StringBuilder()

        var c: Char
        var b = reader.read()
        while (b > -1) { // -1 if the end of the stream is reached
            c = b.toChar()

            if (c == '>') { // read until '>' arrives
                break
            }
            res.append(c)
            b = reader.read()
        }

       /*
        * Imagine the following response 41 0c 00 0d.
        *
        * ELM sends strings!! So, ELM puts spaces between each "byte". Pay
        * attention to the fact that I've put the word byte in quotes, because 41
        * is actually TWO bytes (two chars) in the socket. So, we must do some more
        * processing..
        */
        rawData = res.toString().trim { it <= ' ' }

       /*
        * data may have echo or informative text like "INIT BUS..." or similar.
        * The response ends with two carriage return characters. So we need to take
        * everything from the last carriage return before those two (trimmed above).
        */
        rawData = rawData?.let {
            it.substring(it.indexOf(13.toChar()) + 1)
        }
    }

    /**
     * Gets whether imperial or metric units should be shown
     * @return true if imperial units are used, or false otherwise
     */
    fun useImperialUnits(): Boolean = useImperialUnits

    /**
     * Sets whether to use imperial units or metric.
     *
     * @param isImperial Set to 'true' if you want to use imperial units, false otherwise. By
     * default this value is set to 'false'.
     */
    fun setImperialUnits(isImperial: Boolean) {
        this.useImperialUnits = isImperial
    }
}