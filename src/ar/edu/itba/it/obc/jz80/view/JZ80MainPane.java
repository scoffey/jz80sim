package ar.edu.itba.it.obc.jz80.view;

import java.awt.*;

import javax.swing.*;

import ar.edu.itba.it.obc.jz80.JZ80Sim;

public class JZ80MainPane extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JZ80FlagsPanel flagsPanel = null;

	private JZ80CPUPanel cpuPanel = null;

	private JZ80MemoryTable table = null;

	private JZ80InstructionList instructionList = null;

	private JZ80StackList stackList = null;

	private JZ80Sim sim = null;

	public JZ80MainPane(JZ80Sim s) {
		super();
		sim = s;
		setViewportView(getJContentPane());
	}

	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.gridwidth = 1;
			c.gridheight = 1;
			c.weightx = 0;
			c.insets = new Insets(8, 8, 8, 8);
			jContentPane.add(getFlagsPanel(), c);
			c.weightx = 1;
			jContentPane.add(getCPUPanel(), c);
			c.insets = new Insets(0, 0, 0, 0);
			jContentPane.add(getInstructionList(), c);
			c.gridwidth = GridBagConstraints.REMAINDER;
			jContentPane.add(getStackList(), c);
			c.weighty = 1;
			jContentPane.add(getMemoryTable(), c);
		}
		return jContentPane;
	}

	public JPanel getCPUPanel() {
		if (cpuPanel == null) {
			cpuPanel = new JZ80CPUPanel(sim);
		}
		return cpuPanel;
	}

	public JZ80InstructionList getInstructionList() {
		if (instructionList == null) {
			instructionList = new JZ80InstructionList(sim);
		}
		return instructionList;
	}

	public JZ80StackList getStackList() {
		if (stackList == null) {
			stackList = new JZ80StackList(sim);
		}
		return stackList;
	}

	public JZ80MemoryTable getMemoryTable() {
		if (table == null) {
			table = new JZ80MemoryTable(sim);
		}
		return table;
	}

	public JPanel getFlagsPanel() {
		if (flagsPanel == null) {
			flagsPanel = new JZ80FlagsPanel(sim);
		}
		return flagsPanel;
	}
	
}
