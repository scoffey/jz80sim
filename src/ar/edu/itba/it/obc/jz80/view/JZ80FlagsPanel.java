package ar.edu.itba.it.obc.jz80.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ar.edu.itba.it.obc.jz80.JZ80Sim;
import ar.edu.itba.it.obc.jz80.api.DeviceListener;
import ar.edu.itba.it.obc.jz80.api.Device;
import ar.edu.itba.it.obc.jz80.api.Register;
import ar.edu.itba.it.obc.jz80.system.JZ80Flag;
import ar.edu.itba.it.obc.jz80.system.JZ80RegisterName;

public class JZ80FlagsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JZ80Sim sim;

	private JCheckBox[] checkboxes;

	public JZ80FlagsPanel(JZ80Sim s) {
		super();
		sim = s;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(getIcon());
		add(getFlagCheckboxesPanel());
	}

	private JLabel getIcon() {
		JLabel icon = new JLabel();
		icon.setSize(new Dimension(48, 48));
		icon.setAlignmentX(Component.CENTER_ALIGNMENT);
		URL url = getClass().getResource("/resources/img/gnome-ccperiph.png");
		ImageIcon img = new ImageIcon(url);
		icon.setIcon(img);
		return icon;
	}

	private JPanel getFlagCheckboxesPanel() {
		// checkboxes
		checkboxes = new JCheckBox[6];
		checkboxes[0] = getFlagCheckBox(JZ80Flag.S, "Sign");
		checkboxes[1] = getFlagCheckBox(JZ80Flag.P, "Parity/Overflow");
		checkboxes[2] = getFlagCheckBox(JZ80Flag.Z, "Zero");
		checkboxes[3] = getFlagCheckBox(JZ80Flag.N, "Add/Substract");
		checkboxes[4] = getFlagCheckBox(JZ80Flag.H, "Half-carry");
		checkboxes[5] = getFlagCheckBox(JZ80Flag.C, "Carry");
		// panel
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 2));
		panel.setBorder(BorderFactory.createTitledBorder("Flags"));
		for (int i = 0; i < checkboxes.length; i++) {
			panel.add(checkboxes[i]);
		}
		// device listener
		Register r = sim.getSystem().getCPU().getRegister(
				JZ80RegisterName.F);
		r.addListener(new DeviceListener() {
			public void onWrite(Device d, int address) {
				int val = d.readByteAt(address);
				checkboxes[0].setSelected((val & JZ80Flag.S.getMask()) != 0);
				checkboxes[1].setSelected((val & JZ80Flag.P.getMask()) != 0);
				checkboxes[2].setSelected((val & JZ80Flag.Z.getMask()) != 0);
				checkboxes[3].setSelected((val & JZ80Flag.N.getMask()) != 0);
				checkboxes[4].setSelected((val & JZ80Flag.H.getMask()) != 0);
				checkboxes[5].setSelected((val & JZ80Flag.C.getMask()) != 0);
			}
		});
		return panel;
	}

	private JCheckBox getFlagCheckBox(JZ80Flag flag, String tooltip) {
		JCheckBox checkbox = new JCheckBox();
		checkbox.setText(flag.getMask() == JZ80Flag.P.getMask() ? "P/V" : flag
				.toString());
		checkbox.setToolTipText(tooltip);
		final JZ80Flag f = flag;
		checkbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox c = (JCheckBox) e.getSource();
				Register r = sim.getSystem().getCPU().getRegister(
						JZ80RegisterName.F);
				if (c.isSelected()) {
					r.writeValue(r.readValue() | f.getMask());
				} else {
					r.writeValue(r.readValue() & ~f.getMask());
				}
			}
		});
		return checkbox;
	}

	public void repaint() {
		if (checkboxes == null)
			return; // para evitar la llamada en super; luego siempre funciona
		for (int i = 0; i < checkboxes.length; i++) {
			checkboxes[i].repaint();
		}
		super.repaint();
	}
}
