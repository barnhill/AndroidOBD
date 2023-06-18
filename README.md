![AndroidOBD CI](https://github.com/barnhill/AndroidOBD/workflows/AndroidOBD%20CI/badge.svg) [![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)

## Android OBD Library


### What is this repository for? ###

This project offers a developer friendly interface to communicate with ELM 327 OBD devices via BLUETOOTH.

### Usage ###

Add Dependency:
```Gradle
implementation 'com.pnuema.android:obd:1.4.0'
```

Code:
```
//Request MODE 1, PID 0C - RPM
val pid = PID(ObdModes.MODE_01, "0C")
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

### Who do I talk to? ###

* Brad Barnhill (https://github.com/barnhill)
