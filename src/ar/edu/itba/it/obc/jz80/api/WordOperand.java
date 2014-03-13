package ar.edu.itba.it.obc.jz80.api;

public abstract class WordOperand implements Operand {
	
	// TODO: Documentar como ByteOperand

	public int readValue() {
		return (readWord() & 0xFFFF);
	}

	public void writeValue(int value) {
		writeWord(value & 0xFFFF);
	}
	
	public int getByteSize() {
		return 2;
	}
	
	public String toString() {
		return String.format("%04X", readValue());
	}
	
	public abstract String getName();
	
	public abstract int readWord();

	public abstract void writeWord(int value);

}
