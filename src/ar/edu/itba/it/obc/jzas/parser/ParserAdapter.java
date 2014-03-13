package ar.edu.itba.it.obc.jzas.parser;

import ar.edu.itba.it.obc.jzas.lexer.JZasTokenTypes;
import ar.edu.itba.it.obc.jzas.lexer.Lexer;
import ar.edu.itba.it.obc.jzas.lexer.Token;
import ar.edu.itba.it.obc.jzas.semantic.SemanticAnalyzer;
import ar.edu.itba.it.obc.jzas.util.NumberConversionUtil;
import ar.edu.itba.it.obc.jzas.util.ProcessLogger;

/**
 * Clase que adapta los métodos generados por Byaccj en los parsers a la
 * interfaz Parser de la cátedra. Los parsers generados deben extender de esta
 * clase, que implementa <code>Parser</code>.
 */
public abstract class ParserAdapter implements Parser {

	protected Lexer lexer;
	protected ProcessLogger logger;
	protected SemanticAnalyzer analyzer;

	public void parse(Lexer lex) {
		this.lexer = lex;
		this.yyparseWrapper();
	}

	public SemanticAnalyzer getSemanticAnalyzer() {
		return analyzer;
	}

	public void setSemanticAnalyzer(SemanticAnalyzer analyzer) {
		this.analyzer = analyzer;
	}

	public void setFeedback(ProcessLogger logger) {
		this.logger = logger;
	}

	protected int yylex() {
		Token<JZasTokenTypes> token = lexer.nextToken();
		Object value;
		if (token == null) {
			return 0; /* EOF */
		}

//		System.out.println("TOKEN: " + token);
		value = token.getValue();
		if (value instanceof String)
			setYylval(new String((String) value));
		else
			setYylval(token.getValue() == null ? null : token.getValue().toString());

		return token.getType().getValue();
	}

	protected void yyerror(String error) {
		logger.showError(getFileName(), line(), column(), error);
	}

	public String getFileName() {
		return lexer.getFileName();
	}

	public int line() {
		return lexer.line() + 1;
	}

	public int column() {
		return lexer.column();
	}
	
	public void setLine(int line){
		lexer.setLine(line);
	}
	
	public void setColumn(int column){
		lexer.setColumn(column);
	}


	protected abstract void setYylval(String parserVal);

	protected abstract int yyparseWrapper();

	protected int parseChar(String ch) {
		return ch.charAt(1);
	}

	protected String getRSTPageZeroOffset(int t) {
		switch (t) {
		case 0:
			return "000";
		case 8:
			return "001";
		case 16:
			return "010";
		case 24:
			return "011";
		case 32:
			return "100";
		case 40:
			return "101";
		case 48:
			return "110";
		case 56:
			return "111";
		default:
			return "";
		}
	}

	/* Wrappers definidos para simplificar la gramatica */
	protected String mostSignificativeHex(Integer i) {
		return NumberConversionUtil.mostSignificativeHex(i);
	}

	protected String leastSignificativeHex(Integer i) {
		return NumberConversionUtil.leastSignificativeHex(i);
	}

	protected String mostSignificativeBin(Integer i) {
		return NumberConversionUtil.mostSignificativeBin(i);
	}

	protected String leastSignificativeBin(Integer i) {
		return NumberConversionUtil.leastSignificativeBin(i);
	}

	protected int relativeAddress(Integer address) {
		return address - analyzer.getNextAddress();
	}

	protected String littleEndianBin(int i) {
		return NumberConversionUtil.littleEndianBin(i);
	}

	protected String littleEndianHex(int i) {
		return NumberConversionUtil.littleEndianHex(i);
	}

	protected String bin2Hex(String bin) {
		return NumberConversionUtil.bin2Hex(bin);
	}

	protected String toBinaryString(int bin, int size) {
		return NumberConversionUtil.toBin(bin, size);
	}

	protected String toBin(int bin, int size) {
		return NumberConversionUtil.toBin(bin, size);
	}
}
