

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


# aliases
alias resetcc='sudo systemctl stop cameracontroller.service && sudo systemctl daemon-reload && sudo systemctl start cameracontroller.service && sudo journalctl -u cameracontroller.service -f'

# CASE

- unscrew mount screws and attatch front, then rotate the mount before putting it back on.








# Recurring bugs
## Solved
### Screen not starting
- Reason 1: ``Advanced Settings -> Legacy Drivers`` have to be enabled in ``sudo Raspi-Config``
### Wifi dying randomly
- Reason 1: Overheating
- Solution: Underclock (TODO writeup)
https://raspberrypi.stackexchange.com/questions/27475/wifi-disconnects-after-period-of-time-on-raspberry-pi-doesnt-reconnect