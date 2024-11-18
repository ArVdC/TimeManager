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
			
		long refreshRate = 30L;
		
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
					Integer expectedDay = 1;
					long expectedWDay = 1;
					String[] ed = eDate.split("-");
					try { // The date is supposed to be in correct format (yyyy-mm-dd)
						expectedYear = Integer.parseInt(ed[0]);
						expectedMonth = Integer.parseInt(ed[1]);
						expectedDay = Integer.parseInt(ed[2]);
						expectedWDay = ValuesConverter.dayInWeek(ValuesConverter.tickFromFormattedDate(eDate));
					} catch (NumberFormatException nfe) {
						MsgHandler.errorMsg(dateFormatMsg); // Console error msg
					}
					// #5.B. Get the time 
					String eTime = MainTM.getInstance().cmdsConf.getString(CMDS_COMMANDSLIST + "." + key + "." + CMDS_TIME);
					Integer expectedHour = 0;
					Integer expectedMin = 0;
					String[] expectedTime = eTime.split(":");
					expectedHour = Integer.parseInt(expectedTime[0]);
					expectedMin = Integer.parseInt(expectedTime[1]);
					
					// #5.C. Also set a LocalDateTimeDateTime
					LocalDateTime expectedDateTime = LocalDateTime.of(expectedYear, expectedMonth, expectedDay, expectedHour, expectedMin);

					// #6. Get the current date and time
					Integer currentHour = null;
					Integer currentMin = null;
					Integer currentYear = null;
					Integer currentMonth = null;
					Integer currentDay = null;
					Integer currentWDay = null;
					// Set a default delay
					int minutesBeforeEnd = 1; // (=1min.)
					long ticksBeforeEnd = 1200L; // (=1min.)
					
					// #6.A. If the reference time is an MC world
					if (!refTimeSrc.contains("UTC") && !refTimeSrc.equalsIgnoreCase("")) {
						// Get the date
						Long currentFullTick = Bukkit.getWorld(refTimeSrc).getFullTime();
						Long cDate = ValuesConverter.daysFromTick(currentFullTick);
						currentYear = Integer.parseInt(ValuesConverter.dateFromElapsedDays(cDate, PH_YYYY));
						currentMonth = Integer.parseInt(ValuesConverter.dateFromElapsedDays(cDate, PH_MM));
						currentDay = Integer.parseInt(ValuesConverter.dateFromElapsedDays(cDate, PH_DD));
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
						if (speed > 0.0 && speed <= 0.5) {
							minutesBeforeEnd = 5;
							ticksBeforeEnd = (long) (84 / speed);
						} else if (speed > 0.5 && speed <= 1.0) {
							minutesBeforeEnd = 10;
							ticksBeforeEnd = (long) (167 / speed);
						} else if (speed > 1.0 && speed <= 5.0) {
							minutesBeforeEnd = 15;
							ticksBeforeEnd = (long) (250 / speed);
						} else if (speed > 5.0 && speed <= MainTM.speedMax) {
							minutesBeforeEnd = 20;
							ticksBeforeEnd = (long) (334 / speed);
						}
						
					} // #6.B. Else, the reference time is UTC
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
						currentDay = Integer.parseInt(refDatetime.format(DateTimeFormatter.ofPattern("dd")));
						currentHour = Integer.parseInt(refDatetime.format(DateTimeFormatter.ofPattern("HH")));
						currentMin = Integer.parseInt(refDatetime.format(DateTimeFormatter.ofPattern("mm")));
						// Get the current week day number
						currentWDay = refDatetime.getDayOfWeek().getValue() + 1;
						if (currentWDay == 8) currentWDay = 1;
					}
					// #6.C. Also set a LocalDateTime
					LocalDateTime currentDateTime = LocalDateTime.of(currentYear, currentMonth, currentDay, currentHour, currentMin);

					// #7. Get the edge date and time
					// Apply the active delay length to a LocalDateTime
					LocalDateTime edgeDateTime = expectedDateTime.plusMinutes(minutesBeforeEnd);
					// Set all the Integer variables
					Integer edgeYear = Integer.parseInt(edgeDateTime.format(DateTimeFormatter.ofPattern("yyyy")));
					Integer edgeMonth = Integer.parseInt(edgeDateTime.format(DateTimeFormatter.ofPattern("MM")));
					Integer edgeDay = Integer.parseInt(edgeDateTime.format(DateTimeFormatter.ofPattern("dd")));
					Integer edgeHour = Integer.parseInt(edgeDateTime.format(DateTimeFormatter.ofPattern("HH")));
					Integer edgeMin = Integer.parseInt(edgeDateTime.format(DateTimeFormatter.ofPattern("mm")));
					Boolean eraUp = false;
					Boolean yearUp = false;
					Boolean monthUp = false;
					Boolean dayUp = false;
					Boolean hourUp = false;
					if (edgeYear == 1) eraUp = true;
					if (edgeMonth == 1) yearUp = true;
					if (edgeDay == 1) monthUp = true;
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
							if ((eraUp && ((expectedYear == currentYear) && (expectedMonth <= currentMonth)) || ((currentYear == edgeYear) && (currentMonth <= edgeMonth))) // If there is a transition to the next era (only concern year edge)
									|| (expectedYear <= currentYear && currentYear <= edgeYear)) {
								MsgHandler.devMsg("The data of the years correspond, let's look further.");
							} else {
								MsgHandler.devMsg("The data of the year does not correspond, do nothing.");
								break;
							}
						case ARG_YEAR : // If there is a yearly repetition, the year is ignored
							MsgHandler.devMsg("Months will now be checked :");
							if ((yearUp && ((expectedMonth == currentMonth) && (expectedDay <= currentDay)) || ((currentMonth == edgeMonth) && (currentDay <= edgeDay))) // If there is a transition to the next year (only concern month edge)
									|| (expectedMonth <= currentMonth && currentMonth <= edgeMonth)) {
								MsgHandler.devMsg("The data of the month correspond, let's look further.");
							} else {
								MsgHandler.devMsg("The data of the month does not correspond, do nothing.");
								break;
							}
						case ARG_MONTH : // If there is a monthly repetition, year and month are ignored
						case ARG_WEEK : // If there is a weekly repetition, year and month are ignored
							if (repeatFreq.equals(ARG_WEEK)) { // Do this only for a weekly repetition
								MsgHandler.devMsg("Day number in the week will now be checked :");
								if (currentWDay == expectedWDay) {
									MsgHandler.devMsg("The data of the day number in the week correspond, let's look further.");
								} else {
									MsgHandler.devMsg("The data of the day number in the week does not correspond, do nothing.");
									break;
								}
							} else { // Normal process in all other cases
								MsgHandler.devMsg("Day will now be checked :");
								if ((monthUp && ((expectedDay == currentDay) && (expectedHour <= currentHour)) || ((currentDay == edgeDay) && (currentHour <= edgeHour))) // If there is a transition to the next month (only concern day edge)
										|| (expectedDay <= currentDay && currentDay <= edgeDay)) {
									MsgHandler.devMsg("The data of the day correspond, let's look further.");
								} else {
									MsgHandler.devMsg("The data of the day does not correspond, do nothing.");
									break;
								}
							}
						case ARG_DAY : // If there is a daily repetition, year, month and day are ignored
							MsgHandler.devMsg("Hours will now be checked :");
							if ((dayUp && ((expectedHour == currentHour) && (expectedMin <= currentMin)) || ((currentHour == edgeHour) && (currentMin <= edgeMin))) // If there is a transition to the next day (only concern hour edge)
									|| (expectedHour <= currentHour && expectedMin <= currentMin && currentHour <= edgeHour && currentMin <= edgeMin)) {
								MsgHandler.devMsg("The data of the hours correspond, let's look further.");
							} else {
								MsgHandler.devMsg("The data of the hour does not correspond, do nothing.");
								break;
							}
						case ARG_HOUR : // If there is a hourly repetition, year, month, day and hour are ignored (Only minutes must be less than the edge)
							MsgHandler.devMsg("Minutes will now be checked :");
							if ((hourUp && (expectedMin <= currentMin || currentMin < edgeMin)) // If there is a transition to the next hour (only concern minutes edge)
									|| (expectedMin <= currentMin && currentMin < edgeMin)) {
								MsgHandler.devMsg("The data of the minutes correspond, let's launch commands.");
								launchCmds = true;
							} else MsgHandler.devMsg("The data of the minutes does not correspond, do nothing.");
						}
					}
					if (launchCmds) {
						// #10. Declare the key as having an active scheduler
						commandsSchedulerIsActive.add(key);
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