package org.openmuc.jrxtx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import gnu.io.CommPortIdentifier;

/**
 * Builder class for SerialPorts. Provides a convenient way to set the various fields of a SerialPort.
 * 
 * <p>
 * Example:
 * 
 * <pre>
 * <code>
 * SerialPort port = newBuilder("/dev/ttyUSB")
 *                   .setBaudRate(19200)
 *                   .setParity(Parity.EVEN)
 *                   .build();
 * ImputStream is = port.getInputStream();
 * ..
 * </code>
 * </pre>
 * <p>
 */
public class SerialPortBuilder {

    private String portName;
    private int baudRate;
    private DataBits dataBits;
    private Parity parity;
    private StopBits stopBits;

    /**
     * Get the serial port names on the host system.
     *
     * @return the serial ports names.
     */
    public static String[] getRxTxPorts() {
        @SuppressWarnings("unchecked")
        Enumeration<CommPortIdentifier> identifiers = (Enumeration<CommPortIdentifier>) CommPortIdentifier
                .getPortIdentifiers();
        List<String> result = new ArrayList<String>(20);

        while (identifiers.hasMoreElements()) {
            CommPortIdentifier identifier = identifiers.nextElement();
            if (identifier.getPortType() != CommPortIdentifier.PORT_SERIAL) {
                continue;
            }

            result.add(identifier.getName());
        }
        String[] res = new String[result.size()];
        return result.toArray(res);
    }

    private SerialPortBuilder(String portName) {
        this.portName = portName;
        this.baudRate = 9600;
        this.dataBits = DataBits.DATABITS_8;
        this.parity = Parity.EVEN;
    }

    /**
     * Constructs a new SerialPortBuilder with the default values.
     * 
     * @param portName
     *            the serial port name. E.g. <code>"/dev/ttyUSB0"</code>
     * @return returns the new builder.
     */
    public static SerialPortBuilder newBuilder(String portName) {
        return new SerialPortBuilder(portName);
    }

    /**
     * Set the serial port name.
     * 
     * @param portName
     *            the serial port name e.g. <code>"/dev/ttyUSB0"</code>
     * @return the serial port builder.
     */
    public SerialPortBuilder setPortName(String portName) {
        this.portName = portName;
        return this;
    }

    /**
     * 
     * @param baudRate
     *            the baud rate.
     * @return the serial port builder.
     * 
     * @see SerialPortBuilder#setBaudRate(int)
     */
    public SerialPortBuilder setBaudRate(int baudRate) {
        this.baudRate = baudRate;
        return this;
    }

    public SerialPortBuilder setDataBits(DataBits dataBits) {
        this.dataBits = dataBits;
        return this;
    }

    /**
     * 
     * @param parity
     *            the parity.
     * @return the serial port builder.
     */
    public SerialPortBuilder setParity(Parity parity) {
        this.parity = parity;
        return this;
    }

    /**
     * Set the stop bits.
     * 
     * @param stopBits
     */
    public void setStopBits(StopBits stopBits) {
        this.stopBits = stopBits;
    }

    /**
     * Combine all of the options that have been set and return a new SerialPort object.
     * 
     * @return a new serial port object.
     * @throws IOException
     *             if an I/O exception occurred while opening the serial port.
     */
    public SerialPort build() throws IOException {
        return JRxTxPort.openSerialPort(portName, baudRate, parity, dataBits, stopBits);
    }
}
