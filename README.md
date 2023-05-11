# Note
This code isn't perfect, it was made to be written quick and refactored afterwards, refactoring has started but it's still a WIP

# Installation

- ````mkdir ~/projects````
- install rpi os on sd card ( Needs to be 32bit for FBCP display driver)
- ````sudo raspi-config```` and activate I2C + SPI + SSH + VNI under ````interface options```` and legacy driver under ````advanced options -> GL Driver````
- Deactivate screen blanking
- ````sudo apt install openjdk-8-jdk git guake cmake p7zip-full -y````
- ````sudo nano /boot/config.txt```` and add  ```
  disable_splash=1
  dtoverlay=pi3-disable-bt
  boot_delay=0```
- ````sudo nano /boot/cmdline.txt```` and change ````tty=1```` to ````tty=3```` and add ````quiet loglevel=3 quiet logo.nologo nosplash cma=375M```` after ````rootwait```` or replace if they exist with different values
- ````systemctl disable ModemManager.service && systemctl disable hciuart.service````
-
````shell
sudo dphys-swapfile swapoff
sudo nano /etc/dphys-swapfile
sudo dphys-swapfile setup
sudo dphys-swapfile swapon
````
- fix desktop env ( make bar small, set background, make bar auto hide, remove icons from desk+bar, disallow panel reserving space from maximized windows )
- install battery + display drivers (Instructions below)
- install this program as a service by installing ````./src/main/resources/cameracontroller.service```` as
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

## 1.3 inch display drivers

Raspi-config and enable SPI-config
````shell
mkdir ~/Downloads/
````
````shell
cd ~/Downloads
wget http://www.airspayce.com/mikem/bcm2835/bcm2835-1.71.tar.gz
tar zxvf bcm2835-1.71.tar.gz 
cd bcm2835-1.71/
sudo ./configure && sudo make && sudo make check && sudo make install
# For more, you can refer to the official website at: http://www.airspayce.com/mikem/bcm2835/
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
Notice that the cmake below differs from the one on waveshares instructions because -DBACKLIGHT_CONTROL=OFF is flagged as off
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

- Do ````sudo nano /boot/config.txt```` And add
````
hdmi_force_hotplug=1
hdmi_cvt=300 300 60 1 0 0 0
hdmi_group=2
hdmi_mode=87
display_rotate=0
````

# UPS HAT

````shell
cd ~/Downloads/
wget https://www.waveshare.com/w/upload/4/40/UPS_HAT_C.7z
7zr x UPS_HAT_C.7z -r -o./
cd UPS_HAT_C
python3 INA219.py
````
