package ar.edu.itba.it.obc.jzas.semantic;

import java.util.List;

import ar.edu.itba.it.obc.jzas.parser.ParserAdapter;
import ar.edu.itba.it.obc.jzas.semantic.nodes.IntExpression;
import ar.edu.itba.it.obc.jzas.semantic.symbols.MacroSymbol;
import ar.edu.itba.it.obc.jzas.semantic.symbols.Symbol;
import ar.edu.itba.it.obc.jzas.util.ProcessLogger;

public class MacroDefinitionInspector extends SemanticAnalyzer {

	public MacroDefinitionInspector(ParserAdapter parser, ProcessLogger logger) {
		super(parser, logger);
	}

	@Override
	public void defineMacro(String macroName, List<String> params,
			String instructions) {
		try {
			addSymbol(new MacroSymbol(macroName, params, instructions));
		} catch (DuplicatedSymbolException e) {
			reportError("Macro symbol " + macroName + " duplicated");
		}
	}

	/***************************************************************************
	 * A partir de aca, las implementaciones son dummy ya que se resuelven en la
	 * segunda pasada.
	 **************************************************************************/

	@Override
	public MacroSymbol invokeMacro(String macroName,
			java.util.List<String> params) {
		return null;
	};

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
	public String declareRotule(String rotule) {
		return "";
	}

	@Override
	public void directiveDefb(String rotule, List<Integer> exps) {
	}

	@Override
	public void directiveDeff(String rotule, List<Float> exps) {
	}

	@Override
	public void directiveDefl(String rotule, String substitute) {
	}

	@Override
	public void directiveDefm(String rotule, String message) {
	}

	@Override
	public void directiveDefs(String rotule, Integer size) {
	}

	@Override
	public void directiveDefw(String rotule, List<Integer> exps) {
	}

	@Override
	public void directiveEnd(String rotule) {
	}

	@Override
	public void directiveEqu(String rotule, String substitute) {
	}

	@Override
	public void directiveExtern(List<String> rotules) {
	}

	@Override
	public void directivePublic(List<String> publicRotules) {
	}

	@Override
	public OCRecord instruction(String rotule, OCRecord instruction) {
		return instruction;
	}

	@Override
	public void pgmInstructionLine(OCRecord instruction) {
	}

	@Override
	public Double rotuleAliasExpression(String rotule) {
		return 0.0;
	}

	@Override
	public IntExpression rotuleExpression(String rotule) {
		return new IntExpression();
	}

	@Override
	public void saveAddressSegmentsInfo(Symbol rotule) {
	}

	/***************************************************************************
	 * Fin de la implementación de los handlers de la gramática
	 **************************************************************************/
}
