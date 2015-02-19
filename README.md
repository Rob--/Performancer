[Jump into the code.](https://github.com/Rob--/Performancer/tree/master/app/src/main/java/io/github/rob__/performancer)
---
# Performancer
#### A simple application to analyse hardware components usage on a given device.
---
The collection is split into 7 sections:

1. RAM
  * Total Memory
  * Free Memory
  * Cached
  * Active
  * Inactive
  * Kernel
  * Non-kernal
  * Memory Threshold
2. CPU
  * Cores
    * Core # - status
  * Clock Speed
  * Architecture
  * Model
  * Temperature
3. Battery
  * Health
  * Technology
  * Status
  * Voltage
  * Temperature
  * Battery Present
4. Storage
  * Internal Storage
    * Total Storage
    * Available Storage
    * Used Storage
  * External Storage
    * Total Storage
    * Available Storage
    * Used Storage
  * Applications
    * Running Applications
    * Installed Applications
  * Pictures
  * Videos
5. Device
  * Date
  * Time
  * Brand
  * CPU ABI
  * Device
  * Display Name
  * ID
  * Manufacturer
  * Model
  * (OS) Version Release
6. Sensors
  * Accelerometer
    * X/Y/Z m/s^2
  * Linear Accelerometer
    * X/Y/Z m/s^2
  * Gravity
    * X/Y/Z m/s^2
  * Orientation
    * X/Y/Z degrees
  * Gyroscope
    * X/Y/Z degrees/s
  * Magnetometer
    * Magnitude
    * Magnetic Field
    * Metal Near
  * Light
    * Lux
  * Proximity
    * Limit (CM) - Proximity Sensor Detection
  * Air Pressure
    * hPa
    * Altitude
  * Pedometer
    * X Steps Taken
7. Network
  * WiFi State
  * IP Address
  * Link Speed
  * Saved Networks
  * Nearby Networks
    * Signal Strength - Network Name
  * Phone Number
  * Network
  * Mobile State
  * Roaming
  * Signal Strength
  * Wifi
    * Sent Data (since last reboot)
    * Recieved Data (since last reboot)
  * Mobile
    * Sent Data (since last reboot)
    * Recieved Data (since last reboot)

---

###### All data listed here is simplified. To display advanced data, enable the options in settings.

---

## What and why?

As mentioned earlier, this is a simple application to analyse hardware components usage on a given device. The application will try to gather as much information as possible given out by hardware components. This project/application is just a base to refer to when needing information about certain aspects of the device.

This information is very useful, for example:

RAM/CPU/Battery to interpret device usage.

Storage to understand the device's capacity (along with how to access different media).

General device information to gather all data about the device.

Sensors to collect information about the ambient evironment and to process output from sensors such as the accelerometer/orientation/gyroscope to be used in games or to map out an augmented reality environment

Network information is key to a lot of applications (talking to servers, downloading content, etc).

Device's output a lot of information that is useful to developers. Refer to this project and its source code to find out how to practically gather this information.

---

## Screenshots

![Screenshot 1](https://lh5.ggpht.com/PrHNWkySQYFcT2Lox3h2Y2rqzRSJV33hWkPut5aoJLAXQlnUQvcIX6CsqizNzl2tikch=h310-rw "Screenshot 1")
![Screenshot 2](https://lh4.ggpht.com/-0hhSgoMCuztICAkTDwFfD0W12L6rECME6SVdSw3nh-EecaOQ8LmjFwVYc2jSl7btjw=h310-rw "Screenshot 2")
![Screenshot 3](https://lh3.ggpht.com/kL8kEelyUAjne5decsMq_-v3mEsefYL6z6wmhFHb7FQWmiFwdmYM3aG_5MfxrReGKEY=h310-rw "Screenshot 3")


![Screenshot 4](https://lh5.ggpht.com/rkx-2c0x6D_hnbZnSksbbkDrln4s21fEjC1WZvMZWUGhKyoByoQ-jQVsT9n5iW6aYw=h310-rw "Screenshot 4")
![Screenshot 5](https://lh5.ggpht.com/QulnnPCIcflU2BXvNFI1piLZ9KZJi1GNNJ8QcIOnXcqvgs6bwquxG5-xPD3Pff2QhLU=h310-rw "Screenshot 5")
![Screenshot 6](https://lh4.ggpht.com/9b41kS7jxRDF6Bz3zNLjo8Nk_53D2YmTAMUmw1GQbi3h4A4x_rK0OuydgXdqje5DfQ=h310-rw "Screenshot 6")

[Fullsize Image 1](https://lh5.ggpht.com/PrHNWkySQYFcT2Lox3h2Y2rqzRSJV33hWkPut5aoJLAXQlnUQvcIX6CsqizNzl2tikch=h900-rw),
[Fullsize Image 2](https://lh4.ggpht.com/-0hhSgoMCuztICAkTDwFfD0W12L6rECME6SVdSw3nh-EecaOQ8LmjFwVYc2jSl7btjw=h900-rw),
[Fullsize Image 3](https://lh3.ggpht.com/kL8kEelyUAjne5decsMq_-v3mEsefYL6z6wmhFHb7FQWmiFwdmYM3aG_5MfxrReGKEY=h900-rw),
[Fullsize Image 4](https://lh5.ggpht.com/rkx-2c0x6D_hnbZnSksbbkDrln4s21fEjC1WZvMZWUGhKyoByoQ-jQVsT9n5iW6aYw=h900-rw),
[Fullsize Image 5](https://lh5.ggpht.com/QulnnPCIcflU2BXvNFI1piLZ9KZJi1GNNJ8QcIOnXcqvgs6bwquxG5-xPD3Pff2QhLU=h900-rw),
[Fullsize Image 6](https://lh4.ggpht.com/9b41kS7jxRDF6Bz3zNLjo8Nk_53D2YmTAMUmw1GQbi3h4A4x_rK0OuydgXdqje5DfQ=h900-rw)

---

## Sources
**Android Open Source Project**, *by [Google](http://source.android.com)*

**Definition Documentation**, *by [Centos](https://www.centos.org/docs/5/html/5.1/Deployment_Guide/s2-proc-meminfo.html)*

**Sensory Information**, *by [Android Developer Docs](http://developer.android.com/guide/topics/sensors/sensors_overview.html)*

**Sensory Fusion**, *by [GoogleTechTalks](https://www.youtube.com/watch?v=C7JQ7Rpwn2k)*

**Circular Progress Bar**, *by [Pascal Welsch (passsy)](https://github.com/passsy/)*

**Sliding Menu**, *by [Jeremy Feinstein (jfeinstein10)](https://github.com/jfeinstein10)*

**Process Button**, *by [Dmytro Danylyk (dmytrodanylyk)](https://github.com/dmytrodanylyk/)*

**Roboto Text View**, *by [Evgeny Shishkin (johnkil)](https://github.com/johnkil/)*

**EazeGraph**, *by [Paul Cech (blackfizz)](https://github.com/blackfizz/)*

**Nine Old Androids**, *by [Jake Wharton (JakeWharton)](https://github.com/JakeWharton)*

**Pager Sliding Tap Strip**, *by [Andreas Stütz (astuetz)](https://github.com/astuetz)*
