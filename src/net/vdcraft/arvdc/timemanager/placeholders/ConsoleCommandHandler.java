package net.vdcraft.arvdc.timemanager.placeholders;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

import net.vdcraft.arvdc.timemanager.MainTM;

public class ConsoleCommandHandler implements Listener {

	/**
	 * When a server command is executed, check for {tm_placeholders} and replace them
	 */
	@EventHandler
	public void onCommand(ServerCommandEvent e) {
		
		// #01. Check in the config file if the listener is activated
		String commands = MainTM.getInstance().getConfig().getString(MainTM.CF_PLACEHOLDERS + "." + MainTM.CF_PLACEHOLDER_CMDS);
		if (commands.equalsIgnoreCase(MainTM.ARG_TRUE)) {
			
			// #02. Check if the command contains some placeholder
			String cmd = e.getCommand();
			if (cmd.contains("{" + MainTM.PH_PREFIX)) {

				// #03. Retrieve informations from the sent command
				CommandSender sender = e.getSender();
					// #03.a. If sender is the console, use the server default world
					World w = Bukkit.getServer().getWorlds().get(0);
					// #03.b. If sender is a command block, use this block world
					if (sender instanceof BlockCommandSender) {
						BlockCommandSender bcs = (BlockCommandSender)sender;
						w = bcs.getBlock().getWorld();
					// #03.c. If sender is a minecart, use this minecart world
					} else if (sender instanceof CommandMinecart) {
						CommandMinecart cm = (CommandMinecart)sender;
						w = cm.getWorld();
					// #03.d. If sender is another entity, use this entity world
					} else if (sender instanceof Entity) {
						Entity entity = (Entity)sender;
						w = entity.getWorld();
					}
					// #03.e. Get the world name
					String world = w.getName();
					// #03.f. Use server default language
					String lang = MainTM.getInstance().langConf.getString(MainTM.CF_DEFAULTLANG);

					// #04. Replace placeholders in the message
					String newCmd = PlaceholdersHandler.replaceAllPlaceholders(cmd, world, lang, null);
					
					// #05. Send the modified message
					e.setCommand(newCmd);
			}
		}
	}
	
};