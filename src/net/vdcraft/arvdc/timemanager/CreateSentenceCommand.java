/**********************
**** TAB COMPLETER ****
**********************/

package net.vdcraft.arvdc.timemanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.mainclass.CfgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class CreateSentenceCommand implements TabCompleter {
	
	/****************
	*** VARIABLES ***
	****************/

	// List of admin sub-commands 
	List<String> tmCmdArgsList = Arrays.asList("checksql", "checktimers", "help", "reload", "resync", "set");
	// List of admin sub-commands having a 'help'
	List<String> tmHelpArgsList = Arrays.asList("checksql", "checktimers", "reload", "resync", "set");
	// Arguments list for '/tm reload'
	List<String> tmReloadArgsList = Arrays.asList("all", "config", "lang");
	// Arguments list for '/tm set'
	List<String> tmSetArgsList = Arrays.asList("debugmode", "deflang", "multilang", "refreshrate", "sleep", "speed", "start", "sync", "time");
	// Arguments list for '/tm set deflang
	List<String> tmDefLangArgsList() {
		return LgFileHandler.setAnyListFromLang("languages");
	}
	// Arguments list for '/tm set multilang
	List<String> tmBooleanArgsList = Arrays.asList("true", "false");
	// First 'tick' arguments for '/tm set start' et '/tm set time'
	List<String> tmTimeArgsList = Arrays.asList("morning", "noon", "midday", "sunset", "dusk", "evening", "night", "midnight", "sunrise", "dawn");
	// Modifier arguments for '/tm set speed'
	List<String> tmSpeedArgsList = Arrays.asList("0", "0.5", "1", "1.5", "2", "2.5", "5", "realtime");
	// 'tick' arguments list for '/tm set refreshrate'
	List<String> tmRefRateArgsList = Arrays.asList("5", "10", "15", "20", "25");
	// 'world' arguments list for '/tm resync', '/tm set speed', '/tm set start' et '/tm set time' etc.
	List<String> allArg = Arrays.asList("all");
	List<String> tmWorldsArgsList(CommandSender sender) {
		List<String> worldsArgs = CfgFileHandler.setAnyListFromConfig("worldsList");
		if(sender instanceof Player) { // Hack it only for players
			worldsArgs = ValuesConverter.replaceSpacesInList(worldsArgs);
		}
		return Stream.concat(allArg.stream(), worldsArgs.stream()).collect(Collectors.toList());
	}
	
	// Arguments list for 'units' and 'world' for '/now x x'
	List<String> nowUnitsArgsList = Arrays.asList("hours", "ticks");
	List<String> nowWorldsArgsList() {
		List<String> worldsArgs = CfgFileHandler.setAnyListFromConfig("worldsList");
		worldsArgs = ValuesConverter.replaceSpacesInList(worldsArgs);
		return worldsArgs;
	}

	/****************
	***** EVENT *****
	****************/
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> outputArgsList = new ArrayList<String>();
		MainTM.getInstance();
		
		if(command.getName().equalsIgnoreCase(MainTM.cmdTm)) {
			
			// Always check args length FIRST, then call it/them
			
			if(args.length == 1) { // Command '/tm <...>'
				for(String verif : tmCmdArgsList)
				{
					if(verif.toLowerCase().startsWith(args[0].toLowerCase())) outputArgsList.add(verif);
				}
			} else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("help")) // Command '/tm help <...>'
				{
					for(String verif : tmHelpArgsList)
					{
						if(verif.toLowerCase().startsWith(args[1].toLowerCase())) outputArgsList.add(verif);
					}
				} else if(args[0].equalsIgnoreCase("reload")) // Command '/tm reload <...>'
				{
					for(String verif : tmReloadArgsList)
					{
						if(verif.toLowerCase().startsWith(args[1].toLowerCase())) outputArgsList.add(verif);
					}
				} else if(args[0].equalsIgnoreCase("resync")) // Command '/tm resync <...>'
				{
					for(String verif : tmWorldsArgsList(sender))
					{
						if(verif.toLowerCase().startsWith(args[1].toLowerCase())) outputArgsList.add(verif);
					}
				} else if(args[0].equalsIgnoreCase("set")) // Command '/tm set <...>'
				{
					for(String verif : tmSetArgsList)
					{
						if(verif.toLowerCase().startsWith(args[1].toLowerCase())) outputArgsList.add(verif);
					}
				} else {
					return null;
				}
			} else if(args.length == 3) {
				if(args[0].equalsIgnoreCase("set")) // Command /tm set <...> <...>
				{
					if(args[1].equalsIgnoreCase("deflang")) // Command '/tm set deflang <...>'
					{
						for(String verif : tmDefLangArgsList())
						{
							if(verif.toLowerCase().startsWith(args[2].toLowerCase())) outputArgsList.add(verif);
						}
					} else if(args[1].equalsIgnoreCase("debugmode")) // Command '/tm set debugmode <...>'
					{
						for(String verif : tmBooleanArgsList)
						{
							if(verif.toLowerCase().startsWith(args[2].toLowerCase())) outputArgsList.add(verif);
						}
					} else if(args[1].equalsIgnoreCase("multilang")) // Command '/tm set multilang <...>'
					{
						for(String verif : tmBooleanArgsList)
						{
							if(verif.toLowerCase().startsWith(args[2].toLowerCase())) outputArgsList.add(verif);
						}
					} else if(args[1].equalsIgnoreCase("refreshrate")) // Command '/tm set refreshrate <...>'
					{
						for(String verif : tmRefRateArgsList)
						{
							if(verif.toLowerCase().startsWith(args[2].toLowerCase())) outputArgsList.add(verif);
						}
					} else if(args[1].equalsIgnoreCase("sleep")) // Command '/tm set sleep <...>'
					{
						for(String verif : tmBooleanArgsList)
						{
							if(verif.toLowerCase().startsWith(args[2].toLowerCase())) outputArgsList.add(verif);
						}
					} else if(args[1].equalsIgnoreCase("speed")) // Command '/tm set speed <...>'
					{
						for(String verif : tmSpeedArgsList)
						{
							if(verif.toLowerCase().startsWith(args[2].toLowerCase())) outputArgsList.add(verif);
						}
					} else if(args[1].equalsIgnoreCase("start")) // Command '/tm set start <...>'
					{
						for(String verif : tmTimeArgsList)
						{
							if(verif.toLowerCase().startsWith(args[2].toLowerCase())) outputArgsList.add(verif);
						}
					} else if(args[1].equalsIgnoreCase("sync")) // Command '/tm set sync <...>'
					{
						for(String verif : tmBooleanArgsList)
						{
							if(verif.toLowerCase().startsWith(args[2].toLowerCase())) outputArgsList.add(verif);
						}
					} else if(args[1].equalsIgnoreCase("time")) // Command '/tm set time <...>'
					{
						for(String verif : tmTimeArgsList)
						{
							if(verif.toLowerCase().startsWith(args[2].toLowerCase())) outputArgsList.add(verif);
						}
					}
				} else if(args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("set")) // Command '/tm help set <...>'
				{
					for(String verif : tmSetArgsList)
					{
						if(verif.toLowerCase().startsWith(args[2].toLowerCase())) outputArgsList.add(verif);
					}
				} else {
					return null;
				}
			} else if(args.length == 4) {
				if((args[0].equalsIgnoreCase("set")) && (args[1].equalsIgnoreCase("speed") || args[1].equalsIgnoreCase("start") || args[1].equalsIgnoreCase("time") || args[1].equalsIgnoreCase("sleep") || args[1].equalsIgnoreCase("sync"))) // Command '/tm set <...> <...> <...>'
				{
					for(String verif : tmWorldsArgsList(sender))
					{
						if(verif.toLowerCase().startsWith(args[3].toLowerCase())) outputArgsList.add(verif);
				 	}
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else if(command.getName().equalsIgnoreCase(MainTM.cmdNow)) {
			if(args.length == 1) { // Command '/now <...>'			
				if(sender.hasPermission("timemanager.now.units"))
				{
					for(String verif : nowUnitsArgsList) {
						if(verif.toLowerCase().startsWith(args[0].toLowerCase())) outputArgsList.add(verif);
					}
				}
				if(sender.hasPermission("timemanager.now.worlds"))
				{ 
					for(String verif : nowWorldsArgsList()) {
						if(verif.toLowerCase().startsWith(args[0].toLowerCase())) outputArgsList.add(verif);
					}
				}
			} else if(args.length == 2) // Command '/now <...> <...>'	
			{
				if(sender.hasPermission("timemanager.now.worlds") && sender.hasPermission("timemanager.now.units"))
				{
					for(String verif : nowWorldsArgsList()) {
						if(verif.toLowerCase().startsWith(args[1].toLowerCase())) outputArgsList.add(verif);
					}
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
		return outputArgsList;
	};

}
