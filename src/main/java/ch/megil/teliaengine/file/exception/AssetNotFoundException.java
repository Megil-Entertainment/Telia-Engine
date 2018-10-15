package ch.megil.teliaengine.file.exception;

public class AssetNotFoundException extends Exception {
	private static final long serialVersionUID = 4149231966662622300L;

	public AssetNotFoundException() {
		super();
	}

	public AssetNotFoundException(String message) {
		super(message);
	}

	public AssetNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public AssetNotFoundException(Throwable cause) {
		super(cause);
	}
}