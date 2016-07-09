/*
 * Created on Jun 26, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package configuration;

/**
 * @author Sorin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Configuration {
	//	 the name of the DB used
	private String dbname;
	private String dbtype;
	private String serverName;
	
	public Configuration (String db, String type, String server) {
		dbname = db;
		dbtype = type;
		serverName = server;
	}
	
	/**
	 * @return Returns the dbname.
	 */
	public String getDbname() {
		return dbname;
	}
	/**
	 * @param dbname The dbname to set.
	 */
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	/**
	 * @return Returns the serverName.
	 */
	public String getServerName() {
		return serverName;
	}
	/**
	 * @param serverName The serverName to set.
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	/**
	 * @return Returns the dbtype.
	 */
	public String getDbtype() {
		return dbtype;
	}
	/**
	 * @param dbtype The dbtype to set.
	 */
	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}
	
	public String toString() {
		return dbname + " " + dbtype + " " + serverName;
	}
}
