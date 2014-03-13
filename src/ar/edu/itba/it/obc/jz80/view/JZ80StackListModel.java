package ar.edu.itba.it.obc.jz80.view;

import javax.swing.AbstractListModel;

import ar.edu.itba.it.obc.jz80.JZ80Sim;
import ar.edu.itba.it.obc.jz80.api.Memory;
import ar.edu.itba.it.obc.jz80.api.Register;
import ar.edu.itba.it.obc.jz80.system.JZ80RegisterName;

public class JZ80StackListModel extends AbstractListModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Register sp;

	private Memory memory;

	private int offset;
	
	
	public JZ80StackListModel(JZ80Sim s) {
		sp = s.getSystem().getCPU().getRegister(
				JZ80RegisterName.SP);
		memory = s.getSystem().getMemory();	

		refreshList();
	
	}

	public int getOffset() {
		return offset;
	}
	
	public void refreshList() {
		refreshListFrom(sp.readValue());
	}

	public void refreshListFrom(int address) {
		offset = address;
		fireContentsChanged(this,0,getSize()-1);
	}
	
	public void refreshAddress(int address) {
		int index = calculateAddrIndex(address);
		
		//The address is not being displayed 
		if(index < 0)
			return;
		
		fireContentsChanged(this,index,index);
	}	

	public Object getElementAt(int index) {
		int address = (offset + index*2) & 0xFFFF;
		int msb = memory.readByteAt(address) & 0xFF;
		int lsb = memory.readByteAt(address + 1) & 0xFF;
		return String.format("%04X: %04X", address, (msb << 8) | lsb);
	}

	public int getSize() {
		return 64;
	}
	
	public void readNextMemBlock() {
		offset = (offset + 2) & 0xFFFF;
		fireContentsChanged(this,0,getSize()-1);	
	}
	
	public void readPreviousMemBlock() {
		offset = (offset - 2) & 0xFFFF;
		fireContentsChanged(this,0,getSize()-1);	
	}

	public int calculateAddrIndex(int address) 
	{		 
		int index = (address - offset)/2;
		
		return (index < 0 || index >= getSize()) ? -1 : index;
	}

}
