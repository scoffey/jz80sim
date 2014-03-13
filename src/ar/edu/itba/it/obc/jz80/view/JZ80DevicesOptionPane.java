package ar.edu.itba.it.obc.jz80.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import ar.edu.itba.it.obc.jz80.JZ80Sim;
import ar.edu.itba.it.obc.jz80.api.DeviceView;
import ar.edu.itba.it.obc.jz80.devices.JZ80KeyboardView;
import ar.edu.itba.it.obc.jz80.devices.JZ80LedArrayView;
import ar.edu.itba.it.obc.jz80.devices.JZ80TerminalView;

public class JZ80DevicesOptionPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JZ80Sim controller;

	private JComboBox combo;

	private JTextField input;

	private JList list;

	private ArrayList<Object[]> availableDevices;

	public JZ80DevicesOptionPane(JZ80Sim sim) {
		super();
		controller = sim;
		availableDevices = controller.getAvailableDevices();
		// combo NORTH
		setLayout(new BorderLayout(8, 8));
		add(getDevicesComboBox(), BorderLayout.NORTH);
		// panel WEST
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(5, 1));
		panel.add(getInputField());
		panel.add(getAddButton());
		panel.add(getRemoveButton());
		add(panel, BorderLayout.WEST);
		// scrollpane CENTER
		JScrollPane scrollpane = new JScrollPane();
		scrollpane.setViewportView(getDevicesList());
		add(scrollpane, BorderLayout.CENTER);
	}

	private JComboBox getDevicesComboBox() {
		if (combo == null) {
			String[] names = new String[availableDevices.size()];
			for (int i = 0; i < names.length; i++) {
				names[i] = (String) (availableDevices.get(i)[1]);
			}
			combo = new JComboBox(names);
			combo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int i = combo.getSelectedIndex();
					Integer defaultPort = (Integer) (availableDevices.get(i)[0]);
					getInputField().setText(String.format("%02X", defaultPort));
				}
			});
			combo.setSelectedIndex(0);
		}
		return combo;
	}

	private JTextField getInputField() {
		if (input == null) {
			input = new JTextField();
		}
		return input;
	}

	private JButton getAddButton() {
		JButton button = new JButton("Conectar");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = getInputField().getText();
				if (s.equals("")) {
					controller.displayErrorMessage("Ingrese la dirección "
							+ "del puerto donde conectar \nel dispositivo "
							+ "en notación hexadecimal.");
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
							+ "una dirección de \npuerto de I/O válida en "
							+ "notación hexadecimal.");
					return;
				} catch (IllegalArgumentException nfe) {
					controller.displayErrorMessage("El valor ingresado está "
							+ "fuera del rango de \ndireccionamiento "
							+ "en notación hexadecimal.");
					return;
				}
				DeviceView deviceView = null;
				int i = getDevicesComboBox().getSelectedIndex();
				// TODO: instanciar dinamicamente por reflection
				// segun la clase availableDevices[i][2]
				switch (i) {
				case 0:
					deviceView = new JZ80TerminalView();
					break;
				case 1:
					deviceView = new JZ80KeyboardView();
					break;
				case 2:
					deviceView = new JZ80LedArrayView();
					break;
				}
				controller.connectDevice(address, deviceView);
				updateDeviceListData();
				getInputField().setText("");
			}
		});
		return button;
	}

	private JButton getRemoveButton() {
		JButton button = new JButton("Desconectar");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = (String) getDevicesList().getSelectedValue();
				if (s == null) {
					controller.displayErrorMessage("No hay ningún "
							+ "dispositivo seleccionado\n en la lista.");
					return;
				}
				int address = Integer.parseInt(s.substring(0, s.indexOf(':')),
						16);
				controller.disconnectDevice(address);
				updateDeviceListData();
			}
		});
		return button;
	}

	private JList getDevicesList() {
		if (list == null) {
			list = new JList();
			list.setMinimumSize(new Dimension(200, 300));
			updateDeviceListData();
		}
		return list;
	}

	private void updateDeviceListData() {
		HashMap<String, String> names = new HashMap<String, String>();
		for (Object[] i : availableDevices) {
			names.put(((Class<?>) i[2]).getName(), (String) i[1]);
		}
		HashMap<Integer, DeviceView> connected = controller
				.getConnectedDevices();
		String[] data = new String[connected.size()];
		Iterator<Integer> keys = connected.keySet().iterator();
		for (int i = 0; i < data.length; i++) {
			Integer port = keys.next();
			data[i] = String.format("%02X: %s", port, names.get(connected.get(
					port).getClass().getName()));
		}
		getDevicesList().setListData(data);
	}

}
