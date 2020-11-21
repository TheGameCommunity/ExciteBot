package com.gamebuster19901.excite.bot.database;

public enum Comparator {
	EQUALS("="),
	NOT_EQUAL_TO("!="),
	LESS_THAN("<"),
	LESS_THAN_OR_EQUAL_TO("<="),
	LIKE("LIKE"),
	GREATER_THAN(">"),
	GREATER_THAN_OR_EQUAL_TO(">="),
	IS_TRUE("IS TRUE"),
	IS_FALSE("IS FALSE"),
	IS_NULL("IS NULL"),
	IS_NOT_NULL("IS NOT NULL");
	
	private final String mysql;
	
	private Comparator(String mysql) {
		this.mysql = mysql;
	}
	
	@Override
	public String toString() {
		return mysql;
	}
}
