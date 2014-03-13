package ar.edu.itba.it.obc.jz80.instructions;

import ar.edu.itba.it.obc.jz80.api.InstructionException;

public class JZ80InvalidInstructionException extends InstructionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int code;

	public JZ80InvalidInstructionException(int fetchedBytes) {
		code = fetchedBytes;
	}
	
	public int getInvalidInstructionCode() {
		return code;
	}

}
