/******************
 *** MAIN CLASS ***
 ******************/

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
import net.vdcraft.arvdc.timemanager.mainclass.UpdateHandler;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSleepHandler;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSyncHandler;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MainTM extends JavaPlugin {

    /*****************
     *** VARIABLES ***
     *****************/

    // Main class
    public static MainTM instanceMainClass;

    // Plugin version
    public static String versionTM() {
	return instanceMainClass.getDescription().getVersion().toString();
    };

    // Minecraft server minimal required version decimals "x.xx" (without the "1."
    // and eventually with a "x.0x" format - to permit comparisons)
    public static Double minRequiredMcVersion = 4.06;

    // Current Minecraft server version decimals "x.xx" (without the "1." and eventually with a "x.0x" format - to permit comparisons)
    public static Double decimalOfMcVersion;

    // Enable/Disable debugging
    public static Boolean debugMode = false; // Final user accessible debug msgs
    public static Boolean devMode = false; // Displays more verbose debug msgs
    public static Boolean timerMode = false; // Displays all timers calculations (= ultra-verbose mode)

    // Commands names
    public static final String CMDTM = "tm";
    public static final String CMDNOW = "now";

    // Files names
    public static final String CONFIGFILENAME = "config.yml";
    public static final String lANGFILENAME = "lang.yml";

    // Config and Lang files targets
    public File configFileYaml = new File(this.getDataFolder(), CONFIGFILENAME);
    public File langFileYaml = new File(this.getDataFolder(), lANGFILENAME);
    public FileConfiguration langConf = YamlConfiguration.loadConfiguration(langFileYaml);

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
    public static String cfgVersionMsg = "Enabled " + CONFIGFILENAME + " v";
    public static String lgVersionMsg = "Enabled " + lANGFILENAME + " v";
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
    public static String defLangOkMsg = "exists in " + lANGFILENAME + ", keep it as default translation.";
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
    public static String tickNotNbMsg = "Tick must be an integer number, a listed part of the day, or to be HH:mm:ss formated.";
    public static String speedNotNbMsg = "Speed multiplier must be a number (integer or decimal) or the string \"realtime\".";
    public static String wrongWorldMsg = "The name of the world you just typed does not exist.";
    public static String wrongLangMsg = "The language you just typed does not exist in lang.yml file.";
    public static String wrongYmlMsg = "The name of the yaml file you just typed does not exist.";
    public static String missingArgMsg = "This command requires one or more additional argument(s).";
    public static String isNotBooleanMsg = "This command requires a boolean argument, true or false.";
    public static String couldNotSaveLang = "File " + lANGFILENAME + " couldn't be saved on disk. In worst case, delete the file then restart the server.";
    public static String checkLogMsg = "Please check the console or log file.";
    public static String unknowVersionMsg = "Impossible to determine properly the MC version of your server, the plugin will consider it is an old one.";

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
    public static String elapsedTimeCalculation = elapsedTimeVar + " = (" + currentServerTickVar + " - " + initialTickVar + ") % " + ticksInOneDayVar;
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
    public static String checkConfigMsg = "Please check the " + CONFIGFILENAME + " file and set the debugMode to true to see error details.";
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

    // Check if schedule is already active
    public static Boolean increaseScheduleIsOn = false;
    public static Boolean decreaseScheduleIsOn = false;
    public static Boolean realScheduleIsOn = false;

    // Initialize server tick
    public static Long initialTick;
    public static String initialTime;

    // Language to use if locale doesn't exist in the lang.yml = 'defaultLang'
    public static String serverLang;

    // Config file keys
    public static final String CF_VERSION = "version";
    public static final String CF_DEFTIMEUNITS = "defTimeUnits";
    public static final String CF_REFRESHRATE = "refreshRate";
    public static final String CF_WORLDSLIST = "worldsList";
    public static final String CF_START = "start";
    public static final String CF_SPEED = "speed";
    public static final String CF_SLEEP = "sleep";
    public static final String CF_SYNC = "sync";
    public static final String CF_INITIALTICK = "initialTick";
    public static final String CF_INITIALTICKNB = "initialTickNb";
    public static final String CF_RESETONSTARTUP = "resetOnStartup";
    public static final String CF_USEMYSQL = "useMySql";
    public static final String CF_MYSQL = "mySql";
    public static final String CF_HOST = "host";
    public static final String CF_PORT = "port";
    public static final String CF_SSL = "ssl";
    public static final String CF_DBPREFIX = "dbPrefix";
    public static final String CF_DATABASE = "database";
    public static final String CF_TABLE = "table";
    public static final String CF_USERNAME = "username";
    public static final String CF_PASSWORD = "password";
    public static final String CF_UPDATEMSGSRC = "updateMsgSrc";
    public static final String CF_DEBUGMODE = "debugMode";

    // Lang file keys
    public static final String CF_USEMULTILANG = "useMultiLang";
    public static final String CF_DEFAULTLANG = "defaultLang";
    public static final String CF_lANGUAGES = "languages";
    public static final String CF_DEFAULT = "default";
    public static final String CF_PREFIX = "prefix";
    public static final String CF_MSG = "msg";
    public static final String CF_NOMSG = "noMsg";
    public static final String CF_DAYPARTS = "dayparts";
    public static final String CF_DAY = "day";
    public static final String CF_DUSK = "dusk";
    public static final String CF_NIGHT = "night";
    public static final String CF_DAWN = "dawn";

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
	    getCommand(CMDTM).setExecutor(timemanagerExecutor);
	    // Activate tab completion for admins commands
	    getCommand(CMDTM).setTabCompleter(new CreateSentenceCommand());

	    // #5. Activate the class with players commands
	    CommandExecutor nowExecutor = new PlayerCmdExecutor();
	    getCommand(CMDNOW).setExecutor(nowExecutor);
	    // Activate tab completion for players commands
	    getCommand(CMDNOW).setTabCompleter(new CreateSentenceCommand());

	    // #6. Listen to sleep events
	    getServer().getPluginManager().registerEvents(new WorldSleepHandler(), this);

	    // #7. Synchronize worlds and create scheduled task for faking the time
	    // stretch/expand
	    WorldSyncHandler.WorldSyncFirst();

	    // #8. Confirm activation in console
	    Bukkit.getLogger().info(prefixTM + " " + plEnabledMsg);

	    // Check for an update
	    UpdateHandler.checkForUpdate();
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
     */
    public static void waitTime(Integer ticksToWait) {
	try {
	    Thread.sleep(ticksToWait);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

};