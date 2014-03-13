package ar.edu.itba.it.obc.jzas.semantic;

import ar.edu.itba.it.obc.jzas.semantic.SemanticAnalyzer.Segment;
import ar.edu.itba.it.obc.jzas.util.NumberConversionUtil;

/**
 * Clase que representa la ocurrencia de un rótulo que se ha importado
 */
public class ExternalLabelOcurrence implements Comparable<ExternalLabelOcurrence> {

	/* Rótulo externo que se referencia */
	private String label;

	/* Segmento en que se referencia el rótulo externo */
	private Segment segment;

	/* Dirección relativa al segmento en que se referencia el rótulo externo */
	private int address;

	public ExternalLabelOcurrence(String label, Segment segment, int address) {
		this.label = label;
		this.segment = segment;
		this.address = address;
	}

	public String getLabel() {
		return label;
	}

	public Segment getSegment() {
		return segment;
	}

	public int getAddress() {
		return address;
	}

	public String getAddressAsString() {
		return NumberConversionUtil.littleEndianHex(address);
	}

	public String getAddresAndLabel() {
		return NumberConversionUtil.toHex(address, 4) + " " + label;
	}

	//@Override
	public int compareTo(ExternalLabelOcurrence o) {
		if (this.getSegment().equals(o.getSegment())) {
			return (int) Math.signum(this.getAddress() - o.getAddress());
		}
		if (this.getSegment().equals(Segment.ASEG)) {
			return -1;
		} else if (this.getSegment().equals(Segment.CSEG) && o.getSegment().equals(Segment.DSEG)) {
			return -1;
		} else {
			return 1;
		}
	}

}
