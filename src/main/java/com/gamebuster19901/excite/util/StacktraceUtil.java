package com.gamebuster19901.excite.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.SocketTimeoutException;

public class StacktraceUtil {

	public static String getStackTrace(Throwable t) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		if(!(t instanceof SocketTimeoutException)) {
			t.printStackTrace(printWriter);
			printWriter.write("\n\nThis should not happen, submit a bug report!");
		}
		else {
			printWriter.write("\n\n" + t.getClass().getCanonicalName() + ": " + t.getMessage() + "\n\n");
			printWriter.write("Please wait...");
		}
		return result.toString();
	}
	
}
