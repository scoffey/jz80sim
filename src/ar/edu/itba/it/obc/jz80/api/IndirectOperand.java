package ar.edu.itba.it.obc.jz80.api;

/**
 * IndirectOperand is an extended interface for Operand that provides methods to
 * read and write a word (2 bytes) in the target operand in addition to reading
 * and writing a byte. This is useful for indirect operands whose behaviour is
 * normally one of a ByteOperand but can also work as a WordOperand.
 * 
 * @author scoffey
 * 
 */
public interface IndirectOperand extends Operand {

	/**
	 * @return Word (2 bytes) read from the indirect operand.
	 */
	public int readIndirectWord();

	/**
	 * @param value Word (2 bytes) to be written to the indirect operand.
	 */
	public void writeIndirectWord(int value);

}
