package ar.edu.itba.it.obc.jz80.view;

import java.util.Arrays;
import javax.swing.AbstractListModel;
import ar.edu.itba.it.obc.jz80.JZ80Sim;
import ar.edu.itba.it.obc.jz80.api.Instruction;
import ar.edu.itba.it.obc.jz80.instructions.JZ80InvalidInstructionException;
import ar.edu.itba.it.obc.jz80.system.JZ80RegisterName;
import ar.edu.itba.it.obc.jz80.system.JZ80System;

public class JZ80InstructionListModel extends AbstractListModel {
	
	private static final long serialVersionUID = 1L;
	//private static final short BEGIN_OF= 1L;

	private JZ80Sim sim;
	
	private Instruction[] list;
	
	private int[] addresses;
	
	private int offset;				

	public JZ80InstructionListModel(JZ80Sim s) {
		sim = s;
		list = new Instruction[64];
		addresses = new int[64];
		offset = sim.getSystem().getCPU().getRegister(JZ80RegisterName.PC)
				.readValue();
		refreshListFrom(offset);		
	}	
	
	public void readNewInstructionBlock()
	{
		if(offset == 0x0FFFF){
			offset = 0;			
		}else if(addresses.length > 8){
			offset = addresses[8];					
		}else{
			offset = addresses[addresses.length - 1];
		}
		
		refreshListFrom(offset);		
	}	
	
	public int calculateAddrIndex(int address){
		int index;
		index = Arrays.binarySearch(addresses, address);		
		return index >=0 ? index : -1;
		
	}

	public int getOffset() {
		return offset;
	}

	public void refreshListFrom(int address) {
		JZ80System system = sim.getSystem();
		int i, j;
		offset = address;			
		
		for (i = address, j = 0; j < list.length ; j++) {
			addresses[j] = i;
			try {
				list[j] = system.fetchInstructionAt(i);
				i += list[j].getByteSize();
			} catch (JZ80InvalidInstructionException e) {
				//e.printStackTrace();
				i += 2; // TODO: por el momento suponemos que las instrucciones
				// desconocidas son de 2 bytes de largo
			}
		}		
		
		fireContentsChanged(this,0,getSize()-1);
	}	
	
	//This method updates a instruction that is currently being displayed on the
	//instruction list
	public void refreshInstruction(int address){
		int instrIndex = calculateAddrIndex(address);
		
		if(instrIndex < 0)
			return;
		
		refreshListFrom(offset);
	}

	public Object getElementAt(int index) {
		String s;
		try {
			s = list[index].toString();
		} catch (Exception e) {
			s = "?";
		}
		return String.format("%04X: %s", addresses[index] & 0xFFFF, s);
	}

	public int getSize() {
		return list.length;
	}	

}
