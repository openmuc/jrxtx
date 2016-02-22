package gnu.io;

import java.io.IOException;

public class CommPortTimeoutException extends IOException {
	public CommPortTimeoutException(String message) {
		super(message);
	}
}
