package ar.edu.itba.it.obc.jz80.devices;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import ar.edu.itba.it.obc.jz80.api.DeviceListener;
import ar.edu.itba.it.obc.jz80.api.DeviceView;
import ar.edu.itba.it.obc.jz80.api.Device;

public class JZ80TerminalView extends JPanel implements DeviceView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextArea textarea;

	private JZ80Terminal terminal;

	public JZ80TerminalView() {
		this(80, 24);
	}

	public JZ80TerminalView(int columns, int rows) {
		super();
		terminal = new JZ80Terminal(columns, rows);
		//setBackground(Color.black);
		setLayout(new GridLayout(1, 1));
		setPreferredSize(new Dimension((columns + 1) * 8, (rows + 2) * 13));
		add(getTextArea());
		terminal.addDeviceListenerAt(new DeviceListener() {

			public void onWrite(Device d, int address) {
				getTextArea().setText(terminal.getText());
			}

		}, 0);
	}

	private JTextArea getTextArea() {
		if (textarea == null) {
			textarea = new JTextArea();
			textarea.setFont(new Font("Courier", 0, 12));
			textarea.setForeground(new Color(0xCC, 0xCC, 0xCC));
			textarea.setBackground(new Color(0, 0, 0));
			textarea.setFocusable(true);
			textarea.setEditable(false);
			textarea.setEnabled(true);
			textarea.setSize(getWidth(), getHeight());
			textarea.setVisible(true);
		}
		return textarea;
	}

	public int[] getDefaultPorts() {
		return new int[] { 0x80 };
	}

	public Device getDevice() {
		return terminal;
	}

	public Container getView() {
		return this;
	}

	public Device getDevice(HashMap<String, String> options)
			throws Exception {
		// TODO Auto-generated method stub
		return getDevice();
	}

}
