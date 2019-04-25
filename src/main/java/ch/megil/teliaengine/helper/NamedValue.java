package ch.megil.teliaengine.helper;

public class NamedValue<T> {
	private String name;
	private T value;
	
	public NamedValue(String name, T value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public T getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
