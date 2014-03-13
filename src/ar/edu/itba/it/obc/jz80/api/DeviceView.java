package ar.edu.itba.it.obc.jz80.api;

import java.awt.Container;
import java.util.HashMap;

/**
 * DeviceView is the interface for all views that control a device.
 * 
 * @author scoffey
 * 
 */
public interface DeviceView {

	/**
	 * @return Array of default ports to connect the device.
	 */
	public int[] getDefaultPorts();

	/**
	 * @return Device that the view controls. It might be a new device with
	 *         default options.
	 */
	public Device getDevice();

	/**
	 * Instantiates a new device to be controlled by this view according to the
	 * parameters given by a dictionary of options.
	 * 
	 * @param options Dictionary with the parameters of the device.
	 * @return Device (just instantiated or not) that the view controls.
	 * @throws Exception Any exception related to invalid options.
	 */
	public Device getDevice(HashMap<String, String> options) throws Exception;

	/**
	 * @return A GUI container to render the view for the device.
	 */
	public Container getView();

}
