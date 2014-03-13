package ar.edu.itba.it.obc.jz80.view;

import java.awt.event.*;
import java.lang.reflect.*;

import ar.edu.itba.it.obc.jz80.JZ80Sim;

public class JZ80ActionListener implements ActionListener {
	
	JZ80Sim sim;
	
	Method method;
	
	public JZ80ActionListener(JZ80Sim s, String action) {
		sim = s;
		Class<JZ80Sim> c = JZ80Sim.class;
		try {
			method = c.getMethod(action, new Class<?>[] {});
		} catch (SecurityException e) {
			sim.raiseException(e);
		} catch (NoSuchMethodException e) {
			sim.raiseException(e);
		}
	}
	
	public void actionPerformed(ActionEvent event) {
		try {
			method.invoke(sim);
		} catch (IllegalArgumentException e) {
			sim.raiseException(e);
		} catch (IllegalAccessException e) {
			sim.raiseException(e);
		} catch (InvocationTargetException e) {
			sim.raiseException(e.getCause());
		}
	}


}
