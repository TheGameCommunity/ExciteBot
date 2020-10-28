package com.gamebuster19901.excite.bot.database;

import java.io.IOError;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.StacktraceUtil;

public enum Table {
	
	ADMINS,
	DISCORD_SERVERS,
	DISCORD_USERS,
	OPERATORS,
	PLAYERS;
	
	public static final String HOST = "&localhost";
	
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
	
	@SuppressWarnings("rawtypes")
	public static ResultSet selectColumnsFrom(MessageContext context, String columns, Table table) throws SQLException {
		PreparedStatement st = context.getConnection().prepareStatement("SELECT ? FROM ?");
		st.setString(1, columns);
		st.setString(2, table.toString());
		return st.executeQuery();
	}
	
	@SuppressWarnings("rawtypes")
	public static ResultSet selectColumnsFromWhere(MessageContext context, String columns, Table table, String where) throws SQLException {
		PreparedStatement st = context.getConnection().prepareStatement("SELECT ? FROM ? WHERE ?");
		st.setString(1, columns);
		st.setString(2, table.toString());
		st.setString(3, where);
		return st.executeQuery();
	}
	
	@SuppressWarnings("rawtypes")
	public static ResultSet selectAllFrom(MessageContext context, Table table) throws SQLException {
		return selectColumnsFrom(context, "*", table);
	}
	
	@SuppressWarnings("rawtypes")
	public static ResultSet selectAllFromWhere(MessageContext context, Table table, String where) throws SQLException {
		return selectColumnsFromWhere(context, "*", table, where);
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean existsWhere(MessageContext context, Table table, String where) {
		try {
			PreparedStatement st = context.getConnection().prepareStatement("SELECT EXISTS(SELECT 1 FROM ? WHERE ?);");
			st.setString(1, table.toString());
			st.setString(2, where);
			ResultSet rs = st.executeQuery();
			rs.next();
			return rs.getBoolean(1);
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	public static void updateWhere(MessageContext context, Table table, String parameter, String value, String where) throws SQLException {
		PreparedStatement st = context.getConnection().prepareStatement("UPDATE ? SET ? = ? WHERE ?");
		st.setString(1, table.toString());
		st.setString(2, parameter);
		st.setString(3, value);
		st.setString(4, where);
		st.execute();
	}
	
	@SuppressWarnings("rawtypes")
	public static void addAdmin(MessageContext promoter, DiscordUser user) {
		try {
			PreparedStatement st = promoter.getConnection().prepareStatement("INSERT INTO `admins` (`discord_id`) VALUES ('?');");
			st.setLong(1, user.getId());
			st.execute();
			String botName = Main.discordBot.getSelfUser().getAsMention();
			user.sendMessage("You are now an administrator for " + botName);
			promoter.sendMessage(user.toDetailedString() + " is now an administrator for " + botName);
		} catch (SQLException e) {
			promoter.sendMessage("Could not make " + user + " a bot administrator: ");
			promoter.sendMessage(StacktraceUtil.getStackTrace(e));
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void removeAdmin(MessageContext demoter, DiscordUser user) {
		try {
			revokeAdminDBPermissions(demoter, user.getMySQLUsername());
			String botName = Main.discordBot.getSelfUser().getAsMention();
			user.sendMessage("You are no longer an administrator for " + botName);
			demoter.sendMessage(user.toDetailedString() + " is no longer a bot administrator for " + botName);
		} catch (SQLException e) {
			demoter.sendMessage("could not revoke the administrator permissions of " + user);
			demoter.sendMessage(StacktraceUtil.getStackTrace(e));
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void addOperator(MessageContext promoter, DiscordUser user) {
		try {
			PreparedStatement st = promoter.getConnection().prepareStatement("INSERT INTO `operators` (`discord_id`) VALUES ('?');");
			st.setLong(1, user.getId());
			st.execute();
			String botName = Main.discordBot.getSelfUser().getAsMention();
			user.sendMessage("You are now an operator for " + botName);
			promoter.sendMessage(user.toDetailedString() + " is now an operator for " + botName);
		} catch (SQLException e) {
			promoter.sendMessage("Could not make " + user + " a bot operator: ");
			promoter.sendMessage(StacktraceUtil.getStackTrace(e));
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void removeOperator(MessageContext demoter, DiscordUser user) {
		try {
			revokeAdminDBPermissions(demoter, user.getMySQLUsername());
			String botName = Main.discordBot.getSelfUser().getAsMention();
			user.sendMessage("You are no longer an operator for " + botName);
			demoter.sendMessage(user.toDetailedString() + " is no longer a bot operator for " + botName);
		} catch (SQLException e) {
			demoter.sendMessage("could not revoke the operator permissions of " + user);
			demoter.sendMessage(StacktraceUtil.getStackTrace(e));
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static void clearDBPermissions(MessageContext demoter, String user) throws SQLException {
		PreparedStatement st = demoter.getConnection().prepareStatement("REVOKE User, Admin, Operator FROM '?'" + HOST);
		st.setString(0, user);
		st.execute();
	}
	
	@SuppressWarnings("rawtypes")
	public static void grantUserDBPermissions(MessageContext promoter, String user) throws SQLException {
		clearDBPermissions(promoter, user);
		PreparedStatement st = promoter.getConnection().prepareStatement("GRANT 'User' TO '?'" + HOST);
		PreparedStatement st2 = promoter.getConnection().prepareStatement("SET DEFAULT ROLE User TO '?'" + HOST);
		st.setString(0, user);
		st2.setString(0, user);
		st.execute();
		st2.execute();
	}
	
	@SuppressWarnings("rawtypes")
	public static void grantAdminDBPermissions(MessageContext promoter, String user) throws SQLException {
		clearDBPermissions(promoter, user);
		PreparedStatement st = promoter.getConnection().prepareStatement("GRANT 'Admin' TO '?'" + HOST);
		PreparedStatement st2 = promoter.getConnection().prepareStatement("SET DEFAULT ROLE Admin TO '?'" + HOST);
		st.setString(0, user);
		st2.setString(0, user);
		st.execute();
		st2.execute();
	}
	
	@SuppressWarnings("rawtypes")
	public static void revokeAdminDBPermissions(MessageContext demoter, String user) throws SQLException {
		clearDBPermissions(demoter, user);
		grantUserDBPermissions(demoter, user);
	}
	
	@SuppressWarnings("rawtypes")
	public static void grantOperatorDBPermissions(MessageContext promoter, String user) throws SQLException {
		clearDBPermissions(promoter, user);
		PreparedStatement st = promoter.getConnection().prepareStatement("GRANT 'Operator' TO '?'" + HOST);
		PreparedStatement st2 = promoter.getConnection().prepareStatement("SET DEFAULT ROLE Operator TO '?'" + HOST);
		st.setString(0, user);
		st2.setString(0, user);
		st.execute();
		st2.execute();
	}
	
	@SuppressWarnings("rawtypes")
	public static void revokeOperatorDBPermissions(MessageContext demoter, String user) throws SQLException {
		revokeAdminDBPermissions(demoter, user);
	}
}
