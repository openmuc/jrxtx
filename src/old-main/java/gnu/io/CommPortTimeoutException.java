package gnu.io;

import java.io.InterruptedIOException;

/**
 * Signals that a timeout has occurred on a COM port read.
 */
public class CommPortTimeoutException extends InterruptedIOException {
    public CommPortTimeoutException(String message) {
        super(message);
    }
}
