## feature ideas
- delayed shots?
- Pixel shifting feature
- sound when taking photo
- audio recording when filming
- auto dim screen, auto bright screen
- toggle Wi-Fi from menu, default to off to save battery
- disable boot to desktop (https://forums.raspberrypi.com/viewtopic.php?t=42888 might have good info)

https://himeshp.blogspot.com/2018/08/fast-boot-with-raspberry-pi.html
https://forums.raspberrypi.com//viewtopic.php?t=195692
https://elinux.org/images/6/64/Chris-simmonds-boot-time-elce-2017_0.pdf


# Recurring bugs
## Solved
#### Waveshare 1.4 inch Screen not starting
- Wrong driver enabled
  - ``sudo Raspi-Config `` -> ``Advanced Settings`` -> ``GL Driver`` -> ``Legacy Drivers`` -> ``Yes`` 
  
#### Wifi dying randomly
- Overheating
  - Underclock Raspberry Pi
  - Fan mod
  - Heatsink
- Underpowered
  - Get better UPS/BMS/power brick
- DHCP leash timer to long
  -  Set shorter DHCP leash time on router

#### Camera not outputing image with error "``Camera frontend has timed out! [...] Device timeout detected``"
- ``viewfinder-width`` / ``viewfinder-height`` set
  - Remove from commandline (in this code remove from ``CameraStringBuilder.java`` builder)
  
#### Camera not outputing image with error "``ERROR: *** failed to allocate capture buffers ***``"

- Lack of free RAM (CMA)
  - Uninstall bloatware
  - Use a RPI with more RAM
  - Increase it in /boot/cmdline.txt
  - Use page file
  - Decrease image resolution

#### Camera not outputing image with error "``ERROR: *** no cameras available ***``"
- Broken camera cable
  - Replace it
- Camera was hotplugged
  - Reboot


