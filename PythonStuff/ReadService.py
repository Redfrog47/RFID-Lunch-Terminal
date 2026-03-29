import socket
import time


from mfrc522 import SimpleMFRC522
import RPi.GPIO as GPIO

from gpiozero import Buzzer
from gpiozero import LED


HOST = "127.0.0.1"
PORT = 5000

reader = SimpleMFRC522()

buz = Buzzer(23)
led = LED(22)

lastCardToScan = -1
timeOfScan = time.time()

def connect() :
    while True:
        try:
            print("Attempting connection")
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.connect((HOST, PORT))
            print("Socket connected")

            led.blink(on_time=0.5, off_time=0.5, background=True)

            return sock
        except ConnectionRefusedError:
           print("Connection failed")
           time.sleep(3)

def handleBeep():
    buz.beep(on_time=0.4, off_time=0.5, n=1, background=True)


while True:
    try:
        sock = connect()
        print("Enter card ID")
        while True:
            id, text = reader.read()
            isNewCard = id != lastCardToScan
            isTimePassed = time.time() - timeOfScan > 10
            if isNewCard or isTimePassed:
                try:
                   data = int(text)
                except:
                    print("Failed to parse int")
                    continue
                message =  "P" + str(data) + "\n"
                lastCardToScan = id
                sock.sendall(message.encode())
                print("Sent: " + message)
                timeOfScan = time.time()
                handleBeep()
                print("Enter card ID")

    except (BrokenPipeError, ConnectionResetError):
        print("Lost connection")
        led.off()
    except (KeyboardInterrupt):
        break
    finally:
        led.off()
        GPIO.cleanup()
