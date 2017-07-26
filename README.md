![alt text](http://vdc.serveminecraft.net/plugins/TimeManager/tm-logo.png "TimeManager")

## Spigot plugin for time management and display


### TIME MANAGING FUNCTIONALITIES
Define a start time and a speed modifier per world. Set a suitable refresh rate for the performance of your server.

Time could be stretched/extended up to x10 or match real UTC time, with a per world time adjust.

Worlds list is actualized and timers are synchronized on each server startup and reload. Most values are automatically checked and corrected depending on the case.

Timers and speeds can be modified or re-synchronize with in-game commands or reloading after manually changes. Switching sync/async permit some useful original combinations for rpg or minigames.

This plugin override the vanilla "/time" command. The command to change a single world timer is "/tm set time \[ticks|daypart] \[world]".


### PLAYERS COMMAND /now <units> <world>
A single player command is used to display time (in ticks or hours) for any available world.

Chat messages support multi-language and could automatically be accorded to any player's locale available in the lang.yml file.

This message is configurable and provide the following placeholders: {time}, {dayPart}, {targetWorld} and {player}.

Using the permissions, you can permit players to choose units and/or world arguments or neither of the two.

Four combinations are therefore possible: "/now", "/now \<units>", "/now \<world>" and "/now \<units> \<world>".

This command doesn't display time of Nether and the End worlds.


### ADMINS COMMAND /timemanager or /tm
**/tm help \[cmdName]** Help provides you the correct usage and a short description of each command.

**/tm reload \[all|config|lang]** This command allows you to reload datas from yaml files after manual modifications. All timers will be immediately resynchronized.

**/tm resync \[all|world]** This command will re-synchronize a single or all worlds timers, based on the startup server's time, the elapsed time and the current speed modifier.

**/tm servtime** Admins and console can display a debug/managing message, who displays the startup server's time, the current server's time and each world current time, start time and speed.

**/tm set multilang \[true|false]** Set true or false to use an automatic translation for the _/now_ command.

**/tm set deflang \[lg_LG]** Choose the translation to use if player's locale doesn't exist in the lang.yml or when _useMultiLang_ is false.

**/tm set refreshrate \[ticks]** Set the delay (in ticks) before actualizing the speed stretch/expand effect. Must be an integer between _5_ and _25_. Default value is _10 ticks_, please note that a too small value can cause server lags.

**/tm set speed \[decimal] \[all|world]** The decimal number argument will multiply the world(s) speed. Use _0_ to freeze time, numbers from _0.1_ to _0.9_ to slow time, 1 to get normal speed and numbers from _1_ to _10_ to speedup time. Set this value to _24_ or _realtime_ to make the world time match the real speed time.

**/tm set start \[ticks|daypart] \[all|world]** Define the time at server startup for the specified world (or all of them). By default, all worlds will start at tick \#0. The timer(s) will be immediately resynchronized.

**/tm set time \[ticks|daypart] \[all|world]** Set current time for the specified world (or all of them). Consider using this instead of the vanilla _/time_ command. The tab completion also provides handy presets like "day", "noon", "night", "midnight", etc.

**/tm set sleepUntilDawn \[true|false] \[all|world]** Define if players can sleep until the next day in the specified world (or in all of them). By default, all worlds will start with parameter true, unless their timer is frozen or in real time who will be necessary false.

**/tm sqlcheck** Check the availability of the mySql server according to the values provided in the config.yml file. This only checks the ip address and the correct port opening.


### SHORT LIST OF COMMANDS AND ARGS
- For Players:
  - /now \<units> \<world>
- For Admins:
  - /tm help \[cmd]
  - /tm reload \[all|config|lang]
  - /tm resync \[all|world]
  - /tm servtime
  - /tm set deflang \[true|false]
  - /tm set multilang \[lg_LG]
  - /tm set refreshrate \[ticks]
  - /tm set sleepUntilDawn \[true|false] \[all|world]
  - /tm set speed \[decimal] \[all|world]
  - /tm set start \[ticks|daypart] \[all|world]
  - /tm set time \[ticks|daypart] \[all|world]
  - /tm sqlcheck


### PERMISSIONS NODES
- timemanager.*
  - timemanager.admin
  - timemanager.now.*
    - timemanager.now.cmd
    - timemanager.now.units
    - timemanager.now.worlds

**timemanager.admin:** provide or deny access to /tm commands with all arguments.

**timemanager.now:** provide or deny access to /now commands with or without restrain available arguments.


### TODO
* Need to fix a bug with spaces in world names. They make infinite loops in tab completion.
* Make a separate speed time for the night and day.
* ~~Make compatible with mc versions under 1.12 - adapt the getLocale() method in /now command.é~~~
* Maybe make a 1.8 (and under?) compatible version - something to fix up with the lang.yml, UTF-8 and accented characters.

### TUTORIALS
[![IMAGE 1. How to Install and Configure the Plugin](http://vdc.serveminecraft.net/plugins/TimeManager/1.%20How%20to%20Install%20and%20Configure%20the%20Plugin.png)](https://www.youtube.com/playlist?list=PLPTZNgSLmtr9PxHD_7Y2VFhbSqH8gKBad)

