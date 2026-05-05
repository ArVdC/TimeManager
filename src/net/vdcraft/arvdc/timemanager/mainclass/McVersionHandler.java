package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;

import net.vdcraft.arvdc.timemanager.MainTM;

public class McVersionHandler extends MainTM {

	/**
	 * Get the version of the server and return only the type (Bukkit/Spigot/Paper/...)
	 */
	public static String KeepTypeOfServer() {
		MsgHandler.debugMsg(serverTypeQueryDebugMsg);
		String name = Bukkit.getName();
		if (name == null || name.isEmpty()) name = "Bukkit";
		String serverType = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
		MsgHandler.devMsg(serverTypeResultDebugMsg + " §e" + serverType + " §9server.");
		return serverType;
	}

	/**
	 * Get the version of the server and return only the MC decimal part
	 */
	public static Double KeepDecimalOfMcVersion() {
		MsgHandler.debugMsg(serverMcVersionQueryDebugMsg);
		String completeServerVersion = Bukkit.getVersion();
		MsgHandler.debugMsg(completeVersionDebugMsg + " §e" + completeServerVersion);

		String mcRaw = extractMcVersion(completeServerVersion);
		if (mcRaw == null) {
			Double bukkitFallback = parseBukkitVersion();
			if (bukkitFallback != null) {
				MsgHandler.debugMsg(serverMcVersionResultDebugMsg + " §e" + bukkitFallback + " §bMC version.");
				return bukkitFallback;
			}
			MsgHandler.debugMsg(noVersionNumberDebugMsg + " '" + completeServerVersion + "'.");
			MsgHandler.warnMsg("1. " + versionMCFormatMsg);
			return reqMcVToLoadPlugin;
		}

		Double parsed = mcVersionToDouble(mcRaw);
		if (parsed == null) {
			MsgHandler.debugMsg(wrongVersionNumberDebugMsg);
			MsgHandler.warnMsg(versionMCFormatMsg);
			return reqMcVToLoadPlugin;
		}
		MsgHandler.debugMsg(serverMcVersionResultDebugMsg + " §e" + mcRaw + " §bMC version.");
		return parsed;
	}

	private static String extractMcVersion(String completeServerVersion) {
		if (completeServerVersion == null) return null;
		String s = completeServerVersion.toLowerCase();
		int idx = s.indexOf("(mc:");
		if (idx >= 0) {
			int end = s.indexOf(')', idx);
			if (end < 0) end = s.length();
			return s.substring(idx + 4, end).trim();
		}
		return null;
	}

	private static Double parseBukkitVersion() {
		try {
			String bv = Bukkit.getBukkitVersion();
			if (bv == null) return null;
			int dash = bv.indexOf('-');
			String head = dash > 0 ? bv.substring(0, dash) : bv;
			return mcVersionToDouble(head);
		} catch (Throwable t) {
			return null;
		}
	}

	private static Double mcVersionToDouble(String raw) {
		if (raw == null) return null;
		String v = raw.trim();
		if (v.startsWith("v")) v = v.substring(1);
		String[] parts = v.split("\\.");
		try {
			int major = Integer.parseInt(parts[0]);
			int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
			int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
			if (major == 1) {
				String patchPart = String.valueOf(patch);
				if (patchPart.length() == 1) patchPart = "0" + patchPart;
				return Double.parseDouble(minor + "." + patchPart);
			}
			if (major >= 26) {
				return 99.99;
			}
			String minorPart = String.valueOf(minor);
			if (minorPart.length() == 1) minorPart = "0" + minorPart;
			return Double.parseDouble(major + "." + minorPart);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

};
