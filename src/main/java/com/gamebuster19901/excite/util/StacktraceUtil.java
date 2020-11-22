package com.gamebuster19901.excite.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class StacktraceUtil {

	public static String getStackTrace(Throwable t) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		t.printStackTrace(printWriter);
		printWriter.write("\n\nThis should not happen, submit a bug report!");
		return result.toString();
	}
	
}
