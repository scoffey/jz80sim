/**
 * 
 */
package ar.edu.itba.it.obc.jz80.api;

/**
 * Operand is an interface for all operands used in the execution of an
 * instruction. It provides a layer of abstraction for the addressing modes when
 * the operands of an Instruction must be read or written in a Z80System.
 * 
 * @author scoffey
 * 
 */
public interface Operand {

	public int readValue();

	public void writeValue(int value);

	public int getByteSize();

	public String getName();

}
