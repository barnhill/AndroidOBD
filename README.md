![AndroidOBD CI](https://github.com/barnhill/AndroidOBD/workflows/Android%20CI/badge.svg) [![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)

## Android OBD Library

![AndroidOBD Logo](https://raw.githubusercontent.com/barnhill/AndroidOBD/refs/heads/main/logo.png | width=250)

### What is this repository for? ###

This project offers a developer friendly interface to communicate with ELM 327 OBD devices via BLUETOOTH.

### Usage ###

Add Dependency:
```Gradle
implementation 'com.pnuema.android:obd:1.8.1'
```

To get started you will need to first send a few commands over bluetooth or usb whatever the input stream is that you negotiate with the ELM-327 device.

Connection and init:
```
val connected = ObdInitSequence.run(bluetoothSocket)
```

Once a connection has been established and inited you can send commands and get responses as follows:

Code:
```
//Request MODE 1, PID 0C - RPM
val pid = PIDUtils.getPid(ObdModes.MODE_01, "0C")
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

Note that you do not have to take the raw values and calculate it yourself.  The library will run the value through the formula that are specified in the specifications for CAN to get the resulting value.  This is available in the `calculatedResult` and `formattedResult` fields on the pid after the `command.run(...)` command finishes.

### Who do I talk to? ###

* Brad Barnhill (https://github.com/barnhill)
