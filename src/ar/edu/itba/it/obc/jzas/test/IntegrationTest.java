package ar.edu.itba.it.obc.jzas.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

import ar.edu.itba.it.obc.jz80.api.Instruction;
import ar.edu.itba.it.obc.jz80.instructions.JZ80InvalidInstructionException;
import ar.edu.itba.it.obc.jz80.system.JZ80MemoryLoader;
import ar.edu.itba.it.obc.jz80.system.JZ80System;
import ar.edu.itba.it.obc.jzas.run.Compiler;
import ar.edu.itba.it.obc.jzas.run.Compiler.CompilerModes;
import ar.edu.itba.it.obc.jzas.util.ConsoleProcessLogger;

/**
 * Test de integración entre el compilador y simulador. Para cada test de
 * integración, se compila la decodificación del hexfile generado al compilar
 * con las instrucciones que se esperaría que hubieran. Para ello se deben
 * definir archivos successXX.asm en el directorio "test/jzas/integration" junto
 * con la salida esperada successXX.exp. Entre cada línea del archivo de salida
 * esperada se puede intercalar un '#'entero (que se interpreta como hexa) para
 * mover el PC a dicha posicion (de lo contrario se asume que las instrucciones
 * se encuentran consecutivas comenzando desde la dirección 0 de memoria). TODO:
 * Parametrizar el comienzo de los segmentos de código y datos (ahora cuando se
 * linkedita todos comienzan en 0).
 */
public class IntegrationTest {

	/**
	 * Punto de entrada del test de integración.
	 */
	public static void main(String[] args) {

		/* Obtener todos los archivos .asm que se quieren testear */
		String dirname = "test/jzas/integration";
		File dir = new File(dirname);
		FilenameFilter filter = new FilenameFilter() {
			// @Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".asm") && name.startsWith("success");
			}
		};
		String[] tests = dir.list(filter);
		Arrays.sort(tests);

		for (String test : tests) {
			String currentFile = dir + "/" + test;
			System.out.println("Testing " + currentFile + "...");

			/*
			 * Obtener la direcccion donde debe comenzar el segmento de código y
			 * el de datos
			 */
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(dir + "/"
						+ test.replace(".asm", ".exp")));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			int csegStart = 0, dsegStart = 0;

			for (int i = 0; i < 2; i++) {
				String line = null;
				;
				try {
					line = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (line != null) {
					if (line.startsWith("cseg: ")) {
						csegStart = Integer.parseInt(line.substring(6), 16);
					} else if (line.startsWith("dseg: ")) {
						dsegStart = Integer.parseInt(line.substring(6), 16);
					}
				}
			}


			/* Compilar el test */
			if (!compile(dir + "/" + test, csegStart, dsegStart)) {
				System.err.println(currentFile + ": Compilation error");
				continue;
			}

			/*
			 * Comparar las instrucciones que levanta el simulador con las
			 * esperadas
			 */
			testFile(dir + "/" + test.replace(".asm", ".hex"), dir + "/"
					+ test.replace(".asm", ".exp"));

			/* Borrar los archivos generados */
			new File(dir + "/" + test.replace(".asm", ".obj")).delete();
			new File(dir + "/" + test.replace(".asm", ".hex")).delete();

		}
	}

	/**
	 * Compara la decodificación de un hexfile que realiza el simulador con la
	 * salida esperada informando todas las discrepancias.
	 * 
	 * @param hexFile
	 *            Archivo hex ejecutable
	 * @param expectedFile
	 *            Archivo con las instrucciones que se espera que el hex tenga
	 *            (en el orden especificado también por este archivo).
	 */
	private static void testFile(String hexFile, String expectedFile) {
		JZ80System system = new JZ80System();
		JZ80MemoryLoader memoryLoader = new JZ80MemoryLoader(system);
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(expectedFile));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		/* Cargar el hex */
		try {
			memoryLoader.loadHexFile(new File(hexFile));
		} catch (IOException e) {
			e.printStackTrace();
		}

		int address = 0;
		String line = null;
		int lineNumber = 1;
		try {
			while ((line = br.readLine()) != null) {
				if (line.charAt(0) == ';' || line.startsWith("cseg: ")
						|| line.startsWith("dseg: ")) {
					lineNumber++;
					continue;
				} else if (line.charAt(0) == '#') {
					try {
						/* Cambio de direccion de memoria actual */
						address = Integer.parseInt(line.substring(1), 16);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				} else {
					/* Instruccion */
					Instruction instr = null;
					try {
						instr = system.fetchInstructionAt(address);
					} catch (JZ80InvalidInstructionException e1) {
						System.err.println("INVALID instruction at address "
								+ address);
					}
					if (!instr.toString().equalsIgnoreCase(line)) {
						System.err.println(expectedFile + ": FAILED on line "
								+ lineNumber + ". Found \"" + line
								+ "\", expecting \"" + instr.toString()
								+ "\". Current address " + address);
						System.exit(0);
					}
					// System.out.println("size de la instruccion: " +
					// instr.getByteSize());
					address += instr.getByteSize();
					lineNumber++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Compila un archivo .asm
	 * 
	 * @param test
	 *            Nombre del archivo que se quiere compilar
	 * @return Valor booleano que indica si pudo generar el archivo hex
	 *         ejecutable
	 */
	private static boolean compile(String test, int csegStart, int dsegStart) {
		Compiler compiler = Compiler.getInstance();
		compiler.setLogger(new ConsoleProcessLogger());

		compiler.setMode(CompilerModes.MODE_LINK);
		Compiler.setCsegStart(csegStart);
		Compiler.setDsegStart(dsegStart);
		return compiler.compileFile(Arrays.asList(new String[] { test }), 0, 0) == 0;
	}
}
