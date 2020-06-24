package com.gamebuster19901.excite.bot.ban;

import java.util.List;

import org.apache.commons.csv.CSVRecord;

public interface NotBanned {
	
	public default Ban parseVerdict(CSVRecord record) {
		throw new AssertionError();
	}
	
	public default List<Object> getParameters() {
		throw new AssertionError();
	}

}
