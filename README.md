# MINECRAFT STATISTICS EXPLORER
By Wiktor Tomczak 160069

The program provides an interface to see statistics of players from a minecraft world.

##Requirements:
The program was created in Windows 10, Java version 22.0.1, should work for java version 11+

The program requires an internet connection (to load player nicknames from the Mojang api). Sometimes Mojang servers are not working.

To run the program, use the ready `minecraftStatExplorer.jar` file, or compile manually:
Open terminal in directory containing `src` folder
run commands:

`javac -d out src/*.java`

`jar cfe minecraftStatExplorer.jar Main -C out .`

Make sure the stats folder with .json files is in the same directory as the .jar file !!! (example stats folder is given)

To run, open the jar file or run command `java -jar minecraftStatExplorer.jar`



##Explanation:
Minecraft stores player statistics inside files UUID.json located in the world folder, where UUID is a unique constant id assigned to a player.
Using an API provided by Mojang, the player's nickname can be obtained from the UUID.
In the UUID.json file, there are all statistics of the player divided into 9 categories.

This program reads all statistics from the files and organizes them.
The main window is separated into scrollable tabs, each for different category.
There all statistics are displayed, along with the highest scorer.
Clicking on a statistic opens a window showing all players and their score in that statistic.
Clicking on a column label sorts according to that column.
