package ar.edu.itba.it.obc.jzas.semantic;

/**
 * Excepción que se lanza cuando se intenta agregar un símbolo al contexto
 * actual de la tabla de símbolos, y ya existe otro con el mismo nombre y del
 * mismo tipo.
 */
public class DuplicatedSymbolException extends Exception {
	private static final long serialVersionUID = 1L;

}
