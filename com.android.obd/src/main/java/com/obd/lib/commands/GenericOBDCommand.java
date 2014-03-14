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

import com.obd.lib.models.pidsModel;

import parsii.eval.Expression;
import parsii.eval.Parser;
import parsii.eval.Scope;
import parsii.eval.Variable;

/**
 * Generic class to form an OBD command for communication, also includes the parsing of the result.
 * <p/>
 * author Brad Barnhill
 */
public class GenericOBDCommand extends ObdCommand {
    final pidsModel.Element mPid;
    double mValue = 0;

    public GenericOBDCommand(pidsModel.Element pid) {
        super(pid.Mode + " " + pid.PID);
        mPid = pid;
    }

    @Override
    protected void performCalculations() {
        if (!NODATA.equals(getResult())) {
            Scope scope = Scope.create();

            Variable a = scope.getVariable("A");
            Variable b = scope.getVariable("B");
            Variable c = scope.getVariable("C");
            Variable d = scope.getVariable("D");

            if (buffer.size() > 2) {
                a.setValue(buffer.get(2));
            }

            if (buffer.size() > 3) {
                b.setValue(buffer.get(3));
            }

            if (buffer.size() > 4) {
                c.setValue(buffer.get(4));
            }

            if (buffer.size() > 5) {
                d.setValue(buffer.get(5));
            }

            try {
                Expression expr = Parser.parse(mPid.Formula, scope);
                mValue = expr.evaluate();
            } catch (parsii.tokenizer.ParseException pex) {
                Log.e(GenericOBDCommand.class.getSimpleName(), pex.getMessage());
            }
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
