package ar.edu.itba.it.obc.jzas.lexer;

/**
 * Token identified by the lexical analyzer
 * <p>
 * A token has a type, usually bound to an enum, and a value that may be empty
 * for some types and may contain further information for others
 * 
 * @param <T>
 *            the class of the enumeration of types
 */
public class Token<T> {
	private T type;
	private Object value = null;

	public Token(T type) {
		super();
		this.type = type;
	}

	public Token(T type, Object value) {
		super();
		this.type = type;
		this.value = value;
	}

	/**
	 * Returns the type of the Token.
	 * <p>
	 * Note that types returned may not be unique, but the equals method must
	 * correctly find two instances of the same type
	 * </p>
	 * 
	 * @return The type of the token
	 */
	public T getType() {
		return type;
	}

	/**
	 * Returns the associated value of the token, or null if no Value is defined
	 * 
	 * @return
	 */
	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		if (value == null) {
			return type.toString();
		} else {
			// TODO: esta linea fue modificada
			return String.format("%s (%s)", type.toString(), value.toString());
			// return String.format("%s ", value.toString());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Token other = (Token) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
