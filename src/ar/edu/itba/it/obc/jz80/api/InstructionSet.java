package ar.edu.itba.it.obc.jz80.api;

/**
 * InstructionSet is the interface for classes that can decode the raw bytes of
 * an instruction code into an Instruction.
 * 
 * @author scoffey
 * 
 */
public interface InstructionSet {

	/**
	 * Decodes the raw bytes of an instruction code into an Instruction. The raw
	 * bytes are at most 4 and come in the highest bytes of the int parameter.
	 * 
	 * @param fetchedBytes Raw bytes of the instruction code (starting at the
	 *            MSB of this int).
	 * @return Decoded Instruction.
	 * @throws InstructionException Any exception due to inexistent or invalid
	 *             instruction.
	 */
	public Instruction decode(int fetchedBytes) throws InstructionException;

	/**
	 * Decodes the raw bytes of an instruction code into an opcode. An opcode is
	 * defined as the significant part of the instruction code that determines
	 * its behavior, ignoring the bytes related to immediate values (such as
	 * 0xCAFE in "ld HL, CAFE") and the header and offset of indexed addressing
	 * instructions (such as 0xDD and 0x02 in "ld A, (IX+2)"). Thus, according
	 * to the Z80 specification, an opcode is a single byte, except for an
	 * optional 0xCB or 0xED prefix. The returned int is zeroed at the fourth
	 * and third bytes (MSB bytes), then the second byte might be the prefix
	 * 0xCB, 0xED or 0x00, and the first byte (LSB) is the rest of the opcode.
	 * 
	 * @param fetchedBytes Raw bytes of the instruction code (starting at the
	 *            MSB of this int).
	 * @return Opcode of the instruction code (at the LSB of the returned int,
	 *         with 0xCB, 0xED or 0x00 prefix at the second byte).
	 */
	public int getOpcode(int fetchedBytes);

}
