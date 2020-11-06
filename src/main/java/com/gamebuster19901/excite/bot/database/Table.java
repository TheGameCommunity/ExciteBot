package com.gamebuster19901.excite.bot.database;

import java.io.IOError;
import java.sql.SQLException;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;
import com.gamebuster19901.excite.bot.database.sql.ResultSet;
import com.gamebuster19901.excite.bot.user.DiscordUser;
import com.gamebuster19901.excite.util.StacktraceUtil;

import static com.gamebuster19901.excite.bot.database.Column.ALL_COLUMNS;

public enum Table {
	
	ADMINS,
	AUDIT_BANS,
	AUDIT_COMMANDS,
	AUDIT_NAME_CHANGES,
	AUDIT_PARDONS,
	AUDIT_PROFILE_DISCOVERIES,
	AUDIT_RANK_CHANGES,
	AUDITS,
	DISCORD_SERVERS,
	DISCORD_USERS,
	OPERATORS,
	PLAYERS;
	
	public static final String HOST = "@'localhost'";
	
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
	
	@SuppressWarnings("rawtypes")
	public static ResultSet selectColumnsFrom(MessageContext context, Column columns, Table table) throws SQLException {
		PreparedStatement st = context.getConnection().prepareStatement("SELECT " + columns + " FROM " + table);
		return st.executeQuery();
	}
	
	@SuppressWarnings("rawtypes")
	public static ResultSet selectColumnsFromWhere(MessageContext context, Column columns, Table table, Column where, Comparator comparator, Object comparee) throws SQLException {
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
		return selectColumnsFrom(context, ALL_COLUMNS, table);
	}
	
	@SuppressWarnings("rawtypes")
	public static ResultSet selectAllFromWhere(MessageContext context, Table table, Column whereColumn, Comparator comparator, Object comparee) throws SQLException {
		return selectColumnsFromWhere(context, ALL_COLUMNS, table, whereColumn, comparator, comparee);
	}
	
	@SuppressWarnings("rawtypes")
	public static ResultSet selectAllFromJoinedUsingWhere(MessageContext context, Table mainTable, Table otherTable, Column usingColumn, Column whereColumn, Comparator comparator, Object comparee) {
		try {
			PreparedStatement st = context.getConnection().prepareStatement("SELECT * FROM " + mainTable + " JOIN " + otherTable + " USING (" + usingColumn + ") WHERE " + whereColumn + comparator + " ?");
			return st.executeQuery();
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public static boolean existsWhere(MessageContext context, Table table, Column whereColumn, Comparator comparator, Object comparee) {
		PreparedStatement st = null;
		try {
			if(comparee != null) {
				st = context.getConnection().prepareStatement("SELECT EXISTS(SELECT 1 FROM " + table + " WHERE ? " + comparator + " ?");
				insertValue(st, 2, comparee);
			}
			else {
				st = context.getConnection().prepareStatement("SELECT EXISTS(SELECT 1 FROM " + table + " WHERE ? " + comparator);
			}
			st.setString(1, whereColumn);
			ResultSet rs = st.executeQuery();
			rs.next();
			return rs.getBoolean(1);
		} catch (SQLException e) {
			if(st != null) {
				System.out.println(st);
			}
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void updateWhere(MessageContext context, Table table, Column parameter, Object value, Column whereColumn, Comparator comparator, Object comparee) throws SQLException {
		PreparedStatement st = context.getConnection().prepareStatement("UPDATE " + table + " SET " + parameter + " = ? WHERE " + whereColumn + comparator + " ?");
		insertValue(st, 1, value);
		insertValue(st, 2, comparee);
		st.execute();
		System.out.println(st);
	}
	
	@SuppressWarnings("rawtypes")
	public static void deleteWhere(MessageContext context, Table table, Column column, Comparator comparator, Object comparee) {
		PreparedStatement st = null;
		try {
			st = context.getConnection().prepareStatement("DELETE FROM " + table + " WHERE " + column + comparator + " ?");
			insertValue(st, 1, comparee);
			st.execute();
		} catch (SQLException e) {
			if(st != null) {
				System.out.println(st);
			}
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void addAdmin(MessageContext promoter, DiscordUser user) {
		PreparedStatement st = null;
		try {
			st = Insertion.insertInto(ADMINS).setColumns(Column.DISCORD_ID).to(user.getID()).prepare(promoter);
			st.execute();
			String botName = Main.discordBot.getSelfUser().getAsMention();
			grantAdminDBPermissions(ConsoleContext.INSTANCE, user.getMySQLUsername());
			user.sendMessage("You are now an administrator for " + botName);
			promoter.sendMessage(user.toDetailedString() + " is now an administrator for " + botName);
		} catch (SQLException e) {
			promoter.sendMessage("Could not make " + user + " a bot administrator: ");
			promoter.sendMessage(StacktraceUtil.getStackTrace(e));
			if(st != null) {
				System.out.println(st);
			}
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void removeAdmin(MessageContext demoter, DiscordUser user) {
		try {
			deleteWhere(demoter, Table.ADMINS, Column.DISCORD_ID, Comparator.EQUALS, user.getID());
			revokeAdminDBPermissions(demoter, user.getMySQLUsername());
			String botName = Main.discordBot.getSelfUser().getAsMention();
			user.sendMessage("You are no longer an administrator for " + botName);
			demoter.sendMessage(user.toDetailedString() + " is no longer a bot administrator for " + botName);
		} catch (SQLException e) {
			demoter.sendMessage("could not revoke the administrator permissions of " + user);
			demoter.sendMessage(StacktraceUtil.getStackTrace(e));
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void addOperator(MessageContext promoter, DiscordUser user) {
		PreparedStatement st = null;
		try {
			st = Insertion.insertInto(OPERATORS).setColumns(Column.DISCORD_ID).to(user.getID()).prepare(promoter);
			st.execute();
			String botName = Main.discordBot.getSelfUser().getAsMention();
			grantOperatorDBPermissions(ConsoleContext.INSTANCE, user.getMySQLUsername());
			user.sendMessage("You are now an operator for " + botName);
			promoter.sendMessage(user.toDetailedString() + " is now an operator for " + botName);
		} catch (SQLException e) {
			promoter.sendMessage("Could not make " + user + " a bot operator: ");
			promoter.sendMessage(StacktraceUtil.getStackTrace(e));
			if(st != null) {
				System.out.println(st);
			}
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void removeOperator(MessageContext demoter, DiscordUser user) {
		try {
			deleteWhere(demoter, Table.OPERATORS, Column.DISCORD_ID, Comparator.EQUALS, user.getID());
			revokeAdminDBPermissions(ConsoleContext.INSTANCE, user.getMySQLUsername());
			String botName = Main.discordBot.getSelfUser().getAsMention();
			user.sendMessage("You are no longer an operator for " + botName);
			demoter.getDiscordAuthor().sendMessage(user.toDetailedString() + " is no longer a bot operator for " + botName);
		} catch (SQLException e) {
			demoter.sendMessage("could not revoke the operator permissions of " + user);
			demoter.sendMessage(StacktraceUtil.getStackTrace(e));
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static void clearDBPermissions(MessageContext demoter, String user) {
		PreparedStatement st = null;
		try {
			st = demoter.getConnection().prepareStatement("REVOKE User, Admin, Operator FROM ?" + HOST);
			insertValue(st, 1, user);
			st.execute();
		} catch (SQLException e) {
			if(st != null) {
				System.out.println(st);
			}
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void grantUserDBPermissions(MessageContext promoter, String user) throws SQLException {
		clearDBPermissions(promoter, user);
		PreparedStatement st = promoter.getConnection().prepareStatement("GRANT 'User' TO ?" + HOST);
		PreparedStatement st2 = promoter.getConnection().prepareStatement("SET DEFAULT ROLE User TO ?" + HOST);
		insertValue(st, 1, user);
		insertValue(st2, 1, user);
		try {
			st.execute();
		} catch (SQLException e) {
			throw new AssertionError(st.toString(), e);
		}
		try {
			st2.execute();
		} catch (SQLException e) {
			throw new AssertionError(st2.toString(), e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void grantAdminDBPermissions(MessageContext promoter, String user) throws SQLException {
		clearDBPermissions(promoter, user);
		PreparedStatement st = promoter.getConnection().prepareStatement("GRANT 'Admin' TO ?" + HOST);
		PreparedStatement st2 = promoter.getConnection().prepareStatement("SET DEFAULT ROLE Admin TO ?" + HOST);
		insertValue(st, 1, user);
		insertValue(st2, 1, user);
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
		PreparedStatement st = promoter.getConnection().prepareStatement("GRANT 'Operator' TO ?" + HOST);
		PreparedStatement st2 = promoter.getConnection().prepareStatement("SET DEFAULT ROLE Operator TO ?" + HOST);
		insertValue(st, 1, user);
		insertValue(st2, 1, user);
		st.execute();
		st2.execute();
		System.out.println(st);
		System.out.println(st2);
	}
	
	@SuppressWarnings("rawtypes")
	public static void revokeOperatorDBPermissions(MessageContext demoter, String user) throws SQLException {
		revokeAdminDBPermissions(demoter, user);
	}
	
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public static void insertValue(PreparedStatement st, int index, Object value) throws SQLException {
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
