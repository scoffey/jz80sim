package ar.edu.itba.it.obc.jzas.semantic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import ar.edu.itba.it.obc.jzas.parser.ParserAdapter;
import ar.edu.itba.it.obc.jzas.semantic.nodes.IntExpression;
import ar.edu.itba.it.obc.jzas.semantic.symbols.ExternalSymbol;
import ar.edu.itba.it.obc.jzas.semantic.symbols.MacroSymbol;
import ar.edu.itba.it.obc.jzas.semantic.symbols.RotuleAliasSymbol;
import ar.edu.itba.it.obc.jzas.semantic.symbols.RotuleSymbol;
import ar.edu.itba.it.obc.jzas.semantic.symbols.Symbol;
import ar.edu.itba.it.obc.jzas.util.NumberConversionUtil;
import ar.edu.itba.it.obc.jzas.util.ProcessLogger;

public class CodeGenerator extends SemanticAnalyzer {

	/* Se tienen 3 listas para almacenar instrucciones (1 para cada segmento) */
	private List<OCRecord> asegInstructionList;
	private List<OCRecord> csegInstructionList;
	private List<OCRecord> dsegInstructionList;
	private List<OCRecord> currentInstructionList;

	/*
	 * Este set almacena todas las ocurrencias de rotulos no definidos en el
	 * segmento absoluto que se utilizan como operandos en instrucciones ya que
	 * recien al momento de linkeditar se conoce donde comienzan todos los
	 * segmentos (excepto el absoluto).
	 */
	private TreeSet<AddressSegmentInfo> addressSegmentInfoSet;

	/*
	 * Este set almacena los rotulos que se quieren exportar (directiva public |
	 * global) ya que es necesario guardar esta información en los archivos
	 * objeto para saber si no faltan símbolos al momento de linkeditar.
	 */
	private TreeSet<PublicLabel> publicLabelsSet;

	/*
	 * Este set almacena las ocurrencias de variables importadas (con la
	 * directiva extern). Es necesario guardar dichas ocurrencias en el archivo
	 * objeto para poner las irecciones reales cuando se realice el proceso de
	 * linkedición.
	 */
	private TreeSet<ExternalLabelOcurrence> externalLabelsOcurrencesSet;

	/*
	 * Mapas de memoria de cada segmento que se utilizan para determinar cuando
	 * se agregan instruccciones si hay colisiones con las que ya estaban.
	 */
	TreeSet<Integer> asegUsedMemoryPositions;
	TreeSet<Integer> csegUsedMemoryPositions;
	TreeSet<Integer> dsegUsedMemoryPositions;

	/* Comparator de las mapas de memoria de cada segmento */
	private Comparator<Integer> usedMemoryPositionsComparator;

	public CodeGenerator(ParserAdapter parser, ProcessLogger logger) {
		super(parser, logger);
		this.parser = parser;

		this.asegInstructionList = new ArrayList<OCRecord>();
		this.csegInstructionList = new ArrayList<OCRecord>();
		this.dsegInstructionList = new ArrayList<OCRecord>();
		this.currentInstructionList = asegInstructionList;
		this.addressSegmentInfoSet = new TreeSet<AddressSegmentInfo>();
		this.publicLabelsSet = new TreeSet<PublicLabel>();
		this.externalLabelsOcurrencesSet = new TreeSet<ExternalLabelOcurrence>();
		this.usedMemoryPositionsComparator = new Comparator<Integer>() {
			// @Override
			public int compare(Integer o1, Integer o2) {
				return o1 - o2;
			}
		};
		this.asegUsedMemoryPositions = new TreeSet<Integer>(
				usedMemoryPositionsComparator);
		this.csegUsedMemoryPositions = new TreeSet<Integer>(
				usedMemoryPositionsComparator);
		this.dsegUsedMemoryPositions = new TreeSet<Integer>(
				usedMemoryPositionsComparator);
	}

	/**
	 * Determina si hay superposiciones si se agrega una instruccin en un
	 * determinado segmento.
	 * 
	 * @param record
	 *            Instrucción que se quiere agregar.
	 * @param usedMemoryPositions
	 *            Mapa de memoria del segmento en que se quiere agregar la
	 *            instrucción
	 * @return
	 * @return Verdadero o falso dependiendo de si hay o no colisiones.
	 */
	private boolean hasInstructionsCollision(OCRecord record,
			TreeSet<Integer> usedMemoryPositions) {

		int address = record.getAddress();
		int len = record.getSize();
		boolean ret = false;
		for (int i = address; i < address + len; i++) {
			if (!usedMemoryPositions.add(i)) {
				ret = true;
			}
		}
		return ret;
	}

	public void dumpInstructions(String file) {
		ObjectiveCodeDumper dumper = new HexDumper(file);

		/*
		 * Bajar las instrucciones y los "huecos" para espacio pedido por el
		 * usuario
		 */
		dumpOCRecords(dumper);
		/* Bajar la lista de segmentos asociados a direcciones relativas */
		dumpRelativeLabels(dumper);
		/* Bajar las etiquetas publicas (directiva public | global) */
		dumpPublicLabels(dumper);
		/* Bajar las ocurrencias de las etiquetas externas (directivas extern) */
		dumpExternalLabels(dumper);
		dumper.close();
	}

	@Override
	public MacroSymbol invokeMacro(String macroName, List<String> params) {
		MacroSymbol macroSymbol = super.invokeMacro(macroName, params);

		if (macroSymbol != null) {
			/*
			 * Desencolar la tabla de simbolos de invocaciones que se acaba de
			 * procesar
			 */
			symbolTables.push(macroSymbol.endMacroInvocation());
		}
		return macroSymbol;
	}

	@Override
	public void directiveDefb(String rotule, List<Integer> exps) {
		for (Integer exp : exps) {
			currentInstructionList.add(new OCRecord(NumberConversionUtil.toHex(
					exp, 2), 16, getNextAddress()));
		}
		addToCurrentAddress(exps.size());
	}

	@Override
	public void directiveDeff(String rotule, List<Float> exps) {
		for (Float exp : exps) {
			currentInstructionList.add(new OCRecord(NumberConversionUtil.toHex(
					exp, 8), 16, getNextAddress()));
		}
		addToCurrentAddress(4 * exps.size());
	}

	@Override
	public void directiveDefm(String rotule, String message) {
		String hexCodification = "";
		byte[] bytes = message.getBytes();
		for (Byte b : bytes) {
			hexCodification += NumberConversionUtil.toHex(b, 2);
		}
		currentInstructionList.add(new OCRecord(hexCodification, 16,
				getNextAddress()));
		addToCurrentAddress(message.length());
	}

	@Override
	public void directiveDefs(String rotule, Integer size) {
		String hexCodification = "";
		for (int i = 0; i < size; i++) {
			hexCodification += "0";
		}
		currentInstructionList.add(new OCRecord(hexCodification, 16,
				getNextAddress()));
		addToCurrentAddress(size);
	}

	@Override
	public void directiveDefw(String rotule, List<Integer> exps) {
		String hex;
		for (Integer exp : exps) {
			hex = NumberConversionUtil.toHex(exp, 4);
			currentInstructionList.add(new OCRecord(hex.substring(2, 4)
					+ hex.substring(0, 2), 16, getNextAddress()));
		}
		addToCurrentAddress(2 * exps.size());
	}

	@Override
	public void directiveEnd(String rotule) {
		// TODO Auto-generated method stub
	}

	// @Override
	// public void directiveLocal(String rotule) {
	// Symbol symbol = getSymbol(rotule);
	// if (symbol == null) {
	// reportError("Rotule " + rotule + " not previously defined");
	// } else {
	// symbol.setLocal();
	// }
	// }

	@Override
	public void directivePublic(List<String> publicRotules) {
		for (String rotule : publicRotules) {
			Symbol symbol = getSymbol(rotule);
			if (symbol == null) {
				reportError("Rotule " + rotule + " not previously defined");
			} else {
				symbol.setPublic();
				if (symbol instanceof ExternalSymbol) {
					reportError("Cannot export external symbol");
				} else if (symbol instanceof MacroSymbol) {
					reportError("Macro symbol cannot be exported");
				} else if (symbol instanceof RotuleAliasSymbol) {
					throw new RuntimeException("aun no implementado...");
				} else if (symbol instanceof RotuleSymbol) {
					publicLabelsSet.add(new PublicLabel(symbol.getId(),
							((RotuleSymbol) symbol).getSegment(),
							((RotuleSymbol) symbol).getAddress()));
				} else {
					throw new RuntimeException(
							"SE ACTUALIZO LA JERARQUI DE SIMBOLOS Y NO ME ACTUALIZASTE!");
				}

			}
		}
	}

	@Override
	public OCRecord instruction(String rotule, OCRecord instruction) {
		instruction.setRotule(rotule);
		return instruction;
	}

	@Override
	public void pgmInstructionLine(OCRecord instruction) {
		if (hasInstructionsCollision(instruction,
				getCurrentUsedMemoryPositions())) {
			reportError("Instruction collision");
		}
		currentInstructionList.add(instruction);
	}

	@Override
	public IntExpression rotuleExpression(String rotule) {
		Symbol symbol = getSymbol(rotule);
		if (symbol == null) {
			reportError("Rotule symbol " + rotule + " not defined");
			return new IntExpression();
		}

		if (symbol instanceof RotuleAliasSymbol) {
			try {
				Double ret = Double.parseDouble(((RotuleAliasSymbol) symbol)
						.getAlias());
				return new IntExpression(ret.intValue());
			} catch (NumberFormatException e) {
				/* El alias es un rotulo */
				return rotuleExpression(((RotuleAliasSymbol) symbol).getAlias());
			}
		} else if (symbol instanceof RotuleSymbol) {
			/* Es un RotuleSymbol */
			RotuleSymbol rotuleSymbol = getRotuleSymbol(rotule);
			if (rotuleSymbol != null) {
				return new IntExpression(rotuleSymbol.getAddress(),
						rotuleSymbol);
			} else {
				rotuleSymbol = new RotuleSymbol("ERROR", 0, Segment.ASEG);
				reportError("Rotule symbol " + rotule + " not defined");
				return new IntExpression();
			}
		} else if (symbol instanceof ExternalSymbol) {
			return new IntExpression((ExternalSymbol) symbol);
		} else {
			throw new RuntimeException("TODO MUY MALLLL");
		}

	}

	@Override
	public Double rotuleAliasExpression(String rotule) {
		RotuleAliasSymbol rotuleAliasSymbol = getRotuleAliasSymbol(rotule);
		if (rotuleAliasSymbol != null) {
			try {
				Double ret = Double.parseDouble(rotuleAliasSymbol.getAlias());
				return ret;
			} catch (NumberFormatException e) {
				// NUNCA DEBE PASAR
				throw new RuntimeException("TODO RE MAL");
			}
		} else {
			reportError("Alias " + rotule + "was not defined");
			return 0.0;
		}
	}

	@Override
	public Double MathExpression(Double num) {
		return -num;
	}

	@Override
	public Double MathExpression(MathOperator operator, Double num1, Double num2) {
		switch (operator) {
		case PLUS:
			return num1 + num2;
		case MINUS:
			return num1 - num2;
		case TIMES:
			return num1 * num2;
		case DIVIDE:
			return num1 / num2;
		default:
			throw new RuntimeException("Caso no contemplado");
		}
	}

	@Override
	public IntExpression MathExpression(IntExpression num) {
		num.setData(-num.getData());
		return num;
	}

	@Override
	public IntExpression MathExpression(MathOperator operator,
			IntExpression num1, IntExpression num2) {
		IntExpression errorRet = new IntExpression();
		int data1 = num1.getData();
		int data2 = num2.getData();
		int newData = 0;
		Symbol newSymbol = null;
		switch (operator) {
		case PLUS:
			newData = data1 + data2;
			break;
		case MINUS:
			newData = data1 - data2;
			break;
		case TIMES:
			newData = data1 * data2;
			break;
		case DIVIDE:
			newData = data1 / data2;
			break;
		default:
			throw new RuntimeException("Caso no contemplado");
		}

		/* Verificar que de haber rotulos, la operacion sea valida */
		if (!operator.equals(MathOperator.MINUS)) {
			if (num1.getSymbol() != null) {
				if (num2.getSymbol() != null) {
					reportError("Invalid operation between "
							+ num1.getSymbol().getId() + " and "
							+ num2.getSymbol().getId());
					return errorRet;
				} else {
					newSymbol = num1.getSymbol();
				}
			} else {
				if (num2.getSymbol() != null) {
					newSymbol = num2.getSymbol();
				}
			}
		} else {
			if (num1.getSymbol() != null) {
				if (num2.getSymbol() != null) {
					if (num1.getSymbol() instanceof ExternalSymbol
							|| num2.getSymbol() instanceof ExternalSymbol) {
						reportError("Can't substract external symbols");
					}
				} else {
					newSymbol = num1.getSymbol();
				}
			} else {
				if (num2.getSymbol() != null) {
					newSymbol = num2.getSymbol();
				}
			}
		}

		return new IntExpression(newData, newSymbol);
	}

	/**
	 * Establece como segmento actual al absoluto.
	 */
	@Override
	public void directiveAseg() {
		currentSegment = Segment.ASEG;
		currentInstructionList = asegInstructionList;
	}

	/**
	 * Establece como segmento actual al de código.
	 */
	@Override
	public void directiveCseg() {
		currentSegment = Segment.CSEG;
		currentInstructionList = csegInstructionList;
	}

	/**
	 * Establece como segmento actual al de datos.
	 */
	@Override
	public void directiveDseg() {
		currentSegment = Segment.DSEG;
		currentInstructionList = dsegInstructionList;
	}

	/***************************************************************************
	 * A partir de aca, las implementaciones son dummy ya que se resolvieron en
	 * la primer pasada
	 **************************************************************************/
	@Override
	public void directiveExtern(List<String> rotules) {
	}

	@Override
	public String declareRotule(String rotule) {
		return null;
	}

	@Override
	public void defineMacro(String macroName, List<String> params,
			String instructions) {
	}

	@Override
	public void directiveEqu(String rotule, String substitute) {
	}

	@Override
	public void directiveDefl(String rotule, String substitute) {
	}

	@Override
	public void saveAddressSegmentsInfo(Symbol symbol) {
		if (symbol instanceof ExternalSymbol) {
			externalLabelsOcurrencesSet.add(new ExternalLabelOcurrence(symbol
					.getId(), currentSegment, getNextAddress() - 2));
		} else {
			if (symbol != null) {
				addressSegmentInfoSet.add(new AddressSegmentInfo(
						currentSegment, getNextAddress() - 2,
						((RotuleSymbol) symbol).getSegment()));
			}
		}
	}

	/**
	 * Baja las ocurrencias de las etiquetas importadas desde otros módulos.
	 * 
	 * @param dumper
	 *            ObjectiveCodeDumper
	 */
	private void dumpExternalLabels(ObjectiveCodeDumper dumper) {
		dumper.writeStringLine("EXTERNAL LABELS OCURRENCES");

		SortedSet<ExternalLabelOcurrence> asegSubSet = externalLabelsOcurrencesSet
				.subSet(new ExternalLabelOcurrence("", Segment.ASEG, 0x0000),
						new ExternalLabelOcurrence("", Segment.ASEG, 0xFFFF));

		SortedSet<ExternalLabelOcurrence> csegSubSet = externalLabelsOcurrencesSet
				.subSet(new ExternalLabelOcurrence("", Segment.CSEG, 0x0000),
						new ExternalLabelOcurrence("", Segment.CSEG, 0xFFFF));

		SortedSet<ExternalLabelOcurrence> dsegSubSet = externalLabelsOcurrencesSet
				.subSet(new ExternalLabelOcurrence("", Segment.DSEG, 0x0000),
						new ExternalLabelOcurrence("", Segment.DSEG, 0xFFFF));

		dumper.startASegSection();
		for (ExternalLabelOcurrence ocurrence : asegSubSet) {
			dumper.writeStringLine(ocurrence.getAddresAndLabel());
		}

		dumper.startCSegSection();
		for (ExternalLabelOcurrence ocurrence : csegSubSet) {
			dumper.writeStringLine(ocurrence.getAddresAndLabel());
		}

		dumper.startDSegSection();
		for (ExternalLabelOcurrence ocurrence : dsegSubSet) {
			dumper.writeStringLine(ocurrence.getAddresAndLabel());
		}
	}

	/**
	 * Baja la información necesaria para indicar que en este módulo se exporta
	 * un rótulo
	 * 
	 * @param dumper
	 *            ObjectiveCodeDumper
	 */
	private void dumpPublicLabels(ObjectiveCodeDumper dumper) {
		dumper.writeStringLine("PUBLIC LABELS");

		SortedSet<PublicLabel> asegSubSet = publicLabelsSet.subSet(
				new PublicLabel("", Segment.ASEG, 0x0000), new PublicLabel("",
						Segment.ASEG, 0xFFFF));

		SortedSet<PublicLabel> csegSubSet = publicLabelsSet.subSet(
				new PublicLabel("", Segment.CSEG, 0x0000), new PublicLabel("",
						Segment.CSEG, 0xFFFF));

		SortedSet<PublicLabel> dsegSubSet = publicLabelsSet.subSet(
				new PublicLabel("", Segment.DSEG, 0x0000), new PublicLabel("",
						Segment.DSEG, 0xFFFF));

		dumper.startASegSection();
		for (PublicLabel ocurrence : asegSubSet) {
			dumper.writeStringLine(ocurrence.getAddresAndLabel());
		}

		dumper.startCSegSection();
		for (PublicLabel ocurrence : csegSubSet) {
			dumper.writeStringLine(ocurrence.getAddresAndLabel());
		}

		dumper.startDSegSection();
		for (PublicLabel ocurrence : dsegSubSet) {
			dumper.writeStringLine(ocurrence.getAddresAndLabel());
		}
	}

	/**
	 * Baja los Registros del formato Intel HEX.
	 * 
	 * @param dumper
	 *            ObjectiveCodeDumper
	 */
	private void dumpOCRecords(ObjectiveCodeDumper dumper) {
		dumper.writeStringLine("INTEL HEX RECORDS");
		dumper.startASegSection();
		int offset = 0;
		for (OCRecord i : asegInstructionList) {
			dumper.dumpInstruction(i);
			offset += i.getSize();
		}
		dumper.endASegSection();
		dumper.startCSegSection();
		offset = 0;
		for (OCRecord i : csegInstructionList) {
			dumper.dumpInstruction(i);
			offset += i.getSize();
		}
		dumper.endCSegSection();
		dumper.startDSegSection();
		offset = 0;
		for (OCRecord i : dsegInstructionList) {
			dumper.dumpInstruction(i);
			offset += i.getSize();
		}
		dumper.endDSegSection();
	}

	/**
	 * Baja las ocurrencias de las etiquetas definidas en el segmento de datos y
	 * código.
	 * 
	 * @param dumper
	 *            ObjectiveCodeDumper
	 */

	private void dumpRelativeLabels(ObjectiveCodeDumper dumper) {
		dumper.writeStringLine("SEGMENT RELATIVE LABELS");

		SortedSet<AddressSegmentInfo> csegSubSet = addressSegmentInfoSet
				.subSet(new AddressSegmentInfo(Segment.CSEG, 0x0000, null),
						new AddressSegmentInfo(Segment.CSEG, 0xFFFF, null));

		SortedSet<AddressSegmentInfo> dsegSubSet = addressSegmentInfoSet
				.subSet(new AddressSegmentInfo(Segment.DSEG, 0x0000, null),
						new AddressSegmentInfo(Segment.DSEG, 0xFFFF, null));

		dumper.startCSegSection();
		for (AddressSegmentInfo currentAsi : csegSubSet) {
			dumper.writeStringLine(currentAsi.getAddressAndSegments());
		}

		dumper.startDSegSection();
		for (AddressSegmentInfo currentAsi : dsegSubSet) {
			dumper.writeStringLine(currentAsi.getAddressAndSegments());
		}
	}

	public List<OCRecord> getAsegIntructions() {
		return asegInstructionList;
	}

	public List<OCRecord> getCsegIntructions() {
		return csegInstructionList;
	}

	public List<OCRecord> getDsegIntructions() {
		return dsegInstructionList;
	}

	public TreeSet<Integer> getCurrentUsedMemoryPositions() {
		switch (currentSegment) {
		case ASEG:
			return asegUsedMemoryPositions;
		case CSEG:
			return csegUsedMemoryPositions;
		case DSEG:
			return dsegUsedMemoryPositions;
		default:
			return null;
		}
	}
}
