package org.openmuc.jrxtx;

import java.io.IOException;

/**
 * Signals that a I/O exception with the SerialPort occurred.
 * 
 * @see SerialPort
 */
public class SerialPortException extends IOException {

    /**
     * Constructs a new SerialPortException with the specified detail message.
     * 
     * @param message
     *            the detail message.
     */
    public SerialPortException(String message) {
        super(message);
    }

}
