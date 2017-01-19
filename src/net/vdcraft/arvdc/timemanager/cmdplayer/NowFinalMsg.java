package net.vdcraft.arvdc.timemanager.cmdplayer;

import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;

public class NowFinalMsg extends MainTM {

	/**	
	 * Send final msg to user
	 */
	public static boolean SendNowMsg(CommandSender sender, String finalWorld, String finaldayPart, String finalTime, String finalLang)
	{
		String msgPrefix = MainTM.getInstance().langConf.getString("languages."+finalLang+".prefix");
		String msgNow = MainTM.getInstance().langConf.getString("languages."+finalLang+".msg");
		String msgDayPart = MainTM.getInstance().langConf.getString("languages."+finalLang+".dayparts."+finaldayPart);
		msgPrefix = msgPrefix.replace("&", "§");
		msgNow = msgNow.replace("&", "§");
		msgNow = msgNow.replace("{player}", sender.getName());
		msgNow = msgNow.replace("{time}", finalTime);
		msgNow = msgNow.replace("{targetWorld}", finalWorld);
		msgNow = msgNow.replace("{dayPart}", msgDayPart);
		sender.sendMessage(msgPrefix + "§r " + msgNow);
		return true;
	};
}
