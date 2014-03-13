package ar.edu.itba.it.obc.jz80.devices;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;

import ar.edu.itba.it.obc.jz80.api.DeviceView;
import ar.edu.itba.it.obc.jz80.api.Device;

public class JZ80KeyboardView extends JPanel implements DeviceView {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JZ80Keyboard keyboard;
	
	public JZ80KeyboardView() {
		super();
		keyboard = new JZ80Keyboard();
		setPreferredSize(new Dimension(640, 240));
		setLayout(new GridLayout(8, 16));
		// buttons
		for (int i = 0; i < 0x80; i++) {
			JButton button = getButton(keyboard, i);
			if (button.getText().length() > 1) {
				Font f = button.getFont();
				button.setFont(new Font(f.getFontName(), 0, 9));
			}
			add(button);
		}
	}

	private JButton getButton(JZ80Keyboard k, int ascii) {
		final String[] controlChars = { "NUL", "SOH", "STX", "ETX", "EOT", "ENQ",
				"ACK", "BEL", "BS", "HT", "LF", "VT", "FF", "CR", "SO",
				"SI", "DLE", "DC1", "DC2", "DC3", "DC4", "NAK", "SYN",
				"ETB", "CAN", "EM", "SUB", "ESC", "FS", "GS", "RS", "US" };
		String s;
		if (ascii < 32) {
			s = controlChars[ascii];
		} else if (ascii == 0x7F) {
			s = "DEL";
		} else {
			s = String.valueOf(Character.toChars(ascii));
		}
		JButton button = new JButton(s);
		button.setMargin(new Insets(2, 2, 2, 2));
		final int data = ascii;
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				keyboard.setKeyDown(data);
			}
		});
		return button;
	}

	public int[] getDefaultPorts() {
		return new int[] {0x80, 0x81};
	}

	public Device getDevice() {
		return keyboard;
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
