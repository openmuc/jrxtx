package org.openmuc.jrxtx;

import java.io.InterruptedIOException;

public class SerialPortTimeoutException extends InterruptedIOException {

    public SerialPortTimeoutException(String message) {
        super(message);
    }

}
