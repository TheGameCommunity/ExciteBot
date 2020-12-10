package com.gamebuster19901.excite.bot.database;

import java.sql.SQLException;

public interface TableRetriever {

	public String getString(Column column) throws SQLException;
	
	public boolean getBoolean(Column column) throws SQLException;
	
	public short getShort(Column column) throws SQLException;
	
	public int getInt(Column column) throws SQLException;
	
	public long getLong(Column column) throws SQLException;
	
	public float getFloat(Column column) throws SQLException;
	
	public double getDouble(Column column) throws SQLException;
	
	public int size();
	
}
