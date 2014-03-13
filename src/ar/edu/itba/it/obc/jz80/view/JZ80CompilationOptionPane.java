package ar.edu.itba.it.obc.jz80.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ar.edu.itba.it.obc.jz80.JZ80Sim;

public class JZ80CompilationOptionPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String defaultCommand = "zas -l%s.lst -w132 -%s.obj %s.asm\n"
			+ "zlink -o%s.cpm %s.obj\n"
			+ "objtohex %s.cpm %s.hex\n";

	private JZ80Sim controller;

	private JTextField mainProgramSourceFile;

	private JTextArea compilationCommands;

	public JZ80CompilationOptionPane(JZ80Sim sim) {
		super();
		controller = sim;
		setLayout(new BorderLayout(4, 4));
		add(createTopPanel(), BorderLayout.NORTH);
		add(createTextArea(), BorderLayout.CENTER);
		setVisible(true);
	}

	private JPanel createTopPanel() {
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout(4, 4));
		topPanel.add(new JLabel("Archivo fuente del programa principal: "),
				BorderLayout.NORTH);
		mainProgramSourceFile = new JTextField();
		topPanel.add(mainProgramSourceFile, BorderLayout.CENTER);
		JButton b = new JButton("Examinar...");
		b.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				File f = controller.getView().chooseFile(".asm",
						"Archivos fuente de Assembler (*.asm)");
				if (f != null) {
					mainProgramSourceFile.setText(f.getAbsolutePath());
				}

			}
		});
		topPanel.add(b, BorderLayout.EAST);
		topPanel.add(new JLabel("Comandos a ejecutar para compilar: "),
				BorderLayout.SOUTH);
		return topPanel;
	}

	private JScrollPane createTextArea() {
		compilationCommands = new JTextArea(defaultCommand, 5, 40);
		return new JScrollPane(compilationCommands);
	}

	public File getMainProgramSourceFile() {
		File f = new File(mainProgramSourceFile.getText());
		return f.exists() ? f : null;
	}

	public String[] getCompilationCommands() {
		return compilationCommands.getText().split("\n");
	}

}
