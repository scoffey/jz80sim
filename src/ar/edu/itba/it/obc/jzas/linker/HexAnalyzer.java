package ar.edu.itba.it.obc.jzas.linker;

import ar.edu.itba.it.obc.jzas.util.NumberConversionUtil;

/**
 * Singleton que implementa la interfaz ObjectiveCodeAnalyzer.
 */
public class HexAnalyzer implements ObjectiveCodeAnalyzer {

	private static HexAnalyzer me = new HexAnalyzer();

	public HexAnalyzer() {
	}

	public static HexAnalyzer getInstance() {
		return me;
	}

	// @Override
	public int getAddress(String codifiedInstruction) {
		String bigEndianAddress = getAddressAsString(codifiedInstruction);
		if (bigEndianAddress == null) {
			return -1;
		} else {
			try {
				return NumberConversionUtil.parseBigEndianHex(bigEndianAddress);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return 0;
			}
		}
	}

	// @Override
	public String incrementInstructionData(String codifiedInstruction, int count) {
		/* Validar parámetros de entrada */
		if (count < 0) {
			return null;
		}

		if (!codifiedInstruction.substring(7, 9).equals("00")) {
			return null;
		}

		String littleEndianDataAddress = null;
		int dataAddress;
		// TODO: esta cuenta puede llegar a estar mal...
		int offset = codifiedInstruction.length() - 11 - 4;
		try {
			littleEndianDataAddress = codifiedInstruction.substring(8 + offset,
					8 + 4 + offset);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
		dataAddress = NumberConversionUtil
				.parseLittleEndianHex(littleEndianDataAddress);

		return codifiedInstruction.substring(0, 8 + offset)
				+ NumberConversionUtil.littleEndianHex(dataAddress + count)
				+ codifiedInstruction.substring(8 + offset);
	}

	// @Override
	public int getLength(String codifiedInstruction) {
		try {
			return Integer.parseInt(codifiedInstruction.substring(1, 3), 16);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return 0;
		}
	}

	// @Override
	public String incrementInstructionAddress(String codifiedInstruction,
			int count) {
		/* Validar parámetros de entrada */
		if (count < 0) {
			return null;
		}

		int dataAddress = getAddress(codifiedInstruction);
		String ret = codifiedInstruction.substring(1, 3)
				+ NumberConversionUtil.bigEndianHex(dataAddress + count)
				+ codifiedInstruction.substring(7,
						codifiedInstruction.length() - 2);

		byte checksum = 0;
		for (int j = 0; j < ret.length(); j += 2) {
			checksum += (byte) Integer.parseInt(ret.substring(j, j + 2), 16);
		}
		return ":"
				+ ret
				+ NumberConversionUtil.bin2Hex(NumberConversionUtil.toBin(
						-checksum, 32).substring(24));
	}

	/**
	 * Retorna el checksum de la instrucción codificada.
	 * 
	 * @param codifiedInstruction
	 *            Instrucción codificada
	 * @return Checksum de la instrucción
	 */
	public int getChecksum(String codifiedInstruction) {
		return NumberConversionUtil
				.parseHex(codifiedInstruction.substring(codifiedInstruction
						.length() - 2, codifiedInstruction.length()));
	}

	// @Override
	public String getAddressAsString(String codifiedInstruction) {
		try {
			return codifiedInstruction.substring(3, 7);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
}
