package ar.edu.itba.it.obc.jzas.semantic.nodes;

import ar.edu.itba.it.obc.jzas.semantic.symbols.Symbol;

public class IntExpression {
	Integer data;

	Symbol symbol;

	public IntExpression(Integer data, Symbol symbol) {
		this.data = data;
		this.symbol = symbol;
	}

	public IntExpression(Symbol symbol) {
		this(0, symbol);
	}

	public IntExpression(Integer data) {
		this(data, null);
	}

	public IntExpression(IntExpression intExpression) {
		this(intExpression.data, intExpression.symbol);
	}

	public IntExpression() {
		this(0, null);
	}

	public Integer getData() {
		return data;
	}

	public void setData(Integer data) {
		this.data = data;
	}

	public Symbol getSymbol() {
		return symbol;
	}
}
