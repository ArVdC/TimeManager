package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.CmdsFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;

public class TmSetUseCmds extends MainTM {

	/**
	 * CMD /tm set useCmds [true|false]
	 */
	public static void cmdUseCmds(CommandSender sender, String onOff) {

		// Check if the argument matches what is expected
		if (onOff.equalsIgnoreCase(ARG_TRUE) || onOff.equalsIgnoreCase(ARG_FALSE)) {
			MainTM.getInstance().cmdsConf.set(CMD_SET_USECMDS, onOff);
			CmdsFileHandler.SaveCmdsYml();
			if (onOff.equalsIgnoreCase(ARG_TRUE)) {
				MsgHandler.colorMsg(enableCmdsSchedulerDebugMsg); // Console debug msg (always)
				MsgHandler.playerAdminMsg(sender, enableCmdsSchedulerDebugMsg); // Player debug msg (in case)
			} else if (onOff.equalsIgnoreCase(ARG_FALSE)) {
				MsgHandler.colorMsg(disableCmdsSchedulerDebugMsg); // Console debug msg (always)
				MsgHandler.playerAdminMsg(sender, disableCmdsSchedulerDebugMsg); // Player debug msg (in case)
			}
		}
		// Else, return an error and help message
		else {
			MsgHandler.cmdErrorMsg(sender, MainTM.isNotBooleanMsg, MainTM.CMD_SET + " " + CMD_SET_USECMDS);
		}
	}

};