package ar.edu.itba.it.obc.jz80.api;

/**
 * Instruction is the interface for all instructions in a Z80System. Some of the
 * most important responsiblities of such class is to be executed at a given
 * Z80System and to return some of its attributes such as: raw bytes from which
 * it was decoded, size in bytes, mnemonic, number of operands and destination
 * and source operands.
 * 
 * @author scoffey
 * 
 */
public interface Instruction {

	/**
	 * Executes the instruction at a Z80System.
	 * 
	 * @param s Z80System (context) where the instruction is executed.
	 * @throws InstructionException Any exception due to inexistent, invalid or
	 *             unimplemented instruction.
	 */
	public void execute(Z80System s) throws InstructionException;

	/**
	 * Returns the raw bytes that were fetched from memory to decode this
	 * instruction. These bytes are at most 4, starting at the MSB of the
	 * returned int.
	 * 
	 * @return Raw bytes fetched from memory to decode the instruction.
	 */
	public int getFetchedBytes();

	/**
	 * @return Size of the instruction code in bytes (1 to 4).
	 */
	public int getByteSize();

	/**
	 * Returns the mnemonic of the instruction. For example: "ld", "add",
	 * "push". The returned String contains only lower case letters.
	 * 
	 * @return Mnemonic of the instruction (e.g.: "ld", "add", etc.).
	 */
	public String getMnemonic();

	/**
	 * @return How many operands (none, one or two) there are in the String
	 *         representation of this instruction.
	 */
	public int getOperandCount();

	/**
	 * @param s Z80System (context) where the instruction is to be executed.
	 * @return Corresponding destination operand for this instruction.
	 */
	public Operand getDestinationOperand(Z80System s);

	/**
	 * @param s Z80System (context) where the instruction is to be executed.
	 * @return Corresponding source operand for this instruction.
	 */
	public Operand getSourceOperand(Z80System s);

	/**
	 * Returns the integer value of the first operand. This is useful for
	 * instructions such as "set", "res", "bit" and "rst".
	 * 
	 * @return Integer value of the first operand of this instruction.
	 */
	public int getIntegerOperand();

	/**
	 * Returns the flag condition code of the first operand. This is useful for
	 * conditional jump instructions such as "jp", "jr", "call" and "ret". The
	 * code ranges from 0 to 7 and represents the index for the following array
	 * of flag conditions: "NZ", "Z", "NC", "C", "PO", "PE", "P", "M".
	 * 
	 * @return Flag condition code of the first operand of this instruction.
	 */
	public int getConditionCode();

}
