package ar.edu.itba.it.obc.jzas.semantic;

import java.util.Hashtable;

import ar.edu.itba.it.obc.jzas.semantic.symbols.MacroSymbol;
import ar.edu.itba.it.obc.jzas.semantic.symbols.RotuleAliasSymbol;
import ar.edu.itba.it.obc.jzas.semantic.symbols.RotuleSymbol;
import ar.edu.itba.it.obc.jzas.semantic.symbols.Symbol;

/**
 * Tabla de símbolos. Se maneja como un conjunto de símbolos de un único nivel
 * (contexto). Cuando se agrega una función, se crea una nueva tabla de símbolos
 * dentro de la función, y se setea la variable currentFunction en la tabla
 * actual para saber a donde buscar.
 */
public class SymbolTable {
	private Hashtable<String, Symbol> symbols;

	/**
	 * Crea una nueva tabla de símbolos, de 1 nivel de profundidad (el contexto
	 * global).
	 */
	public SymbolTable() {
		this.symbols = new Hashtable<String, Symbol>();
	}

	public void addSymbol(Symbol s) throws DuplicatedSymbolException {
		if (symbols.containsKey(s.getId())) {
			throw new DuplicatedSymbolException();
		}
		symbols.put(s.getId(), s);
	}

	public MacroSymbol getMacroSymbol(String macroName) {
		Symbol symbol = symbols.get(macroName);
		if (symbol == null || !(symbol instanceof MacroSymbol)) {
			return null;
		} else {
			return (MacroSymbol) symbol;
		}
	}

	public RotuleSymbol getRotuleSymbol(String rotule) {
		Symbol symbol = symbols.get(rotule);
		if (symbol == null || !(symbol instanceof RotuleSymbol)) {
			return null;
		} else {
			return (RotuleSymbol) symbol;
		}
	}

	public RotuleAliasSymbol getRotuleAliasSymbol(String rotule) {
		Symbol symbol = symbols.get(rotule);
		if (symbol == null || !(symbol instanceof RotuleAliasSymbol)) {
			return null;
		} else {
			return (RotuleAliasSymbol) symbol;
		}
	}

	/**
	 * Importa las macros de una tabla de símbolos externa.
	 * 
	 * @param externalSymbolTable
	 *            Tabla de la cual importar los símbolos.
	 */
	public void importFromExternalTable(SymbolTable externalSymbolTable) {
		// TODO: importamos rotulos?
		if (externalSymbolTable == null) {
			return;
		}

		try {
			for (String key : externalSymbolTable.symbols.keySet()) {
				Symbol s = externalSymbolTable.symbols.get(key);
				if (s instanceof MacroSymbol && !s.isExternal()) {
					s.setExternal();
					addSymbol(s);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); // No debe pasar.
		}
	}

	/**
	 * Imprime por salida estándar la tabla de símbolos.
	 */
	public void print(int margin) {
		for (String key : symbols.keySet()) {
			Symbol s = symbols.get(key);
			if (!s.isExternal()) {
				s.print(margin);
			}
		}
	}

	public Symbol getSymbol(String rotule) {
		return symbols.get(rotule);
	}

	public boolean removeSymbol(String rotule) {
		return symbols.remove(rotule) != null;
	}
}
