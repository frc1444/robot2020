# Vision Setup
Just some notes for [robot2020-vision](https://github.com/frc1444/robot2020-vision)

We use a Raspberry Pi for vision processing

---
Password:
* `lightning`. Yep, that's the password. What are you gonna do, hack our robot and log into our pi?

Static ip:
* Might be 10.134.223.157
* Should be 10.14.44.5 when connected to robo radio
* Edit file `/etc/dhcpcd.conf`

Disable WiFi:
* Edit `/boot/config.txt`
  ```
  dtoverlay=disable-wifi
  dtoverlay=disable-bt
  ```
* https://raspberrypi.stackexchange.com/a/62522
