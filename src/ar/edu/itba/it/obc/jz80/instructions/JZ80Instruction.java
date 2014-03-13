package ar.edu.itba.it.obc.jz80.instructions;

import ar.edu.itba.it.obc.jz80.api.Instruction;
import ar.edu.itba.it.obc.jz80.api.InstructionException;
import ar.edu.itba.it.obc.jz80.api.Operand;
import ar.edu.itba.it.obc.jz80.api.Register;
import ar.edu.itba.it.obc.jz80.api.Z80System;
import ar.edu.itba.it.obc.jz80.system.JZ80Processor;
import ar.edu.itba.it.obc.jz80.system.JZ80RegisterName;

public class JZ80Instruction extends JZ80GenericInstruction implements
		Instruction {

	private int fetchedBytes;

	public JZ80Instruction(JZ80GenericInstruction generic, int fetchedBytes) {
		super(generic);
		this.fetchedBytes = fetchedBytes;
	}

	public void execute(Z80System s) throws InstructionException {
		s.getCPU().execute(this);
	}
	
	public int getFetchedBytes() {
		return fetchedBytes;
	}

	public int getImmediateOperandValue() {
		return fetchedBytes >> (8 * (4 - getByteSize()));
	}

	public int getOperandCount() {
		return (op2 == null ? (op1 == null ? 0 : 1) : 2);
	}

	public Operand getSourceOperand(Z80System s) {
		return getSourceOperand(s, JZ80RegisterName.A);
	}

	public Operand getSourceOperand(Z80System s, JZ80RegisterName implicit) {
		String op = op2;
		if (op == null) {
			op = op1;
			if (op == null) {
				return (implicit == null ? null : getDirectOperand(s, implicit
						.toString()));
			}
		}
		return getOperand(s, op);
	}

	public Operand getDestinationOperand(Z80System s) {
		return getDestinationOperand(s, JZ80RegisterName.A);
	}

	public Operand getDestinationOperand(Z80System s, JZ80RegisterName implicit) {
		String op = op1;
		if (op == null) {
			return (implicit == null ? null : getDirectOperand(s, implicit
					.toString()));
		}
		return getOperand(s, op);
	}

	public int getIntegerOperand() {
		try {
			return Integer.parseInt(op1, 16);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public int getConditionCode() {
		final String[] flagcodes = { "NZ", "Z", "NC", "C", "PO", "PE", "P", "M" };
		for (int i = 0; i < flagcodes.length; i++) {
			if (flagcodes[i].equals(op1))
				return i;
		}
		return 0;
	}

	private int decodeLittleEndian(int c) {
		return ((c << 8) & 0xFF00) | ((c >> 8) & 0xFF);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ar.edu.itba.it.obc.jz80.JZ80GenericInstruction#toString()
	 */
	@Override
	public String toString() {
		String s = getMnemonic();
		if (op1 != null) {
			s += " " + getOperandName(op1);
			if (op2 != null) {
				s += ", " + getOperandName(op2);
			}
		}
		return s;
	}

	// Las cadenas que parsea pueden representar operandos de 5 tipos:
	// DIRECT & IMPLICIT: A, B, C, D, E, F, H, L, R, I, AF, BC, DE, HL, IX, IY
	// INDIRECT: (AF), (BC), (DE), (HL), (IX), (IY)
	// IMMEDIATE & RELATIVE: ??, ????
	// EXTENDED & I/O: (??), (????)
	// Para INDEXED usar DIRECT (como con (HL) pero empezando con FD o DD).

	/**
	 * Returns the corresponding operand, binding it to a Z80 system so that it
	 * can be read and written according to its addressing mode.
	 * 
	 * @param s Z80 system
	 * @param op operand string
	 * @return operand
	 */
	public Operand getOperand(Z80System s, String op) {
		if (op == null)
			return null;
		op = op.trim().toUpperCase();
		// Addressing mode detection
		if (op.indexOf('?') >= 0) { // immediate or extended
			return (op.indexOf('(') >= 0) ? getExtendedOperand(s, op)
					: getImmediateOperand(s, op);
		} else { // direct or indirect or indexed
			return (op.indexOf('(') >= 0) ? getIndirectOperand(s, op)
					: getDirectOperand(s, op);
		}
	}

	private Operand getExtendedOperand(Z80System s, String op) {
		int c = getImmediateOperandValue();
		return (op.indexOf("????") < 0) ? new JZ80PortController(s, (byte) (c))
				: new JZ80ExtendedOperand(s, decodeLittleEndian(c));
	}

	private Operand getImmediateOperand(Z80System s, String op) {
		int c = getImmediateOperandValue();
		return (op.indexOf("????") < 0) ? new JZ80ImmediateByte((byte) c)
				: new JZ80ImmediateWord(decodeLittleEndian(c));
	}

	private Register getIndirectOperand(Z80System s, String op) {
		int lp = op.indexOf('(');
		int rp = op.indexOf(')');
		if (!(lp >= 0 && rp >= 0))
			return null;
		op = op.substring(lp + 1, rp).trim();
		JZ80RegisterName r = JZ80Processor.getRegisterName(op);
		if (this instanceof JZ80IndexedInstruction) { // TODO: overriding
			JZ80IndexedInstruction ii = (JZ80IndexedInstruction) this;
			return s.getCPU().getIndexedRegister(r,
					ii.getIndexedOperandOffset());
		}
		return s.getCPU().getIndirectRegister(r);
	}

	private Register getDirectOperand(Z80System s, String op) {
		if (op.length() > 2)
			op = op.substring(0, 2);
		JZ80RegisterName r = JZ80Processor.getRegisterName(op);
		return (r == null) ? null : s.getCPU().getRegister(r);
	}

	/**
	 * Returns a string representation of an operand, as commonly seen in
	 * Assembler program sources.
	 * 
	 * @param op
	 * @return
	 */
	public String getOperandName(String op) {
		if (op == null)
			return "?";
		op = op.trim().toUpperCase();
		// Addressing mode detection
		if (op.indexOf('?') >= 0) { // immediate or extended
			int mask = op.contains("????") ? 0xFFFF : 0xFF;
			String f = new String(op);
			int v = getImmediateOperandValue() & mask;
			if (mask == 0xFF) {
				return String.format(f.replace("??", "%02X"), v);
			} else {
				return String.format(f.replace("????", "%04X"),
						decodeLittleEndian(v));
			}
		} else if (this instanceof JZ80IndexedInstruction) { // TODO:
			// overriding
			JZ80IndexedInstruction ii = (JZ80IndexedInstruction) this;
			return (op.charAt(0) == '(') ? String.format("(%s+%02X)", op
					.substring(1, 3), ii.getIndexedOperandOffset()) : op;
		} else { // direct or indirect
			return op;
		}
	}

}
