package ar.edu.itba.it.obc.jzas.linker;

public interface ObjectiveCodeAnalyzer {
	/**
	 * Retorna la dirección de la instrucción actual relativa al segmento que
	 * corresponda. En caso de que la instrucción no tenga offset, devuelve -1.
	 * 
	 * @param codifiedInstruction
	 *            String con la instrucción codificada
	 * @return Dirección de la insturccion actual relativa al segmento que
	 *         corresponda.
	 */
	public int getAddress(String codifiedInstruction);

	/**
	 * Retorna la dirección de la instrucción actual relativa al segmento que
	 * corresponda. En caso de que la instrucción no tenga offset, devuelve -1.
	 * 
	 * @param codifiedInstruction
	 *            String con la instrucción codificada
	 * @return Dirección de la insturccion actual relativa al segmento que
	 *         corresponda.
	 */
	public String getAddressAsString(String codifiedInstruction);

	/**
	 * Retorna el tamaño de la instrucción que recibe como primer parámetro.
	 * 
	 * @param codifiedInstruction
	 *            Instrucción codificada de la que se quiere obtener el largo.
	 * @return Largo de la instrucción codificada.
	 */
	public int getLength(String codifiedInstruction);

	/**
	 * Genera una nueva instrucción con sumandole al offset especificado la
	 * cantidad de bytes que indica el segundo parámetro. En caso de que el
	 * offset especificado sea inválido o la cantidad de bytes a sumar sea
	 * negativa devuelve null.
	 * 
	 * @param codifiedInstruction
	 *            String con la instrucción codificada.
	 * @param offset
	 *            Desplazamiento.
	 * @param count
	 *            Cantidad de bytes a sumar.
	 * @return Nueva instrucción modificada.
	 */
	public String incrementInstructionData(String codifiedInstruction, int count);

	/**
	 * Aumenta la dirección de la instrucción en la cantidad especificada por el
	 * segundo parámetro. Modifica el checksum según corresponda.Retorna null en
	 * caso de que la cantidad de bytes sea negativa.
	 * 
	 * @param codifiedInstruction
	 *            Insturcción codificada.
	 * @param count
	 *            Cantidad de bytes en que se quiere aumentar la dirección de la
	 *            instrucción.
	 * @return Instrucción modificada.
	 */
	public String incrementInstructionAddress(String codifiedInstruction,
			int count);
}
