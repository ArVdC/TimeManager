/*****************
*** MAIN CLASS ***
*****************/

/**
 * Handle global variables and startup loading
 */

package net.vdcraft.arvdc.timemanager;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import net.vdcraft.arvdc.timemanager.mainclass.CfgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;
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
	public static String worldCurrentStartMsg = " starts at tick";
	
	public static String allTimeChgMsg = "The current time of all the worlds is now set to";
	public static String worldTimeChgMsg = "'s current time is now set to";
	public static String allStartChgMsg = "All the worlds will now start at";
	public static String worldStartChgMsg = "'s will now start at";
	public static String allSpeedChgMsg = "The speedof all the worlds is now multiplied by";
	public static String worldSpeedChgMsg = "'s speed is now multiplied by";
	public static String refreshRateMsg = "The time stretch/expand will refresh every";
	public static String resyncIntroMsg = "All worlds will now be syncronized to the server time.";
	public static String resyncDoneAllMsg = "All worlds have had their time syncronized to the server time.";
	public static String resyncDoneOneMsg = " had its timer synchronized to the server time.";
	
	// Error Messages
	public static String rateNotNbMsg = "Refresh rate must be an integer number.";
	public static String tickNotNbMsg = "Tick must be an integer number.";
	public static String speedNotNbMsg = "Speed must be an integer or a decimal number.";
	public static String wrongWorldMsg = "The name of the world you just typed does not exist.";
	public static String wrongLangMsg = "The language you just typed does not exist in lang.yml file.";
	public static String wrongYmlMsg = "The name of the yaml file you just typed does not exist.";
	public static String missingArgMsg = "This command requires one or more additional argument(s).";
	public static String isNotBooleanMsg = "This command requires a boolean argument, true or false.";
	public static String couldNotSaveLang = "File " + langFileName + " couldn't be saved on disk. In worst case, delete the file then restart the server.";

	// Default config files values
	public static String defTimeUnits = "hours";
	public static Long defStart = 0L;
	public static Integer defRefresh = 10;
	public static Double defSpeed = 1.0;
	
	// Min and Max refresh parameters in ticks
	public static Integer refreshMin = 5;
	public static Integer refreshMax = 25;
	
	// Make the current refresh rate public
	public static Integer refreshRateInt;
	public static Long refreshRateLong;

	// Max speed modifier (Min need to be 0)
	public static Double speedMax = 10.0;
	
	// DayParts in ticks
	public static Integer dayStart = 0;
	public static Integer duskStart = 11500;
	public static Integer nightStart = 13000;
	public static Integer dawnStart = 22500;
	public static Integer dayEnd = 24000;
	
	// Default sentences in the lang.yml
	public static String defaultMsg = "Please ask an admin to properly define the default language in the lang.yml file then reload this plugin."; // Le msg par défaut
	public static String defaultDay = "begin at 6.00 am or tick #" + dayStart; // Les parties de la journée
	public static String defaultDusk = "begin at 5.30 pm or tick #" + duskStart; 
	public static String defaultNight = "begin at 7.00 pm or tick #" + nightStart; 
	public static String defaultDawn = "begin at 4.30 am or tick #" + dawnStart;
		
	// Config and Lang files names and targets
	public File configFileYaml = new File(this.getDataFolder(), configFileName); // le fichier config.yml
	public File langFileYaml = new File(this.getDataFolder(), langFileName); // le fichier lang.yml
	public FileConfiguration langConf = YamlConfiguration.loadConfiguration(langFileYaml);
	
	// Help messages (copy it in README.md)
	public static String helpHelpMsg = "§e/tm help [cmd] §rHelp provides you the correct usage and a short description of each command.";
	public static String reloadHelpMsg = "§e/tm reload [all|config|lang] §rThis command allows you to reload datas from yaml files after manual modifications. All timers will be immediately resynchronized.";
	public static String resyncHelpMsg = "§e/tm resync [all|world] §rThis command will re-synchronize a single or all worlds timers, based on the startup server's time, the elapsed time and the current speed modifier.";
	public static String servtimeHelpMsg = "§e/tm servtime §rAdmins and console can display a debug/managing message, who displays the startup server's time, the current server's time and each world current time, start time and speed.";
	public static String setMultilangHelpMsg = "§e/tm set multilang [true|false] §rSet true or false to use an automatic translation for the /now command.";
	public static String setDefLangHelpMsg = "§e/tm set deflang [lg_LG] §rChoose the translation to use if player's locale doesn't exist in the lang.yml or when useMultiLang is false.";
	public static String setRefreshRateHelpMsg = "§e/tm set refreshrate [ticks] §rSet the delay (in ticks) before actualizing the speed stretch/expand effect. Must be an integer between " + refreshMin + " and " + refreshMax + ". Default value is " + defRefresh + " ticks, please note that a too small value can cause server lags.";
	public static String setSpeedHelpMsg = "§e/tm set speed [decimal] [all|world] §rThe decimal number argument will multiply the world(s) speed. Use 0 to freeze time, numbers from 0.1 to 0.9 to slow time, 1 to get normal speed and numbers bigger than 1 to speedup time. Value must be a decimal or integer number from 0.0 to " + speedMax + ".";
	public static String setStartHelpMsg = "§e/tm set start [ticks] [all|world] §rDefines the time at server startup for the specified world (or all of them). By default, all worlds will start at tick §e#0/24000§r. The timer(s) will be immediately resynchronized.";
	public static String setTimeHelpMsg = "§e/tm set time [ticks] [all|world] §rSets current time for the specified world (or all of them). Consider using this instead of the vanilla §e/time §rcommand. The tab completion also provides handy presets like \"day\", \"noon\", \"night\", \"midnight\", etc.";
	 // Help message when 'set' is used without additional argument
	public static String missingSetArgHelpMsg = "§e/tm help set [deflang|multilang|refreshrate|speed|start|time] §rThis command, use with arguments, permit to change plugin parameters.";
	
	// Check if schedule is already active
	public static boolean ScheduleIsOn = false;
	
	// Initialize server tick
	public static long initialTick;
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
		
		// #6. Synchronize worlds and create scheduled task for faking the time stretch/expand
		WorldSyncHandler.WorldSyncFirst();
		
		// #7. Confirmer la bonne activation du plugin dans la console
		Bukkit.getLogger().info(prefixTM + " " + plEnabledMsg);
	};

	/**
	 * 2. On Plugin disabling
	 */
	@Override
	public void onDisable() {
		// Confirmer la bonne désactivation du plugin dans la console
		this.saveConfig();
		LgFileHandler.SaveLangYml();
		Bukkit.getLogger().info(prefixTM + " " + plDisabledMsg);		 
	};

}