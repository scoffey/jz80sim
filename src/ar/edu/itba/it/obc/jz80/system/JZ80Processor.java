package ar.edu.itba.it.obc.jz80.system;

import java.lang.reflect.Method;
import java.util.ArrayList;

import ar.edu.itba.it.obc.jz80.api.DeviceListener;
import ar.edu.itba.it.obc.jz80.api.IndirectOperand;
import ar.edu.itba.it.obc.jz80.api.Instruction;
import ar.edu.itba.it.obc.jz80.api.InstructionException;
import ar.edu.itba.it.obc.jz80.api.Operand;
import ar.edu.itba.it.obc.jz80.api.Processor;
import ar.edu.itba.it.obc.jz80.api.Register;
import ar.edu.itba.it.obc.jz80.instructions.JZ8016BitRegister;
import ar.edu.itba.it.obc.jz80.instructions.JZ808BitRegister;
import ar.edu.itba.it.obc.jz80.instructions.JZ80IndirectRegister;
import ar.edu.itba.it.obc.jz80.instructions.JZ80Instruction;
import ar.edu.itba.it.obc.jz80.instructions.JZ80InvalidInstructionException;
import ar.edu.itba.it.obc.jz80.instructions.JZ80PortController;

public class JZ80Processor extends JZ80ArithmeticLogicUnit implements
		Processor {

	private static final int REGISTERS_BYTE_SIZE = 18;

	private JZ80System system = null;

	private byte[] values;

	private byte[] altValues;

	private Register[] registers;

	private boolean interruptEnabled;

	private ArrayList<DeviceListener>[] listeners;

	private boolean listenersEnabled;

	@SuppressWarnings("unchecked")
	public JZ80Processor(JZ80System s) {
		JZ80RegisterName[] rs = JZ80RegisterName.values();
		system = s;
		values = new byte[REGISTERS_BYTE_SIZE];
		altValues = new byte[REGISTERS_BYTE_SIZE];
		registers = new Register[rs.length];
		interruptEnabled = false;
		listeners = new ArrayList[REGISTERS_BYTE_SIZE];
		listenersEnabled = true;
		int iord = JZ80RegisterName.I.ordinal();
		int aord = JZ80RegisterName.A.ordinal();
		for (JZ80RegisterName r : JZ80RegisterName.values()) {
			int o = r.ordinal();
			if (o < iord) {
				registers[o] = new JZ8016BitRegister(system, 2 * o, 2 * o + 1);
			} else if (o < aord) {
				registers[o] = new JZ808BitRegister(system, iord + o); // 2*iord+o-iord
			} else {
				registers[o] = new JZ808BitRegister(system, o - aord);
			}
		}
	}

	// Métodos de clase

	public static JZ80RegisterName getRegisterName(String s) {
		for (JZ80RegisterName r : JZ80RegisterName.values()) {
			if (r.toString().equals(s)) {
				return r;
			}
		}
		return null;
	}

	public static JZ80RegisterName getRegisterName(int index) {
		JZ80RegisterName[] names = JZ80RegisterName.values();
		int iord = JZ80RegisterName.I.ordinal();
		return (index + iord < names.length) ? names[index + iord] : null;
		// Cuidado: Depende de los ordinales
	}

	public static JZ80RegisterName getRegisterName(int lsb, int msb) {
		JZ80RegisterName[] names = JZ80RegisterName.values();
		return (lsb == msb + 1 && msb / 2 < names.length) ? names[msb / 2]
				: null;
		// Cuidado: Depende de los ordinales
	}

	// Métodos de instancia

	public Register getRegister(JZ80RegisterName r) {
		return registers[r.ordinal()];
	}

	public JZ80IndirectRegister getIndirectRegister(JZ80RegisterName r) {
		int o = r.ordinal();
		return new JZ80IndirectRegister(system, 2 * o, 2 * o + 1);
	}

	public JZ80IndirectRegister getIndexedRegister(JZ80RegisterName r,
			int offset) {
		int o = r.ordinal();
		return new JZ80IndirectRegister(system, 2 * o, 2 * o + 1, offset);
	}

	public int getSize() {
		return values.length;
	}

	public boolean isReadableAt(int address) {
		return (address >= 0 && address < values.length);
	}

	public boolean isWriteableAt(int address) {
		return (address >= 0 && address < values.length);
	}

	public byte readByteAt(int address) {
		byte val = 0;
		try {
			val = values[address];
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("JZ80Processor.readByteAt: "
					+ "Address out of bounds");
		}
		return val;
	}

	public void writeByteAt(int address, byte value) {
		try {
			values[address] = value;
			if (listenersEnabled && listeners[address] != null) {
				for (DeviceListener listener : listeners[address]) {
					listener.onWrite(this, address);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("JZ80Processor.writeByteAt: "
					+ "Address out of bounds");
		}
	}

	public void reset() {
		for (int i = 0; i < values.length; i++) {
			values[i] = 0;
			altValues[i] = 0;
			if (listeners[i] != null) {
				for (DeviceListener listener : listeners[i]) {
					listener.onWrite(this, i);
				}
			}
		}
	}

	public void addListenerAt(int address, DeviceListener listener) {
		try {
			if (listeners[address] == null) {
				listeners[address] = new ArrayList<DeviceListener>();
			}
			listeners[address].add(listener);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("JZ80Processor.addListenerAt: "
					+ "Address out of bounds");
		}
	}

	public void removeListenerAt(int address, DeviceListener listener) {
		try {
			if (listeners[address] != null) {
				listeners[address].remove(listener);
				if (listeners[address].isEmpty()) {
					listeners[address] = null;
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("JZ80Processor.removeListenerAt: "
					+ "Address out of bounds");
		}
	}

	public void setListenersEnabled(boolean enabled) {
		listenersEnabled = enabled;
	}

	public void triggerAllListeners() {
		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] != null) {
				for (DeviceListener listener : listeners[i]) {
					listener.onWrite(this, i);
				}
			}
		}
	}

	public int fetchInstruction() {
		int pc = getRegister(JZ80RegisterName.PC).readValue();
		return system.getMemory().fetchInstructionAt(pc);
	}

	public void incrementProgramCounterBy(int instructionLength) {
		Register r = getRegister(JZ80RegisterName.PC);
		int pc = r.readValue();
		r.writeValue(pc + instructionLength);
	}

	public void setInterruptEnabled(boolean enabled) {
		interruptEnabled = enabled;
	}

	public boolean isInterruptEnabled() {
		return interruptEnabled;
	}

	public void exchangeRegisterPair(JZ80RegisterName r) {
		int o = r.ordinal();
		if (o >= JZ80RegisterName.I.ordinal()) {
			return; // No está permitido exchange de registros de 8 bits
		}
		byte aux;
		o = 2 * o;
		aux = altValues[o];
		altValues[o] = values[o];
		values[o] = aux;
		o++;
		aux = altValues[o];
		altValues[o] = values[o];
		values[o] = aux;
	}
	
	public void execute(Instruction instruction) throws InstructionException {
		try {
			Class<JZ80Processor> c = JZ80Processor.class;
			Method m = c.getMethod(instruction.getMnemonic(),
					new Class<?>[] { JZ80Instruction.class });
			incrementProgramCounterBy(instruction.getByteSize());
			m.invoke(this, instruction);
		} catch (Exception e) {
			// TODO: discriminar NoSuchMethodException e
			// InvocationTargetException del resto
			JZ80InvalidInstructionException wrapper = new JZ80InvalidInstructionException(
					instruction.getFetchedBytes());
			wrapper.initCause(e);
			wrapper.setStackTrace(e.getStackTrace());
			throw wrapper;
		}
	}

	// Desde aquí hasta el final: Implementación de instrucciones

	public void adc(JZ80Instruction i) {
		Operand op1 = i.getDestinationOperand(system);
		Operand op2 = i.getSourceOperand(system);
		int flags = adc(op1, op2.readValue());
		writeFlags(flags);
	}

	public void add(JZ80Instruction i) {
		Operand op1 = i.getDestinationOperand(system);
		Operand op2 = i.getSourceOperand(system);
		int flags = add(op1, op2.readValue());
		writeFlags(flags);
	}

	public void and(JZ80Instruction i) {
		Operand op1 = i.getDestinationOperand(system);
		Operand op2 = i.getSourceOperand(system);
		int flags = and(op1, op2.readValue());
		writeFlags(flags);
	}

	public void bit(JZ80Instruction i) {
		int flags = bit(i.getSourceOperand(system), i.getIntegerOperand());
		writeFlags(flags);
	}

	public void call(JZ80Instruction i) {
		Operand op = i.getSourceOperand(system);
		call(op.readValue());
	}

	public void ccf(JZ80Instruction i) {
		int flags = ccf();
		writeFlags(flags);
	}

	public void cp(JZ80Instruction i) {
		Operand op1 = getRegister(JZ80RegisterName.A);
		Operand op2 = i.getSourceOperand(system);
		int flags = cp(op1, op2.readValue());
		writeFlags(flags);
	}

	public void cpl(JZ80Instruction i) {
		Operand op1 = i.getDestinationOperand(system);
		Operand op2 = i.getSourceOperand(system);
		int flags = cpl(op1, op2.readValue());
		writeFlags(flags);
	}

	public void daa(JZ80Instruction i) {
		Register op = getRegister(JZ80RegisterName.A);
		int flags = daa(op, op.readValue());
		writeFlags(flags);
	}

	public void dec(JZ80Instruction i) {
		Operand op = i.getDestinationOperand(system);
		int flags = dec(op);
		writeFlags(flags);
	}

	public void di(JZ80Instruction i) {
		setInterruptEnabled(false);
	}

	public void djnz(JZ80Instruction i) {
		Operand op = i.getSourceOperand(system);
		djnz((byte) op.readValue());
	}

	public void ei(JZ80Instruction i) {
		setInterruptEnabled(true);
	}

	public void ex(JZ80Instruction i) {
		Operand op1 = i.getDestinationOperand(system);
		Operand op2 = i.getSourceOperand(system);
		if (op1.getName().equals(op2.getName().substring(0, 2))) {
			// Sirve para "ex AF, AF'" aunque es más genérico
			exchangeRegisterPair(getRegisterName(op1.getName()));
		} else {
			ex(op1, op2);
		}
	}

	public void exx(JZ80Instruction i) {
		exchangeRegisterPair(JZ80RegisterName.BC);
		exchangeRegisterPair(JZ80RegisterName.DE);
		exchangeRegisterPair(JZ80RegisterName.HL);
	}

	public void halt(JZ80Instruction i) {
		halt();
	}

	public void in(JZ80Instruction i) {
		JZ80PortController op = (JZ80PortController) i.getSourceOperand(system);
		in(op);
	}

	public void inc(JZ80Instruction i) {
		Operand op = i.getDestinationOperand(system);
		int flags = inc(op);
		writeFlags(flags);
	}

	public void jp(JZ80Instruction i) {
		Operand op = i.getSourceOperand(system);
		// Distinguir caso jp incondicional o "jp (HL)" del jp condicional
		if (i.getOperandCount() == 1) {
			// Esto es un hack porque la notación de jp (HL) es inconsistente
			// en el set de instrucciones: no salta al valor apuntado por HL
			// (direccionamiento indirecto) sino al valor que tiene (directo).
			if (op.getName().charAt(0) == '(') {
				String s = op.getName().substring(1, 3);
				op = getRegister(JZ80Processor.getRegisterName(s));
			}
			jp(op.readValue());
		} else {
			int condition = i.getConditionCode();
			JZ80Flag[] flags = { JZ80Flag.Z, JZ80Flag.C, JZ80Flag.V, JZ80Flag.S };
			JZ80Flag f = flags[(condition & 7) >> 1];
			jp(op.readValue(), f, (condition & 1) == 1);
		}
	}

	public void jr(JZ80Instruction i) {
		Operand op = i.getSourceOperand(system);
		if (i.getOperandCount() == 1) {
			jr((byte) op.readValue());
		} else {
			int condition = i.getConditionCode() & 0x03;
			JZ80Flag f = (condition < 2) ? JZ80Flag.Z : JZ80Flag.C;
			jr((byte) op.readValue(), f, (condition & 1) == 1);
		}
	}

	public void ld(JZ80Instruction i) {
		Operand op1 = i.getDestinationOperand(system);
		Operand op2 = i.getSourceOperand(system);
		// caso especial "ld HL, (????)", "ld (????), HL"
		// no muy elegantemente solucionado
		if (op1.getByteSize() != op2.getByteSize()) {
			if (op1 instanceof IndirectOperand) {
				((IndirectOperand) op1).writeIndirectWord(op2.readValue());
				return;
			}
			if (op2 instanceof IndirectOperand) {
				op1.writeValue(((IndirectOperand) op2).readIndirectWord());
				return;
			}
		}
		ld(op1, op2);
	}

	public void mlt(JZ80Instruction i) {
		Operand op = i.getSourceOperand(system);
		mlt(op);
	}

	public void neg(JZ80Instruction i) {
		Operand op1 = i.getDestinationOperand(system);
		Operand op2 = i.getSourceOperand(system);
		int flags = neg(op1, op2.readValue());
		writeFlags(flags);
	}

	public void nop(JZ80Instruction i) {
		nop();
	}

	public void or(JZ80Instruction i) {
		Operand op1 = i.getDestinationOperand(system);
		Operand op2 = i.getSourceOperand(system);
		int flags = or(op1, op2.readValue());
		writeFlags(flags);
	}

	public void out(JZ80Instruction i) {
		JZ80PortController op = (JZ80PortController) i
				.getDestinationOperand(system);
		out(op);
	}

	public void pop(JZ80Instruction i) {
		Operand op = i.getSourceOperand(system);
		pop(op);
	}

	public void push(JZ80Instruction i) {
		Operand op = i.getSourceOperand(system);
		push(op);
	}

	public void res(JZ80Instruction i) {
		int bit = i.getIntegerOperand();
		Operand op = i.getSourceOperand(system);
		res(op, bit);
	}

	public void ret(JZ80Instruction i) {
		ret();
	}

	public void rl(JZ80Instruction i) {
		Operand op = i.getSourceOperand(system);
		int flags = rl(op, op.readValue());
		writeFlags(flags);
	}

	public void rla(JZ80Instruction i) {
		int flags = rla();
		writeFlags(flags);
	}

	public void rlc(JZ80Instruction i) {
		Operand op = i.getSourceOperand(system);
		rlc(op, op.readValue());
	}

	public void rlca(JZ80Instruction i) {
		int flags = rlca();
		writeFlags(flags);
	}

	public void rld(JZ80Instruction i) {
		Operand op1 = getRegister(JZ80RegisterName.A);
		Operand op2 = getIndirectRegister(JZ80RegisterName.HL);
		int flags = rld(op1, op2);
		writeFlags(flags);
	}

	public void rr(JZ80Instruction i) {
		Operand op = i.getSourceOperand(system);
		int flags = rr(op, op.readValue());
		writeFlags(flags);
	}

	public void rra(JZ80Instruction i) {
		int flags = rra();
		writeFlags(flags);
	}

	public void rrc(JZ80Instruction i) {
		Operand op = i.getSourceOperand(system);
		int flags = rrc(op, op.readValue());
		writeFlags(flags);
	}

	public void rrca(JZ80Instruction i) {
		int flags = rrca();
		writeFlags(flags);
	}

	public void rrd(JZ80Instruction i) {
		Operand op1 = getRegister(JZ80RegisterName.A);
		Operand op2 = getIndirectRegister(JZ80RegisterName.HL);
		int flags = rrd(op1, op2);
		writeFlags(flags);
	}

	public void rst(JZ80Instruction i) {
		// Para obtener las direcciones del rst
		// (00h, 08h, 10h, 18h, 20h, 28h, 30h o 38h):
		int address = i.getIntegerOperand();
		// TODO: Aunque el set de instrucciones especifica lo de abajo
		// el comportamiento esperado por nuestros usuarios es un nop
		// para "rst 38h"
		if (address != 0x38) {
			rst(address);
		}
	}

	public void sbc(JZ80Instruction i) {
		Operand op1 = i.getDestinationOperand(system);
		Operand op2 = i.getSourceOperand(system);
		int flags = sbc(op1, op2.readValue());
		writeFlags(flags);
	}

	public void scf(JZ80Instruction i) {
		int flags = scf();
		writeFlags(flags);
	}

	public void set(JZ80Instruction i) {
		int bit = i.getIntegerOperand();
		Operand op = i.getSourceOperand(system);
		set(op, bit);
	}

	public void sla(JZ80Instruction i) {
		Operand op = i.getSourceOperand(system);
		int flags = sla(op, op.readValue());
		writeFlags(flags);
	}

	public void sra(JZ80Instruction i) {
		Operand op = i.getSourceOperand(system);
		int flags = sra(op, op.readValue());
		writeFlags(flags);
	}

	public void srl(JZ80Instruction i) {
		Operand op = i.getSourceOperand(system);
		int flags = srl(op, op.readValue());
		writeFlags(flags);
	}

	public void sub(JZ80Instruction i) {
		Operand op1 = getRegister(JZ80RegisterName.A);
		Operand op2 = i.getSourceOperand(system);
		int flags = sub(op1, op2.readValue());
		writeFlags(flags);
	}

	public void xor(JZ80Instruction i) {
		Operand op1 = i.getDestinationOperand(system);
		Operand op2 = i.getSourceOperand(system);
		int flags = xor(op1, op2.readValue());
		writeFlags(flags);
	}

}
