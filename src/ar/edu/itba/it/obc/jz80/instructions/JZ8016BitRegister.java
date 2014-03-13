package ar.edu.itba.it.obc.jz80.instructions;

import ar.edu.itba.it.obc.jz80.api.DeviceListener;
import ar.edu.itba.it.obc.jz80.api.Processor;
import ar.edu.itba.it.obc.jz80.api.Register;
import ar.edu.itba.it.obc.jz80.api.WordOperand;
import ar.edu.itba.it.obc.jz80.api.Z80System;
import ar.edu.itba.it.obc.jz80.system.JZ80Processor;

public class JZ8016BitRegister extends WordOperand implements Register {

	protected Z80System system;

	private int msb;
	
	private int lsb;
	
	public JZ8016BitRegister(Z80System s, int i, int j) {
		system = s;
		msb = i;
		lsb = j;
	}

	public int readWord() {
		Processor p = system.getCPU(); 
		int n = p.readByteAt(msb);
		int m = p.readByteAt(lsb);
		return ((n << 8) | (m & 0xFF));
	}

	public void writeWord(int value) {
		Processor p = system.getCPU(); 
		byte n = (byte) (value >> 8);
		byte m = (byte) value;
		p.writeByteAt(msb, n);
		p.writeByteAt(lsb, m);
	}

	public void addListener(DeviceListener listener) {
		Processor p = system.getCPU();
		p.addListenerAt(msb, listener);
		p.addListenerAt(lsb, listener);
	}

	public String getName() {
		return JZ80Processor.getRegisterName(lsb, msb).toString();
	}
}
