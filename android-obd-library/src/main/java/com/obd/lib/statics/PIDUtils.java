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

import android.util.Log;
import android.util.SparseArray;

import com.google.gson.GsonBuilder;
import com.obd.lib.enums.ObdModes;
import com.obd.lib.models.PID;
import com.obd.lib.models.PIDS;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Class to hold all the static methods necessary for the OBD library.
 * that pertain to DTCs
 *
 * @author Brad Barnhill
 */
public class PIDUtils {
    private static final String TAG = PIDUtils.class.getSimpleName();
    private static final SparseArray<TreeMap<Integer, PID>> pidsSparseArray = new SparseArray<>();

    /**
     * Gets list of pids for the mode specified
     *
     * @param mode Mode to look up the list of pids
     * @return List of {@link PIDS} contained in the specified mode
     * @throws IOException thrown if IO can not be performed
     */
    public static List<PID> getPidList(final ObdModes mode) throws IOException {
        return new ArrayList<>(getPidMap(mode).values());
    }

    /**
     * Gets PID object by mode and pid.
     *
     * @param mode Mode to look the pid up in
     * @param pid Pid number to retrieve
     * @return {@link PID} object
     * @throws IOException thrown if IO can not be performed
     */
    public static PID getPid(final ObdModes mode, final String pid) throws IOException {
        final DateTime start = DateTime.now();

        final TreeMap<Integer, PID> pids = getPidMap(mode);

        if (pids == null) {
            Log.d(TAG, "Pids for this mode do not exist.");
            return null;
        }

        final PID p = pids.get(Integer.parseInt(pid, 16));
        Log.d(TAG, "Found pid " + p.PID + " in " + new Period(start, DateTime.now()).getMillis() + " ms");

        return p;
    }

    private static TreeMap<Integer, PID> getPidMap(final ObdModes mode) throws IOException {
        if (pidsSparseArray.size() > 0 && pidsSparseArray.indexOfKey(mode.getIntValue()) >= 0) {
            //get value from pid cache
            return pidsSparseArray.get(mode.getIntValue());
        } else {
            //not found in cache so read it from json files and store it in cache
            final List<PID> pidList = new GsonBuilder().create().fromJson(FileUtils.readFromFile("pids-mode" + mode.getIntValue() + ".json"), PIDS.class).pids;
            final TreeMap<Integer, PID> pidMap = new TreeMap<>();
            for (final PID pid : pidList) {
                int pidInt = 0;
                try {
                    pidInt = Integer.parseInt(pid.PID, 16);
                } catch (final NumberFormatException nfex) {
                    Log.d(TAG, "Parsing PID number to integer failed: " + nfex.getMessage());
                }

                pidMap.put(pidInt, pid);
            }

            if (pidMap.isEmpty()) {
                throw new IllegalArgumentException("Unsupported mode requested: " + mode.toString());
            }

            pidsSparseArray.put(mode.getIntValue(), pidMap);
            return pidsSparseArray.get(mode.getIntValue());
        }
    }
}
