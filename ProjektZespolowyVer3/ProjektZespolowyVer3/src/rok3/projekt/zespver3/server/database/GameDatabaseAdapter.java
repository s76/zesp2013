package rok3.projekt.zespver3.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import rok3.projekt.zespver3.client.Settings;
import rok3.projekt.zespver3.network.packet.__UserInfo;

/**
 * 
 * @author sheepw76
 * 
 */
public class GameDatabaseAdapter {
	Connection connection;
	private String tablename;
	private String url;
	
	public static void main(String[] args) {
		GameDatabaseAdapter db = new GameDatabaseAdapter(Settings._database_url,Settings._database_table);
		for ( String s : args ) db.setOnlineStatus(s, false);
		String[] mylist={"tuan","tuan1","tuan2", "tuan3", "tuan4" };
		if( args.length == 0 ) 
			for ( String s : mylist )  db.setOnlineStatus(s, false);
		db.printUsersDatabase();
	}

	public GameDatabaseAdapter(String database_url,String tablename) {
		this.url = database_url;
		this.tablename = tablename;
		
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			connection = DriverManager.getConnection(url);
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void close () {
		try {
			connection.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void clearTable() {
		Statement stmt = null;

		try {
			stmt = connection.createStatement();
			String sql = "DELETE from "+tablename+";";

			stmt.executeUpdate(sql);
			connection.commit();
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	/**
	 * 
	 * @return
	 * 		createUserTable(false)
	 */
	public boolean createTable() {
		return createTable(false);
	}
	
	/**
	 * 
	 * @param override_if_exists
	 * 		delete current "Users" table if exists
	 * @return
	 * 		<b>true</b> if the operation completed successfully <br>
	 * 		<b>false</b> if the operation failed <br>
	 */
	public boolean createTable(boolean override_if_exists) {
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			
			stmt = connection.createStatement();
			
			if( override_if_exists) { 
				String sql1 = "DROP TABLE IF EXISTS "+tablename+"";
				stmt.executeUpdate(sql1);
				connection.commit();
			}
			
			String sql2 = "CREATE TABLE "+tablename+"" 
					+ " (id INTEGER PRIMARY KEY,"
					+ " username	TEXT NOT NULL,"
					+ " password	TEXT NOT NULL,"
					+ " userid		TEXT NOT NULL,"
					+ " online		INTEGER DEFAULT 0,"
					+ " wins		INTEGER DEFAULT 0,"
					+ " losses		INTEGER DEFAULT 0,"
					+ " level		INTEGER DEFAULT 0)";
			
			stmt.executeUpdate(sql2);
			connection.commit();
			stmt.close();
			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 * 		<b>true</b> if the operation completed successfully <br>
	 * 		<b>false</b> if the operation failed <br>
	 */
	public synchronized boolean usernameExists (String username) {
		Statement stmt = null;
		boolean result=false;

		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT 1 FROM "+tablename+" WHERE username='"
							+ username + "';");
			if ( rs.next() ) {
				result= true;
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return result;
	}

	
	/**
	 * 
	 * @param username
	 * @param hashed_password
	 * @param userid 
	 * @return
	 * 		<b>true</b> if the operation completed successfully <br>
	 * 		<b>false</b> if the operation failed <br>
	 */
	public synchronized boolean addUser(String username, String hashed_password, String userid) {
		Statement stmt = null;

		try {
			stmt = connection.createStatement();
			String sql = "INSERT INTO "+tablename+" (username,password,userid) "
					+ "VALUES ('" + username + "', '"+hashed_password+"','"+userid+"');";
			
			stmt.executeUpdate(sql);
			connection.commit();
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 * 		<b>true</b> if the operation completed successfully <br>
	 * 		<b>false</b> if the operation failed <br>
	 */
	public synchronized boolean deleteUser(String username) {
		Statement stmt = null;

		try {
			stmt = connection.createStatement();
			String sql = "DELETE from "+tablename+" where username='"+username+"';";

			stmt.executeUpdate(sql);
			connection.commit();
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 * 		hashed password or null
	 */
	public synchronized String getPassword(String username) {
		Statement stmt = null;
		String result = null;
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT password FROM "+tablename+" WHERE username='"
							+ username + "';");
			if (rs.next()) {
				result = rs.getString("password");
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return result;
	}

	/**
	 * 
	 * @param username 
	 * @param hashed_password
	 * @return 
	 * 		<b>true</b> if the operation completed successfully <br>
	 * 		<b>false</b> if the operation failed <br>
	 */
	public synchronized boolean updatePassword(String username, String hashed_password) {
		Statement stmt = null;

		try {
			stmt = connection.createStatement();
			String sql = "UPDATE "+tablename+" set password = '" + hashed_password
					+ "' where username='" + username + "';";
			stmt.executeUpdate(sql);
			connection.commit();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Print out table Users in current database
	 */
	public synchronized void printUsersDatabase() {
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM "+tablename+";");
			while (rs.next()) {
				System.out.println("ID = " + rs.getInt("id"));
				System.out.println("Username = " + rs.getString("username"));
				System.out.println("Password = " + rs.getString("password"));
				System.out.println("Userid = " + rs.getString("userid"));
				System.out.println("Online = " + rs.getInt("online"));
				System.out.println("Wins = " + rs.getInt("wins"));
				System.out.println("Losses = " + rs.getInt("losses"));
				System.out.println("Level = " + rs.getInt("level"));
				System.out.println();
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 * 		<b>userinfo</b> ( userid, wins,losses , level ) <br>
	 * 		<b>null</b> if database error or username not exists
	 */
	public synchronized __UserInfo getUserInfo (String username ) {
		__UserInfo info = null;
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT userid,online,wins,losses,level FROM "+tablename+" WHERE username='"
							+ username + "';");
			if (rs.next()) {
				info = new __UserInfo();
				info.userid = rs.getString("userid").getBytes();
				info.online = rs.getInt("online") == 0? false:true;
				info.wins = (short) rs.getInt("wins");
				info.losses = (short) rs.getInt("losses");
				info.level = (short) rs.getInt("level");
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return info;
	}

	public synchronized boolean useridExists(String userid) {
		Statement stmt = null;
		boolean result=false;

		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT 1 FROM "+tablename+" WHERE userid='"
							+ userid + "';");
			if ( rs.next() ) {
				result= true;
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public synchronized void setOnlineStatus(String userid, boolean b) {
		Statement stmt = null;
		int status = b?1:0;
		try {
			stmt = connection.createStatement();
			String sql = "UPDATE "+tablename+" set online = '" + status
					+ "' where userid='" + userid + "';";
			stmt.executeUpdate(sql);
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("database: set "+userid+" status: online="+b);
	}
}
