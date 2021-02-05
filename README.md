![TimeManager](http://imageshack.com/a/img922/9061/ECwdWj.png "TimeManager")

## Bukkit plugin for time management and display


### TIME MANAGING FUNCTIONALITIES
Define a start time and a speed modifier per world. Set a suitable refresh rate for the performance of your server.

Time could be stretched/extended up to x10 or match real UTC time, with a per world time adjust.

Worlds list is actualized and timers are synchronized on each server startup and reload. Most values are automatically checked and corrected depending on the case.

Timers and speeds can be modified or re-synchronize with in-game commands or reloading after manually changes.

Day and night can be set to different speed values, which can be great for RPG or some mini-games.

This plugin override the vanilla "/time" command. The command to change a single world timer is "/tm set time \[ticks|daypart] \[world]".


### PLAYER COMMAND /now <units> <world>
A single command is used to display the time (in ticks or hours), the date and/or the number of elapsed days for any world.

Chat messages support multi-language and could automatically be accorded to any player's locale available in the lang.yml file.

This message is configurable and provide the following placeholders: {player}, {world}, {currentDay}, {dayPart}, {elapsedDays}, {monthName}, {time}, {yearWeek}, {dd}, {mm}, {yy} and {yyyy}.

Using the permissions, you can permit players to choose units and/or world arguments or neither of the two.

Four combinations are therefore possible: "/now", "/now \<units>", "/now \<world>" and "/now \<units> \<world>".

This command doesn't display time of Nether and the End worlds.


### ADMIN COMMAND /tm

**/tm checkconfig** Admins and console can display a summary of the config.yml and lang.yml files.

**/tm checksql** Check the availability of the mySql server according to the values provided in the config.yml file. This only checks the ip address and the correct port opening.

**/tm checktime \[all|server|world]** Admins and console can display a debug/managing message, who displays the startup server's time, the current server's time and the current time, start time and speed for a specific world (or for all of them).

**/tm checkupdate \[bukkit|spigot|github]** Search if a newer version of the plugin exists on the chosen server. (MC 1.18.9+ only)

**/tm help \[cmd] <subCmd>** Help provides you the correct usage and a short description of targeted command and subcommand.

**/tm reload \[all|config|lang]** This command allows you to reload datas from yaml files after manual modifications. All timers will be immediately resynchronized.

**/tm resync \[all|world]** This command will re-synchronize a single or all worlds timers, based on the startup server's time, the elapsed time and the current speed modifier.

**/tm set debugmode [true|false]** Set true to enable colored verbose messages in the console. Useful to understand some mechanisms of this plugin.

**/tm set deflang \[lg_LG]** Choose the translation to use if player's locale doesn't exist in the lang.yml or when _'useMultiLang'_ is false.

**/tm set elapsedDays \[today|0 → ∞] \[all|world]** §rSets current fullTime for the specified world (or all of them). Could be _today_ or an integer between _0_ and _infinity_ (or almost). A year always lasts 365 days.

**/tm set initialtick \[tick|HH:mm:ss]** Modify the server's initial tick.

**/tm set multilang \[true|false]** Set true or false to use an automatic translation for the _/now_ command.

**/tm set refreshrate \[ticks]** Set the delay (in ticks) before actualizing the speed stretch/expand effect. Must be an integer between _2_ and _20_. Default value is _10 ticks_, please note that a too small value can cause server lags.

**/tm set sleep \[true|false] \[all|world]** Define if players can sleep until the next day in the specified world (or in all of them). By default, all worlds will start with parameter true, unless their timer is frozen or in real time who will be necessary false.

**/tm set speed \[multiplier] \[all|world]** The decimal number argument will multiply the world(s) speed. Use _0.0_ to freeze time, numbers from _0.1_ to _0.9_ to slow time, 1.0 to get normal speed and numbers from _1.1_ to _10.0_ to speed up time. Set this value to _24.0_ or _realtime_ to make the world time match the real speed time.

**/tm set speedDay \[multiplier] \[all|world] & /tm set speedNight \[multiplier] \[all|world]** 
From _0.0_ to _10.0_, the values of daySpeed and nightSpeed can be different from each other.

**/tm set start \[ticks|daypart|HH:mm:ss] \[all|world]** Define the time at server startup for the specified world (or all of them). By default, all worlds will start at tick \#0. The timer(s) will be immediately resynchronized.

**/tm set sync [true|false] [all|world]** Define if the speed distortion method will increase/decrease the world's actual tick, or fit the theoretical tick value based on the server one. By default, all worlds will start with parameter false. Real time based worlds and frozen worlds do not use this option, on the other hand this will affect even the worlds with a normal speed.

**/tm set time \[ticks|daypart|HH:mm:ss] \[all|world]** Set current time for the specified world (or all of them). Consider using this instead of the vanilla _/time_ command. The tab completion also provides handy presets like "day", "noon", "night", "midnight", etc.

**/tm set update \[bukkit|spigot|github]** Define the source server for the update search. (MC 1.18.9+ only)


### SHORT LIST OF COMMANDS AND ARGS
- For Players:
  - /now \<units> \<world>
- For Admins:
  - /tm checkconfig
  - /tm checksql
  - /tm checktime [all|world]
  - /tm checkupdate [bukkit|spigot|github]
  - /tm help \[cmd]
  - /tm reload \[all|config|lang]
  - /tm resync \[all|world]
  - /tm set debugmode \[true|false]
  - /tm set deflang \[true|false]
  - /tm set elapsedDays \[today|0 → ∞] \[all|world]
  - /tm set initialtick [tick|HH:mm:ss]
  - /tm set multilang \[lg_LG]
  - /tm set refreshrate \[tick]
  - /tm set sleep \[true|false] \[all|world]
  - /tm set speed \[multiplier] \[all|world]
  - /tm set speedDay \[multiplier] \[all|world]
  - /tm set speedNight \[multiplier] \[all|world]
  - /tm set start \[tick|daypart|HH:mm:ss] \[all|world]
  - /tm set sync \[true|false] \[all|world]
  - /tm set time \[tick|daypart|HH:mm:ss] \[all|world]
  - /tm set update [bukkit|spigot|github]


### PERMISSIONS NODES
- timemanager.*
  - timemanager.admin
  - timemanager.now.*
    - timemanager.now.cmd
    - timemanager.now.units
    - timemanager.now.worlds

**timemanager.admin:** provide or deny access to /tm subcommands with all arguments.

**timemanager.now:** provide or deny access to /now subcommands with or without restrain available arguments.

### DEPEDENCIES
Since v1.4.0, TimeManager can display its placeholders through PlaceholderAPI and MVdWPlaceholderAPI. The available placeholders are as follows :
####PlaceholderAPI (www.spigotmc.org/resources/placeholderapi.6245) :
%tm_currentday%, %tm_daypart%, %tm_elapseddays%, %tm_monthname%, %tm_time%, %tm_yearweek%, %tm_dd%, %tm_mm%, %tm_yy% and %tm_yyyy%.
####MVdWPlaceholderAPI (www.spigotmc.org/resources/mvdwplaceholderapi.11182) :
{tm_currentday}, {tm_daypart}, {tm_elapseddays}, {tm_monthname}, {tm_time}, {tm_yearweek}, {tm_dd}, {tm_mm}, {tm_yy} and {tm_yyyy}.

### TUTORIALS
[![IMAGE 1. How to Install and Configure the Plugin](http://imageshack.com/a/img924/8047/gxPi0W.png)](https://www.youtube.com/playlist?list=PLPTZNgSLmtr9PxHD_7Y2VFhbSqH8gKBad)

### COMPATIBILITY
* v1.4.0: Spigot, Paper and Bukkit - MC 1.4.6 to 1.16.4
* v1.3.1: Spigot, Paper and Bukkit - MC 1.4.6 to 1.16.4
* v1.3.0: Spigot, Paper and Bukkit - MC 1.4.6 to 1.16.4
* v1.2.1: Spigot, Paper and Bukkit - MC 1.4.6 to 1.16.1
* v1.2.0: Spigot, Paper and Bukkit - MC 1.4.6 to 1.14.3
* v1.1.1: Spigot, Paper and Bukkit - MC 1.4.6 to 1.13
* v1.1.0: Spigot, Paper and Bukkit - MC 1.4.6 to 1.12.1
* v1.0.2: Spigot and Bukkit - MC 1.4.6 to 1.12
* v1.0.1: Spigot - MC 1.9 to 1.12 and Bukkit - MC 1.12
* v1.0.0: Spigot and Bukkit - MC 1.12

### TODO
* ~~Command: Add to '/tm checktime' an argument [all|world] to display the details for a single world.~~
* ~~Command: Create a '/tm checkconfig' command that can display the summary of the current config (All the rest except the worlds details).~~
* ~~Command: Create a '/tm set initialtick' command.~~
* ~~Command: Permit 'HH:mm:ss' format for '/ tm set start', '/ tm set time' and '/ tm set initialtick' first argument.~~
* ~~Command: Make an update message and associated commands.~~
* ~~Day & Night : Make a different speed multiplier for the day and the night.~~
* ~~Calendar: Create new placeholders to display a count of elapsed days and the date in yyyy-mm-dd format.~~
* Sleep: Provide the ability to synchronize a world to a specified one, detecting the coming of a new day after someone has slept.
* Player Item: Create a custom item (and associated permissions and options) to use the '/now' command.
* Schedule: Create a schedule allowing commands to be executed at specific times. 
* Tab completer: Try to improve the current hack that manages the spaces in worlds name.
