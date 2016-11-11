package org.openmuc.jrxtx;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;

import org.openmuc.jrxtx.config.SerialPortConfig;

import gnu.io.CommPortIdentifier;

/**
 * This class implements RXTX port. The class is a wrapper around the RXTXPort.
 */
public class RxTxPort implements SerialPort {

    private SerialPortConfig serialPortConfig;

    /**
     * Get the serial port names on the host system.
     * 
     * @return the serial ports names.
     */
    public static String[] getRxTxPorts() {
        @SuppressWarnings("unchecked")
        Enumeration<CommPortIdentifier> identifiers = (Enumeration<CommPortIdentifier>) CommPortIdentifier
                .getPortIdentifiers();
        ArrayList<String> result = new ArrayList<String>(20);

        while (identifiers.hasMoreElements()) {
            CommPortIdentifier identifier = identifiers.nextElement();
            if (identifier.getPortType() != CommPortIdentifier.PORT_SERIAL) {
                continue;
            }

            result.add(identifier.getName());
        }

        return (String[]) result.toArray();
    }

    /**
     * Allocates an new Serial port on the given port name.
     * 
     * @param portName
     * @return
     */
    public static RxTxPort allocateRxTxPort(String portName) {
        return null;
    }

    public RxTxPort(SerialPortConfig serialPortConfig) {
        this.serialPortConfig = serialPortConfig;
    }

    public InputStream getInputStream() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    public OutputStream getOutputStream() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    public void close() throws IOException {
        // TODO Auto-generated method stub

    }

    public SerialPortConfig getSerialPortConfig() {
        // TODO Auto-generated method stub
        return null;
    }

    public void open() throws IOException {
        // TODO Auto-generated method stub

    }

    public boolean isOpen() {
        // TODO Auto-generated method stub
        return false;
    }

}
