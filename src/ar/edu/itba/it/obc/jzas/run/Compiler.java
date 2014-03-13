package ar.edu.itba.it.obc.jzas.run;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import ar.edu.itba.it.obc.jzas.lexer.JZasLexer;
import ar.edu.itba.it.obc.jzas.lexer.JZasTokenTypes;
import ar.edu.itba.it.obc.jzas.lexer.Lexer;
import ar.edu.itba.it.obc.jzas.lexer.Token;
import ar.edu.itba.it.obc.jzas.linker.Linker;
import ar.edu.itba.it.obc.jzas.parser.JZasParser;
import ar.edu.itba.it.obc.jzas.parser.JZasReader;
import ar.edu.itba.it.obc.jzas.parser.ParserAdapter;
import ar.edu.itba.it.obc.jzas.semantic.CodeGenerator;
import ar.edu.itba.it.obc.jzas.semantic.MacroDefinitionInspector;
import ar.edu.itba.it.obc.jzas.semantic.SemanticAnalyzer;
import ar.edu.itba.it.obc.jzas.semantic.SymbolTableBuilder;
import ar.edu.itba.it.obc.jzas.util.ConsoleProcessLogger;
import ar.edu.itba.it.obc.jzas.util.ProcessLogger;

/**
 * El compilador. Se lo instancia con un lexer, un parser y un nombre de archivo
 * a compilar. El compilador se maneja como un singleton, se lo puede crear a
 * través del método estático <code>createCompiler</code>,y luego utilizarlo
 * a través de <code>getInstance</code>.
 */
public class Compiler {

	private Set<String> inputFiles;
	private CompilerModes mode;
	private ProcessLogger logger;
	private String workingDirectory = "";
	private CompilerInputFile currentFile;
	private boolean showWarnings;
	private Reader in;
	private String preffix;

	private static int csegStart;
	private static int dsegStart;

	private static Compiler me = new Compiler(); /* singleton */

	/**
	 * Modos en los que puede ejecutarse el compilador. Lexer realiza solamente
	 * el análisis léxico, imprimiendo los tokens por salida estándar. Parser
	 * hace el análisis léxico y sintáctico. Dependencies hace todo lo anterior,
	 * y además imprime por salida estándar las dependencias entre los archivos.
	 */
	public enum CompilerModes {
		MODE_LEXER, MODE_SINTACTIC, MODE_SEMANTIC, MODE_SYMBOL_TABLE, MODE_OBJECTIVE_CODE, MODE_LINK
	};

	/**
	 * Punto de entrada de la aplicación.
	 */
	public static void main(String[] args) {
		Compiler compiler = Compiler.getInstance();
		compiler.logger = new ConsoleProcessLogger();

		try {
			List<String> fileNames = setCompilerMode(args);
			if (fileNames == null) {
				return;
			}
			if (compiler.mode == CompilerModes.MODE_LEXER) {
				compiler.dumpTokens(fileNames.get(0));
			} else if (compiler.mode == CompilerModes.MODE_LINK) {
				int csegStart = 0, dsegStart = 0;
				compiler.compileFile(fileNames, csegStart, dsegStart);
			} else {
				compiler.compileFile(fileNames, 0, 0);
			}
		} catch (IllegalArgumentException e) {
			// e.printStackTrace();
			return;
		}
	}

	/**
	 * Establece el modo del Compiler (singleton) y devuelve el nombre del
	 * archivo según los argumentos parseados.
	 * 
	 * @param args Argumentos de la línea de comandos.
	 * @return Nombre del archivo a parsear.
	 * @throws IllegalArgumentException Si los argumentos son incorrectos.
	 */
	@SuppressWarnings("static-access")
	public static List<String> setCompilerMode(String[] args) throws IllegalArgumentException {
		// TODO: para parsear la linea de comandos se puede usar el framework
		// CLI
		List<String> ret = new ArrayList<String>();
		Compiler compiler = Compiler.getInstance();
		CompilerModes mode = null;

		Option helpOption = new Option("h", "help", false, "show help");
		Option versionOption = new Option("v", "version", false, "show version information");
		Option aboutOption = new Option("a", "about", false, "about jzas");
		Option lexicAnalysisOption = OptionBuilder.withArgName("file").hasArg().withDescription(
				"perform lexical analysis for given file").withLongOpt("lexic").create("L");
		Option sintacticAnalysisOption = OptionBuilder.withArgName("file").hasArg().withDescription(
				"perform sintactic analysis for given file").withLongOpt("sintactic").create("S");
		Option symbolTableDumpOption = OptionBuilder.withArgName("file").hasArg().withDescription(
				"dump symbol table for given file").withLongOpt("symboltable").create("T");
		Option semanticAnalysisOption = OptionBuilder.withArgName("file").hasArg().withDescription(
				"perform semantic analysis for given file").withLongOpt("semantic").create("E");
		Option objectiveCodeGenerationOption = OptionBuilder.withArgName("file").hasArg().withDescription(
				"Generate objective code for given file").withLongOpt("compile").create("C");
		Option linkeditOption = OptionBuilder.hasArgs().withArgName("file1 [ file2 ... fileN]").withValueSeparator()
				.withDescription("linkedit given files").withLongOpt("link").create("X");
		Option csegStartOption = OptionBuilder.withArgName("address").hasArg().withDescription(
				"Set code segment start address").withLongOpt("cseg").create("c");
		Option dsegStartOption = OptionBuilder.withArgName("address").hasArg().withDescription(
				"Set data segment start address").withLongOpt("dseg").create("d");
		Option warningOption = OptionBuilder.withDescription("Show warnings").withLongOpt("warning").create("W");

		Options options = new Options();
		options.addOption(helpOption);
		options.addOption(versionOption);
		options.addOption(aboutOption);
		options.addOption(lexicAnalysisOption);
		options.addOption(sintacticAnalysisOption);
		options.addOption(symbolTableDumpOption);
		options.addOption(semanticAnalysisOption);
		options.addOption(objectiveCodeGenerationOption);
		options.addOption(linkeditOption);
		options.addOption(csegStartOption);
		options.addOption(dsegStartOption);
		options.addOption(warningOption);

		CommandLineParser clParser = new GnuParser();
		CommandLine cl = null;
		try {
			cl = clParser.parse(options, args);
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("jzas", options);
			// e.printStackTrace();
			return null;
		}

		if (cl.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("jzas", options);
			return null;
		} else if (cl.hasOption("v")) {
			System.out.println("jzas version 0.1.1.xx");
			return null;
		} else if (cl.hasOption("a")) {
			System.out.println("JZAS (versión 0.1.1.xx)\n" + "Ensamblador del microprocesador ZiLOG™ Z80®\n"
					+ "Desarrollado con fines académicos\n\n"
					+ "Autor:\n   Rafael Martín Bigio <rbigio@alu.itba.edu.ar>\n" + "Con la colaboración de:\n"
					+ "   Santiago Andrés Coffey <scoffey@alu.itba.edu.ar>\n"
					+ "   Carlos Julián Sánchez Romero <casanche@alu.itba.edu.ar>\n"
					+ "   Ing. Eduardo A. Martínez <eam@itba.edu.ar>\n\n" + "© 2008 Departamento de Informática\n"
					+ "ITBA (http://www.itba.edu.ar)\n ");
			return null;
		} else {
			if (cl.hasOption("-L")) {
				mode = CompilerModes.MODE_LEXER;
				ret.add(compiler.getFilename(cl.getOptionValue("L")));
			} else if (cl.hasOption("-S")) {
				mode = CompilerModes.MODE_SINTACTIC;
				ret.add(compiler.getFilename(cl.getOptionValue("S")));
			} else if (cl.hasOption("-E")) {
				mode = CompilerModes.MODE_SEMANTIC;
				ret.add(compiler.getFilename(cl.getOptionValue("E")));
			} else if (cl.hasOption("-T")) {
				mode = CompilerModes.MODE_SYMBOL_TABLE;
				ret.add(compiler.getFilename(cl.getOptionValue("T")));
			} else if (cl.hasOption("-C")) {
				mode = CompilerModes.MODE_OBJECTIVE_CODE;
				ret.add(compiler.getFilename(cl.getOptionValue("C")));
			} else if (cl.hasOption("-X")) {
				mode = CompilerModes.MODE_LINK;
				ret = Arrays.asList(cl.getOptionValues("X"));
				try {
					/*
					 * Parsear la dirección de comienzo del segmento de código y
					 * datos
					 */
					String code = cl.getOptionValue("cseg");
					String data = cl.getOptionValue("dseg");
					int radix;
					if (code != null) {
						radix = 10;
						if (code.charAt(code.length() - 1) == 'H' || code.charAt(code.length() - 1) == 'h') {
							radix = 16;
							code = code.substring(0, code.length() - 1);
						}
						csegStart = Integer.parseInt(code, radix);
					}
					if (data != null) {
						radix = 10;
						if (data.charAt(data.length() - 1) == 'H' || data.charAt(data.length() - 1) == 'h') {
							radix = 16;
							data = data.substring(0, data.length() - 1);
						}
						dsegStart = Integer.parseInt(data, radix);
					}
				} catch (NumberFormatException e) {
					HelpFormatter formatter = new HelpFormatter();
					formatter.printHelp("jzas", options);
					return null;
				}
			} else {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("jzas", options);
				throw new IllegalArgumentException();
			}
		}

		Compiler.getInstance().setMode(mode);
		return ret;
	}

	public String getFilename(String arg) {
		String[] aux = arg.split("/");
		workingDirectory = "";
		for (int i = 0; i < aux.length - 1; i++)
			workingDirectory = workingDirectory.concat(aux[i] + "/");
		String fileName = aux[aux.length - 1];
		if (workingDirectory.equals(""))
			workingDirectory = "./";
		return fileName;
	}

	public void dumpTokens(String fileName) {
		Token<JZasTokenTypes> token;
		Lexer lexer = new JZasLexer();
		lexer.setFeedback(logger);

		try {
			in = new JZasReader(new FileReader(new File(workingDirectory + fileName)));
		} catch (Exception e) {
			logger.showError(fileName, 0, 0, "File not found: " + fileName);
			return;
		}

		lexer.process(in, fileName);

		while ((token = lexer.nextToken()) != null) {
			System.out.println(token);
		}
	}

	public void setMode(CompilerModes mode) {
		Compiler.getInstance().mode = mode;
	}

	public CompilerModes getMode() {
		return mode;
	}

	/**
	 * @return La única instancia del compilador.
	 */
	public static Compiler getInstance() {
		return me;
	}

	/**
	 * Crea una instancia del compilador.
	 */
	private Compiler() {
		super();
		this.inputFiles = new HashSet<String>();
		this.mode = null;
		this.showWarnings = false;
	}

	/**
	 * Agrega un archivo al stack de archivos a compilar.
	 * 
	 * @param fileName Nombre (con path incluido) del archivo a compilar.
	 * @return 0 si no hubo errores
	 */
	public int compileFile(List<String> fileNames, int line, int column) {
		int ret = 0, errRet = 1;
		String file;

		/* Verificar inclusion recursiva. */
		// if (inputFiles.contains(fileName)) {
		// logger.showWarning(currentFile.getFileName(), line, column,
		// "Recursive inclusion of module: " + fileName);
		// return null;
		// }
		/* Si ya estaba procesando un archivo, calculo el path del nuevo. */
		String path = "";
		if (currentFile != null) {
			String[] parts = currentFile.getFileName().split("/");
			for (int i = 0; i < parts.length - 1; i++) {
				path += parts[i] + "/";
			}
		}
		preffix = workingDirectory + path + getCurrentLocation();

		SemanticAnalyzer macroDefinitionInspector = null;
		SemanticAnalyzer codeGenerator = null;
		SemanticAnalyzer symbolTableBuilder = null;
		/* Procesar cada archivo */
		for (String fileName : fileNames) {
			/* Abro archivo de entrada */
			try {
				file = preffix + fileName;
				in = new JZasReader(new FileReader(new File(file)));
			} catch (Exception e) {
				if (mode != CompilerModes.MODE_LEXER) {
					logger.showError(fileName, line, column, "File not found: " + fileName);
				}
				return errRet;
			}
			if (this.mode == CompilerModes.MODE_OBJECTIVE_CODE || this.mode == CompilerModes.MODE_SEMANTIC
					|| this.mode == CompilerModes.MODE_SYMBOL_TABLE || this.mode == CompilerModes.MODE_SINTACTIC
					|| mode == CompilerModes.MODE_LINK) {
				CompilerInputFile aux = currentFile;

				Lexer lexer = new JZasLexer();
				ParserAdapter parser = new JZasParser();
				macroDefinitionInspector = new MacroDefinitionInspector(parser, logger);
				parser.setSemanticAnalyzer(macroDefinitionInspector);
				parser.setFeedback(logger);
				lexer.setFeedback(logger);

				/* Primera pasada: buscamos todas las definiciones de macros. */
//				System.out.println("Primera pasada ...");
				currentFile = new CompilerInputFile(fileName, in);
				this.inputFiles.add(fileName);
				((JZasReader) in).addObserver(macroDefinitionInspector);
				lexer.process(in, fileName);
				parser.parse(lexer);
				if (this.mode == CompilerModes.MODE_SINTACTIC) {
					ret = errRet;
					continue;
				}

				if (macroDefinitionInspector.hasReportedErrors()) {
					ret = errRet;
					continue;
				}

				/* Segunda pasada: construimos tabla de simbolos. */
				symbolTableBuilder = new SymbolTableBuilder(parser, logger);
				symbolTableBuilder.setSymbolTable(macroDefinitionInspector.getSymbolTable());
//				System.out.println("Segunda pasada ...");
				parser.setSemanticAnalyzer(symbolTableBuilder);
				try {
					in = new JZasReader(new FileReader(new File(file)));
					((JZasReader) in).addObserver(symbolTableBuilder);
				} catch (Exception e) {
					/* Nunca debe ocurrir, porque ya lo abrí antes. */
					e.printStackTrace();
				}
				lexer.process(in, fileName);
				parser.parse(lexer);
				if (this.mode == CompilerModes.MODE_SINTACTIC) {
					ret = errRet;
					continue;
				}

				if (symbolTableBuilder.hasReportedErrors()) {
					ret = errRet;
					continue;
				}

				/* Tercera pasada, todo el análisis y generación de código. */
				// System.out.println("Tercera pasada ...");
				codeGenerator = new CodeGenerator(parser, logger);
				codeGenerator.setSymbolTable(symbolTableBuilder.getSymbolTable());

				parser.setSemanticAnalyzer(codeGenerator);
				try {
					in = new JZasReader(new FileReader(new File(file)));
					((JZasReader) in).addObserver(codeGenerator);
				} catch (Exception e) {
					/* Nunca debe ocurrir, porque ya lo abrí antes. */
					e.printStackTrace();
				}
				lexer.process(in, fileName);
				parser.parse(lexer);

				if (mode == CompilerModes.MODE_SYMBOL_TABLE && getFileStackSize() == 1 && !macroDefinitionInspector.hasReportedErrors()
						 && !symbolTableBuilder.hasReportedErrors() && !codeGenerator.hasReportedErrors() ) {
					codeGenerator.getSymbolTable().print(0);
					return ret;
				}

				currentFile = aux;
				this.inputFiles.remove(fileName);

				if ((mode == CompilerModes.MODE_OBJECTIVE_CODE || (mode == CompilerModes.MODE_LINK && fileName
						.endsWith(".asm")))
						&& !macroDefinitionInspector.hasReportedErrors()
						 && !symbolTableBuilder.hasReportedErrors() && !codeGenerator.hasReportedErrors()) {
					/*
					 * Verificar que no haya habido superposiciones de
					 * instrucciones detron de cada segmento
					 */
					((CodeGenerator) codeGenerator).dumpInstructions(preffix + fileName.replace(".asm", ".obj"));
				}
			}
		}

		/* Linkedicion */
		if (mode == CompilerModes.MODE_LINK && !macroDefinitionInspector.hasReportedErrors()
				 && !symbolTableBuilder.hasReportedErrors() && !codeGenerator.hasReportedErrors()) {
			/* Convertir los paths de los archivos en paths absolutos */
			for (int i = 0; i < fileNames.size(); i++) {
				fileNames.set(i, preffix + fileNames.get(i).replace(".asm", ".obj"));
			}

			/* Linkedir los archivos */
			Linker linker = new Linker(csegStart, dsegStart, fileNames, logger);
			linker.linkedit();
		}
		return ret;
	}

	public String getPreffix() {
		return preffix;
	}
	
	public Reader getReader() {
		return in;
	}

	public boolean showWarnings() {
		return showWarnings;
	}

	public void setCurrentLocation(String location) {
		this.currentFile.setLocation(location);
	}

	public String getCurrentLocation() {
		if (this.inputFiles.size() == 0) {
			return "";
		} else {
			return this.currentFile.getLocation();
		}
	}
	
	public String getCurrentFilename() {
		return currentFile.fileName;
	}

	public int getFileStackSize() {
		return inputFiles.size();
	}

	public void setLogger(ProcessLogger logger) {
		this.logger = logger;
	}
	
	public static void setDsegStart(int dsegStart) {
		Compiler.dsegStart = dsegStart;
	}
	
	public static void setCsegStart(int csegStart) {
		Compiler.csegStart = csegStart;
	}

	class CompilerInputFile {
		private String fileName;
		private Reader reader;
		private String location;

		public CompilerInputFile(String fileName, Reader reader, String location) {
			super();
			this.fileName = fileName;
			this.reader = reader;
			this.location = location;
		}

		public CompilerInputFile(String fileName, Reader reader) {
			this(fileName, reader, "");
		}

		public String getFileName() {
			return fileName;
		}

		public String getLocation() {
			return location.equals("") ? "" : location + "/";
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public Reader getReader() {
			return reader;
		}
	}
}
