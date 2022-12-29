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

        val fIn = ObdLibrary.getResourceFileInputStream(fileName)
            ?: error("Could not read resource files. ObdLibrary not initialized.")

        InputStreamReader(fIn).use { isr ->
            BufferedReader(isr).use { input ->
                var line = input.readLine()
                while (line != null) {
                    returnString.append(line)
                    line = input.readLine()
                }
            }
        }

        return returnString.toString()
    }
}
