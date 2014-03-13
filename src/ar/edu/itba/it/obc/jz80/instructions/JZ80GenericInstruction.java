package ar.edu.itba.it.obc.jz80.instructions;

public class JZ80GenericInstruction implements
		Comparable<JZ80GenericInstruction> {

	private int opcode; // generic part of the instruction code (no immediate
	// operand, no indexing-related bytes)

	private int length; // length in bytes of the generic instruction code

	protected String mnemonic; // mnemonic of the instruction

	protected String op1; // first operand if any

	protected String op2; // second operand if any

	protected JZ80GenericInstruction(JZ80GenericInstruction i) {
		// Este constructor lo usa la subclase JZ80Instruction para
		// extender los atributos de una JZ80GenericInstruction
		opcode = i.opcode;
		length = i.length;
		mnemonic = i.mnemonic;
		op1 = i.op1;
		op2 = i.op2;
	}

	public JZ80GenericInstruction(int opcode, String mnemonic, String op1,
			String op2) {
		this.opcode = opcode;
		this.length = (opcode & 0xFF00) == 0 ? 1 : 2;
		this.mnemonic = mnemonic;
		this.op1 = op1;
		this.op2 = op2;
		String s = op1 + ", " + op2;
		if (s.contains("????")) {
			this.length += 2;
		} else if (s.contains("??")) {
			this.length += 1;
		}
	}

	public int getByteSize() {
		return length;
	}

	public String getMnemonic() {
		return mnemonic;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String s = getMnemonic();
		if (op1 != null) {
			s += " " + op1;
			if (op2 != null) {
				s += ", " + op2;
			}
		}
		return s;
	}

	/**
	 * Returns the generic instruction code in a string of hex digits.
	 * 
	 * @return a string of 2n hex digits representing the generic instruction
	 *         binary code where n is the instruction length in bytes.
	 */
	public String getCodeString() {
		int n = (length == 0) ? 4 : length;
		String f = "%0" + Integer.valueOf(n * 2).toString() + "X";
		return String.format(f, opcode << (8 * (n - 1)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final JZ80Instruction other = (JZ80Instruction) obj;
		return (compareTo(other) == 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(JZ80GenericInstruction o) {
		int c1 = opcode;
		int c2 = (o == null) ? 0 : o.opcode;

		return (c1 - c2);
	}

}
