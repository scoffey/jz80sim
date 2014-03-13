package ar.edu.itba.it.obc.jz80.devices;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;

import javax.swing.JPanel;

import ar.edu.itba.it.obc.jz80.api.DeviceView;
import ar.edu.itba.it.obc.jz80.api.Device;

public class JZ80LedArrayView extends JPanel implements DeviceView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static int LED_DIAMETER = 16;

	private JZ80LedArray leds;

	private int columns;

	public JZ80LedArrayView() {
		// TODO Auto-generated constructor stub
		columns = 32;
		leds = new JZ80LedArray(columns);
		setBackground(Color.black);
		setPreferredSize(new Dimension(columns * LED_DIAMETER + columns / 2,
				9 * LED_DIAMETER));
	}

	public int[] getDefaultPorts() {
		int[] ports = new int[leds.getSize()];
		for (int i = 0; i < ports.length; i++) {
			ports[i] = 0x88 + i;
		}
		return ports;
	}

	public Device getDevice() {
		return leds;
	}

	public Container getView() {
		return this;
	}

	public Device getDevice(HashMap<String, String> options)
			throws Exception {
		// TODO Auto-generated method stub
		return getDevice();
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaintMode();
		int xo = getWidth() / 2 - LED_DIAMETER * columns / 2;
		int yo = getHeight() / 2 - LED_DIAMETER * 4;
		for (int i = 0; i < columns; i++) {
			byte b = leds.readByteAt(i);
			for (int j = 0; j < 8; j++) {
				g2.setPaint((b & (1 << j)) == 0 ? Color.darkGray : Color.red);
				g2.fillOval(xo + i * LED_DIAMETER, yo + j * LED_DIAMETER,
						LED_DIAMETER, LED_DIAMETER);
			}
		}
	}

}
