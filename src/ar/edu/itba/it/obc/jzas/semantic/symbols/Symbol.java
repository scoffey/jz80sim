package ar.edu.itba.it.obc.jzas.semantic.symbols;

/**
 * Un símbolo dentro de la tabla de símbolos. Cada tipo de símbolo extiende de
 * esta clase para agregar los campos y la funcionalidad necesaria.
 */
public abstract class Symbol {
	/* Nombre del símbolo */
	protected String id;

	/*
	 * Determina si el símbolo fue definido en un archivo externo (directiva
	 * extern)
	 */
	private boolean isExternal = false;
	/* Determina si el símbolo es local a una macro (directiva local) */
	private boolean isLocal = false;
	/*
	 * Determina si el símbolo puede ser incluído en otros archivos (directiva
	 * public | global)
	 */
	private boolean isPublic = false;

	public Symbol(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for (byte b : id.getBytes()) {
			hash += b;
		}

		return hash;
	}

	public boolean equals(Symbol obj) {
		return id.equals(obj.id) && this.getClass().equals(obj.getClass());
	}

	public String getId() {
		return id;
	}

	public void setExternal() {
		isExternal = true;
	}

	public boolean isExternal() {
		return isExternal;
	}

	public boolean isLocal() {
		return isLocal;
	}

	public void setLocal() {
		isLocal = true;
	}
	
	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic() {
		isPublic = true;
	}


	public abstract void print(int margin);

	protected String getMargin(int margin) {
		String ret = new String();
		for (int i = 0; i < margin; i++) {
			ret += "  ";
		}
		return ret;
	}
}