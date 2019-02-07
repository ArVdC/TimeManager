package net.vdcraft.arvdc.timemanager.mainclass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import net.vdcraft.arvdc.timemanager.MainTM;

public class UpdateHandler extends MainTM {

    private static final String BUKKIT = "bukkit";
    private static final String CURSE = "curse";
    private static final String TWITCH = "twitch";
    private static final String SPIGOT = "spigot";
    private static final String PAPER = "paper";
    private static final String GITHUB = "github";

    private static int bukkitProjectID = 272762;
    private static String bukkitURL = "https://servermods.forgesvc.net/servermods/files?projectIds=" + bukkitProjectID;
    private static String bukkitDownloadURL = "https://dev.bukkit.org/projects/" + bukkitProjectID;

    private static String curseDownloadURL = "https://www.curseforge.com/minecraft/bukkit-plugins/mc-timemanager";

    private static int spigotProjectID = 44344;
    private static String spigotURL = "https://api.spigotmc.org/legacy/update.php?resource=" + spigotProjectID;
    private static String spigotDownloadURL = "https://www.spigotmc.org/resources/" + spigotProjectID;

    private static String githubURL = "https://api.github.com/repos/ArVdC/TimeManager/releases/latest";
    private static String githubDownloadURL = "https://github.com/ArVdC/TimeManager/releases";

    private static String noUpdateMsg = "No update was found, you are running the latest version.";
    private static String urlFailMsg = "No update was found, the provided URL was not recognized.";
    private static String serverFailMsg = "No update was found, the server could not be reached.";

    private static String currentVersion = versionTM();
    private static String latestVersion = null;
    private static URL checkURL = null;
    private static String downloadURL;

    /**
     * Delayed update check on startup
     */
    public static void checkForUpdate() {
	BukkitScheduler firstSyncSheduler = MainTM.getInstance().getServer().getScheduler();
	firstSyncSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
	    @Override
	    public void run() {
		String updateSource = MainTM.getInstance().getConfig().getString(CF_UPDATEMSGSRC).toLowerCase();
		if (updateSource.equals(BUKKIT)) {
		    if (getURL(bukkitURL)) {
			downloadURL = bukkitDownloadURL;
			checkUpdateOnBukkit();
		    }
		} else if (updateSource.equals(CURSE) || updateSource.equals(TWITCH)) {
		    if (getURL(bukkitURL)) {
			downloadURL = curseDownloadURL;
			checkUpdateOnBukkit();
		    }
		} else if (updateSource.equals(SPIGOT) || updateSource.equals(PAPER)) {
		    if (getURL(spigotURL)) {
			downloadURL = spigotDownloadURL;
			checkUpdateOnSpigot();
		    }
		} else if (updateSource.equals(GITHUB)) {
		    if (getURL(githubURL)) {
			downloadURL = githubDownloadURL;
			checkUpdateOnGithub();
		    }
		} else {
		    MainTM.getInstance().getConfig().set(CF_UPDATEMSGSRC, "");
		}
		if (latestVersion != null) {
		    MainTM.getInstance().getConfig().set(CF_UPDATEMSGSRC, updateSource.replaceFirst(".", (updateSource.charAt(0) + "").toUpperCase()));
		    latestVersion = replaceChars(latestVersion);
		    currentVersion = replaceChars(currentVersion);
		    if (latestVersion != null) {
			displayUpdateMsg();
		    }
		}
		MainTM.getInstance().saveConfig();
	    }
	}, 80L);
    }

    /**
     * Send the update message to the console
     */
    private static void displayUpdateMsg() {
	if (compareVersions()) {
	    Bukkit.getServer().getConsoleSender().sendMessage(prefixTM + " An update is available, check §e" + downloadURL + "§r to get the " + latestVersion + " version."); // Console log msg
	} else
	    Bukkit.getLogger().info(prefixTM + " " + noUpdateMsg); // Console log msg
    }

    /**
     * Compare the current version to the last found
     */
    private static boolean compareVersions() {
	int latestMajor = 0;
	int latestMinor = 0;
	int latestPatch = 0;
	int latestRelease = 4;
	int latestDev = 0;
	int currentMajor = 0;
	int currentMinor = 0;
	int currentPatch = 0;
	int currentRelease = 4;
	int currentDev = 0;
	// Split new version numbers
	String[] latestVersionNb = latestVersion.split("[.]");
	if (latestVersionNb.length >= 2) {
	    latestMajor = Integer.parseInt(latestVersionNb[0]);
	    latestMinor = Integer.parseInt(latestVersionNb[1]);
	}
	if (latestVersionNb.length >= 3)
	    latestPatch = Integer.parseInt(latestVersionNb[2]);
	if (latestVersionNb.length >= 4)
	    latestRelease = Integer.parseInt(latestVersionNb[3]);
	if (latestVersionNb.length >= 5)
	    latestDev = Integer.parseInt(latestVersionNb[4]);
	// Split old version numbers
	String[] currentVersionNb = currentVersion.split("[.]");
	if (currentVersionNb.length >= 2) {
	    currentMajor = Integer.parseInt(currentVersionNb[0]);
	    currentMinor = Integer.parseInt(currentVersionNb[1]);
	}
	if (currentVersionNb.length >= 3)
	    currentPatch = Integer.parseInt(currentVersionNb[2]);
	if (currentVersionNb.length >= 4)
	    currentRelease = Integer.parseInt(currentVersionNb[3]);
	if (currentVersionNb.length >= 5)
	    currentDev = Integer.parseInt(currentVersionNb[4]);
	// Check which version is the higher version
	if ((latestMajor > currentMajor)
		|| (latestMajor == currentMajor && latestMinor > currentMinor)
		|| (latestMajor == currentMajor && latestMinor == currentMinor && latestPatch > currentPatch)
		|| (latestMajor == currentMajor && latestMinor == currentMinor && latestPatch == currentPatch && latestRelease > currentRelease)
		|| (latestMajor == currentMajor && latestMinor == currentMinor && latestPatch == currentPatch && latestRelease == currentRelease && latestDev > currentDev))
	    return true;
	return false;
    }

    /**
     * Replace characters before splitting version string into integers
     */
    private static String replaceChars(String version) {
	version = version.replace("dev", "d")
	.replace("alpha", "a")
	.replace("beta", "b")
	.replace("d", "-0.")
	.replace("a", "-1.")
	.replace("b", "-2.")
	.replace("rc", "-3.")
	.replace("--", ".")
	.replace("-", ".")
	.replace("..", ".");
	try {
	    String versionIntTest = version.replace(".", "");
	    Integer.parseInt(versionIntTest); // Prevent all other parse errors
	} catch (NumberFormatException e) {
	    return null;
	}
	return version;
    }

    /**
     * Get latest version number from Bukkit
     */
    private static void checkUpdateOnBukkit() {
	URLConnection connec = null;
	String response = null;
	try {
	    connec = (URLConnection) checkURL.openConnection();
	    // The response will be in a JSON format, so only reading one line is necessary.
	    final BufferedReader reader = new BufferedReader(new InputStreamReader(connec.getInputStream()));
	    response = reader.readLine();
	} catch (IOException e) {
	    Bukkit.getLogger().warning(prefixTM + " " + serverFailMsg); // Console warning msg
	    return;
	}
	JSONArray array = (JSONArray) JSONValue.parse(response);
	if (array.size() > 0) {
	    JSONObject latest = (JSONObject) array.get(array.size() - 1); // Get the newest file's details
	    String versionName = (String) latest.get("name");
	    latestVersion = versionName.replace("TimeManager v", "");
	    //if (debugMode)
	    Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Last version on Bukkit/Curseforge is " + latestVersion + " and you are running the " + versionTM()); // Console debug msg
	}
    }

    /**
     * Get latest version number from Spigot
     */
    private static void checkUpdateOnSpigot() {
	try {
	    HttpURLConnection con = (HttpURLConnection) checkURL.openConnection();
	    latestVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
	    if (debugMode)
		Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Last version on Spigot is " + latestVersion + " and you are running the " + versionTM()); // Console debug msg
	} catch (Exception e) {
	    Bukkit.getLogger().warning(prefixTM + " " + serverFailMsg); // Console warning msg
	}
    }

    /**
     * Get latest version number from GitHub
     */
    private static void checkUpdateOnGithub() {
	URLConnection connec = null;
	String response = null;
	try {
	    connec = (URLConnection) checkURL.openConnection();
	    // The response will be in a JSON format, so only reading one line is necessary.
	    final BufferedReader reader = new BufferedReader(new InputStreamReader(connec.getInputStream()));
	    response = reader.readLine();
	} catch (IOException e) {
	    Bukkit.getLogger().warning(prefixTM + " " + serverFailMsg); // Console warning msg
	    return;
	}
	JSONObject latest = (JSONObject) JSONValue.parse(response);
	; // Get the newest file's details
	String versionName = (String) latest.get("tag_name");
	latestVersion = versionName.replaceFirst("v", "");
	if (debugMode)
	    Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Last version on Github is " + latestVersion + " and you are running the " + versionTM()); // Console debug msg
    }

    /**
     * Try to get an URL from a String
     */
    private static boolean getURL(String url) {
	try {
	    checkURL = new URL(url);
	    return true;
	} catch (MalformedURLException e) {
	    Bukkit.getLogger().severe(prefixTM + " " + urlFailMsg); // Console error msg
	    return false;
	}
    }

};