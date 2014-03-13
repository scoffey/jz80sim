package ar.edu.itba.it.obc.jzas.lexer;

import java.io.IOException;
import java.io.Reader;

public abstract class JZasLexerAdapter {

	// protected ProcessLogger logger;
	protected String fileName;
	protected String finalText;

	public JZasToken<JZasTokenTypes> nextToken() throws IllegalStateException {
		JZasTokenTypes tokenType;

		finalText = null;

		try {
			if ((tokenType = this.yylex()) == null) {
				return null;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new JZasToken<JZasTokenTypes>(tokenType,
				finalText == null ? null : new String(finalText));
	}

	public void process(Reader input, String fileName) {
		this.yyreset(input);
		this.fileName = fileName;
	}

	// public void setFeedback(ProcessLogger logger) {
	// this.logger = logger;
	// }

	public String getFileName() {
		return fileName;
	}

	// public ProcessLogger getFeedback() {
	// return logger;
	// }

	public abstract JZasTokenTypes yylex() throws java.io.IOException;

	public abstract String yytext();

	public abstract void yyreset(java.io.Reader reader);

}
