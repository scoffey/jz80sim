package ar.edu.itba.it.obc.jzas.semantic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.TreeSet;

import ar.edu.itba.it.obc.jzas.util.NumberConversionUtil;

public class HexDumper implements ObjectiveCodeDumper {
	private TreeSet<String> instructionsList;
	private FileWriter fw;

	public HexDumper(String file) {

		this.instructionsList = new TreeSet<String>(new Comparator<String>() {
			// @Override
			public int compare(String o1, String o2) {
				int address1 = NumberConversionUtil.parseBigEndianHex(o1.substring(3, 7));
				int address2 = NumberConversionUtil.parseBigEndianHex(o2.substring(3, 7));
				int len1 = NumberConversionUtil.parseHex(o1.substring(1, 3));
				int len2 = NumberConversionUtil.parseHex(o2.substring(1, 3));
				/* Si se solapan devolver la primera que fue escrita */
				if ((address1 >= address2 && address1 < address2 + len2)
						|| (address1 + len1 >= address2 && address1 + len1 < address2 + len2)) {
					return -1;
				}
				return address1 - address2;
			}
		});

		try {
			this.fw = new FileWriter(new File(file));
		} catch (IOException e) {
			System.err.println("Error opening output file " + file);
			System.exit(0);
		}
	}

	// @Override
	public void allocMemory(Integer offset, String data) {
		writeDataRecord(offset, data);
	}

	// @Override
	public void dumpInstruction(OCRecord i) {
		writeDataRecord(i.getAddress(), i);
	}

	// @Override
	public void eof() {
		writeln(":00000001FF");
	}

	// @Override
	public void startASegSection() {
		writeln("ASEG");
	}

	// @Override
	public void startCSegSection() {
		writeln("CSEG");
	}

	// @Override
	public void startDSegSection() {
		writeln("DSEG");
	}

	// @Override
	public void close() {
		try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// @Override
	public void writeStringLine(String s) {
		writeln(s);
	}

	private void writeDataRecord(Integer offset, OCRecord i) {
		writeDataRecord(offset, NumberConversionUtil.bin2Hex(i.getCodification()));
	}

	/**
	 * 
	 * @param offset offset donde debe ir los datos
	 * @param data Datos codificados en hexa
	 */
	private void writeDataRecord(Integer offset, String data) {
		String record = NumberConversionUtil.toHex(data.length() / 2, 2) + NumberConversionUtil.toHex(offset, 4) + "00"
				+ data;
		byte checksum = 0;
		for (int j = 0; j < record.length(); j += 2) {
			checksum += (byte) Integer.parseInt(record.substring(j, j + 2), 16);
		}
		instructionsList.add(":" + record
				+ NumberConversionUtil.bin2Hex(NumberConversionUtil.toBin(-checksum, 32).substring(24)));
	}

	private void writeln(String s) {
		try {
			fw.write(s + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void dumpInstructionsList() {
		for (String s : instructionsList) {
			writeln(s);
		}
		instructionsList.clear();
	}

	// @Override
	public void endASegSection() {
		dumpInstructionsList();
	}

	// @Override
	public void endCSegSection() {
		dumpInstructionsList();
	}

	// @Override
	public void endDSegSection() {
		dumpInstructionsList();
	}
}
