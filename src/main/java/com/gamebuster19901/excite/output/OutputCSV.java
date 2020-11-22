package com.gamebuster19901.excite.output;

import java.io.IOException;

public interface OutputCSV {

	public static final String REGEX_SPLITTER = "(?:,\"|^\")(\"\"|[\\w\\W]*?)(?=\",|\"$)|(?:,(?!\")|^(?!\"))([^,]*?)(?=$|,)|(\\r\\n|\\n)";
	
	public String toCSV() throws IOException;
	
}
