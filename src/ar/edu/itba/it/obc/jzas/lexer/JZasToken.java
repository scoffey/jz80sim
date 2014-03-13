package ar.edu.itba.it.obc.jzas.lexer;

public class JZasToken<T> {

	private T type;

	private Object value = null;

	public JZasToken(T type) {
		super();
		this.type = type;
	}

	public JZasToken(T type, Object value) {
		super();
		this.type = type;
		this.value = value;
	}

	public T getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		if (value == null) {
			return type.toString();
		} else {
			return String.format("%s (%s)", type.toString(), value.toString());
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
		final JZasToken other = (JZasToken) obj;
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
