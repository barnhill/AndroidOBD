package com.obd.lib.statics;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Static definitions for all file related utilities.
 * <p/>
 * Created by bbarnhill on 6/1/2014.
 */
public class FileUtils {
    /**
     * Read entire file into a string. (used to read json files from assets)
     *
     * @param context context to get the assets from
     * @param fileName name of file to read
     * @return Entire file contents in a string
     * @throws IOException
     */
    public static String readFromFile(Context context, String fileName) throws IOException {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;

        try {
            fIn = context.getResources().getAssets().open(fileName);
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
            } catch (Exception e2) {
                e2.getMessage();
            }
        }

        return returnString.toString();
    }
}
