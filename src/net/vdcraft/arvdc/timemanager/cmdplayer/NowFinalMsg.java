package net.vdcraft.arvdc.timemanager.cmdplayer;

import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;

public class NowFinalMsg extends MainTM {

	/**	
	 * Send final msg to user
	 */
	public static boolean SendNowMsg(CommandSender sender, String finalWorld, String finaldayPart, String finalTime, String finalLang)
	{	
		// #1. Start loading variables from the lang.yml file
		String msgPrefix = MainTM.getInstance().langConf.getString("languages."+finalLang+".prefix");
		String msgDayPart = MainTM.getInstance().langConf.getString("languages."+finalLang+".dayparts."+finaldayPart);
		String msgNow = MainTM.getInstance().langConf.getString("languages."+finalLang+".msg");		
		// #2. Avoid showing actual time if player is in a nether or the_end world
		if(finalWorld.contains("_nether") || finalWorld.contains("_the_end"))
		{
			msgNow = MainTM.getInstance().langConf.getString("languages."+finalLang+".noMsg");
			// #3. If the noMsg in lang.yml file is empty, nothing will be send to the player
			if(msgNow.equalsIgnoreCase("")) {
				return true;
			}
		}
		// #4. Process the message content
		msgPrefix = msgPrefix.replace("&", "§");
		msgNow = msgNow.replace("&", "§");
		msgNow = msgNow.replace("{player}", sender.getName());
		msgNow = msgNow.replace("{time}", finalTime);
		msgNow = msgNow.replace("{targetWorld}", finalWorld);
		msgNow = msgNow.replace("{dayPart}", msgDayPart);		
		// #5. Send the message
		sender.sendMessage(msgPrefix + "§r " + msgNow);
		return true;
	};

}