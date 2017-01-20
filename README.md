#TimeManager
##Spigot plugin for time management and display


###TIME MANAGING FUNCTIONALITIES
Define a start time and a speed modifier per world.

Worlds timers will be synchronized on server startup.

Timers and speed can be modified or re-synchronize with in-game commands.

This plugin override the vanilla "/time" command. The command to change a single world timer is "/tm set time \[ticks] \[world]".


###PLAYERS COMMAND /now <units> <world>
A single player command is used to display time (in ticks or hours) for any available world.

Chat messages support multi-language and could automatically be accorded to any player's locale available in the lang.yml file.

This message is configurable and provide the following placeholders: {time}, {dayPart}, {targetWorld} and {player}.

Using the permissions, you can permit players to choose units and/or world arguments or neither of the two.

Four combinations are therefore possible: "/now", "/now \<units>", "/now \<world>" and "/now \<units> \<world>".


###ADMINS COMMAND /timemanager or /tm
**/tm help \[cmdName]** Help provides you the correct usage and a short description of each command.

**/tm reload \[all|config|lang]** This command allows you to reload datas from yaml files after manual modifications. All timers will be immediately resynchronized.

**/tm resync \[all|world]** This command will re-synchronize a single or all worlds timers, based on the startup server's time, the elapsed time and the current speed modifier.

**/tm servtime** Admins and console can display a debug/managing message, who displays the startup server's time, the current server's time and each world current time, start time and speed.

**/tm set multilang \[true|false]** Set true or false to use an automatic translation for the /now command.

**/tm set deflang \[lg_LG]** Choose the translation to use if player's locale doesn't exist in the lang.yml or when useMultiLang is false.

**/tm set refreshrate \[ticks]** Set the delay (in ticks) before actualizing the speed stretch/expand effect. Must be an integer between 5 and 25. Default value is 10 ticks, please note that a too small value can cause server lags.

**/tm set speed \[decimal] \[all|world]** The decimal number argument will multiply the world(s) speed. Use 0 to freeze time, numbers from 0.1 to 0.9 to slow time, 1 to get normal speed and numbers bigger than 1 to speedup time. Value must be a decimal or integer number from 0 to 10.

**/tm set start \[ticks] \[all|world]** Define the time at server startup for the specified world (or all of them). By default, all worlds will start at tick \#0/24000. The timer(s) will be immediately resynchronized.

**/tm set time \[ticks] \[all|world]** Set current time for the specified world (or all of them). Consider using this instead of the vanilla "/time" command. The tab completion also provides handy presets like "day", "noon", "night", "midnight", etc.


###SHORT LIST OF COMMANDS AND ARGS
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
  - /tm set speed \[decimal] \[all|world]
  - /tm set start \[ticks] \[all|world]
  - /tm set time \[ticks] \[all|world]


###PERMISSIONS NODES
- timemanager.*
  - timemanager.admin
  - timemanager.now.*
    - timemanager.now.cmd
    - timemanager.now.units
    - timemanager.now.worlds

**timemanager.admin:** provide or deny access to /tm commands with all arguments.

**timemanager.now:** provide or deny access to /now commands with or without restrain available arguments.


###TODO
* Make a MySql database to share the reference initial tick.
* Make a speed value for 'real time' spending (1/72).
* Fix a bug whit spaces in world names.