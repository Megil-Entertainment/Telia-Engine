package ch.megil.teliaengine.file.exception;

public class AssetCreationException extends Exception {
	private static final long serialVersionUID = 7973888135485564400L;

	public AssetCreationException() {
		super();
	}

	public AssetCreationException(String message) {
		super(message);
	}

	public AssetCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AssetCreationException(Throwable cause) {
		super(cause);
	}
}
