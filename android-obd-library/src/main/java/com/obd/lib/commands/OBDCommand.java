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
import com.obd.lib.statics.Translations;
import com.udojava.evalex.Expression;

/**
 * Generic class to form an OBD command for communication, also includes the parsing of the result.
 *
 * @author Brad Barnhill
 */
public class OBDCommand extends BaseObdCommand {
    private boolean mMetricUnits = true;

    public OBDCommand(final PID pid) {
        super(pid.Mode.trim() + (pid.PID.trim().isEmpty() ? "" : " " + pid.PID.trim()), pid);
        pid.RetrievalTime = 0;
    }

    /**
     * Set if the result of the request will be ignored
     * @param ignoreResult {@link Boolean} which is whether the result should be ignored when its returned.
     * @return Current {@link OBDCommand}
     */
    public OBDCommand setIgnoreResult(final boolean ignoreResult) {
        mIgnoreResult = ignoreResult;
        return this;
    }

    /**
     * Set to metric or imperial units
     * @param metric True if to return metric units, false for imperial units.
     * @return Current {@link OBDCommand}
     */
    public OBDCommand setUnitType(final boolean metric) {
        mMetricUnits = metric;
        return this;
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    protected void performCalculations() {
        if (!NODATA.equals(getRawResult())) {
            final String exprText = mMetricUnits || mPid.ImperialFormula == null ? mPid.Formula : mPid.ImperialFormula;

            Byte numBytes;
            try {
                numBytes = Byte.parseByte(mPid.Bytes);
            } catch (final NumberFormatException nfex) {
                numBytes = 0;
            }

            mPid.Data = buffer.toArray(new Short[buffer.size()]);

            if (Translations.HandleSpecialPidEnumerations(mPid, buffer)) {
                return;
            }

            if (exprText == null || !exprText.contains("A") && !exprText.contains("B") && !exprText.contains("C") && !exprText.contains("D") || numBytes > 4 || numBytes <= 0 || buffer.size() <= 2) {
                mPid.CalculatedResultString = buffer.toString();
                return;
            }

            //TODO: first two bytes show what command the data is for, verify this is the command returning that is expected

            final Expression expression = new Expression(exprText).setPrecision(5);

            if (buffer.size() > 2)
                expression.with("A", buffer.get(2).toString());

            if (buffer.size() > 3)
                expression.with("B", buffer.get(3).toString());

            if (buffer.size() > 4)
                expression.with("C", buffer.get(4).toString());

            if (buffer.size() > 5)
                expression.with("D", buffer.get(5).toString());

            try {
                mPid.CalculatedResult = expression.eval().floatValue();
                mPid.CalculatedResultString = String.valueOf(mPid.CalculatedResult);
            } catch (final NumberFormatException nfex) {
                Log.e(OBDCommand.class.getSimpleName(), "[Expression:" + expression + "] [Mode:" + mPid.Mode + "] [Pid:" + mPid.PID + "] [Formula:" + mPid.Formula
                        + "] [Bytes:" + mPid.Bytes + "] [BytesReturned:" + buffer.size() + "]", nfex);
            }
        }
    }

    @Override
    public String getFormattedResult() {
        return mPid.CalculatedResult + " " + (mMetricUnits || mPid.ImperialFormula == null ? mPid.Units : mPid.ImperialUnits);
    }

    @Override
    public String getName() {
        return mPid.Description;
    }

    public long getCallDuration() {
        return mPid.RetrievalTime;
    }
}
