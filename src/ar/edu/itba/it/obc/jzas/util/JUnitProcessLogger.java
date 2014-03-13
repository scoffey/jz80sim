package ar.edu.itba.it.obc.jzas.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple process logger that emits messages to the console
 */
public class JUnitProcessLogger implements ProcessLogger {

	public static String ERROR_MESAGE01 = "Incorrect input. Truncating input";
	public static String ERROR_MESAGE02 = "Incorrect input. Truncating input";
	public static String ERROR_MESAGE03 = "Incorrect input. Truncating input";
	public static String ERROR_MESAGE04 = "Incorrect input. Truncating input";
	public static String ERROR_MESAGE05 = "Incorrect input. Auto completing comment";
	public static String ERROR_MESAGE06 = "Incorrect input. Ignoring input";

	private List<String> messages;

	public JUnitProcessLogger() {
		this.messages = new ArrayList<String>();
	}

	private String print(PrintStream out, String type, String fileName, int line, int column, String message) {
		if (fileName == null) {
			fileName = "";
		}
		if (fileName.length() > 0) {
			fileName = fileName + ": ";
		}
		String ordinalStr = "";
		if (line > 0) {
			ordinalStr = Integer.toString(line) + ": ";
		}
		return new String(type + fileName + ordinalStr + message);
	}

	// @Override
	public void showError(String fileName, int line, int column, String description) {
		print(System.err, "Error", fileName, line, column, description);
	}

	// @Override
	public void showInfo(String fileName, int line, int column, String description) {
		print(System.err, "Info", fileName, line, column, description);
	}

	// @Override
	public void showWarning(String fileName, int line, int column, String description) {
		print(System.err, "Warn", fileName, line, column, description);
	}

	public List<String> getMessages() {
		return messages;
	}

	// @Override
	public void showError(String description) {
		System.err.println(description);
	}
}
