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

import net.vdcraft.arvdc.timemanager.mainclass.CfgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;

public class CreateSentenceCommand implements TabCompleter {
	
	/****************
	*** VARIABLES ***
	****************/

	// Configurer la liste des sous-commandes admins
	List<String> tmCmdArgsList = Arrays.asList("help", "reload", "resync", "servtime", "set");
	// Configurer la liste des arguments possibles pour '/tm reload'
	// Configurer la liste des sous-commandes admins
	List<String> tmHelpArgsList = Arrays.asList("reload", "resync", "servtime", "set");
	// Configurer la liste des arguments possibles pour '/tm reload'
	List<String> tmReloadArgsList = Arrays.asList("all", "config", "lang");
	// Configurer la liste des arguments possibles pour '/tm set'
	List<String> tmSetArgsList = Arrays.asList("deflang", "multilang", "refreshrate", "speed", "start", "time");
	// Configurer la liste des arguments possibles pour '/tm set deflang
	List<String> tmDefLangArgsList = LgFileHandler.setAnyListFromLang("languages");
	// Configurer la liste des arguments possibles pour '/tm set multilang
	List<String> tmMultiLgArgsList = Arrays.asList("true", "false");
	// Configurer la liste des 1ers arguments 'tick'  pour '/tm set start' et '/tm set time'
	List<String> tmTimeArgsList = Arrays.asList("morning", "noon", "midday", "sunset", "dusk", "evening", "night", "midnight", "sunrise", "dawn");
	// Configurer la liste des 1ers arguments 'speed' pour '/tm set speed'
	List<String> tmSpeedArgsList = Arrays.asList("0", "0.5", "1", "1.5", "2", "2.5", "5");
	// Configurer la liste des arguments 'tick' pour '/tm set refreshrate'
	List<String> tmRefRateArgsList = Arrays.asList("5", "10", "15", "20", "25");
	// Configurer la liste des arguments 'world' pour '/tm resync', '/tm set speed', '/tm set start' et '/tm set time'
	List<String> allArg = Arrays.asList("all");
	List<String> worldsArgs = CfgFileHandler.setAnyListFromConfig("worldsList");
	List<String> tmWorldsArgsList = Stream.concat(allArg.stream(), worldsArgs.stream()).collect(Collectors.toList());
	// Configurer la liste des arguments 'units' and 'world' for '/now x x'
	List<String> nowUnitsArgsList = Arrays.asList("hours", "ticks");
	List<String> nowWorldsArgsList = CfgFileHandler.setAnyListFromConfig("worldsList");

	/****************
	***** EVENT *****
	****************/
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		List<String> outputArgsList = new ArrayList<String>();
		MainTM.getInstance();
		
		if(command.getName().equalsIgnoreCase(MainTM.cmdTm)) {
			
			// Always check args length FIRST, then call it/them
			
			if(args.length == 1) { // Command '/tm <...>'
				for(String verif : tmCmdArgsList)
				{
					if(verif.startsWith(args[0])) outputArgsList.add(verif);
				}
			} else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("help")) // Command '/tm help <...>'
				{
					for(String verif : tmHelpArgsList)
					{
						if(verif.startsWith(args[1])) outputArgsList.add(verif);
					}
				} else if(args[0].equalsIgnoreCase("reload")) // Command '/tm reload <...>'
				{
					for(String verif : tmReloadArgsList)
					{
						if(verif.startsWith(args[1])) outputArgsList.add(verif);
					}
				} else if(args[0].equalsIgnoreCase("resync")) // Command '/tm resync <...>'
				{
					for(String verif : tmWorldsArgsList)
					{
						if(verif.startsWith(args[1])) outputArgsList.add(verif);
					}
				} else if(args[0].equalsIgnoreCase("set")) // Command '/tm set <...>'
				{
					for(String verif : tmSetArgsList)
					{
						if(verif.startsWith(args[1])) outputArgsList.add(verif);
					}
				} else {
					return null;
				}
			} else if(args.length == 3) {
				if(args[0].equalsIgnoreCase("set")) // Command /tm set <...> <...>
				{
					if(args[1].equalsIgnoreCase("deflang")) // Command '/tm set deflang <...>'
					{
						for(String verif : tmDefLangArgsList)
						{
							if(verif.startsWith(args[2])) outputArgsList.add(verif);
						}
					} else if(args[1].equalsIgnoreCase("multilang")) // Command '/tm set multilang <...>'
					{
						for(String verif : tmMultiLgArgsList)
						{
							if(verif.startsWith(args[2])) outputArgsList.add(verif);
						}
					} else if(args[1].equalsIgnoreCase("refreshrate")) // Command '/tm set refreshrate <...>'
					{
						for(String verif : tmRefRateArgsList)
						{
							if(verif.startsWith(args[2])) outputArgsList.add(verif);
						}
					} else if(args[1].equalsIgnoreCase("speed")) // Command '/tm set speed <...>'
					{
						for(String verif : tmSpeedArgsList)
						{
							if(verif.startsWith(args[2])) outputArgsList.add(verif);
						}
					} else if(args[1].equalsIgnoreCase("start")) // Command '/tm set start <...>'
					{
						for(String verif : tmTimeArgsList)
						{
							if(verif.startsWith(args[2])) outputArgsList.add(verif);
						}
					} else if(args[1].equalsIgnoreCase("time")) // Command '/tm set time <...>'
					{
						for(String verif : tmTimeArgsList)
						{
							if(verif.startsWith(args[2])) outputArgsList.add(verif);
						}
					}
				} else if(args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("set")) // Command '/tm help set <...>'
				{
					for(String verif : tmSetArgsList)
					{
						if(verif.startsWith(args[2])) outputArgsList.add(verif);
					}
				} else {
					return null;
				}
			} else if(args.length == 4) {
				if((args[0].equalsIgnoreCase("set")) && (args[1].equalsIgnoreCase("speed") || args[1].equalsIgnoreCase("start") || args[1].equalsIgnoreCase("time"))) // Command '/tm set <...> <...> <...>'
				{
					for(String verif : tmWorldsArgsList)
					{
						if(verif.startsWith(args[3])) outputArgsList.add(verif);
				 	}
				} else {
					return null;
				}
			}
		} else if(command.getName().equalsIgnoreCase(MainTM.cmdNow)) {
			if(args.length == 1) { // Command '/now <...>'			
				if(sender.hasPermission("timemanager.now.units"))
				{
					for(String verif : nowUnitsArgsList) {
						if(verif.startsWith(args[0])) outputArgsList.add(verif);
					}
				} else if(sender.hasPermission("timemanager.now.worlds") && !sender.hasPermission("timemanager.now.units"))
				{ 
					for(String verif : nowWorldsArgsList) {
						if(verif.startsWith(args[0])) outputArgsList.add(verif);
					}
				}
			} else if(args.length == 2) // Command '/now <...> <...>'	
			{
				if(sender.hasPermission("timemanager.now.worlds") && sender.hasPermission("timemanager.now.units"))
				{
					for(String verif : nowWorldsArgsList) {
						if(verif.startsWith(args[1])) outputArgsList.add(verif);
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
