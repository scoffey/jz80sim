package ar.edu.itba.it.obc.jz80.api;

import java.util.Iterator;

/**
 * Memory is the interface for any memory (byte array) of a Z80System including
 * the main RAM. It extends the Device interface as it must provide methods to
 * read and write bytes at a given address. The extensions also provide methods
 * to read a block of bytes (byte array), fetch an instruction at a given
 * address and manage listeners in order to participate in Observer patterns.
 * 
 * @author scoffey
 * 
 */
public interface Memory extends Device {
	
	// TODO: Documentar

	public byte[] readByteArrayAt(int address, int size);

	public int fetchInstructionAt(int address);

	public void addListener(DeviceListener listener);

	public void removeListener(DeviceListener listener);

	public Iterator<DeviceListener> getListeners();

	public void setListenersEnabled(boolean enabled);

	public void triggerAllListenersAt(int address);

}
