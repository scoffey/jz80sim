package ar.edu.itba.it.obc.jz80.tests;

import ar.edu.itba.it.obc.jz80.api.InstructionException;
import ar.edu.itba.it.obc.jz80.instructions.JZ80Instruction;
import ar.edu.itba.it.obc.jz80.instructions.JZ80InstructionSet;
import ar.edu.itba.it.obc.jz80.instructions.JZ80InvalidInstructionException;
import ar.edu.itba.it.obc.jz80.system.JZ80System;

public class JZ80InstructionSetTest {

	private JZ80InstructionSet set;

	public JZ80InstructionSetTest() {
		set = new JZ80InstructionSet();
	}

	public boolean testMissing() {
		JZ80Instruction instr;
		JZ80System system = new JZ80System();
		boolean retval = true;
		String s = "FAILED: Cannot decode ";
		String t = "FAILED: Cannot execute ";
		for (int i = 0; i < 0x0100; i++) {
			try {
				instr = set.decode(i << 24);
				try {
					instr.execute(system);
				} catch (InstructionException e) {
					System.err.println(t + instr);
					retval = false;
				}
			} catch (JZ80InvalidInstructionException e) {
				System.err.println(s + String.format("%02X", i));
				retval = false;
			}
		}
		for (int i = 0xCB00; i < 0xCC00; i++) {
			try {
				instr = set.decode(i << 16);
				try {
					instr.execute(system);
				} catch (InstructionException e) {
					System.err.println(t + instr);
					retval = false;
				}
			} catch (JZ80InvalidInstructionException e) {
				System.err.println(s + String.format("%02X", i));
				retval = false;
			}
		}
		for (int i = 0xED00; i < 0xEF00; i++) {
			try {
				instr = set.decode(i << 16);
				try {
					instr.execute(system);
				} catch (InstructionException e) {
					System.err.println(t + instr);
					retval = false;
				}
			} catch (JZ80InvalidInstructionException e) {
				System.err.println(s + String.format("%02X", i));
				retval = false;
			}
		}
		return retval;
	}

	public boolean testInstruction(int code, int size, String s) {
		JZ80Instruction instr;
		JZ80System system = new JZ80System();
		try {
			instr = set.decode(code);
		} catch (JZ80InvalidInstructionException e1) {
			System.err.println(String.format("FAILED: null == %s", s));
			return false;
		}
		int actualSize = instr.getByteSize();
		if (actualSize != size) {
			System.err.println(String.format("FAILED: size(%08X) is %d "
					+ "but it should be %d", code, actualSize, size));
			return false;
		}
		String t = instr.toString();
		if (t.compareToIgnoreCase(s) != 0) {
			System.err.println(String.format("FAILED: %s == %s", t, s));
			return false;
		}
		try {
			instr.execute(system);
		} catch (InstructionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public void test() {
		testInstruction(0x0A000000, 1, "ld A, (BC)");
		testInstruction(0x1A000000, 1, "ld A, (DE)");
		testInstruction(0x02000000, 1, "ld (BC), A");
		testInstruction(0x12000000, 1, "ld (DE), A");
		testInstruction(0x2A012345, 3, "ld HL, (2301)");
		testInstruction(0x2AFEDCBA, 3, "ld HL, (DCFE)");
		testInstruction(0x3A012345, 3, "ld A, (2301)");
		testInstruction(0x3AFEDCBA, 3, "ld A, (DCFE)");
		testInstruction(0x22012345, 3, "ld (2301), HL");
		testInstruction(0x22FEDCBA, 3, "ld (DCFE), HL");
		testInstruction(0x32012345, 3, "ld (2301), A");
		testInstruction(0x32FEDCBA, 3, "ld (DCFE), A");
		testInstruction(0x40000000, 1, "ld B, B");
		testInstruction(0x41000000, 1, "ld B, C");
		testInstruction(0x42000000, 1, "ld B, D");
		testInstruction(0x43000000, 1, "ld B, E");
		testInstruction(0x44000000, 1, "ld B, H");
		testInstruction(0x45000000, 1, "ld B, L");
		testInstruction(0x46000000, 1, "ld B, (HL)");
		testInstruction(0x47000000, 1, "ld B, A");
		testInstruction(0x48000000, 1, "ld C, B");
		testInstruction(0x49000000, 1, "ld C, C");
		testInstruction(0x4A000000, 1, "ld C, D");
		testInstruction(0x4B000000, 1, "ld C, E");
		testInstruction(0x4C000000, 1, "ld C, H");
		testInstruction(0x4D000000, 1, "ld C, L");
		testInstruction(0x4E000000, 1, "ld C, (HL)");
		testInstruction(0x4F000000, 1, "ld C, A");
		testInstruction(0x50000000, 1, "ld D, B");
		testInstruction(0x51000000, 1, "ld D, C");
		testInstruction(0x52000000, 1, "ld D, D");
		testInstruction(0x53000000, 1, "ld D, E");
		testInstruction(0x54000000, 1, "ld D, H");
		testInstruction(0x55000000, 1, "ld D, L");
		testInstruction(0x56000000, 1, "ld D, (HL)");
		testInstruction(0x57000000, 1, "ld D, A");
		testInstruction(0x58000000, 1, "ld E, B");
		testInstruction(0x59000000, 1, "ld E, C");
		testInstruction(0x5A000000, 1, "ld E, D");
		testInstruction(0x5B000000, 1, "ld E, E");
		testInstruction(0x5C000000, 1, "ld E, H");
		testInstruction(0x5D000000, 1, "ld E, L");
		testInstruction(0x5E000000, 1, "ld E, (HL)");
		testInstruction(0x5F000000, 1, "ld E, A");
		testInstruction(0x60000000, 1, "ld H, B");
		testInstruction(0x61000000, 1, "ld H, C");
		testInstruction(0x62000000, 1, "ld H, D");
		testInstruction(0x63000000, 1, "ld H, E");
		testInstruction(0x64000000, 1, "ld H, H");
		testInstruction(0x65000000, 1, "ld H, L");
		testInstruction(0x66000000, 1, "ld H, (HL)");
		testInstruction(0x67000000, 1, "ld H, A");
		testInstruction(0x68000000, 1, "ld L, B");
		testInstruction(0x69000000, 1, "ld L, C");
		testInstruction(0x6A000000, 1, "ld L, D");
		testInstruction(0x6B000000, 1, "ld L, E");
		testInstruction(0x6C000000, 1, "ld L, H");
		testInstruction(0x6D000000, 1, "ld L, L");
		testInstruction(0x6E000000, 1, "ld L, (HL)");
		testInstruction(0x6F000000, 1, "ld L, A");
		testInstruction(0x70000000, 1, "ld (HL), B");
		testInstruction(0x71000000, 1, "ld (HL), C");
		testInstruction(0x72000000, 1, "ld (HL), D");
		testInstruction(0x73000000, 1, "ld (HL), E");
		testInstruction(0x74000000, 1, "ld (HL), H");
		testInstruction(0x75000000, 1, "ld (HL), L");
		testInstruction(0x76000000, 1, "halt");
		testInstruction(0x77000000, 1, "ld (HL), A");
		testInstruction(0x78000000, 1, "ld A, B");
		testInstruction(0x79000000, 1, "ld A, C");
		testInstruction(0x7A000000, 1, "ld A, D");
		testInstruction(0x7B000000, 1, "ld A, E");
		testInstruction(0x7C000000, 1, "ld A, H");
		testInstruction(0x7D000000, 1, "ld A, L");
		testInstruction(0x7E000000, 1, "ld A, (HL)");
		testInstruction(0x7F000000, 1, "ld A, A");
		testInstruction(0x06012345, 2, "ld B, 01");
		testInstruction(0x06FEDCBA, 2, "ld B, FE");
		testInstruction(0x0E012345, 2, "ld C, 01");
		testInstruction(0x0EFEDCBA, 2, "ld C, FE");
		testInstruction(0x16012345, 2, "ld D, 01");
		testInstruction(0x16FEDCBA, 2, "ld D, FE");
		testInstruction(0x1E012345, 2, "ld E, 01");
		testInstruction(0x1EFEDCBA, 2, "ld E, FE");
		testInstruction(0x26012345, 2, "ld H, 01");
		testInstruction(0x26FEDCBA, 2, "ld H, FE");
		testInstruction(0x2E012345, 2, "ld L, 01");
		testInstruction(0x2EFEDCBA, 2, "ld L, FE");
		testInstruction(0x36012345, 2, "ld (HL), 01");
		testInstruction(0x36FEDCBA, 2, "ld (HL), FE");
		testInstruction(0x3E012345, 2, "ld A, 01");
		testInstruction(0x3EFEDCBA, 2, "ld A, FE");
		testInstruction(0xF9000000, 1, "ld SP, HL");
		testInstruction(0xED4B0123, 4, "ld BC, (2301)");
		testInstruction(0xED4BFEDC, 4, "ld BC, (DCFE)");
		testInstruction(0xED5B0123, 4, "ld DE, (2301)");
		testInstruction(0xED5BFEDC, 4, "ld DE, (DCFE)");
		testInstruction(0xED6B0123, 4, "ld HL, (2301)");
		testInstruction(0xED6BFEDC, 4, "ld HL, (DCFE)");
		testInstruction(0xED7B0123, 4, "ld SP, (2301)");
		testInstruction(0xED7BFEDC, 4, "ld SP, (DCFE)");
		testInstruction(0xED430123, 4, "ld (2301), BC");
		testInstruction(0xED43FEDC, 4, "ld (DCFE), BC");
		testInstruction(0xED530123, 4, "ld (2301), DE");
		testInstruction(0xED53FEDC, 4, "ld (DCFE), DE");
		testInstruction(0xED630123, 4, "ld (2301), HL");
		testInstruction(0xED63FEDC, 4, "ld (DCFE), HL");
		testInstruction(0xED730123, 4, "ld (2301), SP");
		testInstruction(0xED73FEDC, 4, "ld (DCFE), SP");
		testInstruction(0x01012345, 3, "ld BC, 2301");
		testInstruction(0x01FEDCBA, 3, "ld BC, DCFE");
		testInstruction(0x11012345, 3, "ld DE, 2301");
		testInstruction(0x11FEDCBA, 3, "ld DE, DCFE");
		testInstruction(0x21012345, 3, "ld HL, 2301");
		testInstruction(0x21FEDCBA, 3, "ld HL, DCFE");
		testInstruction(0x31012345, 3, "ld SP, 2301");
		testInstruction(0x31FEDCBA, 3, "ld SP, DCFE");
		testInstruction(0xED570000, 2, "ld A, I");
		testInstruction(0xED5F0000, 2, "ld A, R");
		testInstruction(0xED470000, 2, "ld I, A");
		testInstruction(0xED4F0000, 2, "ld R, A");
		testInstruction(0x08000000, 1, "ex AF, AF'");
		testInstruction(0xE3000000, 1, "ex (SP), HL");
		testInstruction(0xEB000000, 1, "ex DE, HL");
		testInstruction(0xD9000000, 1, "exx");
		testInstruction(0xF3000000, 1, "di");
		testInstruction(0xFB000000, 1, "ei");
		testInstruction(0x2F000000, 1, "cpl");
		testInstruction(0xA0000000, 1, "and B");
		testInstruction(0xA1000000, 1, "and C");
		testInstruction(0xA2000000, 1, "and D");
		testInstruction(0xA3000000, 1, "and E");
		testInstruction(0xA4000000, 1, "and H");
		testInstruction(0xA5000000, 1, "and L");
		testInstruction(0xA6000000, 1, "and (HL)");
		testInstruction(0xA7000000, 1, "and A");
		testInstruction(0xE6012345, 2, "and 01");
		testInstruction(0xE6FEDCBA, 2, "and FE");
		testInstruction(0xB0000000, 1, "or B");
		testInstruction(0xB1000000, 1, "or C");
		testInstruction(0xB2000000, 1, "or D");
		testInstruction(0xB3000000, 1, "or E");
		testInstruction(0xB4000000, 1, "or H");
		testInstruction(0xB5000000, 1, "or L");
		testInstruction(0xB6000000, 1, "or (HL)");
		testInstruction(0xB7000000, 1, "or A");
		testInstruction(0xF6012345, 2, "or 01");
		testInstruction(0xF6FEDCBA, 2, "or FE");
		testInstruction(0xA8000000, 1, "xor B");
		testInstruction(0xA9000000, 1, "xor C");
		testInstruction(0xAA000000, 1, "xor D");
		testInstruction(0xAB000000, 1, "xor E");
		testInstruction(0xAC000000, 1, "xor H");
		testInstruction(0xAD000000, 1, "xor L");
		testInstruction(0xAE000000, 1, "xor (HL)");
		testInstruction(0xAF000000, 1, "xor A");
		testInstruction(0xEE012345, 2, "xor 01");
		testInstruction(0xEEFEDCBA, 2, "xor FE");
		testInstruction(0xED440000, 2, "neg");
		testInstruction(0x27000000, 1, "daa");
		testInstruction(0x05000000, 1, "dec B");
		testInstruction(0x0D000000, 1, "dec C");
		testInstruction(0x15000000, 1, "dec D");
		testInstruction(0x1D000000, 1, "dec E");
		testInstruction(0x25000000, 1, "dec H");
		testInstruction(0x2D000000, 1, "dec L");
		testInstruction(0x35000000, 1, "dec (HL)");
		testInstruction(0x3D000000, 1, "dec A");
		testInstruction(0x0B000000, 1, "dec BC");
		testInstruction(0x1B000000, 1, "dec DE");
		testInstruction(0x2B000000, 1, "dec HL");
		testInstruction(0x3B000000, 1, "dec SP");
		testInstruction(0x04000000, 1, "inc B");
		testInstruction(0x0C000000, 1, "inc C");
		testInstruction(0x14000000, 1, "inc D");
		testInstruction(0x1C000000, 1, "inc E");
		testInstruction(0x24000000, 1, "inc H");
		testInstruction(0x2C000000, 1, "inc L");
		testInstruction(0x34000000, 1, "inc (HL)");
		testInstruction(0x3C000000, 1, "inc A");
		testInstruction(0x03000000, 1, "inc BC");
		testInstruction(0x13000000, 1, "inc DE");
		testInstruction(0x23000000, 1, "inc HL");
		testInstruction(0x33000000, 1, "inc SP");
		testInstruction(0xED4C0000, 2, "mlt BC");
		testInstruction(0xED5C0000, 2, "mlt DE");
		testInstruction(0xED6C0000, 2, "mlt HL");
		testInstruction(0xED7C0000, 2, "mlt SP");
		testInstruction(0x98000000, 1, "sbc A, B");
		testInstruction(0x99000000, 1, "sbc A, C");
		testInstruction(0x9A000000, 1, "sbc A, D");
		testInstruction(0x9B000000, 1, "sbc A, E");
		testInstruction(0x9C000000, 1, "sbc A, H");
		testInstruction(0x9D000000, 1, "sbc A, L");
		testInstruction(0x9E000000, 1, "sbc A, (HL)");
		testInstruction(0x9F000000, 1, "sbc A, A");
		testInstruction(0xDE012345, 2, "sbc A, 01");
		testInstruction(0xDEFEDCBA, 2, "sbc A, FE");
		testInstruction(0xED420000, 2, "sbc HL, BC");
		testInstruction(0xED520000, 2, "sbc HL, DE");
		testInstruction(0xED620000, 2, "sbc HL, HL");
		testInstruction(0xED720000, 2, "sbc HL, SP");
		testInstruction(0x90000000, 1, "sub B");
		testInstruction(0x91000000, 1, "sub C");
		testInstruction(0x92000000, 1, "sub D");
		testInstruction(0x93000000, 1, "sub E");
		testInstruction(0x94000000, 1, "sub H");
		testInstruction(0x95000000, 1, "sub L");
		testInstruction(0x96000000, 1, "sub (HL)");
		testInstruction(0x97000000, 1, "sub A");
		testInstruction(0xD6012345, 2, "sub 01");
		testInstruction(0xD6FEDCBA, 2, "sub FE");
		testInstruction(0x88000000, 1, "adc A, B");
		testInstruction(0x89000000, 1, "adc A, C");
		testInstruction(0x8A000000, 1, "adc A, D");
		testInstruction(0x8B000000, 1, "adc A, E");
		testInstruction(0x8C000000, 1, "adc A, H");
		testInstruction(0x8D000000, 1, "adc A, L");
		testInstruction(0x8E000000, 1, "adc A, (HL)");
		testInstruction(0x8F000000, 1, "adc A, A");
		testInstruction(0xCE012345, 2, "adc A, 01");
		testInstruction(0xCEFEDCBA, 2, "adc A, FE");
		testInstruction(0xED4A0000, 2, "adc HL, BC");
		testInstruction(0xED5A0000, 2, "adc HL, DE");
		testInstruction(0xED6A0000, 2, "adc HL, HL");
		testInstruction(0xED7A0000, 2, "adc HL, SP");
		testInstruction(0x80000000, 1, "add A, B");
		testInstruction(0x81000000, 1, "add A, C");
		testInstruction(0x82000000, 1, "add A, D");
		testInstruction(0x83000000, 1, "add A, E");
		testInstruction(0x84000000, 1, "add A, H");
		testInstruction(0x85000000, 1, "add A, L");
		testInstruction(0x86000000, 1, "add A, (HL)");
		testInstruction(0x87000000, 1, "add A, A");
		testInstruction(0xC6012345, 2, "add A, 01");
		testInstruction(0xC6FEDCBA, 2, "add A, FE");
		testInstruction(0x09000000, 1, "add HL, BC");
		testInstruction(0x19000000, 1, "add HL, DE");
		testInstruction(0x29000000, 1, "add HL, HL");
		testInstruction(0x39000000, 1, "add HL, SP");
		testInstruction(0xCB800000, 2, "res 0, B");
		testInstruction(0xCB810000, 2, "res 0, C");
		testInstruction(0xCB820000, 2, "res 0, D");
		testInstruction(0xCB830000, 2, "res 0, E");
		testInstruction(0xCB840000, 2, "res 0, H");
		testInstruction(0xCB850000, 2, "res 0, L");
		testInstruction(0xCB860000, 2, "res 0, (HL)");
		testInstruction(0xCB870000, 2, "res 0, A");
		testInstruction(0xCB880000, 2, "res 1, B");
		testInstruction(0xCB890000, 2, "res 1, C");
		testInstruction(0xCB8A0000, 2, "res 1, D");
		testInstruction(0xCB8B0000, 2, "res 1, E");
		testInstruction(0xCB8C0000, 2, "res 1, H");
		testInstruction(0xCB8D0000, 2, "res 1, L");
		testInstruction(0xCB8E0000, 2, "res 1, (HL)");
		testInstruction(0xCB8F0000, 2, "res 1, A");
		testInstruction(0xCB900000, 2, "res 2, B");
		testInstruction(0xCB910000, 2, "res 2, C");
		testInstruction(0xCB920000, 2, "res 2, D");
		testInstruction(0xCB930000, 2, "res 2, E");
		testInstruction(0xCB940000, 2, "res 2, H");
		testInstruction(0xCB950000, 2, "res 2, L");
		testInstruction(0xCB960000, 2, "res 2, (HL)");
		testInstruction(0xCB970000, 2, "res 2, A");
		testInstruction(0xCB980000, 2, "res 3, B");
		testInstruction(0xCB990000, 2, "res 3, C");
		testInstruction(0xCB9A0000, 2, "res 3, D");
		testInstruction(0xCB9B0000, 2, "res 3, E");
		testInstruction(0xCB9C0000, 2, "res 3, H");
		testInstruction(0xCB9D0000, 2, "res 3, L");
		testInstruction(0xCB9E0000, 2, "res 3, (HL)");
		testInstruction(0xCB9F0000, 2, "res 3, A");
		testInstruction(0xCBA00000, 2, "res 4, B");
		testInstruction(0xCBA10000, 2, "res 4, C");
		testInstruction(0xCBA20000, 2, "res 4, D");
		testInstruction(0xCBA30000, 2, "res 4, E");
		testInstruction(0xCBA40000, 2, "res 4, H");
		testInstruction(0xCBA50000, 2, "res 4, L");
		testInstruction(0xCBA60000, 2, "res 4, (HL)");
		testInstruction(0xCBA70000, 2, "res 4, A");
		testInstruction(0xCBA80000, 2, "res 5, B");
		testInstruction(0xCBA90000, 2, "res 5, C");
		testInstruction(0xCBAA0000, 2, "res 5, D");
		testInstruction(0xCBAB0000, 2, "res 5, E");
		testInstruction(0xCBAC0000, 2, "res 5, H");
		testInstruction(0xCBAD0000, 2, "res 5, L");
		testInstruction(0xCBAE0000, 2, "res 5, (HL)");
		testInstruction(0xCBAF0000, 2, "res 5, A");
		testInstruction(0xCBB00000, 2, "res 6, B");
		testInstruction(0xCBB10000, 2, "res 6, C");
		testInstruction(0xCBB20000, 2, "res 6, D");
		testInstruction(0xCBB30000, 2, "res 6, E");
		testInstruction(0xCBB40000, 2, "res 6, H");
		testInstruction(0xCBB50000, 2, "res 6, L");
		testInstruction(0xCBB60000, 2, "res 6, (HL)");
		testInstruction(0xCBB70000, 2, "res 6, A");
		testInstruction(0xCBB80000, 2, "res 7, B");
		testInstruction(0xCBB90000, 2, "res 7, C");
		testInstruction(0xCBBA0000, 2, "res 7, D");
		testInstruction(0xCBBB0000, 2, "res 7, E");
		testInstruction(0xCBBC0000, 2, "res 7, H");
		testInstruction(0xCBBD0000, 2, "res 7, L");
		testInstruction(0xCBBE0000, 2, "res 7, (HL)");
		testInstruction(0xCBBF0000, 2, "res 7, A");
		testInstruction(0xCBC00000, 2, "set 0, B");
		testInstruction(0xCBC10000, 2, "set 0, C");
		testInstruction(0xCBC20000, 2, "set 0, D");
		testInstruction(0xCBC30000, 2, "set 0, E");
		testInstruction(0xCBC40000, 2, "set 0, H");
		testInstruction(0xCBC50000, 2, "set 0, L");
		testInstruction(0xCBC60000, 2, "set 0, (HL)");
		testInstruction(0xCBC70000, 2, "set 0, A");
		testInstruction(0xCBC80000, 2, "set 1, B");
		testInstruction(0xCBC90000, 2, "set 1, C");
		testInstruction(0xCBCA0000, 2, "set 1, D");
		testInstruction(0xCBCB0000, 2, "set 1, E");
		testInstruction(0xCBCC0000, 2, "set 1, H");
		testInstruction(0xCBCD0000, 2, "set 1, L");
		testInstruction(0xCBCE0000, 2, "set 1, (HL)");
		testInstruction(0xCBCF0000, 2, "set 1, A");
		testInstruction(0xCBD00000, 2, "set 2, B");
		testInstruction(0xCBD10000, 2, "set 2, C");
		testInstruction(0xCBD20000, 2, "set 2, D");
		testInstruction(0xCBD30000, 2, "set 2, E");
		testInstruction(0xCBD40000, 2, "set 2, H");
		testInstruction(0xCBD50000, 2, "set 2, L");
		testInstruction(0xCBD60000, 2, "set 2, (HL)");
		testInstruction(0xCBD70000, 2, "set 2, A");
		testInstruction(0xCBD80000, 2, "set 3, B");
		testInstruction(0xCBD90000, 2, "set 3, C");
		testInstruction(0xCBDA0000, 2, "set 3, D");
		testInstruction(0xCBDB0000, 2, "set 3, E");
		testInstruction(0xCBDC0000, 2, "set 3, H");
		testInstruction(0xCBDD0000, 2, "set 3, L");
		testInstruction(0xCBDE0000, 2, "set 3, (HL)");
		testInstruction(0xCBDF0000, 2, "set 3, A");
		testInstruction(0xCBE00000, 2, "set 4, B");
		testInstruction(0xCBE10000, 2, "set 4, C");
		testInstruction(0xCBE20000, 2, "set 4, D");
		testInstruction(0xCBE30000, 2, "set 4, E");
		testInstruction(0xCBE40000, 2, "set 4, H");
		testInstruction(0xCBE50000, 2, "set 4, L");
		testInstruction(0xCBE60000, 2, "set 4, (HL)");
		testInstruction(0xCBE70000, 2, "set 4, A");
		testInstruction(0xCBE80000, 2, "set 5, B");
		testInstruction(0xCBE90000, 2, "set 5, C");
		testInstruction(0xCBEA0000, 2, "set 5, D");
		testInstruction(0xCBEB0000, 2, "set 5, E");
		testInstruction(0xCBEC0000, 2, "set 5, H");
		testInstruction(0xCBED0000, 2, "set 5, L");
		testInstruction(0xCBEE0000, 2, "set 5, (HL)");
		testInstruction(0xCBEF0000, 2, "set 5, A");
		testInstruction(0xCBF00000, 2, "set 6, B");
		testInstruction(0xCBF10000, 2, "set 6, C");
		testInstruction(0xCBF20000, 2, "set 6, D");
		testInstruction(0xCBF30000, 2, "set 6, E");
		testInstruction(0xCBF40000, 2, "set 6, H");
		testInstruction(0xCBF50000, 2, "set 6, L");
		testInstruction(0xCBF60000, 2, "set 6, (HL)");
		testInstruction(0xCBF70000, 2, "set 6, A");
		testInstruction(0xCBF80000, 2, "set 7, B");
		testInstruction(0xCBF90000, 2, "set 7, C");
		testInstruction(0xCBFA0000, 2, "set 7, D");
		testInstruction(0xCBFB0000, 2, "set 7, E");
		testInstruction(0xCBFC0000, 2, "set 7, H");
		testInstruction(0xCBFD0000, 2, "set 7, L");
		testInstruction(0xCBFE0000, 2, "set 7, (HL)");
		testInstruction(0xCBFF0000, 2, "set 7, A");
		testInstruction(0xCB400000, 2, "bit 0, B");
		testInstruction(0xCB410000, 2, "bit 0, C");
		testInstruction(0xCB420000, 2, "bit 0, D");
		testInstruction(0xCB430000, 2, "bit 0, E");
		testInstruction(0xCB440000, 2, "bit 0, H");
		testInstruction(0xCB450000, 2, "bit 0, L");
		testInstruction(0xCB460000, 2, "bit 0, (HL)");
		testInstruction(0xCB470000, 2, "bit 0, A");
		testInstruction(0xCB480000, 2, "bit 1, B");
		testInstruction(0xCB490000, 2, "bit 1, C");
		testInstruction(0xCB4A0000, 2, "bit 1, D");
		testInstruction(0xCB4B0000, 2, "bit 1, E");
		testInstruction(0xCB4C0000, 2, "bit 1, H");
		testInstruction(0xCB4D0000, 2, "bit 1, L");
		testInstruction(0xCB4E0000, 2, "bit 1, (HL)");
		testInstruction(0xCB4F0000, 2, "bit 1, A");
		testInstruction(0xCB500000, 2, "bit 2, B");
		testInstruction(0xCB510000, 2, "bit 2, C");
		testInstruction(0xCB520000, 2, "bit 2, D");
		testInstruction(0xCB530000, 2, "bit 2, E");
		testInstruction(0xCB540000, 2, "bit 2, H");
		testInstruction(0xCB550000, 2, "bit 2, L");
		testInstruction(0xCB560000, 2, "bit 2, (HL)");
		testInstruction(0xCB570000, 2, "bit 2, A");
		testInstruction(0xCB580000, 2, "bit 3, B");
		testInstruction(0xCB590000, 2, "bit 3, C");
		testInstruction(0xCB5A0000, 2, "bit 3, D");
		testInstruction(0xCB5B0000, 2, "bit 3, E");
		testInstruction(0xCB5C0000, 2, "bit 3, H");
		testInstruction(0xCB5D0000, 2, "bit 3, L");
		testInstruction(0xCB5E0000, 2, "bit 3, (HL)");
		testInstruction(0xCB5F0000, 2, "bit 3, A");
		testInstruction(0xCB600000, 2, "bit 4, B");
		testInstruction(0xCB610000, 2, "bit 4, C");
		testInstruction(0xCB620000, 2, "bit 4, D");
		testInstruction(0xCB630000, 2, "bit 4, E");
		testInstruction(0xCB640000, 2, "bit 4, H");
		testInstruction(0xCB650000, 2, "bit 4, L");
		testInstruction(0xCB660000, 2, "bit 4, (HL)");
		testInstruction(0xCB670000, 2, "bit 4, A");
		testInstruction(0xCB680000, 2, "bit 5, B");
		testInstruction(0xCB690000, 2, "bit 5, C");
		testInstruction(0xCB6A0000, 2, "bit 5, D");
		testInstruction(0xCB6B0000, 2, "bit 5, E");
		testInstruction(0xCB6C0000, 2, "bit 5, H");
		testInstruction(0xCB6D0000, 2, "bit 5, L");
		testInstruction(0xCB6E0000, 2, "bit 5, (HL)");
		testInstruction(0xCB6F0000, 2, "bit 5, A");
		testInstruction(0xCB700000, 2, "bit 6, B");
		testInstruction(0xCB710000, 2, "bit 6, C");
		testInstruction(0xCB720000, 2, "bit 6, D");
		testInstruction(0xCB730000, 2, "bit 6, E");
		testInstruction(0xCB740000, 2, "bit 6, H");
		testInstruction(0xCB750000, 2, "bit 6, L");
		testInstruction(0xCB760000, 2, "bit 6, (HL)");
		testInstruction(0xCB770000, 2, "bit 6, A");
		testInstruction(0xCB780000, 2, "bit 7, B");
		testInstruction(0xCB790000, 2, "bit 7, C");
		testInstruction(0xCB7A0000, 2, "bit 7, D");
		testInstruction(0xCB7B0000, 2, "bit 7, E");
		testInstruction(0xCB7C0000, 2, "bit 7, H");
		testInstruction(0xCB7D0000, 2, "bit 7, L");
		testInstruction(0xCB7E0000, 2, "bit 7, (HL)");
		testInstruction(0xCB7F0000, 2, "bit 7, A");
		testInstruction(0x37000000, 1, "scf");
		testInstruction(0x3F000000, 1, "ccf");
		testInstruction(0xCB000000, 2, "rlc B");
		testInstruction(0xCB010000, 2, "rlc C");
		testInstruction(0xCB020000, 2, "rlc D");
		testInstruction(0xCB030000, 2, "rlc E");
		testInstruction(0xCB040000, 2, "rlc H");
		testInstruction(0xCB050000, 2, "rlc L");
		testInstruction(0xCB060000, 2, "rlc (HL)");
		testInstruction(0xCB070000, 2, "rlc A");
		testInstruction(0xCB080000, 2, "rrc B");
		testInstruction(0xCB090000, 2, "rrc C");
		testInstruction(0xCB0A0000, 2, "rrc D");
		testInstruction(0xCB0B0000, 2, "rrc E");
		testInstruction(0xCB0C0000, 2, "rrc H");
		testInstruction(0xCB0D0000, 2, "rrc L");
		testInstruction(0xCB0E0000, 2, "rrc (HL)");
		testInstruction(0xCB0F0000, 2, "rrc A");
		testInstruction(0xCB100000, 2, "rl B");
		testInstruction(0xCB110000, 2, "rl C");
		testInstruction(0xCB120000, 2, "rl D");
		testInstruction(0xCB130000, 2, "rl E");
		testInstruction(0xCB140000, 2, "rl H");
		testInstruction(0xCB150000, 2, "rl L");
		testInstruction(0xCB160000, 2, "rl (HL)");
		testInstruction(0xCB170000, 2, "rl A");
		testInstruction(0xCB180000, 2, "rr B");
		testInstruction(0xCB190000, 2, "rr C");
		testInstruction(0xCB1A0000, 2, "rr D");
		testInstruction(0xCB1B0000, 2, "rr E");
		testInstruction(0xCB1C0000, 2, "rr H");
		testInstruction(0xCB1D0000, 2, "rr L");
		testInstruction(0xCB1E0000, 2, "rr (HL)");
		testInstruction(0xCB1F0000, 2, "rr A");
		testInstruction(0xCB200000, 2, "sla B");
		testInstruction(0xCB210000, 2, "sla C");
		testInstruction(0xCB220000, 2, "sla D");
		testInstruction(0xCB230000, 2, "sla E");
		testInstruction(0xCB240000, 2, "sla H");
		testInstruction(0xCB250000, 2, "sla L");
		testInstruction(0xCB260000, 2, "sla (HL)");
		testInstruction(0xCB270000, 2, "sla A");
		testInstruction(0xCB280000, 2, "sra B");
		testInstruction(0xCB290000, 2, "sra C");
		testInstruction(0xCB2A0000, 2, "sra D");
		testInstruction(0xCB2B0000, 2, "sra E");
		testInstruction(0xCB2C0000, 2, "sra H");
		testInstruction(0xCB2D0000, 2, "sra L");
		testInstruction(0xCB2E0000, 2, "sra (HL)");
		testInstruction(0xCB2F0000, 2, "sra A");
		testInstruction(0xCB380000, 2, "srl B");
		testInstruction(0xCB390000, 2, "srl C");
		testInstruction(0xCB3A0000, 2, "srl D");
		testInstruction(0xCB3B0000, 2, "srl E");
		testInstruction(0xCB3C0000, 2, "srl H");
		testInstruction(0xCB3D0000, 2, "srl L");
		testInstruction(0xCB3E0000, 2, "srl (HL)");
		testInstruction(0xCB3F0000, 2, "srl A");
		testInstruction(0x07000000, 1, "rlca");
		testInstruction(0x0F000000, 1, "rrca");
		testInstruction(0x17000000, 1, "rla");
		testInstruction(0x1F000000, 1, "rra");
		testInstruction(0xED6F0000, 2, "rld");
		testInstruction(0xB8000000, 1, "cp B");
		testInstruction(0xB9000000, 1, "cp C");
		testInstruction(0xBA000000, 1, "cp D");
		testInstruction(0xBB000000, 1, "cp E");
		testInstruction(0xBC000000, 1, "cp H");
		testInstruction(0xBD000000, 1, "cp L");
		testInstruction(0xBE000000, 1, "cp (HL)");
		testInstruction(0xBF000000, 1, "cp A");
		testInstruction(0xFE012345, 2, "cp 01");
		testInstruction(0xFEFEDCBA, 2, "cp FE");
		testInstruction(0x10012345, 2, "djnz 01"); // TODO: mostrar con -2?
		testInstruction(0x10FEDCBA, 2, "djnz FE");
		testInstruction(0xC2012345, 3, "jp NZ, 2301");
		testInstruction(0xC2FEDCBA, 3, "jp NZ, DCFE");
		testInstruction(0xCA012345, 3, "jp Z, 2301");
		testInstruction(0xCAFEDCBA, 3, "jp Z, DCFE");
		testInstruction(0xD2012345, 3, "jp NC, 2301");
		testInstruction(0xD2FEDCBA, 3, "jp NC, DCFE");
		testInstruction(0xDA012345, 3, "jp C, 2301");
		testInstruction(0xDAFEDCBA, 3, "jp C, DCFE");
		testInstruction(0xE2012345, 3, "jp PO, 2301");
		testInstruction(0xE2FEDCBA, 3, "jp PO, DCFE");
		testInstruction(0xEA012345, 3, "jp PE, 2301");
		testInstruction(0xEAFEDCBA, 3, "jp PE, DCFE");
		testInstruction(0xF2012345, 3, "jp P, 2301");
		testInstruction(0xF2FEDCBA, 3, "jp P, DCFE");
		testInstruction(0xFA012345, 3, "jp M, 2301");
		testInstruction(0xFAFEDCBA, 3, "jp M, DCFE");
		testInstruction(0xC3012345, 3, "jp 2301");
		testInstruction(0xC3FEDCBA, 3, "jp DCFE");
		testInstruction(0xE9000000, 1, "jp (HL)");
		testInstruction(0x20012345, 2, "jr NZ, 01");
		testInstruction(0x20FEDCBA, 2, "jr NZ, FE");
		testInstruction(0x28012345, 2, "jr Z, 01");
		testInstruction(0x28FEDCBA, 2, "jr Z, FE");
		testInstruction(0x30012345, 2, "jr NC, 01");
		testInstruction(0x30FEDCBA, 2, "jr NC, FE");
		testInstruction(0x38012345, 2, "jr C, 01");
		testInstruction(0x38FEDCBA, 2, "jr C, FE");
		testInstruction(0x18012345, 2, "jr 01");
		testInstruction(0x18FEDCBA, 2, "jr FE");
		testInstruction(0xC5000000, 1, "push BC");
		testInstruction(0xD5000000, 1, "push DE");
		testInstruction(0xE5000000, 1, "push HL");
		testInstruction(0xF5000000, 1, "push AF");
		testInstruction(0xC1000000, 1, "pop BC");
		testInstruction(0xD1000000, 1, "pop DE");
		testInstruction(0xE1000000, 1, "pop HL");
		testInstruction(0xF1000000, 1, "pop AF");
		testInstruction(0xCD012345, 3, "call 2301");
		testInstruction(0xCDFEDCBA, 3, "call DCFE");
		testInstruction(0xC4012345, 3, "call NZ, 2301");
		testInstruction(0xC4FEDCBA, 3, "call NZ, DCFE");
		testInstruction(0xCC012345, 3, "call Z, 2301");
		testInstruction(0xCCFEDCBA, 3, "call Z, DCFE");
		testInstruction(0xD4012345, 3, "call NC, 2301");
		testInstruction(0xD4FEDCBA, 3, "call NC, DCFE");
		testInstruction(0xDC012345, 3, "call C, 2301");
		testInstruction(0xDCFEDCBA, 3, "call C, DCFE");
		testInstruction(0xE4012345, 3, "call PO, 2301");
		testInstruction(0xE4FEDCBA, 3, "call PO, DCFE");
		testInstruction(0xEC012345, 3, "call PE, 2301");
		testInstruction(0xECFEDCBA, 3, "call PE, DCFE");
		testInstruction(0xF4012345, 3, "call P, 2301");
		testInstruction(0xF4FEDCBA, 3, "call P, DCFE");
		testInstruction(0xFC012345, 3, "call M, 2301");
		testInstruction(0xFCFEDCBA, 3, "call M, DCFE");
		testInstruction(0xC9000000, 1, "ret");
		testInstruction(0xC0000000, 1, "ret NZ");
		testInstruction(0xC8000000, 1, "ret Z");
		testInstruction(0xD0000000, 1, "ret NC");
		testInstruction(0xD8000000, 1, "ret C");
		testInstruction(0xE0000000, 1, "ret PO");
		testInstruction(0xE8000000, 1, "ret PE");
		testInstruction(0xF0000000, 1, "ret P");
		testInstruction(0xF8000000, 1, "ret M");
		testInstruction(0xDB012345, 2, "in A, (01)");
		testInstruction(0xDBFEDCBA, 2, "in A, (FE)");
		testInstruction(0xD3012345, 2, "out (01), A");
		testInstruction(0xD3FEDCBA, 2, "out (FE), A");
		testInstruction(0x00000000, 1, "nop");
		testInstruction(0xC7000000, 1, "rst 00");
		testInstruction(0xCF000000, 1, "rst 08");
		testInstruction(0xD7000000, 1, "rst 10");
		testInstruction(0xDF000000, 1, "rst 18");
		testInstruction(0xE7000000, 1, "rst 20");
		testInstruction(0xEF000000, 1, "rst 28");
		testInstruction(0xF7000000, 1, "rst 30");
		testInstruction(0xFF000000, 1, "rst 38");
		// IX
		testInstruction(0xDD2A0123, 4, "ld IX, (2301)");
		testInstruction(0xDD2AFEDC, 4, "ld IX, (DCFE)");
		testInstruction(0xDD220123, 4, "ld (2301), IX");
		testInstruction(0xDD22FEDC, 4, "ld (DCFE), IX");
		testInstruction(0xDD467800, 3, "ld B, (IX+78)");
		testInstruction(0xDD4E7800, 3, "ld C, (IX+78)");
		testInstruction(0xDD567800, 3, "ld D, (IX+78)");
		testInstruction(0xDD5E7800, 3, "ld E, (IX+78)");
		testInstruction(0xDD667800, 3, "ld H, (IX+78)");
		testInstruction(0xDD6E7800, 3, "ld L, (IX+78)");
		testInstruction(0xDD707800, 3, "ld (IX+78), B");
		testInstruction(0xDD717800, 3, "ld (IX+78), C");
		testInstruction(0xDD727800, 3, "ld (IX+78), D");
		testInstruction(0xDD737800, 3, "ld (IX+78), E");
		testInstruction(0xDD747800, 3, "ld (IX+78), H");
		testInstruction(0xDD757800, 3, "ld (IX+78), L");
		testInstruction(0xDD777800, 3, "ld (IX+78), A");
		testInstruction(0xDD7E7800, 3, "ld A, (IX+78)");
		testInstruction(0xDD367801, 4, "ld (IX+78), 01");
		testInstruction(0xDD3678FE, 4, "ld (IX+78), FE");
		testInstruction(0xDDF90000, 2, "ld SP, IX");
		testInstruction(0xED6B0123, 4, "ld HL, (2301)");
		testInstruction(0xED6BFEDC, 4, "ld HL, (DCFE)");
		testInstruction(0xED630123, 4, "ld (2301), HL");
		testInstruction(0xED63FEDC, 4, "ld (DCFE), HL");
		testInstruction(0xDD210123, 4, "ld IX, 2301");
		testInstruction(0xDD21FEDC, 4, "ld IX, DCFE");
		testInstruction(0xDDA67800, 3, "and (IX+78)");
		testInstruction(0xDDB67800, 3, "or (IX+78)");
		testInstruction(0xDDAE7800, 3, "xor (IX+78)");
		testInstruction(0xDD357800, 3, "dec (IX+78)");
		testInstruction(0xDD2B0000, 2, "dec IX");
		testInstruction(0xDD347800, 3, "inc (IX+78)");
		testInstruction(0xDD230000, 2, "inc IX");
		testInstruction(0xDD9E7800, 3, "sbc A, (IX+78)");
		testInstruction(0xDD967800, 3, "sub (IX+78)");
		testInstruction(0xDD8E7800, 3, "adc A, (IX+78)");
		testInstruction(0xDD867800, 3, "add A, (IX+78)");
		testInstruction(0xDD090000, 2, "add IX, BC");
		testInstruction(0xDD190000, 2, "add IX, DE");
		testInstruction(0xDD290000, 2, "add IX, IX"); // TODO: está bien?
		testInstruction(0xDD390000, 2, "add IX, SP");
		testInstruction(0xDDCB7886, 4, "res 0, (IX+78)");
		testInstruction(0xDDCB788E, 4, "res 1, (IX+78)");
		testInstruction(0xDDCB7896, 4, "res 2, (IX+78)");
		testInstruction(0xDDCB789E, 4, "res 3, (IX+78)");
		testInstruction(0xDDCB78A6, 4, "res 4, (IX+78)");
		testInstruction(0xDDCB78AE, 4, "res 5, (IX+78)");
		testInstruction(0xDDCB78B6, 4, "res 6, (IX+78)");
		testInstruction(0xDDCB78BE, 4, "res 7, (IX+78)");
		testInstruction(0xDDCB78C6, 4, "set 0, (IX+78)");
		testInstruction(0xDDCB78CE, 4, "set 1, (IX+78)");
		testInstruction(0xDDCB78D6, 4, "set 2, (IX+78)");
		testInstruction(0xDDCB78DE, 4, "set 3, (IX+78)");
		testInstruction(0xDDCB78E6, 4, "set 4, (IX+78)");
		testInstruction(0xDDCB78EE, 4, "set 5, (IX+78)");
		testInstruction(0xDDCB78F6, 4, "set 6, (IX+78)");
		testInstruction(0xDDCB78FE, 4, "set 7, (IX+78)");
		testInstruction(0xDDCB7846, 4, "bit 0, (IX+78)");
		testInstruction(0xDDCB784E, 4, "bit 1, (IX+78)");
		testInstruction(0xDDCB7856, 4, "bit 2, (IX+78)");
		testInstruction(0xDDCB785E, 4, "bit 3, (IX+78)");
		testInstruction(0xDDCB7866, 4, "bit 4, (IX+78)");
		testInstruction(0xDDCB786E, 4, "bit 5, (IX+78)");
		testInstruction(0xDDCB7876, 4, "bit 6, (IX+78)");
		testInstruction(0xDDCB787E, 4, "bit 7, (IX+78)");
		testInstruction(0xDDCB7806, 4, "rlc (IX+78)");
		testInstruction(0xDDCB780E, 4, "rrc (IX+78)");
		testInstruction(0xDDCB7816, 4, "rl (IX+78)");
		testInstruction(0xDDCB781E, 4, "rr (IX+78)");
		testInstruction(0xDDCB7826, 4, "sla (IX+78)");
		testInstruction(0xDDCB782E, 4, "sra (IX+78)");
		testInstruction(0xDDCB783E, 4, "srl (IX+78)");
		testInstruction(0xDDBE7800, 3, "cp (IX+78)");
		testInstruction(0xDDE50000, 2, "push IX");
		testInstruction(0xDDE10000, 2, "pop IX");
		// en principio, las siguientes no existen:
		// testInstruction(0xDD767800, 3, "ld (IX+78), (HL)");
		// testInstruction(0xDDED6C00, 3, "mlt IX");
		// testInstruction(0xDDED4200, 3, "sbc IX, BC");
		// testInstruction(0xDDED5200, 3, "sbc IX, DE");
		// testInstruction(0xDDED6200, 3, "sbc IX, HL");
		// testInstruction(0xDDED7200, 3, "sbc IX, SP");
		// testInstruction(0xDDED4A00, 3, "adc IX, BC");
		// testInstruction(0xDDED5A00, 3, "adc IX, DE");
		// testInstruction(0xDDED6A00, 3, "adc IX, HL");
		// testInstruction(0xDDED7A00, 3, "adc IX, SP");

		// IY
		testInstruction(0xFD2A0123, 4, "ld IY, (2301)");
		testInstruction(0xFD2AFEDC, 4, "ld IY, (DCFE)");
		testInstruction(0xFD220123, 4, "ld (2301), IY");
		testInstruction(0xFD22FEDC, 4, "ld (DCFE), IY");
		testInstruction(0xFD467800, 3, "ld B, (IY+78)");
		testInstruction(0xFD4E7800, 3, "ld C, (IY+78)");
		testInstruction(0xFD567800, 3, "ld D, (IY+78)");
		testInstruction(0xFD5E7800, 3, "ld E, (IY+78)");
		testInstruction(0xFD667800, 3, "ld H, (IY+78)");
		testInstruction(0xFD6E7800, 3, "ld L, (IY+78)");
		testInstruction(0xFD707800, 3, "ld (IY+78), B");
		testInstruction(0xFD717800, 3, "ld (IY+78), C");
		testInstruction(0xFD727800, 3, "ld (IY+78), D");
		testInstruction(0xFD737800, 3, "ld (IY+78), E");
		testInstruction(0xFD747800, 3, "ld (IY+78), H");
		testInstruction(0xFD757800, 3, "ld (IY+78), L");
		testInstruction(0xFD777800, 3, "ld (IY+78), A");
		testInstruction(0xFD7E7800, 3, "ld A, (IY+78)");
		testInstruction(0xFD367801, 4, "ld (IY+78), 01");
		testInstruction(0xFD3678FE, 4, "ld (IY+78), FE");
		testInstruction(0xFDF90000, 2, "ld SP, IY");
		testInstruction(0xED6B0123, 4, "ld HL, (2301)");
		testInstruction(0xED6BFEDC, 4, "ld HL, (DCFE)");
		testInstruction(0xED630123, 4, "ld (2301), HL");
		testInstruction(0xED63FEDC, 4, "ld (DCFE), HL");
		testInstruction(0xFD210123, 4, "ld IY, 2301");
		testInstruction(0xFD21FEDC, 4, "ld IY, DCFE");
		testInstruction(0xFDA67800, 3, "and (IY+78)");
		testInstruction(0xFDB67800, 3, "or (IY+78)");
		testInstruction(0xFDAE7800, 3, "xor (IY+78)");
		testInstruction(0xFD357800, 3, "dec (IY+78)");
		testInstruction(0xFD2B0000, 2, "dec IY");
		testInstruction(0xFD347800, 3, "inc (IY+78)");
		testInstruction(0xFD230000, 2, "inc IY");
		testInstruction(0xFD9E7800, 3, "sbc A, (IY+78)");
		testInstruction(0xFD967800, 3, "sub (IY+78)");
		testInstruction(0xFD8E7800, 3, "adc A, (IY+78)");
		testInstruction(0xFD867800, 3, "add A, (IY+78)");
		testInstruction(0xFD090000, 2, "add IY, BC");
		testInstruction(0xFD190000, 2, "add IY, DE");
		testInstruction(0xFD290000, 2, "add IY, IY"); // TODO: está bien?
		testInstruction(0xFD390000, 2, "add IY, SP");
		testInstruction(0xFDCB7886, 4, "res 0, (IY+78)");
		testInstruction(0xFDCB788E, 4, "res 1, (IY+78)");
		testInstruction(0xFDCB7896, 4, "res 2, (IY+78)");
		testInstruction(0xFDCB789E, 4, "res 3, (IY+78)");
		testInstruction(0xFDCB78A6, 4, "res 4, (IY+78)");
		testInstruction(0xFDCB78AE, 4, "res 5, (IY+78)");
		testInstruction(0xFDCB78B6, 4, "res 6, (IY+78)");
		testInstruction(0xFDCB78BE, 4, "res 7, (IY+78)");
		testInstruction(0xFDCB78C6, 4, "set 0, (IY+78)");
		testInstruction(0xFDCB78CE, 4, "set 1, (IY+78)");
		testInstruction(0xFDCB78D6, 4, "set 2, (IY+78)");
		testInstruction(0xFDCB78DE, 4, "set 3, (IY+78)");
		testInstruction(0xFDCB78E6, 4, "set 4, (IY+78)");
		testInstruction(0xFDCB78EE, 4, "set 5, (IY+78)");
		testInstruction(0xFDCB78F6, 4, "set 6, (IY+78)");
		testInstruction(0xFDCB78FE, 4, "set 7, (IY+78)");
		testInstruction(0xFDCB7846, 4, "bit 0, (IY+78)");
		testInstruction(0xFDCB784E, 4, "bit 1, (IY+78)");
		testInstruction(0xFDCB7856, 4, "bit 2, (IY+78)");
		testInstruction(0xFDCB785E, 4, "bit 3, (IY+78)");
		testInstruction(0xFDCB7866, 4, "bit 4, (IY+78)");
		testInstruction(0xFDCB786E, 4, "bit 5, (IY+78)");
		testInstruction(0xFDCB7876, 4, "bit 6, (IY+78)");
		testInstruction(0xFDCB787E, 4, "bit 7, (IY+78)");
		testInstruction(0xFDCB7806, 4, "rlc (IY+78)");
		testInstruction(0xFDCB780E, 4, "rrc (IY+78)");
		testInstruction(0xFDCB7816, 4, "rl (IY+78)");
		testInstruction(0xFDCB781E, 4, "rr (IY+78)");
		testInstruction(0xFDCB7826, 4, "sla (IY+78)");
		testInstruction(0xFDCB782E, 4, "sra (IY+78)");
		testInstruction(0xFDCB783E, 4, "srl (IY+78)");
		testInstruction(0xFDBE7800, 3, "cp (IY+78)");
		testInstruction(0xFDE50000, 2, "push IY");
		testInstruction(0xFDE10000, 2, "pop IY");
		// en principio, las siguientes no existen:
		// testInstruction(0xFD767800, 3, "ld (IY+78), (HL)");
		// testInstruction(0xFDED6C00, 3, "mlt IY");
		// testInstruction(0xFDED4200, 3, "sbc IY, BC");
		// testInstruction(0xFDED5200, 3, "sbc IY, DE");
		// testInstruction(0xFDED6200, 3, "sbc IY, HL");
		// testInstruction(0xFDED7200, 3, "sbc IY, SP");
		// testInstruction(0xFDED4A00, 3, "adc IY, BC");
		// testInstruction(0xFDED5A00, 3, "adc IY, DE");
		// testInstruction(0xFDED6A00, 3, "adc IY, HL");
		// testInstruction(0xFDED7A00, 3, "adc IY, SP");

		// TODO: testear getOperand además de getOperandName
		// TODO: revisar TODOs del JZ80Processor
		// TODO: revisar inexistentes con IX/IY
		// TODO: siempre vale que HL se puede reemplazar por IX/IY?

		// TODO: cargar estos testeos desde un archivo de datos
	}

	public static void main(String[] args) {
		JZ80InstructionSetTest t = new JZ80InstructionSetTest();
		t.test();
		System.out.println("Decoding test done.");
		t.testMissing();
		System.out.println("Missing codes test done.");
	}
}