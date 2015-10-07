# Alarm Server

## Requirements on phone
WiFi should always be connected.
Android 2.3 and earlier: Settings -> Wireless -> Wifi Settings -> Menu -> Advanced -> WiFi Sleep Policy -> Never
Android 4.0 and later: Settings > WiFi -> Menu -> Advanced -> Keep Wi-Fi on during sleep -> Always


## GET http://<ip>:8080/start
Will set an alarm to sound after 10 minutes. This alarm can be stopped (before sounding and after sounding) by calling /stop. If stop is not called, it will stop automatically after 5 minutes.

## GET http://<ip>:8080/stop
Will stop a pending or running alarm.