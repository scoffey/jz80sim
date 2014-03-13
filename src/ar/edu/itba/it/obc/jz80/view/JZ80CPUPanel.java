package ar.edu.itba.it.obc.jz80.view;

import java.awt.*;

import javax.swing.*;

import ar.edu.itba.it.obc.jz80.JZ80Sim;
import ar.edu.itba.it.obc.jz80.api.Processor;
import ar.edu.itba.it.obc.jz80.system.JZ80RegisterName;

public class JZ80CPUPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JZ80Sim sim;

	private JLabel[] labels;

	private JTextField[] fields;

	private JPanel[] panels;

	public JZ80CPUPanel(JZ80Sim s) {
		super();
		sim = s;
		setLayout(new GridBagLayout());
		initializeJPanels();
		addComponents();
	}

	private void initializeJLabels() {
		JZ80RegisterName[] r = new JZ80RegisterName[] { JZ80RegisterName.A, JZ80RegisterName.F,
				JZ80RegisterName.B, JZ80RegisterName.C, JZ80RegisterName.D, JZ80RegisterName.E,
				JZ80RegisterName.H, JZ80RegisterName.L, JZ80RegisterName.IX,
				JZ80RegisterName.IY, JZ80RegisterName.SP, JZ80RegisterName.PC };
		labels = new JLabel[r.length];
		for (int i = 0; i < r.length; i++) {
			labels[i] = new JLabel(r[i].toString());
			labels[i].setHorizontalAlignment(SwingConstants.CENTER);
		}
	}

	private void initializeJTextFields() {
		JZ80RegisterName[] r = new JZ80RegisterName[] { JZ80RegisterName.A, JZ80RegisterName.F,
				JZ80RegisterName.B, JZ80RegisterName.C, JZ80RegisterName.D, JZ80RegisterName.E,
				JZ80RegisterName.H, JZ80RegisterName.L, JZ80RegisterName.IX,
				JZ80RegisterName.IY, JZ80RegisterName.SP, JZ80RegisterName.PC };
		fields = new JTextField[r.length];
		//Font f = new Font("Monospaced", Font.TRUETYPE_FONT, 12);
		Processor p = sim.getSystem().getCPU();
		for (int i = 0; i < r.length; i++) {
			fields[i] = new JZ80RegisterField(p.getRegister(r[i]));
			//fields[i].setFont(f);
			fields[i].setHorizontalAlignment(JTextField.CENTER);
		}
	}

	private void initializeJPanels() {
		initializeJLabels();
		initializeJTextFields();
		panels = new JPanel[8];
		panels[0] = createPanel(0, 1); // AF
		panels[2] = createPanel(2, 3); // BC
		panels[4] = createPanel(4, 5); // DE
		panels[6] = createPanel(6, 7); // HL
		panels[1] = createPanel(8); // IX
		panels[3] = createPanel(9); // IY
		panels[5] = createPanel(10); // SP
		panels[7] = createPanel(11); // PC
	}

	private JPanel createPanel(int i, int j) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 2));
		panel.add(labels[i]);
		panel.add(labels[j]);
		panel.add(fields[i]);
		panel.add(fields[j]);
		return panel;
	}

	private JPanel createPanel(int i) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 1));
		panel.add(labels[i]);
		panel.add(fields[i]);
		return panel;
	}

	private void addComponents() {
		GridLayout layout = new GridLayout(4, 2);
		layout.setHgap(8);
		setLayout(layout);
		for (int i = 0; i < panels.length; i++) {
			add(panels[i]);
		}
	}

}
