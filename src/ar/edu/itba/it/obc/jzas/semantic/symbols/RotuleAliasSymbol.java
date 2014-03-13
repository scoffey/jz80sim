package ar.edu.itba.it.obc.jzas.semantic.symbols;

//import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

//import ar.edu.itba.it.obc.jzas.semantic.RotuleAliasRedefinitionException;

public class RotuleAliasSymbol extends Symbol {
	private String alias;
	private int equCount;

	public RotuleAliasSymbol(String rotule, String alias, boolean isEqu) {
		super(rotule);
		this.alias = alias;
		this.equCount = isEqu ? 1 : 0;
	}

	@Override
	public void print(int margin) {
		System.out.printf("%-20s%-20s%-20s\n", "equ", id, alias);
	}

	public String getRotule() {
		return id;
	}

	public String getAlias() {
		return alias;
	}

	public void setSubstitute(String substitute) {
		this.alias = substitute;
	}

	public int getEquCount() {
		return equCount;
	}

	public void incrementEquCount() {
		equCount++;
	}
}
