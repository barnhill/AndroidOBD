package com.pnuema.android.obd.statics

import com.pnuema.android.obd.R
import com.pnuema.android.obd.models.PID
import java.util.*

/**
 * This class provides translations for pids that are based on enumeration or other translation
 * methods other than just conversion using a simple formula.
 *
 * @author Brad Barnhill
 */
object Translations {
    private const val bitsInOneCharValue = 4
    private const val HEX_RADIX = 16

    fun handleSpecialPidEnumerations(pid: PID, buffer: ArrayList<Int>): Boolean {
        if (pid.mode == "01" && pid.PID == "00") {
            mode1Pid00Translation(pid, buffer)
            return true
        } else if (pid.mode == "01" && pid.PID == "01") {
            //dtc count
            mode1Pid01Translation(pid, buffer)
            return true
        } else if (pid.mode == "01" && pid.PID == "51") {
            //fuel type
            mode1Pid51Translation(pid, buffer)
            return true
        }

        return false
    }

    /**
     * Not finished yet
     */
    private fun mode1Pid00Translation(pid: PID, buffer: ArrayList<Int>) {
        if (buffer.size != 6) {
            return
        }

        val hexString = StringBuilder()
        val subList = buffer.subList(2, buffer.size)
        var temp: String
        for (i in subList.indices) {
            temp = Integer.toHexString(subList[i])
            hexString.append(if (temp.length == 1) "0" else "").append(temp)
        }

        pid.calculatedResult = java.lang.Long.parseLong(hexString.toString(), HEX_RADIX).toFloat()
        pid.calculatedResultString = hexString.toString()
    }

    private fun mode1Pid01Translation(pid: PID, buffer: ArrayList<Int>) {
        if (buffer.isNotEmpty() && buffer.size > 2) {
            // ignore first two bytes [hh hh] of the response
            val mil = buffer[2]
            val on = ObdContext.getResourceString(R.string.mode1_pid01_translation_on)
            val off = ObdContext.getResourceString(R.string.mode1_pid01_translation_off)
            val onOff = if (mil and 0x80 == 128) on else off
            pid.calculatedResultString = String.format(ObdContext.getResourceString(R.string.mode1_pid01_translation), onOff, mil and 0x7F)
        }
    }

    private fun mode1Pid51Translation(pid: PID, buffer: ArrayList<Int>) {
        val pidTranslation = ObdContext.getResourceStringArray(R.array.mode1_pid51_translation)
        if (buffer.isNotEmpty() && buffer.size > 2 && buffer[2] < pidTranslation.size) {
            pid.calculatedResultString = pidTranslation[buffer[2]]
        } else {
            pid.calculatedResultString = ObdContext.getResourceString(R.string.pid_value_unavailable)
        }
    }

    /**
     * Convert the given hexadecimal number to bits
     *
     * @param hex The [String] representation of the hexadecimal
     * @return [BitSet] containing the bits of the hexadecimal
     */
    private fun hexToBitSet(hex: String): BitSet {
        val hexBitSize = hex.length * bitsInOneCharValue
        val hexBitSet = BitSet(hexBitSize)

        for (i in hex.indices) {
            val hexChar = hex[i]
            val charBitSet = hexCharToBitSet(hexChar)

            for (j in 0 until bitsInOneCharValue) {
                if (charBitSet.get(j)) {
                    hexBitSet.set(j + (hex.length - i - 1) * bitsInOneCharValue)
                }
            }
        }

        return hexBitSet
    }

    /**
     * Convert the given hexadecimal character to its bits. Note: valid inputs
     * are 0-F
     *
     * @param hexChar the hexadecimal character to convert
     * @return BitSet containing the bits of the hexadecimal character
     */
    private fun hexCharToBitSet(hexChar: Char): BitSet {
        val charBitSet = BitSet(bitsInOneCharValue)
        val hex = Integer.parseInt(hexChar.toString(), HEX_RADIX)

        for (i in 0 until bitsInOneCharValue) {
            val bit = Integer.lowestOneBit(hex shr i)
            if (bit == 1)
                charBitSet.set(i)
        }

        return charBitSet
    }

    /**
     * Gets if a pid is available or not based on the value of a bitset
     *
     * @param pid PID to check if available to query
     * @param pidValue parent pid value (hex) which denotes which pids are available
     * @return True if PID is available, false otherwise.
     */
    fun isPidAvailable(pid: PID, pidValue: String): Boolean {
        //each parent pid governs 32 pids
        val pidIndex = Integer.parseInt(pid.PID, HEX_RADIX) % 32 - 1

        return hexToBitSet(pidValue).get(pidIndex)
    }
}
