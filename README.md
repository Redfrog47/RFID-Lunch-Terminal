# RFID-Lunch-Terminal

This project contains code to turn a Raspberry Pi with an RC522 RFID reader into an attendance terminal to keep track of who leaves and when

It is set up for the Pi to interace with a windows computer over a usb in usb-gadget mode

It also contains the code for a windows app to interface with the Pi

## Features

### Pi side features

The Pi internally handles saving scan times to a spreadsheet, along with the name and ID of the person who scanned

People are marked tardy in the spreadsheet if they return after the designated bell time

People are marked suspicious if they scan an odd number of times (implying that they signed out without signing back in, or vice-versa)

Names and IDs are stored locally on the Pi to avoid the security risk of having them saved on the RFID cards

When cards are scanned a buzzer plays a sound to give feedback

Three LEDs indicate the status of the Pi (See Usage)

### Windows side features

The windows app gives you the ability to interface with the data on the pi in many ways

It has two windows
- Lunch Terminal
  - Scan someone by their ID (if they don't have their card)
  - Pull current spreadsheet and save it to the windows computer
  - Open File Manager window
- Device Manager
  - Set up a card with a name and ID
  - Pull and save all spreadsheets to windows computer
  - Update designated lunch bell
  - Turn off the Pi
  - Delete data
     - Delete all spreadsheets on the Pi
     - Delete the key containing names and IDs on the Pi
     - Delete both spreadsheets and keys
  

## Installation

I have included zip files for the CardScanTerminal and the Pi image in [releases](https://github.com/Redfrog47/RFID-Lunch-Terminal/releases)

I have also included all the necessary files to set up your own Pi in Pi-config

### Using zip files

Card Terminal setup
- Download CardScanTerminal.zip
- Extract it to your desired destination
- To run the app open the extracted CardScanTerminal folder and run CardTerminal.exe

Pi setup
- Download rfid-image.zip
- Download the [official raspberry pi imager](https://www.raspberrypi.com/software/)
- Extract the rfid-image zip file
- Use the raspberry pi imager to flash a micro sd card by following the prompts and selecting to use rfid-pi.img as a custom image
- Once flashed you should be able to plug the micro sd card into a pi, and it will boot straight into working as the card terminal

 ### Manual configuration

 #### Card Terminal setup
 - Clone the repository
 - Create an app-image using the below instructions

To create an app-image from in the Java-windows directory:

    javac *.java

    jar cfe app.jar Main *.class icon.png

    mkdir package

    mv app.jar package

    jpackage --name CardTerminal --input package --main-jar app.jar --main-class Main --type app-image --icon icon.ico

#### Pi setup

Set up a pi with Pi OS Lite with the user called "pi" and an internet connection (it is omportant to remove this connection before rebooting)

Enable the SPI and I2C Interfaces in `raspi-config`

    cd /home/pi

    sudo apt update

    sudo apt install git

    git clone https://github.com/Redfrog47/RFID-Lunch-Terminal.git
    
    cd /home/pi/RFID-Lunch-Terminal/Pi-config/services
    
    sudo cp *.service /etc/systemd/system
    
    sudo systemctl daemon-reload
    
    sudo systemctl enable java_app.service usb-gadget.service rfid-reader.service status-checker.service
    
    cd ../shell
    
    sudo cp usb-gadget.sh /usr/bin
    
    sudo chmod +x /usr/bin/usb-gadget.sh
    
    sudo apt install python3-dev python3-rpi.gpio

    cd /home/pi

    python3 -m venv rfid-venv
    
    source /home/pi/rfid-venv/bin/activate
    
    pip install mfrc522 gpiozero

    deactivate

    sudo apt install default-jdk

    cd RFID-Lunch-Terminal/Java-pi

    javac *.java

    jar cfe app.jar Main *.class
    
    sudo apt install dnsmasq
    
    sudo rm /etc/dnsmasq.conf
    
    cd /home/pi/RFID-Lunch-Terminal/Pi-config/config
    
    sudo cp dnsmasq.conf /etc
    
    sudo systemctl daemon-reload
    
    sudo systemctl enable dnsmasq

    cd /boot/firmware

    sudo rm config.txt cmdline.txt

    cd home/pi/RFID-Lunch-Terminal/Pi-config/config

    sudo cp config.txt cmdline.txt /boot/firmware

    sudo apt install util-linux-extra

    sudo reboot

  Notes:
  - After you set up the Pi, take it off the internet with `sudo nmtui` to avoid issues with dates and times
  - When enabling the services we intentionally don't enable rfid-writer.service
  - Replacing cmdline.txt directly with the one from the repository is not actually a good idea
    - Instead you should just edit the text to have all the same arguments, but keep the origional PARTUUID number
    - I have been doing it by replacing it though, and if you do that you will need to find the PARTUUID and replace it externally
    - If you have acces to another linux machine you can run `lsblk -o PARTUUID,NAME` with the micro sd card inserted
    - Then find the one that ends in -02 and replace the PARTUUID in cmdline.txt with it
    - If you dont do this, the pi will not boot
   
   ## Usage
   ### Pi
   The Pi boots straight into functioning as the scan terminal

   It is intended to be left plugged in all the time

   The Pi does not have a way to sync time automatically, so it relies on the app to do it

   After plugging in the Pi the time must be synced by openening the app (you don't need to push any buttons because it syncs automatically, but make sure to open the app after the LEDs come on)

   The LEDs attached to the pi provide information about the status of the Pi
   - Blinking green LED means the Pi is working in read mode
   - Solid green LED means the Pi is working in write mode
   - Blue LED being on means the app is connected
   - Red LED indicates a problem

   Red LED
   - Red LED means the Pi has broken down internally, and you should reboot it

   ### App

   The app can be used control the Pi and it's data as described in Features

   ## Hardware

   This poject uses an MFRC522 rfid read/write module
   
   It also uses a buzzer connected to GPIO 23 and LEDs connected to GPIO 22, 24, and 27

   A usb cable can be used to connect the Pi to a windows computer

   ## Structure

   When using the CardTerminal app, spreadsheets are saved to C:\Users\user\AppData\Roaming\CardTerminal\Spreadsheets

   Internally on the Pi the spreadsheets are saved to /home/pi/RFID-Lunch-Terminal/Java-pi/Spreadsheets

   The data key representing names and IDs is called "key.csv" and is in /home/pi/RFID-Lunch-Terminal/Java-pi

   For security reasons, the Pi only saves an index to an rfid card representing the persons location in the key.csv key file

   ## Notes

   I left ssh on in the image of the pi, and you can use it by running `ssh pi@192.168.7.2`

   The reason this project sends spreadsheets over usb at all is because my school didn't want the Pi to be connected to wifi

   This whole thing would have been a lot easier if I could have just used googles API to save to a google sheet

   I definetly could have structured this project better, and I'm sure it is horribly inefficient, but it is my first project like this and I finished it, so I don't care

   Using Pi OS Lite for this project was my first experience with linux, and in the time it took to finish it, I have installed linux on 4 of the computers in my house 🤣

   This project is definitely fixing an already solved issue, but it ended up being fairly cheap for what it is which is good I guess

   It definitely should not have taken months though 😭
