package ar.edu.itba.it.obc.jzas.semantic;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.List;
import java.util.Stack;

import ar.edu.itba.it.obc.jzas.parser.JZasReader;
import ar.edu.itba.it.obc.jzas.parser.Observer;
import ar.edu.itba.it.obc.jzas.parser.ParserAdapter;
import ar.edu.itba.it.obc.jzas.run.Compiler;
import ar.edu.itba.it.obc.jzas.run.Compiler.CompilerModes;
import ar.edu.itba.it.obc.jzas.semantic.nodes.IntExpression;
import ar.edu.itba.it.obc.jzas.semantic.symbols.MacroSymbol;
import ar.edu.itba.it.obc.jzas.semantic.symbols.RotuleAliasSymbol;
import ar.edu.itba.it.obc.jzas.semantic.symbols.RotuleSymbol;
import ar.edu.itba.it.obc.jzas.semantic.symbols.Symbol;
import ar.edu.itba.it.obc.jzas.util.NumberConversionUtil;
import ar.edu.itba.it.obc.jzas.util.ProcessLogger;

/**
 * Clase abstracta de la cual debe extender cualquier clase que quiera agregar
 * acciones a los métodos callback que se invocan a medida que se parsea el
 * archivo de entrada. En este caso, las dos implementaciones son
 * <code>SymbolTableBuilder</code>, que genera la tabla de símbolos con los
 * símbolos de nivel superior, y <code>CodeGenerator</code> que genera código
 * intermedio y completa la tabla de símbolos.
 */
public abstract class SemanticAnalyzer implements Observer {

	protected ParserAdapter parser;
	protected ProcessLogger logger;
	protected boolean reportedErrors = false;
	protected boolean reportedWarnings = false;
	protected Stack<SymbolTable> symbolTables;

	/*
	 * Línea y columna en que se invoca una macro (en caso de que la
	 * macro-expansión de dicha macro genere más macro--expansiones, esta se
	 * mantiene en la menor profunda (la primera!)).
	 */
	private int leastDeepMacroInvocationLine;
	private int leastDeepMacroInvocationColumn;

	protected int asegnextAddress;
	protected int csegnextAddress;
	protected int dsegnextAddress;

	public enum Segment {
		ASEG, CSEG, DSEG;

		public String toString() {
			if (this.equals(ASEG)) {
				return "ASEG";
			} else if (this.equals(CSEG)) {
				return "CSEG";
			} else {
				return "DSEG";
			}
		}
	};

	protected Segment currentSegment;

	/**
	 * Registros del Z80
	 */
	public enum Register {
		B(0x0), C(0x1), D(0x2), E(0x3), H(0x4), L(0x5), A(0x7), BC(0x0), DE(0x1), HL(
				0x2), IX(0x2), IY(0x2), SP(0x3), AF(0x3);
		private int type;

		private Register(int type) {
			this.type = type;
		}

		public String getCodification() {
			return NumberConversionUtil.toBin(type, 3);
		}
	}

	/**
	 * Flags del Z80
	 */
	public enum Flag {
		NZ(0x0), Z(0X1), NC(0X2), C(0X3), PO(0X4), PE(0X5), P(0X6), M(0X7);

		private int type;

		private Flag(int type) {
			this.type = type;
		}

		public String getCodification() {
			return NumberConversionUtil.toBin(type, 3);
		}
	}

	/**
	 * Operadores matemáticos binarios.
	 */
	public enum MathOperator {
		PLUS, MINUS, TIMES, DIVIDE
	};

	public SemanticAnalyzer(ParserAdapter parser, ProcessLogger logger) {
		this.parser = parser;
		this.logger = logger;
		this.symbolTables = new Stack<SymbolTable>();
		this.symbolTables.push(new SymbolTable());

		asegnextAddress = 0;
		csegnextAddress = 0;
		dsegnextAddress = 0;
		currentSegment = Segment.ASEG;

		leastDeepMacroInvocationLine = -1;
		leastDeepMacroInvocationColumn = -1;
	}

	public void setSymbolTable(SymbolTable symbolTable) {
		this.symbolTables = new Stack<SymbolTable>();
		this.symbolTables.push(symbolTable);
	}

	public SymbolTable getSymbolTable() {
		return symbolTables.peek();
	}

	public boolean hasReportedErrors() {
		return reportedErrors;
	}

	public void reportError(String message) {
		reportedErrors = true;
		if (Compiler.getInstance().getMode()
				.equals(CompilerModes.MODE_SEMANTIC)
				|| Compiler.getInstance().getMode().equals(
						CompilerModes.MODE_OBJECTIVE_CODE)
				|| Compiler.getInstance().getMode().equals(
						CompilerModes.MODE_SYMBOL_TABLE)
				|| Compiler.getInstance().getMode().equals(
						CompilerModes.MODE_LINK)
				|| Compiler.getInstance().getMode().equals(
						CompilerModes.MODE_SINTACTIC)) {
			/*
			 * Si el error se produjo en una macro-expansión indicar en que
			 * línea se invocó a la macro; sino, informar la línea del error
			 */
			if (leastDeepMacroInvocationLine == -1) {
				logger.showError(parser.getFileName(), parser.line(), parser
						.column(), message);
			} else {
				logger.showError(parser.getFileName(),
						leastDeepMacroInvocationLine,
						leastDeepMacroInvocationColumn, message);
			}
		}
	}

	public void reportWarning(String message) {
		reportedWarnings = true;
		if (Compiler.getInstance().showWarnings()
				&& (Compiler.getInstance().getMode().equals(
						CompilerModes.MODE_SEMANTIC)
						|| Compiler.getInstance().getMode().equals(
								CompilerModes.MODE_OBJECTIVE_CODE)
						|| Compiler.getInstance().getMode().equals(
								CompilerModes.MODE_LINK)
						|| Compiler.getInstance().getMode().equals(
								CompilerModes.MODE_SYMBOL_TABLE) || Compiler
						.getInstance().getMode().equals(
								CompilerModes.MODE_SINTACTIC))) {
			logger.showWarning(parser.getFileName(), parser.line(), parser
					.column(), message);
		}

	}

	/***************************************************************************
	 * Handlers de la gramática.
	 **************************************************************************/
	/**
	 * Dada una instrucción, avanza la próxima dirección del segmento actual la
	 * cantidad determinada por la longitud en bytes de la instrucción en
	 * cuestión.
	 * 
	 * @return La misma instrucción que recibe como parámetro.
	 */
	public OCRecord newInstruction(OCRecord instruction) {
		addToCurrentAddress(instruction.getSize());
		return instruction;
	}

	/**
	 * Establece como segmento actual al absoluto.
	 */
	public void directiveAseg() {
		currentSegment = Segment.ASEG;
	}

	/**
	 * Establece como segmento actual al de código.
	 */
	public void directiveCseg() {
		currentSegment = Segment.CSEG;
	}

	/**
	 * Establece como segmento actual al de datos.
	 */
	public void directiveDseg() {
		currentSegment = Segment.DSEG;
	}

	/**
	 * Setea la proxima dirección del segmento actual a la indicada por el
	 * parámetro.
	 * 
	 * @param Address
	 */
	public void directiveOrg(Integer Address) {
		setNextAddress(Address);
	}

	public abstract void pgmInstructionLine(OCRecord instruction);

	public MacroSymbol invokeMacro(String macroName, List<String> params) {
		/* Construir la tabla de símbolos interna de la macro */
		MacroSymbol macroSymbol = getMacroSymbol(macroName);
		if (macroSymbol == null) {
			reportError("Identifier " + macroName
					+ " undefined (expecting macro symbol).");
			return null;
		}

		/* Validar la cantidad de parametros */
		if (macroSymbol.getInputParams().size() != params.size()) {
			reportError("Actual params size dont match formal params size for macro "
					+ macroName);
			return null;
		}

		/*
		 * Reemplazar los parámetros todas las ocurrencias de parametros
		 * formales con los reales
		 */
		String realSource = replaceMacroParameter(macroSymbol, params);

		/*
		 * Mantener referencia a la línea en que se invoca a la macro (para
		 * reportar errores)
		 */
		if (leastDeepMacroInvocationLine == -1) {
			leastDeepMacroInvocationLine = parser.line() - 1;
			leastDeepMacroInvocationColumn = parser.column();
		}
		/*
		 * Cambiar el origen de datos del reader y apilar la tabla de simbolos
		 * de la macro para que sea completada
		 */
		JZasReader reader = (JZasReader) Compiler.getInstance().getReader();
		reader.pushReader(new StringReader(realSource));
		return macroSymbol;
	}

	public abstract OCRecord instruction(String rotule, OCRecord instruction);

	public abstract String declareRotule(String rotule);

	public abstract void directiveEqu(String rotule, String substitute);

	public abstract void directiveDefl(String rotule, String substitute);

	public abstract void defineMacro(String macroName, List<String> params,
			String instructions);

	public abstract void directiveDefb(String rotule, List<Integer> exps);

	public abstract void directiveDefw(String rotule, List<Integer> exps);

	public abstract void directiveDefm(String rotule, String message);

	public abstract void directiveDefs(String rotule, Integer size);

	public abstract void directiveDeff(String rotule, List<Float> exps);

	public abstract void directiveEnd(String rotule);

	// public abstract void directiveLocal(String lovalRotule);

	public abstract void directivePublic(List<String> publicRotules);

	public void directiveInclude(List<String> rotules) {
		for (String filename : rotules) {
			FileReader fileReader = null;
			String path = Compiler.getInstance().getCurrentFilename();
			path = path.substring(0, path.lastIndexOf('/') + 1) + filename;
			try {
				fileReader = new FileReader(path);
			} catch (FileNotFoundException e) {
				reportError("File not found: " + path);
				continue;
			}

			/* TODO: linea y columna para reportar errores! */

			/* Cambiar el origen de datos del reader */
			JZasReader reader = (JZasReader) Compiler.getInstance().getReader();
			reader.pushReader(fileReader);
		}

	}

	public abstract IntExpression rotuleExpression(String rotule);

	public abstract Double rotuleAliasExpression(String rotule);

	public abstract Double MathExpression(Double num);

	public abstract Double MathExpression(MathOperator operator, Double num1,
			Double num2);

	public abstract IntExpression MathExpression(IntExpression num);

	public abstract IntExpression MathExpression(MathOperator operator,
			IntExpression num1, IntExpression num2);

	public abstract void directiveExtern(List<String> rotules);

	public abstract void saveAddressSegmentsInfo(Symbol rotule);

	/**
	 * Reemplaza las apariciones de los parámetros formales por los parámetros
	 * reales.
	 * 
	 * @param macroSymbol
	 *            MacroSymbol correspondiente a la macro que se quiere
	 *            macro-expander.
	 * @param params
	 *            Parámetros actuales.
	 * @return String con el código reemplazado.
	 */
	protected String replaceMacroParameter(MacroSymbol macroSymbol,
			List<String> params) {
		String ret = new String(macroSymbol.getCode());

		for (int i = 0; i < macroSymbol.getInputParams().size(); i++) {
			String formalParam = macroSymbol.getInputParams().get(i);
			String actualParam = params.get(i);
			ret = ret.replaceAll("([^a-zA-Z0-9]|^)" + formalParam
					+ "([^a-zA-Z0-9]|$)", "$1" + actualParam + "$2");
		}

		return ret;
	}

	public IntExpression dollarSignExpression() {
		return new IntExpression(getNextAddress(), new RotuleSymbol("$",
				getNextAddress(), currentSegment));
	}

	public int solveRotule(String rotuleName) {
		RotuleSymbol rotule = getRotuleSymbol(rotuleName);
		if (rotule == null) {
			reportError("Rotule " + rotuleName + " was not defined");
			return -1;
		}
		return rotule.getAddress();
	}

	public int getNextAddress() {
		switch (currentSegment) {
		case ASEG:
			return asegnextAddress;
		case CSEG:
			return csegnextAddress;
		case DSEG:
			return dsegnextAddress;
		default:
			return -1;
		}
	}

	protected void addToCurrentAddress(int byteCount) {
		switch (currentSegment) {
		case ASEG:
			asegnextAddress += byteCount;
		case CSEG:
			csegnextAddress += byteCount;
		case DSEG:
			dsegnextAddress += byteCount;
		}
	}

	protected void setNextAddress(Integer address) {
		switch (currentSegment) {
		case ASEG:
			asegnextAddress = address;
		case CSEG:
			csegnextAddress = address;
		case DSEG:
			dsegnextAddress = address;
		}
	}

	public void setCurrentSegment(Segment segment) {
		this.currentSegment = segment;
	}

	public void setAsegNextAddress(int address) {
		this.asegnextAddress = address;
	}

	public void setCsegNextAddress(int address) {
		this.asegnextAddress = address;
	}

	public void setDsegNextAddress(int address) {
		this.asegnextAddress = address;
	}

	/***************************************************************************
	 * Operaciones del Stack de tabla de símbolos (Wrappers de las operaciones
	 * de la tabla de símbolo que trabajan sobre todo el stack).
	 **************************************************************************/

	protected void addSymbol(Symbol s) throws DuplicatedSymbolException {
		for (SymbolTable symbolTable : symbolTables) {
			if (symbolTable.getSymbol(s.getId()) != null) {
				throw new DuplicatedSymbolException();
			}
		}
		symbolTables.peek().addSymbol(s);
	}

	protected MacroSymbol getMacroSymbol(String macroName) {
		Stack<SymbolTable> aux = new Stack<SymbolTable>();
		MacroSymbol ret = null;
		while (!symbolTables.isEmpty() && ret == null) {
			SymbolTable current = symbolTables.pop();
			ret = current.getMacroSymbol(macroName);
			aux.push(current);
		}

		while (!aux.isEmpty()) {
			symbolTables.push(aux.pop());
		}

		return ret;
	}

	protected RotuleSymbol getRotuleSymbol(String rotule) {
		Stack<SymbolTable> aux = new Stack<SymbolTable>();
		RotuleSymbol ret = null;
		while (!symbolTables.isEmpty() && ret == null) {
			SymbolTable current = symbolTables.pop();
			ret = current.getRotuleSymbol(rotule);
			aux.push(current);
		}

		while (!aux.isEmpty()) {
			symbolTables.push(aux.pop());
		}

		return ret;
	}

	protected RotuleAliasSymbol getRotuleAliasSymbol(String rotule) {
		Stack<SymbolTable> aux = new Stack<SymbolTable>();
		RotuleAliasSymbol ret = null;
		while (!symbolTables.isEmpty() && ret == null) {
			SymbolTable current = symbolTables.pop();
			ret = current.getRotuleAliasSymbol(rotule);
			aux.push(current);
		}

		while (!aux.isEmpty()) {
			symbolTables.push(aux.pop());
		}

		return ret;
	}

	protected Symbol getSymbol(String rotule) {
		Stack<SymbolTable> aux = new Stack<SymbolTable>();
		Symbol ret = null;
		while (!symbolTables.isEmpty() && ret == null) {
			SymbolTable current = symbolTables.pop();
			ret = current.getSymbol(rotule);
			aux.push(current);
		}

		while (!aux.isEmpty()) {
			symbolTables.push(aux.pop());
		}

		return ret;
	}

	protected boolean removeSymbol(String rotule) {
		Stack<SymbolTable> aux = new Stack<SymbolTable>();
		boolean mustContinue = true;
		while (!symbolTables.isEmpty() && mustContinue) {
			SymbolTable current = symbolTables.pop();
			if (current.removeSymbol(rotule)) {
				mustContinue = false;
			}
			aux.push(current);
		}

		while (!aux.isEmpty()) {
			symbolTables.push(aux.pop());
		}

		return !mustContinue;
	}

	/***************************************************************************
	 * Fin de los Wrappers para el Stack de tablas de símbolos
	 **************************************************************************/

	/**
	 * El JZasFileReader me notifica que finalizo uno de sus sources (fin de una
	 * macro!). Se debe desapilar la tabla de símbolos que estaba en el tope del
	 * Stack.
	 */
	public void update(Object object) {
		if (!(object instanceof FileReader)) {
			symbolTables.pop();
			/*
			 * Si volvi a procesar el archivo principal, restaurar la línea y
			 * columna (que se vio afectada por la macro-expansión).
			 */
			if (symbolTables.size() == 1) {
				parser.setLine(leastDeepMacroInvocationLine);
				parser.setColumn(leastDeepMacroInvocationColumn);
				leastDeepMacroInvocationLine = -1;
				leastDeepMacroInvocationColumn = -1;
			}
		}
	}
}
