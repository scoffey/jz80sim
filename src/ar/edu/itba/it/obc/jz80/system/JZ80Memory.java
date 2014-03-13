package ar.edu.itba.it.obc.jz80.system;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import ar.edu.itba.it.obc.jz80.api.DeviceListener;
import ar.edu.itba.it.obc.jz80.api.Memory;

public class JZ80Memory implements Memory {

	private byte[] memory;

	private ArrayList<DeviceListener> listeners;

	private boolean listenersEnabled;

	public JZ80Memory() {
		memory = new byte[0x10000];
		listeners = new ArrayList<DeviceListener>();
		listenersEnabled = true;
	}

	public byte readByteAt(int address) {
		byte b = 0;
		try {
			b = memory[address];
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("JZ80Memory.readByteAt: "
					+ "Address out of bounds");
		}
		return b;
	}

	public byte[] readByteArrayAt(int address, int size) {
		byte[] array = new byte[size];
		try {
			for (int i = 0; i < size; i++) {
				array[i] = memory[(address + i) & 0xFFFF];
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("JZ80Memory.readByteArrayAt: "
					+ "Address out of bounds");
		}
		return array;
	}

	public int fetchInstructionAt(int address) {
		int instr = 0;
		try {
			for (int i = 0; i < 4; i++) {
				instr <<= 8;
				instr |= (memory[(address + i) & 0xFFFF] & 0xFF);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("JZ80Memory.fetchInstructionAt: "
					+ "Address out of bounds");
		}
		return instr;
	}

	public void writeByteAt(int address, byte value) {
		try {
			memory[address] = value;
			if (listenersEnabled) {
				for (DeviceListener listener : listeners) {
					listener.onWrite(this, address);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("JZ80Memory.writeByteAt: "
					+ "Address out of bounds");
		}
	}

	public boolean isReadableAt(int address) {
		return (address >= 0 && address < memory.length);
	}

	public boolean isWriteableAt(int address) {
		return (address >= 0 && address < memory.length);
	}

	public int getSize() {
		return memory.length;
	}

	public void reset() {
		int len = memory.length;
		memory = null;
		memory = new byte[len];
		// Acá habría que llamar a todos los listeners
		// sólo que por eficiencia no se hace porque hay que
		// hacerlo para todas las address posibles.
		// Usar runAllListenersAt luego del reset.
	}

	public void addListener(DeviceListener listener) {
		listeners.add(listener);
	}

	public void removeListener(DeviceListener listener) {
		listeners.remove(listener);
	}

	public Iterator<DeviceListener> getListeners() {
		return listeners.iterator();
	}

	public void setListenersEnabled(boolean enabled) {
		listenersEnabled = enabled;
	}

	public void triggerAllListenersAt(int address) {
		for (DeviceListener listener : listeners) {
			listener.onWrite(this, address);
		}
	}

	public void dump(InputStream stream, int address) throws IOException {
		// TODO: No funciona con streams ya abiertos?
		stream.read(memory, address, memory.length);
	}
}
