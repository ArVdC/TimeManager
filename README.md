![TimeManager](http://imageshack.com/a/img922/9061/ECwdWj.png "TimeManager")

## Bukkit plugin for time management and display


### TIME MANAGING FUNCTIONALITIES
Define a start time and a speed modifier per world. Set a suitable refresh rate for the performance of your server.

Speed could be increased/decreased up to 20 times or match UTC time with offset to local time.

Day and night can be set to different speed values, which can be great for RPG or some mini-games.

Worlds list is actualized and timers are synchronized on each server startup and reload.

Time and speed can be modified with in-game commands or reloading after manually changes.

Sleep can be authorized, forbidden or linked with some other worlds.

A specific time offset can be defined for each player.

TimeManager can schedule commands that run at a time specified in the cmds.yml file. Scheduled commands can use the placeholders described below, with the exception of {tm_player}.

This plugin override the vanilla '/time' command. The command to change a single world timer is '/tm set time \[ticks|daypart|HH:mm:ss] \[world]'.

### SLEEP MANAGING FUNCTIONALITIES
TimeManager allows you to manage sleep: number of players required, animation during night skip, synchronization between worlds when waking up, etc.

### PLAYER COMMAND /now \<display> \<world>
A single command is used to display a custom message with the time, the date, the number of elapsed days or weeks, or many other placeholders.

/now messages support multi-language and could automatically be accorded to any player's locale available in the lang.yml file.

Using the permissions, you can permit players to choose the display and/or the world argument or neither of the two.

Display argument can be : 'msg', 'title' or 'actionbar'.

Nether and the End worlds could have a specific message.

Hexadecimal colors can be used in the YAML files.

### PLACEHOLDERS
The available placeholders are as follows :
- {tm_player} : Displays the name of the player.
- {tm_world} : Displays the name of the world.
- {tm_tick} : Displays the current tick.
- {tm_time12} : Displays the current time in hh:mm:ss format. (01:00:00 → 12:59:59)
- {tm_time24} : Displays the current time in HH:mm:ss format. (00:00:00 → 23:59:59)
- {tm_hours12} : Displays the current hour value in 2 digits. (1 → 12)
- {tm_hours24} : Displays the current hour value in 2 digits. (0 → 23)
- {tm_minutes} : Displays the current minutes value in 2 digits. (0 → 59)
- {tm_seconds} : Displays the current seconds value in 2 digits. (0 → 59)
- {tm_ampm} : Displays the current *AM* or *PM* part of the day.
- {tm_daypart} : Displays the name of the current part of the day, among the four existing ones, in each of the languages.
- {tm_currentday} : Displays the number of the current day. (1 → ∞)
- {tm_elapseddays} : Displays the number of elapsed day(s). (0 → ∞)
- {tm_dayname} : Displays the name of current day, based on entries in the lang.yml file.
- {tm_yearday} : Displays the number of the day in the year. (1 → 365)
- {tm_yearweek} : Displays the number of the week in the year. (1 → 52)
- {tm_week} : Displays the number of elapsed weeks. (1 → ∞)
- {tm_monthname} : Displays the name of current month, based on entries in the lang.yml file.
- {tm_dd} : Displays the day part of the date in 2 digits.
- {tm_mm} : Displays the month part of the date in 2 digits.
- {tm_yy} : Displays the year part of the date in 2 digits.
- {tm_yyyy} : Displays the year part of the date in 4 digits.

Please note that these placeholders are case sensitive.

They can be used in lang and cmds YAML files, but also in signs, books, chat messages and commands.

#### DEPEDENCIES
TimeManager can display its placeholders through [PlaceholderAPI](www.spigotmc.org/resources/placeholderapi.6245) and [MVdWPlaceholderAPI](www.spigotmc.org/resources/mvdwplaceholderapi.11182). You just need to place the API in your plugin folder and set the related node to 'true' in the TimeManager config.yml file.

### ADMIN COMMAND /tm
**/tm checkConfig** Admins and console can display a summary of the config.yml and lang.yml files.

**/tm checkSql** Checks the availability of the mySql server according to the values provided in the config.yml file. This only checks the ip address and the correct port opening.

**/tm checkTime \[all|server|world]** Admins and console can display a debug/managing message, who displays the startup server's time, the current server's time and the current time, start time and speed for a specific world (or for all of them).

**/tm checkUpdate \[bukkit|curse|spigot|github]** Search if a newer version of the plugin exists on the chosen server. (MC 1.8.8+ only)

**/tm help \[cmd] \[\<subCmd>]** Help provides you the correct usage and a short description of targeted command and subcommand.

**/tm now \[msg|title|actionbar] \[player|all|world]** Send the '/now' (chat, title or action bar) message to a specific player, all players in a specific world, or all online players.

**/tm reload \[all|config|lang|cmds]** This command allows you to reload datas from yaml files after manual modifications. All timers will be immediately resynchronized.

**/tm resync \[all|world]** This command will re-synchronize a single or all worlds timers, based on the startup server's time, the elapsed time and the current speed modifier.

**/tm set date \[today|yyyy-mm-dd] \[all|world]** Sets current date for the specified world (or all of them). Could be 'today' or any yyyy-mm-dd date. The length of the months corresponds to reality, with the exception of February which always lasts 28 days. A year therefore always lasts 365 days.

**/tm set debugMode \[true|false]** Set true to enable colored verbose messages in the console. Useful to understand some mechanisms of this plugin.

**/tm set duration \[00d-00h-00m-00s] \[all|world]** Sets the speed of the world based on the desired duration rather than with a speed multiplier.

**/tm set durationDay \[00d-00h-00m-00s] \[all|world] & /tm set durationNight \[00d-00h-00m-00s] \[all|world]** The length of day and night can be defined separately.

**/tm set defLang \[lg_LG]** Choose the translation to use if player's locale doesn't exist in the lang.yml or when 'useMultiLang' is false.

**/tm set elapsedDays \[0 → ∞] \[all|world]** Sets current number of elapsed days for the specified world (or all of them). Could be an integer between '0' and infinity (or almost). Setting this to '0' will bring the world back to day one.

**/tm set firstStartTime \[default|previous|start] \[all|world]** Forces the time at which a world starts when starting the server. The value 'default' allows the usual resynchronization at startup. The value 'start' forces the world to start at the time specified in the world's 'start' node. The value 'previous' returns the time in the world before the server was shut down.

**/tm set initialTick \[ticks|HH:mm:ss]** Modify the server's initial tick.

**/tm set multiLang \[true|false]** Set true or false to use an automatic translation for the _/now_ command.

**/tm set playerOffset \[-23999 → 23999] \[all|player]** Defines a specific offset relative to the world time on player's client (the world speed will be still active). Set to '0' to cancel.

**/tm set playerTime \[ticks|daypart|HH:mm:ss|reset] \[all|player]** Defines a specific time on player's client (the world speed will be still active). Use the 'reset' argument to cancel.

**/tm set refreshRate \[ticks]** Sets the delay (in ticks) before actualizing the speed stretch/expand effect. Must be an integer between '2' and '20'. Default value is '10' ticks, please note that a too small value can cause server lags.

**/tm set sleep \[true|false|linked] \[all|world]** Defines if players can sleep until the next day in the specified world (or in all of them). By default, all worlds will start with parameter true, unless their timer is in real time who will be necessary false.
If you want to both allow sleep and keep the same time in multiple worlds, you can use the 'linked' function which allows a group of worlds to spend the night together.

**/tm set speed \[0.0 → 20.0] \[all|world]** The decimal number argument will multiply the world(s) speed. Use 0.0 to freeze time, numbers from 0.01 to 0.99 to slow time, 1.0 to get normal speed and numbers from 1.1 to 20.0 to speed up time. Set this value to 24.0 or 'realtime' to make the world time match the real speed time.

**/tm set speedDay \[0.0 → 20.0] \[all|world] & /tm set speedNight \[0.0 → 20.0] \[all|world]** 
Night and day speeds can be different from each other.

**/tm set start \[ticks|daypart|HH:mm:ss|timeShift] \[all|world]** Defines the time at server startup for the specified world (or all of them). By default, all worlds will start at tick \#0. The timer(s) will be immediately resynchronized.
If a world is using the real time speed, the start value will determine the UTC time shift and values like '+1' or '-1' will be accepted.

**/tm set sync \[true|false] \[all|world]** Defines if the speed distortion method will increase/decrease the world's actual tick, or fit the theoretical tick value based on the server one. By default, all worlds will start with parameter false. Real time based worlds and frozen worlds do not use this option, on the other hand this will affect even the worlds with a normal speed.

**/tm set time \[ticks|daypart|HH:mm:ss] \[all|world]** Sets current time for the specified world (or all of them). Consider using this instead of the vanilla _/time_ command. The tab completion also provides handy presets like "day", "noon", "night", "midnight", etc.

**/tm set update \[none|bukkit|curse|spigot|github]** Defines the source server for the update search. (MC 1.8.8+ only)
   
**/tm set useCmds \[true|false]** §rSet true to enable a custom commands scheduler. See the cmds.yml file for details.

### SHORT LIST OF COMMANDS AND ARGS
- For Players:
  - /now \<msg|title|actionbar> \<world>
- For Admins:
  - /tm checkConfig
  - /tm checkSql
  - /tm checkTime \[all|world]
  - /tm checkUpdate \[bukkit|spigot|github]
  - /tm help \[cmd] \[\<subCmd>]
  - /tm now \[msg|title|actionbar] \[all|player|world]
  - /tm reload \[all|config|lang|cmds]
  - /tm resync \[all|world]
  - /tm set date \[today|yyyy-mm-dd] \[all|world]
  - /tm set debugMode \[true|false]
  - /tm set defLang \[true|false]
  - /tm set duration \[00d-00h-00m-00s] \[all|world]
  - /tm set durationDay \[00d-00h-00m-00s] \[all|world]
  - /tm set durationNight \[00d-00h-00m-00s] \[all|world]
  - /tm set elapsedDays \[0 → ∞] \[all|world]
  - /tm set firstStartTime \[default|previous|start] \[all|world]
  - /tm set initialTick \[ticks|HH:mm:ss]
  - /tm set multiLang \[lg_LG]
  - /tm set playerOffset \[-23999 → 23999] \[all|player]
  - /tm set playerTime \[ticks|daypart|HH:mm:ss|reset] \[all|player]
  - /tm set refreshRate \[ticks]
  - /tm set sleep \[true|false] \[all|world]
  - /tm set speed \[0.0 → 20.0] \[all|world]
  - /tm set speedDay \[0.0 → 20.0] \[all|world]
  - /tm set speedNight \[0.0 → 20.0] \[all|world]
  - /tm set start \[ticks|daypart|HH:mm:ss] \[all|world]
  - /tm set sync \[true|false] \[all|world]
  - /tm set time \[ticks|daypart|HH:mm:ss] \[all|world]
  - /tm set update \[none|bukkit|spigot|github]
  - /tm set useCmds \[true|false]


### PERMISSIONS NODES
- timemanager.*
  - timemanager.admin
  - timemanager.now.*
    - timemanager.now.cmd
    - timemanager.now.display
    - timemanager.now.world
  - timemanager.sleep.*
    - timemanager.sleep.allowed
    - timemanager.sleep.counted

**timemanager.admin:** provide or deny access to /tm subcommands with all arguments.

**timemanager.now:** provide or deny access to /now subcommands with or without arguments.

**timemanager.placeholders:** Allows the use of placeholders in chat.

**timemanager.sleep.allowed :** Specifies whether the player is allowed to enter a bed (but saving the spawn point is still possible).

**timemanager.sleep.counted :** Specifies whether the player is counted for the night skip.

### YAML files
Full descriptions can be found in the respective file headers.
#### config.yml
This file mainly contains information concerning each world as well as general options such as the reference tick, SQL database usage, placeholder activation, etc.
#### lang.yml
This file contains the different translations. It is possible to modify them and add new languages. It is also possible to activate automatic translation depending on each player's locale.
#### cmds.yml
This file allows you to schedule the execution of commands at specific times and dates, with the possibility of repeating them according to the chosen cycle.
The reference time and date can be that of any world, or the actual time.

### TUTORIALS
[![IMAGE 1. How to Basically Configure the Plugin](http://imageshack.com/a/img924/8047/gxPi0W.png)](https://www.youtube.com/playlist?list=PLPTZNgSLmtr9PxHD_7Y2VFhbSqH8gKBad)

### COMPATIBILITY
v1.10: MC 1.9.4 to 1.21.3

### API
Although the plugin is not intended to be an API, it is possible to use the different placeholders by importing the PlaceholdersHandler class in your Java code :

```Java
import net.vdcraft.arvdc.timemanager.mainclass.PlaceholdersHandler
```
Then use one of the following methods to transform one or more placeholders in any string :

```Java
String replaceAllPlaceholders(String msg, String world, String lang, Player p)
String replaceAllPlaceholders(String msg, String world, String lang)
String replaceAllPlaceholders(String msg, World w, String lang, Player p)
String replaceAllPlaceholders(String msg, World w, String lang)
```

It is possible to retrieve the client language by importing the following class :

```Java
import net.vdcraft.arvdc.timemanager.mainclass.PlayerLangHandler
```
Then use one of the following of the following methods :

```java
String setLangToUse(CommandSender sender)
String setLangToUse(Player p)
```

### TODO
* ~~Commands: Add to '/tm checktime' an argument [all|world] to display the details for a single world.~~
* ~~Commands: Create a '/tm checkconfig' command that can display the summary of the current config (All the rest except the worlds details).~~
* ~~Commands: Create a '/tm set initialtick' command.~~
* ~~Commands: Permit 'HH:mm:ss' format for '/ tm set start', '/ tm set time' and '/ tm set initialtick' first argument.~~
* ~~Commands: Make an update message and associated commands.~~
* ~~Day & Night : Make a different speed multiplier for the day and the night.~~
* ~~Calendar: Create new placeholders to display a count of elapsed days and the date in yyyy-mm-dd format.~~
* ~~Scheduler: Create a scheduler allowing commands to be executed at specific times.~~
* ~~Sleep/Sync: Provide the ability to synchronize a world to a specified one, detecting the coming of a new day after someone has slept.~~
* ~~Tab completer: Try to improve the current hack that manages the spaces in worlds name. (Usefull until MC 1.12.2)~~
* ~~Commands: Allow players to individually set their time.~~
* ~~Worlds: Include _nether_ and _the end_ in the world list or link them to their reference world.~~
* ~~Worlds: Add an per world option, for the behavior of timers when starting the server.~~
* ~~Placeholders: Allow the use of placeholders in books and signs.~~
* ~~cmds.yml file: Permit to use a 'pause' between commands lines.~~
* ~~Placeholders: Add a placeholder to show current number of the day in the week.~~
* ~~Colors: Add hexadecimal color recognition.~~
* ~~Placeholders: Permit players to use placeholders in chat messages.~~
* ~~Placeholders: Permit external commands to use placeholders.~~
* ~~Command: Add a '/tm set duration' command with a 00d-00h-00m-00s format.~~
* ~~Placeholders: Add new placeholders for the names of the seven days of the week.~~
* ~~Sleep: Add some features to manage sleep as the plugin doesn't work well with most sleep management plugins.~~
* Commands: Allow players to display the list of placeholders.
* Placeholders: Create signs where the placeholders constantly refresh.
* Player Items: Create a custom item (and associated permissions and options) to use the '/now' command.
* Sleep : Improve particles displayed during sleep animation.

Please open an issue on GitHub if you want a specific improvement or encounter any bugs.
