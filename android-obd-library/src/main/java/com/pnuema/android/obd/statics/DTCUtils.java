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
package com.pnuema.android.obd.statics;

import com.google.gson.GsonBuilder;
import com.pnuema.android.obd.models.DTC;
import com.pnuema.android.obd.models.DTCS;

import java.io.IOException;
import java.util.List;

/**
 * Class to hold all the static methods necessary for the OBD library
 * that pertain to DTCs
 *
 * @author Brad Barnhill
 */
@SuppressWarnings("unused")
public class DTCUtils {
    public static List<DTC> getDTCList() throws IOException {
        return new GsonBuilder().create().fromJson(FileUtils.readFromFile("dtc-codes.json"), DTCS.class).dtcs;
    }
}
