package ar.edu.itba.it.obc.jz80.api;

/**
 * ByteOperand es una clase abstracta que provee métodos para simplificar la
 * implementación de cualquier operando de tamaño de un byte (8 bits).
 * Implementa readValue y writeValue en base a los métodos abstractos readByte y
 * writeByte que las subclases deberán implementar, para evitar la
 * responsabilidad de controlar el tamaño en la lectura y escritura del
 * operando. Otros métodos que esta clase ya implementa son getByteSize y
 * toString.
 * 
 * @author scoffey
 * 
 */
public abstract class ByteOperand implements Operand {

	public int readValue() {
		return (((int) readByte()) & 0xFF);
	}

	public void writeValue(int value) {
		writeByte((byte) value);
	}

	public int getByteSize() {
		return 1;
	}

	public String toString() {
		return String.format("%02X", readValue());
	}

	public abstract String getName();

	/**
	 * Lee un byte del operando. (readValue luego se asegura que se convierte a
	 * un int con todos los bytes en cero excepto el LSB.)
	 * 
	 * @return byte leído del operando
	 */
	public abstract byte readByte();

	/**
	 * Escribe un byte en el operando. (writeValue se asegura que sólo se invoca
	 * con un byte en lugar de un int.)
	 * 
	 * @param value byte a escribir en el operando
	 */
	public abstract void writeByte(byte value);

}
