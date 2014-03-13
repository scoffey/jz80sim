package ar.edu.itba.it.obc.jzas.semantic.symbols;


public class ExternalSymbol extends Symbol {

	public ExternalSymbol(String id) {
		super(id);
	}

	@Override
	public void print(int margin) {
		System.out.printf("%-20s%-20s\n", "extern", id);
	}

}
