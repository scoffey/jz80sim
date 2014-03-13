package ar.edu.itba.it.obc.jzas.parser;

import ar.edu.itba.it.obc.jzas.lexer.Lexer;
import ar.edu.itba.it.obc.jzas.util.ProvidesFeedback;

/**
 * Interface implemented by parsers
 */
public interface Parser extends ProvidesFeedback {

	/**
	 * Parses input provided by a given lexical analyzer
	 * 
	 * @param lex
	 *            the lexical analyzer
	 */
	public void parse(Lexer lex);

}
