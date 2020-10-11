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

import com.pnuema.android.obd.models.DTC
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.IOException

/**
 * Class to hold all the static methods necessary for the OBD library
 * that pertain to DTCs
 *
 * @author Brad Barnhill
 */
object DTCUtils {
    val dtcList: List<DTC>
        @Throws(IOException::class)
        get() = Json.decodeFromString(FileUtils.readFromFile("dtc-codes.json"))
}
