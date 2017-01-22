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

import net.vdcraft.arvdc.timemanager.mainclass.CfgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.SleepUntilDawnHandler;
import net.vdcraft.arvdc.timemanager.mainclass.SqlHandler;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSyncHandler;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MainTM extends JavaPlugin {
	
	/****************
	*** VARIABLES ***
	****************/
	
	// Main class
	public static MainTM instanceMainClass;
	
	// Plugin version
	public static String versionTM = "1.0.0";
	
	// Files names
	public static String configFileName = "config.yml";
	public static String langFileName = "lang.yml";

	// Commands names
	public static String cmdTm = "timemanager";
	public static String cmdNow = "now";
	
	// Admin and Console messages
	public static String prefixTM = "[TimeManager]";
	public static String prefixTMColor = "§8§l[§6§lTimeManager§8§l]§r";
	
	public static String plEnabledMsg = "The plugin is now enabled, timers will be initialized when all the other plugins are loaded.";
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
	public static String worldsCheckMsg = "Worlds list was actualized.";
	public static String multiLangIsOnMsg = "Multilanguage support is enable.";
	public static String multiLangIsOffMsg = "Multilanguage support is disable.";
	public static String defLangCheckMsg = "Default translation is actually set to";
	public static String defLangResetMsg = "is missing or corrupt, back to the default parameter.";
	public static String defLangOkMsg = "exists in " + langFileName + ", keep it as default translation.";
	
	public static String serverInitTickMsg = "The server's initial tick was";
	public static String serverCurrentTickMsg = "The server's current tick is";
	public static String worldCurrentTickMsg = "'s current tick is";
	public static String worldCurrentTimeMsg = "'s current time is";
	public static String worldCurrentSpeedMsg = "'s current speed is";
	public static String worldRealSpeedMsg = "set to match real UTC time.";
	public static String worldCurrentStartMsg = "starts at";
	
	public static String allTimeChgMsg = "The current time of all the worlds is now set to";
	public static String worldTimeChgMsg = "'s current time is now set to";
	public static String allStartChgMsg = "All the worlds are synchronized using their new start time.";
	public static String worldStartChgMsg = "is synchronized using its new start time.";
	public static String allSleepTrueChgMsg = "It is now possible to sleep in any world unless those with frozen or real time speed setting.";
	public static String worldSleepTrueChgMsg = "It is now possible to sleep in world ";
	public static String allSleepFalseChgMsg = "It is now impossible to sleep in any world.";
	public static String worldSleepFalseChgMsg = "It is now impossible to sleep in world ";
	public static String worldSleepNoChgMsg = "Impossible to change this parameter cause of the actual speed setting for world ";
	public static String allSpeedChgMsg = "The speed of all the worlds is now multiplied by";
	public static String worldSpeedChgMsg = "'s speed is now multiplied by";
	public static String allRealSpeedChgMsg = "The speed of all the worlds will now match the real speed of time.";
	public static String worldRealSpeedChgMsg = "'s speed will now match the real speed of time.";
	public static String refreshRateMsg = "The time stretch/expand will refresh every";
	public static String resyncIntroMsg = "All worlds will now be syncronized to the server time.";
	public static String resyncDoneAllMsg = "All worlds have had their time syncronized to the server time.";
	public static String resyncDoneOneMsg = " had its timer synchronized to the server time.";
	
	// Errors Messages
	public static String rateNotNbMsg = "Refresh rate must be an integer number.";
	public static String tickNotNbMsg = "Tick must be an integer number or a listed part of the day.";
	public static String speedNotNbMsg = "Speed must be a number (integer or decimal) or the string \"realtime\".";
	public static String wrongWorldMsg = "The name of the world you just typed does not exist.";
	public static String wrongLangMsg = "The language you just typed does not exist in lang.yml file.";
	public static String wrongYmlMsg = "The name of the yaml file you just typed does not exist.";
	public static String missingArgMsg = "This command requires one or more additional argument(s).";
	public static String isNotBooleanMsg = "This command requires a boolean argument, true or false.";
	public static String couldNotSaveLang = "File " + langFileName + " couldn't be saved on disk. In worst case, delete the file then restart the server.";
	public static String checkLogMsg = "Please check the console or log file.";
	    
    // mySQL
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
	public static String connectionOkMsg = "is correctly responding on port #";
	
	// mySQL errors messages
	public static String checkConfigMsg = "Please check the configuration file.";
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
	public static String defSleepUntilDawn = "true";
	
	// Min and Max refresh parameters in ticks
	public static Integer refreshMin = 5;
	public static Integer refreshMax = 25;
	
	// Make the current refresh rate public
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
	public static String defaultMsg = "Please ask an admin to properly define the default language in the lang.yml file then reload this plugin.";
	public static String defaultNoMsg = "There is no day-night cycle in the Nether and the End dimensions.";	
	public static String defaultDay = "begin at 6.00 am or tick #" + dayStart;
	public static String defaultDusk = "begin at 5.30 pm or tick #" + duskStart; 
	public static String defaultNight = "begin at 7.00 pm or tick #" + nightStart; 
	public static String defaultDawn = "begin at 4.30 am or tick #" + dawnStart;
		
	// Config and Lang files names and targets
	public File configFileYaml = new File(this.getDataFolder(), configFileName);
	public File langFileYaml = new File(this.getDataFolder(), langFileName);
	public FileConfiguration langConf = YamlConfiguration.loadConfiguration(langFileYaml);
	
	// Help messages (copy it in README.md)
	public static String helpHelpMsg = "§e/tm help [cmd] §rHelp provides you the correct usage and a short description of each command.";
	public static String reloadHelpMsg = "§e/tm reload [all|config|lang] §rThis command allows you to reload datas from yaml files after manual modifications. All timers will be immediately resynchronized.";
	public static String resyncHelpMsg = "§e/tm resync [all|world] §rThis command will re-synchronize a single or all worlds timers, based on the startup server's time, the elapsed time and the current speed modifier.";
	public static String servtimeHelpMsg = "§e/tm servtime §rAdmins and console can display a debug/managing message, who displays the startup server's time, the current server's time and each world current time, start time and speed.";
	public static String setMultilangHelpMsg = "§e/tm set multilang [true|false] §rSet true or false to use an automatic translation for the §o/now command§r.";
	public static String setDefLangHelpMsg = "§e/tm set deflang [lg_LG] §rChoose the translation to use if player's locale doesn't exist in the lang.yml or when §ouseMultiLang§r is false.";
	public static String setRefreshRateHelpMsg = "§e/tm set refreshrate [ticks] §rSet the delay (in ticks) before actualizing the speed stretch/expand effect. Must be an integer between §o" + refreshMin + "§r and §o" + refreshMax + "§r. Default value is §o" + defRefresh + " ticks§r, please note that a too small value can cause server lags.";
	public static String setSpeedHelpMsg = "§e/tm set speed [decimal] [all|world] §rThe decimal number argument will multiply the world(s) speed. Use §o0§r to freeze time, numbers from §o0.1§r to §o0.9§r to slow time, §o1§o to get normal speed and numbers from §o1§r to " + speedMax + " to speedup time. Set this value to §o24§r or §orealtime§r to make the world time match the real speed time.";
	public static String setStartHelpMsg = "§e/tm set start [ticks|daypart] [all|world] §rDefines the time at server startup for the specified world (or all of them). By default, all worlds will start at §otick #0§r. The timer(s) will be immediately resynchronized.";
	public static String setTimeHelpMsg = "§e/tm set time [ticks|daypart] [all|world] §rSets current time for the specified world (or all of them). Consider using this instead of the vanilla §o/time§r command. The tab completion also provides handy presets like \"day\", \"noon\", \"night\", \"midnight\", etc.";
	public static String setSleepUntilDawnHelpMsg = "§e/tm set sleepUntilDawn [true|false] [all|world] §rDefine if players can sleep until the next day in the specified world (or in all of them). By default, all worlds will start with parameter true, unless their timer is frozen or in real time who will be necessary false.";
	public static String sqlcheckHelpMsg = "§e/tm sqlcheck §rCheck the availability of the mySql server according to the values provided in the config.yml file. This only checks the ip address and the correct port opening.";
	// Help message when 'set' is used without additional argument
	public static String missingSetArgHelpMsg = "§e/tm help set [deflang|multilang|refreshrate|speed|start|time] §rThis command, use with arguments, permit to change plugin parameters.";
	
	// Check if schedule is already active
	public static Boolean increaseScheduleIsOn = false;
	public static Boolean decreaseScheduleIsOn = false;
	public static Boolean realScheduleIsOn = false;
	
	// Initialize server tick
	public static Long initialTick;
	public static String initialTime;
	
	// Language to use if locale doesn't exist in the lang.yml = 'defaultLang' 
    public static String serverLang;
    
    // Use the Console as a sender
    public CommandSender laConsole = this.getServer().getConsoleSender();
	
	/*****************
	***** METHOD *****
	*****************/
    
	/** 
	 * Instantiate the main class 'MainTM'
	 */
	public static MainTM getInstance() {
		return instanceMainClass;
	};

	/*****************
	***** EVENTS *****
	*****************/
	
	/**
	 * 1. On Plugin enabling
	 */
	@Override
	public void onEnable() {		
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
		getServer().getPluginManager().registerEvents(new SleepUntilDawnHandler(), this);
		
		// #7. Synchronize worlds and create scheduled task for faking the time stretch/expand
		WorldSyncHandler.WorldSyncFirst();
		
		// #8. Confirm activation in console
		Bukkit.getLogger().info(prefixTM + " " + plEnabledMsg);
	};

	/**
	 * 2. On Plugin disabling
	 */
	
	@Override
	public void onDisable() {
		// Save YAMLs
		this.saveConfig();
		LgFileHandler.SaveLangYml();
		
		// Close SQL connection
		SqlHandler.closeConnection("Host");
		SqlHandler.closeConnection("DB");	
		
		// Confirm disabling in console
		Bukkit.getLogger().info(prefixTM + " " + plDisabledMsg);		
	};
	
}