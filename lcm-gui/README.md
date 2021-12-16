
# LCMGUI
LcmGUI is a java gui that allows for the user to filter the data recieved from multiple sensors to both the sensors they desire as well as the time window they wish to see data gathere within.
This GUI can also write data recieved from LCM messages into CSV files. GUI promts user with a choice between downloading all data or only the time-filtered data.
The names of the LCM channels are used in determining which sensors the data is from as well which file to write.
## To Run:
sudo apt-get install openjdk-8-jdk
sudo apt-get install openjdk-8-jre
You will need to have the LCM.jar as well as LcmGui.jar within your /usr/share/java folder
export CLASSPATH=/usr/share/java/*:.
javac Test.java
java Test


Select the sensors from which you would like data from using the checkboxes provided at the top right corner of the form.
Input the time window (assume 0 is takeoff) within which you would like data. Make sure all that is within the input box is mm.ss. You can have longer than 2 digits on either side.
Press the Submit button on the button right.

Displayed are the sensors' names and their data.
To Download this as a CSV click the buttons below the text area.
The button mentioning all data will give you all data with no regard to your time window. 
The button mentioning the time window will only export data within the time window specified.
### Warnings:
Will not output data if your CSV file is currently open.
Will not accept values other than numbers in input window. ex. 00.00-999.00
Must have at least 1 Chkbox ticked before submitting.
