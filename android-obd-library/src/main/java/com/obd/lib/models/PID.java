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
package com.obd.lib.models;

import java.io.Serializable;

/**
 * Holder for a single pids data
 *
 * @author Brad Barnhill
 */
public class PID implements Serializable {
    public String Mode;
    public String PID;
    public String Bytes;
    public String Description;
    public String Min;
    public String Max;
    public String Units;
    public String Formula;
    public Short[] Data;
    public String CalculatedResultString;
    public float CalculatedResult;
    public long RetrievalTime;

    public PID setMode(int mode) {
        Mode = String.valueOf(mode);

        //0 pad the Mode
        if (Mode.length() == 1) {
            Mode = "0" + Mode;
        }

        return this;
    }

    public PID setPID(String pid) {
        PID = pid;
        return this;
    }
}
