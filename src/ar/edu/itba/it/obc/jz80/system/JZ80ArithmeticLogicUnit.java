package ar.edu.itba.it.obc.jz80.system;

import ar.edu.itba.it.obc.jz80.api.Operand;
import ar.edu.itba.it.obc.jz80.api.Register;
import ar.edu.itba.it.obc.jz80.instructions.JZ80IndirectRegister;

public abstract class JZ80ArithmeticLogicUnit {
	
	// Métodos abstractos para la implementación de algunas instrucciones

	public abstract Register getRegister(JZ80RegisterName r);

	public abstract JZ80IndirectRegister getIndexedRegister(JZ80RegisterName r,
			int offset);

	// Métodos auxiliares para manejo de flags

	public int getFlags() {
		return getRegister(JZ80RegisterName.F).readValue();
	}

	public void writeFlags(int flags) {
		getRegister(JZ80RegisterName.F).writeValue(flags);
	}

	private int setFlag(int value, JZ80Flag f) {
		return (value | f.getMask());
	}

	private int resetFlag(int value, JZ80Flag f) {
		return (value & ~f.getMask());
	}

	private boolean readFlag(int value, JZ80Flag f) {
		return (value & f.getMask()) != 0;
	}

	private int writeFlag(int value, JZ80Flag f, boolean bit) {
		return (bit ? setFlag(value, f) : resetFlag(value, f));
	}

	private int checkOverflow16(int flags, int op1, int op2, int result) {
		return checkOverflow8(flags, op1 >> 8, op2 >> 8, result >> 8);
	}

	private int checkOverflow8(int flags, int op1, int op2, int result) {
		boolean sign1 = ((op1 & 0x80) != 0);
		boolean sign2 = ((op2 & 0x80) != 0);
		boolean signr = ((result & 0x80) != 0);
		boolean overflow = ((sign1 == sign2) && (sign1 != signr));
		return writeFlag(flags, JZ80Flag.V, overflow);
	}

	private int checkParity(int flags, int result) {
		byte b = ((byte) (result & 0xFF));
		boolean odd = false;
		for (int i = 0; i < 8; i++) {
			if ((b & 1) != 0) {
				odd = !odd;
			}
			b >>= 1;
		}
		return writeFlag(flags, JZ80Flag.P, odd);
	}

	private int checkFlags8(int flags, int result) {
		flags = writeFlag(flags, JZ80Flag.S, (result & 0x80) != 0);
		flags = writeFlag(flags, JZ80Flag.Z, (result & 0xFF) == 0);
		flags = writeFlag(flags, JZ80Flag.C, result >= 0x100);
		return flags;
	}

	private int checkFlags16(int flags, int result) {
		flags = writeFlag(flags, JZ80Flag.S, (result & 0x8000) != 0);
		flags = writeFlag(flags, JZ80Flag.Z, (result & 0xFFFF) == 0);
		flags = writeFlag(flags, JZ80Flag.C, result >= 0x10000);
		return flags;
	}

	private int checkRotationFlags(int flags, int result, boolean carry,
			boolean checkSZP) {
		if (checkSZP) {
			flags = checkFlags8(flags, result);
			flags = checkParity(flags, result);
		}
		flags = writeFlag(flags, JZ80Flag.C, carry);
		flags = resetFlag(flags, JZ80Flag.H);
		flags = resetFlag(flags, JZ80Flag.N);
		return flags;
	}

	// Métodos que implementan la ejecución de instrucciones

	public int adc(Operand op, int val) {
		int c = readFlag(getFlags(), JZ80Flag.C) ? 1 : 0;
		return add(op, val + c);
	}

	public int add(Operand op, int val) {
		int x = op.readValue();
		int y = val;
		int result;
		// Flags
		int flags = getFlags();
		if (op.getByteSize() < 2) {
			result = (x & 0xFF) + (y & 0xFF);
			flags = checkFlags8(flags, result);
			flags = checkOverflow8(flags, x, y, result);
			flags = writeFlag(flags, JZ80Flag.H,
					(x & 0x0F) + (y & 0x0F) >= 0x10);
		} else {
			result = (x & 0xFFFF) + (y & 0xFFFF);
			flags = checkFlags16(flags, result);
			flags = checkOverflow16(flags, x, y, result);
		}
		flags = resetFlag(flags, JZ80Flag.N);
		op.writeValue(result);
		return flags;
	}

	public int and(Operand op, int val) {
		int result = op.readValue() & val;
		op.writeValue(result);
		// Flags
		int flags = getFlags();
		flags = checkFlags8(flags, result);
		flags = checkParity(flags, result);
		flags = resetFlag(flags, JZ80Flag.H);
		flags = resetFlag(flags, JZ80Flag.N);
		flags = resetFlag(flags, JZ80Flag.C);
		return flags;
	}

	public int bit(Operand op, int bit) {
		int val = op.readValue();
		int mask = 1 << (bit + 1);
		// Flags
		int flags = getFlags();
		flags = writeFlag(flags, JZ80Flag.Z, (val & mask) == 0);
		flags = setFlag(flags, JZ80Flag.H);
		flags = resetFlag(flags, JZ80Flag.N);
		return flags;
	}

	public void call(int address) {
		Register pc = getRegister(JZ80RegisterName.PC);
		push(pc);
		pc.writeValue(address);
	}

	public int ccf() {
		int flags = getFlags();
		boolean c = readFlag(flags, JZ80Flag.C);
		flags = writeFlag(flags, JZ80Flag.H, c);
		flags = writeFlag(flags, JZ80Flag.N, false);
		flags = writeFlag(flags, JZ80Flag.C, !c);
		return flags;
	}

	public int cp(Operand op, int val) {
		int dst = op.readValue();
		int result = dst - val;
		// Flags
		int flags = getFlags();
		flags = checkFlags8(flags, result);
		flags = checkOverflow8(flags, dst, val, result);
		flags = writeFlag(flags, JZ80Flag.H,
				(dst & 0x0F) + (val & 0x0F) >= 0x10);
		flags = setFlag(flags, JZ80Flag.N);
		return flags;
	}

	public int cpl(Operand op, int val) {
		op.writeValue(~val);
		// Flags
		int flags = getFlags();
		flags = setFlag(flags, JZ80Flag.H);
		flags = setFlag(flags, JZ80Flag.N);
		return flags;
	}

	public int daa(Operand op, int val) {
		int flags = getFlags();
		int delta = 0;
		if (val > 0x99 || readFlag(flags, JZ80Flag.C)) {
			delta |= 0x60;
		} // else the upper nibble of correctionFactor is 0
		if ((val & 0x0F) > 9 || readFlag(flags, JZ80Flag.H)) {
			delta |= 0x06;
		} // else the lower nibble of correctionFactor is 0
		int result = readFlag(flags, JZ80Flag.N) ? val - delta : val + delta;
		op.writeValue(result);
		// Flags
		flags = checkFlags8(flags, result);
		flags = checkParity(flags, result);
		flags = writeFlag(flags, JZ80Flag.C, (delta & 0xF0) == 0x60);
		flags = writeFlag(flags, JZ80Flag.H, ((val ^ result) & 0x10) != 0);
		return flags;
	}

	public int dec(Operand op) {
		boolean c = readFlag(getFlags(), JZ80Flag.C);
		int flags = sub(op, 1);
		flags = writeFlag(flags, JZ80Flag.C, c); // el carry no cambia
		return flags;
	}

	public void djnz(byte offset) {
		int flags = dec(getRegister(JZ80RegisterName.B));
		if (!readFlag(flags, JZ80Flag.Z)) {
			jr(offset);
		}
		// Los flags no cambian
	}

	public void ex(Operand op1, Operand op2) {
		int x = op1.readValue();
		int y = op2.readValue();
		op1.writeValue(y);
		op2.writeValue(x);
	}

	public int inc(Operand op) {
		boolean c = readFlag(getFlags(), JZ80Flag.C);
		int flags = add(op, 1);
		flags = writeFlag(flags, JZ80Flag.C, c); // el carry no cambia
		return flags;
	}

	public void jp(int address) {
		Register r = getRegister(JZ80RegisterName.PC);
		r.writeValue(address);
	}

	public void jp(int address, JZ80Flag f, boolean condition) {
		if (readFlag(getFlags(), f) == condition) {
			jp(address);
		}
	}

	public void jr(byte offset) {
		Register r = getRegister(JZ80RegisterName.PC);
		r.writeValue(r.readValue() + (int) offset);
	}

	public void jr(byte offset, JZ80Flag f, boolean condition) {
		if (readFlag(getFlags(), f) == condition) {
			jr(offset);
		}
	}

	public void halt() {
	}

	public void in(Operand op) {
		Register r = getRegister(JZ80RegisterName.A);
		r.writeValue(op.readValue());
	}

	public void mlt(Operand op) {
		int val = op.readValue();
		int x = (val & 0xFF00) >> 16;
		int y = (val & 0xFF);
		op.writeValue(x * y);
		// No altera flags
	}

	public void ld(Operand op1, Operand op2) {
		op1.writeValue(op2.readValue());
	}

	public int neg(Operand op, int val) {
		int result = (val == 0x80 ? val : -val) & 0xFF;
		op.writeValue(result);
		// Flags
		int flags = getFlags();
		flags = checkFlags8(flags, result);
		flags = writeFlag(flags, JZ80Flag.H, ((val ^ result) & 0x10) != 0);
		flags = writeFlag(flags, JZ80Flag.V, val == 0x80);
		flags = setFlag(flags, JZ80Flag.N);
		flags = writeFlag(flags, JZ80Flag.C, val != 0);
		return flags;
	}

	public void nop() {
	}

	public int or(Operand op, int val) {
		int result = op.readValue() | val;
		op.writeValue(result);
		// Flags
		int flags = getFlags();
		flags = checkFlags8(flags, result);
		flags = checkParity(flags, result);
		flags = resetFlag(flags, JZ80Flag.H);
		flags = resetFlag(flags, JZ80Flag.N);
		flags = resetFlag(flags, JZ80Flag.C);
		return flags;
	}

	public void out(Operand op) {
		Register r = getRegister(JZ80RegisterName.A);
		op.writeValue(r.readValue());
	}

	public void pop(Operand op) {
		Register r = getRegister(JZ80RegisterName.SP);
		JZ80IndirectRegister lsb = getIndexedRegister(JZ80RegisterName.SP, 1);
		JZ80IndirectRegister msb = getIndexedRegister(JZ80RegisterName.SP, 0);
		int val = (msb.readValue() << 8) | lsb.readValue();
		op.writeValue(val);
		r.writeValue(r.readValue() + 2);
	}

	public void push(Operand op) {
		Register r = getRegister(JZ80RegisterName.SP);
		JZ80IndirectRegister lsb = getIndexedRegister(JZ80RegisterName.SP, -1);
		JZ80IndirectRegister msb = getIndexedRegister(JZ80RegisterName.SP, -2);
		int val = op.readValue();
		lsb.writeValue(val);
		msb.writeValue(val >> 8);
		r.writeValue(r.readValue() - 2);
	}

	public void res(Operand op, int bit) {
		int val = op.readValue();
		int mask = 1 << bit;
		op.writeValue(val & ~mask);
	}

	public void ret() {
		pop(getRegister(JZ80RegisterName.PC));
	}

	private int rl(Operand op, int val, boolean checkSZP) {
		int flags = getFlags();
		int carry = readFlag(flags, JZ80Flag.C) ? 1 : 0;
		int result = (val << 1) | carry;
		op.writeValue(result);
		// Flags
		flags = checkRotationFlags(flags, result, (val & 0x80) != 0, checkSZP);
		return flags;
	}

	public int rl(Operand op, int val) {
		return rl(op, val, true);
	}

	public int rla() {
		Register r = getRegister(JZ80RegisterName.A);
		return rl(r, r.readValue(), false);
	}

	private int rlc(Operand op, int val, boolean checkSZP) {
		int carry = ((val & 0x80) != 0) ? 1 : 0;
		int result = (val << 1) | carry;
		op.writeValue(result);
		// Flags
		int flags = getFlags();
		flags = checkRotationFlags(flags, result, carry != 0, checkSZP);
		return flags;
	}

	public int rlc(Operand op, int val) {
		return rlc(op, val, true);
	}

	public int rlca() {
		Register r = getRegister(JZ80RegisterName.A);
		return rlc(r, r.readValue(), false);
	}

	public int rld(Operand op1, Operand op2) {
		// Input: A = nibble4 nibble3
		// (HL) = nibble2 nibble1
		// Output: A = nibble4 nibble2
		// (HL) = nibble1 nibble3
		int x = op1.readValue();
		int y = op2.readValue();
		op1.writeValue((x & 0xF0) | ((y & 0xF0) >> 4));
		int result = ((x & 0x0F) << 4) | (y & 0x0F);
		op2.writeValue(result);
		// Flags
		int flags = getFlags();
		boolean c = readFlag(flags, JZ80Flag.C);
		flags = checkRotationFlags(flags, result, c, true);
		return flags;
	}

	private int rr(Operand op, int val, boolean checkSZP) {
		int flags = getFlags();
		int carry = readFlag(flags, JZ80Flag.C) ? 0x80 : 0;
		int result = (val >> 1) | carry;
		op.writeValue(result);
		// Flags
		flags = checkRotationFlags(flags, result, (val & 1) != 0, checkSZP);
		return flags;
	}

	public int rr(Operand op, int val) {
		return rr(op, val, true);
	}

	public int rra() {
		Register r = getRegister(JZ80RegisterName.A);
		return rr(r, r.readValue(), false);
	}

	private int rrc(Operand op, int val, boolean checkSZP) {
		int carry = ((val & 1) != 0) ? 0x80 : 0;
		int result = (val >> 1) | carry;
		op.writeValue(result);
		// Flags
		int flags = getFlags();
		flags = checkRotationFlags(flags, result, carry != 0, checkSZP);
		return flags;
	}

	public int rrc(Operand op, int val) {
		return rrc(op, val, true);
	}

	public int rrca() {
		Register r = getRegister(JZ80RegisterName.A);
		return rrc(r, r.readValue(), false);
	}

	public int rrd(Operand op1, Operand op2) {
		// Input: A = nibble4 nibble3
		// (HL) = nibble2 nibble1
		// Output: A = nibble4 nibble1
		// (HL) = nibble3 nibble2
		int x = op1.readValue();
		int y = op2.readValue();
		op1.writeValue((x & 0xF0) | (y & 0x0F));
		int result = ((x & 0x0F) << 4) | ((y & 0xF0) >> 4);
		op2.writeValue(result);
		// Flags
		int flags = getFlags();
		boolean c = readFlag(flags, JZ80Flag.C);
		flags = checkRotationFlags(flags, result, c, true);
		return flags;
	}

	public void rst(int address) {
		Register r = getRegister(JZ80RegisterName.PC);
		push(r);
		r.writeValue(address & 0xFF);
	}

	public int sbc(Operand op, int val) {
		int c = readFlag(getFlags(), JZ80Flag.C) ? 1 : 0;
		return sub(op, val + c); // TODO: Revisar esta suma de carry
	}

	public int scf() {
		// Flags
		int flags = getFlags();
		flags = writeFlag(flags, JZ80Flag.H, false);
		flags = writeFlag(flags, JZ80Flag.N, false);
		flags = writeFlag(flags, JZ80Flag.C, true);
		return flags;
	}

	public void set(Operand op, int bit) {
		int val = op.readValue();
		int mask = 1 << bit;
		op.writeValue(val | mask);
	}

	public int sla(Operand op, int val) {
		int result = val << 1;
		op.writeValue(result);
		// Flags
		int flags = getFlags();
		flags = checkRotationFlags(flags, result, (val & 0x80) != 0, true);
		return flags;
	}

	public int sra(Operand op, int val) {
		int result = (val >> 1) | (val & 0x80);
		op.writeValue(result);
		// Flags
		int flags = getFlags();
		flags = checkRotationFlags(flags, result, (val & 1) != 0, true);
		return flags;
	}

	public int srl(Operand op, int val) {
		int result = (val >> 1) & 0x7F;
		op.writeValue(result);
		// Flags
		int flags = getFlags();
		flags = checkRotationFlags(flags, result, (val & 1) != 0, true);
		return flags;
	}

	public int sub(Operand op, int val) {
		int x = op.readValue();
		int y = -val;
		int result;
		// Flags
		int flags = getFlags();
		if (op.getByteSize() < 2) {
			result = (x & 0xFF) + (y & 0xFF);
			flags = checkFlags8(flags, result);
			flags = checkOverflow8(flags, x, y, result);
			flags = writeFlag(flags, JZ80Flag.H,
					(x & 0x0F) + (x & 0x0F) >= 0x010);
		} else {
			result = (x & 0xFFFF) + (y & 0xFFFF);
			flags = checkFlags16(flags, result);
			flags = checkOverflow16(flags, x, y, result);
		}
		flags = writeFlag(flags, JZ80Flag.N, true);
		op.writeValue(result);
		return flags;
	}

	public int xor(Operand op, int val) {
		int result = op.readValue() ^ val;
		op.writeValue(result);
		// Flags
		int flags = getFlags();
		flags = checkFlags8(flags, result);
		flags = checkParity(flags, result);
		flags = resetFlag(flags, JZ80Flag.H);
		flags = resetFlag(flags, JZ80Flag.N);
		flags = resetFlag(flags, JZ80Flag.C);
		return flags;
	}

}
