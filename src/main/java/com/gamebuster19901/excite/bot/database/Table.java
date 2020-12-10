package com.gamebuster19901.excite.bot.database;

import java.io.IOError;
import java.sql.SQLException;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;
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
	public static Result selectColumnsFrom(MessageContext context, Column columns, Table table) throws SQLException {
		PreparedStatement st = context.getConnection().prepareStatement("SELECT " + columns + " FROM " + table);
		return st.query();
	}
	
	@SuppressWarnings("rawtypes")
	public static Result selectColumnsFromWhere(MessageContext context, Column columns, Table table, Comparison comparison) throws SQLException {
		PreparedStatement st;
		st = context.getConnection().prepareStatement("SELECT " + columns + " FROM " + table + " WHERE " + comparison);
		comparison.insertValues(st);

		try {
			return st.query();
		}
		catch(SQLException e) {
			throw new SQLException(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Result selectAllFrom(MessageContext context, Table table) throws SQLException {
		return selectColumnsFrom(context, ALL_COLUMNS, table);
	}
	
	@SuppressWarnings("rawtypes")
	public static Result selectAllFromWhere(MessageContext context, Table table, Comparison comparison) throws SQLException {
		return selectColumnsFromWhere(context, ALL_COLUMNS, table, comparison);
	}
	
	@SuppressWarnings("rawtypes")
	public static Result selectAllFromJoinedUsingWhere(MessageContext context, Table mainTable, Table otherTable, Column usingColumn, Comparison comparison) {
		try {
			PreparedStatement st = context.getConnection().prepareStatement("SELECT * FROM " + mainTable + " JOIN " + otherTable + " USING (" + usingColumn + ") WHERE " + comparison);
			comparison.insertValues(st);
			return st.query();
		} catch (SQLException e) {
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static boolean existsWhere(MessageContext context, Table table, Comparison comparison) {
		PreparedStatement st = null;
		try {
			st = context.getConnection().prepareStatement("SELECT EXISTS(SELECT 1 FROM " + table + " WHERE " + comparison);
			comparison.insertValues(st);
			Result result = st.query();
			return result.getRowCount() > 0;
		} catch (SQLException e) {
			if(st != null) {
				System.out.println(st);
			}
			throw new IOError(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void updateWhere(MessageContext context, Table table, Column parameter, Object value, Comparison comparison) throws SQLException {
		PreparedStatement st = context.getConnection().prepareStatement("UPDATE " + table + " SET " + parameter + " = ? WHERE " + comparison);
		insertValue(st, 1, value);
		comparison.offset(1);
		comparison.insertValues(st, 2);
		st.execute();
	}
	
	@SuppressWarnings("rawtypes")
	public static void deleteWhere(MessageContext context, Table table, Comparison comparison) {
		PreparedStatement st = null;
		try {
			st = context.getConnection().prepareStatement("DELETE FROM " + table + " WHERE " + comparison);
			comparison.insertValues(st);
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
			deleteWhere(demoter, Table.ADMINS, new Comparison(Column.DISCORD_ID, Comparator.EQUALS, user.getID()));
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
			deleteWhere(demoter, Table.OPERATORS, new Comparison(Column.DISCORD_ID, Comparator.EQUALS, user.getID()));
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
	}
	
	@SuppressWarnings("rawtypes")
	public static void revokeOperatorDBPermissions(MessageContext demoter, String user) throws SQLException {
		revokeAdminDBPermissions(demoter, user);
	}
	
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public static void insertValue(PreparedStatement st, int index, Object value) throws SQLException {
		if(value == null) {
			st.setObject(index, null);
		}
		else {
			Class clazz = value.getClass();
			if(clazz == Comparison.class) {
				insertValue(st, index, ((Comparison) value).getValue(index));
			}
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
	
	public static String makeSafe(String string) {
		return string.replace("%", "\\%").replace("_", "\\_").replace("\\", "\\\\");
	}
}
