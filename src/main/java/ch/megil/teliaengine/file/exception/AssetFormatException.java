package ch.megil.teliaengine.file.exception;

public class AssetFormatException extends AssetLoadException {
	private static final long serialVersionUID = -2102647624832672335L;

	public AssetFormatException() {
		super();
	}

	public AssetFormatException(String message) {
		super(message);
	}

	public AssetFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public AssetFormatException(Throwable cause) {
		super(cause);
	}
}
