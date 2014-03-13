package ar.edu.itba.it.obc.jzas.lexer;

import java.io.Reader;

import ar.edu.itba.it.obc.jzas.util.ProvidesFeedback;

public interface Lexer extends ProvidesFeedback {
	/**
	 * Switches processing to the given input. If there was a previous input, it
	 * will be silently discarded and the next token will be retrieved from the
	 * new stream.
	 * 
	 * @param input
	 */
	public void process(Reader input, String fileName);

	/**
	 * Parses input and retrieves the next token
	 * 
	 * @return The next token or null if end of file has been found
	 * @throws IllegalStateException
	 *             if there is no stream to process, or the last stream has
	 *             already ended
	 */
	public Token<JZasTokenTypes> nextToken() throws IllegalStateException;

	public int line();

	public int column();
	
	public void setLine(int line);
	
	public void setColumn(int column);

	public String getFileName();

}
