package ar.edu.itba.it.obc.jzas.util;

import java.io.PrintStream;

/**
 * Simple process logger that emits messages to the console
 */
public class ConsoleProcessLogger implements ProcessLogger {

	// TODO: borrar estos errores
	// /* Errores lexicos */
	// public static final String INVALID_INTEGER = "Invalid integer: ";
	// public static final String INVALID_REAL = "Invalid real: ";
	// public static final String INVALID_IDENTIFIER = "Invalid identifier: ";
	// public static final String UNEXPECTEDLY_CLOSED_COMMENT = "Unexpectedly
	// closed comment.";
	// public static final String UNEXPECTED_CHARACTER = "Unexpected character:
	// ";
	// public static final String UNTERMINATED_COMMENT = "Unterminated comment
	// ";
	//
	// /* Mensajes de error para los contextos definidos */
	// public static final String FUNCTION_DECLARATION_TO_EXPECTED = "'->'
	// expected";
	// public static final String TYPES_LIST_TYPE_EXPECTED = "Extra ',' or type
	// expected";
	// public static final String EXPRESSION_LIST_EXPRESSION_EXPECTED = "Extra
	// ',' or expression expected";
	// public static final String EXPRESSION_INVALID_EXPRESSION = "Invalid
	// expression";
	// public static final String OUTPUT_PARAMS_EXPECTED_PARAMS_LIST =
	// "Unnecesary token '->' or otuput parameters list expected";
	// public static final String TYPE_DECLARATION_EXPECTED_TYPES_LIST =
	// "Expected types list";
	// public static final String CONSTRAINT_DECLARATION_EXPECTED_EXPRESSION =
	// "Expected return expression";
	// public static final String VAR_DECLARATION_VAR_TYPE = "Variable type
	// error";
	// public static final String ASSIGN_INVALID_LVALUE = "Invalid lvalue";
	// public static final String BLOCK_QUIT_INVOCATION = "Cannot invoke quit on
	// this block";
	// public static final String USE_INVALID_MODULE = "Invalid module in use";
	// public static final String FUNCTION_DECLARATION_EXPECTED_COLON =
	// "Expected ':' in function declaration";
	// public static final String VAR_DECLARATION_EXPECTED_SEMICOLON = "Expected
	// ';' in variable declaration";
	// public static final String FUNCTION_DECLARATION_INVALID = "Error in
	// function declaration";
	// public static final String INVALID_INSTRUCTION_IN_GLOBAL_CONTEXT =
	// "Instruction may only be inside a block.";
	//
	// public static final String EXPRESSION_INVALID_PLUS = "Syntax error in sum
	// after token '+'";
	// public static final String EXPRESSION_INVALID_MINUS = "Syntax error in
	// substraction after token '-'";
	// public static final String EXPRESSION_INVALID_TIMES = "Syntax error in
	// product after token '*'";
	// public static final String EXPRESSION_INVALID_DIVIDE = "Syntax error in
	// division after token '/'";
	// public static final String EXPRESSION_INVALID_MODULE = "Syntax error in
	// modulus after token '%'";
	// public static final String EXPRESSION_INVALID_AND = "Syntax error in
	// logical operation after token 'and'";
	// public static final String EXPRESSION_INVALID_OR = "Syntax error in
	// logical operation after token 'or'";
	// public static final String EXPRESSION_INVALID_REL = "Syntax error in
	// logical operation after relational operator";
	//
	// public static final String RECURSIVE_INCLUSION = "Recursive inclusion of
	// module: ";
	// public static final String EXPS_INVALID_LIST = "Invalid expression
	// list.";
	//
	// /* Errores generales */
	// public static final String INVALID_INSTRUCTION = "Error in instruction";
	// public static final String INVALID_INSTRUCTION_IN_CURRENT_CONTEXT =
	// "invalid instruction in current context";
	// public static final String UNEXPECTED_TOKEN = "Unexpected token";
	// public static final String EXPECTED_CLOSE_BRACE = "Expected '}'";
	// public static final String EXPECTED_SEMICOLON = "Expected ';'";
	// public static final String EXPECTED_ASSIGN = "Expected '='";

	private void print(PrintStream out, String type, String fileName, int line, int column, String message) {
		out.println(fileName + ": line " + line + " col " + column + ": " + type + ": " + message);
	}

	// @Override
	public void showError(String fileName, int line, int column, String description) {
		print(System.err, "error", fileName, line, column, description);
	}

	// @Override
	public void showInfo(String fileName, int line, int column, String description) {
		print(System.err, "info", fileName, line, column, description);
	}

	// @Override
	public void showWarning(String fileName, int line, int column, String description) {
		print(System.err, "warn", fileName, line, column, description);
	}

	// @Override
	public void showError(String description) {
		System.err.println(description);
	}

}
