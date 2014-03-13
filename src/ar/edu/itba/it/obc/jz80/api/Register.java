package ar.edu.itba.it.obc.jz80.api;

/**
 * Register is an interface that extends Operand by providing a method to add a
 * listener to the register in order to participate in Observer patterns.
 * 
 * @author scoffey
 * 
 */
public interface Register extends Operand {

	public abstract void addListener(DeviceListener listener);

}
