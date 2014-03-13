package ar.edu.itba.it.obc.jzas.semantic.symbols;

import ar.edu.itba.it.obc.jzas.semantic.SemanticAnalyzer.Segment;

public class RotuleSymbol extends Symbol {
	private int address;
	private Segment segment;

	public RotuleSymbol(String id, int address, Segment segment) {
		super(id);
		this.address = address;
		this.segment = segment;
	}

	@Override
	public void print(int margin) {
		System.out.printf("%-20s%-20s\n", "rotule", id);
	}

	public int getAddress() {
		return address;
	}
	
	public Segment getSegment() {
		return segment;
	}
}
