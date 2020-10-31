package com.gamebuster19901.excite.bot.database;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.file.File;

public class DatabaseConnection {

	final Connection connection;
	
	public DatabaseConnection() throws IOException, SQLException {
		File file = new File("./mysql.secret");
		if(file.isSecret()) {
			List<String> lines = Files.readAllLines(file.toPath());
			this.connection = DriverManager.getConnection(lines.get(0), lines.get(1), lines.get(2));
			getTables();
		}
		else {
			throw new IOException(file.getAbsolutePath() + " is not secret!");
		}
	}
	
	public DatabaseConnection(DiscordUser discord) throws IOException, SQLException {
		String username = discord.getMySQLUsername();
		if(!MySQLUserExists(username)) {
			PreparedStatement createUser = Main.CONSOLE.getConnection().prepareStatement("CREATE USER ?@localhost IDENTIFIED BY ?");
			createUser.setString(1, username);
			createUser.setString(2, username);
			System.out.println(createUser);
			createUser.execute();
			
			Table.grantUserDBPermissions(ConsoleContext.INSTANCE, username);
			
			PreparedStatement setDefaultRole = Main.CONSOLE.getConnection().prepareStatement("SET DEFAULT ROLE User TO ?@localhost;");
			setDefaultRole.setString(1, username);
			System.out.println(setDefaultRole);
			setDefaultRole.execute();
		}
		File file = new File("./mysql.secret");
		if(file.isSecret()) {
			List<String> lines = Files.readAllLines(file.toPath());
			this.connection = DriverManager.getConnection(lines.get(0), username, username);
		}
		else {
			throw new IOException(file.getAbsolutePath() + " is not secret!");
		}
	}
		
	public DatabaseConnection(String connectionInfo) throws SQLException {
		this.connection = DriverManager.getConnection(connectionInfo);
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public static boolean MySQLUserExists(String username) throws SQLException {
		PreparedStatement st = Main.CONSOLE.getConnection().prepareStatement("SELECT EXISTS(SELECT 1 FROM mysql.user WHERE user = ?);");
		st.setString(1, username);
		ResultSet rs = st.executeQuery();
		rs.next();
		return rs.getBoolean(1);
	}
	
	void getTables() throws SQLException {
		ResultSet rs = connection.getMetaData().getTables("excitebot", null, "%", null);
		while (rs.next()) {
			System.out.println(rs.getString(3));
		}
	}
	
}
