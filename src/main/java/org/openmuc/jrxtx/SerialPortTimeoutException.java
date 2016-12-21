package org.openmuc.jrxtx;

import java.io.InterruptedIOException;

/**
 * Signals that the read function of the SerialPort input stream has timed out.
 */
public class SerialPortTimeoutException extends InterruptedIOException {

    /**
     * Constructs a new SerialPortTimeoutException with the specified detail message.
     * 
     * @param message
     *            the detail message.
     */
    public SerialPortTimeoutException(String message) {
        super(message);
    }

}
