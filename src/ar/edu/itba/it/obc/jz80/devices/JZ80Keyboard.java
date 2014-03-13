package ar.edu.itba.it.obc.jz80.devices;


public class JZ80Keyboard extends JZ80AbstractDevice {

	private byte data;
	private boolean control;

	public JZ80Keyboard() {
		reset();
	}

	public boolean isReadableAt(int address) {
		return (address & ~1) == 0;
	}

	public boolean isWriteableAt(int address) {
		return false;
	}

	public byte readByteAt(int address) {
		byte retval = 0;
		if (address == 0) {
			retval = data;
			control = false;
		} else if (address == 1) {
			retval = (byte) (control ? 1 : 0);
		}
		return retval;
	}

	public void reset() {
		data = 0;
		control = false;
	}

	public void writeByteAt(int address, byte value) {
		return;
	}

	public void setKeyDown(int character) {
		data = Integer.valueOf(character).byteValue();
		control = true;
	}

	@Override
	public int getSize() {
		return 2;
	}
	
}

