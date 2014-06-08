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

import com.google.gson.Gson;
import com.obd.lib.models.PID;
import com.obd.lib.models.PIDS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Class to hold all the static methods necessary for the OBD library.
 * that pertain to DTCs
 *
 * @author Brad Barnhill
 */
public class PIDUtils {
    public static List<PID> getPidList(Context context) throws IOException {
        return getPidList(context, 1);
    }

    public static List<PID> getPidList(Context context, int mode) throws IOException {
        return new Gson().fromJson(FileUtils.readFromFile(context, "pids-mode" + mode + ".json"), PIDS.class).pids;
    }
}
