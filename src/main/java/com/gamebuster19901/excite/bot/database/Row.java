package com.gamebuster19901.excite.bot.database;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;

import com.gamebuster19901.excite.bot.database.sql.ResultSet;

public class Row implements TableRetriever {

	public HashMap<Column, Object> results = new HashMap<Column, Object>();
	
	public Row (ResultSet results) throws SQLException {
		this(results, true);
	}
	
	public Row(ResultSet results, boolean next) throws SQLException {
		if(next) {
			results.next();
		}
		ResultSetMetaData metaData = results.getMetaData();
		int columns = results.getMetaData().getColumnCount();
		for(int i = 1; i < columns + 1; i++) {
			Column column = Column.getColumn(metaData.getColumnName(i));
			int type = metaData.getColumnType(i);
			
			if(type == Types.CHAR || type == Types.VARCHAR || type == Types.LONGVARCHAR) {
				this.results.put(column, results.getString(column));
				continue;
			}
			
			if(type == Types.BIT || type == Types.BOOLEAN) {
				this.results.put(column, results.getBoolean(column));
				continue;
			}
			
			if(type == Types.TINYINT || type == Types.SMALLINT) {
				this.results.put(column, results.getShort(column));
				continue;
			}
			
			if(type == Types.INTEGER) {
				this.results.put(column, results.getInt(column));
				continue;
			}
			
			if(type == Types.BIGINT) {
				this.results.put(column, results.getLong(column));
				continue;
			}
			
			if(type == Types.FLOAT || type == Types.REAL) {
				this.results.put(column, results.getFloat(column));
				continue;
			}
			
			if(type == Types.DOUBLE) {
				this.results.put(column, results.getDouble(column));
				continue;
			}
			
			throw new AssertionError("Unknown type " + type);
			
		}
	}
	
	public String getString(Column column) {
		if(results.containsKey(column)) {
			return (String) results.get(column);
		}
		throw new AssertionError("Column " + column + " is not in " + results);
	}
	
	public boolean getBoolean(Column column) {
		if(results.containsKey(column)) {
			return (boolean) results.get(column);
		}
		throw new AssertionError("Column " + column + " is not in " + results);
	}
	
	public short getShort(Column column) {
		if(results.containsKey(column)) {
			return (short) results.get(column);
		}
		throw new AssertionError("Column " + column + " is not in " + results);
	}
	
	public int getInt(Column column) {
		if(results.containsKey(column)) {
			return (int) results.get(column);
		}
		throw new AssertionError("Column " + column + " is not in " + results);
	}
	
	public long getLong(Column column) {
		if(results.containsKey(column)) {
			return (long) results.get(column);
		}
		throw new AssertionError("Column " + column + " is not in " + results);
	}
	
	public float getFloat(Column column) {
		if(results.containsKey(column)) {
			return (float) results.get(column);
		}
		throw new AssertionError("Column " + column + " is not in " + results);
	}
	
	public double getDouble(Column column) {
		if(results.containsKey(column)) {
			return (double) results.get(column);
		}
		throw new AssertionError("Column " + column + " is not in " + results);
	}
	
	public int size() {
		return results.size();
	}
}
