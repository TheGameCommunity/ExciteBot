package com.gamebuster19901.excite.bot.database;

import java.sql.SQLException;
import java.util.ArrayList;

import com.gamebuster19901.excite.bot.database.sql.PreparedStatement;

public class Comparison {
	
	private final Column column;
	private final Comparator comparator;
	private final Object value;
	private String string;
	private ArrayList<Comparison> sequence = new ArrayList<Comparison>();
	private int offset;
	
	public Comparison(Column column, Comparator comparator, Object value) {
		this.column = column;
		this.comparator = comparator;
		this.value = value;
		this.string = toString();
		sequence.add(this);
	}
	
	public Comparison(Column column, Comparator comparator) {
		this.column = column;
		this.comparator = comparator;
		this.value = null;
	}
	
	public String toString() {
		if(string == null) {
			if(value != null) {
				return column + " " + comparator + " ? ";
			}
			return column + " " + comparator + " ";
		}
		return string;
	}
	
	public Object getColumn() {
		return column;
	}
	
	public Object getValue() {
		return value;
	}
	
	public Object getValue(int index) {
		return sequence.get(index - 1 - offset).getValue();
	}
	
	public int offset(int amount) {
		offset = offset + amount;
		return offset;
	}
	
	public Comparison or(Comparison comparison) {
		string = string + " OR " + comparison;
		sequence.add(comparison);
		return this;
	}
	
	public Comparison and(Comparison comparison) {
		string = string + " AND " + comparison;
		sequence.add(comparison);
		return this;
	}
	
	public Comparison openBeginning() {
		string = "(" + string;
		return this;
	}
	
	public Comparison open(Comparison comparison) {
		string = string + " ( " + comparison;
		sequence.add(comparison);
		return this;
	}
	
	public Comparison close() {
		string = string + " ) ";
		return this;
	}
	
	public Comparison insertValues(PreparedStatement ps) throws SQLException {
		return insertValues(ps, 1);
	}
	
	public Comparison insertValues(PreparedStatement ps, int startIndex) throws SQLException {
		int i = startIndex;
		for(Comparison c : sequence) {
			if(this.getValue(i) != null) {
				Table.insertValue(ps, i, this.getValue(i++));
				System.out.println();
			}
		}
		return this;
	}
	
}
