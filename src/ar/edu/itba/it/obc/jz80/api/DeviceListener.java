package ar.edu.itba.it.obc.jz80.api;

/**
 * DeviceListener is the interface for all event handlers in observable devices.
 * The only method required is the handler for the write event.
 * 
 * @author scoffey
 * 
 */

public interface DeviceListener {

	/**
	 * Handles the write event. For convenience, it receives the device and the
	 * address written as parameters.
	 * 
	 * @param d Device that was written.
	 * @param address Address of the device register written.
	 */
	public void onWrite(Device d, int address);

}
