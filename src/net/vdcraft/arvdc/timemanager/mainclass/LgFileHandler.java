package net.vdcraft.arvdc.timemanager.mainclass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import net.vdcraft.arvdc.timemanager.MainTM;

public class LgFileHandler extends MainTM {

	/** 
	 * Activate or reload the language file
	 */
    public static void loadLang(String firstOrRe) {
    	
    	// #1. When it is the server startup
    	if(firstOrRe.equalsIgnoreCase("first")) {
    		// Creation of lang.yml file if doesn't exist
		    if(!(MainTM.getInstance().langFileYaml.exists())) { 	    
		    	Bukkit.getLogger().info(prefixTM + " " + lgFileCreaMsg); // Console log msg
		    	 // Copy the file from src in .jar
				CopyFilesHandler.copy(MainTM.getInstance().getResource(langFileName), MainTM.getInstance().langFileYaml);
				// Actualize values
				MainTM.getInstance().langConf = YamlConfiguration.loadConfiguration(MainTM.getInstance().langFileYaml); 
		    } else {
		    	Bukkit.getLogger().info(prefixTM + " " + lgFileExistMsg); // Console log msg
		    }
	    }
    	
    	// #2. When using the admin command /tm reload
    	if(firstOrRe.equalsIgnoreCase("re")) {
		    if(MainTM.getInstance().langFileYaml.exists()) { 		
	    		// Notification
	            Bukkit.getLogger().info(prefixTM + " " + lgFileTryReloadMsg);
				// Reload values from lang.yml file
				MainTM.getInstance().langConf = YamlConfiguration.loadConfiguration(MainTM.getInstance().langFileYaml);
		    } else loadLang("first");
    	}
    	
    	// #3. In both case
    	
    	// #A. Restore fixed values
		MainTM.getInstance().langConf.set("version", versionTM);
		MainTM.getInstance().langConf.set("languages.default.prefix", prefixTMColor);
		MainTM.getInstance().langConf.set("languages.default.msg", defaultMsg);
		MainTM.getInstance().langConf.set("languages.default.noMsg", defaultNoMsg);
    	MainTM.getInstance().langConf.set("languages.default.dayparts.day", defaultDay);
	    MainTM.getInstance().langConf.set("languages.default.dayparts.dusk", defaultDusk); 
	    MainTM.getInstance().langConf.set("languages.default.dayparts.night", defaultNight); 
	    MainTM.getInstance().langConf.set("languages.default.dayparts.dawn", defaultDawn);	   
	    
	    // #B. Check 'defLang' integrity 
	    checkDefLang();
	    	    
		// #C. Is multilanguage enable ? Set to false if doesn't exist or if invalid boolean	    
	    if(MainTM.getInstance().langConf.getKeys(false).contains("useMultiLang")) {
			if(MainTM.getInstance().langConf.getString("useMultiLang").equalsIgnoreCase("true")) {
				Bukkit.getLogger().info(prefixTM + " " + multiLangIsOnMsg);
			} else {
	    		MainTM.getInstance().langConf.set("useMultiLang", "false");
				Bukkit.getLogger().info(prefixTM + " " + multiLangIsOffMsg);
			}
	    } else {
    		MainTM.getInstance().langConf.set("useMultiLang", "false");
			Bukkit.getLogger().info(prefixTM + " " + multiLangIsOffMsg);
	    }
	    
		// #D. Save the lang.yml file
	    SaveLangYml();
				
		// #E. Notification
        Bukkit.getLogger().info(prefixTM + " " + lgVersionMsg + MainTM.getInstance().langConf.getString("version") + ".");
    };
    
	/** 
	 * Check 'defaultLang' integrity in lang.yml
	 */
    // Check if 'defaultLang' key exists in yaml, if not create it and set it to default
    private static void checkDefLang() {
	    if(!MainTM.getInstance().langConf.getKeys(false).contains("defaultLang")) {
	    	restoreDefLang();
	    } else { // Else, if 'defaultLang' key exists but is void set it to default
	    	if(MainTM.getInstance().langConf.getString("defaultLang").equals("")) {
	    		MainTM.getInstance().langConf.set("defaultLang", "default");
	    	}
		  	// Then actualize 'defaultLang' from lang.yml file for checking
			serverLang = new String(MainTM.getInstance().langConf.getString("defaultLang"));
			MainTM.getInstance().laConsole.sendMessage(prefixTM + " " + defLangCheckMsg + " §e" + serverLang + "§r."); // Console log msg
			// Check if key 'defaultLang' correspond to an existing language who contains every needed keys
		    if(!MainTM.getInstance().langConf.getConfigurationSection("languages").getKeys(false).contains(serverLang)) {
		    	restoreDefLang();    	
			} else {
				Set<String> langKeys = MainTM.getInstance().langConf.getConfigurationSection("languages."+serverLang).getKeys(true);
				if(langKeys.contains("prefix") && langKeys.contains("msg") && langKeys.contains("noMsg") && langKeys.contains("dayparts") && langKeys.contains("dayparts.day") && langKeys.contains("dayparts.dusk") && langKeys.contains("dayparts.night") && langKeys.contains("dayparts.dawn")) {
				    // If every key exists, keep actual 'defLang'
				    MainTM.getInstance().laConsole.sendMessage(prefixTM + " §e" + serverLang + "§r " + defLangOkMsg); // Console log msg
				} else {
					restoreDefLang();
				}
			}
	    }
	};
    
	/** 
	 * Restore the 'default' translation in lang.yml
	 */ 
	private static void restoreDefLang() { 
		MainTM.getInstance().langConf.set("defaultLang", "default");
	    serverLang = new String(MainTM.getInstance().langConf.getString("defaultLang"));
		MainTM.getInstance().laConsole.sendMessage(prefixTM + " §e" + serverLang + "§r " + defLangResetMsg);	// Console log msg
	}

	/** 
	 * Save the lang.yml
	 */
	public static void SaveLangYml() { 
		try {
			MainTM.getInstance().langConf.save(MainTM.getInstance().langFileYaml);
		} catch (IOException e) {
			Bukkit.getLogger().severe(prefixTM + " " + couldNotSaveLang);
			e.printStackTrace();
		}
	};

	/** 
	 * Return an array list from everything listed in a specific key from the lang.yml
	 */ 
	public static List<String> setAnyListFromLang(String inWichYamlKey)	{
		List<String> listedElementsList = new ArrayList<>();
		for(String listedElement : MainTM.getInstance().langConf.getConfigurationSection(inWichYamlKey).getKeys(false))	{
			listedElementsList.add(listedElement);
		}			
		return listedElementsList;
	};
    
}