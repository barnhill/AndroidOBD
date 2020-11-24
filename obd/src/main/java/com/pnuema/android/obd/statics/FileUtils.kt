package com.pnuema.android.obd.statics

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Static definitions for all file related utilities.
 *
 * @author Brad Barnhill
 */
object FileUtils {
    /**
     * Read entire file into a string. (used to read json files from assets)
     *
     * @param fileName name of file to read
     * @return Entire file contents in a string
     * @throws IOException thrown if IO can not be performed
     */
    @Throws(IOException::class)
    fun readFromFile(fileName: String): String {
        val returnString = StringBuilder()
        var fIn: InputStream? = null
        var isr: InputStreamReader? = null
        var input: BufferedReader? = null

        try {
            fIn = ObdLibrary.getResourceFileInputStream(fileName)

            if (fIn == null) {
                error("Could not read resource files. ObdLibrary not initialized.")
            }

            isr = InputStreamReader(fIn)
            input = BufferedReader(isr)
            var line = input.readLine()
            while (line != null) {
                returnString.append(line)
                line = input.readLine()
            }
        } finally {
            try {
                isr?.close()
                fIn?.close()
                input?.close()
            } catch (ignored: Exception) {
            }
        }

        return returnString.toString()
    }
}
