# guireader
gui reader used for reading GUIs without interfering with memory access  
Currently, no config file, no variable pixelreads, no variable .mp3
very basic prototype

Quick start guide:  DEPRECATED, will add new instructions soon
Download the code
Open in the IDE of you choice (IntelliJ is preferred by me)
Only File you need to edit is Main.java
In line 65 edit the String to math the location and file which you want to be played  
In line 105 edit the parameters, best is to screenshot the complete screen and look the pixel values up in a image editor:    
First is the x coordinate of the object, x goes from left to right (Multimonitors are possible to use but they are a bit wierd)  
Second is the y coordinate, y goes from up to down  
X and Y coordinates are attributed to the top left pixel of your bar, set the object one pixel more to the right to get accurate 0% readings  
Third is the Width of the bar you want to be read  
Fourth is the height of the bar
Fifth is the resolution you want to have, note that this can't be higher than your width  
Once you edited these lines you can run the code  

Upon start the program will set the RGB value representing "on" after 3secs, so be sure the coordiates are right using the displayed image.  
The blue dots are where the RGB values are checked.  
If none of the probe pixels have the "on" value +-10, the before set .mp3 will play  
After it played, the program wont't play the .mp3 unless the reset button was played, this is to prevent menu changes to trigger the event  


TODO:  
Add variable coords, pref through drag & drop  
Re-add config file via JSON  
Make GUI more intuitive  
More logic to improve usability  
FileChooser to load config  
Second Bar to handle Inventory opening  
Make trigger more resilient, to avoid accidental triggers

Feel free to msg me on discord Caesar#4967  
If you want to use or modify this code be sure to credit me.
