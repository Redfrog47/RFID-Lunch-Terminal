import socket
import time

import RPi.GPIO as GPIO
from gpiozero import LED

HOST = "127.0.0.1"
PORT = 5000
RECONNECT_DELAY = 3

red = LED(24)
green = LED(22)
blue = LED(27)

def connect() :
     while True:
        try:
            print("Attempting connection")
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.connect((HOST, PORT))
            print("Socket connected")

            green.blink(on_time=0.4, off_time=1.0, background=True)
            
            return sock
        except ConnectionRefusedError:
           print("Connection failed")
           time.sleep(RECONNECT_DELAY)

def statusLoop() :
    sock = connect()

    while True :
        try :
            data = sock.recv(1024)

            if not data:
                raise ConnectionResetError

            message = data.decode().strip()

            print(message)

            if message == "Synced":
                red.off()

            if message == "Connected":
                blue.on()

            if message == "Disconnected":
                blue.off()

        except (BrokenPipeError, ConnectionResetError):
                print("Status socket disconnected. Reconnecting...")
                sock.close()
                sock = connect()
                green.off()

        except Exception as e:
            print(f"Status loop error: {e}")

# Main entry thing
# I still hate python
try :
    red.on()
    statusLoop()
finally :
    GPIO.cleanup()