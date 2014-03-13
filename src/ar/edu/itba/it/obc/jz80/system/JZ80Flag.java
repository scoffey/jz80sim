package ar.edu.itba.it.obc.jz80.system;

public enum JZ80Flag {
	C(0), N(1), P(3), V(3), H(5), Z(6), S(7);

	private int mask;

	private JZ80Flag(int bit) {
		mask = (1 << bit);
	}

	public int getMask() {
		return mask;
	}
}
