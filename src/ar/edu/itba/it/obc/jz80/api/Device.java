package ar.edu.itba.it.obc.jz80.api;

/**
 * Device is the interface for all input/output devices in a Z80System. The
 * basic operations are to read and write a device register (one byte) of a
 * given address and to reset the device. A device should also return how many
 * registers can be addressed and which of them are readable (input) and
 * writable (output).
 * 
 * @author scoffey
 * 
 */
public interface Device {

	/**
	 * @return Number of registers that can be addressed in the device.
	 */
	public int getSize();

	/**
	 * @param address Address of a device register.
	 * @return Whether the device can be read at the given address.
	 */
	public boolean isReadableAt(int address);

	/**
	 * @param address Address of a device register.
	 * @return Whether the device can be written at the given address.
	 */
	public boolean isWriteableAt(int address);

	/**
	 * Reads a device register.
	 * 
	 * @param address Address of the device register to be read.
	 * @return Value (1 byte) read.
	 */
	public byte readByteAt(int address);

	/**
	 * Writes a device register.
	 * 
	 * @param address Address of the device register to be written.
	 * @param value Value (1 byte) to be written.
	 */
	public void writeByteAt(int address, byte value);

	/**
	 * Resets the device.
	 */
	public void reset();

}
