package ar.edu.itba.it.obc.jz80.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import ar.edu.itba.it.obc.jz80.JZ80Sim;

public class JZ80BreakpointsPane extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JZ80Sim controller;

	private JTextField input;

	private JList list;

	public JZ80BreakpointsPane(JZ80Sim sim) {
		super();
		controller = sim;
		setLayout(new BorderLayout(8, 0));
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(5, 1));
		panel.add(getInputField());
		panel.add(getAddButton());
		panel.add(getRemoveButton());
		add(panel, BorderLayout.WEST);
		JScrollPane scrollpane = new JScrollPane();
		scrollpane.setViewportView(getBreakpointList());
		updateBreakpointListData();
		add(scrollpane, BorderLayout.CENTER);
	}

	private JTextField getInputField() {
		if (input == null) {
			input = new JTextField();
		}
		return input;
	}

	private JButton getAddButton() {
		JButton button = new JButton("Agregar");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = getInputField().getText();
				if (s.equals("")) {
					controller.displayErrorMessage("Ingrese la dirección "
							+ "de memoria a agregar \nen notación "
							+ "hexadecimal.");
					return;
				}
				Integer address;
				try {
					address = Integer.parseInt(s, 16);
					if (!(address >= 0 && address < 0x10000)) {
						throw new IllegalArgumentException(
								"Address is out of range");
					}
				} catch (NumberFormatException nfe) {
					controller.displayErrorMessage("El valor ingresado no es "
							+ "una dirección de \nmemoria válida en "
							+ "notación hexadecimal.");
					return;
				} catch (IllegalArgumentException nfe) {
					controller.displayErrorMessage("El valor ingresado está "
							+ "fuera del rango de \ndireccionamiento "
							+ "en notación hexadecimal.");
					return;
				}
				controller.addBreakpoint(address);
				updateBreakpointListData();
				input.setText("");
			}
		});
		return button;
	}

	private JButton getRemoveButton() {
		JButton button = new JButton("Quitar");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer address = (Integer) getBreakpointList()
						.getSelectedValue();
				if (address == null) {
					controller.displayErrorMessage("No hay ningún "
							+ "breakpoint seleccionado\n en la lista.");
					return;
				}
				controller.removeBreakpoint(address);
				updateBreakpointListData();
			}
		});
		return button;
	}

	private JList getBreakpointList() {
		if (list == null) {
			list = new JList();
			list.setMinimumSize(new Dimension(200, 300));
		}
		return list;
	}
	
	private void updateBreakpointListData() {
		Integer[] breakpoints = controller.getBreakpoints();
		String[] data = new String[breakpoints.length];
		for (int i = 0; i < breakpoints.length; i++) {
			data[i] = String.format("%04X", breakpoints[i]);
		}
		getBreakpointList().setListData(data);
	}

}
