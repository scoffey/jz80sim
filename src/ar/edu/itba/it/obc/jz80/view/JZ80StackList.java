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
import ar.edu.itba.it.obc.jz80.view.JZ80StackListModel;

public class JZ80StackList extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JZ80Sim sim;

	private JList jList = null;

	private JZ80StackListModel listModel = null;
	private Register sp = null;

	public JZ80StackList(JZ80Sim s) {
		super();
		sim = s;
		setViewportView(getJList());

		sp = sim.getSystem().getCPU().getRegister(JZ80RegisterName.SP);
		DeviceListener dl = new DeviceListener() {
			public void onWrite(Device d, int address) {
				setSelectedWordAt(sp.readValue());
			}
		};
		
		sp.addListener(dl);
		
		dl = new DeviceListener() {
			public void onWrite(Device d, int address) {
				getStackListModel().refreshAddress(address);
			}
		};
		
		sim.getSystem().getMemory().addListener(dl);

		verticalScrollBar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				JZ80StackListModel m = getStackListModel();
				int max = verticalScrollBar.getMaximum();
				int min = verticalScrollBar.getMinimum();
				int visible = verticalScrollBar.getVisibleAmount();
				int value = e.getValue();
				int address, index;

				// If the scroll bar is close to the
				// limits, the stack list is updated
				if (Math.abs(max - value) < (visible + 1)) {
					address = sp.readValue();
					m.readNextMemBlock();
					
					index = checkAddressVisibility(address);
					
					if(index >= 0)
					{
						getJList().setSelectedIndex(index);
					}

					verticalScrollBar.setValue(max - (visible + 1));
				} else if (Math.abs(min - value) < 2) {
					address = sp.readValue();
					m.readPreviousMemBlock();
					
					index = checkAddressVisibility(address);
					
					if(index >= 0)
					{
						getJList().setSelectedIndex(index);
					}

					// The magic value 3 allows to keep scrolling up
					verticalScrollBar.setValue(3);
				}

			}
		});
	}

	public int checkAddressVisibility(int address)
	{
		JZ80StackListModel m = getStackListModel();
		JViewport v = getViewport();
		int index = m.calculateAddrIndex(address);
		
		if(index < 0)
			return index;
		
		Rectangle rect = v.getViewRect();
		// Not using component height because it can be 0
		Point p = new Point(0, 18 * index);

		return rect.contains(p) ? index : -1 ;
	}
	
	public void setSelectedWordAt(int address) {
		JViewport v = getViewport();
		JZ80StackListModel m = getStackListModel();
		int index = m.calculateAddrIndex(address);

		if (index < 0) {
			m.refreshListFrom(address);
			index = m.calculateAddrIndex(address);
		}

		jList.setSelectedIndex(index);
		Rectangle rect = v.getViewRect();
		// Not using component height because it can be 0
		Point p = new Point(0, 18 * index);

		if (!rect.contains(p))
			v.setViewPosition(p);

	}

	private JZ80StackListModel getStackListModel() {
		if (listModel == null) {
			listModel = new JZ80StackListModel(sim);
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
			jList = new JList(getStackListModel());
			jList.setFixedCellWidth(80);
			jList.setToolTipText("Pila");
		}
		return jList;
	}

}
