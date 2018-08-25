package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;

import net.vdcraft.arvdc.timemanager.MainTM;

public class McVersionHandler extends MainTM {
	
	/** 
	 *  Get the version of the server and return only the type (Bukkit/Spigot/Paper)
	 */
	public static String KeepTypeOfServer() {
		if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + serverTypeQueryDebugMsg); // Console debug msg
		String splitMarker = "ArVdC";
		String[] split1;
		String split2;
		String[] split3;
		String serverType;
		String completeServerVersion = Bukkit.getVersion().toLowerCase();
		if(completeServerVersion.contains("git-")) { // If the syntax is normal
			completeServerVersion = completeServerVersion.replace("git-", splitMarker);
			split1 = completeServerVersion.split(splitMarker);
			if(devMode == true) { // Dev msg start
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Version string was split into: §e" + split1.length + "§b part(s)."); // Console dev msg
				Integer count = 0;
				for(String split : split1) {
					Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " [" + count + "]: §e" + split ); // Console dev msg
					count++;
				}
			} // Dev msg end
			split2 = split1[1]; // Keep only what is after the "git-"
			split2 = split2.replace("-", splitMarker); // Tag the character "-" after the version value 
			split3 = split2.split(splitMarker); // Keep only what was before the first "-" character
			serverType = split3[0].substring(0, 1).toUpperCase() + split3[0].substring(1).toLowerCase(); // Keep the name part of the version and capitalize it
		} else { // In case 'git' string doesn't exist
			serverType = "other type of";
		}
		if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + serverTypeResultDebugMsg + " §e" + serverType + " §bserver.");
		return serverType;
	}
	
	/** 
	 *  Get the version of the server and return only the MC decimal part
	 */
	public static Double KeepDecimalOfMcVersion() {
		if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + serverMcVersionQueryDebugMsg); // Console debug msg
		String splitMarker = "oAoroVodoCo";
		String[] split1;
		String split2;
		String[] split3;
		String[] split4;
		String mcVersionString;
		Double mcVersion;
		String completeServerVersion = Bukkit.getVersion().toLowerCase();
		if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + completeVersionDebugMsg + " §e" + completeServerVersion); // Console debug msg
		if(completeServerVersion.contains("(mc: 1.")) { // For usual version syntax
			completeServerVersion = completeServerVersion.replace("(mc: 1.", splitMarker);
			split1 = completeServerVersion.split(splitMarker);
			if(devMode == true) { // Dev msg start
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Lenght of list: §e" + split1.length); // Console dev msg
				Integer count = 0;
				for(String split : split1) {
					Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " [" + count + "]: §e" + split ); // Console dev msg
					count++;
				}
			} // Dev msg end
			split2 = split1[1]; // Keep only what is after the "(mc: 1."
			split2 = split2.replace(")", splitMarker); // Tag the character ")" after the version value 
			
		} else if(completeServerVersion.contains("1.")) {  // For other type of syntax (less specific format, so it could crash sometimes)
			completeServerVersion = completeServerVersion.replace("1.", splitMarker);
			split1 = completeServerVersion.split(splitMarker);
			if(devMode == true) { // Dev msg start
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Lenght of list: §e" + split1.length); // Console dev msg
				Integer count = 0;
				for(String split : split1) {
					Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " [" + count + "]: §e" + split ); // Console dev msg
					count++;
				}
			} // Dev msg end
			split2 = split1[1];
			split2 = split2.replace(")", splitMarker).replace("]", splitMarker).replace("-", splitMarker).replace("_", splitMarker).replace(" ", splitMarker);
		} else { // Use the latest version of MC
			if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + noVersionNumberDebugMsg + " '" + completeServerVersion + "'."); // Console debug msg
			mcVersion = minRequiredMcVersion;
			Bukkit.getLogger().warning(prefixTM + " 1. " + unknowVersionMsg); // Console warn msg
			return mcVersion;
		}
		// Then, for the 2 first cases
		split3 = split2.split(splitMarker); // Keep only what was before a ")", "]", "-", "_" or " " character
		mcVersionString = split3[0]; // If version is in a " 1.x" format, keep it
		if(mcVersionString.contains(".")) { // But if version is in a " 1.x.x" format, check if a "0" needs to be add before the last number
			String mcVersionSplit = mcVersionString.replace(".", splitMarker);
			split4 = mcVersionSplit.split(splitMarker);
			String firstPart = split4[0];
			String secondPart = split4[1];
			if(secondPart.length() == 1) {
				secondPart = "0" + secondPart;
			}
			if(devMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Needed to adjust the decimal to compare them correctly: §e1." + mcVersionString + "§b = §e1." + firstPart + "." + secondPart + "§b."); // Console dev msg
			mcVersionString = firstPart + "." + secondPart;
		} 	
		try { // Check if value could be parsed as a double
    		mcVersion = Double.parseDouble(mcVersionString);
		} catch (NumberFormatException nfe) { // If not possible, use the latest version of MC
			if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + wrongVersionNumberDebugMsg + "\n" + nfe);
			mcVersion = minRequiredMcVersion;
			Bukkit.getLogger().warning(prefixTM + " " + unknowVersionMsg); // Console warn msg
		}		
		if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + serverMcVersionResultDebugMsg + " §e1." + split3[0] + " §bMC version.");
		return mcVersion;
	}

};