

##### Running the program:
1. Ensure Java 17 is installed on your computer.
2. Using your preferred IDE, perform the Gradle build and then run it through Gradle.

##### Adjusting the time for each time-step: 
Navigate to the GameRunner file, and locate the line where the Timeline is initialised, around line 186. Here you will see a Duration.millis(x). Change that x value to your desired milliseconds per time-step.

##### Using the GUI:
The user interface is quite self-explanatory. First, you can pick the game-mode: exploration or guards versus. intruders. Next you can pick the type of agent: a random one, or our stronger agent. After making that choice, the user interface will initialise.

##### Using a different map:
There are three maps given with the simulation. To use a different map, place the map txt file in the src/main/resources folder. Next, go to the GameRunner class, line 94 and change it to String mapD = “YOUR_MAP_NAME.txt”.

