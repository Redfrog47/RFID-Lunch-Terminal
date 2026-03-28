#!/bin/bash
cd /sys/kernel/config/usb_gadget/
mkdir -p pi
cd pi

echo 0x1d6b > idVendor
echo 0x0104 > idProduct
echo 0x0100 > bcdDevice
echo 0x0200 > bcdUSB

mkdir -p strings/0x409
echo "0123456789" > strings/0x409/serialnumber
echo "PiZero2" > strings/0x409/manufacturer
echo "USB Ethernet" > strings/0x409/product

mkdir -p configs/c.1/strings/0x409
echo "Config 1" > configs/c.1/strings/0x409/configuration
echo 250 > configs/c.1/MaxPower

mkdir -p functions/rndis.usb0
HOST="02:00:00:00:00:01"
SELF="02:00:00:00:00:02"
echo $HOST > functions/rndis.usb0/host_addr
echo $SELF > functions/rndis.usb0/dev_addr

echo 1 > os_desc/use
echo 0xcd > os_desc/b_vendor_code
echo MSFT100 > os_desc/qw_sign

mkdir -p functions/rndis.usb0/os_desc/interface.rndis
echo RNDIS > functions/rndis.usb0/os_desc/interface.rndis/compatible_id
echo 5162001 > functions/rndis.usb0/os_desc/interface.rndis/sub_compatible_id

ln -s functions/rndis.usb0 configs/c.1/
ln -s configs/c.1 os_desc

ls /sys/class/udc > UDC

while ! ip link show usb0 &>/dev/null; do
	sleep 0.5
done

ip link set usb0 up

ip addr add 192.168.7.2/24 dev usb0

sleep 2

systemctl restart dnsmasq
