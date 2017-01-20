/***********************
**** PLAYER COMMAND ****
***********************/

package net.vdcraft.arvdc.timemanager;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCmdExecutor implements CommandExecutor {

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	
		// #1. Check if sender is a Player or the Console
		if(sender instanceof Player) {} else {return false;}
		
		// #2. Accept max two arguments (units and world)
		if(args.length <= 2) {} else {return false;}

		// #3. Get the world
		World worldToDisplay = ((Player) sender).getWorld(); // First set this to the world he's stand in
		// Check if player give a world name in argument + if he has permission to do that
		if(args.length == 1 && sender.hasPermission("timemanager.now.worlds")) {
		
			for(World loadedWorld : Bukkit.getServer().getWorlds()) {	// List the loaded worlds on the server
			
				if(loadedWorld.getName().equals(args[0])) {
				
					worldToDisplay = loadedWorld;
				}
			}	
		} else if(args.length == 2 && sender.hasPermission("timemanager.now.worlds")) {
		
			for(World loadedWorld : Bukkit.getServer().getWorlds())	{ // List the loaded worlds on the server
			
				if(loadedWorld.getName().equals(args[1])) {
				
					worldToDisplay = loadedWorld;
				}
			}
		}
		String worldNameToDisplay = worldToDisplay.getName();
		
		// #4. Get the actual tick in regard of the world value
		Long timeInTicks = worldToDisplay.getTime();
		
		// #5. Define the part of the days in regard of the tick value
		String dayPartToDisplay = net.vdcraft.arvdc.timemanager.cmdplayer.NowGetDayPart.SetDayPartToDisplay(timeInTicks);

		// #6. Check if the cmd arg is 'hours' or 'ticks' to set the time format
		String timeToDisplay = "tick #" + timeInTicks.toString(); // Format time to display
		String defUnits = MainTM.getInstance().getConfig().getString("defTimeUnits"); // By default, check the config.yml
		if(args.length > 0 && sender.hasPermission("timemanager.now.units") && (args[0].equalsIgnoreCase("hours") || args[0].equalsIgnoreCase("ticks"))) {
			defUnits = args[0].toString(); // Else store command argument as actual time units param
		}
			if(defUnits.equalsIgnoreCase("hours")) {				
				timeToDisplay = net.vdcraft.arvdc.timemanager.cmdplayer.NowFormatTime.ticksAsTime(timeInTicks); // Convert time display format	
			}
		// #7. Check the player's locale and try to use it
		String langToUse = net.vdcraft.arvdc.timemanager.cmdplayer.UserDefineLang.setLangToUse(sender);
		
		// #8. Send final msg to user, who returns a 'true' value
		return net.vdcraft.arvdc.timemanager.cmdplayer.NowFinalMsg.SendNowMsg(sender, worldNameToDisplay, dayPartToDisplay, timeToDisplay, langToUse);
	};
}