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

import android.content.Context;

import com.obd.lib.R;
import com.obd.lib.models.PID;

/**
 * Generic class to form an OBD command for communication, also includes the parsing of the result.
 * <p/>
 * author Brad Barnhill
 */
public class OBDCommand extends BaseObdCommand {
    private static PID mPid;
    private final Context mContext;

    public OBDCommand(Context context, PID pid) {
        this(context, pid, false);
    }

    public OBDCommand(Context context, PID pid, boolean ignoreResult) {
        super(pid.Mode.trim() + (pid.PID.trim().isEmpty() ? "" : " " + pid.PID.trim()), ignoreResult);
        mPid = pid;
        mContext = context;
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    protected void performCalculations() {
        if (!NODATA.equals(getResult())) {
            final String exprText = mPid.Formula;
            final short numBytes = Short.parseShort(mPid.Bytes);
            mPid.Data = buffer.toArray(new Short[buffer.size()]);

            if (handleSpecialPidEnumerations()) {
                return;
            }

            if (exprText == null || (!exprText.contains("A") && !exprText.contains("B") && !exprText.contains("C") && !exprText.contains("D")) || numBytes > 4) {
                mPid.CalculatedResult = buffer.toString();
                return;
            }

            //TODO: first two bytes show what command the data is for, verify this is the command returning that is expected

            final Expression expression = new com.obd.lib.commands.Expression(exprText).setPrecision(5);

            if (buffer.size() > 2)
                expression.with("A", buffer.get(2).toString());

            if (buffer.size() > 3)
                expression.with("B", buffer.get(3).toString());

            if (buffer.size() > 4)
                expression.with("C", buffer.get(4).toString());

            if (buffer.size() > 5)
                expression.with("D", buffer.get(5).toString());

            mPid.CalculatedResult = String.valueOf(expression.eval().floatValue());
        }
    }

    @Override
    public String getFormattedResult() {
        return mPid.CalculatedResult + " " + mPid.Units;
    }

    @Override
    public String getName() {
        return mPid.Description;
    }

    private boolean handleSpecialPidEnumerations() {
        if (mPid.Mode.equals("01") && mPid.PID.equals("51")) {
            //fuel type
            mode1Pid51_Translation();
            return true;
        }

        return false;
    }

    private void mode1Pid51_Translation() {
        final String[] pidTranslation = mContext.getResources().getStringArray(R.array.mode1_pid51_translation);
        if (!buffer.isEmpty() && buffer.get(2) < pidTranslation.length) {
            mPid.CalculatedResult = pidTranslation[buffer.get(2)];
        }
    }
}