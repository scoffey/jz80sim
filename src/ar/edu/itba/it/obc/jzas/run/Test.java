package ar.edu.itba.it.obc.jzas.run;

import java.io.InputStreamReader;

import ar.edu.itba.it.obc.jzas.lexer.JZasLexer;
import ar.edu.itba.it.obc.jzas.lexer.Lexer;
import ar.edu.itba.it.obc.jzas.parser.JZasParser;
import ar.edu.itba.it.obc.jzas.parser.Parser;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Lexer lex = new JZasLexer();
		Parser parser = new JZasParser();
		lex.process(new InputStreamReader(System.in), "io");

		parser.parse(lex);
	}

}
