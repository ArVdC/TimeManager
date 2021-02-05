package net.vdcraft.arvdc.timemanager.placeholders;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdplayer.UserMsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class PAPIHandler extends PlaceholderExpansion {
	
	/**
	 * PlaceholderAPI (See www.spigotmc.org/resources/placeholderapi.6245 for credits)
	 */

    private MainTM plugin;

    /**
     * Since we register the expansion inside our own plugin, we
     * can simply use this method here to get an instance of our
     * plugin.
     *
     * @param plugin
     *        The instance of our plugin.
     */
    public PAPIHandler(MainTM plugin){
    	this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     * 
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest 
     * method to obtain a value if a placeholder starts with our 
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "tm";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    /**
     * This is the method called when a placeholder with our identifier 
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.Player Player}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null){
            return "";
        }

        // %tm_tick%
        if(identifier.equals("tick")){
			Long t = player.getWorld().getTime();
			String tick = t.toString();
			return tick;
        }

        // %tm_time%
        if(identifier.equals("time")){
			long t = player.getWorld().getTime();
			String time = ValuesConverter.formattedTimeFromTick(t);
			return time;
        }

        // %tm_dayPart%
        if(identifier.equals("dayPart")){
			long t = player.getWorld().getTime();
			String lg = UserMsgHandler.setLangToUse(player);
			String dayPart = ValuesConverter.getDayPart(t);
			return MainTM.getInstance().langConf.getString(MainTM.CF_LANGUAGES + "." + lg + "." + MainTM.CF_DAYPARTS + "." + dayPart);
        }

        // %tm_elapseddays%
        if(identifier.equals("elapseddays")){
			return ValuesConverter.elapsedDaysFromTick(player.getWorld().getFullTime()).toString();
        }

        // %tm_currentday%
        if(identifier.equals("currentday")){
			Long elapsedDay = ValuesConverter.elapsedDaysFromTick(player.getWorld().getFullTime());
			return (++elapsedDay).toString();
        }

        // %tm_yearweek%
        if(identifier.equals("yearweek")){
			Long week = ValuesConverter.yearWeekFromTick(player.getWorld().getFullTime());
			return week.toString() ;
        }

        // %tm_monthname%
        if(identifier.equals("monthname")){
			String lg = UserMsgHandler.setLangToUse(player);
			long elapsedDays = ValuesConverter.elapsedDaysFromTick(player.getWorld().getFullTime());
			String mm = "m" + ValuesConverter.dateFromElapsedDays(elapsedDays, "mm");
			return MainTM.getInstance().langConf.getString(MainTM.CF_LANGUAGES + "." + lg + "." + MainTM.CF_MONTHS + "." + mm);
        }

        // %tm_currentday%
        if(identifier.equals("currentday")){
			Long currentDay = ValuesConverter.elapsedDaysFromTick(player.getWorld().getFullTime());
			return (++currentDay).toString();
        }

        // %tm_dd%
        if(identifier.equals("dd")){
			long elapsedDays = ValuesConverter.elapsedDaysFromTick(player.getWorld().getFullTime());
			return ValuesConverter.dateFromElapsedDays(elapsedDays, "dd");
        }

        // %tm_mm%
        if(identifier.equals("mm")){
			long elapsedDays = ValuesConverter.elapsedDaysFromTick(player.getWorld().getFullTime());
			return ValuesConverter.dateFromElapsedDays(elapsedDays, "mm");
        }

        // %tm_yy%
        if(identifier.equals("yy")){
			long elapsedDays = ValuesConverter.elapsedDaysFromTick(player.getWorld().getFullTime());
			return ValuesConverter.dateFromElapsedDays(elapsedDays, "yy");
        }

        // %tm_yyyy%
        if(identifier.equals("yyyy")){
			long elapsedDays = ValuesConverter.elapsedDaysFromTick(player.getWorld().getFullTime());
			return ValuesConverter.dateFromElapsedDays(elapsedDays, "yyyy");
        }
 
        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%) was provided
        return null;
    }
}