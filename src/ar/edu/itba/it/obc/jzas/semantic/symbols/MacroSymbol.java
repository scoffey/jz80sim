package ar.edu.itba.it.obc.jzas.semantic.symbols;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.it.obc.jzas.semantic.SymbolTable;

/**
 * Símbolo para almacenar una macro en la tabla de símbolos. Contiene el nombre
 * del rótulo en el que se define la macro, la lista de parámetros de entrada y
 * la lista de instrucciones que contiene.
 */
public class MacroSymbol extends Symbol {

	/* Parámetros de entradas */
	private List<String> inputParams;
	
	/* Código interno de la macro */
	private String code;
	
	/*
	 * Lista de tablas de simbolos para cada una de las invocaciones. Es
	 * utilizada como una cola.
	 */
	List<SymbolTable> symbolTables;

	public MacroSymbol(String id, List<String> inputParams, String code) {
		super(id);
		this.inputParams = inputParams;
		this.code = code;
		this.symbolTables = new ArrayList<SymbolTable>();
	}

	public List<String> getInputParams() {
		return inputParams;
	}

	public void setInputParams(List<String> inputParams) {
		this.inputParams = inputParams;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public void print(int margin) {
		System.out.printf("%-20s%-20s\n", "macro", id);
		for (String inputParam : inputParams) {
			System.out.printf("%-20s%-20s%-20s\n", "", "parameter", inputParam);
		}
	}

	/**
	 * Agrega una nueva tabla de símbolos a la cola de tabla de símbolos. Esta
	 * operación es utilizada cuando se encuentra una nueva invocación y se
	 * desea construir la tabla de símbolos de dicha invocación.
	 */
	public SymbolTable beginMacroInvocation(){
		SymbolTable ret = new SymbolTable();
		symbolTables.add(ret);
		return ret;
	}

	/**
	 * Descarta una de las tablas de símbolos dado que finalizo la invocacion de
	 * una macro.
	 */
	public SymbolTable endMacroInvocation() {
		return symbolTables.remove(0);
	}
}
