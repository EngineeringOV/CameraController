# Note
- This code isn't perfect, it was made to be written quick and refactored afterwards, refactoring is in progress
- Video doesn't work yet, it worked with the zero 1 but after upgrading to zero 2 it only allows video capture of at most 380x380 which is a garbage resolution, this is probably related to changes in memory settings etc and needs to be explored but should be fixable.

# BOM ( todo Update with links)

- Raspberry Pi Zero 2 (Zero 1 could probably work but you'd have to fork and remove some more CPU and Memory instense features)
- SD card
- Raspberry HQ camera module (Or other similar)
- 3d printer (or baseplate for Module+Raspberry)
- Waveshare 1.3 inch display HAT
- Waveshare UPS HAT (C) (That one that is the size of a Raspberry pi zero)
- M2.5 screws and standoffs
- Any C-mount camera lens you want to use (Or an adapter to other mount)
- ***Optional*** tripod mount with screw for Camera-module

# Installation

- ````mkdir ~/projects````
- install rpi os on sd card ( Needs to be 32bit for FBCP display driver)
- ````sudo raspi-config```` and activate I2C + SPI + SSH + VNI under ````interface options```` and legacy driver under ````advanced options -> GL Driver````
- Deactivate screen blanking
- Save settings and exit raspi-config
- ````sudo apt install openjdk-8-jdk git guake cmake p7zip-full -y````
- ````sudo nano /boot/config.txt```` and add  ```
  disable_splash=1
  dtoverlay=pi3-disable-bt
  boot_delay=0```
- ````sudo nano /boot/cmdline.txt```` and change ````tty=1```` to ````tty=3```` and add ````quiet loglevel=3 quiet logo.nologo nosplash cma=375M```` after ````rootwait```` or replace if they exist with different values
- ````systemctl disable ModemManager.service && systemctl disable hciuart.service````
- Set your swap memory by

1: 
````shell
sudo dphys-swapfile swapoff
sudo nano /etc/dphys-swapfile
````
and write the amount (todo default amount)

2: Set Save and Exit and finish it by  
````
sudo dphys-swapfile setup
sudo dphys-swapfile swapon
````
- fix desktop env ( make bar small, set background, make bar auto hide, remove icons from desk+bar, disallow panel reserving space from maximized windows )
- install  display drivers (Instructions below) and ***Optional*** battery code examples 

## 1.3 inch display drivers

Raspi-config and enable SPI-config
````shell
mkdir ~/Downloads/
````
````shell
cd ~/Downloads
wget https://www.airspayce.com/mikem/bcm2835/bcm2835-1.71.tar.gz
tar zxvf bcm2835-1.71.tar.gz 
cd bcm2835-1.71/
sudo ./configure && sudo make && sudo make check && sudo make install
# For more, you can refer to the official website at: https://www.airspayce.com/mikem/bcm2835/
````
````shell
cd ~/Downloads
git clone https://github.com/WiringPi/WiringPi
cd WiringPi
./build
gpio -v
````
````shell
sudo apt-get update
sudo apt-get install ttf-wqy-zenhei
sudo apt-get install python3-pip
sudo pip3 install RPi.GPIO
sudo pip3 install spidev
````
Notice that the cmake below differs from the one on WaveShares instructions because -DBACKLIGHT_CONTROL=OFF is flagged as off
````shell
cd ~/Downloads/
wget https://www.waveshare.com/w/upload/f/f9/Waveshare_fbcp.7z
7z x Waveshare_fbcp.7z -o./waveshare_fbcp
cd waveshare_fbcp
mkdir build
cd build
cmake -DSPI_BUS_CLOCK_DIVISOR=20 -DWAVESHARE_1INCH3_LCD_HAT=ON -DBACKLIGHT_CONTROL=OFF -DSTATISTICS=0 ..
make -j
````
````shell
sudo cp ~/Downloads/waveshare_fbcp/build/fbcp /usr/local/bin/fbcp
sudo nano /etc/rc.local
````
Then add ````fbcp&```` before exit 0

##Setting up config

- Do ````sudo nano /boot/config.txt```` And at the bottom add
````
#Speeds up boot
disable_splash=1
dtoverlay=disable-bt
boot_delay=0

#Sets display settings for our fancy display
hdmi_force_hotplug=1
hdmi_cvt=300 300 60 1 0 0 0
hdmi_group=2
hdmi_mode=87
display_rotate=0

#Makes sure the wifi doesn't crash as it can running with air cooling
arm_freq=600
gpu_freq=300
sdram_freq=400

#Leaves enough RAM for camera as camera doesn't share with GPU_MEM.
gpu_mem=32

#Sets the right camera driver
dtoverlay=imx477,media-controller=0
````

## Compile
- Copy ```src/main/resources/config.properties.default``` and to ```src/main/resources/config.properties``` and fill it out as you want it
- Compile this program
- Put the resulting JAR in ~/projects
- Install this program as a service by installing ````./src/main/resources/cameracontroller.service```` as
  ```` /etc/systemd/system/cameracontroller.service````
  and then start it by running
````shell
sudo systemctl enable cameracontroller.service
sudo systemctl start cameracontroller.service
````
and if you're having problems you can see logs by running
````shell
sudo journalctl -u cameracontroller.service -f
````
And then in the terminal do
````bash
sudo reboot
````
### If the camera starts correctly then you did everything correctly and you're now done
