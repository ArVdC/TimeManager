package net.vdcraft.arvdc.timemanager.placeholders;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdplayer.PlayerLangHandler;
//import net.vdcraft.arvdc.timemanager.placeholders.PlaceholdersHandler;
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
        return MainTM.PH_IDENTIFIER;
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

        String world = player.getWorld().getName();
        String lang = PlayerLangHandler.setLangToUse((CommandSender) player);
        
        // %tm_player%
        if(identifier.equals(MainTM.PH_PLAYER)){
			return player.getName();
        }

        // %tm_world%
        if(identifier.equals(MainTM.PH_WORLD)){
			return world;
        }

        // %tm_tick%
        if(identifier.equals(MainTM.PH_TICK)){
			return PlaceholdersHandler.replacePlaceholder("{" + MainTM.PH_PREFIX + identifier + "}", world, lang, player);
        }

        // %tm_time12%
        if(identifier.equals(MainTM.PH_TIME12)){
			return PlaceholdersHandler.replacePlaceholder("{" + MainTM.PH_PREFIX + identifier + "}", world, lang, player);
        }

        // %tm_time24%
        if(identifier.equals(MainTM.PH_TIME24)){
			return PlaceholdersHandler.replacePlaceholder("{" + MainTM.PH_PREFIX + identifier + "}", world, lang, player);
        }

        // %tm_hours12%
        if(identifier.equals(MainTM.PH_HOURS12)){
			return PlaceholdersHandler.replacePlaceholder("{" + MainTM.PH_PREFIX + identifier + "}", world, lang, player);
        }

        // %tm_hours24%
        if(identifier.equals(MainTM.PH_HOURS24)){
			return PlaceholdersHandler.replacePlaceholder("{" + MainTM.PH_PREFIX + identifier + "}", world, lang, player);
        }

        // %tm_minutes%
        if(identifier.equals(MainTM.PH_MINUTES)){
			return PlaceholdersHandler.replacePlaceholder("{" + MainTM.PH_PREFIX + identifier + "}", world, lang, player);
        }

        // %tm_seconds%
        if(identifier.equals(MainTM.PH_SECONDS)){
			return PlaceholdersHandler.replacePlaceholder("{" + MainTM.PH_PREFIX + identifier + "}", world, lang, player);
        }

        // %tm_ampm%
        if(identifier.equals(MainTM.PH_AMPM)){
			return PlaceholdersHandler.replacePlaceholder("{" + MainTM.PH_PREFIX + identifier + "}", world, lang, player);
        }

        // %tm_dayPart%
        if(identifier.equals(MainTM.PH_DAYPART)){
			return PlaceholdersHandler.replacePlaceholder("{" + MainTM.PH_PREFIX + identifier + "}", world, lang, player);
        }

        // %tm_currentday%
        if(identifier.equals(MainTM.PH_C_DAY)){
			return PlaceholdersHandler.replacePlaceholder("{" + MainTM.PH_PREFIX + identifier + "}", world, lang, player);
        }

        // %tm_elapseddays%
        if(identifier.equals(MainTM.PH_E_DAYS)){
			return PlaceholdersHandler.replacePlaceholder("{" + MainTM.PH_PREFIX + identifier + "}", world, lang, player);
        }

     // %tm_weekdaynb%
        if(identifier.equals(MainTM.PH_WEEKDAY)){
			return PlaceholdersHandler.replacePlaceholder("{" + MainTM.PH_PREFIX + identifier + "}", world, lang, player);
        }        
        
        // %tm_yearweek%
        if(identifier.equals(MainTM.PH_YEARWEEK)){
			return PlaceholdersHandler.replacePlaceholder("{" + MainTM.PH_PREFIX + identifier + "}", world, lang, player);
        }

        // %tm_week%
        if(identifier.equals(MainTM.PH_WEEK)){
			return PlaceholdersHandler.replacePlaceholder("{" + MainTM.PH_PREFIX + identifier + "}", world, lang, player);
        }

        // %tm_monthname%
        if(identifier.equals(MainTM.PH_MONTHNAME)){
			return PlaceholdersHandler.replacePlaceholder("{" + MainTM.PH_PREFIX + identifier + "}", world, lang, player);
        }

        // %tm_dd%
        if(identifier.equals(MainTM.PH_DD)){
			return PlaceholdersHandler.replacePlaceholder("{" + MainTM.PH_PREFIX + identifier + "}", world, lang, player);
        }

        // %tm_mm%
        if(identifier.equals(MainTM.PH_MM)){
			return PlaceholdersHandler.replacePlaceholder("{" + MainTM.PH_PREFIX + identifier + "}", world, lang, player);
        }

        // %tm_yy%
        if(identifier.equals(MainTM.PH_YY)){
			return PlaceholdersHandler.replacePlaceholder("{" + MainTM.PH_PREFIX + identifier + "}", world, lang, player);
        }

        // %tm_yyyy%
        if(identifier.equals(MainTM.PH_YYYY)){
			return PlaceholdersHandler.replacePlaceholder("{" + MainTM.PH_PREFIX + identifier + "}", world, lang, player);
        }
 
        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%) was provided
        return null;
    }
}