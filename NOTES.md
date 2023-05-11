

## ideas
- support for different timelapse modes, shorter or longer gaps between images, longer or shorter run times
- shutter, gain, sharpness, contrast control, delayed shots?
- sound when taking photo
- audio when filming
- only show settings relevant to current camera mode
- auto dim screen, auto bright screen
- toggle Wi-Fi from menu, default to off to save battery
- disable desktop (https://forums.raspberrypi.com/viewtopic.php?t=42888 might have good info)

https://himeshp.blogspot.com/2018/08/fast-boot-with-raspberry-pi.html
https://forums.raspberrypi.com//viewtopic.php?t=195692
https://elinux.org/images/6/64/Chris-simmonds-boot-time-elce-2017_0.pdf

# Recurring bugs
## Solved
### Screen not starting
- Reason 1: ``Advanced Settings -> Legacy Drivers`` have to be enabled in ``sudo Raspi-Config``
### Libcamera freezes during timelapse, no more stills taken (3 - 10 min after start)
- Cause: output buffer filling up
- Reason 1: Only reading from process Standard Output buffer and not Standard error output buffer. (Solved by looping through Standard Error aswell)
- Reason 2: Output buffer only being read once whilst output is continous (Solved by using thread)
- Reason 3: The way Libcamera works internally writes is to continously to the first line of Standard Out and this basically freezes any attempt to read it like ```reader.readLine()```. If you then call to read Standard Error in the same thread it will not be reached as the thread is frozen.  This will cause the Standard Error buffer to clog and this eventually causes a the process to freeze
- Reason 4: If the verbose flag is disabled and output is not thusly consumed


# aliases
alias brc="nano ~/.bashrc && source ~/.bashrc"
alias resetcc=' sudo systemctl stop cameracontroller.service && sudo systemctl daemon-reload && sudo systemctl start cameracontroller.service && sudo journalctl -u cameracontroller.service -f'
alias battery='python3 ~/Downloads/UPS_HAT_C/INA219.py'

# CASE

- unscrew mount screws and attatch front, then rotate the mount before putting it back on.









https://raspberrypi.stackexchange.com/questions/27475/wifi-disconnects-after-period-of-time-on-raspberry-pi-doesnt-reconnect