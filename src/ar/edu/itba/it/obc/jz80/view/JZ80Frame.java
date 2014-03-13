package ar.edu.itba.it.obc.jz80.view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import ar.edu.itba.it.obc.jz80.JZ80Sim;
import ar.edu.itba.it.obc.jz80.api.DeviceView;

public class JZ80Frame extends JFrame {

	// Variables de clase

	private static final long serialVersionUID = 1L;

	private static WindowAdapter windowClosingListener = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			JZ80Frame f = (JZ80Frame) e.getSource();
			if (f.showConfirmationMessage("Confirmar salida",
					"¿Está seguro que desea salir?")) {
				f.setVisible(false);
				f.dispose();
			}
		}
	};

	// Variables de instancia

	private JPanel panel;

	private JZ80Sim controller;

	private JZ80MainPane mainPane;

	private JZ80BreakpointsPane breakpointsPane;

	private JZ80DevicesOptionPane devicesOptionPane;

	private JZ80PreferencesOptionPane preferencesOptionPane;

	private JZ80CompilationOptionPane compilationOptionPane;

	public JZ80Frame(JZ80Sim controller) {
		super("JZ80Sim");
		this.controller = controller;
		setJMenuBar(new JZ80MenuBar(controller));
		setContentPane(getJContentPane());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(windowClosingListener);
		setPreferredSize(new Dimension(640, 480));
		setSize(new Dimension(640, 480));
		setVisible(true);
	}

	private JPanel getJContentPane() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridheight = 1;
			panel.add(new JZ80ToolBar(controller), c);
			c.fill = GridBagConstraints.BOTH;
			c.weighty = 1.0;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridheight = GridBagConstraints.REMAINDER;
			panel.add(getMainPane(), c);
		}
		return panel;
	}

	public JZ80MainPane getMainPane() {
		if (mainPane == null) {
			mainPane = new JZ80MainPane(controller);
		}
		return mainPane;
	}

	public JZ80Sim getController() {
		return controller;
	}

	public File chooseFile(final String extensions, final String description) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				if (!f.canRead())
					return false;
				if (extensions == null)
					return true;
				if (f.getName().toLowerCase().endsWith(extensions))
					return true;
				return false;
			}

			@Override
			public String getDescription() {
				return description;
			}

		});
		int ret = chooser.showOpenDialog(this);
		return (ret == JFileChooser.APPROVE_OPTION) ? chooser.getSelectedFile()
				: null;
	}

	public boolean confirmClosing() {
		windowClosingListener.windowClosing(new WindowEvent(this,
				WindowEvent.WINDOW_CLOSING));
		return !isVisible();
	}

	public String prompt(String title, String message) {
		return JOptionPane.showInputDialog(this, message, title,
				JOptionPane.OK_OPTION);
	}

	public boolean showConfirmationMessage(String title, String message) {
		return JOptionPane.showConfirmDialog(this, message, title,
				JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION;
	}

	public void showInformationMessage(String title, String message) {
		JOptionPane.showMessageDialog(this, message, title,
				JOptionPane.INFORMATION_MESSAGE);
	}

	public void showErrorMessage(String title, String message) {
		JOptionPane.showMessageDialog(this, message, title,
				JOptionPane.ERROR_MESSAGE);
	}

	public void showBreakpointsPane() {
		JZ80BreakpointsPane p = getBreakpointsPane();
		JOptionPane.showOptionDialog(this, p, "Breakpoints",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				null, null);
	}

	public void showDevicesOptionPane() {
		JZ80DevicesOptionPane p = getDevicesOptionPane();
		JOptionPane.showOptionDialog(this, p, "Dispositivos de I/O",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				null, null);
	}

	public void showDeviceView(DeviceView deviceView, int port) {
		JDialog dialog = new JDialog(this, false);
		dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		final int p = port;
		dialog.addWindowListener(new WindowAdapter() {
			// @Override
			public void windowClosing(WindowEvent e) {
				controller.disconnectDevice(p);
				super.windowClosing(e);
			}
		});
		dialog.setTitle(deviceView.getDevice().getClass().getSimpleName());
		dialog.setContentPane(deviceView.getView());
		dialog.pack();
		dialog.setLocationByPlatform(true);
		dialog.setVisible(true);
	}

	public void closeDeviceView(DeviceView deviceView, int port) {
		Container targetView = deviceView.getView();
		Window[] ws = getOwnedWindows();
		for (int i = 0; i < ws.length; i++) {
			if (ws[i] instanceof JDialog) {
				JDialog d = (JDialog) ws[i];
				if (d.getContentPane() == targetView) {
					// TODO: repite el código del WindowListener
					controller.disconnectDevice(port);
					d.dispose();
				}
			}
		}
	}

	public void showPreferencesOptionPane() {
		JZ80PreferencesOptionPane p = getPreferencesOptionPane();
		JOptionPane.showOptionDialog(this, p, "Preferencias",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				null, null);
	}

	public void showCompilationOptionPane() {
		JZ80CompilationOptionPane p = getCompilationOptionPane();
		JOptionPane.showOptionDialog(this, p, "Compilación",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				null, null);
	}

	public JZ80BreakpointsPane getBreakpointsPane() {
		if (breakpointsPane == null) {
			breakpointsPane = new JZ80BreakpointsPane(controller);
		}
		return breakpointsPane;
	}

	public JZ80DevicesOptionPane getDevicesOptionPane() {
		if (devicesOptionPane == null) {
			devicesOptionPane = new JZ80DevicesOptionPane(controller);
		}
		return devicesOptionPane;
	}

	public JZ80PreferencesOptionPane getPreferencesOptionPane() {
		if (preferencesOptionPane == null) {
			preferencesOptionPane = new JZ80PreferencesOptionPane(controller);
		}
		return preferencesOptionPane;
	}

	public JZ80CompilationOptionPane getCompilationOptionPane() {
		if (compilationOptionPane == null) {
			compilationOptionPane = new JZ80CompilationOptionPane(controller);
		}
		return compilationOptionPane;
	}

	public void setLookAndFeel(String classname) {
		try {
			UIManager.setLookAndFeel(classname);
		} catch (Exception e) {
			controller.raiseException(e);
			return;
		}
		// Actualizar todo el arbol de componentes graficos
		reset();
		// Fix para repintar el dialog abierto actualmente (medio trucho)
		Container c = getPreferencesOptionPane();
		while (!(c instanceof JOptionPane))
			c = c.getParent();
		JDialog d = (JDialog) (((JOptionPane) c).getRootPane().getParent());
		SwingUtilities.updateComponentTreeUI(d);
		d.pack();
	}

	public void reset() {
		SwingUtilities.updateComponentTreeUI(this);
		SwingUtilities.updateComponentTreeUI(getBreakpointsPane());
		SwingUtilities.updateComponentTreeUI(getDevicesOptionPane());
		SwingUtilities.updateComponentTreeUI(getPreferencesOptionPane());
		pack();
	}

}
