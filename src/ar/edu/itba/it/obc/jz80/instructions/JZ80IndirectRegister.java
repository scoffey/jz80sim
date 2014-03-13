package ar.edu.itba.it.obc.jz80.instructions;

import ar.edu.itba.it.obc.jz80.api.IndirectOperand;
import ar.edu.itba.it.obc.jz80.system.JZ80System;

public class JZ80IndirectRegister extends JZ8016BitRegister implements
		IndirectOperand {

	private int offset;

	public JZ80IndirectRegister(JZ80System s, int i, int j) {
		super(s, i, j);
		offset = 0;
	}

	public JZ80IndirectRegister(JZ80System s, int i, int j, int o) {
		super(s, i, j);
		offset = o;
	}

	@Override
	public int readValue() {
		int address = super.readValue() + offset;
		return (int) system.getMemory().readByteAt(address & 0xFFFF);
	}

	@Override
	public void writeValue(int value) {
		int address = super.readValue() + offset;
		system.getMemory().writeByteAt(address & 0xFFFF, (byte) value);
	}

	@Override
	public int getByteSize() {
		return 1;
	}

	public String getName() {
		String d = "";
		String s = super.getName();
		if (s.equals("IX") || s.equals("IY")) {
			d = "+" + String.format("%02X", offset);
		}
		return "(" + s + d + ")";
	}

	public int readIndirectWord() {
		int address = super.readValue() + offset;
		int msb = system.getMemory().readByteAt((address + 1) & 0xFFFF);
		return (msb << 8) | system.getMemory().readByteAt(address & 0xFFFF);
	}

	public void writeIndirectWord(int value) {
		int address = super.readValue() + offset;
		system.getMemory().writeByteAt((address + 1) & 0xFFFF,
				(byte) (value >> 8));
		system.getMemory().writeByteAt(address & 0xFFFF, (byte) value);
	}

}
