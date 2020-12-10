package com.gamebuster19901.excite.bot.database;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import com.gamebuster19901.excite.bot.database.sql.ResultSet;

public class Result implements TableRetriever {
	
	private final ArrayList<Row> rows = new ArrayList<Row>();
	
	private final int columns;
	private int cursor = 0;
	
	public Result(ResultSet resultSet) throws SQLException {
		ResultSetMetaData metaData = resultSet.getMetaData();
		columns = metaData.getColumnCount();
		
		while(resultSet.next()) {
			rows.add(new Row(resultSet, false));
		}
		
		resultSet.close(); //the resultSet should not be used anymore
	}
	
	public Row getRow(boolean next) {
		if(next) {
			next();
		}
		if(cursorValid()) {
			return rows.get(cursor - 1);
		}
		throw new IndexOutOfBoundsException("Cursor is at illegal position " + cursor + " (length " + rows.size() + ")");
	}
	
	public Row getRow() {
		return getRow(false);
	}
	
	public boolean next() {
		++cursor;
		return cursorValid();
	}
	
	public boolean hasNext() {
		return cursorValid(cursor + 1);
	}
	
	public boolean previous() {
		--cursor;
		return cursorValid();
	}
	
	public boolean hasPrevious() {
		return cursorValid(cursor - 1);
	}
	
	public boolean cursorValid() {
		return cursorValid(cursor);
	}
	
	public boolean cursorValid(int index) {
		return index > 0 && index <= rows.size();
	}
	
	public String getString(Column column) {
		return getRow().getString(column);
	}
	
	public boolean getBoolean(Column column) {
		return getRow().getBoolean(column);
	}
	
	public short getShort(Column column) {
		return getRow().getShort(column);
	}
	
	public int getInt(Column column) {
		return getRow().getInt(column);
	}
	
	public long getLong(Column column) {
		return getRow().getLong(column);
	}
	
	public float getFloat(Column column) {
		return getRow().getFloat(column);
	}
	
	public double getDouble(Column column) {
		return getRow().getDouble(column);
	}
	
	public int size() {
		return getRowCount();
	}
	
	public int getColumnCount() {
		return columns;
	}
	
	public int getRowCount() {
		return rows.size();
	}
}
