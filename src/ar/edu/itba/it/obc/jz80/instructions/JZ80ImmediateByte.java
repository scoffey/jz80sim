package ar.edu.itba.it.obc.jz80.instructions;

import ar.edu.itba.it.obc.jz80.api.ByteOperand;

public class JZ80ImmediateByte extends ByteOperand {

	private byte value;
	
	public JZ80ImmediateByte(byte v) {
		value = v;
	}

	@Override
	public String getName() {
		return toString();
	}

	@Override
	public byte readByte() {
		return value;
	}

	@Override
	public void writeByte(byte value) {
	}

}
