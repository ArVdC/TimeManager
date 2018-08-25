/*****************
*** MAIN CLASS ***
*****************/

/**
 * Handle global variables and startup loading
 */

package net.vdcraft.arvdc.timemanager;

import java.io.File;
import java.sql.Connection;

import org.bukkit.plugin.java.JavaPlugin;

import net.vdcraft.arvdc.timemanager.mainclass.McVersionHandler;
import net.vdcraft.arvdc.timemanager.mainclass.CfgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.SqlHandler;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSleepHandler;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSyncHandler;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MainTM extends JavaPlugin {
	
	/****************
	*** VARIABLES ***
	****************/
	
	// Main class
	public static MainTM instanceMainClass;

	// Plugin version
	public static String versionTM() { return instanceMainClass.getDescription().getVersion().toString(); };
	
	// Minecraft server minimal required version decimals "x.xx" (without the "1." and eventually with a "x.0x" format - to permit comparisons) 
	public static Double minRequiredMcVersion = 4.06;
	
	// Minecraft server latest version decimals "x.xx" (without the "1." and eventually with a "x.0x" format - to permit comparisons) 
	public static Double latestMcVersion = 13.00;
	
	// Enable/Disable debugging
	public static Boolean debugMode = false; // Final user accessible debug msgs
	public static Boolean devMode = false; // Displays more verbose debug msgs
	public static Boolean timerMode = false; // Displays all timers calculations (= ultra-verbose mode)
	
	// Files names
	public static String configFileName = "config.yml";
	public static String langFileName = "lang.yml";

	// Commands names
	public static String cmdTm = "tm";
	public static String cmdNow = "now";
	
	// Admin and Console messages
	// - Prefixes
	public static String prefixTM = "[TimeManager]";
	public static String prefixTMColor = "§8§l[§6§lTimeManager§8§l]§r";
	public static String prefixDebugMode = "§8§l[§e§lTimeManager§8§l]§b";	
	// - Plugin enable & reload messages
	public static String plEnabledMsg = "The plugin is now enabled, timers will be initialized when all the other plugins are loaded.";
	public static String plBadVersionMsg = "§cThe plugin is not compatible with versions under 1." + minRequiredMcVersion + " and you are running a ";
	public static String plDisabledMsg = "The plugin is now disabled.";	
	public static String cfgFileCreaMsg = "The configuration file was created.";
	public static String lgFileCreaMsg = "The language file was created.";
	public static String cfgFileExistMsg = "The configuration file already exists.";
	public static String lgFileExistMsg = "The language file already exists.";
	public static String cfgVersionMsg = "Enabled " + configFileName + " v";
	public static String lgVersionMsg = "Enabled " + langFileName + " v";
	public static String cfgFileTryReloadMsg = "Reloading the configuration file.";
	public static String cfgFileReloadMsg = "The configuration file was reloaded.";
	public static String lgFileTryReloadMsg = "Reloading the language file.";
	public static String lgFileReloadMsg = "The language file was reloaded.";
	public static String worldsCheckMsg = "The worlds list was actualized.";
	public static String multiLangIsOnMsg = "Multilanguage support is enable.";
	public static String multiLangIsOffMsg = "Multilanguage support is disable.";
	public static String multiLangDoesntWork = "Multilanguage is not supported by CraftBukkit under the 1.12 version. Upgrade or try with Spigot.";
	public static String defLangCheckMsg = "Default translation is actually set to";
	public static String defLangResetMsg = "is missing or corrupt, back to the default parameter.";
	public static String defLangOkMsg = "exists in " + langFileName + ", keep it as default translation.";	
	public static String resyncIntroMsg = "All worlds will now be syncronized to the server time. If you want to keep them synchronized, set their 'sync' option to true.";
	// - Cmds resync & checkTime
	public static String serverInitTickMsg = "The server's initial tick is";
	public static String serverCurrentTickMsg = "The server's current tick is";
	public static String worldCurrentStartMsg = "starts at";
	public static String worldCurrentTickMsg = "'s current tick is";
	public static String worldCurrentTimeMsg = "'s current time is";
	public static String worldCurrentSpeedMsg = "'s current speed is";
	public static String worldRealSpeedMsg = "set to match real UTC time";
	public static String worldCurrentSyncMsg = "synchronized to the server time";
	public static String worldCurrentSleepMsg = "'s 'sleep' option is set to";
	// - Cmd resync
	public static String resyncDoneOneMsg = "had its time re" + worldCurrentSyncMsg;
	// - Cmd set refreshRate
	public static String refreshRateMsg = "The time stretch/expand will refresh every";
	// - Cmd set initial tick
	public static String initialTickYmlMsg = "The new initial tick will be saved in the config.yml file.";
	public static String initialTickSqlMsg = "The new initial tick will be saved in the MySQL database.";
	public static String initialTickGetFromSqlMsg = "The new initial tick was get from the MySQL database.";
	public static String initialTickNoChgMsg = "The tick you just entered is the same as the current one.";
	// - Cmd set sleep
	public static String worldSleepTrueChgMsg = "It is allowed to sleep until the dawn in the world";
	public static String worldSleepFalseChgMsg = "It is forbidden to sleep until the dawn in the world";
	public static String worldSleepNoChgMsg = "Impossible to change the 'sleep' option cause of the actual speed setting for the world";
	// - Cmd set speed
	public static String worldSpeedChgIntro = "The speed of the world";
	public static String worldSpeedChgMsg = "is now multiplied by";
	public static String worldRealSpeedChgMsg = "will now match the real time, the 'start' value will defines the time zone.";
	// - Cmd set start
	public static String worldStartChgMsg1 = "The time of the world";
	public static String worldStartChgMsg2 = "was resynchronized using its new 'start' value.";
	// - Cmd set sync
	public static String worldSyncTrueChgMsg = "The synchronization to the server time is activated for the world";
	public static String worldSyncFalseChgMsg = "The synchronization to the server time is disabled for the world";
	public static String worldSyncNoChgMsg = "Impossible to change the 'sync' option cause of the actual speed setting for the world";
	public static String world24hNoSyncChgMsg = "is synchronized to real UTC time and doesn't need to be resynchronized.";	
	public static String worldFrozenNoSyncChgMsg = "has its speed frozen and doesn't need to be resynchronized.";	
	public static String worldSyncSleepChgMsg = "'sleep' option was forced to false, cause of its synchronization value.";
	public static String SleepWorldSyncChgMsg = "'sync' option was forced to false in order to allow players to sleep until the dawn.";	
	// - Cmd set time
	public static String worldTimeChgMsg1 = "The current time of the world";	
	public static String worldTimeChgMsg2 = "is now set to";
	public static String worldSyncTimeChgMsg = "is synchronized to the server time, its 'start' value will be changed to modify its current time.";
	public static String worldRealSyncTimeChgMsg = "will always match real UTC minutes and seconds, a new rounded 'start' value will be calculated.";
	
	// Errors messages
	public static String rateNotNbMsg = "Refresh rate must be an integer number.";
	public static String tickNotNbMsg = "Tick must be an integer number or a listed part of the day.";
	public static String speedNotNbMsg = "Speed multiplier must be a number (integer or decimal) or the string \"realtime\".";
	public static String wrongWorldMsg = "The name of the world you just typed does not exist.";
	public static String wrongLangMsg = "The language you just typed does not exist in lang.yml file.";
	public static String wrongYmlMsg = "The name of the yaml file you just typed does not exist.";
	public static String missingArgMsg = "This command requires one or more additional argument(s).";
	public static String isNotBooleanMsg = "This command requires a boolean argument, true or false.";
	public static String couldNotSaveLang = "File " + langFileName + " couldn't be saved on disk. In worst case, delete the file then restart the server.";
	public static String checkLogMsg = "Please check the console or log file.";
	public static String unknowVersionMsg= "Impossible to determine properly the MC version of your server, the plugin will consider it is an old one.";

	// Debug messages (with colors)
	public static String enableDebugModeDebugMsg = "The debug mode is §aenabled§b.";
	public static String disableDebugModeDebugMsg = "The debug mode is §cdisabled§b.";
	public static String cfgOptionsCheckDebugMsg = "The options will be now checked for each world.";
	public static String refrehWorldsListDebugMsg = "Refreshing the §e'worldsList'§b keys in config.yml.";
	public static String worldsRawListDebugMsg = "Raw list of all loaded worlds:";
	public static String worldsFormatListDebugMsg = "Raw list of all loaded worlds:";
	public static String worldsCfgListDebugMsg = "Worlds list from the config.yml:";
	public static String delWorldDebugMsg = "was deleted from the config list.";
	public static String speedAdjustDebugMsg = "The §e'speed'§b option value was converted from";
	public static String startAdjustDebugMsg = "The §e'start'§b option value was converted from";
	public static String syncAdjustTrueDebugMsg = "The §e'sync'§b option is forced to §atrue §bfor the world";
	public static String syncAdjustFalseDebugMsg = "The §e'sync'§b option is forced to §cfalse §bfor the world";
	public static String sleepAdjustFalseDebugMsg = "The §e'sleep'§b option is forced to §cfalse §bfor the world";
	public static String availableTranslationsDebugMsg = "Available translations are:";
	public static String daylightTrueDebugMsg = "The gamerule §e'doDaylightCycle'§b is now set to §atrue§b for the world";
	public static String daylightFalseDebugMsg = "The gamerule §e'doDaylightCycle'§b is now set to §cfalse§b for the world";
	public static String mcLocaleDebugMsg = "The locale will be determined by the Minecraft client.";
	public static String pcLocaleDebugMsg = "The locale will be determined by the computer and §onot §bby the Minecraft client.";
	public static String foundLocaleDebugMsg = "The locale found for the player";
	public static String useLocaleDebugMsg = "The locale that will be used for the player";
	public static String launchSchedulerDebugMsg = "If off, launch the scheduler corresponding to the asked speed value.";
	public static String serverTypeQueryDebugMsg = "Try to get the server's type.";
	public static String serverTypeResultDebugMsg = "You are running a";
	public static String serverMcVersionQueryDebugMsg = "Try to get the server's Minecraft version.";
	public static String serverMcVersionResultDebugMsg = "You are using a";
	public static String completeVersionDebugMsg = "Complete version details:";
	public static String noVersionNumberDebugMsg = "No MC version number could be recognized in";
	public static String wrongVersionNumberDebugMsg = "Your MC version doesn't correspond to any decimal number:";
	// Debug Calculation for timer synchronization
	public static String actualTimeVar = "§c[actualTime]§b";
	public static String adjustedElapsedTimeVar = "§5[adjustedElapsedTime]§b";
	public static String adjustedTicksVar = "§3[adjustedTick]§b";
	public static String askedTimeVar = "§8[askedTime]§b";
	public static String currentTickVar = "§8[currentTick]§b";
	public static String currentServerTickVar = "§8[currentServerTick]§b";
	public static String elapsedTimeVar = "§d[elapsedTime]§b";
	public static String initialTickVar = "§7[initialTick]§b";
	public static String mcTimeRatioVar = "§6[mcTimeRatio]§b";
	public static String sixHoursLessVar = "§9[sixHoursLess]§b";
	public static String speedModifierVar = "§a[speedModifier]§b";
	public static String ticksInOneDayVar = "§6[ticksInOneDay]§b";
	public static String worldStartAtVar = "§e[worldStartAt]§b";
	public static String oldWorldStartAtVar = "§3[oldWorldStartAt]§b";
	public static String actualTimeCalculation = actualTimeVar + " = " + worldStartAtVar + " + " + adjustedElapsedTimeVar;
	public static String elapsedTimeCalculation = elapsedTimeVar + " = (" + currentServerTickVar + " - " + initialTickVar +") % " + ticksInOneDayVar;
	public static String adjustedElapsedTimeCalculation = adjustedElapsedTimeVar + " = " + elapsedTimeVar + " * " + speedModifierVar;
	public static String worldStartAtCalculation = worldStartAtVar + " = " + oldWorldStartAtVar + " + " + askedTimeVar + " - " + actualTimeVar;
	public static String adjustedTicksCalculation = adjustedTicksVar + " = " + currentTickVar + " / " + mcTimeRatioVar;
	public static String realActualTimeCalculation = actualTimeVar + " = " + worldStartAtVar + " - " + sixHoursLessVar + " + " + adjustedTicksVar;
	
	// MySQL
	public static String host;
	public static String port;
	public static String ssl;
	public static String dbPrefix;
	public static String database;
	public static String tableName;
	public static String username;
	public static String password;
	public static Connection connectionHost;
	public static Connection connectionDB;
	public static String connectionOkMsg = "is correctly responding on port";
	public static Long sqlInitialTickAutoUpdateValue = 2L;
	public static Boolean mySqlRefreshIsAlreadyOn = false;
	public static String sqlInitialTickAutoUpdateMsg = "If someone changes the InitialTickNb value in the MySQL database, the change will be reflected on this server within the next " + sqlInitialTickAutoUpdateValue + " minutes.";
	
	// mySQL errors messages
	public static String tryReachHostMsg = "Trying to reach the provided mySQL host";
	public static String checkConfigMsg = "Please check the " + configFileName + " file and set the debugMode to true to see error details.";
	public static String connectionFailMsg = "Something prevented to establish a connection with provided host";
	public static String dbCreationFailMsg = "Something prevented the database creation.";
	public static String tableCreationFailMsg = "Something prevented the table creation.";
	public static String datasCreationFailMsg = "Something preventeds datas from being written.";
	public static String datasOverridingFailMsg = "Something keeps the datas from being updated.";
	public static String tableReachFailMsg = "The table where the reference tick should be stocked is unreachable.";
	public static String disconnectionFailMsg = "Something prevented the mySQL disconnection.";

	// Default config files values
	public static String defTimeUnits = "hours";
	public static Long defStart = 0L;
	public static Integer defRefresh = 10;
	public static Double defSpeed = 1.0;
	public static String defSleep = "true";
	public static String defSync = "false";
	
	// Min and Max refresh parameters in ticks
	public static Integer refreshMin = 2;
	public static Integer refreshMax = 20;
	
	// Handle the current refresh rate 
	public static Integer refreshRateInt;
	public static Long refreshRateLong;

	// Max speed modifier (Min need to be 0)
	public static Double speedMax = 10.0;

	// Number who make time turn real
	public static Double realtimeSpeed = 24.0;
	
	// DayParts in ticks
	public static Integer dayStart = 0;
	public static Integer duskStart = 11500;
	public static Integer nightStart = 13000;
	public static Integer dawnStart = 22500;
	public static Integer dayEnd = 24000;
	
	// Default sentences in the lang.yml
	public static String defaultPrefix = "&8&l[&6&lTime Manager&8&l]";
	public static String defaultMsg = "Please ask an admin to properly define the default language in the lang.yml file then reload this plugin.";
	public static String defaultNoMsg = "There is no day-night cycle in the Nether and the End dimensions.";	
	public static String defaultDay = "begin at 6.00 am or tick #" + dayStart;
	public static String defaultDusk = "begin at 5.30 pm or tick #" + duskStart; 
	public static String defaultNight = "begin at 7.00 pm or tick #" + nightStart; 
	public static String defaultDawn = "begin at 4.30 am or tick #" + dawnStart;
		
	// Config and Lang files targets
	public File configFileYaml = new File(this.getDataFolder(), configFileName);
	public File langFileYaml = new File(this.getDataFolder(), langFileName);
	public FileConfiguration langConf = YamlConfiguration.loadConfiguration(langFileYaml);
	
	// Help messages (copy it in README.md)
	public static String headerHelp = "§e---------§r Help: " + prefixTMColor + " §e---------";
	public static String helpHelpMsg = "§6/tm help [cmd] <subCmd>: §rHelp provides you the correct usage and a short description of targeted command or subcommand.";
	public static String reloadHelpMsg = "§6/tm reload [all|config|lang]: §rThis command allows you to reload datas from yaml files after manual modifications. All timers will be immediately resynchronized.";
	public static String resyncHelpMsg = "§6/tm resync [all|world]: §rThis command will re-synchronize a single or all worlds timers, based on the startup server's time, the elapsed time and the current speed modifier.";
	public static String checkconfigHelpMsg = "§6/tm checkconfig: §rAdmins and console can display a summary of the config.yml and lang.yml files.";
	public static String checkSqlHelpMsg = "§6/tm checksql: §rCheck the availability of the mySQL server according to the values provided in the config.yml file. This only checks the ip address and the correct port opening.";
	public static String checktimeHelpMsg = "§6/tm checktime [all|server|world]: §rAdmins and console can display a debug/managing message, who displays the startup server's time, the current server's time and the current time, start time and speed for a specific world (or for all of them).";
	public static String setDebugHelpMsg = "§6/tm set debugmode [true|false]: §rSet true to enable colored verbose messages in the console. Useful to understand some mechanisms of this plugin.";
	public static String setInitialTickHelpMsg = "§6/tm set multilang [tick]: Modify the server's initial tick.";
	public static String setMultilangHelpMsg = "§6/tm set multilang [true|false]: §rSet true or false to use an automatic translation for the §o/now §rcommand.";
	public static String setDefLangHelpMsg = "§6/tm set deflang [lg_LG]: §rChoose the translation to use if player's locale doesn't exist in the lang.yml or when §o'multiLang'§r is false.";
	public static String setRefreshRateHelpMsg = "§e/tm set refreshrate [tick]: §rSet the delay (in ticks) before actualizing the speed stretch/expand effect. Must be an integer between §o" + refreshMin + "§r and §o" + refreshMax + "§r. Default value is §o" + defRefresh + " ticks§r, please note that a too small value can cause server lags.";
	public static String setSleepHelpMsg = "§6/tm set sleep [true|false] [all|world]: §rDefine if players can sleep until the next day in the specified world (or in all of them). By default, all worlds will start with parameter true, unless their timer is frozen or in real time who will be necessary false.";
	public static String setSpeedHelpMsg = "§6/tm set speed [multiplier] [all|world]: §rThe decimal number argument will multiply the world(s) speed. Use §o0.0§r to freeze time, numbers from §o0.1§r to §o0.9§r to slow time, §o1.0§o to get normal speed and numbers from §o1.1§r to " + speedMax + " to speedup time. Set this value to §o24.0§r or §orealtime§r to make the world time match the real speed time.";
	public static String setStartHelpMsg = "§6/tm set start [tick|daypart] [all|world]: §rDefines the time at server startup for the specified world (or all of them). By default, all worlds will start at §otick #0§r. The timer(s) will be immediately resynchronized.";
	public static String setTimeHelpMsg = "§6/tm set time [tick|daypart] [all|world]: §rSets current time for the specified world (or all of them). Consider using this instead of the vanilla §o/time§r command. The tab completion also provides handy presets like \"day\", \"noon\", \"night\", \"midnight\", etc.";
	public static String setSyncHelpMsg = "§6/tm set sync [true|false] [all|world]: §rDefine if the speed distortion method will increase/decrease the world's actual tick, or fit the theoretical tick value based on the server one. By default, all worlds will start with parameter false. Real time based worlds and frozen worlds do not use this option, on the other hand this will affect even the worlds with a normal speed.";
	// Help message when 'set' is used without additional argument
	public static String missingSetArgHelpMsg = "§e/tm help set [deflang|multilang|refreshrate|sleep|speed|start|sync|time]: §rThis command, use with arguments, permit to change plugin parameters.";
	
	// Check if schedule is already active
	public static Boolean increaseScheduleIsOn = false;
	public static Boolean decreaseScheduleIsOn = false;
	public static Boolean realScheduleIsOn = false;
	
	// Initialize server tick
	public static Long initialTick;
	public static String initialTime;
	
	// Language to use if locale doesn't exist in the lang.yml = 'defaultLang' 
    public static String serverLang;
	
	/*****************
	***** METHOD *****
	*****************/

	/** 
	 * Instantiate the main class 'MainTM'
	 */
	public static MainTM getInstance() {
		return instanceMainClass;
	}
	
	/*****************
	***** EVENTS *****
	*****************/
	/**
	 * 1. On Plugin enabling
	 */
	@Override
	public void onEnable() {

		// #0. Don't start the plugin with too old versions of the game
		if(McVersionHandler.KeepDecimalOfMcVersion() < minRequiredMcVersion) {
			Bukkit.getServer().getConsoleSender().sendMessage(prefixTM + " §c" + plBadVersionMsg + "1." + McVersionHandler.KeepDecimalOfMcVersion() + " server.");
		} else {			
			// #1. Initiate this main class as the contain of the instance
			instanceMainClass = this;
	
			// #2. Activate the configuration file
			CfgFileHandler.loadConfig("first");
			
			// #3. Activate the languages file
			LgFileHandler.loadLang("first");
	
			// #4. Activate the class with admins commands
			CommandExecutor timemanagerExecutor = new AdminCmdExecutor();
			getCommand(cmdTm).setExecutor(timemanagerExecutor);
			// Activate tab completion for admins commands
			getCommand(cmdTm).setTabCompleter(new CreateSentenceCommand());
			
			// #5. Activate the class with players commands
			CommandExecutor nowExecutor = new PlayerCmdExecutor();
			getCommand(cmdNow).setExecutor(nowExecutor);	
			// Activate tab completion for players commands
			getCommand(cmdNow).setTabCompleter(new CreateSentenceCommand());
			
			// #6. Listen to sleep events
			getServer().getPluginManager().registerEvents(new WorldSleepHandler(), this);
			
			// #7. Synchronize worlds and create scheduled task for faking the time stretch/expand
			WorldSyncHandler.WorldSyncFirst();
			
			// #8. Confirm activation in console
			Bukkit.getLogger().info(prefixTM + " " + plEnabledMsg);
		}
	}

	/**
	 * 2. On Plugin disabling
	 */	
	@Override
	public void onDisable() {
		// #0. Don't disable the plugin with if not loaded first
		if(McVersionHandler.KeepDecimalOfMcVersion() < minRequiredMcVersion) {
		} else {
			// Save YAMLs
			this.saveConfig();
			LgFileHandler.SaveLangYml();
			
			// Close SQL connection
			SqlHandler.closeConnection("Host");
			SqlHandler.closeConnection("DB");	
			
			// Confirm disabling in console
			Bukkit.getLogger().info(prefixTM + " " + plDisabledMsg);
		}
	}
	
	/** 
	 * 3. Custom wait
	 * 
	 */ 
	public static void waitTime(Integer ticksToWait) {
		try {
			Thread.sleep(ticksToWait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
};