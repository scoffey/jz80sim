package ar.edu.itba.it.obc.jz80.api;

import java.io.File;

/**
 * Z80System is the interface for any implementation of the model of a system
 * containing a CPU, a main memory and a set of ports where devices can be
 * connected.
 * 
 * @author scoffey
 * 
 */
public interface Z80System {
	
	// TODO: Documentar

	public Memory getMemory();

	public Processor getCPU();

	public Device getDevice(int port);

	public byte readInputAt(int port);

	public void writeOutputAt(int port, byte value);

	public Device connectDevice(int port, int deviceAddress, Device device);

	public Device disconnectDevice(int port);

	public Device connectMultiplePortDevice(int firstPort, int lastPort,
			Device device);

	public Device disconnectMultiplePortDevice(int port);

	public boolean hasAvailablePortRange(int firstPort, int lastPort);

	public Instruction fetchInstructionAt(int address)
			throws InstructionException;

	public Instruction executeNext() throws InstructionException;

	public int readProgramCounter();

	public int readStackPointer();

	public void loadFileToMemory(File f) throws Exception;

	public void reset();

	public void resetDevices();

}
