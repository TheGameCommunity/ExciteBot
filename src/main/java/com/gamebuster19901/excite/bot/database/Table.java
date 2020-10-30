package com.gamebuster19901.excite.bot.database;

import java.io.IOError;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.StacktraceUtil;
import com.gamebuster19901.excite.util.Vulnerable;

public enum Table {
	
	ADMINS,
	DISCORD_SERVERS,
	DISCORD_USERS,
	OPERATORS,
	PLAYERS;
	
	public static final String HOST = "@'localhost'";
	
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
	
	@Vulnerable
	@SuppressWarnings("rawtypes")
	public static ResultSet selectColumnsFrom(MessageContext context, @Vulnerable String columns, Table table) throws SQLException {
		PreparedStatement st = context.getConnection().prepareStatement("SELECT " + columns + " FROM " + table);
		return st.executeQuery();
	}
	
	@Vulnerable
	@SuppressWarnings("rawtypes")
	public static ResultSet selectColumnsFromWhere(MessageContext context, @Vulnerable String columns, Table table, @Vulnerable String where, Comparator comparator, Object comparee) throws SQLException {
		PreparedStatement st;
		if(comparee != null) {
			st = context.getConnection().prepareStatement("SELECT " + columns + " FROM " + table + " WHERE " + where + comparator + " ?");
			insertValue(st, 1, comparee);
		}
		else {
			st = context.getConnection().prepareStatement("SELECT " + columns + " FROM " + table + " WHERE " + where + comparator);
		}
		
		try {
			return st.executeQuery();
		}
		catch(SQLException e) {
			throw new SQLException(st.toString());
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static ResultSet selectAllFrom(MessageContext context, Table table) throws SQLException {
		return selectColumnsFrom(context, "*", table);
	}
	
	@Vulnerable
	@SuppressWarnings("rawtypes")
	public static ResultSet selectAllFromWhere(MessageContext context, Table table, String where, Comparator comparator, Object comparee) throws SQLException {
		return selectColumnsFromWhere(context, "*", table, where, comparator, comparee);
	}
	
	@Vulnerable
	@SuppressWarnings("rawtypes")
	public static boolean existsWhere(MessageContext context, Table table, @Vulnerable String where, Comparator comparator, Object comparee) {
		try {
			PreparedStatement st;
			if(comparee != null) {
				st = context.getConnection().prepareStatement("SELECT EXISTS(SELECT 1 FROM " + table + " WHERE ? " + comparator + " ?");
				insertValue(st, 2, comparee);
			}
			else {
				st = context.getConnection().prepareStatement("SELECT EXISTS(SELECT 1 FROM " + table + " WHERE ? " + comparator);
			}
			st.setString(1, where);
			ResultSet rs = st.executeQuery();
			rs.next();
			return rs.getBoolean(1);
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	@Vulnerable
	@SuppressWarnings("rawtypes")
	public static void updateWhere(MessageContext context, Table table, @Vulnerable String parameter, Object value, @Vulnerable String where, Comparator comparator, Object comparee) throws SQLException {
		PreparedStatement st = context.getConnection().prepareStatement("UPDATE " + table + " SET " + parameter + " = ? WHERE " + where + comparator + " ?");
		insertValue(st, 1, value);
		insertValue(st, 2, comparee);
		st.execute();
		System.out.println(st);
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
	
	@SuppressWarnings("rawtypes")
	private static void insertValue(PreparedStatement st, int index, Object value) throws SQLException {
		Class clazz = value.getClass();
		if(clazz.isPrimitive() || value instanceof Number || value instanceof Boolean || value instanceof Character) {
			if (clazz == long.class || clazz == Long.class) {
				st.setLong(index, (long)value);
			}
			else if(clazz == int.class || clazz == Integer.class) {
				st.setInt(index, (int)value);
			}
			else if (clazz == boolean.class || clazz == Boolean.class) {
				st.setBoolean(index, (boolean)value);
			}
			else if (clazz == double.class || clazz == Double.class) {
				st.setDouble(index, (double)value);
			}
			else if (clazz == float.class || clazz == Float.class) {
				st.setFloat(index, (float)value);
			}
			else if (clazz == short.class || clazz == Short.class) {
				st.setShort(index, (short)value);
			}
			else if (clazz == byte.class || clazz == Byte.class) {
				st.setShort(index, (byte)value);
			}
			else if (clazz == char.class || clazz == Character.class) {
				st.setString(index, value.toString());
			}
		}
		else {
			st.setString(index, value.toString());
		}
	}
}
