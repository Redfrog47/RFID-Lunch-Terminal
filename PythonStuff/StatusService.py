import socket
import time

import RPi.GPIO as GPIO
from gpiozero import LED

HOST = "127.0.0.1"
PORT = 5000
RECONNECT_DELAY = 3

red = LED(24)
blue = LED(27)

def connect() :
     while True:
        try:
            print("Attempting connection")
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.connect((HOST, PORT))
            print("Socket connected")
            
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

            if message.startswith("Synced"):
                red.off()

            if message.startswith("Connected"):
                blue.on()

            if message.startswith("Disconnected"):
                blue.off()

        except (BrokenPipeError, ConnectionResetError):
                print("Status socket disconnected. Reconnecting...")
                sock.close()
                sock = connect()

        except Exception as e:
            print(f"Status loop error: {e}")

# Main entry thing
# I still hate python
try :
    red.on()
    statusLoop()
finally :
    GPIO.cleanup()