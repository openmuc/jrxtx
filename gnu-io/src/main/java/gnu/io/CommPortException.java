package gnu.io;

import java.io.IOException;

/**
 * Thrown to indicate that there is an error creating or accessing a COM.
 */
public class CommPortException extends IOException {
    public CommPortException(String message) {
        super(message);
    }

    public CommPortException(String message, Throwable cause) {
        super(message);
        super.initCause(cause);
    }
}
