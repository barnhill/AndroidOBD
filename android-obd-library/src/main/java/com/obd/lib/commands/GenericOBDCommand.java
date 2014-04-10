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
package com.obd.lib.commands;

import android.util.Log;

import com.obd.lib.models.PID;

/**
 * Generic class to form an OBD command for communication, also includes the parsing of the result.
 * <p/>
 * author Brad Barnhill
 */
public class GenericOBDCommand extends ObdCommand {
    final PID mPid;
    double mValue = 0;

    public GenericOBDCommand(PID pid) {
        this(pid, false);
    }

    public GenericOBDCommand(PID pid, boolean ignoreResult) {
        super(pid.Mode + " " + pid.PID.trim(), ignoreResult);
        mPid = pid;
    }

    @Override
    protected void performCalculations() {
        if (!NODATA.equals(getResult())) {
            String exprText = mPid.Formula;
            int numBytes = Integer.parseInt(mPid.Bytes);

            if (buffer.size() > 2 && numBytes > 0) {
                exprText = exprText.replaceFirst("A", buffer.get(2).toString());
            }

            if (buffer.size() > 3 && numBytes > 1) {
                exprText = exprText.replaceFirst("B", buffer.get(3).toString());
            }

            if (buffer.size() > 4 && numBytes > 2) {
                exprText = exprText.replaceFirst("C", buffer.get(4).toString());
            }

            if (buffer.size() > 5 && numBytes > 0) {
                exprText = exprText.replaceFirst("D", buffer.get(5).toString());
            }

            mPid.CalculatedResult = String.valueOf(new com.obd.lib.commands.Expression(exprText).eval().floatValue());
            Log.d(GenericOBDCommand.class.getSimpleName(), "ExprText: " + exprText);
            Log.d(GenericOBDCommand.class.getSimpleName(), "Result: " + mPid.CalculatedResult);
        }
    }

    @Override
    public String getFormattedResult() {
        return mValue + " " + mPid.Units;
    }

    @Override
    public String getName() {
        return mPid.Description;
    }
}
