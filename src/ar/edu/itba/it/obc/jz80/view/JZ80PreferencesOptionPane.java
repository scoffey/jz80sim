package ar.edu.itba.it.obc.jz80.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ar.edu.itba.it.obc.jz80.JZ80Sim;

public class JZ80PreferencesOptionPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final LookAndFeelInfo[] lookAndFeels = UIManager
			.getInstalledLookAndFeels();

	public JZ80Sim controller;

	public JZ80PreferencesOptionPane(JZ80Sim controller) {
		this.controller = controller;
		setLayout(new GridBagLayout());
		addOptionRow(0, "Velocidad de ejecución:", getExecutionSlider());
		addOptionRow(1, "Estilo de interfaz gráfica:", getLookAndFeelComboBox());
	}
	
	private void addOptionRow(int i, String name, JComponent options) {
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(4, 4, 4, 4);
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = i;
		c.gridwidth = 1;
		c.weighty = 1.0;
		add(new JLabel(name), c);
		c.gridx = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		add(options, c);
	}

	private JSlider getExecutionSlider() {
		// slider
		int millis = controller.getExecutionInterval();
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 20, 20 - millis / 50);
		slider.setMinorTickSpacing(1);
		slider.setMajorTickSpacing(5);
		slider.setPaintTicks(true);
		slider.setSnapToTicks(true);
		// labels
		Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
		labels.put(0, new JLabel("Lenta"));
		labels.put(10, new JLabel("Media"));
		labels.put(20, new JLabel("Rápida"));
		slider.setLabelTable(labels);
		slider.setPaintLabels(true);
		// change listener
		slider.addChangeListener(getChangeListener());
		return slider;
	}

	private ChangeListener getChangeListener() {
		return new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider slider = (JSlider) e.getSource();
				int millis = (20 - slider.getValue()) * 50;
				controller.setExecutionInterval(millis);
			}
		};
	}

	private JComboBox getLookAndFeelComboBox() {
		// combobox
		String[] names = new String[lookAndFeels.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = lookAndFeels[i].getName();
		}
		JComboBox combobox = new JComboBox(names);
		// default selection
		String laf = UIManager.getLookAndFeel().getClass().getName();
		for (int i = 0; i < names.length; i++) {
			if (lookAndFeels[i].getClassName().equals(laf)) {
				combobox.setSelectedIndex(i);
			}
		}
		// selection listener
		combobox.addActionListener(getSelectionListener());
		return combobox;
	}

	private ActionListener getSelectionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox combobox = (JComboBox) e.getSource();
				String classname = lookAndFeels[combobox.getSelectedIndex()]
						.getClassName();
				controller.getView().setLookAndFeel(classname);
			}
		};
	}

}
