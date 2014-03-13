package ar.edu.itba.it.obc.jzas.semantic;

import java.util.List;

import ar.edu.itba.it.obc.jzas.parser.ParserAdapter;
import ar.edu.itba.it.obc.jzas.semantic.nodes.IntExpression;
import ar.edu.itba.it.obc.jzas.semantic.symbols.ExternalSymbol;
import ar.edu.itba.it.obc.jzas.semantic.symbols.MacroSymbol;
import ar.edu.itba.it.obc.jzas.semantic.symbols.RotuleAliasSymbol;
import ar.edu.itba.it.obc.jzas.semantic.symbols.RotuleSymbol;
import ar.edu.itba.it.obc.jzas.semantic.symbols.Symbol;
import ar.edu.itba.it.obc.jzas.util.ProcessLogger;

/**
 * Analizador semántico para hacer la primera pasada sobre el archivo. Construye
 * la tabla de símbolos con los rótulos y las macros. No genera código
 * intermedio.
 */
public class SymbolTableBuilder extends SemanticAnalyzer {

	/*
	 * Macro que esta siendo invocada para la que se quiere construir su tabla
	 * de símbolos interna
	 */

	public SymbolTableBuilder(ParserAdapter parser, ProcessLogger logger) {
		super(parser, logger);
	}

	@Override
	public String declareRotule(String rotule) {
		defineRotule(rotule, getNextAddress());
		return rotule;
	}

	@Override
	public void directiveDefb(String rotule, List<Integer> exps) {
		defineRotule(rotule, getNextAddress());
		addToCurrentAddress(exps.size());
	}

	@Override
	public void directiveDeff(String rotule, List<Float> exps) {
		defineRotule(rotule, getNextAddress());
		addToCurrentAddress(4 * exps.size());
	}

	@Override
	public void directiveDefm(String rotule, String message) {
		defineRotule(rotule, getNextAddress());
		addToCurrentAddress(message.length());
	}

	@Override
	public void directiveDefs(String rotule, Integer size) {
		defineRotule(rotule, getNextAddress());
		addToCurrentAddress(size);
	}

	@Override
	public void directiveDefw(String rotule, List<Integer> exps) {
		defineRotule(rotule, getNextAddress());
		addToCurrentAddress(2 * exps.size());
	}

	@Override
	public void directiveEqu(String rotule, String substitute) {
		addRotuleAliasSymbol(rotule, substitute, true);
	}

	@Override
	public void directiveDefl(String rotule, String substitute) {
		addRotuleAliasSymbol(rotule, substitute, false);
	}

	@Override
	public OCRecord instruction(String rotule, OCRecord instruction) {
		System.out.flush();
		if (!rotule.equals("")) {
			defineRotule(rotule, getNextAddress() - instruction.getSize());
		}
		instruction.setRotule(rotule);
		return instruction;
	}

	@Override
	public void directiveExtern(List<String> rotules) {
		for (String rotule : rotules) {
			try {
				ExternalSymbol externalSymbol = new ExternalSymbol(rotule);
				externalSymbol.setExternal();
				addSymbol(externalSymbol);
			} catch (DuplicatedSymbolException e) {
				reportError("Rotule symbol " + rotule + " duplicated");
			}
		}

	}

	@Override
	public MacroSymbol invokeMacro(String macroName, List<String> params) {
		MacroSymbol macroSymbol = super.invokeMacro(macroName, params);

		if (macroSymbol != null) {
			/*
			 * Apilar la nueva tabla de símbolos (la que se acaba de encolar en
			 * la cola de invocaciones de la macro)
			 */
			symbolTables.push(macroSymbol.beginMacroInvocation());
		}
		return macroSymbol;
	}

	/***************************************************************************
	 * A partir de aca, las implementaciones son dummy ya que se resuelven en la
	 * tercer pasada.
	 **************************************************************************/
	@Override
	public void pgmInstructionLine(OCRecord instruction) {
	}

	@Override
	public void directivePublic(List<String> publicRotules) {
	}

	@Override
	public void directiveEnd(String rotule) {
	}

	// @Override
	// public void directiveLocal(String rotule) {
	// }

	@Override
	public IntExpression rotuleExpression(String rotule) {
		return new IntExpression();
	}

	@Override
	public Double rotuleAliasExpression(String rotule) {
		return 0.0;
	}

	@Override
	public Double MathExpression(Double num) {
		return 0.0;
	}

	@Override
	public Double MathExpression(MathOperator operator, Double num1, Double num2) {
		return 0.0;
	}

	@Override
	public IntExpression MathExpression(IntExpression num) {
		return new IntExpression();
	}

	@Override
	public IntExpression MathExpression(MathOperator operator,
			IntExpression num1, IntExpression num2) {
		return new IntExpression();
	}

	@Override
	public void saveAddressSegmentsInfo(Symbol rotule) {
	}

	@Override
	public void defineMacro(String macroName, List<String> params,
			String instructions) {
	}

	/***************************************************************************
	 * Fin de las implementaciones del SemanticAnalyzer
	 **************************************************************************/

	private void defineRotule(String rotule, int address) {
		if (!rotule.equals("")) {
			try {
				addSymbol(new RotuleSymbol(rotule, address, currentSegment));
			} catch (DuplicatedSymbolException e) {
				reportError("Rotule symbol " + rotule + " duplicated");
			}
		}
	}

	private void defineRotuleAlias(String rotule, String alias) {
		try {
			addSymbol(new RotuleSymbol(rotule, getNextAddress(), currentSegment));
		} catch (DuplicatedSymbolException e) {
			reportError("Rotule symbol " + rotule + " duplicated");
		}
	}

	/**
	 * Agrega a la tabla de simbolos un RotuleAliasSymbol. Si este ya existía
	 * intenta actualizarlo y reporta errores en caso de que no sea posible
	 * dicha actualización. Tambień actualiza el estado interno del símbolo en
	 * caso de que lo haya redefinido (tanto el valo como la cantidad de
	 * redefiniciones efectivas).
	 * 
	 * @param rotule
	 *            Rótulo que se quiere (re)definir
	 * @param substitute
	 *            Substituto para el rótulo
	 * @param isEqu
	 *            Valor booleano que indica si se invocó este método tras un euq
	 *            o un defl (el comportamiento para las redefiniciones es
	 *            distinto).
	 */
	private void addRotuleAliasSymbol(String rotule, String substitute,
			boolean isEqu) {
		Symbol oldSymbol = getSymbol(rotule);

		/*
		 * Si el simbolo ya estaba definido fijarse si se lo puede redefinir y
		 * actualiza la cantidad de refediniciones efectivas
		 */
		if (oldSymbol != null) {
			if (!(oldSymbol instanceof RotuleAliasSymbol)) {
				reportError("Rotule symbol " + rotule + " duplicated");
			} else {
				RotuleAliasSymbol rotuleAliasSymbol = (RotuleAliasSymbol) oldSymbol;
				
				boolean areAliasesEquals = rotuleAliasSymbol.getAlias().equals(
						substitute);
				if (rotuleAliasSymbol.getEquCount() > 0 && isEqu) {
					if (!areAliasesEquals) {
						reportError("Alias " + rotule + " cannot be redefined");
					}
				} else {
					if (!areAliasesEquals) {
						if (isEqu){
							rotuleAliasSymbol.incrementEquCount();
						}
						rotuleAliasSymbol.setSubstitute(substitute);
					}
				}
			}
		} else {
			/* Si el alias no estaba definido, definirlo ahora */
			try {
				addSymbol(new RotuleAliasSymbol(rotule, substitute, isEqu));
			} catch (DuplicatedSymbolException e) {
				/* Nunca debe pasar porque ya se validó arriba */
				e.printStackTrace();
			}
		}
	}
}