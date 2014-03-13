package ar.edu.itba.it.obc.jz80.instructions;

import ar.edu.itba.it.obc.jz80.system.JZ80Processor;
import ar.edu.itba.it.obc.jz80.api.ByteOperand;
import ar.edu.itba.it.obc.jz80.api.DeviceListener;
import ar.edu.itba.it.obc.jz80.api.Register;
import ar.edu.itba.it.obc.jz80.api.Processor;
import ar.edu.itba.it.obc.jz80.api.Z80System;

public class JZ808BitRegister extends ByteOperand implements Register {

	protected Z80System system;

	private int index;

	public JZ808BitRegister(Z80System s, int i) {
		system = s;
		index = i;
	}

	public void addListener(DeviceListener listener) {
		Processor p = system.getCPU();
		p.addListenerAt(index, listener);
	}

	public String getName() {
		return JZ80Processor.getRegisterName(index).toString();
	}

	@Override
	public byte readByte() {
		return system.getCPU().readByteAt(index);
	}

	@Override
	public void writeByte(byte value) {
		system.getCPU().writeByteAt(index, (byte) value);
	}

}
