package net.vdcraft.arvdc.timemanager.mainclass;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.scheduler.BukkitScheduler;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.placeholders.PlaceholdersHandler;

public class CmdsScheduler extends MainTM {

	/**
	 * Execute a command at a precise time with a scheduler
	 */
	public static void commandsScheduler() {

		BukkitScheduler commandsScheduler = MainTM.getInstance().getServer().getScheduler();

		long refreshRate = 5L;
		
		cmdsTask = commandsScheduler.scheduleSyncRepeatingTask(MainTM.getInstance(), new Runnable() {

			@Override
			public void run() {
				// #1. Declare inactive and cancel repetition if useCmds is false
				if (MainTM.getInstance().cmdsConf.getString(CMDS_USECOMMANDS).equalsIgnoreCase(ARG_FALSE)) {
					commandsSchedulerIsActive.remove(ARG_ACTIVE);
					stopCmdsScheduler();
					MsgHandler.devMsg("The commands scheduler is stopped.");
					// #2. Declare active if useCmds is true (once only)
				} else if (!commandsSchedulerIsActive.contains(ARG_ACTIVE)) {
					commandsSchedulerIsActive.add(ARG_ACTIVE);
					MsgHandler.devMsg("The commands scheduler is started.");
				}

				MsgHandler.devMsg("The scheduler list is active and contains : " + commandsSchedulerIsActive);
				MsgHandler.devMsg("==================");

				for (String key : MainTM.getInstance().cmdsConf.getConfigurationSection(CMDS_COMMANDSLIST).getKeys(false)) {

					// #3. Get the time reference (MC world or UTC)
					String refTimeSrc = MainTM.getInstance().cmdsConf.getString(CMDS_COMMANDSLIST + "." + key + "." + CMDS_REFTIME);					

					// #4. Get the repeat frequency
					String repeatFreq = MainTM.getInstance().cmdsConf.getString(CMDS_COMMANDSLIST + "." + key + "." + CMDS_REPEATFREQ);

					// #5. Get the expected date and time
					// #5.A. Get the date 
					String eDate = MainTM.getInstance().cmdsConf.getString(CMDS_COMMANDSLIST + "." + key + "." + CMDS_DATE);
					Integer expectedYear = 1;
					Integer expectedMonth = 1;
					Integer expectedMDay = 1;
					Integer expectedWDay = 1;
					String[] ed = eDate.split("-");
					try { // The date is supposed to be in correct format (yyyy-mm-dd)
						expectedYear = Integer.parseInt(ed[0]);
						expectedMonth = Integer.parseInt(ed[1]);
						expectedMDay = Integer.parseInt(ed[2]);
						expectedWDay = ValuesConverter.dayInWeek(ValuesConverter.tickFromFormattedDate(eDate));
					} catch (NumberFormatException nfe) { MsgHandler.errorMsg(dateFormatMsg); } // Console error msg
					// #5.B. Get the time 
					String eTime = MainTM.getInstance().cmdsConf.getString(CMDS_COMMANDSLIST + "." + key + "." + CMDS_TIME);
					Integer expectedHour = 0;
					Integer expectedMin = 0;
					String[] expectedTime = eTime.split(":");
					expectedHour = Integer.parseInt(expectedTime[0]);
					expectedMin = Integer.parseInt(expectedTime[1]);
					
					// #5.C. Also set a LocalDateTimeDateTime
					LocalDateTime expectedDateTime = LocalDateTime.of(expectedYear, expectedMonth, expectedMDay, expectedHour, expectedMin);

					// #6. Get the current date and time
					Integer currentYear = null;
					Integer currentMonth = null;
					Integer currentMDay = null;
					Integer currentWDay = null;
					Integer currentHour = null;
					Integer currentMin = null;
					// Set a default delay
					int minutesBeforeEnd = 1; // (= 1 real minute before the edge time)
					long ticksBeforeEnd = minutesBeforeEnd * 1200L; // (= 1 * 1 real minute before be erased from the active list)
					
					// #6.A. If the reference time is an MC world
					if (!refTimeSrc.contains("UTC") && !refTimeSrc.equalsIgnoreCase("")) {
						// Get the date
						Long currentFullTick = Bukkit.getWorld(refTimeSrc).getFullTime();
						Long cDate = ValuesConverter.daysFromTick(currentFullTick);
						currentYear = Integer.parseInt(ValuesConverter.dateFromElapsedDays(cDate, PH_YYYY));
						currentMonth = Integer.parseInt(ValuesConverter.dateFromElapsedDays(cDate, PH_MM));
						currentMDay = Integer.parseInt(ValuesConverter.dateFromElapsedDays(cDate, PH_DD));
						// Get the week day number
						currentWDay = ValuesConverter.dayInWeek(Bukkit.getWorld(refTimeSrc).getFullTime());
						// Get the time
						Long currentTick = Bukkit.getWorld(refTimeSrc).getTime();
						String cHour = ValuesConverter.formattedTimeFromTick(currentTick, false);
						String[] ch = cHour.split(":");
						currentHour = Integer.parseInt(ch[0]);
						currentMin = Integer.parseInt(ch[1]);
						// Set the active delay length
						String speedParam = ValuesConverter.wichSpeedParam(Bukkit.getWorld(refTimeSrc).getTime());
						Double speed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + refTimeSrc + "." + speedParam);
						Double ticksPerMinute = (1000 / 60) / speed;
						minutesBeforeEnd = (int) (Math.ceil(refreshRate / ticksPerMinute) * 2) - 1; // MC minutes before the edge time = at less 2 * refreshRate cycle
						ticksBeforeEnd = (long) (Math.ceil((minutesBeforeEnd * ticksPerMinute) * 2.3)); // Number of ticks before the key is deleted from the active list (multiply by 2.3 permit to stay under a complete hour even at speed 20)
						
					} // #6.B. Else, if the reference time is UTC
					else {
						// Get and adapt the time shift
						Integer timeShift = 0;
						try {
							timeShift = Integer.parseInt(refTimeSrc.replace("UTC+","").replace("UTC-","-"));
						} catch (IllegalArgumentException nfe) {
							MsgHandler.errorMsg(utcFormatMsg); // Console error msg
						}
						if (timeShift > 18) timeShift = 18;
						if (timeShift < -18) timeShift = -18;
						// Get the UTC time with shift
						Date now = new Date();
						LocalDateTime refDatetime = LocalDateTime.ofInstant(now.toInstant(),ZoneOffset.ofHours(timeShift));
						currentYear = Integer.parseInt(refDatetime.format(DateTimeFormatter.ofPattern("yyyy")));
						currentMonth = Integer.parseInt(refDatetime.format(DateTimeFormatter.ofPattern("MM")));
						currentMDay = Integer.parseInt(refDatetime.format(DateTimeFormatter.ofPattern("dd")));
						currentWDay = refDatetime.getDayOfWeek().getValue() + 1;
						if (currentWDay == 8) currentWDay = 1;
						currentHour = Integer.parseInt(refDatetime.format(DateTimeFormatter.ofPattern("HH")));
						currentMin = Integer.parseInt(refDatetime.format(DateTimeFormatter.ofPattern("mm")));
					}
					// #6.C. Also set a LocalDateTime
					LocalDateTime currentDateTime = LocalDateTime.of(currentYear, currentMonth, currentMDay, currentHour, currentMin);

					// #7. Get the edge date and time
					// Apply the active delay length to a LocalDateTime
					LocalDateTime edgeDateTime = expectedDateTime.plusMinutes(minutesBeforeEnd);
					// Set all the Integer variables
					Integer edgeYear = Integer.parseInt(edgeDateTime.format(DateTimeFormatter.ofPattern("yyyy")));
					Integer edgeMonth = Integer.parseInt(edgeDateTime.format(DateTimeFormatter.ofPattern("MM")));
					Integer edgeMDay = Integer.parseInt(edgeDateTime.format(DateTimeFormatter.ofPattern("dd")));
					Integer edgeWDay = edgeDateTime.getDayOfWeek().getValue() + 1;
					if (edgeWDay == 8) edgeWDay = 1;
					Integer edgeHour = Integer.parseInt(edgeDateTime.format(DateTimeFormatter.ofPattern("HH")));
					Integer edgeMin = Integer.parseInt(edgeDateTime.format(DateTimeFormatter.ofPattern("mm")));
					Boolean yearUp = false;
					Boolean monthUp = false;
					Boolean dayUp = false;
					Boolean hourUp = false;
					if (edgeMonth == 1) yearUp = true;
					if (edgeMDay == 1) monthUp = true;
					if (edgeHour == 0) dayUp = true;
					if (edgeMin == (minutesBeforeEnd + expectedMin - 60)) hourUp = true;

					// #8. Send dev messages
					if (devMode) {
						MsgHandler.devMsg("Command #" + key);
						if (!refTimeSrc.contains("UTC") && !refTimeSrc.equalsIgnoreCase("")) {
							MsgHandler.devMsg("Current MC Date and Time :  §e" + currentDateTime);
						} else {
							MsgHandler.devMsg("Current UTC Date and Time : §e" + currentDateTime);
						}
						MsgHandler.devMsg("Expected Date and Time :    §e" + expectedDateTime);
						MsgHandler.devMsg("Edge Date and Time :        §e" + edgeDateTime);
						MsgHandler.devMsg("Interval : " + minutesBeforeEnd + " minutes (#" + ticksBeforeEnd + ")");
						MsgHandler.devMsg("Reference Time Source : " + refTimeSrc);
						MsgHandler.devMsg("Repeat Frequence : " + repeatFreq);
						MsgHandler.devMsg("==================");
					}
					// #9. Check if we are into the delay and if schedule is not already active and try to launch it
					Boolean launchCmds = false;
					if (!commandsSchedulerIsActive.contains(key)) {
						switch (repeatFreq) {
						case ARG_NONE : // If there is no repetition, the whole date and hour must match
							MsgHandler.devMsg("Years will now be checked :");
							if ((yearUp && currentMonth >= expectedMonth && currentYear.equals(expectedYear))
									|| (yearUp && currentMonth < expectedMonth && currentYear.equals(edgeYear))
									|| (!yearUp && currentYear.equals(expectedYear))) {
								MsgHandler.devMsg("The data of the year corresponds, let's look further.");
							} else {
								MsgHandler.devMsg("The data of the year does not correspond, do nothing.");
								break;
							}
						case ARG_YEAR : // If there is a yearly repetition, the year is ignored
							MsgHandler.devMsg("Months will now be checked :");
							if ((monthUp && currentMDay >= expectedMDay && currentMonth.equals(expectedMonth))
									|| (monthUp && currentMDay < expectedMDay && currentMonth.equals(edgeMonth))
									|| (!monthUp && currentMonth.equals(expectedMonth))) {
								MsgHandler.devMsg("The data of the month corresponds, let's look further.");
							} else {
								MsgHandler.devMsg("The data of the month does not correspond, do nothing.");
								break;
							}
						case ARG_MONTH : // If there is a monthly or weekly repetition, year and month/week are ignored
						case ARG_WEEK :
							if (repeatFreq.equals(ARG_WEEK)) { // -> Do this only for a weekly repetition
								MsgHandler.devMsg("Day number in the week will now be checked :");
								if ((dayUp && currentHour >= expectedHour && currentWDay.equals(expectedWDay))
										|| (dayUp && currentHour < edgeHour && currentWDay.equals(edgeWDay))
										|| (!dayUp && currentWDay.equals(expectedWDay))) {
									MsgHandler.devMsg("The data of the day number in the week corresponds, let's look further.");
								} else {
									MsgHandler.devMsg("The data of the day number in the week does not correspond, do nothing.");
									break;
								}
							} else { // -> Or do a monthly repetition in all other cases
								MsgHandler.devMsg("Day number in the month will now be checked :");
								if ((dayUp && currentHour >= expectedHour && currentMDay.equals(expectedMDay))
										|| (dayUp && currentHour < edgeHour && currentMDay.equals(edgeMDay))
										|| (!dayUp && currentMDay.equals(expectedMDay))) {
									MsgHandler.devMsg("The data of the day number in the month corresponds, let's look further.");
								} else {
									MsgHandler.devMsg("The data of the day number in the month does not correspond, do nothing.");
									break;
								}
							}
						case ARG_DAY : // If there is a daily repetition, year, month/week and day are ignored
							MsgHandler.devMsg("Hours will now be checked :");
							if ((hourUp && currentMin >= expectedMin && currentHour.equals(expectedHour))
									|| (hourUp && currentMin < edgeMin && currentHour.equals(edgeHour))
									|| (!hourUp && currentHour.equals(expectedHour))) {
								MsgHandler.devMsg("§bThe data of the hours corresponds, let's look further.");
							} else {
								MsgHandler.devMsg("The data of the hours does not correspond, do nothing.");
								break;
							}
						case ARG_HOUR : // If there is a hourly repetition, year, month/week, day and hour are ignored
							if (!refTimeSrc.contains("UTC") && !refTimeSrc.equalsIgnoreCase("")) {
								if ((hourUp && (currentMin >= expectedMin || currentMin <= edgeMin))
										|| (!hourUp && currentMin >= expectedMin && currentMin <= edgeMin)) {
									MsgHandler.devMsg("§bThe data of the minutes corresponds, let's launch commands.");
									launchCmds = true;
								} else {
									MsgHandler.devMsg("The data of the minutes does not correspond, do nothing.");
									break;
								}
							} else {
								if (currentMin == expectedMin) { // Les minutes actuelles doivent être égales à celles attendues.
									MsgHandler.devMsg("§bThe data of the minutes corresponds, let's launch commands.");
									launchCmds = true;
								} else {
									MsgHandler.devMsg("The data of the minutes does not correspond, do nothing.");
									break;
								}
							}
						}
					}
					if (launchCmds) {
						// #10. Declare the key as having an active scheduler
						if (!commandsSchedulerIsActive.contains(key)) commandsSchedulerIsActive.add(key);
						MsgHandler.devMsg("Added the key " + key + " at the scheduler list : " + commandsSchedulerIsActive);
						MsgHandler.devMsg("=================="); 
						// After the delay, delete the key from the active scheduler list
						delayedDeleteKey(ticksBeforeEnd, key);
						MsgHandler.devMsg("Prepared to remove the key " + key + " from the scheduler list : " + commandsSchedulerIsActive);
						MsgHandler.devMsg("==================");

						// #11. Execute the command(s)
						Long delay = 0L;
						String lang = MainTM.getInstance().langConf.getString(LG_DEFAULTLANG);
						// #11.A. Search for each listed command
						for (String commandNb : MainTM.getInstance().cmdsConf.getConfigurationSection(CMDS_COMMANDSLIST + "." + key + "." + CMDS_CMDS).getKeys(false)) {
							MsgHandler.devMsg("CommandNb : " + commandNb);
							String command = MainTM.getInstance().cmdsConf.getString(CMDS_COMMANDSLIST + "." + key + "." + CMDS_CMDS + "." + commandNb);
							MsgHandler.devMsg("Command : " + command);
							if (command.charAt(0) == '/') command = command.replaceFirst("/","");
							command = command.replace("&","§");
							String world = MainTM.getInstance().cmdsConf.getString(CMDS_COMMANDSLIST + "." + key + "." + CMDS_PHREFWOLRD);
							// #11.B. Replace placeholders
							if (command.contains("{" + PH_PREFIX)) {
								String[] phSlipt1 = command.split("\\{");
								for (String ph1 : phSlipt1) {
									if (ph1.contains("}")) {
										String[] phSlipt2 = ph1.split("\\}");
										for (String ph2 : phSlipt2) {
											if (ph2.contains(PH_PREFIX)) {
												String ph3 = PlaceholdersHandler.replacePlaceholder("{" + ph2 + "}", world, lang, null);
												MsgHandler.devMsg("A placeholder was detected : §e" + "{" + ph2 + "}" + "§9 will be changed by \"§e" + ph3 + "§9\".");
												String ph = "{" + ph2 + "}";
												command = command.replace(ph, ph3);
											}
										}
									}
								}
							}
							// #11.C Replace hexadecimal colors by ChatColors
							command = ValuesConverter.replaceAllHexColors(command);
							// #11.D. Check if a waiting time is asked
							if (command.contains("wait ") || command.contains("pause ") ) {							
								String[] pauseSlipt = command.split(" ");
								if (pauseSlipt.length >= 1) {
									MsgHandler.devMsg("A waiting time is asked for §e" + pauseSlipt[1] + "§9 seconds"); // Console dev msg
									try {
										Long p = Long.parseLong(pauseSlipt[1]) * 20;
										delay = delay + p;
									} catch (NumberFormatException nfe) {
										MsgHandler.errorMsg(waitBeforeCmdMsg); // Console error msg
									}
								}
							// #11.E. Dispatch the command	
							} else {
								delayedCmdDispatch(delay, command);
							}
						}
					}
				}
			}
		}, 0L, refreshRate);
	}

	/**
	 * Dispatch a single command, eventually with a cumulative delay
	 */
	private static void delayedCmdDispatch(Long delay, String command) {
		BukkitScheduler delayedCmdDispatch = MainTM.getInstance().getServer().getScheduler();
		delayedCmdDispatch.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
				MsgHandler.devMsg("The command '" + command + "§9' will now be dispatched."); // Console dev msg
				Bukkit.dispatchCommand(console, command);
			}
		}, delay);
	}

	/**
	 * Cancel the active command scheduler
	 */
	private static void stopCmdsScheduler() {
		Bukkit.getScheduler().cancelTask(MainTM.cmdsTask);
	}

	/**
	 * Delete the key from the active scheduler list
	 */
	private static void delayedDeleteKey(Long endDelay, String key) {
		BukkitScheduler delayedDeleteKey = MainTM.getInstance().getServer().getScheduler();
		delayedDeleteKey.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (commandsSchedulerIsActive.contains(key)) commandsSchedulerIsActive.remove(key);
				MsgHandler.devMsg("Removed the key " + key + " from the scheduler list : " + commandsSchedulerIsActive);
				MsgHandler.devMsg("==================");
			}
		}, endDelay);
	}
	
};