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

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdplayer.UserMsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class PlayerCmdExecutor implements CommandExecutor {

    /*****************
     ***** EVENT *****
     *****************/

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

	// #1. Check if sender is a Player or the Console
	if (sender instanceof Player) {
	} else {
	    return false;
	}

	// #2. Get the world the player stands in as default value
	World worldToDisplay = ((Player) sender).getWorld();

	// #3. Check if player has permission to define the world with an argument
	if (sender.hasPermission("timemanager.now.worlds")) {
	    // #3. Check if at least one argument exists, if yes, check it is equal to a
	    // unit of time the redefine the world name
	    Integer nbArgum = args.length;
	    Integer argumRest = nbArgum;
	    Integer argumActu;
	    String givenWorldName = new String();
	    if (nbArgum == 1) {
		if (args[0].equalsIgnoreCase("hours") || args[0].equalsIgnoreCase("ticks")) {
		} else {
		    givenWorldName = args[0];
		}
	    } else if (nbArgum == 2) {
		if (args[0].equalsIgnoreCase("hours") || args[0].equalsIgnoreCase("ticks")) {
		    givenWorldName = args[1];
		} else {
		    givenWorldName = args[0] + " " + args[1];
		}
	    } else if (nbArgum > 2) {
		if (args[0].equalsIgnoreCase("hours") || args[0].equalsIgnoreCase("ticks")) {
		    givenWorldName = args[1];
		    argumActu = 2;
		    while (argumRest > 2) {
			givenWorldName = givenWorldName + " " + args[argumActu];
			argumRest--;
			argumActu++;
		    }
		} else {
		    givenWorldName = args[0];
		    argumActu = 1;
		    while (argumRest > 1) {
			givenWorldName = givenWorldName + " " + args[argumActu];
			argumRest--;
			argumActu++;
		    }
		}
	    }
	    // #4. Compare given world name with permitted worlds list
	    for (World loadedWorld : Bukkit.getServer().getWorlds()) { // List the loaded worlds on the server

		if (loadedWorld.getName().equals(givenWorldName)) {

		    worldToDisplay = loadedWorld; // Switch from the player's world to the specified one
		}
	    }
	}

	// #5. Use the name of the targeted world
	String worldNameToDisplay = worldToDisplay.getName();

	// #6. Get the actual tick in regard of the world value
	Long timeInTicks = worldToDisplay.getTime();

	// #7. Define the part of the days in regard of the tick value
	String dayPartToDisplay = ValuesConverter.SetDayPartToDisplay(timeInTicks);

	// #8. Check if the first arg is 'hours' or 'ticks' to set the time format
	String timeToDisplay = "tick #" + timeInTicks.toString(); // Format time to display
	String defUnits = MainTM.getInstance().getConfig().getString(MainTM.CF_DEFTIMEUNITS); // By default, check the config.yml
	if (args.length > 0 && sender.hasPermission("timemanager.now.units")
		&& (args[0].equalsIgnoreCase("hours") || args[0].equalsIgnoreCase("ticks"))) {
	    defUnits = args[0].toString(); // Else store command argument as actual time units param
	}
	if (defUnits.equalsIgnoreCase("hours")) {
	    timeToDisplay = ValuesConverter.returnTimeFromTickValue(timeInTicks); // Convert time display format
	}
	// #9. Check the player's locale and try to use it
	String langToUse = UserMsgHandler.setLangToUse(sender);

	// #10. Send final msg to user, who will returns a 'true' value at the end
	return UserMsgHandler.SendNowMsg(sender, worldNameToDisplay, dayPartToDisplay, timeToDisplay, langToUse);
    };
}