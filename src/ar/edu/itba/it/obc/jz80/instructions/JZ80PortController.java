package ar.edu.itba.it.obc.jz80.instructions;

import ar.edu.itba.it.obc.jz80.api.ByteOperand;
import ar.edu.itba.it.obc.jz80.api.Z80System;

public class JZ80PortController extends ByteOperand {

	private Z80System system;
	
	private byte port;

	public JZ80PortController(Z80System s, byte port) {
		this.system = s;
		this.port = port;
	}

	@Override
	public byte readByte() {
		return system.readInputAt(port);
	}

	@Override
	public void writeByte(byte value) {
		system.writeOutputAt(port, (byte) value);
	}

	@Override
	public String getName() {
		return String.format("(%02X)", (int) port);
	}

}
