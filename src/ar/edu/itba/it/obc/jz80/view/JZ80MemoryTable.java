package ar.edu.itba.it.obc.jz80.view;

import java.awt.Dimension;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.table.*;

import ar.edu.itba.it.obc.jz80.JZ80Sim;
import ar.edu.itba.it.obc.jz80.api.DeviceListener;
import ar.edu.itba.it.obc.jz80.api.Device;
import ar.edu.itba.it.obc.jz80.api.Memory;

public class JZ80MemoryTable extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTable jTable = null;

	private JZ80MemoryTableModel tableModel = null;

	private JZ80Sim sim;

	public JZ80MemoryTable(JZ80Sim s) {
		super();
		sim = s;
		setViewportView(getJTable());
		setPreferredSize(new Dimension(600, 150));
		getJTable().setToolTipText("Memoria");
	}

	/*
	 * JViewport v = getViewport(); int h = getJTable().getRowHeight() *
	 * (address / 16); v.setViewPosition(new Point(0, h)); super.repaint();
	 */

	private JZ80MemoryTableModel getMemoryTableModel() {
		if (tableModel == null) {
			Memory memory = sim.getSystem().getMemory();
			tableModel = new JZ80MemoryTableModel(memory);
			memory.addListener(new DeviceListener() {
				public void onWrite(Device d, int address) {
					repaint();
				}
			});
		}
		return tableModel;
	}

	/**
	 * This method initializes jTable
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable(getMemoryTableModel());
			jTable.setColumnSelectionAllowed(false);
			jTable.setRowSelectionAllowed(false);
			JTableHeader th = jTable.getTableHeader();
			th.setResizingAllowed(false);
			th.setReorderingAllowed(false);
			((DefaultTableCellRenderer) jTable
					.getDefaultRenderer(jTable.getClass()))
					.setHorizontalAlignment(SwingConstants.CENTER);
			initializeColumns();
		}
		return jTable;
	}

	private void initializeColumns() {
		Enumeration<TableColumn> cs = jTable.getColumnModel().getColumns();
		while (cs.hasMoreElements()) {
			TableColumn c = cs.nextElement();
			int w = 20;
			if (c.getModelIndex() == 0) {
				w = 40;
			} else if (c.getModelIndex() == 17) {
				w = 160;
			}
			c.setMinWidth(w);
			c.setPreferredWidth(w);
		}
	}

}
