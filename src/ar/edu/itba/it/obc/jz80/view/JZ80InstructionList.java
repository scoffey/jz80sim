package ar.edu.itba.it.obc.jz80.view;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.*;

import ar.edu.itba.it.obc.jz80.JZ80Sim;
import ar.edu.itba.it.obc.jz80.api.DeviceListener;
import ar.edu.itba.it.obc.jz80.api.Device;
import ar.edu.itba.it.obc.jz80.api.Register;
import ar.edu.itba.it.obc.jz80.system.JZ80RegisterName;
import ar.edu.itba.it.obc.jz80.view.JZ80InstructionListModel;

public class JZ80InstructionList extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JZ80Sim sim;

	private JList jList = null;

	private JZ80InstructionListModel listModel = null;
	private Register pc = null;

	public JZ80InstructionList(JZ80Sim s) {
		super();
		sim = s;
		setViewportView(getJList());
		pc = sim.getSystem().getCPU().getRegister(JZ80RegisterName.PC);

		DeviceListener dl = new DeviceListener() {
			public void onWrite(Device d, int address) {
				setSelectedInstructionAt(sim.getSystem().readProgramCounter());
			}
		};
		
		pc.addListener(dl);
				
		dl = new DeviceListener() {
			public void onWrite(Device d, int address) {
				getInstructionListModel().refreshInstruction(address);
			}
		};
		
		sim.getSystem().getMemory().addListener(dl);
				

		verticalScrollBar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int max = verticalScrollBar.getMaximum();
				int visible = verticalScrollBar.getVisibleAmount();
				int value = e.getValue();
				JZ80InstructionListModel m = null;

				if (Math.abs(max - value) < (visible + 1)) {
					m = getInstructionListModel();
					m.readNewInstructionBlock();
					
					//Selects the current instruction pointed by pc 
					//only if its visible
					if(!selectPcInstrIfVisible(pc.readValue()))
					{
						//Just moves the scroll bar
						verticalScrollBar.setValue(max - (visible + 1));
					}
					
				}
			}
		});
	}

	public void setViewPortAt(int index) {
		JViewport v = getViewport();
		Rectangle rect = v.getViewRect();
		// Not using component height because it can be 0
		Point p = new Point(0, 18 * index);

		if (!rect.contains(p))
			v.setViewPosition(p);
	}
	
	public boolean selectPcInstrIfVisible(int address) {
		JZ80InstructionListModel m = getInstructionListModel();		
		int instIndex = m.calculateAddrIndex(address);
		JViewport v = null; 
		Rectangle rect = null;
		// Not using component height because it can be 0
		Point p = null;
		
		if(instIndex == -1)
			return false;
		
		v = getViewport();
		rect = v.getViewRect();
		p = new Point(0, 18 * instIndex);

		if (rect.contains(p))
		{
			getJList().setSelectedIndex(instIndex);
			return true;
		}		
		
		return false;
	}

	public void setSelectedInstructionAt(int address) {

		JZ80InstructionListModel m = getInstructionListModel();
		int instIndex = m.calculateAddrIndex(address);

		if (instIndex < 0) {
			m.refreshListFrom(address);
			instIndex = m.calculateAddrIndex(address);
		}

		if (instIndex >= 0) {
			getJList().setSelectedIndex(instIndex);
			setViewPortAt(instIndex);
		}
		else
		{		
			System.err.println(String.format("JZ80InstructionList.setSelectedInstructionAt:"
					+ " Instr couldn't be found Address: %X", address));
		}

	}

	private JZ80InstructionListModel getInstructionListModel() {
		if (listModel == null) {
			listModel = new JZ80InstructionListModel(sim);
		}
		return listModel;
	}

	/**
	 * This method initializes jList
	 * 
	 * @return javax.swing.JList
	 */
	private JList getJList() {
		if (jList == null) {
			jList = new JList(getInstructionListModel());
			jList.setFixedCellWidth(180);
			jList.setToolTipText("Lista de instrucciones");
		}
		return jList;
	}

}
