package com.gamebuster19901.excite.output;

public interface OutputCSV {

	public static final String REGEX_SPLITTER = "(?:,\"|^\")(\"\"|[\\w\\W]*?)(?=\",|\"$)|(?:,(?!\")|^(?!\"))([^,]*?)(?=$|,)|(\\r\\n|\\n)";
	
	public String toCSV();
	
}
