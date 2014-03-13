package ar.edu.itba.it.obc.jzas.util;

public class ReflectionUtils {
	@SuppressWarnings("unchecked")
	public static <T> T createInstance(String className) {
		try {
			Class<?> clazz = Class.forName(className);
			T result = (T) clazz.newInstance();
			return result;
		} catch (ClassNotFoundException e) {
			System.err.println("Can't find class " + className);
		} catch (InstantiationException e) {
			System.err.println("Class " + className + " is not concrete");
		} catch (IllegalAccessException e) {
			System.err.println("Class " + className
					+ " needs a public constructor");
		} catch (ClassCastException e) {
			System.err.println("Class " + className
					+ " doesn't implement expected interfacer");
		}
		return null;
	}
}
