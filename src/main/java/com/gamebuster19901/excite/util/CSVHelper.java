package com.gamebuster19901.excite.util;

import org.apache.commons.csv.CSVRecord;

public class CSVHelper {

	private final CSVRecord record;
	
	public CSVHelper(CSVRecord record) {
		this.record = record;
	}
	
	public int getInt(Enum<?> e) {
		return Integer.parseInt(record.get(e));
	}
	
	public int getInt(int i) {
		return Integer.parseInt(record.get(i));
	}
	
	public int getInt(String name) {
		return Integer.parseInt(record.get(name));
	}
	
	public short getShort(Enum<?> e) {
		return Short.parseShort(record.get(e));
	}
	
	public short getShort(int i) {
		return Short.parseShort(record.get(i));
	}
	
	public short getShort(String name) {
		return Short.parseShort(record.get(name));
	}
	
	public byte getByte(Enum<?> e) {
		return Byte.parseByte(record.get(e));
	}
	
	public byte getByte(int i) {
		return Byte.parseByte(record.get(i));
	}
	
	public byte getByte(String name) {
		return Byte.parseByte(record.get(name));
	}
	
	public long getLong(Enum<?> e) {
		return Long.parseLong(record.get(e));
	}
	
	public long getLong(int i) {
		return Long.parseLong(record.get(i));
	}
	
	public long getLong(String name) {
		return Long.parseLong(record.get(name));
	}
	
	public String get(Enum<?> e) {
		return record.get(e);
	}
	
	public String get(int i) {
		return record.get(i);
	}
	
	public String get(String name) {
		return record.get(name);
	}
	
	public String getNonNull(Enum<?> e) {
		String ret = get(e);
		if(ret != null) {
			return ret;
		}
		throw new NullPointerException(e.name() + " in record " + record.getRecordNumber());
	}
	
	public String getNonNull(int i) {
		String ret = get(i);
		if(ret != null) {
			return ret;
		}
		throw new NullPointerException("index " + i + " in record " + record.getRecordNumber());
	}
	
	public String getNonNull(String name) {
		String ret = get(name);
		if(ret != null) {
			return ret;
		}
		throw new NullPointerException(name + " in record " + record.getRecordNumber());
	}
	
	public String getNull(Enum<?> e) {
		String ret = get(e);
		if(ret == null) {
			return "";
		}
		return ret;
	}
	
	public String getNull(int i) {
		String ret = get(i);
		if(ret == null) {
			return "";
		}
		return ret;
	}
	
	public String getNull(String name) {
		String ret = get(name);
		if(ret == null) {
			return "";
		}
		return ret;
	}
	
	public long getRecordNumber() {
		return record.getRecordNumber();
	}
	
	public CSVRecord getRecord() {
		return record;
	}
	
	public int size() {
		return record.size();
	}
	
	@Override
	public String toString() {
		return record.toString();
	}
}
