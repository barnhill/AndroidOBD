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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Class to hold all the static methods necessary for the OBD library.
 * that pertain to DTCs
 *
 * @author Brad Barnhill
 */
public class PIDUtils {
    public static final String TAG = PIDUtils.class.getSimpleName();
    public static final HashMap<Integer, TreeMap<Integer, PID>> pidHashMap = new HashMap<>();

    /**
     * Gets list of pids for the mode specified
     *
     * @param context Context of the application
     * @param mode Mode to look up the list of pids
     * @return List of PIDS contained in the specified mode
     * @throws IOException
     */
    public static List<PID> getPidList(Context context, int mode) throws IOException {
        return new ArrayList<>(getPidMap(context, mode).values());
    }

    /**
     * Gets PID object by mode and pid.
     *
     * @param context Context of the application
     * @param mode Mode to look the pid up in
     * @param pid Pid number to retrieve
     * @return PID object
     * @throws IOException
     */
    public static PID getPid(Context context, int mode, String pid) throws IOException {
        DateTime start = DateTime.now();

        TreeMap<Integer, PID> pids = getPidMap(context, mode);

        if (pids == null) {
            Log.d(TAG, "Pids for this mode do not exist.");
            return null;
        }

        PID p = pids.get(Integer.parseInt(pid, 16));
        Log.d(TAG, "Found pid " + p.PID + " in " + new Period(start, DateTime.now()).getMillis() + " ms");

        return p;
    }

    private static TreeMap<Integer, PID> getPidMap(Context context, Integer mode) throws IOException {
        if (!pidHashMap.isEmpty() && pidHashMap.containsKey(mode)) {
            //get value from pid cache
            return pidHashMap.get(mode);
        } else {
            //not found in cache so read it from json files and store it in cache
            List<PID> pidList = new Gson().fromJson(FileUtils.readFromFile(context, "pids-mode" + mode + ".json"), PIDS.class).pids;
            TreeMap<Integer, PID> pidMap = new TreeMap<>();
            for (PID pid : pidList) {
                int pidInt = 0;
                try {
                    pidInt = Integer.parseInt(pid.PID, 16);
                } catch (NumberFormatException nfex) {
                    Log.d(TAG, "Parsing PID number to integer failed: " + nfex.getMessage());
                }

                pidMap.put(pidInt, pid);
            }

            if (pidMap.isEmpty()) {
                throw new IllegalArgumentException("Unsupported mode requested: " + mode.toString());
            }

            pidHashMap.put(mode, pidMap);
            return pidHashMap.get(mode);
        }
    }
}
