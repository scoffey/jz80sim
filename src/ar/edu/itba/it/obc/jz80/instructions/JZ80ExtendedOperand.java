package ar.edu.itba.it.obc.jz80.instructions;

import ar.edu.itba.it.obc.jz80.api.IndirectOperand;
import ar.edu.itba.it.obc.jz80.api.Z80System;

public class JZ80ExtendedOperand extends JZ80ImmediateWord implements
		IndirectOperand {

	private Z80System system;

	public JZ80ExtendedOperand(Z80System s, int v) {
		super(v);
		system = s;
	}

	@Override
	public int readValue() {
		int address = super.readValue();
		return (int) system.getMemory().readByteAt(address);
	}

	@Override
	public void writeValue(int value) {
		int address = super.readValue();
		system.getMemory().writeByteAt(address, (byte) value);
	}

	@Override
	public int getByteSize() {
		return 1;
	}

	@Override
	public String getName() {
		return "(" + super.getName() + ")";
	}

	public int readIndirectWord() {
		int address = super.readValue();
		int msb = system.getMemory().readByteAt((address + 1) & 0xFFFF);
		return (msb << 8) | system.getMemory().readByteAt(address);
	}

	public void writeIndirectWord(int value) {
		int address = super.readValue();
		system.getMemory().writeByteAt((address + 1) & 0xFFFF,
				(byte) (value >> 8));
		system.getMemory().writeByteAt(address, (byte) value);
	}

}
