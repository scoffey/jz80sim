package ar.edu.itba.it.obc.jz80.view;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;

import ar.edu.itba.it.obc.jz80.JZ80Sim;

public class JZ80MenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JZ80Sim sim;

	private JMenu[] menus;

	public JZ80MenuBar(JZ80Sim s) {
		super();
		sim = s;
		menus = new JMenu[5];
		add(getFileJMenu());
		add(getEditJMenu());
		add(getViewJMenu());
		add(getOptionsJMenu());
		add(getHelpJMenu());
	}

	private JMenu getFileJMenu() {
		if (menus[0] == null) {
			menus[0] = new JMenu("Archivo");
			menus[0].setMnemonic(KeyEvent.VK_A);
			menus[0]
					.add(getMenuItem("Abrir...", "openFile", KeyEvent.VK_A, 'O'));
			menus[0].add(getMenuItem("Recargar", "reopenFile", KeyEvent.VK_R,
					'R'));
			menus[0]
					.add(getMenuItem("Compilar", "compile", KeyEvent.VK_C, 'M'));
			menus[0].add(getMenuItem("Salir", "quit", KeyEvent.VK_S, 'Q'));
		}
		return menus[0];
	}

	private JMenu getEditJMenu() {
		if (menus[1] == null) {
			menus[1] = new JMenu("Edición");
			menus[1].setMnemonic(KeyEvent.VK_E);
			JMenu execute = new JMenu("Ejecutar");
			execute.setMnemonic(KeyEvent.VK_E);
			execute.add(getMenuItem("Instrucción", "executeInstruction",
					KeyEvent.VK_I, KeyEvent.VK_F5));
			execute.add(getMenuItem("Subrutina", "executeSubroutine",
					KeyEvent.VK_S, KeyEvent.VK_F6));
			execute.add(getMenuItem("Programa", "executeProgram",
					KeyEvent.VK_P, KeyEvent.VK_F7));
			execute.add(getMenuItem("Detener", "stopExecution", KeyEvent.VK_D,
					KeyEvent.VK_F8));
			menus[1].add(execute);
			JMenu reset = new JMenu("Resetear");
			reset.setMnemonic(KeyEvent.VK_R);
			reset.add(getMenuItem("Registros de la CPU", "resetCPU",
					KeyEvent.VK_P, KeyEvent.VK_F9));
			reset.add(getMenuItem("Memoria", "resetMemory", KeyEvent.VK_M,
					KeyEvent.VK_F10));
			reset.add(getMenuItem("Dispositivos de I/O", "resetDevices",
					KeyEvent.VK_D, KeyEvent.VK_F11));
			reset.add(getMenuItem("Reiniciar todo", "resetAll", KeyEvent.VK_A,
					KeyEvent.VK_F12));
			menus[1].add(reset);
		}
		return menus[1];
	}

	private JMenu getViewJMenu() {
		if (menus[2] == null) {
			menus[2] = new JMenu("Ver");
			menus[2].setMnemonic(KeyEvent.VK_V);
			JMenu instructions = new JMenu("Instrucciones");
			instructions.setMnemonic(KeyEvent.VK_I);
			instructions.add(getMenuItem("Ir a dirección actual",
					"gotoCurrentInstruction", KeyEvent.VK_A, '\0'));
			instructions.add(getMenuItem("Ir a dirección...",
					"gotoInstruction", KeyEvent.VK_D, '\0'));
			menus[2].add(instructions);
			JMenu stack = new JMenu("Pila");
			stack.setMnemonic(KeyEvent.VK_P);
			stack.add(getMenuItem("Ir a dirección actual",
					"gotoCurrentStackAddress", KeyEvent.VK_A, '\0'));
			stack.add(getMenuItem("Ir a dirección...", "gotoStackAddress",
					KeyEvent.VK_D, '\0'));
			menus[2].add(stack);
		}
		return menus[2];
	}

	private JMenu getOptionsJMenu() {
		if (menus[3] == null) {
			menus[3] = new JMenu("Opciones");
			menus[3].setMnemonic(KeyEvent.VK_O);
			menus[3].add(getMenuItem("Compilación...",
					"configureCompilationOptions", KeyEvent.VK_C, 'L'));
			menus[3].add(getMenuItem("Dispositivos de I/O...",
					"configureDevices", KeyEvent.VK_D, 'D'));
			menus[3].add(getMenuItem("Breakpoints...", "configureBreakpoints",
					KeyEvent.VK_B, 'B'));
			menus[3].add(getMenuItem("Preferencias...", "configurePreferences",
					KeyEvent.VK_P, 'P'));
		}
		return menus[3];
	}

	private JMenu getHelpJMenu() {
		if (menus[4] == null) {
			menus[4] = new JMenu("Ayuda");
			menus[4].setMnemonic(KeyEvent.VK_Y);
			menus[4].add(getMenuItem("Documentación", "openHelpTopics",
					KeyEvent.VK_D, KeyEvent.VK_F1));
			menus[4].add(getMenuItem("Acerca de JZ80Sim...",
					"popupAboutDialog", KeyEvent.VK_A, '\0'));
		}
		return menus[4];
	}

	private JMenuItem getMenuItem(String label, String action, int mnemonic,
			char accel) {
		JMenuItem item = getMenuItemWrapped(label, action, mnemonic);
		if (accel != '\0') {
			int mask = System.getProperty("os.name").startsWith("Mac OS") ? ActionEvent.META_MASK
					: ActionEvent.CTRL_MASK;
			item.setAccelerator(KeyStroke.getKeyStroke(accel, mask));
		}
		return item;
	}

	private JMenuItem getMenuItem(String label, String action, int mnemonic,
			int accel) {
		JMenuItem item = getMenuItemWrapped(label, action, mnemonic);
		item.setAccelerator(KeyStroke.getKeyStroke(accel, 0));
		return item;
	}

	private JMenuItem getMenuItemWrapped(String label, String action,
			int mnemonic) {
		JMenuItem item = new JMenuItem(label, mnemonic);
		item.addActionListener(new JZ80ActionListener(sim, action));
		return item;
	}

}
