package net.vdcraft.arvdc.timemanager.mainclass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitScheduler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.vdcraft.arvdc.timemanager.MainTM;

public class UpdateHandler extends MainTM {

	private static int bukkitProjectID = 272762;
	private static String bukkitURL = "https://servermods.forgesvc.net/servermods/files?projectIds=" + bukkitProjectID;
	private static String bukkitDownloadURL = "https://dev.bukkit.org/projects/" + bukkitProjectID;

	private static String curseDownloadURL = "https://www.curseforge.com/minecraft/bukkit-plugins/mc-timemanager";

	private static int spigotProjectID = 44344;
	private static String spigotURL = "https://api.spigotmc.org/legacy/update.php?resource=" + spigotProjectID;
	private static String spigotDownloadURL = "https://www.spigotmc.org/resources/" + spigotProjectID;

	private static String githubURL = "https://api.github.com/repos/ArVdC/TimeManager/releases/latest";
	private static String githubDownloadURL = "https://github.com/ArVdC/TimeManager/releases";

	private static String currentVersion = versionTM();
	private static String latestVersion = null;
	private static URL checkURL = null;
	private static String downloadURL;

	/**
	 * Delayed update check on startup
	 */
	public static void delayCheckForUpdate() {
		BukkitScheduler firstSyncSheduler = MainTM.getInstance().getServer().getScheduler();
		firstSyncSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				CommandSender sender = Bukkit.getServer().getConsoleSender(); 
				String updateSource = MainTM.getInstance().getConfig().getString(CF_UPDATEMSGSRC).toLowerCase();
				checkForUpdate(sender, updateSource, true);
			}
		}, 80L);
	}

	/**
	 * Update check
	 */
	public static void checkForUpdate(CommandSender sender, String updateSource, Boolean saveSource) {
		if (updateSource.equals(ARG_BUKKIT)) {
			if (getURL(bukkitURL)) {
				downloadURL = bukkitDownloadURL;
				checkUpdateOnBukkit();
			}
		} else if (updateSource.equals(ARG_CURSE)) {
			if (getURL(bukkitURL)) {
				updateSource = ARG_CURSE;
				downloadURL = curseDownloadURL;
				checkUpdateOnBukkit();
			}
		} else if (updateSource.equals(ARG_SPIGOT) || updateSource.equals(ARG_PAPER)) {
			if (getURL(spigotURL)) {
				updateSource = ARG_SPIGOT;
				downloadURL = spigotDownloadURL;
				checkUpdateOnSpigot();
			}
		} else if (updateSource.equals(ARG_GITHUB)) {
			if (getURL(githubURL)) {
				downloadURL = githubDownloadURL;
				checkUpdateOnGithub();
			}
		} else { // On reload, clear the configuration value if the updateSource is void or unknown, then save it to config.yml
			MainTM.getInstance().getConfig().set(CF_UPDATEMSGSRC, "");
			MainTM.getInstance().saveConfig();
		}
		if (latestVersion != null) {
			latestVersion = ValuesConverter.replaceChars(latestVersion);
			currentVersion = ValuesConverter.replaceChars(currentVersion);
			displayUpdateMsg(sender);
		}
		if ((saveSource == true) && (updateSource.length() > 0)) { // Format the configuration value, then save it to config.yml
			String newSource = updateSource.replaceFirst(".", (updateSource.charAt(0) + "").toUpperCase());
			MainTM.getInstance().getConfig().set(CF_UPDATEMSGSRC, newSource);
			MainTM.getInstance().saveConfig();
		}
	}

	/**
	 * Send the update message to the command sender
	 */
	private static void displayUpdateMsg(CommandSender sender) {
		if (newPluginVersionExists()) {
			MsgHandler.playerAdminMsg(sender, "An update is available, check §e" + downloadURL + "§r to get the " + latestVersion + " version."); // Final player msg
			MsgHandler.colorMsg("An update is available, check §e" + downloadURL + "§r to get the " + latestVersion + " version."); // Console log msg
		} else {
			MsgHandler.playerAdminMsg(sender, noUpdateMsg); // Final player msg
			MsgHandler.colorMsg(noUpdateMsg); // Console log msg
		}
	}

	/**
	 * Compare the current plugin version to the latest and returns "true" if an update exists
	 */
	private static boolean newPluginVersionExists() {
		int latestMajor = 0;
		int latestMinor = 0;
		int latestPatch = 0;
		int latestRelease = 4;
		int latestDev = 0;
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
		if (ValuesConverter.requestedPluginVersionIsNewerThanCurrent("cfg", latestMajor, latestMinor, latestPatch, latestRelease, latestDev)) return true;
		return false;
	}

	/**
	 * Get latest version number from Bukkit
	 */
	private static void checkUpdateOnBukkit() {
		URLConnection connec = null;
		String response = null;
		String splitMarker = "\u2063";
		try {
			connec = (URLConnection) checkURL.openConnection();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(connec.getInputStream()));
			String[] list = reader.readLine().replace("]", "").replace("[", "").replace("},{", "}," + splitMarker + "{").split(splitMarker);
			response = list[list.length - 1];
			reader.close();
			@SuppressWarnings("deprecation")
			JsonObject o = (JsonObject) new JsonParser().parse(response);
			JsonElement e = (JsonElement) o.get("name");
			latestVersion = e.toString().replaceFirst("TimeManager v", "").replace("\"", "");
			MsgHandler.debugMsg(LatestVersionPart1DebugMsg + " " + ARG_BUKKIT + " is " + latestVersion + " " + LatestVersionPart2DebugMsg + " " + versionTM()); // Console debug msg
		} catch (IOException e) {
			MsgHandler.warnMsg(serverFailMsg); // Console warning msg
		}
	}

	/**
	 * Get latest version number from Spigot
	 */
	private static void checkUpdateOnSpigot() {
		try {
			HttpURLConnection con = (HttpURLConnection) checkURL.openConnection();
			latestVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine().replace("\"", "");
			MsgHandler.debugMsg(LatestVersionPart1DebugMsg + " " + ARG_SPIGOT + " is " + latestVersion + " " + LatestVersionPart2DebugMsg + " " + versionTM()); // Console debug msg
		} catch (Exception e) {
			MsgHandler.warnMsg(serverFailMsg); // Console warning msg
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
			final BufferedReader reader = new BufferedReader(new InputStreamReader(connec.getInputStream()));
			response = reader.readLine();
			reader.close();
			@SuppressWarnings("deprecation")
			JsonObject o = (JsonObject) new JsonParser().parse(response);
			JsonElement e = (JsonElement) o.get("tag_name");
			latestVersion = e.toString().replaceFirst("v", "").replace("\"", "");
			MsgHandler.debugMsg(LatestVersionPart1DebugMsg + " " + ARG_GITHUB + " is " + latestVersion + " " + LatestVersionPart2DebugMsg + " " + versionTM()); // Console debug msg
		} catch (IOException e) {
			MsgHandler.warnMsg(serverFailMsg); // Console warning msg
		}
	}

	/**
	 * Try to get an URL from a String
	 */
	private static boolean getURL(String url) {
		try {
			checkURL = new URL(url);
			return true;
		} catch (MalformedURLException e) {
			MsgHandler.errorMsg(urlFailMsg); // Console error msg
			return false;
		}
	}

};