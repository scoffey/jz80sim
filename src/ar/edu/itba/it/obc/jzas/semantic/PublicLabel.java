package ar.edu.itba.it.obc.jzas.semantic;

import ar.edu.itba.it.obc.jzas.semantic.SemanticAnalyzer.Segment;
import ar.edu.itba.it.obc.jzas.util.NumberConversionUtil;

/**
 * Clase que representa un rotulo que se desea exportar
 */
public class PublicLabel implements Comparable<PublicLabel> {
	private String label;
	private Segment segment;
	private Integer address;

	public PublicLabel(String label, Segment segment, Integer address) {
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

	public Integer getAddress() {
		return address;
	}

	public String getAddresAndLabel() {
		return NumberConversionUtil.toHex(address, 4) + " " + label;
	}

	//@Override
	public int compareTo(PublicLabel o) {
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
