package ch.megil.teliaengine.file.exception;

public class AssetLoadException extends Exception {
	private static final long serialVersionUID = -3595856751329854583L;

	public AssetLoadException() {
		super();
	}

	public AssetLoadException(String message) {
		super(message);
	}

	public AssetLoadException(String message, Throwable cause) {
		super(message, cause);
	}

	public AssetLoadException(Throwable cause) {
		super(cause);
	}
}
