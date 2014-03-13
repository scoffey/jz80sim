package ar.edu.itba.it.obc.jzas.linker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import ar.edu.itba.it.obc.jzas.semantic.AddressSegmentInfo;
import ar.edu.itba.it.obc.jzas.semantic.ExternalLabelOcurrence;
import ar.edu.itba.it.obc.jzas.semantic.HexDumper;
import ar.edu.itba.it.obc.jzas.semantic.ObjectiveCodeDumper;
import ar.edu.itba.it.obc.jzas.semantic.PublicLabel;
import ar.edu.itba.it.obc.jzas.semantic.SemanticAnalyzer.Segment;
import ar.edu.itba.it.obc.jzas.util.ProcessLogger;

/**
 * Esta clase se encarga de llevar a cabo el proceso de linkedición. Es decir,
 * dado un conjunto de archivos objeto, genera el archivo ejecutable.
 */
public class Linker {
	/* Dirección donde comienzan los segmentos de código y datos */
	private int csegStart, dsegStart;

	/* Lista de archivos objeto */
	private List<OCFile> files;
	private ProcessLogger logger;

	/* Clase que permite obtener información sobre archivos objetos */
	private HexAnalyzer hexAnalyzer;

	/* Archivo destino apra el ejecutable */
	private String targetFile;

	/*
	 * Sets con toda la información que había en los archivos objeto de los
	 * labels públicos, externos y ocurrencias de rótulos que fueron definidos
	 * en segmentos direccionados en forma relativa
	 */
	private TreeSet<AddressSegmentInfo> completeAddressSegmentInfoSet;
	private TreeSet<PublicLabel> completePublicLabelsSet;
	private TreeSet<ExternalLabelOcurrence> completeExternalLabelsOcurrencesSet;

	/* Set con todas las instrucciones de todos los archivos objeto */
	private InstructionCollection instructionsCollection;

	public Linker(int codeSegmentStart, int dataSegmentStart, List<String> filenames, ProcessLogger logger)
			throws IllegalArgumentException {
		this.csegStart = codeSegmentStart;
		this.dsegStart = dataSegmentStart;
		this.logger = logger;
		this.hexAnalyzer = new HexAnalyzer();
		this.targetFile = filenames.get(0).replace(".obj", ".hex");
		this.files = new ArrayList<OCFile>();

		for (String filename : filenames) {
			try {
				files.add(new OCFile(filename));
			} catch (FileNotFoundException e) {
				logger.showError("File not found");
				throw new IllegalArgumentException();
			}
		}

		/* Rellenar los conjuntos de labels y el conjunto de instrucciones */
		this.completeAddressSegmentInfoSet = new TreeSet<AddressSegmentInfo>();
		this.completePublicLabelsSet = new TreeSet<PublicLabel>();
		this.completeExternalLabelsOcurrencesSet = new TreeSet<ExternalLabelOcurrence>();
		this.instructionsCollection = new InstructionCollection();

		for (OCFile file : files) {
			completeAddressSegmentInfoSet.addAll(file.getAddressSegmentInfoSet());
			completeExternalLabelsOcurrencesSet.addAll(file.getExternalLabelsOcurrencesSet());
			completePublicLabelsSet.addAll(file.getPublicLabelsSet());
			instructionsCollection.addAll(Segment.ASEG, file.getAsegInstructionList());
			instructionsCollection.addAll(Segment.CSEG, file.getCsegInstructionList());
			instructionsCollection.addAll(Segment.DSEG, file.getDsegInstructionList());
		}

	}

	/**
	 * LLeva a cabo el proceso de linkedición.
	 */
	public void linkedit() {
		/* Verificar que no se pisen instrucciones */
		if (hasInstructionsCollision()) {
			System.out.println("todo mal");
			logger.showError("Instructions collision");
			return;
		}

		/* Verificar que no no existan rótulos publicos duplicados */
		hasDuplicatedPublicRotule();

		/*
		 * Verificar que todos los rótulos externos hayan sido definidos en
		 * otros módulos
		 */
		backpatchExternalLabels();

		/*
		 * Generar el archivo ejecutable agregando a las instrucciones que
		 * contienen direcciones el offset que corresponda en caso de que
		 * utilicen rótulos definidos fuera del segmento absoluto
		 */
		dumpInstructions();
	}

	/**
	 * Para todas las ocurrencias de rótulos externos, suma en dichas
	 * ocurrencias las direcciones verdaderas (relativas al segmento en donde
	 * estan definidos). Además agrega al conjunto de labels relativos la
	 * ocurrencia de etiquetas no definidas en el segmento absoluto para que
	 * despues también se le sume el offset de dicho segmento.s
	 */
	private void backpatchExternalLabels() {
		for (ExternalLabelOcurrence externalLabelOcurrence : completeExternalLabelsOcurrencesSet) {
			/* Obtener el PublicLabel que vincula la ExternalLabelOcurrence */
			PublicLabel publicLabel = getPublicLabel(externalLabelOcurrence.getLabel());
			if (publicLabel == null) {
				throw new RuntimeException("NUNCA DEBERIA PASAR ESTO");
			}

			/* Modificar la instrucción */
			InstructionWrapper instruction = instructionsCollection.getInstruction(externalLabelOcurrence.getSegment(),
					externalLabelOcurrence.getAddress());
			instruction.setInstruction(hexAnalyzer.incrementInstructionData(instruction.getInstruction(), publicLabel
					.getAddress()));

			/* Agregar conjunto de RelativeLabels */
			completeAddressSegmentInfoSet.add(new AddressSegmentInfo(externalLabelOcurrence.getSegment(),
					externalLabelOcurrence.getAddress(), publicLabel.getSegment()));
		}
	}

	/**
	 * Dado un rótulo, retorna el PublicLabel asociado.
	 * 
	 * @param label Rótulo que se desea buscar.
	 * @return PublicLabel asociado al rótulo. En caso de no enocntrarlo,
	 *         devuelve null.
	 */
	private PublicLabel getPublicLabel(String label) {
		for (PublicLabel publicLabel : completePublicLabelsSet) {
			if (publicLabel.getLabel().equals(label)) {
				return publicLabel;
			}
		}
		return null;
	}

	/**
	 * A partir de las ocurrencias de etiquetas definidas en segmentos con
	 * direccionamiento relativo (cseg, dseg), genera el archivo de salida
	 * modificando las instrucciones para tener en cuenta el offset definido a
	 * la hora de linkeditar en que comienzan cada uno de los segmentos.
	 */
	private void dumpInstructions() {
		ObjectiveCodeDumper dumper = new HexDumper(targetFile);
		for (InstructionWrapper instruction : instructionsCollection) {
			if (instruction.getSegment().equals(Segment.ASEG)) {
				dumper.writeStringLine(instruction.getInstruction());
			} else if (instruction.getSegment().equals(Segment.CSEG)) {
				dumper
						.writeStringLine(hexAnalyzer.incrementInstructionAddress(instruction.getInstruction(),
								csegStart));
			} else {
				dumper
						.writeStringLine(hexAnalyzer.incrementInstructionAddress(instruction.getInstruction(),
								dsegStart));
			}
		}
		dumper.eof();
		dumper.close();
	}

	/**
	 * Determina si en 2 archivos se exporta el mismo rótulo.
	 * 
	 * @return Verdadero o falso segun si en 2 archivos se define como público
	 *         el mismo rótulo
	 */
	private boolean hasDuplicatedPublicRotule() {
		int collisionsCount = 0;
		Hashtable<String, String> publicLabels = new Hashtable<String, String>();

		for (OCFile file : files) {
			for (PublicLabel publicLabel : file.getPublicLabelsSet()) {
				if (publicLabels.contains(publicLabel.getLabel())) {
					logger.showError("Duplicated public label in " + file.getFilename() + ". First defined public in "
							+ publicLabels.get(publicLabel.getLabel()));
					collisionsCount++;
				} else {
					publicLabels.put(publicLabel.getLabel(), file.getFilename());
				}
			}
		}
		return collisionsCount > 0;
	}

	private boolean hasInstructionsCollision() {
		TreeSet<Integer> usedMemoryPositions = new TreeSet<Integer>(new Comparator<Integer>() {
			// @Override
			public int compare(Integer o1, Integer o2) {
				return o1 - o2;
			}
		});
		for (OCFile file : files) {
			if (hasInstructionsCollision(usedMemoryPositions, file.getAsegInstructionList().iterator(), 0)) {
				return true;
			}
			if (hasInstructionsCollision(usedMemoryPositions, file.getCsegInstructionList().iterator(), csegStart)) {
				return true;
			}
			if (hasInstructionsCollision(usedMemoryPositions, file.getDsegInstructionList().iterator(), dsegStart)) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Determina si se superponen las instrucciones del iterador de
	 * instrucciones recibidos (incluyendo las que ya fueron chequeadas y
	 * marcadas en el mapa de memoria asociado al TreeSet).
	 * 
	 * @param usedMemoryPositions Mapa de memoria con las posiciones que fueron
	 *            ya ocupadas previamente. Este mapa es modificado agregando las
	 *            ocurrencias de las instrucciones del iterador!.
	 * @param iter Iterados con las instrucciones que se quiere validar que no
	 *            se superpongan.
	 * @param offset Offset inicial para todas las instrucciones de la lista de
	 *            instrucciones.
	 * @return Verdadero o false según haya o no superposición.
	 */
	private boolean hasInstructionsCollision(TreeSet<Integer> usedMemoryPositions, Iterator<String> iter,
			int offset) {
		HexAnalyzer hexAnalyzer = HexAnalyzer.getInstance();
		String instruction;
		while (iter.hasNext()) {
			instruction = iter.next();
			int address = hexAnalyzer.getAddress(instruction);
			int len = hexAnalyzer.getLength(instruction);
			for (int i = offset + address; i < offset + address + len; i++) {
				if (!usedMemoryPositions.add(i)) {
					System.out.println("COLISION EN " + i);
					return true;
				}
			}
		}
		return false;
	}

	private class OCFile {
		private String filename;
		private List<String> asegInstructionList;
		private List<String> csegInstructionList;
		private List<String> dsegInstructionList;
		private TreeSet<AddressSegmentInfo> addressSegmentInfoSet;
		private TreeSet<PublicLabel> publicLabelsSet;
		private TreeSet<ExternalLabelOcurrence> externalLabelsOcurrencesSet;

		public OCFile(String filename) throws FileNotFoundException {
			this.filename = filename;
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String line;

			asegInstructionList = new ArrayList<String>();
			csegInstructionList = new ArrayList<String>();
			dsegInstructionList = new ArrayList<String>();

			addressSegmentInfoSet = new TreeSet<AddressSegmentInfo>();
			publicLabelsSet = new TreeSet<PublicLabel>();
			externalLabelsOcurrencesSet = new TreeSet<ExternalLabelOcurrence>();

			try {
				/* Saltear el titulo */
				br.readLine();

				/* Saltear "ASEG" */
				br.readLine();

				while (!(line = br.readLine()).equals("CSEG")) {
					asegInstructionList.add(line);
				}

				while (!(line = br.readLine()).equals("DSEG")) {
					csegInstructionList.add(line);
				}

				while (!(line = br.readLine()).equals("SEGMENT RELATIVE LABELS")) {
					dsegInstructionList.add(line);
				}

				// TODO: cargar el resto de la información del archivo
			} catch (IOException e) {
				// Nunca debe pasar
				e.printStackTrace();
			}
		}

		public List<String> getAsegInstructionList() {
			return asegInstructionList;
		}

		public List<String> getCsegInstructionList() {
			return csegInstructionList;
		}

		public List<String> getDsegInstructionList() {
			return dsegInstructionList;
		}

		public TreeSet<AddressSegmentInfo> getAddressSegmentInfoSet() {
			return addressSegmentInfoSet;
		}

		public TreeSet<PublicLabel> getPublicLabelsSet() {
			return publicLabelsSet;
		}

		public TreeSet<ExternalLabelOcurrence> getExternalLabelsOcurrencesSet() {
			return externalLabelsOcurrencesSet;
		}

		public String getFilename() {
			return filename;
		}
	}

	/**
	 * Colección de InstructionWrapper
	 */
	private class InstructionCollection implements Iterable<InstructionWrapper> {
		private List<InstructionWrapper> instructions;

		public InstructionCollection() {
			instructions = new ArrayList<InstructionWrapper>();
		}

		/**
		 * Retorna el InstructionWrapper correspondiente al segmento y offset
		 * indicadp.
		 * 
		 * @param segment Segmento en que debe estar la instrucción.
		 * @param offset Offset que debe contener la instrucción.
		 * @return InstructionWrapper correspondiente a la consulta.
		 */
		public InstructionWrapper getInstruction(Segment segment, int offset) {
			for (InstructionWrapper instruction : instructions) {
				if (instruction.segment.equals(segment)
						&& (offset >= instruction.address - instruction.length && offset <= instruction.address
								+ instruction.length)) {
					return instruction;

				}
			}
			return null;
		}

		public void addAll(Segment segment, List<String> instructions) {
			// TODO: aca no habra que hacer nada mas?
			for (String instruction : instructions) {
				this.instructions.add(new InstructionWrapper(segment, instruction));
			}
		}

		//@Override
		public Iterator<InstructionWrapper> iterator() {
			return instructions.iterator();
		}

	}

	/**
	 * Wrapper que contiene una instrucción codificada en un String junto con el
	 * segmento, la dirección (inicial al momento de iniciar la linkedición) y
	 * el largo de la misma.
	 */
	private class InstructionWrapper {
		private Segment segment;
		private int address;
		private int length;
		private String instruction;

		public InstructionWrapper(Segment segment, String instruction) {
			this.segment = segment;
			this.instruction = instruction;
			this.address = hexAnalyzer.getAddress(instruction);
			this.length = hexAnalyzer.getLength(instruction);
		}

		public void setInstruction(String instruction) {
			this.instruction = instruction;
		}

		public String getInstruction() {
			return instruction;
		}

		public Segment getSegment() {
			return segment;
		}
	}

}
