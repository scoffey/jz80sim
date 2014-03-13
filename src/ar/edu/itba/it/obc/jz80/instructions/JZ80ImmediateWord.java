package ar.edu.itba.it.obc.jz80.instructions;

import ar.edu.itba.it.obc.jz80.api.WordOperand;

public class JZ80ImmediateWord extends WordOperand {

	private int value;
	
	public JZ80ImmediateWord(int v) {
		value = v;
	}

	@Override
	public String getName() {
		return String.format("%04X", value & 0xFFFF);
	}

	@Override
	public int readWord() {
		return value;
	}

	@Override
	public void writeWord(int value) {
	}

}
