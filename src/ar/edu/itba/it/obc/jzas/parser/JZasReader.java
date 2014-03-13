package ar.edu.itba.it.obc.jzas.parser;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Clase que implementa la interfaz Reader de Java. El compilador utiliza este
 * tipo de Readers en vez de FileReaders. Esto permite cambiar el origen de
 * datos mientras se está procesando un archivo. Por ejemplo, cuando se
 * encuentra un ainvocación de una macro se puede cambiar el Reader para que lea
 * el código de la macro almacenado en un String. También este esquema permite
 * cambiar el Reader cuando encuentra una directiva include.
 */
public class JZasReader extends Reader {
	/* Stack de Readers */
	private Stack<Reader> readers;

	/*
	 * Lista de observadores a quienes hay que notificar cuando se acaba un
	 * string
	 */
	private List<Observer> observers;


	public JZasReader(FileReader fr) {
		this.readers = new Stack<Reader>();
		this.readers.push(fr);
		this.observers = new ArrayList<Observer>();
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	public void pushReader(Reader reader) {
		readers.push(reader);
	}

	@Override
	public void close() throws IOException {
		while (!readers.isEmpty()) {
			readers.pop().close();
		}
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int ret;

		/*
		 * Siempre como máximo se lee de a un caracter porque si no no se puede
		 * intercalar las macros con el código
		 */
		len = 1;
		ret = readers.peek().read(cbuf, off, len);
		/*
		 * Si se acabó el Reader actual pasar al anterior o devolver -1 si no
		 * quedaron más.
		 */
		if (ret == -1) {
			Reader popedInstance = readers.pop();
			if (!readers.isEmpty()) {
				notifyObservers(popedInstance);
				ret = read(cbuf, off, len);
			} else {
				ret = -1;
			}

		}
		return ret;
	}

	private void notifyObservers(Reader popedInstance) {
		for (Observer observer : observers) {
			observer.update(popedInstance);
		}
	}
}
