package com.gamebuster19901.excite.bot.database.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import com.gamebuster19901.excite.bot.database.Column;

public class ResultSet implements java.sql.ResultSet{

	private final java.sql.ResultSet parent;
	
	public ResultSet(java.sql.ResultSet parent) {
		this.parent = parent;
	}
	
	@Override
	public String toString() {
		return parent.toString();
	}
	
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return parent.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return parent.isWrapperFor(iface);
	}

	@Override
	public boolean next() throws SQLException {
		return parent.next();
	}

	@Override
	public void close() throws SQLException {
		parent.close();
	}

	@Override
	public boolean wasNull() throws SQLException {
		return parent.wasNull();
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		return parent.getBigDecimal(columnIndex);
	}

	@Override
	public byte[] getBytes(int columnIndex) throws SQLException {
		return parent.getBytes(columnIndex);
	}

	@Override
	public Date getDate(int columnIndex) throws SQLException {
		return parent.getDate(columnIndex);
	}

	@Override
	public Time getTime(int columnIndex) throws SQLException {
		return parent.getTime(columnIndex);
	}

	@Override
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return parent.getTimestamp(columnIndex);
	}

	@Override
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return parent.getAsciiStream(columnIndex);
	}

	@Override
	@Deprecated
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		return parent.getUnicodeStream(columnIndex);
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return parent.getBinaryStream(columnIndex);
	}

	@Override
	@Deprecated
	public String getString(int columnIndex) throws SQLException {
		return parent.getString(columnIndex);
	}
	
	@Override
	@Deprecated
	public String getString(String columnLabel) throws SQLException {
		return parent.getString(columnLabel);
	}
	
	public String getString(Column column) throws SQLException {
		return getString(column.toString());
	}
	
	@Override
	@Deprecated
	public boolean getBoolean(int columnIndex) throws SQLException {
		return parent.getBoolean(columnIndex);
	}

	@Override
	@Deprecated
	public boolean getBoolean(String columnLabel) throws SQLException {
		return parent.getBoolean(columnLabel);
	}
	
	public boolean getBoolean(Column column) throws SQLException {
		return getBoolean(column.toString());
	}

	@Override
	@Deprecated
	public byte getByte(int columnIndex) throws SQLException {
		return parent.getByte(columnIndex);
	}
	
	@Override
	@Deprecated
	public byte getByte(String columnLabel) throws SQLException {
		return parent.getByte(columnLabel);
	}
	
	public byte getByte(Column column) throws SQLException {
		return getByte(column.toString());
	}

	@Override
	@Deprecated
	public short getShort(int columnIndex) throws SQLException {
		return parent.getShort(columnIndex);
	}
	
	@Override
	@Deprecated
	public short getShort(String columnLabel) throws SQLException {
		return parent.getShort(columnLabel);
	}
	
	public short getShort(Column column) throws SQLException {
		return getShort(column.toString());
	}
	
	@Override
	@Deprecated
	public int getInt(int columnIndex) throws SQLException {
		return parent.getInt(columnIndex);
	}

	@Override
	@Deprecated
	public int getInt(String columnLabel) throws SQLException {
		return parent.getInt(columnLabel);
	}
	
	public int getInt(Column column) throws SQLException {
		return getInt(column.toString());
	}

	@Override
	@Deprecated
	public long getLong(int columnIndex) throws SQLException {
		return parent.getLong(columnIndex);
	}
	
	@Override
	@Deprecated
	public long getLong(String columnLabel) throws SQLException {
		return parent.getLong(columnLabel);
	}
	
	public long getLong(Column column) throws SQLException {
		return getLong(column.toString());
	}

	@Override
	@Deprecated
	public float getFloat(int columnIndex) throws SQLException {
		return parent.getFloat(columnIndex);
	}
	
	@Override
	@Deprecated
	public float getFloat(String columnLabel) throws SQLException {
		return parent.getFloat(columnLabel);
	}
	
	public float getFloat(Column column) throws SQLException {
		return getFloat(column.toString());
	}

	@Override
	@Deprecated
	public double getDouble(int columnIndex) throws SQLException {
		return parent.getDouble(columnIndex);
	}
	
	@Override
	@Deprecated
	public double getDouble(String columnLabel) throws SQLException {
		return parent.getDouble(columnLabel);
	}
	
	public double getDoutble(Column column) throws SQLException {
		return getDouble(column.toString());
	}

	@Override
	@Deprecated
	public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		return parent.getBigDecimal(columnLabel, scale);
	}

	@Override
	public byte[] getBytes(String columnLabel) throws SQLException {
		return parent.getBytes(columnLabel);
	}

	@Override
	public Date getDate(String columnLabel) throws SQLException {
		return parent.getDate(columnLabel);
	}

	@Override
	public Time getTime(String columnLabel) throws SQLException {
		return parent.getTime(columnLabel);
	}

	@Override
	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		return parent.getTimestamp(columnLabel);
	}

	@Override
	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		return parent.getAsciiStream(columnLabel);
	}

	@Override
	@Deprecated
	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		return parent.getUnicodeStream(columnLabel);
	}

	@Override
	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		return parent.getBinaryStream(columnLabel);
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return parent.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		parent.clearWarnings();
	}

	@Override
	public String getCursorName() throws SQLException {
		return parent.getCursorName();
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return parent.getMetaData();
	}

	@Override
	public Object getObject(int columnIndex) throws SQLException {
		return parent.getObject(columnIndex);
	}

	@Override
	public Object getObject(String columnLabel) throws SQLException {
		return parent.getObject(columnLabel);
	}

	@Override
	public int findColumn(String columnLabel) throws SQLException {
		return parent.findColumn(columnLabel);
	}

	@Override
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		return parent.getCharacterStream(columnIndex);
	}

	@Override
	public Reader getCharacterStream(String columnLabel) throws SQLException {
		return parent.getCharacterStream(columnLabel);
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return parent.getBigDecimal(columnIndex);
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		return parent.getBigDecimal(columnLabel);
	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		return parent.isBeforeFirst();
	}

	@Override
	public boolean isAfterLast() throws SQLException {
		return parent.isAfterLast();
	}

	@Override
	public boolean isFirst() throws SQLException {
		return parent.isFirst();
	}

	@Override
	public boolean isLast() throws SQLException {
		return parent.isLast();
	}

	@Override
	public void beforeFirst() throws SQLException {
		parent.beforeFirst();
	}

	@Override
	public void afterLast() throws SQLException {
		parent.afterLast();
	}

	@Override
	public boolean first() throws SQLException {
		return parent.first();
	}

	@Override
	public boolean last() throws SQLException {
		return parent.last();
	}

	@Override
	public int getRow() throws SQLException {
		return parent.getRow();
	}

	@Override
	public boolean absolute(int row) throws SQLException {
		return parent.absolute(row);
	}

	@Override
	public boolean relative(int rows) throws SQLException {
		return parent.relative(rows);
	}

	@Override
	public boolean previous() throws SQLException {
		return parent.previous();
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		parent.setFetchDirection(direction);
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return parent.getFetchDirection();
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		parent.setFetchSize(rows);
	}

	@Override
	public int getFetchSize() throws SQLException {
		return parent.getFetchSize();
	}

	@Override
	public int getType() throws SQLException {
		return parent.getType();
	}

	@Override
	public int getConcurrency() throws SQLException {
		return parent.getConcurrency();
	}

	@Override
	public boolean rowUpdated() throws SQLException {
		return parent.rowUpdated();
	}

	@Override
	public boolean rowInserted() throws SQLException {
		return parent.rowInserted();
	}

	@Override
	public boolean rowDeleted() throws SQLException {
		return parent.rowDeleted();
	}

	@Override
	@Deprecated
	public void updateNull(int columnIndex) throws SQLException {
		parent.updateNull(columnIndex);
	}

	@Override
	@Deprecated
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		parent.updateBoolean(columnIndex, x);
	}

	@Override
	@Deprecated
	public void updateByte(int columnIndex, byte x) throws SQLException {
		parent.updateByte(columnIndex, x);
	}

	@Override
	@Deprecated
	public void updateShort(int columnIndex, short x) throws SQLException {
		parent.updateShort(columnIndex, x);
	}

	@Override
	@Deprecated
	public void updateInt(int columnIndex, int x) throws SQLException {
		parent.updateInt(columnIndex, x);
	}

	@Override
	@Deprecated
	public void updateLong(int columnIndex, long x) throws SQLException {
		parent.updateLong(columnIndex, x);
	}

	@Override
	@Deprecated
	public void updateFloat(int columnIndex, float x) throws SQLException {
		parent.updateFloat(columnIndex, x);
	}

	@Override
	@Deprecated
	public void updateDouble(int columnIndex, double x) throws SQLException {
		parent.updateDouble(columnIndex, x);
	}

	@Override
	@Deprecated
	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		parent.updateBigDecimal(columnIndex, x);
	}

	@Override
	@Deprecated
	public void updateString(int columnIndex, String x) throws SQLException {
		parent.updateString(columnIndex, x);
	}

	@Override
	@Deprecated
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		parent.updateBytes(columnIndex, x);
	}

	@Override
	public void updateDate(int columnIndex, Date x) throws SQLException {
		parent.updateDate(columnIndex, x);
	}

	@Override
	public void updateTime(int columnIndex, Time x) throws SQLException {
		parent.updateTime(columnIndex, x);
	}

	@Override
	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		parent.updateTimestamp(columnIndex, x);
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		parent.updateAsciiStream(columnIndex, x, length);
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		parent.updateBinaryStream(columnIndex, x, length);
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		parent.updateCharacterStream(columnIndex, x, length);
	}

	@Override
	public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
		parent.updateObject(columnIndex, x, scaleOrLength);
	}

	@Override
	public void updateObject(int columnIndex, Object x) throws SQLException {
		parent.updateObject(columnIndex, x);
	}

	@Override
	@Deprecated
	public void updateNull(String columnLabel) throws SQLException {
		parent.updateNull(columnLabel);
	}

	@Override
	@Deprecated
	public void updateBoolean(String columnLabel, boolean x) throws SQLException {
		parent.updateBoolean(columnLabel, x);
	}

	@Override
	@Deprecated
	public void updateByte(String columnLabel, byte x) throws SQLException {
		parent.updateByte(columnLabel, x);
	}

	@Override
	@Deprecated
	public void updateShort(String columnLabel, short x) throws SQLException {
		parent.updateShort(columnLabel, x);
	}

	@Override
	@Deprecated
	public void updateInt(String columnLabel, int x) throws SQLException {
		parent.updateInt(columnLabel, x);
	}

	@Override
	@Deprecated
	public void updateLong(String columnLabel, long x) throws SQLException {
		parent.updateLong(columnLabel, x);
	}

	@Override
	@Deprecated
	public void updateFloat(String columnLabel, float x) throws SQLException {
		parent.updateFloat(columnLabel, x);
	}

	@Override
	@Deprecated
	public void updateDouble(String columnLabel, double x) throws SQLException {
		parent.updateDouble(columnLabel, x);
	}

	@Override
	@Deprecated
	public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
		parent.updateBigDecimal(columnLabel, x);
	}

	@Override
	@Deprecated
	public void updateString(String columnLabel, String x) throws SQLException {
		parent.updateString(columnLabel, x);
	}

	@Override
	@Deprecated
	public void updateBytes(String columnLabel, byte[] x) throws SQLException {
		parent.updateBytes(columnLabel, x);
	}

	@Override
	@Deprecated
	public void updateDate(String columnLabel, Date x) throws SQLException {
		parent.updateDate(columnLabel, x);
	}

	@Override
	@Deprecated
	public void updateTime(String columnLabel, Time x) throws SQLException {
		parent.updateTime(columnLabel, x);
	}

	@Override
	@Deprecated
	public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
		parent.updateTimestamp(columnLabel, x);
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
		parent.updateAsciiStream(columnLabel, x, length);
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
		parent.updateBinaryStream(columnLabel, x, length);
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
		parent.updateCharacterStream(columnLabel, reader, length);
	}

	@Override
	public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
		parent.updateObject(columnLabel, x, scaleOrLength);
	}

	@Override
	public void updateObject(String columnLabel, Object x) throws SQLException {
		parent.updateObject(columnLabel, x);
	}

	@Override
	public void insertRow() throws SQLException {
		parent.insertRow();
	}

	@Override
	public void updateRow() throws SQLException {
		parent.updateRow();
	}

	@Override
	public void deleteRow() throws SQLException {
		parent.deleteRow();
	}

	@Override
	public void refreshRow() throws SQLException {
		parent.refreshRow();
	}

	@Override
	public void cancelRowUpdates() throws SQLException {
		parent.cancelRowUpdates();
	}

	@Override
	public void moveToInsertRow() throws SQLException {
		parent.moveToInsertRow();
	}

	@Override
	public void moveToCurrentRow() throws SQLException {
		parent.moveToCurrentRow();
	}

	@Override
	public Statement getStatement() throws SQLException {
		return parent.getStatement();
	}

	@Override
	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
		return parent.getObject(columnIndex, map);
	}

	@Override
	public Ref getRef(int columnIndex) throws SQLException {
		return parent.getRef(columnIndex);
	}

	@Override
	public Blob getBlob(int columnIndex) throws SQLException {
		return parent.getBlob(columnIndex);
	}

	@Override
	public Clob getClob(int columnIndex) throws SQLException {
		return parent.getClob(columnIndex);
	}

	@Override
	public Array getArray(int columnIndex) throws SQLException {
		return parent.getArray(columnIndex);
	}

	@Override
	public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
		return parent.getObject(columnLabel, map);
	}

	@Override
	public Ref getRef(String columnLabel) throws SQLException {
		return parent.getRef(columnLabel);
	}

	@Override
	public Blob getBlob(String columnLabel) throws SQLException {
		return parent.getBlob(columnLabel);
	}

	@Override
	public Clob getClob(String columnLabel) throws SQLException {
		return parent.getClob(columnLabel);
	}

	@Override
	public Array getArray(String columnLabel) throws SQLException {
		return parent.getArray(columnLabel);
	}

	@Override
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return parent.getDate(columnIndex, cal);
	}

	@Override
	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		return parent.getDate(columnLabel, cal);
	}

	@Override
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return parent.getTime(columnIndex, cal);
	}

	@Override
	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		return parent.getTime(columnLabel, cal);
	}

	@Override
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		return parent.getTimestamp(columnIndex, cal);
	}

	@Override
	public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		return parent.getTimestamp(columnLabel, cal);
	}

	@Override
	public URL getURL(int columnIndex) throws SQLException {
		return parent.getURL(columnIndex);
	}

	@Override
	public URL getURL(String columnLabel) throws SQLException {
		return parent.getURL(columnLabel);
	}

	@Override
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		parent.updateRef(columnIndex, x);
	}

	@Override
	public void updateRef(String columnLabel, Ref x) throws SQLException {
		parent.updateRef(columnLabel, x);
	}

	@Override
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		parent.updateBlob(columnIndex, x);
	}

	@Override
	public void updateBlob(String columnLabel, Blob x) throws SQLException {
		parent.updateBlob(columnLabel, x);
	}

	@Override
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		parent.updateClob(columnIndex, x);
	}

	@Override
	public void updateClob(String columnLabel, Clob x) throws SQLException {
		parent.updateClob(columnLabel, x);
	}

	@Override
	public void updateArray(int columnIndex, Array x) throws SQLException {
		parent.updateArray(columnIndex, x);
	}

	@Override
	public void updateArray(String columnLabel, Array x) throws SQLException {
		parent.updateArray(columnLabel, x);
	}

	@Override
	public RowId getRowId(int columnIndex) throws SQLException {
		return parent.getRowId(columnIndex);
	}

	@Override
	public RowId getRowId(String columnLabel) throws SQLException {
		return parent.getRowId(columnLabel);
	}

	@Override
	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		parent.updateRowId(columnIndex, x);
	}

	@Override
	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		parent.updateRowId(columnLabel, x);
	}

	@Override
	public int getHoldability() throws SQLException {
		return parent.getHoldability();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return parent.isClosed();
	}

	@Override
	public void updateNString(int columnIndex, String nString) throws SQLException {
		parent.updateNString(columnIndex, nString);
	}

	@Override
	public void updateNString(String columnLabel, String nString) throws SQLException {
		parent.updateNString(columnLabel, nString);
	}

	@Override
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		parent.updateNClob(columnIndex, nClob);
	}

	@Override
	public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		parent.updateNClob(columnLabel, nClob);
	}

	@Override
	public NClob getNClob(int columnIndex) throws SQLException {
		return parent.getNClob(columnIndex);
	}

	@Override
	public NClob getNClob(String columnLabel) throws SQLException {
		return parent.getNClob(columnLabel);
	}

	@Override
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		return parent.getSQLXML(columnIndex);
	}

	@Override
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		return parent.getSQLXML(columnLabel);
	}

	@Override
	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		parent.updateSQLXML(columnIndex, xmlObject);
	}

	@Override
	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		parent.updateSQLXML(columnLabel, xmlObject);
	}

	@Override
	public String getNString(int columnIndex) throws SQLException {
		return parent.getNString(columnIndex);
	}

	@Override
	public String getNString(String columnLabel) throws SQLException {
		return parent.getNString(columnLabel);
	}

	@Override
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		return parent.getNCharacterStream(columnIndex);
	}

	@Override
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		return parent.getNCharacterStream(columnLabel);
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		parent.updateNCharacterStream(columnIndex, x, length);
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		parent.updateNCharacterStream(columnLabel, reader, length);
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
		parent.updateAsciiStream(columnIndex, x, length);
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
		parent.updateBinaryStream(columnIndex, x, length);
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		parent.updateCharacterStream(columnIndex, x, length);
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
		parent.updateAsciiStream(columnLabel, x, length);
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
		parent.updateBinaryStream(columnLabel, x, length);
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		parent.updateCharacterStream(columnLabel, reader, length);
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		parent.updateBlob(columnIndex, inputStream, length);
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		parent.updateBlob(columnLabel, inputStream, length);
	}

	@Override
	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		parent.updateClob(columnIndex, reader, length);
	}

	@Override
	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		parent.updateClob(columnLabel, reader, length);
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		parent.updateNClob(columnIndex, reader, length);
	}

	@Override
	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		parent.updateNClob(columnLabel, reader, length);
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
		parent.updateNCharacterStream(columnIndex, x);
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
		parent.updateNCharacterStream(columnLabel, reader);
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
		parent.updateAsciiStream(columnIndex, x);
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
		parent.updateBinaryStream(columnIndex, x);
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
		parent.updateCharacterStream(columnIndex, x);
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
		parent.updateAsciiStream(columnLabel, x);
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
		parent.updateBinaryStream(columnLabel, x);
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
		parent.updateCharacterStream(columnLabel, reader);
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		parent.updateBlob(columnIndex, inputStream);
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		parent.updateBlob(columnLabel, inputStream);
	}

	@Override
	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		parent.updateClob(columnIndex, reader);
	}

	@Override
	public void updateClob(String columnLabel, Reader reader) throws SQLException {
		parent.updateClob(columnLabel, reader);
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		parent.updateNClob(columnIndex, reader);
	}

	@Override
	public void updateNClob(String columnLabel, Reader reader) throws SQLException {
		parent.updateNClob(columnLabel, reader);
	}

	@Override
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		return parent.getObject(columnIndex, type);
	}

	@Override
	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		return parent.getObject(columnLabel, type);
	}

}
