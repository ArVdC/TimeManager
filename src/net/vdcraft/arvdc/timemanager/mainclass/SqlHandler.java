package net.vdcraft.arvdc.timemanager.mainclass;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;

import com.mysql.jdbc.PreparedStatement;

import net.vdcraft.arvdc.timemanager.MainTM;

public class SqlHandler extends MainTM {

	/** 
	 *  Manage SQL keys in config.yml
	 */	
	public static void initSqlDatas() {
    	if(MainTM.getInstance().getConfig().getKeys(false).contains("initialTick")) {
        	if(MainTM.getInstance().getConfig().getConfigurationSection("initialTick").getKeys(false).contains("useMySql")) {
			    if(MainTM.getInstance().getConfig().getString("initialTick.useMySql").equals("")) {
			    	MainTM.getInstance().getConfig().set("initialTick.useMySql", "false");
			    }
        	}
    	}
    	if(MainTM.getInstance().getConfig().getKeys(false).contains("mySql")) {
	    	if(MainTM.getInstance().getConfig().getConfigurationSection("mySql").getKeys(false).contains("host")) {
			    if(MainTM.getInstance().getConfig().getString("mySql.host").equals("")) {
			    	MainTM.getInstance().getConfig().set("mySql.host", "localhost");
			    }
	    	}		
	    	if(MainTM.getInstance().getConfig().getConfigurationSection("mySql").getKeys(false).contains("port")) {
			    if(MainTM.getInstance().getConfig().getString("mySql.port").equals("")) {
			    	MainTM.getInstance().getConfig().set("mySql.port", "3306");
			    }
	    	}
	    	if(MainTM.getInstance().getConfig().getConfigurationSection("mySql").getKeys(false).contains("ssl")) {
			    if(MainTM.getInstance().getConfig().getString("mySql.ssl").equals("")) {
			    	MainTM.getInstance().getConfig().set("mySql.ssl", "false");
			    }
	    	}
	    	if(MainTM.getInstance().getConfig().getConfigurationSection("mySql").getKeys(false).contains("database")) {
			    if(MainTM.getInstance().getConfig().getString("mySql.database").equals("")) {
			    	MainTM.getInstance().getConfig().set("mySql.database", "timemanager");
			    }
	    	}
	    	if(MainTM.getInstance().getConfig().getConfigurationSection("mySql").getKeys(false).contains("table")) {
			    if(MainTM.getInstance().getConfig().getString("mySql.table").equals("")) {
			    	MainTM.getInstance().getConfig().set("mySql.table", "refTick");
			    }
	    	}
    	}
    	host = MainTM.getInstance().getConfig().getString("mySql.host");
    	port = MainTM.getInstance().getConfig().getString("mySql.port");
    	ssl = MainTM.getInstance().getConfig().getString("mySql.ssl");
    	dbPrefix = MainTM.getInstance().getConfig().getString("mySql.dbPrefix").replace("_", "");
    	if(!dbPrefix.equals("")) {
    		dbPrefix = dbPrefix + "_";
    	}
    	database = dbPrefix + MainTM.getInstance().getConfig().getString("mySql.database").replace("_", "");
    	database = database.replace(".", "");
    	database = database.replace(" ", "");
    	tableName = MainTM.getInstance().getConfig().getString("mySql.table");
    	username = MainTM.getInstance().getConfig().getString("mySql.username");
    	password = MainTM.getInstance().getConfig().getString("mySql.password");
	};
	
	/** 
	 *  Try to open SQL connection, if not possible, change and use the tick value from config.yml
	 */	
	public synchronized static boolean openTheConnectionIfPossible() {
		if(connectionToHostIsAvailable() == true) { // Try to join the host, if the connection to host is possible, keep it open
			if(connectionToDatabaseIsAvailable() == false) { // Try to connect the database, create it if missing
				createNewDatabase();
			}
	    	if(connectionToTableIsAvailable() == false) { // Try to connect the table, create it if missing
	    		createNewTable();
	    	}
	        closeConnection("Host"); // Close the "Host" connection, but keep the "DB" connection opened
			return true;
		} else {
			MainTM.getInstance().getConfig().set("initialTick.useMySql", "false"); // Force to stop use SQL
			return false;
		}
	};
	
	/** 
	 *  Check for connection
	 */	
	public synchronized static boolean connectionToHostIsAvailable() {
		String isSslOn = " without";
		if(ssl.equalsIgnoreCase("true")) {
			isSslOn = "";
		}
	    try {
	    	connectionHost = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "?user=" + username + "&password=" + password + "&useSSL=" + ssl);
	        Bukkit.getLogger().info(prefixTM + " The mySQL host \"" + host + "\" " + connectionOkMsg + port + isSslOn + " using ssl."); // Console log msg
	        return true;
	    } catch(Exception e) {
            e.printStackTrace();
	        Bukkit.getLogger().severe(prefixTM + " " + connectionFailMsg + " \"" + host + "\". " + checkConfigMsg); // Console error msg
	        closeConnection("Host");
	        return false;
	    }
	};
	
	/** 
	 *  Check for existing database
	 */	
	public synchronized static boolean connectionToDatabaseIsAvailable() {
	    try {
	    	connectionDB = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?user=" + username + "&password=" + password + "&useSSL=" + ssl);
	        //Bukkit.getLogger().info(prefixTM + " " + "The database \"" + database + "\" already exists."); // Console debug msg
	        return true;
	    } catch(Exception e) {
	        //Bukkit.getLogger().info(prefixTM + " " + "The database \"" + database + "\" doesn't exist yet."); // Console debug msg
	        return false;
	    }
	};

	/** 
	 *  Create the database
	 */	
	public synchronized static void createNewDatabase() {
		String createDB = "CREATE DATABASE IF NOT EXISTS " + database + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
	    try {
	    	Statement sqlTable = connectionHost.createStatement();
	    	sqlTable.executeUpdate(createDB);
	        //Bukkit.getLogger().info(prefixTM + " " + "The database \"" + database + "\" was created."); // Console debug msg
	    } catch(Exception e) {
            e.printStackTrace();
	        Bukkit.getLogger().severe(prefixTM + " " + dbCreationFailMsg + " " + checkConfigMsg); // Console error msg
	        closeConnection("Host");
		}
	};
	
	/** 
	 *  Check for existing table
	 */	
	public synchronized static boolean connectionToTableIsAvailable() {
		try {
			ResultSet checkForThisTable = connectionDB.getMetaData().getTables(null, null, tableName, null);
	        boolean cftt = checkForThisTable.next();
	        if(cftt == true) {
		        //Bukkit.getLogger().info(prefixTM + " The table \"" + tableName + "\" already exists."); // Console debug msg
		        return true;
	        } else {
	    		//Bukkit.getLogger().info(prefixTM + " The table \"" + tableName + "\" doesn't exist yet."); // Console debug msg
		        return false;
	        }
		} catch (SQLException e) {
    		//Bukkit.getLogger().info(prefixTM + " Any table doesn't exist yet."); // Console debug msg
	        return false;
		}
	};
	
	/** 
	 *  Create a new table
	 */	
	public synchronized static void createNewTable() {
		String createTable = "CREATE TABLE IF NOT EXISTS " + tableName + "(id INT(1), initialTickNb VARCHAR(7), PRIMARY KEY(id))";
	    try {
	    	PreparedStatement sqlTable = (PreparedStatement) connectionDB.prepareStatement(createTable);
	    	sqlTable.executeUpdate();
	        //Bukkit.getLogger().info(prefixTM + " " + "The table \"" + tableName + "\" was correctly created in database " + database + "."); // Console debug msg
	    } catch(Exception e) {
            e.printStackTrace();
	        Bukkit.getLogger().severe(prefixTM + " " + tableCreationFailMsg + " " + checkConfigMsg); // Console error msg
	        closeConnection("DB");
	    }
	};
    
    /** 
	 *  Write a new tick value
	 */	
	public synchronized static void setServerTickSQL(Long tick) {
	    try {
	    	PreparedStatement sqlTick = (PreparedStatement) connectionDB.prepareStatement("INSERT INTO " + tableName + " (id, initialTickNb) VALUES (?,?)");
	    	sqlTick.setInt(1, 1);
	    	sqlTick.setLong(2, tick);
	    	sqlTick.executeUpdate();
	        //Bukkit.getLogger().info(prefixTM + " " + "The reference initial tick was created in mySQL database."); // Console debug msg
	    } catch(Exception e) {
            e.printStackTrace();
	        Bukkit.getLogger().severe(prefixTM + " " + datasCreationFailMsg + " " + checkConfigMsg); // Console error msg
	        closeConnection("DB");
	    }
	};

    /** 
	 *  Update a tick value
	 */	
	public synchronized static void updateServerTickSQL(Long tick) {
	    try {
	    	connectionDB.createStatement().executeUpdate("UPDATE " + tableName + " SET initialTickNb=" + tick + " WHERE id=1");
	        //Bukkit.getLogger().info(prefixTM + " " + "The reference initial tick was updated in mySQL database."); // Console debug msg
	    } catch(Exception e) {
	        e.printStackTrace();
	        Bukkit.getLogger().severe(prefixTM + " " + datasOverridingFailMsg + " " + checkConfigMsg); // Console error msg
	        closeConnection("DB");
	    }
	};    
	    
	/** 
	 *  Get the tick value
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
	    		// Bukkit.getLogger().info(prefixTM + " The initialTickNb value is empty, creating a new one."); // Console debug msg
	    	}
	    	return tickIs;
	    } catch(Exception e) {
    		Bukkit.getLogger().info(prefixTM + " " + tableReachFailMsg + " " + checkConfigMsg); // Console error msg
	    	return null;
	    }
	};

	/** 
	 *  Close a connection
	 */	
	public static void closeConnection(String hostOrDB) {
		try {     
	        if(hostOrDB.equalsIgnoreCase("Host") && connectionHost != null && !connectionHost.isClosed()) {
	        	connectionHost.close();
	        	//Bukkit.getLogger().info(prefixTM + " " + "SQL connection with Host is closed."); // Console debug msg
			}     
	        if(hostOrDB.equalsIgnoreCase("DB") && connectionDB != null && !connectionDB.isClosed()) {
	        	connectionDB.close();
	        	//Bukkit.getLogger().info(prefixTM + " " + "SQL connection with DB is closed."); // Console debug msg
			}
	    } catch (SQLException e) {
	        e.printStackTrace();
	        Bukkit.getLogger().severe(prefixTM + " " + disconnectionFailMsg + " " + checkConfigMsg); // Console error msg
	    }	
	};

}