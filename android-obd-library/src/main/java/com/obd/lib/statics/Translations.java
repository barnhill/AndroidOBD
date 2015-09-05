package com.obd.lib.statics;

import android.content.Context;

import com.bradbarnhill.obd.android.R;
import com.obd.lib.models.PID;

import java.util.ArrayList;

/**
 * This class provides translations for pids that are based on enumeration or other translation
 * methods other than just conversion using a simple formula.
 * <p/>
 *
 * @author Brad Barnhill
 */
public class Translations {
    public static boolean HandleSpecialPidEnumerations(final Context context, final PID pid, final ArrayList<Short> buffer) {
        if (pid.Mode.equals("01") && pid.PID.equals("01")) {
            //dtc count
            mode1Pid01_Translation(context, pid, buffer);
            return true;
        } else if (pid.Mode.equals("01") && pid.PID.equals("51")) {
            //fuel type
            mode1Pid51_Translation(context, pid, buffer);
            return true;
        }

        return false;
    }

    private static void mode1Pid01_Translation(final Context context, final PID pid, final ArrayList<Short> buffer) {
        if (!buffer.isEmpty() && buffer.size() > 2 && context != null) {
            // ignore first two bytes [hh hh] of the response
            final int mil = buffer.get(2);
            final String on = context.getString(R.string.mode1_pid01_translation_on);
            final String off = context.getString(R.string.mode1_pid01_translation_off);
            final String onOff = (mil & 0x80) == 128 ? on : off;
            pid.CalculatedResultString = context.getString(R.string.mode1_pid01_translation, onOff, mil & 0x7F);
        }
    }

    private static void mode1Pid51_Translation(final Context context, final PID pid, final ArrayList<Short> buffer) {
        final String[] pidTranslation = context.getResources().getStringArray(R.array.mode1_pid51_translation);
        if (!buffer.isEmpty() && buffer.get(2) < pidTranslation.length) {
            pid.CalculatedResultString = pidTranslation[buffer.get(2)];
        }
    }
}
