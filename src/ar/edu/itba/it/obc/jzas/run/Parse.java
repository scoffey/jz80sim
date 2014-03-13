package ar.edu.itba.it.obc.jzas.run;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import ar.edu.itba.it.obc.jzas.lexer.Lexer;
import ar.edu.itba.it.obc.jzas.parser.Parser;
import ar.edu.itba.it.obc.jzas.util.ConsoleProcessLogger;
import ar.edu.itba.it.obc.jzas.util.ReflectionUtils;

/**
 * Parses a file through a given parser
 * 
 * @param args -
 *            args[0] should contain the class that implements the lexical
 *            analyzer args[1] should contain the class that implements the
 *            parser args[2] should contain the file to parse
 */
public class Parse {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			info();
			return;
		}
		Lexer lex = ReflectionUtils.createInstance(args[0]);
		if (lex == null) {
			return;
		}
		Parser parser = ReflectionUtils.createInstance(args[1]);
		if (lex == null) {
			return;
		}

		Reader in;
		try {
			in = new FileReader(args[2]);
		} catch (FileNotFoundException e) {
			System.err.println("File " + args[2] + " not found");
			return;
		}

		lex.setFeedback(new ConsoleProcessLogger());
		parser.setFeedback(new ConsoleProcessLogger());
		lex.process(in, args[2]);

		parser.parse(lex);
	}

	private static void info() {
		System.out.println("Parser");
		System.out
				.println("syntax: Parse <lexer class> <parser class> <file to be parsed>");
	}
}
