package ar.edu.itba.it.obc.jz80.system;

import java.io.*;

import ar.edu.itba.it.obc.jz80.api.Memory;
import ar.edu.itba.it.obc.jz80.api.Register;

public class JZ80MemoryLoader {

	JZ80System system;

	public JZ80MemoryLoader(JZ80System s) {
		system = s;
	}

	public void loadFile(File f) throws Exception {
		String filename = f.getName();
		String extension = filename.substring(filename.lastIndexOf(".") + 1)
				.toLowerCase();
		if (extension.equals("cpm")) {
			loadCpmFile(f);
		} else if (extension.equals("hex")) {
			loadHexFile(f);
		} else if (extension.equals("bin")) {
			loadBinFile(f);
		} else {
			throw new Exception("Invalid file extension: " + extension);
		}
	}

	public void loadCpmFile(File f) throws IOException {
		int i = 0;
		int j = 0;
		int startAddress = 0;
		int startOffset = 0;
		Memory memory = system.getMemory();
		Register pc = system.getCPU().getRegister(JZ80RegisterName.PC);
		byte[] buf = new byte[8];
		FileInputStream stream = new FileInputStream(f);
		while ((j = stream.read(buf, 0, 8)) != -1) {
			i += j;
			String s = new String(buf);
			if (s.indexOf("Lcode") == 0) {
				startOffset = buf[6] & 0xFF;
				if ((j = stream.read(buf, 0, 5)) != -1) {
					i += j;
					startAddress = buf[2] << 8;
					startAddress |= buf[1];
					startAddress &= 0xFFFF;
					while ((j = stream.read()) != -1) {
						i++;
						if (j == 0) {
							break;
						}
					}
					for (i = startAddress; (j = stream.read()) != -1; i++) {
						memory.writeByteAt(i, (byte) j);
					}
				}
			}
		}
		pc.writeValue(startAddress + startOffset);
		stream.close();
	}

	public void loadHexFile(File f) throws IOException {
		String line;
		BufferedReader bfr = new BufferedReader(new FileReader(f));
		for (int i = 1; (line = bfr.readLine()) != null; i++) {
			try {
				if (!loadHexLine(line, i))
					break;
			} catch (Exception e) {
				// TODO
				System.err.println(e.getMessage());
			}
		}
		bfr.close();
	}

	private boolean loadHexLine(String line, int number) throws Exception {
		if (line.trim().equals(""))
			return false;
		if (line.charAt(0) != ':') {
			throw new Exception(String.format("Line %d in hex file omitted: "
					+ "Does not begin with ':'", number));
		}
		int count = Integer.parseInt(line.substring(1, 3), 16);
		int address = Integer.parseInt(line.substring(3, 7), 16);
		int type = Integer.parseInt(line.substring(7, 9), 16);
		int dataSize = 2 * count;
		String data = line.substring(9, 9 + dataSize);
		int checksum = Integer.parseInt(line.substring(9 + dataSize,
				9 + dataSize + 2), 16);
		int actualChecksum = count + ((address >> 8) & 0xFF) + (address & 0xFF)
				+ type;
		Memory memory = system.getMemory();
		Register pc = system.getCPU().getRegister(JZ80RegisterName.PC);
		byte[] bs = new byte[count];
		for (int i = 0; i < bs.length; i++) {
			String b = data.substring(2 * i, 2 * i + 2);
			bs[i] = (byte) Integer.parseInt(b, 16);
			actualChecksum += bs[i];
		}
		actualChecksum = (0x100 - (actualChecksum & 0xFF)) & 0xFF;
		if (actualChecksum != checksum) {
			throw new Exception(String.format("Line %d in hex file omitted: "
					+ "Invalid checksum: %02X != %02X", number, actualChecksum,
					checksum));
		}
		if (number == 1) {
			// Por si falta el valor inicial de PC (entry point)
			pc.writeValue(address);
		}
		switch (type) {
		case 0:
			for (int i = 0; i < bs.length; i++) {
				memory.writeByteAt(address + i, bs[i]);
			}
			break;
		case 1:
			pc.writeValue(address); // valor inicial de PC (entry point)
			break;
		case 2:
		case 3:
		case 4:
		case 5:
			break;
		default:
			throw new Exception(String.format("Line %d in hex file omitted: "
					+ "Invalid record type", number));
			// break;
		}
		if (!line.substring(9 + dataSize + 2).trim().equals("")) {
			throw new Exception(String.format(
					"Warning: Trailing chars in hex file at line %d", number));
		}
		return (type != 1);
	}

	public void loadBinFile(File f) throws IOException {
		int b;
		Memory memory = system.getMemory();
		Register pc = system.getCPU().getRegister(JZ80RegisterName.PC);
		FileInputStream stream = new FileInputStream(f);
		for (int i = 0; (b = stream.read()) != -1; i++) {
			memory.writeByteAt(i, (byte) b);
		}
		pc.writeValue(0);
		stream.close();
	}

}
