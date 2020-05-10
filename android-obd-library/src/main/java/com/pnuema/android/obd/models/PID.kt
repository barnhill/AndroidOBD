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
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

import java.io.Serializable

/**
 * Holder for a single pids data
 *
 * @author Brad Barnhill
 */
@JsonClass(generateAdapter = true)
class PID : Serializable {
    @Json(name = "Mode")
    var mode: String = "01"
    @Json(name = "PID")
    var PID: String = "01"
    @Json(name = "Bytes")
    var bytes: String = ""
    @Json(name = "Description")
    var description: String = ""
    @Json(name = "Min")
    var min: String? = null
    @Json(name = "Max")
    var max: String? = null
    @Json(name = "Units")
    var units: String? = null
    @Json(name = "Formula")
    var formula: String? = null
    @Json(name = "ImperialFormula")
    var imperialFormula: String? = null
    @Json(name = "ImperialUnits")
    var imperialUnits: String? = null

    var data: Array<Int>? = null
    var calculatedResultString: String? = null
    var calculatedResult: Float = 0.toFloat()
    var retrievalTime: Long = 0
    var isPersistent: Boolean = false

    /**
     * Sets the mode of the PID
     *
     * @param mode mode to set.
     * @return DTC object with the mode set. (returns object for method chaining support)
     */
    fun setMode(mode: ObdModes): PID {
        this.mode = "0" + mode.value

        return this
    }

    /**
     * Sets the PID.
     *
     * @param pid Pid to set (ex. 0C).
     * @param mode Mode to set (ex. 01).
     * @return PID object with the Mode and Pid set. (returns object for method chaining support)
     */
    fun setModeAndPID(mode: ObdModes, pid: String): PID {
        setMode(mode)
        this.PID = pid
        return this
    }

    /**
     * Get the string description of the PID.
     *
     * @return [String] description of the PID
     */
    override fun toString(): String {
        return description
    }
}
