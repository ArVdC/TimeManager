/******************
 *** MAIN CLASS ***
 ******************/

/**
 * Handle global variables and startup loading
 */

package net.vdcraft.arvdc.timemanager;

import java.io.File;
import java.sql.Connection;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.vdcraft.arvdc.timemanager.mainclass.CfgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.McVersionHandler;
import net.vdcraft.arvdc.timemanager.mainclass.SqlHandler;
import net.vdcraft.arvdc.timemanager.mainclass.UpdateHandler;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSleepHandler;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSyncHandler;

public class MainTM extends JavaPlugin {

	/*****************
	 *** VARIABLES ***
	 *****************/

	// Main class
	protected static MainTM instanceMainClass;

	// Plugin version
	protected static String versionTM() {
		return instanceMainClass.getDescription().getVersion().toString();
	}

	// Minecraft server minimal required version decimals "x.xx" (without the "1."
	// and eventually with a "x.0x" format - to permit comparisons)
	protected static Double minRequiredMcVersion = 4.06;
	protected static Double requiredMcVersionForUpdate = 8.09;

	// Current Minecraft server version decimals "x.xx" (without the "1." and eventually with a "x.0x" format - to permit comparisons)
	public static Double decimalOfMcVersion;

	// Enable/Disable debugging
	public static Boolean debugMode = false; // Displays user accessible debug msgs
	public static Boolean devMode = false; // Displays more verbose debug msgs
	public static Boolean timerMode = false; // Displays all timers calculations (= ultra-verbose mode)

	// Commands names
	protected static final String CMD_TM = "tm";
	protected static final String CMD_NOW = "now";

	// /tm sub-commands names
	protected static final String CMD_CHECKCONFIG = "checkconfig";
	protected static final String CMD_CHECKSQL = "checksql";
	protected static final String CMD_CHECKTIME = "checktime";
	protected static final String CMD_CHECKUPDATE = "checkupdate";
	protected static final String CMD_HELP = "help";
	protected static final String CMD_RELOAD = "reload";
	protected static final String CMD_RESYNC = "resync";
	protected static final String CMD_SET = "set";

	// /tm sub-commands names
	protected static final String CMD_SET_DEBUG = "debugmode";
	protected static final String CMD_SET_DEFLANG = "deflang";
	protected static final String CMD_SET_INITIALTICK = "initialtick";
	protected static final String CMD_SET_MULTILANG = "multilang";
	protected static final String CMD_SET_REFRESHRATE = "refreshrate";
	protected static final String CMD_SET_SLEEP= "sleep";
	protected static final String CMD_SET_SPEED = "speed";
	protected static final String CMD_SET_D_SPEED = "speedDay";
	protected static final String CMD_SET_N_SPEED = "speedNight";
	protected static final String CMD_SET_START = "start";
	protected static final String CMD_SET_SYNC = "sync";
	protected static final String CMD_SET_TIME = "time";
	protected static final String CMD_SET_UPDATE = "update";

	// Files names
	protected static final String CONFIGFILENAME = "config.yml";
	protected static final String lANGFILENAME = "lang.yml";

	// Config and Lang files targets
	public File configFileYaml = new File(this.getDataFolder(), CONFIGFILENAME);
	public File langFileYaml = new File(this.getDataFolder(), lANGFILENAME);
	public FileConfiguration langConf = YamlConfiguration.loadConfiguration(langFileYaml);

	// Admin and Console messages
	// Prefixes
	protected static String prefixTM = "[TimeManager]";
	protected static String prefixTMColor = "§8§l[§6§lTimeManager§8§l]§r";
	public static String prefixDebugMode = "§8§l[§e§lTimeManager§8§l]§b";

	// Plugin enable & reload messages
	protected static String plEnabledMsg = "The plugin is now enabled, timers will be initialized when all the other plugins are loaded.";
	protected static String plBadVersionMsg = "§cThe plugin is not compatible with versions under 1." + minRequiredMcVersion + " and you are running a ";
	protected static String plDisabledMsg = "The plugin is now disabled.";
	protected static String cfgFileCreaMsg = "The configuration file was created.";
	protected static String lgFileCreaMsg = "The language file was created.";
	protected static String cfgFileExistMsg = "The configuration file already exists.";
	protected static String lgFileExistMsg = "The language file already exists.";
	protected static String cfgVersionMsg = "Enabled " + CONFIGFILENAME + " v";
	protected static String lgVersionMsg = "Enabled " + lANGFILENAME + " v";
	protected static String cfgFileTryReloadMsg = "Reloading the configuration file.";
	protected static String cfgFileReloadMsg = "The configuration file was reloaded.";
	protected static String lgFileTryReloadMsg = "Reloading the language file.";
	protected static String lgFileReloadMsg = "The language file was reloaded.";
	protected static String worldsCheckMsg = "The worlds list was actualized.";
	protected static String multiLangIsOnMsg = "Multilanguage support is enable.";
	protected static String multiLangIsOffMsg = "Multilanguage support is disable.";
	protected static String multiLangDoesntWork = "Multilanguage is not supported by CraftBukkit under the 1.12 version. Upgrade or try with Spigot.";
	protected static String defLangCheckMsg = "Default translation is actually set to";
	protected static String defLangResetMsg = "is missing or corrupt, back to the default parameter.";
	protected static String defLangOkMsg = "exists in " + lANGFILENAME + ", keep it as default translation.";
	protected static String resyncIntroMsg = "All worlds have been syncronized to the server time. If you want to keep them synchronized, set their 'sync' option to true.";

	// Cmds resync & checkTime
	protected static String serverInitTickMsg = "The server's initial tick is";
	protected static String serverCurrentTickMsg = "The server's current tick is";
	protected static String worldCurrentStartMsg = "starts at";
	protected static String worldCurrentTickMsg = "'s current tick is";
	protected static String worldCurrentTimeMsg = "'s current time is";
	protected static String worldCurrentSpeedMsg = "'s current speed is";
	protected static String worldCurrentDaySpeedMsg = "'s current day speed is";
	protected static String worldCurrentNightSpeedMsg = "'s current night speed is";
	protected static String worldRealSpeedMsg = "set to match real UTC time";
	protected static String worldCurrentSyncMsg = "synchronized to the server time";
	protected static String worldCurrentSleepMsg = "'s 'sleep' option is set to";

	// Cmd resync
	protected static String resyncDoneMsg = "had its time re" + worldCurrentSyncMsg;
	protected static String noResyncNeededMsg = "is already synchronized to the server time.";

	// Cmd set refreshRate
	protected static String refreshRateMsg = "The time stretch/expand will refresh every";

	// Cmd set initial tick
	protected static String initialTickYmlMsg = "The new initial tick will be saved in the config.yml file.";
	protected static String initialTickSqlMsg = "The new initial tick will be saved in the MySQL database.";
	protected static String initialTickGetFromSqlMsg = "The new initial tick was get from the MySQL database.";
	protected static String initialTickNoChgMsg = "The tick you just entered is the same as the current one.";

	// Cmd set sleep
	protected static String worldSleepTrueChgMsg = "It is allowed to sleep until the dawn in the world";
	protected static String worldSleepFalseChgMsg = "It is forbidden to sleep until the dawn in the world";
	protected static String worldSleepNoChgMsg = "Impossible to change the 'sleep' option cause of the actual speed setting for the world";

	// Cmd set speed
	protected static String worldSpeedChgIntro = "The speed of the world";
	protected static String worldDaySpeedChgIntro = "The day speed of the world";
	protected static String worldNightSpeedChgIntro = "The night speed of the world";
	protected static String worldSpeedChgMsg = "is now multiplied by";
	protected static String worldRealSpeedChgMsg = "will now match the real time, the 'start' value will defines the time zone.";

	// Cmd set start
	protected static String worldStartChgMsg1 = "The time of the world";
	protected static String worldStartChgMsg2 = "was resynchronized using its new 'start' value.";

	// Cmd set sync
	protected static String worldSyncTrueChgMsg = "The synchronization to the server time is activated for the world";
	protected static String worldSyncFalseChgMsg = "The synchronization to the server time is disabled for the world";
	protected static String worldSyncNoChgMsg = "Impossible to change the 'sync' option cause of the actual speed setting for the world";
	protected static String world24hNoSyncChgMsg = "is synchronized to real UTC time and doesn't need to be resynchronized.";
	protected static String worldFrozenNoSyncChgMsg = "has its speed frozen and doesn't need to be resynchronized.";
	protected static String worldSyncSleepChgMsg = "'sleep' option was forced to false, cause of its synchronization value.";
	protected static String sleepWorldSyncChgMsg = "'sync' option was forced to false in order to allow players to sleep until the dawn.";

	// Cmd set time
	protected static String worldTimeChgMsg1 = "The current time of the world";
	protected static String worldTimeChgMsg2 = "is now set to";
	protected static String worldSyncTimeChgMsg = "is synchronized to the server time, its 'start' value will be changed to modify its current time.";
	protected static String worldRealSyncTimeChgMsg = "will always match real UTC minutes and seconds, a new rounded 'start' value will be calculated.";

	// Cmd set update
	protected static String updateEnableCheckMsg = "The plugin update message at server start will use";
	protected static String updateDisableCheckMsg = "The plugin update message at server start is now";
	protected static String updateCommandsDisabledMsg = "The update functions are disabled under MC v1.";
	protected static String noUpdateMsg = "No update was found, you are running the latest version.";
	protected static String urlFailMsg = "No update was found, the provided URL was not recognized.";
	protected static String serverFailMsg = "No update was found, the server could not be reached.";

	// Errors messages
	protected static String rateNotNbMsg = "Refresh rate must be an integer number.";
	protected static String tickNotNbMsg = "Tick must be an integer number, a listed part of the day, or to be HH:mm:ss formated.";
	protected static String speedNotNbMsg = "Speed multiplier must be a number (integer or decimal) or the string \"realtime\".";
	protected static String wrongWorldMsg = "The name of the world you just typed does not exist.";
	protected static String wrongLangMsg = "The language you just typed does not exist in lang.yml file.";
	protected static String wrongYmlMsg = "The name of the yaml file you just typed does not exist.";
	protected static String missingArgMsg = "This command requires one or more additional argument(s).";
	protected static String isNotBooleanMsg = "This command requires a boolean argument, true or false.";
	protected static String couldNotSaveLang = "File " + lANGFILENAME + " couldn't be saved on disk. In worst case, delete the file then restart the server.";
	protected static String checkLogMsg = "Please check the console or log file.";
	protected static String unknowVersionMsg = "Impossible to determine properly the MC version of your server, the plugin will consider it is an old one.";


	// Debug messages (with colors)
	protected static String enableDebugModeDebugMsg = "The debug mode is §aenabled§b.";
	protected static String disableDebugModeDebugMsg = "The debug mode is §cdisabled§b.";
	protected static String cfgOptionsCheckDebugMsg = "The options will be now checked for each world.";
	protected static String refrehWorldsListDebugMsg = "Refreshing the §eworldsList§b keys in config.yml.";
	protected static String worldsRawListDebugMsg = "Raw list of all loaded worlds:";
	protected static String worldsFormatListDebugMsg = "Name's list of all loaded worlds:";
	protected static String worldsCfgListDebugMsg = "Worlds list from the config.yml:";
	protected static String delWorldDebugMsg = "was deleted from the config list.";
	protected static String daySpeedAdjustDebugMsg = "The §edaySpeed§b option value was converted from";
	protected static String nightSpeedAdjustDebugMsg = "The §enightSpeed§b option value was converted from";
	protected static String startAdjustDebugMsg = "The §estart§b option value was converted from";
	protected static String syncAdjustTrueDebugMsg = "The §esync§b option is forced to §atrue §bfor the world";
	protected static String syncAdjustFalseDebugMsg = "The §esync§b option is forced to §cfalse §bfor the world";
	protected static String sleepAdjustFalseDebugMsg = "The §esleep§b option is forced to §cfalse §bfor the world";
	protected static String availableTranslationsDebugMsg = "Available translations are:";
	protected static String daylightTrueDebugMsg = "The gamerule §edoDaylightCycle§b is now set to §atrue§b for the world";
	protected static String daylightFalseDebugMsg = "The gamerule §edoDaylightCycle§b is now set to §cfalse§b for the world";
	protected static String mcLocaleDebugMsg = "The locale will be determined by the Minecraft client.";
	protected static String pcLocaleDebugMsg = "The locale will be determined by the computer and §onot §bby the Minecraft client.";
	protected static String foundLocaleDebugMsg = "The locale found for the player";
	protected static String useLocaleDebugMsg = "The locale that will be used for the player";
	protected static String launchSchedulerDebugMsg = "If off, launch the scheduler corresponding to the asked speed value.";
	protected static String serverTypeQueryDebugMsg = "Try to get the server's type.";
	protected static String serverTypeResultDebugMsg = "You are running a";
	protected static String serverMcVersionQueryDebugMsg = "Try to get the server's Minecraft version.";
	protected static String serverMcVersionResultDebugMsg = "You are using a";
	protected static String completeVersionDebugMsg = "Complete version details:";
	protected static String noVersionNumberDebugMsg = "No MC version number could be recognized in";
	protected static String wrongVersionNumberDebugMsg = "Your MC version doesn't correspond to any decimal number:";
	protected static String LatestVersionPart1DebugMsg = "Last version on";
	protected static String LatestVersionPart2DebugMsg = "and you are running the";

	// Debug Calculation for timer synchronization
	protected static String actualTimeVar = "§c[actualTime]§b";
	protected static String adjustedElapsedTimeVar = "§5[adjustedElapsedTime]§b";
	protected static String adjustedTicksVar = "§3[adjustedTick]§b";
	protected static String askedTimeVar = "§8[askedTime]§b";
	protected static String currentTickVar = "§8[currentTick]§b";
	protected static String currentServerTickVar = "§8[currentServerTick]§b";
	protected static String elapsedTimeVar = "§d[elapsedTime]§b";
	protected static String initialTickVar = "§7[initialTick]§b";
	protected static String mcTimeRatioVar = "§6[mcTimeRatio]§b";
	protected static String sixHoursLessVar = "§9[sixHoursLess]§b";
	protected static String speedModifierVar = "§a[speedModifier]§b";
	protected static String daySpeedModifierVar = "§a[daySpeedModifier]§b";
	protected static String nightSpeedModifierVar = "§2[nightspeedModifier]§b";
	protected static String halfDaylightCycleVar = "§7[halfDaylightCycle]§b";
	protected static String serverRemainingTimeVar = "§5[serverRemainingTime]§b";
	protected static String ticksInOneDayVar = "§6[ticksInOneDay]§b";
	protected static String worldStartAtVar = "§e[worldStartAt]§b";
	protected static String oldWorldStartAtVar = "§3[oldWorldStartAt]§b";
	protected static String actualTimeCalculation = actualTimeVar + " = " + worldStartAtVar + " + " + adjustedElapsedTimeVar;
	protected static String elapsedTimeCalculation = elapsedTimeVar + " = (" + currentServerTickVar + " - " + initialTickVar + ") % " + ticksInOneDayVar;
	protected static String adjustedElapsedTimeCalculation = adjustedElapsedTimeVar + " = " + elapsedTimeVar + " * " + speedModifierVar;
	protected static String worldStartAtCalculation = worldStartAtVar + " = " + oldWorldStartAtVar + " + " + askedTimeVar + " - " + actualTimeVar;
	protected static String adjustedTicksCalculation = adjustedTicksVar + " = " + currentTickVar + " / " + mcTimeRatioVar;
	protected static String realActualTimeCalculation = actualTimeVar + " = " + worldStartAtVar + " - " + sixHoursLessVar + " + " + adjustedTicksVar;

	// MySQL
	protected static String host;
	protected static String port;
	protected static String ssl;
	protected static String dbPrefix;
	protected static String database;
	protected static String tableName;
	protected static String username;
	protected static String password;
	protected static Connection connectionHost;
	protected static Connection connectionDB;
	protected static String connectionOkMsg = "is correctly responding on port";
	protected static Long sqlInitialTickAutoUpdateValue = 2L;
	protected static Boolean mySqlRefreshIsAlreadyOn = false;
	protected static String sqlInitialTickAutoUpdateMsg = "If someone changes the InitialTickNb value in the MySQL database, the change will be reflected on this server within the next " + sqlInitialTickAutoUpdateValue + " minutes.";

	// mySQL errors messages
	protected static String tryReachHostMsg = "Trying to reach the provided mySQL host";
	protected static String checkConfigMsg = "Please check the " + CONFIGFILENAME + " file and set the debugMode to true to see error details.";
	protected static String connectionFailMsg = "Something prevented to establish a connection with provided host";
	protected static String dbCreationFailMsg = "Something prevented the database creation.";
	protected static String tableCreationFailMsg = "Something prevented the table creation.";
	protected static String datasCreationFailMsg = "Something preventeds datas from being written.";
	protected static String datasOverridingFailMsg = "Something keeps the datas from being updated.";
	protected static String tableReachFailMsg = "The table where the reference tick should be stocked is unreachable.";
	protected static String disconnectionFailMsg = "Something prevented the mySQL disconnection.";

	// Default config files values
	protected static String defTimeUnits = "hours";
	protected static Long defStart = 0L;
	protected static Integer defRefresh = 10;
	protected static Double defSpeed = 1.0;
	protected static String defSleep = "true";
	protected static String defSync = "false";

	// Min and Max refresh parameters in ticks
	protected static Integer refreshMin = 2;
	protected static Integer refreshMax = 20;

	// Handle the current refresh rate
	protected static Integer refreshRateInt;
	protected static Long refreshRateLong;

	// Max speed modifier (Min need to be 0)
	protected static Double speedMax = 10.0;

	// Number who make time turn real
	protected static Double realtimeSpeed = 24.0;

	// DayParts in ticks
	protected static Integer dawnStart = 0;
	protected static Integer dayStart = 1000;
	protected static Integer duskStart = 12000;
	protected static Integer nightStart = 13000;
	protected static Integer mcDayEnd = 24000;

	// Check if schedule is already active
	public static Boolean increaseScheduleIsOn = false;
	public static Boolean decreaseScheduleIsOn = false;
	public static Boolean realScheduleIsOn = false;

	// Initialize server tick
	protected static Long initialTick;
	protected static String initialTime;

	// Language to use if locale doesn't exist in the lang.yml = 'defaultLang'
	protected static String serverLang;

	// Config file keys
	protected static final String CF_VERSION = "version";
	protected static final String CF_DEFTIMEUNITS = "defTimeUnits";
	protected static final String CF_REFRESHRATE = "refreshRate";
	public static final String CF_WAKEUPTICK = "wakeUpTick";
	public static final String CF_WORLDSLIST = "worldsList";
	public static final String CF_START = "start";
	public static final String CF_SPEED = "speed";
	public static final String CF_D_SPEED = "daySpeed";
	public static final String CF_N_SPEED = "nightSpeed";
	public static final String CF_SLEEP = "sleep";
	public static final String CF_SYNC = "sync";
	protected static final String CF_INITIALTICK = "initialTick";
	protected static final String CF_INITIALTICKNB = "initialTickNb";
	protected static final String CF_RESETONSTARTUP = "resetOnStartup";
	protected static final String CF_USEMYSQL = "useMySql";
	protected static final String CF_MYSQL = "mySql";
	protected static final String CF_HOST = "host";
	protected static final String CF_PORT = "port";
	protected static final String CF_SSL = "ssl";
	protected static final String CF_DBPREFIX = "dbPrefix";
	protected static final String CF_DATABASE = "database";
	protected static final String CF_TABLE = "table";
	protected static final String CF_USERNAME = "username";
	protected static final String CF_PASSWORD = "password";
	protected static final String CF_UPDATEMSGSRC = "updateMsgSrc";
	protected static final String CF_DEBUGMODE = "debugMode";
	protected static final String CF_BUKKIT = "bukkit";
	protected static final String CF_CURSE = "curse";
	protected static final String CF_TWITCH = "twitch";
	protected static final String CF_SPIGOT = "spigot";
	protected static final String CF_PAPER = "paper";
	protected static final String CF_GITHUB = "github";

	// Lang file keys
	protected static final String CF_USEMULTILANG = "useMultiLang";
	protected static final String CF_DEFAULTLANG = "defaultLang";
	protected static final String CF_lANGUAGES = "languages";
	protected static final String CF_DEFAULT = "default";
	protected static final String CF_PREFIX = "prefix";
	protected static final String CF_MSG = "msg";
	protected static final String CF_NOMSG = "noMsg";
	protected static final String CF_DAYPARTS = "dayparts";
	protected static final String CF_DAY = "day";
	protected static final String CF_DUSK = "dusk";
	protected static final String CF_NIGHT = "night";
	protected static final String CF_DAWN = "dawn";

	/******************
	 ***** METHOD *****
	 ******************/

	/**
	 * Instantiate the main class 'MainTM'
	 */
	public static MainTM getInstance() {
		return instanceMainClass;
	}

	/******************
	 ***** EVENTS *****
	 ******************/

	/**
	 * 1. On Plugin enabling
	 */
	@Override
	public void onEnable() {

		// #0. Don't start the plugin with too old versions of the game
		decimalOfMcVersion = McVersionHandler.KeepDecimalOfMcVersion();
		if (decimalOfMcVersion < minRequiredMcVersion) {
			Bukkit.getServer().getConsoleSender().sendMessage(prefixTM + " §c" + plBadVersionMsg + "1." + decimalOfMcVersion + " server.");
		} else {

			// #1. Initiate this main class as the contain of the instance
			instanceMainClass = this;

			// #2. Activate the configuration file
			CfgFileHandler.loadConfig("first");

			// #3. Activate the languages file
			LgFileHandler.loadLang("first");

			// #4. Activate the class with admins commands
			CommandExecutor timemanagerExecutor = new AdminCmdExecutor();
			getCommand(CMD_TM).setExecutor(timemanagerExecutor);
			// Activate tab completion for admins commands
			getCommand(CMD_TM).setTabCompleter(new CreateSentenceCommand());

			// #5. Activate the class with players commands
			CommandExecutor nowExecutor = new PlayerCmdExecutor();
			getCommand(CMD_NOW).setExecutor(nowExecutor);

			// #6. Activate tab completion for players commands
			getCommand(CMD_NOW).setTabCompleter(new CreateSentenceCommand());

			// #7. Listen to sleep events
			getServer().getPluginManager().registerEvents(new WorldSleepHandler(), this);

			// #8. Synchronize worlds and create scheduled task for faking the time
			// stretch/expand
			WorldSyncHandler.firstSync();

			// #9. Confirm activation in console
			Bukkit.getLogger().info(prefixTM + " " + plEnabledMsg);

			// #10. Check for an update
			if (MainTM.decimalOfMcVersion >= MainTM.requiredMcVersionForUpdate)
				UpdateHandler.delayCheckForUpdate();
			else Bukkit.getLogger().warning(prefixTM + " " + updateCommandsDisabledMsg + requiredMcVersionForUpdate.toString().replace(".0", "."));
		}
	}

	/**
	 * 2. On Plugin disabling
	 */
	@Override
	public void onDisable() {
		// #0. Don't disable the plugin with if not loaded first
		if (decimalOfMcVersion < minRequiredMcVersion) {
		} else {

			// #1. Save YAMLs
			this.saveConfig();
			LgFileHandler.SaveLangYml();

			// #2. Close SQL connection
			SqlHandler.closeConnection("Host");
			SqlHandler.closeConnection("DB");

			// #3. Confirm disabling in console
			Bukkit.getLogger().info(prefixTM + " " + plDisabledMsg);
		}
	}

	/**
	 * 3. Custom wait
	 */
	protected static void waitTime(Integer ticksToWait) {
		try {
			Thread.sleep(ticksToWait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

};