package ar.edu.itba.it.obc.jz80.devices;

public class JZ80Terminal extends JZ80AbstractDevice {

	StringBuffer[] buffer = null;

	int currentRow = 0;

	int columns = 0;

	public JZ80Terminal(int cols, int rows) {
		resetListeners();
		buffer = new StringBuffer[rows];
		columns = cols;
		reset();
	}

	public boolean isReadableAt(int address) {
		return false;
	}

	public boolean isWriteableAt(int address) {
		return (address == 0);
	}

	public byte readByteAt(int address) {
		return 0;
	}

	public void reset() {
		currentRow = 0;
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = new StringBuffer(columns);
		}
	}

	public void writeByteAt(int address, byte value) {
		if (address != 0) {
			return;
		}
		int len = buffer[currentRow].length();
		if (len >= columns || value == '\n') {
			currentRow++;
			if (currentRow >= buffer.length) {
				for (int i = 1; i < currentRow; i++) {
					buffer[i - 1] = buffer[i];
				}
				currentRow = buffer.length - 1;
			}
			buffer[currentRow] = new StringBuffer(columns);
		}
		if (value != '\n')
			buffer[currentRow].append(Character.valueOf((char) value));
		triggerListenerEventAt(0);
	}

	public String getText() {
		StringBuffer s = new StringBuffer(buffer.length * (columns + 1));
		for (int i = 0; i < buffer.length; i++) {
			s.append(buffer[i]);
			s.append('\n');
		}
		return s.toString();
	}
	
	public int getColumns() {
		return columns;
	}
	
	public int getRows() {
		return buffer.length;
	}
	
	@Override
	public int getSize() {
		return 1;
	}

}
