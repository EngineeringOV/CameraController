# Note
- This code isn't perfect, it was made to be written quick and refactored afterwards, refactoring is in progress
- Video doesn't work yet.
<details>
<summary>Click to expand RPI Zero README!</summary>


# BOM ( todo Update with links)


  ```
  Reccomended
  [Arducam 64MP Camera module]
  ```
 __or__
  ```
  Not recomended (Screen to small on default build)
  [Raspberry HQ camera module] 
  [C or CS-mount lens]
  [optionally, a tripod] 
  ```
- ```[Raspberry Pi Zero 2]``` (Zero 1 might work)
- ```[SD card]```
- ```[Waveshare 1.3 inch display HAT]```
- ```[Waveshare UPS HAT (C)]``` (That one that is the size of a Raspberry pi zero)
- ```[Pin header]``` (for solderfree you want to dremel pins to keep battery safe)
- ```[M2.5 screws and standoffs]```
- ```[Heat sink]```
- ```[3d printer]``` (or camera mount)

# Installation


- Install RaspberryPi OS on a SD card (Bullseye 32bit)
- Deactivate ```screen blanking``` by running```sudo raspi-config``` -> ```Display Options``` -> ```D4 Screen Blanking``` -> ```<No>```

- #### Install CameraController (this repository)
```bash
sudo apt update -y && sudo apt upgrade -y
sudo apt install openjdk-8-jdk git -y

mkdir ~/projects
cd ~/Downloads/
git clone https://github.com/EngineeringOV/CameraController.git
cd CameraController
sudo cp ./src/main/resources/cameracontroller.service /etc/systemd/system/cameracontroller.service
sudo cp ./src/main/resources/default.config.properties ~/projects/config.properties
sudo bash gradlew jar
sudo cp ./build/libs/cameraController-1.jar ~/projects/cameraController-1.jar
sudo systemctl enable cameracontroller.service
sudo systemctl start cameracontroller.service
#sudo journalctl -u cameracontroller.service -f
sudo nano ~/projects/config.properties

sudo reboot
```
- #### Setup environment, optional but not really 
````bash
sudo apt install guake cmake p7zip-full zsh -y
# disable services that are slow to boot and use a lot of power
systemctl disable ModemManager.service
systemctl disable hciuart.service

# Enable I2C and SPI
sudo bash -c 'echo -e "dtparam=i2c_arm=on\ndtparam=spi=on" >> /boot/config.txt'
# Autohide taskbar
sudo sed -i "s/autohide=.*/autohide=1/" /etc/xdg/lxpanel/LXDE-pi/panels/panel

# Set swap size 
sudo dphys-swapfile swapoff
echo "CONF_SWAPSIZE=1024" > /etc/dphys-swapfile
sudo dphys-swapfile setup
sudo dphys-swapfile swapon

# Effectivised CMDline
sudo sed -i -e 's/\bconsole=tty[0-9]\+/console=tty3/' \
            -e '/rootwait/!b;s/\brootwait\b/& quiet loglevel=3 quiet logo.nologo nosplash cma=375M/' \
            -e 's/quiet[^ ]*//g;s/loglevel=[^ ]*//g;s/logo\.nologo//g;s/nosplash//g;s/cma=[^ ]*//g' \
            -e '/rootwait/s/$/ quiet loglevel=3 quiet logo.nologo nosplash cma=375M/' /boot/cmdline.txt


#todo disable wifi power savings mode
#todo install oh my zsh
# todo split up and automate config.txt additions
````

- install  display drivers (Instructions below) and ***Optional*** battery code examples 

### If the camera starts correctly then you did everything correctly and you're now done

## 1.3 inch display drivers

Raspi-config and enable SPI-config
````bash
#Update and install required libs
sudo apt install ttf-wqy-zenhei python3-pip cmake -y
sudo pip3 install RPi.GPIO
sudo pip3 install spidev

# bcm
mkdir ~/Downloads/
cd ~/Downloads
wget https://www.airspayce.com/mikem/bcm2835/bcm2835-1.71.tar.gz
tar zxvf bcm2835-1.71.tar.gz 
cd bcm2835-1.71/
sudo ./configure && sudo make && sudo make check && sudo make install
# For more, you can refer to the official website at: https://www.airspayce.com/mikem/bcm2835/

# wiring pi
cd ~/Downloads
git clone https://github.com/WiringPi/WiringPi
cd WiringPi
./build
gpio -v

# fbcp (Display driver)
#Notice that the cmake below differs from the one on WaveShares instructions because -DBACKLIGHT_CONTROL=OFF is flagged as off
cd ~/Downloads/
wget https://www.waveshare.com/w/upload/f/f9/Waveshare_fbcp.7z
7z x Waveshare_fbcp.7z -o./waveshare_fbcp
cd waveshare_fbcp
mkdir build
cd build
cmake -DSPI_BUS_CLOCK_DIVISOR=20 -DWAVESHARE_1INCH3_LCD_HAT=ON -DBACKLIGHT_CONTROL=OFF -DSTATISTICS=0 ..
make -j

sudo cp ~/Downloads/waveshare_fbcp/build/fbcp /usr/local/bin/fbcp
#Make fbcp autostart on boot
if ! grep -q "fbcp&" /etc/rc.local; then
  sudo sed -i '/^exit 0$/s/^exit 0$/fbcp\&\n&/' /etc/rc.local
fi
````

## Setting up config

- Do ````sudo nano /boot/config.txt```` And at the bottom add
````bash
# Speeds up boot
disable_splash=1
dtoverlay=disable-bt
boot_delay=0

# Sets display settings for our fancy display
hdmi_force_hotplug=1
hdmi_cvt=300 300 60 1 0 0 0
hdmi_group=2
hdmi_mode=87
display_rotate=0

# Makes sure the wifi doesn't crash as it can running with air cooling
#arm_freq=600
#gpu_freq=300
#sdram_freq=400

# Leaves enough RAM for camera as camera doesn't share with GPU_MEM.
gpu_mem=32

#Sets the right camera driver
````

HQ Camera Module
```bash
dtoverlay=imx477,media-controller=0
```
Arducam 64MP
```bash
todo fix
```

## (Optional development example code) UPS HAT
```shell
cd ~/Downloads/
wget https://www.waveshare.com/w/upload/4/40/UPS_HAT_C.7z
7zr x UPS_HAT_C.7z -r -o./
cd UPS_HAT_C
# python3 INA219.py
```
</details>

