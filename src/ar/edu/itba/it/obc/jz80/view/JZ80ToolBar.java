package ar.edu.itba.it.obc.jz80.view;

import java.net.URL;

import javax.swing.*;

import ar.edu.itba.it.obc.jz80.JZ80Sim;

public class JZ80ToolBar extends JToolBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JButton[] buttons;

	private JZ80Sim sim;

	public JZ80ToolBar(JZ80Sim s) {
		super();
		sim = s;
		buttons = new JButton[10];
		buttons[0] = getButton("Abrir...", "document-open", "openFile");
		buttons[1] = getButton("Recargar", "view-refresh", "reopenFile");
		buttons[2] = getButton("Ejecutar instrucción", "go-next",
				"executeInstruction");
		buttons[3] = getButton("Ejecutar subrutina", "go-jump",
				"executeSubroutine");
		buttons[4] = getButton("Ejecutar programa", "go-last", "executeProgram");
		buttons[5] = getButton("Detener ejecución", "process-stop",
				"stopExecution");
		buttons[6] = getButton("Dispositivos de I/O...", "input-keyboard",
				"configureDevices");
		buttons[7] = getButton("Breakpoints...", "dialog-error",
				"configureBreakpoints");
		buttons[8] = getButton("Preferencias...", "preferences-system",
				"configurePreferences");
		buttons[9] = getButton("Ayuda", "help-browser", "openHelpTopics");
		for (int i = 0; i < buttons.length; i++) {
			add(buttons[i]);
		}
		setFloatable(false);
	}

	private JButton getButton(String label, String icon, String action) {
		JButton button = new JButton();
		button.setToolTipText(label);
		URL url = getClass().getResource("/resources/img/" + icon + ".png");
		ImageIcon img = new ImageIcon(url);
		button.setIcon(img);
		button.addActionListener(new JZ80ActionListener(sim, action));
		return button;
	}

}
