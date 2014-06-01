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

import org.joda.time.DateTime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Base OBD command.
 */
public abstract class ObdCommand {
    public static final String NODATA = "NODATA";

    protected ArrayList<Short> buffer = null;
    protected String cmd = null;
    protected boolean useImperialUnits = false;
    protected String rawData = null;
    protected boolean mIgnoreResult;
    private DateTime mStartTime;
    protected long mDurationOfCall;

    private ObdCommand() {
    }

    /**
     * Default ctor to use
     *
     * @param command the command to send
     */
    public ObdCommand(String command) {
        this.buffer = new ArrayList<Short>();
        this.cmd = command;
    }

    public ObdCommand(String command, boolean ignoreResult) {
        this(command.trim());
        mStartTime = new DateTime();
        mIgnoreResult = ignoreResult;
    }

    /**
     * Copy ctor.
     *
     * @param other the ObdCommand to copy.
     */
    public ObdCommand(ObdCommand other) {
        this(other.cmd);
    }

    /**
     * Sends the OBD-II request and deals with the response.
     * <p/>
     * This method CAN be overriden in fake commands.
     */
    public ObdCommand run(InputStream in, OutputStream out) throws IOException,
            InterruptedException {
        sendCommand(out);
        readResult(in);

        mDurationOfCall = new DateTime().getMillis() - mStartTime.getMillis();

        return this;
    }

    /**
     * Sends the OBD-II request.
     * <p/>
     * This method may be overriden in subclasses, such as ObMultiCommand or
     * TroubleCodesObdCommand.
     *
     * @param out The output stream.
     */
    protected void sendCommand(OutputStream out) throws IOException,
            InterruptedException {
        // add the carriage return char
        cmd += "\r";

        // write to OutputStream (i.e.: a BluetoothSocket)
        out.write(cmd.getBytes());
        out.flush();

    /*
     * HACK GOLDEN HAMMER ahead!!
     * 
     * Due to the time that some systems may take to respond, let's give it
     * 200ms.
     */
        //Thread.sleep(200);
    }

    /**
     * Resends this command.
     */
    protected void resendCommand(OutputStream out) throws IOException,
            InterruptedException {
        out.write("\r".getBytes());
        out.flush();
    }

    /**
     * Reads the OBD-II response.
     * <p/>
     * This method may be overriden in subclasses, such as ObdMultiCommand.
     */
    protected void readResult(InputStream in) throws IOException {
        readRawData(in);

        if (!mIgnoreResult) {
            fillBuffer();
            performCalculations();
        }
    }

    /**
     * This method exists so that for each command, there must be a method that is
     * called only once to perform calculations.
     */
    protected abstract void performCalculations();

    /**
     *
     */
    protected void fillBuffer() {
        // read string each two chars
        buffer.clear();

        int begin = 0;
        int end = 2;
        while (end <= rawData.length()) {
            try {
                buffer.add(Short.decode("0x" + rawData.substring(begin, end)));
            } catch (NumberFormatException e) {
                break;
            }
            begin = end;
            end += 2;
        }
    }

    void readRawData(InputStream in) throws IOException {
        byte b;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives
        while ((char) (b = (byte) in.read()) != '>')
            if ((char) b != ' ')
                res.append((char) b);

    /*
     * Imagine the following response 41 0c 00 0d.
     * 
     * ELM sends strings!! So, ELM puts spaces between each "byte". And pay
     * attention to the fact that I've put the word byte in quotes, because 41
     * is actually TWO bytes (two chars) in the socket. So, we must do some more
     * processing..
     */
        rawData = res.toString().trim();

    /*
     * Data may have echo or informative text like "INIT BUS..." or similar.
     * The response ends with two carriage return characters. So we need to take
     * everything from the last carriage return before those two (trimmed above).
     */
        rawData = rawData.substring(rawData.indexOf(13) + 1);

     /*remove any remaining carriage returns as this could be a multipart message,
       this means that all messages will have a colon with a message number in front
       that must be stripped too.
      */
        String [] rows = rawData.split("\\r");

        if (rows.length > 1) {
            rawData = "";
            for (String s : rows) {
                if (s.trim().isEmpty()) {
                    continue;
                }
                rawData += s.substring(s.indexOf(":") + 1);
            }
        }
    }

    /**
     * @return the raw command response in string representation.
     */
    public String getResult() {
        rawData = rawData.contains("SEARCHING") || rawData.contains("DATA") || rawData.contains("ELM327") ? NODATA
                : rawData;

        return rawData;
    }

    /**
     * @return a formatted command response in string representation.
     */
    public abstract String getFormattedResult();

    /**
     * @return a list of integers
     */
    protected ArrayList<Short> getBuffer() {
        return buffer;
    }

    /**
     * @return true if imperial units are used, or false otherwise
     */
    public boolean useImperialUnits() {
        return useImperialUnits;
    }

    /**
     * Sets whether to use imperial units.
     *
     * @param isImperial Set to 'true' if you want to use imperial units, false otherwise. By
     *                   default this value is set to 'false'.
     */
    public void useImperialUnits(boolean isImperial) {
        this.useImperialUnits = isImperial;
    }

    /**
     * @return the OBD command name.
     */
    public abstract String getName();

    /**
     * Gets how long the read from OBD took in milliseconds
     *
     * @return the number of milliseconds that the call took
     */
    public long getCallDuration() {
        return mDurationOfCall;
    }

}