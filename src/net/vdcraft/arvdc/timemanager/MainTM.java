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
//import org.bukkit.GameRule;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import net.vdcraft.arvdc.timemanager.Metrics;
import net.vdcraft.arvdc.timemanager.mainclass.BooksHandler;
import net.vdcraft.arvdc.timemanager.mainclass.CfgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.CmdsFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.HiddenListenerHandler;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.McVersionHandler;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.SignsHandler;
import net.vdcraft.arvdc.timemanager.mainclass.SqlHandler;
import net.vdcraft.arvdc.timemanager.mainclass.UpdateHandler;
import net.vdcraft.arvdc.timemanager.mainclass.WorldListHandler;
import net.vdcraft.arvdc.timemanager.mainclass.SleepHandler;
import net.vdcraft.arvdc.timemanager.mainclass.SyncHandler;
import net.vdcraft.arvdc.timemanager.placeholders.ChatHandler;
import net.vdcraft.arvdc.timemanager.placeholders.ConsoleCommandHandler;
import net.vdcraft.arvdc.timemanager.placeholders.PlayerCommandHandler;
import net.vdcraft.arvdc.timemanager.placeholders.MVdWPAPIHandler;
import net.vdcraft.arvdc.timemanager.placeholders.PAPIHandler;

@SuppressWarnings("unused")
public class MainTM extends JavaPlugin {

	/*****************
	 *** VARIABLES ***
	 *****************/

	// Main class
	public static MainTM instanceMainClass;

	// Plugin name
	public static String nameTM() {
		return instanceMainClass.getDescription().getName().toString();
	}
	// Plugin version
	public static String versionTM() {
		return instanceMainClass.getDescription().getVersion().toString();
	}

	// Enable/Disable debugging
	public static Boolean debugMode = false; // Displays user accessible debug messages
	public static Boolean devMode = false; // Displays more verbose debug messages
	public static Boolean timerMode = false; // Displays all timers calculations (= ultra-verbose mode)

	// Current Minecraft server version decimals "x.xx" (without the "1." and eventually with a "x.0x" format - to permit comparisons)
	public static Double serverMcVersion;
	public static String serverType;
	
	// Minimal required Minecraft server version decimals "x.xx" (without the "1." and eventually with a "x.0x" format - to permit comparisons)
	protected static Double reqMcVToLoadPlugin = 4.06;  // Timemanager can only be loaded with MC 1.4.6 +
	protected static Double reqMcVForUpdate = 8.08; // Timemanager update message is only available since MC 1.8.8
	public static Double reqMcVForActionbarMsg = 9.0; // Actionbar appeared with MC 1.9.0
	public static Double reqMcVForTxtCompLegacyMsg = 12.0; // Actionbar had differences between bukkit and spigot until MC 1.9.0
	public static Double reqMcVToGetLocale = 12.0; // Locale detection is only available since MC 1.12.0
	public static Double reqMcVForGamerules = 13.0; // Gamerules syntax has been changed since MC 1.13.0
	public static Double reqMcVForStatistics = 13.0; // New statistics were added in MC 1.13.0
	public static Double maxMcVForTabCompHack = 13.02;  // Tab completion needed a hack to handle spaces in worlds names until MC 1.13.2
	public static Double reqMcVForSounds = 14.0; // New sounds were added in MC 1.14.0
	public static Double reqMcVForWakeup = 14.0; // HumanEntity.wakeup() method added in MC 1.14.0
	public static Double reqMcVForTimeSkipEvent = 15.0; // New TimeSkipEvent appeared in MC 1.15.0
	public static Double reqMcVForNewSendTitleMsg = 16.0; // Title's syntax changed in MC 1.16.0
	public static Double reqMcVForHexColors = 16.0; // Hexa colors recognition appeared with MC 1.16.0
	public static Double reqMcVForSleepPercentage = 17.0; // PLAYERS_SLEEPING_PERCENTAGE was added in 1.17.0
	public static Double reqMcVForDustTransition = 17.0; // Particle DustTransition appeared with MC 1.17.0
	public static Double reqMcVForCancelLeaveBedEvent = 17.0; // PlayerBedLeaveEvent was not cancellable before MC 1.17.0
	public static Double reqMcVForWorldIsBedWorks = 18.0; // World interface changed in MC 1.18
	public static Double reqMcVForConfigFile = 19.0; // ConfigFile options changed in MC 1.19

	// Default config files values
	protected static long defWakeUpTick = 0L;
	public static long defStart = 0L;
	protected static Integer defRefresh = 10;
	public static Double defSpeed = 1.0;
	public static String defSleep = "true";
	public static String defSync = "false";
	public static String defFirstStartTime = "default";
	public static String defNightSkipMode = "default";
	public static String defNightSkipNbPlayers = "100%";
	protected static String defUpdateMsgSrc = "none";
	protected static int defTitleFadeIn = 20;
	protected static int defTitleStay = 60;
	protected static int defTitleFadeOut = 20;

	// Language to use if locale doesn't exist in the lang.yml = 'defaultLang'
	protected static String serverLang;

	// Min and Max refresh parameters in ticks
	protected static Integer refreshMin = 2;
	protected static Integer refreshMax = 20;

	// Handle the current refresh rate
	protected static Integer refreshRateInt;
	public static Long refreshRateLong;

	// Max speed modifier (Min needs to be 0)
	protected static Double speedMax = 20.0;

	// Number who make time turn real
	public static Double realtimeSpeed = 24.0;

	// DayParts in ticks
	public static Long dawnStart = 23000L;
	protected static Long dayStart = 1000L;
	protected static Long duskStart = 12000L;
	protected static Long nightStart = 13000L;
	protected static Long mcDayEnd = 24000L;
	
	// Expected time for the date change
	protected static String newDayStartsAt_0h00 = "00:00";
	protected static String newDayStartsAt_6h00 = "06:00";
	
	// Add each active schedule in corresponding list
	public static List<String> realSpeedSchedulerIsActive = new ArrayList<String>();
	public static List<String> syncConstantSpeedSchedulerIsActive = new ArrayList<String>();
	public static List<String> syncVariableSpeedSchedulerIsActive = new ArrayList<String>();
	public static List<String> asyncIncreaseSpeedSchedulerIsActive = new ArrayList<String>();
	public static List<String> asyncDecreaseSpeedSchedulerIsActive = new ArrayList<String>();
	public static List<String> asyncNormalSpeedSchedulerIsActive = new ArrayList<String>();
	public static List<String> commandsSchedulerIsActive = new ArrayList<String>();
	public static int cmdsTask;

	// Initialize server tick
	protected static Long initialTick;
	protected static String initialTime;

	// Config file keys
	protected static final String CF_VERSION = "version";
	public static final String CF_REFRESHRATE = "refreshRate";
	public static final String CF_WAKEUPTICK = "wakeUpTick";
	public static final String CF_NEWDAYAT = "newDayAt";
	public static final String CF_WORLDSLIST = "worldsList";
	public static final String CF_START = "start";
	public static final String CF_SPEED = "speed";
	public static final String CF_D_SPEED = "daySpeed";
	public static final String CF_N_SPEED = "nightSpeed";
	public static final String CF_SLEEP = "sleep";
	public static final String CF_SYNC = "sync";
	public static final String CF_FIRSTSTARTTIME = "firstStartTime";
	public static final String CF_NIGHTSKIP_MODE = "nightSkipMode";
	public static final String CF_NIGHTSKIP_REQUIREDPLAYERS = "nightSkipRequiredPlayers";
	public static final String CF_NIGHTSKIP_LEGACYPERCENTAGE = "nightSkipLegacyPercentage";
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
	public static final String CF_PLACEHOLDERS = "placeholders";
	protected static final String CF_PLACEHOLDER_PAPI = "PlaceholderAPI";
	protected static final String CF_PLACEHOLDER_MVDWPAPI = "MVdWPlaceholderAPI";
	public static final String CF_PLACEHOLDER_CHAT = "inChatEnable";
	public static final String CF_PLACEHOLDER_CMDS = "inCommandsEnable";
	
	// Lang file keys
	protected static final String LG_USEMULTILANG = "useMultiLang";
	public static final String LG_DEFAULTLANG = "defaultLang";
	protected static final String LG_DEFAULTDISPLAY = "defaultDisplay";
	public static final String LG_LANGUAGES = "languages";
	public static final String LG_TITLES = "titles";
	public static final String LG_FADEIN = "fadeIn";
	public static final String LG_STAY = "stay";
	public static final String LG_FADEOUT = "fadeOut";
	protected static final String LG_DEFAULT = "default";
	protected static final String LG_PREFIX = "prefix";
	protected static final String LG_MSG = "msg";
	protected static final String LG_NETHERMSG = "netherMsg";
	protected static final String LG_ENDMSG = "endMsg";
	protected static final String LG_NOMSG = "noMsg";
	protected static final String LG_TITLE = "title";
	protected static final String LG_SUBTITLE = "subtitle";
	protected static final String LG_ACTIONBAR = "actionbar";
	public static final String LG_DAYPARTS = "dayparts";
	protected static final String LG_DAY = "day";
	protected static final String LG_DUSK = "dusk";
	protected static final String LG_NIGHT = "night";
	protected static final String LG_DAWN = "dawn";
	public static final String LG_DAYS = "days";
	protected static final String LG_DAY_01 = "d01";
	protected static final String LG_DAY_02 = "d02";
	protected static final String LG_DAY_03 = "d03";
	protected static final String LG_DAY_04 = "d04";
	protected static final String LG_DAY_05 = "d05";
	protected static final String LG_DAY_06 = "d06";
	protected static final String LG_DAY_07 = "d07";
	public static final String LG_MONTHS = "months";
	protected static final String LG_MONTH_01 = "m01";
	protected static final String LG_MONTH_02 = "m02";
	protected static final String LG_MONTH_03 = "m03";
	protected static final String LG_MONTH_04 = "m04";
	protected static final String LG_MONTH_05 = "m05";
	protected static final String LG_MONTH_06 = "m06";
	protected static final String LG_MONTH_07 = "m07";
	protected static final String LG_MONTH_08 = "m08";
	protected static final String LG_MONTH_09 = "m09";
	protected static final String LG_MONTH_10 = "m10";
	protected static final String LG_MONTH_11 = "m11";
	protected static final String LG_MONTH_12 = "m12";
	
	// Cmds file keys
	protected static final String CMDS_USECOMMANDS = "useCmds";
	protected static final String CMDS_COMMANDSLIST = "cmdsList";
	protected static final String CMDS_CMDS = "cmds";
	protected static final String CMDS_REFTIME = "cmdsRefTime";
	protected static final String CMDS_PHREFWOLRD = "plholderRefWorld";
	protected static final String CMDS_TIME = "time";
	protected static final String CMDS_DATE = "date";
	protected static final String CMDS_REPEATFREQ = "repeatFreq";

	// Commands names
	protected static final String CMD_TM = "tm";
	protected static final String CMD_NOW = "now";

	// "/tm" sub-commands names
	protected static final String CMD_CHECKCONFIG = "checkConfig";
	protected static final String CMD_CHECKSQL = "checkSql";
	protected static final String CMD_CHECKTIME = "checkTime";
	protected static final String CMD_CHECKUPDATE = "checkUpdate";
	protected static final String CMD_HELP = "help";
	protected static final String CMD_RELOAD = "reload";
	protected static final String CMD_RESYNC = "resync";
	protected static final String CMD_SET = "set";
	protected static final String CMD_TMNOW = "now";

	// "/tm set" sub-commands names
	protected static final String CMD_SET_DATE = "date";
	protected static final String CMD_SET_DEBUG = "debugMode";
	protected static final String CMD_SET_DEV = "devMode";
	protected static final String CMD_SET_TIMER = "timerMode";
	protected static final String CMD_SET_DEFLANG = "defLang";
	protected static final String CMD_SET_DURATION = "duration";
	protected static final String CMD_SET_D_DURATION = "durationDay";
	protected static final String CMD_SET_N_DURATION = "durationNight";
	protected static final String CMD_SET_E_DAYS = "elapsedDays";
	protected static final String CMD_SET_INITIALTICK = "initialTick";
	protected static final String CMD_SET_FIRSTSTARTTIME = "firstStartTime";
	protected static final String CMD_SET_MULTILANG = "multiLang";
	protected static final String CMD_SET_PLAYEROFFSET = "playerOffset";
	protected static final String CMD_SET_PLAYERTIME = "playerTime";
	protected static final String CMD_SET_REFRESHRATE = "refreshRate";
	protected static final String CMD_SET_SLEEP= "sleep";
	protected static final String CMD_SET_SPEED = "speed";
	protected static final String CMD_SET_D_SPEED = "speedDay";
	protected static final String CMD_SET_N_SPEED = "speedNight";
	protected static final String CMD_SET_START = "start";
	protected static final String CMD_SET_SYNC = "sync";
	protected static final String CMD_SET_TIME = "time";
	protected static final String CMD_SET_UPDATE = "update";
	protected static final String CMD_SET_USECMDS = "useCmds";
	
	// Commands arguments and YAML key names
	public static final String ARG_TRUE = "true";
	public static final String ARG_FALSE = "false";
	protected static final String ARG_NONE = "none";
	protected static final String ARG_FIRST = "first";
	protected static final String ARG_RE = "re";
	protected static final String ARG_ALL = "all";
	protected static final String ARG_TIME = "time";
	public static final String ARG_START = "start";
	public static final String ARG_DEFAULT = "default";
	public static final String ARG_PREVIOUS = "previous";
	public static final String ARG_LINKED = "linked";
	protected static final String ARG_CONFIG = "config";
	protected static final String ARG_LANG = "lang";
	protected static final String ARG_CMDS = "cmds";
	protected static final String ARG_ACTIONBAR = "actionbar";
	protected static final String ARG_TITLE = "title";
	protected static final String ARG_MSG = "msg";
	public static final String ARG_INSTANT = "instant";
	public static final String ARG_ANIMATION = "animation";
	protected static final String ARG_100PERCENT = "100%";
	protected static final String ARG_SERVER = "server";
	protected static final String ARG_BUKKIT = "bukkit";
	protected static final String ARG_CURSE = "curse";
	protected static final String ARG_SPIGOT = "spigot";
	protected static final String ARG_PAPER = "paper";
	protected static final String ARG_GITHUB = "github";
	public static final String ARG_NETHER = "_nether";
	public static final String ARG_THEEND = "_the_end";
	public static final String ARG_TODAY = "today";
	public static final String ARG_ACTIVE = "active";
	public static final String ARG_RESET = "reset";
	protected static final String ARG_HOUR = "hour";
	protected static final String ARG_DAY = "day";
	protected static final String ARG_WEEK = "week";
	protected static final String ARG_MONTH = "month";
	protected static final String ARG_YEAR = "year";
	
	// Placeholders names
	public static final String PH_IDENTIFIER = "tm";
	public static final String PH_PREFIX = PH_IDENTIFIER + "_";
	public static final String PH_PLAYER = "player";
	public static final String PH_WORLD = "world";
	public static final String PH_TICK = "tick";
	public static final String PH_AMPM = "ampm";
	public static final String PH_AM = "AM";
	public static final String PH_PM = "PM";
	public static final String PH_DAYPART = "daypart";
	public static final String PH_TIME12 = "time12";
	public static final String PH_TIME24 = "time24";
	public static final String PH_HOURS12 = "hours12";
	public static final String PH_HOURS24 = "hours24";
	public static final String PH_MINUTES = "minutes";
	public static final String PH_SECONDS = "seconds";
	public static final String PH_E_DAYS = "elapseddays";
	public static final String PH_C_DAY = "currentday";
	public static final String PH_DAYNAME = "dayname";
	public static final String PH_YEARDAY = "yearday";
	public static final String PH_YEARWEEK = "yearweek";
	public static final String PH_WEEK = "week";
	public static final String PH_MONTHNAME = "monthname";
	public static final String PH_DD = "dd";
	public static final String PH_MM = "mm";
	public static final String PH_YY = "yy";
	public static final String PH_YYYY = "yyyy";
	
	// Permissions names
	protected static final String PERM_TM = "timemanager.admin";
	protected static final String PERM_NOW = "timemanager.now.cmd";
	protected static final String PERM_NOW_DISPLAY = "timemanager.now.display";
	protected static final String PERM_NOW_WORLD = "timemanager.now.world";
	public static final String PERM_PLACEHOLDERS = "timemanager.placeholders";
	public static final String PERM_SLEEP_ALLOWED = "timemanager.sleep.allowed";
	public static final String PERM_SLEEP_COUNTED = "timemanager.sleep.counted";
	
	// Files names
	protected static final String CONFIGFILENAME = "config.yml";
	protected static final String CONFIGHEADERFILENAME = "config-header.txt";
	protected static final String LANGFILENAME = "lang.yml";
	protected static final String LANGHEADERFILENAME = "lang-header.txt";
	protected static final String CMDSFILENAME = "cmds.yml";
	protected static final String CMDSHEADERFILENAME = "cmds-header.txt";

	// YAML files targets
	public File configFileYaml = new File(this.getDataFolder(), CONFIGFILENAME);
	public File configHeaderFileTxt = new File(this.getDataFolder(), CONFIGHEADERFILENAME);
	public File langFileYaml = new File(this.getDataFolder(), LANGFILENAME);
	public FileConfiguration langConf = YamlConfiguration.loadConfiguration(langFileYaml);
	public File langHeaderFileTxt = new File(this.getDataFolder(), LANGHEADERFILENAME);
	public File cmdsFileYaml = new File(this.getDataFolder(), CMDSFILENAME);
	public FileConfiguration cmdsConf = YamlConfiguration.loadConfiguration(cmdsFileYaml);
	public File cmdsHeaderFileTxt = new File(this.getDataFolder(), CMDSHEADERFILENAME);

	// Use a lang_backup file
	protected final static String LANGBCKPFILENAME = "lang_backup.yml";
	public File langBckpFileYaml = new File(this.getDataFolder(), LANGBCKPFILENAME);
	public FileConfiguration langBckpConf = YamlConfiguration.loadConfiguration(langBckpFileYaml);
	
	// GameRules
	public static final String GR_DO_DAYLIGHT_CYCLE = "doDaylightCycle" ;
	public static final String GR_PLAYERS_SLEEPING_PERCENTAGE = "playersSleepingPercentage" ;
	public static final String GR_PLAYERS_SLEEPING_IGNORED = "sleepingIgnored" ;
	public static final String GR_SEND_COMMAND_FEEDBACK = "sendCommandFeedback" ;
	
	/********************
	 ***** MESSAGES *****
	 ********************/
	// Prefixes
	public static String prefixTM = "[TimeManager]";
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
	public static String worldsCheckMsg = "The worlds list was actualized.";
	protected static String multiLangIsOnMsg = "Multilanguage support is enable.";
	protected static String multiLangIsOffMsg = "Multilanguage support is disable.";
	protected static String multiLangDoesntWork = "Multilanguage is not supported by CraftBukkit under the 1.12 version. Upgrade or try with Spigot, Paper, ...";
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
	protected static String worldRealSpeedMsg = "set to match real time (=1200 ticks/minute).";
	protected static String worldCurrentSyncMsg = "synchronized to the server time";
	protected static String worldCurrentSleepMsg = "'s 'sleep' option is set to";

	// Cmds now & tm now
	protected static String noActionbarMsg = "Action Bar messages wont work below MC 1." + reqMcVForActionbarMsg;
	protected static String noPlayersMsg = "There are no players in the targeted world(s) to use the '/ tm now' command.";
	protected static String nonPlayerSenderMsg = "Only players can use the '/now' command. Try to use '/tm now' instead.";
	
	// Cmd set elapsedDays
	protected static String worldFullTimeChgMsg = "Total elapsed days in world";
	protected static String tooLateForDayZeroMsg1 = "It is too late (";
	protected static String tooLateForDayZeroMsg2 = ") to return to the first day. First use \"/tm set time\" to set a value between 6:00 and 23:59.";
	
	// Cmd resync
	protected static String resyncDoneMsg = "had its time re" + worldCurrentSyncMsg + ".";
	protected static String noResyncNeededMsg = "is already synchronized to the server time.";

	// Cmd set initialTick
	protected static String initialTickYmlMsg = "The new initial tick will be saved in the config.yml file.";
	protected static String initialTickSqlMsg = "The new initial tick will be saved in the MySQL database.";
	protected static String initialTickGetFromSqlMsg = "The new initial tick was get from the MySQL database.";
	protected static String initialTickNoChgMsg = "The tick you just entered is the same as the current one.";

	// Cmd set firstStartTime
	protected static String firstStartTimeNoChgMsg = "Impossible to change the 'firstStartTime' option cause of the actual 'sync' setting for the world";
	protected static String firstStartTimeStartChgMsg = "will start according to its 'start' value.";
	protected static String firstStartTimePreviousChgMsg = "will start at the time it was when the server shut down.";
	protected static String firstStartTimeDefaultChgMsg = "will start according to the 'resetOnStartup' value.";
	
	//Cmd set playerTime
	protected static String playerOffsetChgMsg1 = "The current offset for the player";
	protected static String playerTimeChgMsg1 = "The current time for the player";
	protected static String playerTimeChgMsg2 = "is now set to";
	protected static String playerTimeResetMsg2 = "is now reseted.";

	// Cmd set refreshRate
	protected static String refreshRateMsg = "The time increase/decrease will refresh every";

	// Cmd set sleep
	protected static String worldSleepTrueChgMsg = "It is allowed to sleep until the dawn in the world";
	protected static String worldSleepLinkedChgMsg = "This world will now react to any sleep event in the other linked worlds.";
	protected static String worldSleepFalseChgMsg = "It is forbidden to sleep until the dawn in the world";
	protected static String worldSleepNoChgMsg = "Impossible to change the 'sleep' option cause of the actual speed setting for the world";
	protected static String sleepWorldSyncChgMsg = "'sync' option was forced to 'false' in order to allow players to sleep until the dawn.";

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
	protected static String worldPreviousTimeResetMsg = "has been reset to its last known time.";
	protected static String worldStartTimeResetMsg = "has been reset to its default start time.";
	protected static String worldSyncSleepChgMsg = "'sleep' option was forced to 'false', cause of its new synchronization value.";
	protected static String worldSyncAnimChgMsg = "'nightCycleAnimation' option was forced to 'false', cause of its new synchronization value.";
	protected static String worldSyncfirstTimeStartChgMsg = "'firstTimeStart' option was forced to 'default', cause of its new synchronization value.";

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
	
	// Cmd set useCmds
	protected static String enableCmdsSchedulerDebugMsg = "The commands scheduler is §aenabled§b.";
	protected static String disableCmdsSchedulerDebugMsg = "The commands scheduler is §cdisabled§b.";

	// Errors messages (B&W)
	protected static String playerFormatMsg = "This player doesn't exist, or is offline.";
	protected static String rateFormatMsg = "Refresh rate must be an integer number.";
	protected static String offsetTickMsg = "Offset must be an integer number.";
	protected static String waitBeforeCmdMsg = "Waiting time must be an integer number.";
	protected static String wakeUpTickFormatMsg = "Wake up tick must be an integer number, default value will be used.";
	protected static String startTickFormatMsg = "Start tick must be an integer number, default value will be used.";
	protected static String dayFormatMsg = "Day number must be an integer number between 1 and ";
	protected static String dateFormatMsg = "Date must be 'today', or be in the format yyyy-mm-dd, default value will be used.";
	protected static String durationFormatMsg = "Duration must be in the format 00d-00h-00m-00s, default value will be used.";
	protected static String hourFormatMsg = "Hour must be in the format HH:mm:ss, default value will be used.";
	protected static String monthFormatMsg = "Month number must be an integer number between 1 and 12.";
	protected static String speedFormatMsg = "Speed multiplier must be a number (integer or decimal) or the string 'realtime', default value will be used.";
	protected static String tickFormatMsg = "Tick must be an integer number, a listed part of the day, or to be HH:mm:ss formatted, default value will be used.";
	protected static String titlesTimersFormatMsg = "Titles timers (fadeIn, stay, fadeOut) must be integers, default values will be used.";
	protected static String utcFormatMsg = "Time shift must be formatted as 'UTC' followed by '+' or '-' and an integer number, without space (e.g. UTC+1), default value will be used.";
	protected static String yearFormatMsg = "Year number must be an integer number between 1 and 9999.";
	protected static String wrongWorldMsg = "The name of the world you just typed does not exist.";
	protected static String wrongLangMsg = "The language you just typed does not exist in lang.yml file.";
	protected static String wrongYmlMsg = "The name of the yaml file you just typed does not exist.";
	protected static String missingArgMsg = "This command requires one or more additional argument(s).";
	protected static String isNotBooleanMsg = "This command requires a boolean argument, 'true' or 'false'.";
	protected static String nbPlayersMustBeIntMsg = "The " + CF_NIGHTSKIP_REQUIREDPLAYERS + " value must be an integer or a percentage. Default value will be used.";
	protected static String couldNotSaveLang = "File " + LANGFILENAME + " couldn't be saved on disk. In worst case, delete the file then restart the server.";
	protected static String couldNotSaveCmds = "File " + CMDSFILENAME + " couldn't be saved on disk. In worst case, delete the file then restart the server.";
	protected static String checkLogMsg = "Please check the console or log file.";
	protected static String versionMCFormatMsg = "Unable to correctly determine your server MC version, the plugin will consider it is an old one.";
	protected static String versionTMFormatMsg = "Unable to correctly determine the plugin version.";
	protected static String tmNowUnknownArgMsg = "argument you entered could not be recognized. This must be an online player name, a world name, or 'all'.";
	protected static String tmNowEmptyServerMsg = "There are currently no online players. '/tm now' will therefore have no effect.";
	protected static String tmNowEmptyWorldMsg = "is empty. '/tm now' will therefore have no effect.";

	// Debug messages (with colors)
	protected static String enableDebugModeDebugMsg = "The debug mode is §aenabled§b.";
	protected static String disableDebugModeDebugMsg = "The debug mode is §cdisabled§b.";
	protected static String enableDevModeDebugMsg = "The dev mode is §aenabled§b.";
	protected static String disableDevModeDebugMsg = "The dev mode is §cdisabled§b.";
	protected static String enableTimerModeDebugMsg = "The timer mode is §aenabled§b.";
	protected static String disableTimerModeDebugMsg = "The timer mode is §cdisabled§b.";
	protected static String cfgOptionsCheckDebugMsg = "The options will be now checked for each world.";
	public static String refrehWorldsListDebugMsg = "Refreshing the §eworldsList§b keys in config.yml.";
	public static String worldsRawListDebugMsg = "Raw list of all loaded worlds:";
	public static String worldsFormatListDebugMsg = "Name's list of all loaded worlds:";
	public static String worldsCfgListDebugMsg = "Worlds list from the config.yml:";
	public static String delWorldDebugMsg = "was deleted from the config list.";
	protected static String daySpeedAdjustDebugMsg = "The §e" + CF_D_SPEED + "§b option value was converted from";
	protected static String nightSpeedAdjustDebugMsg = "The §e" + CF_N_SPEED + "§b option value was converted from";
	protected static String startAdjustDebugMsg = "The §e" + CF_START + "§b option value was converted from";
	protected static String syncAdjustTrueDebugMsg = "The §e" + CF_SYNC + "§b option is forced to §a" + ARG_TRUE + "§b for the world";
	protected static String syncAdjustFalseDebugMsg = "The §e" + CF_SYNC + "§b option is forced to §c" + ARG_FALSE + "§b for the world";
	protected static String sleepAdjustFalseDebugMsg = "The §e" + CF_SLEEP + "§b option is forced to §c" + ARG_FALSE + "§b for the world";
	protected static String firstStartTimeAdjustDefaultDebugMsg = "The §e" + CF_FIRSTSTARTTIME + "§b option is forced to §e" + ARG_DEFAULT + "§b for the world";
	protected static String nightSkipModeAdjustDefaultDebugMsg = "The §e" + CF_NIGHTSKIP_MODE + "§b option is forced to §e" + ARG_DEFAULT + "§b for the world";
	protected static String nightSkipNbPlayersAdjustDefaultDebugMsg = "The §e" + CF_NIGHTSKIP_REQUIREDPLAYERS + "§b option is forced to §e" + ARG_100PERCENT + "§b for the world";
	protected static String availableTranslationsDebugMsg = "Available translations are:";
	public static String daylightTrueDebugMsg = "The §e" + GR_DO_DAYLIGHT_CYCLE + "§b value is now set to §a" + ARG_TRUE + "§b for the world";
	protected static String daylightFalseDebugMsg = "The §e" + GR_DO_DAYLIGHT_CYCLE + "§b value is now set to §c" + ARG_FALSE + "§b for the world";
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
	public static String sleepProcessStartsDebugMsg = "§b starts to sleep (0/100 ticks).";
	public static String sleepProcessInterruptedDebugMsg = "Someone left the bed before the end of the night.";
	public static String sleepProcess100TicksDebugMsg = "§b's sleep time is achieved (100/100 ticks) at tick §e#";
	public static String sleepProcessAdjustMorningTicksDebugMsg = "The morning tick was adjusted to §e#";
	public static String sleepProcessForbiddenDebugMsg = "Sleeping is forbid in the world";
	public static String sleepProcessImpossibleDebugMsg = "Sleeping is impossible in the world";
	public static String sleepProcessSyncActiveDebugMsg = "Sync is active in the world";
	public static String sleepProcessTimeFrozenDebugMsg = "Time is frozen in the world";
	public static String sleepProcessItIsDayDebugMsg = "It's daytime in the world";
	public static String sleepProcessNoSleepPermDebugMsg = "The permission §e" + PERM_SLEEP_ALLOWED + "§b is set to §c" + ARG_FALSE + "§b for this player.";
	public static String sleepProcessNoCountPermDebugMsg = "The permission §e" + PERM_SLEEP_COUNTED + "§b is set to §c" + ARG_FALSE + "§b for this player.";
	public static String sleepProcessWaitingBetweenTwoBedsDebugMsg = "Players must wait 25 ticks before entering a bed again.";
	public static String sleepProcessIgnoredDebugMsg = "The GameRule §e" + GR_PLAYERS_SLEEPING_IGNORED + "§b is set to §a" + ARG_TRUE + "§b for this player.";
	public static String sleepProcessEndsDebugMsg = "'s sleep process ends here.";
	public static String sleepProcessAwakeNoSleepDebugMsg = "without having been able to sleep.";
	public static String sleepAnimationDefaultSpeedDebugMsg = "Only a few ticks to skip in 100 ticks. Night cycle animation will use a default speed of §e5 §b.";
	public static String sleepAnimationSpeedCalculationDebugMsg = " ticks to skip in 100 ticks. Night cycle animation'll use a speed of §e";
	public static String sleepFulltimeTickDebugMsg = "Player achieved sleeping at fulltime §e#";
	public static String sleepGameRulePart1DebugMsg = "The §e" + GR_PLAYERS_SLEEPING_PERCENTAGE + "§b GameRule is set so that §e";
	public static String sleepGameRulePart2DebugMsg = "% §bof players must be sleeping simultaneously to skip the night in the world";
	public static String cmdsWrongPHWorldDebugMsg = "does not exist. It was replaced by the default value";
	public static String cmdsWrongTimeSrcDebugMsg = "is neither a world or an UTC time. It was replaced by the default value";
	public static String schedulerOffDebugMsg = "will no longer use any scheduler.";
	public static String scheduler24DebugMsg = "the realtime speed scheduler.";
	public static String schedulerConstantSyncDebugMsg = "the synchronous constant speed scheduler.";
	public static String schedulerVariableSyncDebugMsg = "the synchronous variable speed scheduler.";
	public static String schedulerAsyncIncreaseDebugMsg = "the asynchronous increase speed scheduler.";
	public static String schedulerAsyncDecreaseDebugMsg = "the asynchronous decrease speed scheduler.";
	public static String schedulerAsyncNormalDebugMsg = "the asynchronous normal speed scheduler.";
	public static String schedulerWillUseDebugMsg = "will now use ";
	public static String schedulerIsRunningDebugMsg = "is running in ";
	public static String schedulerFractionDebugMsg = "The fraction used as a time modifier from the decimal is : ";
	public static String durationToFractionDebugMsg = "The calculation of the duration as a speed multiplier is : ";
	public static String addNewWorldDebugMsg = " §bdoes not exist yet, it will be added to the Timemanager worlds list.";
	public static String deleteUnknowWorldDebugMsg = " §bdoes not exist anymore, it will be deleted from the Timemanager worlds list.";
	
	// Debug Calculation for timer synchronization (with colors)
	protected static String actualTimeVar = "§c[actualTime]§b";
	protected static String askedTimeVar = "§8[askedTime]§b";
	protected static String currentServerTickVar = "§8[currentServerTick]§b";
	protected static String currentTickVar = "§8[currentTick]§b";
	protected static String elapsedTimeVar = "§d[ServerElapsedTime]§b";
	protected static String firstHalfDaylightCycleVar = "§f[firstHalfDaylightCycle]§b";
	protected static String firstSpeedModifierVar = "§a[firstSpeedModifier]§b";
	protected static String initialTickVar = "§7[initialTick]§b";
	protected static String mcTimeRatioVar = "§6[mcTimeRatio]§b";
	protected static String oldWorldStartAtVar = "§3[oldWorldStartAt]§b";
	protected static String secondHalfDaylightCycleVar = "§f[secondHalfDaylightCycle]§b";
	protected static String secondSpeedModifierVar = "§2[secondSpeedModifier]§b";
	protected static String serverRemainingTimeVar = "§5[serverRemainingTime]§b";
	protected static String sixHoursLessVar = "§9[sixHoursLess]§b";
	protected static String speedModifierVar = "§a[speedModifier]§b";
	protected static String ticksInOneDayVar = "§6[ticksInOneDay]§b";
	protected static String worldElapsedTimeVar = "§5[WorldElapsedTime]§b";
	protected static String worldStartAtVar = "§e[worldStartAt]§b";
	protected static String worldTicksVar = "§3[worldTick]§b";
	protected static String actualTimeCalculation = actualTimeVar + " = " + worldStartAtVar + " + " + worldElapsedTimeVar;
	protected static String elapsedTimeCalculation = elapsedTimeVar + " = (" + currentServerTickVar + " - " + initialTickVar + ") % " + ticksInOneDayVar;
	protected static String realActualTimeCalculation = actualTimeVar + " = " + worldStartAtVar + " - " + sixHoursLessVar + " + " + worldTicksVar;
	protected static String worldStartAtCalculation = worldStartAtVar + " = " + oldWorldStartAtVar + " + " + askedTimeVar + " - " + actualTimeVar;
	protected static String worldTicksCalculation = worldTicksVar + " = " + currentTickVar + " / " + mcTimeRatioVar;
	protected static String worldElapsedTimeCalculation = worldElapsedTimeVar + " = " + elapsedTimeVar + " * " + speedModifierVar;
	
	// Sleep listener
	public static String sleepNewDayMsg = "The players slept and spent the night in the world";
	public static String sleepLinkedNewDayMsg = "has also had its time changed at the same hour.";

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
		serverMcVersion = McVersionHandler.KeepDecimalOfMcVersion();
		serverType = McVersionHandler.KeepTypeOfServer();
		if (serverMcVersion < reqMcVToLoadPlugin) {
			MsgHandler.colorMsg("§c" + plBadVersionMsg + "1." + serverMcVersion + " server.");
		} else {

			// #1. Initiate this main class as the contain of the instance
			instanceMainClass = this;

			// #2. Activate the configuration file
			CfgFileHandler.loadConfig(ARG_FIRST);

			// #3. Activate the languages file
			LgFileHandler.loadLang(ARG_FIRST);
			
			// #4. Activate the scheduler file
			CmdsFileHandler.loadCmds(ARG_FIRST);

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

			// #8.A. Listen to sleep events
			getServer().getPluginManager().registerEvents(new SleepHandler(), this);
			// #8.B. Hidden listener (Check if MC version is at least 1.15.0)
			if (serverMcVersion >= reqMcVForTimeSkipEvent) getServer().getPluginManager().registerEvents(new HiddenListenerHandler(), this);

			// #9. Listen to books events
			getServer().getPluginManager().registerEvents(new BooksHandler(), this);

			// #10. Listen to signs events
			getServer().getPluginManager().registerEvents(new SignsHandler(), this);

			// #11. Listen to chat events
			getServer().getPluginManager().registerEvents(new ChatHandler(), this);
			
			// #12. Listen to commands events
			getServer().getPluginManager().registerEvents(new PlayerCommandHandler(), this);
			getServer().getPluginManager().registerEvents(new ConsoleCommandHandler(), this);	
			
			// #13. Listen to worlds events
			getServer().getPluginManager().registerEvents(new WorldListHandler(), this);	 // TODO		

			// #14. Synchronize worlds and create scheduled task for faking the time increase/decrease
			SyncHandler.firstSync();

			// #15. Activate (or not) the placeholder APIs
			if (MainTM.getInstance().getConfig().getString(CF_PLACEHOLDERS + "." + CF_PLACEHOLDER_PAPI).equalsIgnoreCase(ARG_TRUE)
					&& Bukkit.getPluginManager().getPlugin(CF_PLACEHOLDER_PAPI) != null) {
				MsgHandler.debugMsg(CF_PLACEHOLDER_PAPI + " detected.");
				new PAPIHandler(this).register();
			}
			if (MainTM.getInstance().getConfig().getString(CF_PLACEHOLDERS + "." + CF_PLACEHOLDER_MVDWPAPI).equalsIgnoreCase(ARG_TRUE)
					&& Bukkit.getPluginManager().getPlugin(CF_PLACEHOLDER_MVDWPAPI) != null) {
				MsgHandler.debugMsg(CF_PLACEHOLDER_MVDWPAPI + " detected.");
				MVdWPAPIHandler.loadMVdWPlaceholderAPI();
			}
			
			// #16. bStats
			int pluginId = 10412;
	        Metrics metrics = new Metrics(this, pluginId);

			// #17. Confirm activation in console
			MsgHandler.infoMsg(plEnabledMsg);
			
			// #18. Check for an update
			if (serverMcVersion >= MainTM.reqMcVForUpdate)
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
		if (serverMcVersion < reqMcVToLoadPlugin) {
		} else {

			// #1. Save YAMLs
			if (MainTM.serverMcVersion < MainTM.reqMcVForSleepPercentage)
				Bukkit.getServer().getWorlds().stream().forEach(world -> MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world.getName() + "." + CF_NIGHTSKIP_LEGACYPERCENTAGE, null));
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