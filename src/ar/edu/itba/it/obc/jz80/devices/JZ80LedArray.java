package ar.edu.itba.it.obc.jz80.devices;

public class JZ80LedArray extends JZ80AbstractDevice {
	
	private byte[] data;
	
	public JZ80LedArray(int columns) {
		data = new byte[columns];
	}

	@Override
	public int getSize() {
		return data.length;
	}

	public boolean isReadableAt(int address) {
		return (address >= 0 && address < data.length);
	}

	public boolean isWriteableAt(int address) {
		return (address >= 0 && address < data.length);
	}

	public byte readByteAt(int address) {
		return data[address];
	}

	public void reset() {
		int columns = data.length;
		data = new byte[columns];
	}

	public void writeByteAt(int address, byte value) {
		data[address] = value;
	}

}
