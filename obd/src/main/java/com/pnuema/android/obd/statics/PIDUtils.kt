/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.pnuema.android.obd.statics

import android.util.Log
import android.util.SparseArray
import com.pnuema.android.obd.enums.ObdModes
import com.pnuema.android.obd.models.PID
import com.pnuema.android.obd.models.PIDS
import kotlinx.serialization.json.Json
import java.io.IOException
import java.util.*

/**
 * Class to hold all the static methods necessary for the OBD library.
 * that pertain to DTCs
 *
 * @author Brad Barnhill
 */
@Suppress("unused")
object PIDUtils {
    private val TAG = PIDUtils::class.java.simpleName
    private val pidsSparseArray = SparseArray<SortedMap<Int, PID>>()

    /**
     * Gets list of pids for the mode specified
     *
     * @param mode mode to look up the list of pids
     * @return List of [PIDS] contained in the specified mode
     * @throws IOException thrown if IO can not be performed
     */
    @Throws(IOException::class)
    fun getPidList(mode: ObdModes): List<PID> = ArrayList(getPidMap(mode)!!.values)

    /**
     * Gets PID object by mode and pid.
     *
     * @param mode mode to look the pid up in
     * @param pid Pid number to retrieve
     * @return [PID] object
     * @throws IOException thrown if IO can not be performed
     */
    @Throws(IOException::class,java.lang.IllegalArgumentException::class)
    fun getPid(mode: ObdModes, pid: String): PID? {
        val start = Calendar.getInstance().time

        getPidMap(mode)?.let { pids ->
            val p = pids[Integer.parseInt(pid, 16)]
            p?.let {
                Log.d(TAG, "Found pid " + it.PID + " in " + (Calendar.getInstance().time.time - start.time).toString() + " ms")
            }

            return p
        } ?: run {
            Log.d(TAG, "Pids for this mode do not exist.")
            return null
        }
    }

    @Throws(IOException::class,java.lang.IllegalArgumentException::class)
    private fun getPidMap(mode: ObdModes): SortedMap<Int, PID>? {
        if (pidsSparseArray.size() > 0 && pidsSparseArray.indexOfKey(mode.intValue) >= 0) {
            //get value from pid cache
            return pidsSparseArray.get(mode.intValue)
        } else {
            //not found in cache so read it from json files and store it in cache
            val pidList = Json.decodeFromString<PIDS>(FileUtils.readFromFile("pids-mode" + mode.intValue + ".json")).pids
            val pidMap = TreeMap<Int, PID>()

            pidList.forEach { pid ->
                try {
                    pidMap[Integer.parseInt(pid.PID, 16)] = pid
                } catch (nfex: NumberFormatException) {
                    Log.d(TAG, "Parsing PID number to integer failed: " + nfex.message)
                }
            }

            require(!pidMap.isEmpty()) { "Unsupported mode requested: $mode" }

            pidsSparseArray.put(mode.intValue, pidMap)
            return pidsSparseArray.get(mode.intValue)
        }
    }
}
