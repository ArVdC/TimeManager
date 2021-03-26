/******************
 *** MAIN CLASS ***
 ******************/

/**
 * Handle global variables and startup loading
 */

package net.vdcraft.arvdc.timemanager;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.vdcraft.arvdc.timemanager.Metrics;

import net.vdcraft.arvdc.timemanager.mainclass.CfgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.CmdsFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.McVersionHandler;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.SqlHandler;
import net.vdcraft.arvdc.timemanager.mainclass.UpdateHandler;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSleepHandler;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSyncHandler;
import net.vdcraft.arvdc.timemanager.placeholders.MVdWPAPIHandler;
import net.vdcraft.arvdc.timemanager.placeholders.PAPIHandler;

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
	protected static Double reqMcVToLoadPlugin = 4.06;
	protected static Double reqMcVForUpdate = 8.09;
	public static Double reqMcVForDaylightCycle = 13.0;

	// Current Minecraft server version decimals "x.xx" (without the "1." and eventually with a "x.0x" format - to permit comparisons)
	public static Double decimalOfMcVersion;

	// Enable/Disable debugging
	public static Boolean debugMode = false; // Displays user accessible debug msgs
	public static Boolean devMode = true; // Displays more verbose debug msgs
	public static Boolean timerMode = false; // Displays all timers calculations (= ultra-verbose mode)

	// Default config files values
	protected static String defTimeUnits = "hours";
	protected static long defWakeUpTick = 0L;
	protected static long defStart = 0L;
	protected static Integer defRefresh = 10;
	protected static Double defSpeed = 1.0;
	protected static String defSleep = "true";
	protected static String defSync = "false";
	protected static String defUpdateMsgSrc = "none";

	// Language to use if locale doesn't exist in the lang.yml = 'defaultLang'
	protected static String serverLang;

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
	protected static Long dawnStart = 0L;
	protected static Long dayStart = 1000L;
	protected static Long duskStart = 12000L;
	protected static Long nightStart = 13000L;
	protected static Long mcDayEnd = 24000L;
	
	// Expected time for the date change
	protected static String newDayStartsAt_0h00 = "00:00";
	protected static String newDayStartsAt_6h00 = "06:00";

	// Add each active schedule in corresponding list
	public static List<String> realSpeedSchedulerIsActive = new ArrayList<String>();
	public static List<String> syncSpeedSchedulerIsActive = new ArrayList<String>();
	public static List<String> asyncIncreaseSpeedSchedulerIsActive = new ArrayList<String>();
	public static List<String> asyncDecreaseSpeedSchedulerIsActive = new ArrayList<String>();
	public static List<String> commandsSchedulerIsActive = new ArrayList<String>();
	public static int cmdsTask;

	// Initialize server tick
	protected static Long initialTick;
	protected static String initialTime;

	// Config file keys
	protected static final String CF_VERSION = "version";
	protected static final String CF_DEFTIMEUNITS = "defTimeUnits";
	protected static final String CF_REFRESHRATE = "refreshRate";
	public static final String CF_WAKEUPTICK = "wakeUpTick";
	public static final String CF_NEWDAYAT = "newDayAt";
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
	protected static final String CF_BUKKIT = "bukkit";
	protected static final String CF_CURSE = "curse";
	protected static final String CF_TWITCH = "twitch";
	protected static final String CF_SPIGOT = "spigot";
	protected static final String CF_PAPER = "paper";
	protected static final String CF_GITHUB = "github";
	protected static final String CF_DEBUGMODE = "debugMode";
	protected static final String CF_PLACEHOLDER = "placeholders";
	protected static final String CF_PLACEHOLDER_PAPI = "PlaceholderAPI";
	protected static final String CF_PLACEHOLDER_MVDWPAPI = "MVdWPlaceholderAPI";
	// Lang file keys
	protected static final String CF_USEMULTILANG = "useMultiLang";
	protected static final String CF_DEFAULTLANG = "defaultLang";
	public static final String CF_LANGUAGES = "languages";
	protected static final String CF_DEFAULT = "default";
	protected static final String CF_PREFIX = "prefix";
	protected static final String CF_MSG = "msg";
	protected static final String CF_NOMSG = "noMsg";
	public static final String CF_DAYPARTS = "dayparts";
	protected static final String CF_DAY = "day";
	protected static final String CF_DUSK = "dusk";
	protected static final String CF_NIGHT = "night";
	protected static final String CF_DAWN = "dawn";
	public static final String CF_MONTHS = "months";
	protected static final String CF_MONTH_01 = "m01";
	protected static final String CF_MONTH_02 = "m02";
	protected static final String CF_MONTH_03 = "m03";
	protected static final String CF_MONTH_04 = "m04";
	protected static final String CF_MONTH_05 = "m05";
	protected static final String CF_MONTH_06 = "m06";
	protected static final String CF_MONTH_07 = "m07";
	protected static final String CF_MONTH_08 = "m08";
	protected static final String CF_MONTH_09 = "m09";
	protected static final String CF_MONTH_10 = "m10";
	protected static final String CF_MONTH_11 = "m11";
	protected static final String CF_MONTH_12 = "m12";
	// Cmds file keys
	protected static final String CF_USECOMMANDS = "useCmds";
	protected static final String CF_COMMANDSLIST = "cmdsList";
	protected static final String CF_CMDS = "cmds";
	protected static final String CF_REFTIME = "refTime";
	protected static final String CF_HOUR = "hour";
	protected static final String CF_DATE = "date";
	protected static final String CF_REPEATFREQ = "repeatFreq";

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
	protected static final String CMD_SET_DATE = "date";
	protected static final String CMD_SET_DEBUG = "debugMode";
	protected static final String CMD_SET_DEFLANG = "defLang";
	protected static final String CMD_SET_E_DAYS = "elapsedDays";
	protected static final String CMD_SET_INITIALTICK = "initialTick";
	protected static final String CMD_SET_MULTILANG = "multiLang";
	protected static final String CMD_SET_REFRESHRATE = "refreshRate";
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
	protected static final String LANGFILENAME = "lang.yml";
	protected static final String CMDSFILENAME = "cmds.yml";

	// Config and Lang files targets
	public File configFileYaml = new File(this.getDataFolder(), CONFIGFILENAME);
	public File langFileYaml = new File(this.getDataFolder(), LANGFILENAME);
	public FileConfiguration langConf = YamlConfiguration.loadConfiguration(langFileYaml);
	public File cmdsFileYaml = new File(this.getDataFolder(), CMDSFILENAME);
	public FileConfiguration cmdsConf = YamlConfiguration.loadConfiguration(cmdsFileYaml);

	// Use a lang_backup file
	protected final static String LANGBCKPFILENAME = "lang_backup.yml";
	public File langBckpFileYaml = new File(this.getDataFolder(), LANGBCKPFILENAME);
	public FileConfiguration langBckpConf = YamlConfiguration.loadConfiguration(langBckpFileYaml);

	// Admin and Console messages
	// Prefixes
	protected static String prefixTM = "[TimeManager]";
	protected static String prefixTMColor = "§8§l[§6§lTimeManager§8§l]§r";
	public static String prefixDebugMode = "§8§l[§e§lTimeManager§8§l]§b";

	// Plugin enable & reload messages
	protected static String plEnabledMsg = "The plugin is now enabled, timers will be initialized when all the other plugins are loaded.";
	protected static String plBadVersionMsg = "§cThe plugin is not compatible with versions under 1." + reqMcVToLoadPlugin + " and you are running a ";
	protected static String plDisabledMsg = "The plugin is now disabled.";
	protected static String cfgFileCreateMsg = "The configuration file was created.";
	protected static String lgFileCreaMsg = "The language file was created.";
	protected static String cmdsFileCreaMsg = "The commands file was created.";
	protected static String cfgFileExistMsg = "The configuration file already exists.";
	protected static String lgFileExistMsg = "The language file already exists.";
	protected static String cmdsFileExistMsg = "The commands file already exists.";
	protected static String cfgVersionMsg = "Enabled " + CONFIGFILENAME + " v";
	protected static String lgVersionMsg = "Enabled " + LANGFILENAME + " v";
	protected static String cmdsVersionMsg = "Enabled " + CMDSFILENAME + " v";
	protected static String cfgFileTryReloadMsg = "Reloading the configuration file.";
	protected static String cfgFileReloadMsg = "The configuration file was reloaded.";
	protected static String lgFileTryReloadMsg = "Reloading the language file.";
	protected static String lgFileReloadMsg = "The language file was reloaded.";
	protected static String cmdsFileTryReloadMsg = "Reloading the commands file.";
	protected static String cmdsFileReloadMsg = "The commands file was reloaded.";
	protected static String worldsCheckMsg = "The worlds list was actualized.";
	protected static String multiLangIsOnMsg = "Multilanguage support is enable.";
	protected static String multiLangIsOffMsg = "Multilanguage support is disable.";
	protected static String multiLangDoesntWork = "Multilanguage is not supported by CraftBukkit under the 1.12 version. Upgrade or try with Spigot.";
	protected static String defLangCheckMsg = "Default translation is actually set to";
	protected static String defLangResetMsg = "is missing or corrupt, back to the default parameter.";
	protected static String defLangOkMsg = "exists in " + LANGFILENAME + ", keep it as default translation.";
	protected static String defLangNonOkMsg = "Your " + LANGFILENAME + " is partially corrupt. You should make a backup of your file by renaming it, then reload the lang file with the command.";
	protected static String LangFileNonOkMsg = "Your " + LANGFILENAME + " couldn't be updated. You should make a backup of your file by renaming it, then reload the lang file with the command.";
	protected static String langFileUpdateMsg = "Your " + LANGFILENAME + " was renamed " + LANGBCKPFILENAME + ". A new file was created and automatically completed with your old data.";
	protected static String resyncIntroMsg = "All worlds have been syncronized to the server time. If you want to keep them synchronized, set their 'sync' option to true.";
	protected static String cmdsIsOnMsg = "The commands scheduler is enable.";
	protected static String cmdsIsOffMsg = "The commands scheduler is disable.";
	// Cmds resync & checkTime
	protected static String serverInitTickMsg = "The server's initial tick is";
	protected static String serverCurrentTickMsg = "The server's current tick is";
	protected static String worldCurrentElapsedDaysMsg = "is running since";
	protected static String worldCurrentStartMsg = "starts at";
	protected static String worldCurrentTickMsg = "'s current tick is";
	protected static String worldCurrentTimeMsg = "'s current time is";
	protected static String worldCurrentSpeedMsg = "'s current speed is";
	protected static String worldCurrentDaySpeedMsg = "'s current day speed is";
	protected static String worldCurrentNightSpeedMsg = "'s current night speed is";
	protected static String worldRealSpeedMsg = "set to match real UTC time";
	protected static String worldCurrentSyncMsg = "synchronized to the server time";
	protected static String worldCurrentSleepMsg = "'s 'sleep' option is set to";

	// Cmd set elapsedDays
	protected static String worldFullTimeChgMsg = "Total elapsed days in world";
	protected static String tooLateForDayZeroMsg1 = "It is too late (";
	protected static String tooLateForDayZeroMsg2 = ") to return to the first day. First use \"/tm set time\" to set a value between 6:00 and 23:59.";
	

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
	protected static String worldRealSyncTimeChgMsg = "could not be changed. Please use the '/tm set start' command instead if you want to change the time zone.";
	protected static String worldTimeNoChange = "is synchronized ";
	
	// Cmd set update
	protected static String updateEnableCheckMsg = "The plugin update message at server start will use";
	protected static String updateDisableCheckMsg = "The plugin update message at server start is now";
	protected static String updateCommandsDisabledMsg = "The update functions are disabled under MC v1.";
	protected static String noUpdateMsg = "No update was found, you are running the latest version.";
	protected static String urlFailMsg = "No update was found, the provided URL was not recognized.";
	protected static String serverFailMsg = "No update was found, the server could not be reached.";

	// Errors messages (B&W)
	protected static String rateFormatMsg = "Refresh rate must be an integer number.";
	protected static String wakeUpTickFormatMsg = "Wake up tick must be an integer number, default value will be used.";
	protected static String startTickFormatMsg = "Start tick must be an integer number, default value will be used.";
	protected static String utcFormatMsg = "Time shift must be formatted as \"UTC\" followed by \"+ or -\" and an integer number, without space (e.g. UTC+1), default value will be used.";
	protected static String yearFormatMsg = "Year number must be an integer number between 1 and 9999.";
	protected static String monthFormatMsg = "Month number must be an integer number between 1 and 12.";
	protected static String dayFormatMsg = "Day number must be an integer number between 1 and ";
	protected static String dateFormatMsg = "Date must be \"today\", or be in the format yyyy-mm-dd, default value will be used.";
	protected static String tickFormatMsg = "Tick must be an integer number, a listed part of the day, or to be HH:mm:ss formatted, default value will be used.";
	protected static String hourFormatMsg = "Hour must be in the format HH:mm:ss, default value will be used.";
	protected static String speedFormatMsg = "Speed multiplier must be a number (integer or decimal) or the string \"realtime\", default value will be used.";
	protected static String wrongWorldMsg = "The name of the world you just typed does not exist.";
	protected static String wrongLangMsg = "The language you just typed does not exist in lang.yml file.";
	protected static String wrongYmlMsg = "The name of the yaml file you just typed does not exist.";
	protected static String missingArgMsg = "This command requires one or more additional argument(s).";
	protected static String isNotBooleanMsg = "This command requires a boolean argument, true or false.";
	protected static String couldNotSaveLang = "File " + LANGFILENAME + " couldn't be saved on disk. In worst case, delete the file then restart the server.";
	protected static String couldNotSaveCmds = "File " + CMDSFILENAME + " couldn't be saved on disk. In worst case, delete the file then restart the server.";
	protected static String checkLogMsg = "Please check the console or log file.";
	protected static String versionMCFormatMsg = "Unable to correctly determine your server MC version, the plugin will consider it is an old one.";
	protected static String versionTMFormatMsg = "Unable to correctly determine the plugin version. ";

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
	protected static String syncAdjustTrueDebugMsg = "The §esync§b option is forced to §atrue§b for the world";
	protected static String syncAdjustFalseDebugMsg = "The §esync§b option is forced to §cfalse§b for the world";
	protected static String sleepAdjustFalseDebugMsg = "The §esleep§b option is forced to §cfalse§b for the world";
	protected static String availableTranslationsDebugMsg = "Available translations are:";
	public static String daylightTrueDebugMsg = "The §edoDaylightCycle§b value is now set to §atrue§b for the world";
	protected static String daylightFalseDebugMsg = "The §edoDaylightCycle§b value is now set to §cfalse§b for the world";
	protected static String mcLocaleDebugMsg = "The locale will be determined by the Minecraft client.";
	protected static String pcLocaleDebugMsg = "The locale will be determined by the computer and §onot §bby the Minecraft client.";
	protected static String foundLocaleDebugMsg = "The locale found for the player";
	protected static String useLocaleDebugMsg = "The locale that will be used for the player";
	protected static String launchSchedulerDebugMsg = "If necessary, launch the scheduler corresponding to the asked speed value.";
	protected static String serverTypeQueryDebugMsg = "Try to get the server's type.";
	protected static String serverTypeResultDebugMsg = "You are running a";
	protected static String serverMcVersionQueryDebugMsg = "Try to get the server's Minecraft version.";
	protected static String serverMcVersionResultDebugMsg = "You are using a";
	protected static String completeVersionDebugMsg = "Complete version details:";
	protected static String noVersionNumberDebugMsg = "No MC version number could be recognized in";
	protected static String wrongVersionNumberDebugMsg = "Your MC version doesn't correspond to any decimal number:";
	protected static String LatestVersionPart1DebugMsg = "Last version on";
	protected static String LatestVersionPart2DebugMsg = "and you are running the";
	public static String sleepProcessStartsDebugMsg = "§b is sleeping now (1/100 ticks).";
	public static String sleepProcess99TicksDebugMsg = "Sleep time is almost reached (99/100 ticks).";
	public static String sleepProcess100TicksDebugMsg = "Sleep time is achieved (100/100 ticks).";
	public static String sleepProcessWaitMorningTicksDebugMsg = "From now on, waiting for the morning.";
	public static String sleepProcessAdjustMorningTicksDebugMsg = "The morning tick was adjusted to";
	public static String sleepProcessSleepForbid1DebugMsg = "Sleeping is forbid in the world";
	public static String sleepProcessSleepForbid2DebugMsg = "The process ends here.";
	public static String sleepProcessAwakeNoSleepDebugMsg = "without having been able to sleep.";
	public static String sleepOkMorningDebugMsg = "§aWake up, it's morning !!!";
	public static String sleepNoMorningDebugMsg = "§cToo late...  morning might never come.";

	// Debug Calculation for timer synchronization (with colors)
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
	
	// Sleep listener
	public static String sleepNewDayMsg = "The players slept and spent the night in the world";

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
	protected static long sqlInitialTickAutoUpdateValue = 2L;
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
		if (decimalOfMcVersion < reqMcVToLoadPlugin) {
			MsgHandler.colorMsg("§c" + plBadVersionMsg + "1." + decimalOfMcVersion + " server.");
		} else {

			// #1. Initiate this main class as the contain of the instance
			instanceMainClass = this;

			// #2. Activate the configuration file
			CfgFileHandler.loadConfig("first");

			// #3. Activate the languages file
			LgFileHandler.loadLang("first");
			
			// #4. Activate the scheduler file
			CmdsFileHandler.loadCmds("first");

			// #5. Activate the class with admins commands
			CommandExecutor timemanagerExecutor = new AdminCmdExecutor();
			getCommand(CMD_TM).setExecutor(timemanagerExecutor);
			// Activate tab completion for admins commands
			getCommand(CMD_TM).setTabCompleter(new CreateSentenceCommand());

			// #6. Activate the class with players commands
			CommandExecutor nowExecutor = new PlayerCmdExecutor();
			getCommand(CMD_NOW).setExecutor(nowExecutor);

			// #7. Activate tab completion for players commands
			getCommand(CMD_NOW).setTabCompleter(new CreateSentenceCommand());

			// #8. Listen to sleep events
			getServer().getPluginManager().registerEvents(new WorldSleepHandler(), this);

			// #9. Synchronize worlds and create scheduled task for faking the time stretch/expand
			WorldSyncHandler.firstSync();

			// #10. Activate (or not) the placeholder APIs
			if (MainTM.getInstance().getConfig().getString(CF_PLACEHOLDER + "." + CF_PLACEHOLDER_PAPI).equalsIgnoreCase("true")
					&& Bukkit.getPluginManager().getPlugin(CF_PLACEHOLDER_PAPI) != null) {
				MsgHandler.debugMsg(CF_PLACEHOLDER_PAPI + " detected.");
				new PAPIHandler(this).register();
			}
			if (MainTM.getInstance().getConfig().getString(CF_PLACEHOLDER + "." + CF_PLACEHOLDER_MVDWPAPI).equalsIgnoreCase("true")
					&& Bukkit.getPluginManager().getPlugin(CF_PLACEHOLDER_MVDWPAPI) != null) {
				MsgHandler.debugMsg(CF_PLACEHOLDER_MVDWPAPI + " detected.");
				MVdWPAPIHandler.loadMVdWPlaceholderAPI();
			}
			
			// #11. bStats
			int pluginId = 10412;
	        @SuppressWarnings("unused")
	        Metrics metrics = new Metrics(this, pluginId);

			// #12. Confirm activation in console
			MsgHandler.infoMsg(plEnabledMsg);
			
			
			// #13. Check for an update
			if (decimalOfMcVersion >= MainTM.reqMcVForUpdate)
				UpdateHandler.delayCheckForUpdate();
			else MsgHandler.warnMsg(updateCommandsDisabledMsg + reqMcVForUpdate.toString().replace(".0", "."));
		}
	}

	/**
	 * 2. On Plugin disabling
	 */
	@Override
	public void onDisable() {
		// #0. Don't disable the plugin with if not loaded first
		if (decimalOfMcVersion < reqMcVToLoadPlugin) {
		} else {

			// #1. Save YAMLs
			this.saveConfig();
			LgFileHandler.SaveLangYml();

			// #2. Close SQL connection
			SqlHandler.closeConnection("Host");
			SqlHandler.closeConnection("DB");

			// #3. Confirm disabling in console
			MsgHandler.infoMsg(plDisabledMsg);
		}
	}
	
	/**
	 * 3. Custom wait
	 */
	public static void waitTime(Integer msToWait) {
		try {
			Thread.sleep(msToWait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

};