import socket
import time

from mfrc522 import SimpleMFRC522
import RPi.GPIO as GPIO

from gpiozero import Buzzer


HOST = "127.0.0.1"
PORT = 5002
RECONNECT_DELAY = 3

reader = SimpleMFRC522()
buzzer = Buzzer(23)

def beep():
    buzzer.beep(on_time=0.3, off_time=0.2, n=1, background=True)


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

def writeLoop() :
    sock = connect()

    while True :
        try :
            data = sock.recv(1024)

            if not data:
                raise ConnectionResetError

            message = data.decode().strip()

            if message.startswith("Index"):
                try:
                    index = int(message[5:])
                except ValueError:
                    sock.sendall(b"IError\n")
                    continue
                if index >= 0:
                    write_index(index)
                    sock.sendall(b"ISaved\n")
                    print("Sent ISaved")
                else:
                    sock.sendall(b"IError\n")
        except (BrokenPipeError, ConnectionResetError):
                print("Write socket disconnected. Reconnecting...")
                sock.close()
                sock = connect()

        except Exception as e:
            print(f"Write loop error: {e}")

def write_index(index) :
    try:
            reader.write(str(index))
            beep()
    except Exception as e:
        print(f"Failed to write to card: {e}")

# Main entry ig
# I hate python
try :
    writeLoop()
finally :
    GPIO.cleanup()