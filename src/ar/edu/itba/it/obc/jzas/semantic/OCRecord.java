package ar.edu.itba.it.obc.jzas.semantic;

import ar.edu.itba.it.obc.jzas.util.NumberConversionUtil;

/**
 * Representa una instrucción de código objeto independiente del estandar de
 * codificación.
 */
public class OCRecord {
	private String codification;
	private int address;
	private String rotule;
	

	public OCRecord() {
		codification = "";
	}

	public OCRecord(String codification, int base, int address) {
		if (base == 16) {
			this.codification = NumberConversionUtil.hex2Bin(codification);
		} else if (base == 2) {
			this.codification = codification;
		} else {
			throw new IllegalArgumentException("constructor OCRecord invocado con parametros invalidos");
		}

		this.address = address;
	}

	public void setRotule(String rotule) {
		this.rotule = rotule;
	}

	public int getSize() {
		// TODO: ojo que esto es valido solo si la codification es un unsigned
		// int
		return (codification.length() / 8);

	}

	private void wideCodification(int size) {
		int len = size - this.codification.length();
		for (int i = 0; i < len; i++) {
			this.codification = "0" + this.codification;
		}
	}

	public String getCodification() {
		return codification;
	}

	@Override
	public String toString() {
		return codification;
	}
	
	public int getAddress() {
		return address;
	}
}
