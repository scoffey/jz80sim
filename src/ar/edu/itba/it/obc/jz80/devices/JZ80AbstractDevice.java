package ar.edu.itba.it.obc.jz80.devices;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ar.edu.itba.it.obc.jz80.api.DeviceListener;
import ar.edu.itba.it.obc.jz80.api.Device;

public abstract class JZ80AbstractDevice implements Device {
	
	private Set<DeviceListener>[] listeners;
	
	@SuppressWarnings("unchecked")
	public void resetListeners() {
		listeners = new HashSet[getSize()];
		for (int i = 0; i < listeners.length; i++) {
			if (isWriteableAt(i)) {
				listeners[i] = new HashSet<DeviceListener>();
			}
		}
	}
	
	public void addDeviceListenerAt(DeviceListener listener, int address) {
		listeners[address].add(listener);
	}

	public void removeDeviceListenerAt(DeviceListener listener, int address) {
		listeners[address].remove(listener);
	}

	public Iterator<DeviceListener> getDeviceListenersAt(int address) {
		return listeners[address].iterator();
	}
	
	public void triggerListenerEventAt(int address) {
		for (DeviceListener listener : listeners[address]) {
			listener.onWrite(this, address);
		}
	}
	
	public abstract int getSize();

}
