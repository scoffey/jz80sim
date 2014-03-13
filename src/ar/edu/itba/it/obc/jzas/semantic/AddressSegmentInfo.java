package ar.edu.itba.it.obc.jzas.semantic;

import ar.edu.itba.it.obc.jzas.semantic.SemanticAnalyzer.Segment;
import ar.edu.itba.it.obc.jzas.util.NumberConversionUtil;

public class AddressSegmentInfo implements Comparable<AddressSegmentInfo> {
	private Segment segment;
	private Integer address;
	private Segment segmentReferenced;

	public AddressSegmentInfo(Segment segment, Integer address, Segment segmentReferenced) {
		this.segment = segment;
		this.address = address;
		this.segmentReferenced = segmentReferenced;
	}

	public String getAddressAndSegments() {
		StringBuffer ret = new StringBuffer();
		ret.append(NumberConversionUtil.toHex(address, 4));
		ret.append(" ");
		ret.append(segmentReferenced.toString());
		return ret.toString();
	}

	public Segment getSegment() {
		return segment;
	}

	public Integer getAddress() {
		return address;
	}

	public Segment getSegmentReferenced() {
		return segmentReferenced;
	}

	//@Override
	public int compareTo(AddressSegmentInfo o) {
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
