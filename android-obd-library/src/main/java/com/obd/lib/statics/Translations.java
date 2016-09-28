package com.obd.lib.statics;

import com.bradbarnhill.obd.android.R;
import com.obd.lib.models.PID;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * This class provides translations for pids that are based on enumeration or other translation
 * methods other than just conversion using a simple formula.
 *
 * @author Brad Barnhill
 */
public class Translations {
    public static boolean HandleSpecialPidEnumerations(final PID pid, final ArrayList<Short> buffer) {
        if (pid.Mode.equals("01") && pid.PID.equals("00")) {
            mode1Pid00_Translation(pid, buffer);
            return true;
        } else if (pid.Mode.equals("01") && pid.PID.equals("01")) {
            //dtc count
            mode1Pid01_Translation(pid, buffer);
            return true;
        } else if (pid.Mode.equals("01") && pid.PID.equals("51")) {
            //fuel type
            mode1Pid51_Translation(pid, buffer);
            return true;
        }

        return false;
    }

    /**
     * Not finished yet
     */
    private static void mode1Pid00_Translation(final PID pid, final ArrayList<Short> buffer) {
        if (buffer.size() != 6) {
            return;
        }

        String hexString = "";
        final List<Short> subList = buffer.subList(2, buffer.size());
        String temp;
        for (int i = 0; i < subList.size(); i++) {
            temp = Integer.toHexString(subList.get(i));
            hexString += (temp.length() == 1 ? "0" : "") + temp;
        }

        pid.CalculatedResult = Long.parseLong(hexString, 16);
        pid.CalculatedResultString = hexString;
    }

    private static void mode1Pid01_Translation(final PID pid, final ArrayList<Short> buffer) {
        if (!buffer.isEmpty() && buffer.size() > 2) {
            // ignore first two bytes [hh hh] of the response
            final int mil = buffer.get(2);
            final String on = AppContext.getResourceString(R.string.mode1_pid01_translation_on);
            final String off = AppContext.getResourceString(R.string.mode1_pid01_translation_off);
            final String onOff = (mil & 0x80) == 128 ? on : off;
            pid.CalculatedResultString = String.format(AppContext.getResourceString(R.string.mode1_pid01_translation), onOff, mil & 0x7F);
        }
    }

    private static void mode1Pid51_Translation(final PID pid, final ArrayList<Short> buffer) {
        final String[] pidTranslation = AppContext.getResourceStringArray(R.array.mode1_pid51_translation);
        if (!buffer.isEmpty() && buffer.get(2) < pidTranslation.length) {
            pid.CalculatedResultString = pidTranslation[buffer.get(2)];
        }
    }

    private final static int bitsInOneCharValue = 4;
    private final static int HEX_RADIX = 16;
    private final static String SPACE = " ";
    /**
     * Convert the given hexadecimal number to bits
     *
     * @param hex The {@link String} representation of the hexadecimal
     * @return {@link BitSet} containing the bits of the hexadecimal
     */
    public static BitSet hexToBitSet(final String hex) {
        final int hexBitSize = hex.length() * bitsInOneCharValue;
        final BitSet hexBitSet = new BitSet(hexBitSize);

        for (int i = 0; i < hex.length(); i++) {
            final Character hexChar = hex.charAt(i);
            final BitSet charBitSet = hexCharToBitSet(hexChar);

            for (int j = 0; j < bitsInOneCharValue; j++) {
                if (charBitSet.get(j)) {
                    hexBitSet.set(j + (hex.length() - i - 1) * bitsInOneCharValue);
                }
            }
        }

        return hexBitSet;
    }

    /**
     * Convert the given hexadecimal character to its bits. Note: valid inputs
     * are 0-F
     *
     * @param hexChar the hexadecimal character to convert
     * @return BitSet containing the bits of the hexadecimal character
     */
    public static BitSet hexCharToBitSet(final Character hexChar) {
        final BitSet charBitSet = new BitSet(bitsInOneCharValue);
        final int hex = Integer.parseInt(hexChar.toString(), HEX_RADIX);

        for (int i = 0; i < bitsInOneCharValue; i++) {
            final int bit = Integer.lowestOneBit(hex >> i);
            if (bit == 1)
                charBitSet.set(i);
        }

        return charBitSet;
    }

    /**
     * Gets if a pid is available or not based on the value of a bitset
     *
     * @param pid PID to check if available to query
     * @param pidValue parent pid value (hex) which denotes which pids are available
     * @return True if PID is available, false otherwise.
     */
    public static boolean isPidAvailable(final PID pid, final String pidValue) {
        //each parent pid governs 32 pids
        final int pidIndex = Integer.parseInt(pid.PID, 16) % 32 - 1;

        return hexToBitSet(pidValue).get(pidIndex);
    }
}
