package ar.edu.itba.it.obc.jz80.instructions;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ar.edu.itba.it.obc.jz80.util.SimpleCsvReader;

public class JZ80InstructionSet {

	private static final Pattern instructionCodingPattern = Pattern
			.compile("^(\\w+)\\s*([^,\\s]+)?,?\\s*([^,\\s]+)?$");

	private JZ80GenericInstruction[] shortCodes;
	private JZ80GenericInstruction[] cbCodes;
	private JZ80GenericInstruction[] edCodes;

	public JZ80InstructionSet() {
		shortCodes = new JZ80GenericInstruction[256];
		cbCodes = new JZ80GenericInstruction[256];
		edCodes = new JZ80GenericInstruction[256];
		try {
			loadInstructionSet(new SimpleCsvReader(getClass()
					.getResourceAsStream("/resources/data/opcodes.csv")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JZ80Instruction decode(int fetchedBytes)
			throws JZ80InvalidInstructionException {
		// Opcode parsing
		int opcode = getOpcode(fetchedBytes);
		int opcodeLSB = opcode & 0xFF;
		int opcodeMSB = opcode >> 8;
		int indexingPrefix = (fetchedBytes >> 24) & 0xFF;
		if (indexingPrefix != 0xDD && indexingPrefix != 0xFD)
			indexingPrefix = 0;
		// Generic instruction fetching
		JZ80GenericInstruction gi = null;
		if (opcodeMSB == 0) {
			gi = shortCodes[opcodeLSB];
		} else if (opcodeMSB == 0xCB) {
			gi = cbCodes[opcodeLSB];
		} else if (opcodeMSB == 0xED) {
			gi = edCodes[opcodeLSB];
		}
		if (gi == null) {
			throw new JZ80InvalidInstructionException(fetchedBytes);
		}
		// Return a common instruction or a special one for IX/IY cases
		return (indexingPrefix == 0) ? new JZ80Instruction(gi, fetchedBytes)
				: new JZ80IndexedInstruction(gi, fetchedBytes);
	}

	private void loadInstructionSet(SimpleCsvReader reader) throws IOException {
		List<String> row;
		int opcode;
		row = reader.getNextCsvRow(); // ignore header row
		while ((row = reader.getNextCsvRow()) != null) {
			try {
				opcode = Integer.parseInt(row.get(0), 16);
			} catch (NumberFormatException e) {
				continue;
			}
			if ((opcode & 0xFF) != opcode)
				continue;
			shortCodes[opcode] = parseGenericInstruction(opcode, row.get(1));
			cbCodes[opcode] = parseGenericInstruction((0xCB << 8) | opcode, row
					.get(2));
			edCodes[opcode] = parseGenericInstruction((0xED << 8) | opcode, row
					.get(3));
		}
	}

	private JZ80GenericInstruction parseGenericInstruction(int opcode,
			String code) {
		if (code == null || (code = code.trim()).length() == 0)
			return null;
		Matcher m = instructionCodingPattern.matcher(code);
		if (!m.find())
			return null;
		String mnemonic = m.group(1).toLowerCase();
		String op1 = m.group(2);
		String op2 = m.group(3);
		return new JZ80GenericInstruction(opcode, mnemonic, op1, op2);
	}

	/**
	 * Returns the instruction opcode, i.e., the instruction code without
	 * immediate values or the indexing prefix and offset bytes. The returned
	 * int is has the two highest bytes in zero and the lowest are set to the
	 * opcode prefix (0xCB, 0xED or 0) and the core part of the opcode in the
	 * LSB.
	 * 
	 * @param fetchedBytes 4-byte binary code fetched to decode the next
	 *            instruction
	 * @return opcode
	 */
	public int getOpcode(int fetchedBytes) {
		int code = fetchedBytes;
		int h = (fetchedBytes >> 24) & 0xFF;
		if (h == 0xFD || h == 0xDD) {
			h = (fetchedBytes >> 16) & 0xFF;
			if (h == 0x21 || h == 0x22 || h == 0x2A) {
				// para casos especiales sin offset dd: DDxxxxxx, FDxxxxxx
				code = fetchedBytes << 8;
			} else {
				// para casos con offset dd opcional de largo variable:
				// DDxx, DDxxdd, DDxxddxx, FDxx, FDxxdd, FDxxddxx
				code = (h << 24) | ((fetchedBytes << 16) & 0x00FF0000);
			}
		}
		// Considerar si el opcode simple es de 1 o 2 bytes.
		// (Los de 2 bytes siempre comienzan con CB o ED.)
		boolean isShort = !(h == 0xCB || h == 0xED);
		code >>= isShort ? 24 : 16;
		return (code & (isShort ? 0xFF : 0xFFFF));
	}

}
