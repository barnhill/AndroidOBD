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
package com.pnuema.android.obd.enums

import java.lang.Long.parseLong

/**
 * All OBD modes.
 *
 * @author Brad Barnhill
 */
enum class ObdModes constructor(val value: Char) {
    /**
     * This mode returns the common values for some sensors
     */
    MODE_01('1'),

    /**
     * This mode gives the freeze frame (or instantaneous) data of a fault
     */
    MODE_02('2'),

    /**
     * This mode shows the stored diagnostic trouble codes
     */
    MODE_03('3'),

    /**
     * This mode is used to clear recorded fault codes
     */
    MODE_04('4'),

    /**
     * This mode gives the results of self-diagnostics done on the oxygen/lamda sensors
     */
    MODE_05('5'),

    /**
     * This mode gives the results of self-diagnostics done on systems not subject to constant surveillance
     */
    MODE_06('6'),

    /**
     * This mode gives unconfirmed fault codes
     */
    MODE_07('7'),

    /**
     * This mode gives the results of self-diagnostics on other systems (Hardly used in Europe)
     */
    MODE_08('8'),

    /**
     * This mode gives the information concerning the vehicle
     */
    MODE_09('9'),

    /**
     * This mode shows the permanent diagnostic trouble codes
     */
    MODE_0A('A');

    val intValue: Int
        get() = parseLong(value.toString(), 16).toInt()

    override fun toString(): String {
        return value.toString()
    }
}