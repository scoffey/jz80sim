package ar.edu.itba.it.obc.jz80.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import ar.edu.itba.it.obc.jz80.api.DeviceListener;
import ar.edu.itba.it.obc.jz80.api.Device;
import ar.edu.itba.it.obc.jz80.api.Register;

public class JZ80RegisterField extends JTextField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Register register;

	public JZ80RegisterField(Register r) {
		super(r.toString());
		register = r;
		setInputVerifier(getRegisterInputVerifier());

		r.addListener(new DeviceListener() {
			public void onWrite(Device d, int address) {
				setText(register.toString());
				// Esto fuerza el repaint del JTextField y mantiene
				// consistente el estado de la vista con el del modelo
			}
		});

		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getInputVerifier().verify((JComponent) e.getSource());
			}
		});

	}

	private InputVerifier getRegisterInputVerifier() {
		final Register r = register;
		return new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				JZ80RegisterField tf = (JZ80RegisterField) input;
				int v = 0;
				try {
					v = Integer.parseInt(tf.getText(), 16);
					v &= (r.getByteSize() < 2) ? 0xFF : 0xFFFF;
				} catch (Exception e) {
				}
				r.writeValue(v);
				setText(r.toString());
				return true;
			}
		};
	}

}
