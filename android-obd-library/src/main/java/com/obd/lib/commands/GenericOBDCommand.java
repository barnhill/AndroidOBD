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

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import parsii.eval.Parser;
import parsii.eval.Scope;
import parsii.eval.Variable;

/**
 * Generic class to form an OBD command for communication, also includes the parsing of the result.
 * <p/>
 * author Brad Barnhill
 */
public class GenericOBDCommand extends ObdCommand {
    final PID mPid;
    double mValue = 0;

    public GenericOBDCommand(PID pid) {
        super(pid.Mode + " " + pid.PID.trim());
        mPid = pid;
    }

    @Override
    protected void performCalculations() {
        if (!NODATA.equals(getResult())) {
            String exprText = mPid.Formula;
            int numBytes = Integer.parseInt(mPid.Bytes);

            if (buffer.size() > 2 && numBytes  > 0) {
                exprText = exprText.replace("A", buffer.get(2).toString());
            }

            if (buffer.size() > 3 && numBytes  > 1) {
                exprText = exprText.replace("B", buffer.get(3).toString());
            }

            if (buffer.size() > 4 && numBytes  > 2) {
                exprText = exprText.replace("C", buffer.get(4).toString());
            }

            if (buffer.size() > 5 && numBytes  > 0) {
                exprText = exprText.replace("D", buffer.get(5).toString());
            }

            Log.d(GenericOBDCommand.class.getSimpleName(), "ExprText: " + exprText);
            ExpressionParser parser = new SpelExpressionParser();
            Expression expression = parser.parseExpression(exprText);
            Log.d(GenericOBDCommand.class.getSimpleName(), "Expression: " + expression.getExpressionString());
            mValue = expression.getValue(double.class);
            Log.d(GenericOBDCommand.class.getSimpleName(), "Value: " + expression.getExpressionString());

//            Scope scope = Scope.create();
//
//            Variable A = scope.getVariable("A");
//            Variable B = scope.getVariable("B");
//            Variable C = scope.getVariable("C");
//            Variable D = scope.getVariable("D");
//
//            int numBytes = Integer.parseInt(mPid.Bytes);
//
//            if (buffer.size() > 2 && numBytes  > 0) {
//                A.setValue(buffer.get(2));
//            }
//
//            if (buffer.size() > 3 && numBytes  > 1) {
//                B.setValue(buffer.get(3));
//            }
//
//            if (buffer.size() > 4 && numBytes  > 2) {
//                C.setValue(buffer.get(4));
//            }
//
//            if (buffer.size() > 5 && numBytes  > 3) {
//                D.setValue(buffer.get(5));
//            }
//
//            try {
//                mValue = Parser.parse(mPid.Formula, scope).evaluate();
//            } catch (parsii.tokenizer.ParseException pex) {
//                Log.e(GenericOBDCommand.class.getSimpleName(), pex.getMessage());
//            } catch (NoClassDefFoundError cdex) {
//                Log.e(GenericOBDCommand.class.getSimpleName(), cdex.getMessage());
//            }
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
