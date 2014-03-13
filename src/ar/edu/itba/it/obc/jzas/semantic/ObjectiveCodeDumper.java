package ar.edu.itba.it.obc.jzas.semantic;

public interface ObjectiveCodeDumper {
	public void dumpInstruction(OCRecord i);

	/**
	 * 
	 * @param data
	 *            Datos codificados en hexa
	 */
	public void allocMemory(Integer offset, String data);

	public void startASegSection();

	public void startCSegSection();

	public void startDSegSection();

	public void eof();

	public void close();

	public void writeStringLine(String s);

	public void endASegSection();

	public void endCSegSection();

	public void endDSegSection();
}
