package net.vdcraft.arvdc.timemanager.mainclass;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;

import java.sql.PreparedStatement;

import net.vdcraft.arvdc.timemanager.MainTM;

public class SqlHandler extends MainTM {

    /**
     * Manage SQL keys in config.yml
     */
    public static void initSqlDatas() {
	if (MainTM.getInstance().getConfig().getKeys(false).contains(CF_INITIALTICK)) {
	    if (MainTM.getInstance().getConfig().getConfigurationSection(CF_INITIALTICK).getKeys(false).contains(CF_USEMYSQL)) {
		if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equals("")) {
		    MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_USEMYSQL, "false");
		}
	    }
	} else {
	    MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_USEMYSQL, "false");
	}
	if (MainTM.getInstance().getConfig().getKeys(false).contains(CF_MYSQL)) {
	    if (MainTM.getInstance().getConfig().getConfigurationSection(CF_MYSQL).getKeys(false).contains(CF_HOST)) {
		if (MainTM.getInstance().getConfig().getString(CF_MYSQL + "." + CF_HOST).equals("")) {
		    MainTM.getInstance().getConfig().set(CF_MYSQL + "." + CF_HOST, "localhost");
		}
	    }
	    if (MainTM.getInstance().getConfig().getConfigurationSection(CF_MYSQL).getKeys(false).contains(CF_PORT)) {
		if (MainTM.getInstance().getConfig().getString(CF_MYSQL + "." + CF_PORT).equals("")) {
		    MainTM.getInstance().getConfig().set(CF_MYSQL + "." + CF_PORT, "3306");
		}
	    }
	    if (MainTM.getInstance().getConfig().getConfigurationSection(CF_MYSQL).getKeys(false).contains("ssl")) {
		if (MainTM.getInstance().getConfig().getString("mySql.ssl").equals("")) {
		    MainTM.getInstance().getConfig().set(CF_MYSQL + "." + CF_SSL, "false");
		}
	    }
	    if (MainTM.getInstance().getConfig().getConfigurationSection(CF_MYSQL).getKeys(false).contains("database")) {
		if (MainTM.getInstance().getConfig().getString("mySql.database").equals("")) {
		    MainTM.getInstance().getConfig().set(CF_MYSQL + "." + CF_DATABASE, "timemanager");
		}
	    }
	    if (MainTM.getInstance().getConfig().getConfigurationSection(CF_MYSQL).getKeys(false).contains("table")) {
		if (MainTM.getInstance().getConfig().getString("mySql.table").equals("")) {
		    MainTM.getInstance().getConfig().set(CF_MYSQL + "." + CF_TABLE, "refTick");
		}
	    }
	    if (!MainTM.getInstance().getConfig().getConfigurationSection(CF_MYSQL).getKeys(false).contains("username")) {
		MainTM.getInstance().getConfig().set(CF_MYSQL + "." + CF_USERNAME, "user");
	    }
	    if (!MainTM.getInstance().getConfig().getConfigurationSection(CF_MYSQL).getKeys(false).contains("password")) {
		MainTM.getInstance().getConfig().set(CF_MYSQL + "." + CF_PASSWORD, "***");
	    }
	} else {
	    MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_USEMYSQL, "false");
	    MainTM.getInstance().getConfig().set(CF_MYSQL + "." + CF_HOST, "localhost");
	    MainTM.getInstance().getConfig().set(CF_MYSQL + "." + CF_PORT, "3306");
	    MainTM.getInstance().getConfig().set(CF_MYSQL + "." + CF_SSL, "false");
	    MainTM.getInstance().getConfig().set(CF_MYSQL + "." + CF_DATABASE, "timemanager");
	    MainTM.getInstance().getConfig().set(CF_MYSQL + "." + CF_TABLE, "refTick");
	    MainTM.getInstance().getConfig().set(CF_MYSQL + "." + CF_USERNAME, "user");
	    MainTM.getInstance().getConfig().set(CF_MYSQL + "." + CF_PASSWORD, "***");
	}
	host = MainTM.getInstance().getConfig().getString(CF_MYSQL + "." + CF_HOST);
	port = MainTM.getInstance().getConfig().getString(CF_MYSQL + "." + CF_PORT);
	ssl = MainTM.getInstance().getConfig().getString(CF_MYSQL + "." + CF_SSL);
	tableName = MainTM.getInstance().getConfig().getString(CF_MYSQL + "." + CF_TABLE);
	username = MainTM.getInstance().getConfig().getString(CF_MYSQL + "." + CF_USERNAME);
	password = MainTM.getInstance().getConfig().getString(CF_MYSQL + "." + CF_PASSWORD);
	dbPrefix = MainTM.getInstance().getConfig().getString(CF_MYSQL + "." + CF_DBPREFIX).replace("_", "");
	if (!dbPrefix.equals("")) {
	    dbPrefix = dbPrefix + "_";
	}
	database = dbPrefix + MainTM.getInstance().getConfig().getString(CF_MYSQL + "." + CF_DATABASE)
		.replace("_", "")
		.replace(".", "")
		.replace(" ", "");
	tableName = MainTM.getInstance().getConfig().getString(CF_MYSQL + "." + CF_TABLE);
	username = MainTM.getInstance().getConfig().getString(CF_MYSQL + "." + CF_USERNAME);
	password = MainTM.getInstance().getConfig().getString(CF_MYSQL + "." + CF_PASSWORD);
    }

    /**
     * Try to open SQL connection, if not possible, change and use the tick value
     * from config.yml
     */
    public synchronized static Boolean openTheConnectionIfPossible(Boolean msgOnOff) {
	if (connectionToHostIsAvailable(msgOnOff) == true) { // Try to join the host, if the connection to host is possible, keep it open
	    if (connectionToDatabaseIsAvailable() == false) { // Try to connect the database, create it if missing
		createNewDatabase();
	    }
	    if (connectionToTableIsAvailable() == false) { // Try to connect the table, create it if missing
		createNewTable();
	    }
	    closeConnection("Host"); // Close the "Host" connection, but keep the "DB" connection opened
	    return true;
	} else {
	    MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_USEMYSQL, "false"); // Force to stop use SQL
	    return false;
	}
    }

    /**
     * Check for connection
     */
    public synchronized static boolean connectionToHostIsAvailable(Boolean msgOnOff) {
	String isSslOn = " without";
	if (ssl.equalsIgnoreCase("true")) {
	    isSslOn = "";
	}
	try {
	    connectionHost = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "?user=" + username + "&password=" + password + "&useSSL=" + ssl);
	    if (msgOnOff.equals(true))
		Bukkit.getLogger().info(prefixTM + " The mySQL host \"" + host + "\" " + connectionOkMsg + " #" + port + isSslOn + " using ssl."); // Console log msg
	    return true;
	} catch (Exception e) {
	    if (debugMode == true) {
		Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Connection opening failure details:");
		e.printStackTrace();
	    }
	    Bukkit.getLogger().severe(prefixTM + " " + connectionFailMsg + " \"" + host + "\". " + checkConfigMsg); // Console error msg
	    closeConnection("Host");
	    return false;
	}
    }

    /**
     * Check for existing database
     */
    public synchronized static boolean connectionToDatabaseIsAvailable() {
	try {
	    connectionDB = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?user=" + username + "&password=" + password + "&useSSL=" + ssl);
	    if (debugMode == true)
		Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " The database \"" + database + "\" already exists."); // Console debug msg
	    return true;
	} catch (Exception e) {
	    if (debugMode == true)
		Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " The database \"" + database + "\" doesn't exist yet."); // Console debug msg
	    return false;
	}
    }

    /**
     * Create the database
     */
    public synchronized static void createNewDatabase() {
	String createDB = "CREATE DATABASE IF NOT EXISTS " + database + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
	try {
	    Statement sqlTable = connectionHost.createStatement();
	    sqlTable.executeUpdate(createDB);
	    if (debugMode == true)
		Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " The database \"" + database + "\" was created."); // Console debug msg
	} catch (Exception e) {
	    if (debugMode == true) {
		Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " New database creating failure details:");
		e.printStackTrace();
	    }
	    Bukkit.getLogger().severe(prefixTM + " " + dbCreationFailMsg + " " + checkConfigMsg); // Console error msg
	    closeConnection("Host");
	}
    }

    /**
     * Check for existing table
     */
    public synchronized static boolean connectionToTableIsAvailable() {
	try {
	    ResultSet checkForThisTable = connectionDB.getMetaData().getTables(null, null, tableName, null);
	    boolean cftt = checkForThisTable.next();
	    if (cftt == true) {
		if (debugMode == true)
		    Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " The table \"" + tableName + "\" already exists."); // Console debug msg
		return true;
	    } else {
		if (debugMode == true)
		    Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " The table \"" + tableName + "\" doesn't exist yet."); // Console debug msg
		return false;
	    }
	} catch (SQLException e) {
	    if (debugMode == true)
		Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Any table doesn't exist yet."); // Console debug msg
	    return false;
	}
    }

    /**
     * Create a new table
     */
    public synchronized static void createNewTable() {
	String createTable = "CREATE TABLE IF NOT EXISTS " + tableName + "(id INT(1), initialTickNb VARCHAR(7), PRIMARY KEY(id))";
	try {
	    PreparedStatement sqlTable = (PreparedStatement) connectionDB.prepareStatement(createTable);
	    sqlTable.executeUpdate();
	    if (debugMode == true)
		Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " The table \"" + tableName + "\" was correctly created in database " + database + "."); // Console debug msg
	} catch (Exception e) {
	    if (debugMode == true) {
		Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " New table creating failure details:");
		e.printStackTrace();
	    }
	    if (debugMode == true)
		Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + tableCreationFailMsg + " " + checkConfigMsg); // Console error msg
	    closeConnection("DB");
	}
    }

    /**
     * Write a new tick value
     */
    public synchronized static void setServerTickSQL(Long tick) {
	try {
	    PreparedStatement sqlTick = (PreparedStatement) connectionDB.prepareStatement("INSERT INTO " + tableName + " (id, initialTickNb) VALUES (?,?)");
	    sqlTick.setInt(1, 1);
	    sqlTick.setLong(2, tick);
	    sqlTick.executeUpdate();
	    if (debugMode == true)
		Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + "The reference initial tick was created in mySQL database."); // Console debug msg
	} catch (Exception e) {
	    if (debugMode == true) {
		Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " New data writing failure details:");
		e.printStackTrace();
	    }
	    Bukkit.getLogger().severe(prefixTM + " " + datasCreationFailMsg + " " + checkConfigMsg); // Console error msg
	    closeConnection("DB");
	}
    }

    /**
     * Update the initialTickNb value in the MySQL database
     */
    public synchronized static void updateServerTickSQL(Long tick) {
	try {
	    connectionDB.createStatement().executeUpdate("UPDATE " + tableName + " SET initialTickNb=" + tick + " WHERE id=1");
	    if (debugMode == true)
		Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + "The reference initial tick was updated in mySQL database."); // Console debug msg
	} catch (Exception e) {
	    if (debugMode == true) {
		Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Data writing failure details:");
		e.printStackTrace();
	    }
	    Bukkit.getLogger().severe(prefixTM + " " + datasOverridingFailMsg + " " + checkConfigMsg); // Console error msg
	    closeConnection("DB");
	}
    }

    /**
     * Get the tick value
     */
    public synchronized static Long getServerTickSQL() {
	try {
	    PreparedStatement sqlTick = (PreparedStatement) connectionDB.prepareStatement("SELECT initialTickNb FROM " + tableName + " WHERE id=1");
	    ResultSet rs = sqlTick.executeQuery();
	    Long tickIs = null;
	    if (rs.next() == true) {
		tickIs = rs.getLong("initialTickNb");
	    }
	    if (tickIs == null) {
		if (debugMode == true)
		    Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " The initialTickNb value is empty, creating a new one."); // Console debug msg
	    }
	    return tickIs;
	} catch (Exception e) {
	    Bukkit.getLogger().info(prefixTM + " " + tableReachFailMsg + " " + checkConfigMsg); // Console error msg
	    return null;
	}
    }

    /**
     * Close a connection
     */
    public static void closeConnection(String hostOrDB) {
	try {
	    if (hostOrDB.equalsIgnoreCase("Host") && connectionHost != null && !connectionHost.isClosed()) {
		connectionHost.close();
		if (debugMode == true)
		    Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " The mySQL connection with the host is closed."); // Console debug msg
	    }
	    if (hostOrDB.equalsIgnoreCase("DB") && connectionDB != null && !connectionDB.isClosed()) {
		connectionDB.close();
		if (debugMode == true)
		    Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " The mySQL connection with the database is closed."); // Console debug msg
	    }
	} catch (SQLException e) {
	    if (debugMode == true) {
		Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Connection closing failure details:");
		e.printStackTrace();
	    }
	    Bukkit.getLogger().severe(prefixTM + " " + disconnectionFailMsg + " " + checkConfigMsg); // Console error msg
	}
    }

};