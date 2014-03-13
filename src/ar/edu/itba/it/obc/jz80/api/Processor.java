package ar.edu.itba.it.obc.jz80.api;

import ar.edu.itba.it.obc.jz80.system.JZ80RegisterName;
// TODO: desacoplar JZ80RegisterName

/**
 * Processor is an interface for the CPU of a Z80System.
 * TODO
 */
public interface Processor extends Device {

	public Register getRegister(JZ80RegisterName r);

	public Register getIndirectRegister(JZ80RegisterName r);

	public Register getIndexedRegister(JZ80RegisterName r, int offset);

	public void addListenerAt(int address, DeviceListener listener);

	public void removeListenerAt(int address, DeviceListener listener);

	public void setListenersEnabled(boolean enabled);

	public void triggerAllListeners();

	public int fetchInstruction();

	public void incrementProgramCounterBy(int instructionLength);

	public void setInterruptEnabled(boolean enabled);

	public boolean isInterruptEnabled();

	public void exchangeRegisterPair(JZ80RegisterName r);

	public void execute(Instruction instruction) throws InstructionException;

}
