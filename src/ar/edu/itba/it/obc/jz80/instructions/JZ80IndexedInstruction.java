package ar.edu.itba.it.obc.jz80.instructions;

public class JZ80IndexedInstruction extends JZ80Instruction {

	public JZ80IndexedInstruction(JZ80GenericInstruction generic,
			int fetchedBytes) throws JZ80InvalidInstructionException {
		super(generic, fetchedBytes);
		if (!((op1 != null && op1.contains("HL")) || (op2 != null && op2
				.contains("HL")))) { // pre-condition: instruction using HL
			throw new JZ80InvalidInstructionException(fetchedBytes);
		}
		int h = (fetchedBytes >> 24) & 0xFF;
		if (op1 != null) {
			if (h == 0xDD)
				op1 = op1.replace("HL", "IX");
			else if (h == 0xFD)
				op1 = op1.replace("HL", "IY");
		}
		if (op2 != null) {
			if (h == 0xDD)
				op2 = op2.replace("HL", "IX");
			else if (h == 0xFD)
				op2 = op2.replace("HL", "IY");
		}
	}

	public int getIndexedOperandOffset() {
		return (getFetchedBytes() >> 8) & 0xFF;
	}

	@Override
	public int getByteSize() {
		int len = super.getByteSize() + 1;
		// ahora falta sumar uno más si tiene un operando indexado
		if (len < 4 && op1 != null) {
			if (op1.charAt(0) == '(') {
				len++;
			} else if (op2 != null) {
				if (op2.charAt(0) == '(') {
					len++;
				}
			}
		}
		return len;
	}

}
