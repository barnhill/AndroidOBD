![AndroidOBD CI](https://github.com/barnhill/AndroidOBD/workflows/Android%20CI/badge.svg) [![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)

## Android OBD Library


### What is this repository for? ###

This project offers a developer friendly interface to communicate with ELM 327 OBD devices via BLUETOOTH.

### Usage ###

Add Dependency:
```Gradle
implementation 'com.pnuema.android:obd:1.7.0'
```

To get started you will need to first send a few commands over bluetooth or usb whatever the input stream is that you negotiate with the ELM-327 device.

Connection and init:
```
val MODE_AT = "AT"

//set defaults
initPid.mode = MODE_AT
initPid.PID = "D"
var cmd = OBDCommand(initPid).setIgnoreResult(true).run(inputStream, outputStream)
Log.d(TAG, "Set defaults sent (" + initPid.mode + " " + initPid.PID + ") Received: " + cmd.rawResult)

//resets the ELM327
initPid.mode = MODE_AT
initPid.PID = "Z"
cmd = OBDCommand(initPid).setIgnoreResult(true).run(inputStream, outputStream)
Log.d(TAG, "Reset command sent (" + initPid.mode + " " + initPid.PID + ") Received: " + cmd.rawResult)

//extended responses off
initPid.mode = MODE_AT
initPid.PID = "E0"
cmd = OBDCommand(initPid).setIgnoreResult(true).run(inputStream, outputStream)
Log.d(TAG, "Extended Responses Off (" + initPid.mode + " " + initPid.PID + ") Received: " + cmd.rawResult)

//line feeds off
initPid.mode = MODE_AT
initPid.PID = "L0"
cmd = OBDCommand(initPid).setIgnoreResult(true).run(inputStream, outputStream)
Log.d(TAG, "Turn Off Line Feeds (" + initPid.mode + " " + initPid.PID + ") Received: " + cmd.rawResult)

//printing of spaces off
initPid.mode = MODE_AT
initPid.PID = "S0"
cmd = OBDCommand(initPid).setIgnoreResult(true).run(inputStream, outputStream)
Log.d(TAG, "Printing Spaces Off (" + initPid.mode + " " + initPid.PID + ") Received: " + cmd.rawResult)

//headers off
initPid.mode = MODE_AT
initPid.PID = "H0"
cmd = OBDCommand(initPid).setIgnoreResult(true).run(inputStream, outputStream)
Log.d(TAG, "Headers Off (" + initPid.mode + " " + initPid.PID + ") Received: " + cmd.rawResult)

//set protocol
initPid.mode = "$MODE_AT SP"
initPid.PID = ObdProtocols.AUTO.value.toString()
cmd = OBDCommand(initPid).setIgnoreResult(true).run(inputStream, outputStream)
Log.d(TAG, "Select Protocol (" + initPid.mode + " " + initPid.PID + ") Received: " + cmd.rawResult)

//set timeout for response from the ECU
initPid.mode = "$MODE_AT ST"
initPid.PID = Integer.toHexString(0xFF and ECU_RESPONSE_TIMEOUT)
cmd = OBDCommand(initPid).setIgnoreResult(true).run(inputStream, outputStream)
```

Once a connection has been established and inited you can send commands and get responses as follows:

Code:
```
//Request MODE 1, PID 0C - RPM
val pid = PIDUtils.getPid(ObdModes.MODE_01, "OC")
val command = OBDCommand(pid)
command.run(bluetoothSocket.inputStream, bluetoothSocket.outputStream)

Log.d("PID", "${pid.description} : ${pid.calculatedResult}")
Log.d("PID Formatted Result", command.formattedResult)
```

```
//Clear DTCs - NonPermanent
val pid = PID(ObdModes.MODE_04) //Clear DTCs
val command = OBDCommand(pid)
command.run(bluetoothSocket.inputStream, bluetoothSocket.outputStream)
```

Note that you do not have to take the raw values and calculate it yourself.  The library will run the value through the formula that are specified in the specifications for CAN to get the resulting value.  This is available in the `calculatedResult` and `formattedResult` fields on the pid after the `pid.run(...)` command finishes.

### Who do I talk to? ###

* Brad Barnhill (https://github.com/barnhill)
