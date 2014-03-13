package ar.edu.itba.it.obc.jz80;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import ar.edu.itba.it.obc.jz80.api.DeviceView;
import ar.edu.itba.it.obc.jz80.api.Device;
import ar.edu.itba.it.obc.jz80.api.Instruction;
import ar.edu.itba.it.obc.jz80.api.InstructionException;
import ar.edu.itba.it.obc.jz80.devices.JZ80KeyboardView;
import ar.edu.itba.it.obc.jz80.devices.JZ80LedArrayView;
import ar.edu.itba.it.obc.jz80.devices.JZ80TerminalView;
import ar.edu.itba.it.obc.jz80.instructions.JZ80InvalidInstructionException;
import ar.edu.itba.it.obc.jz80.system.JZ80System;
import ar.edu.itba.it.obc.jz80.view.JZ80Frame;

public class JZ80Sim {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private File lastOpenedFile = null;

	private Thread execution = null;

	private int executionStep = 100;

	private JZ80System system = null;

	private JZ80Frame view = null;

	private HashMap<Integer, DeviceView> deviceViews = null;

	private TreeSet<Integer> breakpoints = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JZ80Sim sim = new JZ80Sim();

		// TODO: Check file extension
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i] != null && args[i].compareTo("") != 0) {
					sim.openFile(new File(args[i]));
				}
			}
		}

	}

	/**
	 * This is the JZ80Sim default constructor
	 */
	public JZ80Sim() {
		resetControllerParameters();
		system = new JZ80System();
		view = new JZ80Frame(this);
		deviceViews = new HashMap<Integer, DeviceView>();
	}

	public JZ80System getSystem() {
		return system;
	}

	public JZ80Frame getView() {
		return view;
	}

	public int getExecutionInterval() {
		return executionStep;
	}

	public void setExecutionInterval(int milliseconds) {
		executionStep = milliseconds;
	}

	public Integer[] getBreakpoints() {
		return breakpoints.toArray(new Integer[breakpoints.size()]);
	}

	public void addBreakpoint(Integer address) {
		breakpoints.add(address);
	}

	public void removeBreakpoint(Integer address) {
		breakpoints.remove(address);
	}

	public void executeInstruction() {
		stopExecution();
		executeNext();
	}

	public void executeSubroutine() {
		doExecutionLoop(0);
	}

	public void executeProgram() {
		doExecutionLoop(1);
	}

	private void doExecutionLoop(int haltingMask) {
		stopExecution();
		final int mask = ~haltingMask;
		execution = new Thread(new Runnable() {
			public void run() {
				Instruction instruction = null;
				do {
					instruction = executeNext();
					try {
						Thread.sleep(executionStep);
					} catch (InterruptedException e) {
						return;
					}
				} while ((getHaltingFlags(instruction) & mask) == 0);
			}
		});
		if (executionStep < 50)
			setListenersEnabled(false);
		execution.start();
	}

	private Instruction executeNext() {
		try {
			return getSystem().executeNext();
		} catch (JZ80InvalidInstructionException e) {
			displayErrorMessage("El código de instrucción "
					+ String.format("%04X", e.getInvalidInstructionCode())
					+ " no es válido o no fue \n"
					+ "implementado aún. Dado que se desconoce su largo, puede ser \n"
					+ "que las instrucciones subsiguientes no sean correctamente \n"
					+ "decodificadas.");
		} catch (InstructionException e) {
			displayErrorMessage("Instrucción inválida.");
		}
		return null;
	}

	public void stopExecution() {
		if (execution != null) {
			execution.interrupt();
			execution = null;
			setListenersEnabled(true); // TODO: Deberían activarse sólo si es
			// necesario
		}
	}

	public int getHaltingFlags(Instruction instruction) {
		int retval = 0;
		if (instruction == null) {
			retval |= 16;
		} else {
			String s = instruction.getMnemonic();
			if (s.equals("halt")) {
				retval |= 4;
			} else if (s.equals("rst")) {
				retval |= 2;
			} else if (s.equals("ret")) {
				retval |= 1;
			}
		}
		if (breakpoints.contains(Integer.valueOf(system.readProgramCounter()))) {
			retval |= 8;
		}
		return retval;
	}

	public void openFile() {
		File f = view.chooseFile(".hex", "Archivos de código binario (*.hex)");
		if (f != null) {
			openFile(f);
		}
	}

	public void reopenFile() {
		if (lastOpenedFile != null) {
			openFile(lastOpenedFile);
		}
	}

	private void openFile(File f) {
		if (!f.exists()) {
			displayErrorMessage("No existe el archivo \n" + f.getName());
			return;
		} else if (!f.canRead()) {
			displayErrorMessage("No se puede leer el archivo \n" + f.getName());
			return;
		}
		try {
			setListenersEnabled(false);
			system.loadFileToMemory(f);
			lastOpenedFile = f;
		} catch (IOException e) {
			displayErrorMessage("No se puede leer el archivo \n" + f.getName());
		} catch (Exception e) {
			e.printStackTrace();
			displayErrorMessage("El formato del archivo " + f.getName()
					+ " \nno es reconocido.");
		} finally {
			setListenersEnabled(true);
		}
	}

	public void configureCompilationOptions() {
		view.showCompilationOptionPane();
	}

	public void compile() {
		File asm;
		String msg = "El archivo fuente del programa principal a compilar \n"
				+ "no fue especificado. \n"
				+ "¿Desea configurar las opciones de compilación?";
		while ((asm = view.getCompilationOptionPane()
				.getMainProgramSourceFile()) == null) {
			if (view.showConfirmationMessage("Archivo no especificado", msg)) {
				configureCompilationOptions();
			} else {
				return;
			}
		}
		compile(asm);
	}

	public void compile(File asm) {
		String[] commands = view.getCompilationOptionPane()
				.getCompilationCommands();
		File path = asm.getParentFile();
		String name = asm.getName();
		int index;
		if ((index = name.indexOf('.')) >= 0) {
			name = name.substring(0, index);
		}
		String output = "";
		for (String i : commands) {
			i = i.trim();
			if (i.length() == 0)
				continue;
			try {
				output += runCommand(i.replaceAll("%s", name), path);
			} catch (IOException e) {
				raiseException(e);
				return;
			}
		}
		view.showInformationMessage("Salida de la compilación", output);
		File newHex = new File(path, name + ".hex");
		if (newHex.exists()) {
			openFile(newHex);
		}
	}

	public void resetCPU() {
		setListenersEnabled(false);
		system.getCPU().reset();
		setListenersEnabled(true);
	}

	public void resetMemory() {
		setListenersEnabled(false);
		system.getMemory().reset();
		setListenersEnabled(true);
	}

	public void resetDevices() {
		setListenersEnabled(false);
		system.resetDevices();
		setListenersEnabled(true);
	}

	public void resetAll() {
		File backup = lastOpenedFile;
		setListenersEnabled(false);
		resetControllerParameters();
		system.reset();
		view.reset();
		setListenersEnabled(true);
		lastOpenedFile = backup;
		reopenFile();
	}

	public void setListenersEnabled(boolean enabled) {
		system.getCPU().setListenersEnabled(enabled);
		system.getMemory().setListenersEnabled(enabled);
		if (enabled) {
			system.getCPU().triggerAllListeners();
			system.getMemory().triggerAllListenersAt(
					system.readProgramCounter());
			system.getMemory().triggerAllListenersAt(system.readStackPointer());
		}
	}

	private void resetControllerParameters() {
		lastOpenedFile = null;
		execution = null;
		executionStep = 100;
		breakpoints = new TreeSet<Integer>();
	}

	public void gotoCurrentInstruction() {
		view.getMainPane().getInstructionList().setSelectedInstructionAt(
				system.readProgramCounter());
	}

	public void gotoCurrentStackAddress() {
		view.getMainPane().getStackList().setSelectedWordAt(
				system.readStackPointer());
	}

	public void gotoInstruction() {
		Integer address = promptForAddress(
				"Vista de la lista de instrucciones", "Ir a dirección: ");
		if (address != null) {
			view.getMainPane().getInstructionList().setSelectedInstructionAt(
					address.intValue());
		}
	}

	public void gotoStackAddress() {
		Integer address = promptForAddress("Vista de la pila",
				"Ir a dirección: ");
		if (address != null) {
			view.getMainPane().getStackList().setSelectedWordAt(
					address.intValue());
		}
	}

	private Integer promptForAddress(String title, String message) {
		int address = 0;
		boolean addressIsOk = false;
		String invalidAddrErrMsg = "El valor ingresado no es una dirección válida \n"
				+ "en formato hexadecimal abreviado.";
		String outOfRangeErrMsg = "La dirección ingresada está fuera de rango \n"
				+ "en formato hexadecimal abreviado.";
		while (!addressIsOk) {
			String s = view.prompt(title, message);
			if (s == null) {
				return null;
			}
			try {
				address = Integer.parseInt(s, 16);
				if (!(address >= 0 && address < 0x10000)) {
					view.showErrorMessage("Dirección fuera de rango",
							outOfRangeErrMsg);
				} else {
					addressIsOk = true;
				}
			} catch (NumberFormatException e) {
				view.showErrorMessage("Dirección inválida", invalidAddrErrMsg);
			}
		}
		return Integer.valueOf(address);
	}

	public void quit() {
		if (view.confirmClosing()) {
			System.exit(0);
		}
	}

	public void popupAboutDialog() {
		final String s = "JZ80Sim (versión 0.1.1.61)\n"
				+ "Simulador del microprocesador ZiLOG™ Z80®\n"
				+ "Desarrollado con fines académicos\n\n"
				+ "Autor:\n   Santiago Andrés Coffey <scoffey@alu.itba.edu.ar>\n"
				+ "Con la colaboración de:\n"
				+ "   Carlos Julián Sánchez Romero <casanche@alu.itba.edu.ar>\n"
				+ "   Rafael Martín Bigio <rbigio@alu.itba.edu.ar>\n"
				+ "   Ing. Eduardo A. Martínez <eam@itba.edu.ar>\n\n"
				+ "© 2008 Departamento de Informática\n"
				+ "ITBA (http://www.itba.edu.ar)\n ";
		view.showInformationMessage("Acerca de JZ80Sim", s);
	}

	public void configureBreakpoints() {
		view.showBreakpointsPane();
	}

	public void configureDevices() {
		view.showDevicesOptionPane();
		// Dispositivos que se pueden implementar:
		// Terminal gráfica con profundidad de colores variable
		// Clock tipo RTC (implementar fecha y hora completas)
		// Sonido? Joystick? Red (puerto serie)? Disco?
	}

	public void connectDevice(int port, DeviceView deviceView) {
		Device device = deviceView.getDevice();
		int lastPort = port + device.getSize() - 1;
		if (!system.hasAvailablePortRange(port, lastPort)) {
			String s = "Ya existen dispositivos conectados en ";
			if (port == lastPort) {
				s += String.format("el puerto %02X.\n", port);
			} else {
				s += String
						.format("los puertos %02X a %02X.\n", port, lastPort);
			}
			if (!view.showConfirmationMessage("Conflicto de conexión "
					+ "de dispositivos", s + "¿Desea desconectarlos "
					+ "para conectar el nuevo dispositivo?\n ")) {
				return;
			}
			for (int i = port; i <= lastPort; i++) {
				disconnectDevice(i);
			}
		}
		deviceViews.put(Integer.valueOf(port), deviceView);
		view.showDeviceView(deviceView, port);
		system.connectMultiplePortDevice(port, lastPort, device);
	}

	public void disconnectDevice(int port) {
		DeviceView deviceView = deviceViews.remove(Integer.valueOf(port));
		if (deviceView == null)
			return;
		view.closeDeviceView(deviceView, port);
		system.disconnectMultiplePortDevice(port);
	}

	public HashMap<Integer, DeviceView> getConnectedDevices() {
		return deviceViews;
	}

	public ArrayList<Object[]> getAvailableDevices() {
		ArrayList<Object[]> available = new ArrayList<Object[]>();
		available.add(new Object[] { Integer.valueOf(0x84),
				"Terminal de caracteres", JZ80TerminalView.class });
		available.add(new Object[] { Integer.valueOf(0x80), "Teclado",
				JZ80KeyboardView.class });
		available.add(new Object[] { Integer.valueOf(0x88), "Arreglo de LEDs",
				JZ80LedArrayView.class });
		return available;
	}

	public void configurePreferences() {
		view.showPreferencesOptionPane();
	}

	public void openHelpTopics() {
		if (view.showConfirmationMessage("Documentación no disponible",
				"La documentación de JZ80Sim no está disponible \n"
						+ "en esta versión.\n\n"
						+ "¿Desea consultar \"Z80 Family CPU User Manual\" \n"
						+ "en http://www.zilog.com?\n")) {
			openURL("http://www.zilog.com/docs/z80/um0080.pdf");
		}
	}

	public void openURL(String url) {
		// TODO: Este método debería reemplazarse por java.awt.Desktop.open del
		// JDK 1.6+. (La idea era usarlo para ver un documento HTML o PDF de
		// documentación o ayuda.)
		String osName = System.getProperty("os.name");
		Runtime r = Runtime.getRuntime();
		try {
			if (osName.startsWith("Mac OS")) {
				Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL",
						new Class[] { String.class });
				openURL.invoke(null, new Object[] { url });
			} else if (osName.startsWith("Windows")) {
				r.exec("rundll32 url.dll,FileProtocolHandler " + url);
			} else { // assume Unix or Linux
				String[] browsers = { "firefox", "opera", "konqueror",
						"epiphany", "mozilla", "netscape" };
				String browser = null;
				for (int i = 0; i < browsers.length; i++) {
					if (r.exec(new String[] { "which", browsers[i] }).waitFor() == 0) {
						browser = browsers[i];
						break;
					}
				}
				if (browser == null) {
					throw new FileNotFoundException();
				}
				r.exec(new String[] { browser, url });
			}
		} catch (Exception e) {
			displayErrorMessage("No se pudo abrir la documentación en un navegador web.");
		}
	}

	public String runCommand(String command, File path) throws IOException {
		Runtime r = Runtime.getRuntime();
		Process p = r.exec(command, null, path);
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStream stdout = p.getInputStream();
		InputStream stderr = p.getErrorStream();
		String retval = streamToString(stdout) + "\n";
		retval += streamToString(stderr);
		stdout.close();
		stderr.close();
		return retval;
	}

	private String streamToString(InputStream in) throws IOException {
		byte[] buffer = new byte[1024]; // Buffer 1K at a time
		int bytesRead;
		ByteArrayOutputStream dst = new ByteArrayOutputStream();
		while ((bytesRead = in.read(buffer)) >= 0) {
			dst.write(buffer, 0, bytesRead);
		}
		return dst.toString();
	}

	public void displayErrorMessage(String message) {
		view.showErrorMessage("Error", message);
	}

	public void raiseException(Throwable e) {
		String description = e.getMessage();
		if (description == null || description.equals("")) {
			description = "Se ha producido un error en el programa.";
		}
		// Stack trace
		StackTraceElement[] st = e.getStackTrace();
		StringBuffer s = new StringBuffer("La excepción capturada fue: "
				+ e.toString() + "\n");
		for (int i = 0; i < st.length; i++) {
			if (st[i].isNativeMethod()) {
				break;
			}
			s.append("\t\ten " + st[i].toString() + "\n");
		}
		displayErrorMessage(description + "\n\n" + s.toString());
	}

}
