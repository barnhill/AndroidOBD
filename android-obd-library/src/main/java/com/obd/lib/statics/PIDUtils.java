/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.obd.lib.statics;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.obd.lib.models.PID;
import com.obd.lib.models.PIDS;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Class to hold all the static methods necessary for the OBD library.
 * that pertain to DTCs
 *
 * @author Brad Barnhill
 */
public class PIDUtils {
    public static final String TAG = PIDUtils.class.getSimpleName();

    public static List<PID> getPidList(Context context) throws IOException {
        return getPidList(context, 1);
    }

    public static List<PID> getPidList(Context context, int mode) throws IOException {
        return new Gson().fromJson(FileUtils.readFromFile(context, "pids-mode" + mode + ".json"), PIDS.class).pids;
    }

    public static PID getPid(Context context, int mode, String pid) throws IOException {
        DateTime start = DateTime.now();
        List<PID> pids = getPidList(context, mode);

        if (pids == null) {
            Log.d(TAG, "Pids for this mode do not exist.");
            return null;
        }

        for (PID p : pids) {
            if (p.PID.toLowerCase().equals(pid.toLowerCase(Locale.US).trim())) {
                Log.d(TAG, "Found pid " + p.PID + " in " + new Period(start, DateTime.now()).getMillis() + " ms");
                return p;
            }
        }

        //pid not found
        Log.d(TAG, "Pid not found in this mode");
        return null;
    }
}
