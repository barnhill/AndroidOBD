package com.obd.lib.statics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Static definitions for all file related utilities.
 *
 * @author Brad Barnhill
 */
public class FileUtils {
    /**
     * Read entire file into a string. (used to read json files from assets)
     *
     * @param fileName name of file to read
     * @return Entire file contents in a string
     * @throws IOException thrown if IO can not be performed
     */
    static String readFromFile(final String fileName) throws IOException {
        final StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;

        try {
            fIn = AppContext.getResourceFileInputStream(fileName);

            if (fIn == null) {
                return "";
            }

            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line;
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (final Exception e2) {
                e2.getMessage();
            }
        }

        return returnString.toString();
    }
}
