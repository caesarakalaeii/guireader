# guireader
gui reader used to read GUIs without interfering with memory access  

Project Structure:
https://imgur.com/a/13PhufC
Known issues:
RGB Spinner won't set RGB


Quick start guide:  
Download the code  
Open in the IDE of you choice (IntelliJ is preferred by me)  

Upon start click on File in the top right and load both a screenshot and a soundfile.  
Then click the Button "New reader", it will show a white rectangle with dots.  
The white dots are where the RGB values are checked.  
This is where your screen readout will happen.  
Edit the position with the X-Coord and Y-Coord spinners, as well as the width and height with the accordingly named spinners.  
The resilience spinner will declare how much the RGB value may differ from the set "on" value.  
Resolution will set the resolution of the bar, and set the dots in a regular pattern.  
Theshold will declare when the logic returns a "true".  
Once the set button is pressed the bar cannot be changed or deleted.  
It will save the RGB value of the most left or up probe as the default "on" value.  
Through the choice boxes you can build a logic analyzer, so multiple bars can be combined throu a series of simple logic parameters.  
To set the type of alert choose in the according choice box and clock "Set Alert".  
If the logic set triggers through, the before set mp3 will play.  
After it played, the program wont't play the .mp3 unless the reset button was played, this is to prevent menu changes to trigger the event  


TODO:  
✓ Add variable coords, pref through drag & drop  (Not drag and drop but hey)  
✓ Make GUI more intuitive  (better? yeah, good? no)  
✓ More logic to improve usability    
✓ Second Bar to handle Inventory opening  (as many as you like baby)  
implement an actual Logger
Re-add config file via JSON    
FileChooser to load config  
Make trigger more resilient, to avoid accidental triggers  

Feel free to msg me on discord Caesar#4967  
If you want to use or modify this code be sure to credit me.
