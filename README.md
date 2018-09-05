# Indoor-Positioning-System

The purpose of the project is to monitor a patient's position in a room and start an alarm if the patient does not move from a specific area for an extended period of time.

There are 3 "towers", 2 of which send "encrypted" information about the monitored device's Wi-Fi signal strength to the third, which is also the server.
The server translates the signal strength into meters and performs trilateration to find the position of the device, which is then displayed in a GUI.
If enough time passes and the device is still considered to be in a danger zone, an alarm goes off. #TEST
