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
package com.pnuema.android.obd.models

import com.pnuema.android.obd.enums.ObdModes
import kotlinx.serialization.Serializable

/**
 * Holder for a single DTC's data
 *
 * @author Brad Barnhill
 */
@Serializable
data class DTC (
    var mode: String = "01",
    var code: String? = null,
    var description: String? = null
) {
    /**
     * Sets the mode.
     *
     * @param mode mode to set.
     * @return DTC object with the mode set. (returns object for method chaining support)
     */
    fun setMode(mode: ObdModes): DTC {
        this.mode = "0" + mode.value

        return this
    }
}
