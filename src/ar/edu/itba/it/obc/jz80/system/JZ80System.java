package ar.edu.itba.it.obc.jz80.system;

import java.io.*;

import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.it.obc.jz80.api.Device;
import ar.edu.itba.it.obc.jz80.api.Instruction;
import ar.edu.itba.it.obc.jz80.api.InstructionException;
import ar.edu.itba.it.obc.jz80.api.Memory;
import ar.edu.itba.it.obc.jz80.api.Processor;
import ar.edu.itba.it.obc.jz80.api.Z80System;
import ar.edu.itba.it.obc.jz80.instructions.JZ80Instruction;
import ar.edu.itba.it.obc.jz80.instructions.JZ80InstructionSet;
import ar.edu.itba.it.obc.jz80.instructions.JZ80InvalidInstructionException;

public class JZ80System implements Z80System {

	public static boolean DEBUG = false;

	private JZ80Processor cpu;

	private JZ80Memory memory;

	private JZ80InstructionSet set;

	private Map<Integer, Device> devices;

	private Map<Integer, Integer> portMapping;
	
	public JZ80System() {
		cpu = new JZ80Processor(this);
		memory = new JZ80Memory();
		set = new JZ80InstructionSet();
		devices = new HashMap<Integer, Device>();
		portMapping = new HashMap<Integer, Integer>();
	}

	public Memory getMemory() {
		return memory;
	}

	public Processor getCPU() {
		return cpu;
	}

	public Device getDevice(int port) {
		return devices.get(port);
	}
	
	public byte readInputAt(int port) {
		Device device = devices.get(port);
		if (device == null)
			return 0; // TODO: throw exception
		Integer deviceAddress = portMapping.get(port);
		if (deviceAddress == null)
			deviceAddress = 0;
		return device.readByteAt(deviceAddress);
	}

	public void writeOutputAt(int port, byte value) {
		Device device = devices.get(port);
		if (device == null)
			return; // TODO: throw exception
		Integer deviceAddress = portMapping.get(port);
		if (deviceAddress == null)
			deviceAddress = 0;
		device.writeByteAt(deviceAddress, value);
	}

	public Device connectDevice(int port, int deviceAddress,
			Device device) {
		portMapping.put(port, deviceAddress);
		devices.put(port, device);
		return null;
	}

	public Device disconnectDevice(int port) {
		return devices.remove(port);
	}

	public Device connectMultiplePortDevice(int firstPort, int lastPort,
			Device device) {
		int totalPorts = lastPort - firstPort + 1;
		Device otherDevice = null;
		for (int i = 0; i < totalPorts; i++) {
			Device tmp = connectDevice(firstPort + i, i, device);
			if (tmp != null)
				otherDevice = tmp;
		}
		return otherDevice;
	}

	public Device disconnectMultiplePortDevice(int port) {
		Device device = devices.remove(port);
		if (device == null)
			return null;
		for (int i = port + 1; devices.get(i) != null
				&& device == devices.get(i); i++) {
			devices.remove(i);
		}
		for (int i = port - 1; devices.get(i) != null
				&& device == devices.get(i); i--) {
			devices.remove(i);
		}
		return device;
	}

	public boolean hasAvailablePortRange(int firstPort, int lastPort) {
		for (int i = firstPort; i <= lastPort; i++) {
			if (devices.get(i) != null) {
				return false;
			}
		}
		return true;
	}

	public Instruction fetchInstructionAt(int address)
			throws JZ80InvalidInstructionException {
		int fetchedBytes = memory.fetchInstructionAt(address);
		return set.decode(fetchedBytes);
	}

	public Instruction executeNext() throws InstructionException {
		int fetchedBytes = cpu.fetchInstruction();
		JZ80Instruction instruction = set.decode(fetchedBytes);
		instruction.execute(this);
		debugInstructionExecution(instruction);
		return instruction;
	}

	private void debugInstructionExecution(JZ80Instruction instruction) {
		if (!DEBUG)
			return;
		StringBuffer s = new StringBuffer(instruction.toString() + ": ");
		for (JZ80RegisterName r : JZ80RegisterName.values()) {
			s.append(r.toString() + "=" + cpu.getRegister(r).toString() + ", ");
		}
		System.out.println(s.substring(0, s.length() - 2));
	}

	public int readProgramCounter() {
		return cpu.getRegister(JZ80RegisterName.PC).readValue();
	}

	public int readStackPointer() {
		return cpu.getRegister(JZ80RegisterName.SP).readValue();
	}

	public void loadFileToMemory(File f) throws Exception {
		JZ80MemoryLoader loader = new JZ80MemoryLoader(this);
		loader.loadFile(f);
	}

	public void reset() {
		cpu.reset();
		memory.reset();
		resetDevices();
	}

	public void resetDevices() {
		for (Device d : devices.values()) {
			d.reset();
		}
	}
}
