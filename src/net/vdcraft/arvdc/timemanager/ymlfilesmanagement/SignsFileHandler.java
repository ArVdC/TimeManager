package net.vdcraft.arvdc.timemanager.ymlfilesmanagement;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.RefreshingSignHandler;

public class SignsFileHandler extends MainTM {

	/**
	 * Activate or reload the signs file
	 */
	public static void loadSigns(String firstOrRe) {

		// #1. When it is the server startup
		if (firstOrRe.equalsIgnoreCase(ARG_FIRST)) {
			// #1.A Creation of signs.yml file if doesn't exist
			if (!(MainTM.getInstance().signsFileYaml.exists())) {
				MsgHandler.infoMsg(signsFileCreaMsg); // Console log msg
				// #1.A.a. Copy the file from src in .jar
				CopyFilesHandler.copy(MainTM.getInstance().getResource(SIGNSFILENAME), MainTM.getInstance().signsFileYaml);
				// #1.A.b. Actualize values
				MainTM.getInstance().signsConf = YamlConfiguration.loadConfiguration(MainTM.getInstance().signsFileYaml);
			} else {		
				MsgHandler.infoMsg(signsFileExistMsg); // Console log msg
			}
			// #1.B. Load the header from the .txt file
			// #1.B.a. Extract the file from the .jar
			CopyFilesHandler.copyAnyFile(SIGNSHEADERFILENAME, MainTM.getInstance().signsHeaderFileTxt);
			// #1.B.b. Try to get the documentation text
			List<String> header = new ArrayList<String>();
			try {
				header.addAll(Files.readAllLines(MainTM.getInstance().signsHeaderFileTxt.toPath(), Charset.defaultCharset()));
			} catch (IOException e) {
				header.add(SIGNSHEADERFILENAME + " could not be loaded. Find it inside the .jar file to get the " + SIGNSFILENAME + " documentation.");
			}
			MsgHandler.devMsg("The §eheader§9 of " + SIGNSFILENAME + " file contents : §e" + header); // Console dev msg
			// #1.B.c. Delete the txt file
			MainTM.getInstance().signsHeaderFileTxt.delete();
			// #1.B.d. Set the header into the yml file
			MainTM.getInstance().signsConf.options().setHeader(header);
		}

		// #2. When using the admin command /tm reload
		if (firstOrRe.equalsIgnoreCase(ARG_RE)) {
			if (MainTM.getInstance().signsFileYaml.exists()) {
				// Notification
				MsgHandler.infoMsg(signsFileTryReloadMsg);
				// Reload values from cmds.yml file
				MainTM.getInstance().signsConf = YamlConfiguration.loadConfiguration(MainTM.getInstance().signsFileYaml);
			} else
				loadSigns(ARG_FIRST);
		}

		// #3. In both case
		
		// #3.A. Restore the version value
		MainTM.getInstance().signsConf.set(CF_VERSION, versionTM());
		
		// #3.B. Create a signs list key if not already there
		if (!MainTM.getInstance().signsConf.getKeys(false).contains(SIGNS_SIGNSLIST)) {
			MainTM.getInstance().signsConf.set(SIGNS_SIGNSLIST, "");	
		}
			
		// #3.C. If useSigns is enable, detect wrong worlds names
		if (MainTM.getInstance().getConfig().getString(CF_SIGNS + "." + CF_SIGNS_USESIGNS).equalsIgnoreCase(ARG_TRUE)) {
			List<String> worlds = CfgFileHandler.setAnyListFromConfig(CF_WORLDSLIST);
			if (MainTM.getInstance().signsConf.getConfigurationSection(SIGNS_SIGNSLIST) != null) {
				for (String key : MainTM.getInstance().signsConf.getConfigurationSection(SIGNS_SIGNSLIST).getKeys(false)) {
					String world = MainTM.getInstance().signsConf.getString(SIGNS_SIGNSLIST + "." + key + "." + SIGNS_WORLD);
					String defWorld = Bukkit.getServer().getWorlds().get(0).getName();
					if (!worlds.contains(world)) {
						MainTM.getInstance().signsConf.set(SIGNS_SIGNSLIST + "." + key + "." + SIGNS_WORLD, defWorld);
						MsgHandler.debugMsg(SIGNSFILENAME + ": World §e" + world + "§b " + signsWrongWorldDebugMsg + " §e" + defWorld + "§b."); // Console debug msg
					}
				}
			}
		}
		// #3.D Initialize the RefreshingSignHandler
		RefreshingSignHandler.loadFromDisk();
		RefreshingSignHandler.startOrRestart();
	}

	/**
	 * Save the signs.yml file
	 */
	public static void SaveSignsYml() {
		try {
			MainTM.getInstance().signsConf.save(MainTM.getInstance().signsFileYaml);
		} catch (IOException e) {
			MsgHandler.errorMsg(MainTM.prefixTM + " " + couldNotSaveSigns);
				e.printStackTrace();
		}
	}

};