package ar.edu.itba.it.obc.jzas.util;

/**
 * Interface implemented by clases that are capable of showing or storing
 * process information.
 * <p>
 * Process feedback may have a context, which may mean a stage of the process;
 * an ordinal, used to track a particular job item on batches, and a
 * descripction
 * </p>
 * <p>
 * Both context and ordinals are optionals, and may be omitted (null for
 * context, 0 for ordinal)
 * </p>
 */
public interface ProcessLogger {
	/**
	 * Emits an error message
	 * 
	 * @param fileName Name of the source file being parsed.
	 * @param line Line to which the error belongs.
	 * @param column Column to which the error belongs.
	 * @param description The message of the error.
	 */
	public void showError(String fileName, int line, int column, String description);

	public void showError(String description);

	/**
	 * Emits an warning message
	 * 
	 * @param fileName Name of the source file being parsed.
	 * @param line Line to which the error belongs.
	 * @param column Column to which the error belongs.
	 * @param description The message of the error.
	 */
	public void showWarning(String fileName, int line, int column, String description);

	/**
	 * Emits an informational message
	 * 
	 * @param fileName Name of the source file being parsed.
	 * @param line Line to which the error belongs.
	 * @param column Column to which the error belongs.
	 * @param description The message of the error.
	 */
	public void showInfo(String fileName, int line, int column, String description);

}
