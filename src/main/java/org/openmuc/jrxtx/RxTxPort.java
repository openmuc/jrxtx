package org.openmuc.jrxtx;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;

import gnu.io.CommPort;
import gnu.io.CommPortException;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import gnu.io.UnsupportedCommOperationException;

/**
 * This class implements RXTX port.
 * 
 * <p>
 * The class is a wrapper around the RXTXPort.
 * </p>
 */
public class RxTxPort implements SerialPort {

    /**
     * Only access the config via {@link #getConfig()}.
     */
    private SerialPortConfig serialPortConfig;

    private volatile boolean closed;

    private RXTXPort wrappedPort;

    private SerialInputStream serialIs;
    private SerialOutputStream serial0s;

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
        String[] res = new String[result.size()];
        return result.toArray(res);
    }

    public static RxTxPort openSerialPort(String portName, int baudRate) throws SerialPortException {
        Parity parity = Parity.EVEN;
        return openSerialPort(portName, baudRate, parity);
    }

    public static RxTxPort openSerialPort(String portName) throws SerialPortException {
        int baudRate = 9600;
        return openSerialPort(portName, baudRate);
    }

    public static RxTxPort openSerialPort(String portName, int baudRate, Parity parity) throws SerialPortException {
        return openSerialPort(portName, baudRate, parity, DataBits.DATABITS_8);
    }

    public static RxTxPort openSerialPort(String portName, int baudRate, Parity parity, DataBits dataBits)
            throws SerialPortException {
        StopBits stopbits = StopBits.STOPBITS_1;
        return openSerialPort(portName, baudRate, parity, dataBits, stopbits);
    }

    public static RxTxPort openSerialPort(String portName, int baudRate, Parity parity, DataBits dataBits,
            StopBits stopBits) throws SerialPortException {
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

            CommPort comPort = portIdentifier.open(RxTxPort.class.getCanonicalName() + System.currentTimeMillis(),
                    2000);

            if (!(comPort instanceof RXTXPort)) {
                throw new SerialPortException("Unable to open the serial port.");
            }

            RXTXPort rxtxPort = (RXTXPort) comPort;

            try {
                rxtxPort.setSerialPortParams(baudRate, dataBits.getOldValue(), stopBits.getOldValue(),
                        parity.getOldValue());
            } catch (UnsupportedCommOperationException e) {
                String message = format("Not able to apply config on serial port.\n{0}", e.getMessage());
                throw new SerialPortException(message);
            }

            return new RxTxPort(rxtxPort);
        } catch (NoSuchPortException e) {
            String errMessage = format("Serial Port {0} not found.\n{1}", portName, e.getMessage());
            throw new PortNotFoundException(errMessage);
        } catch (PortInUseException e) {
            String errMessage = format("Serial Port {0} is already in use.\n{1}", portName, e.getMessage());
            throw new PortNotFoundException(errMessage);
        }

    }

    private RxTxPort(RXTXPort comPort) {
        this.wrappedPort = comPort;
        this.closed = false;

        this.serial0s = new SerialOutputStream(this.wrappedPort.getOutputStream());
        this.serialIs = new SerialInputStream(this.wrappedPort.getInputStream());

        this.serialPortConfig = initConfig();
    }

    private SerialPortConfigImpl initConfig() {
        String portName = wrappedPort.getName();
        int baudRate = wrappedPort.getBaudRate();
        Parity parity = Parity.forValue(wrappedPort.getParity());
        StopBits stopBits = null;
        DataBits dataBits = null;
        int serialPortTimeout = 0;
        return new SerialPortConfigImpl(portName, baudRate, parity, stopBits, dataBits, serialPortTimeout);
    }

    public InputStream getInputStream() throws IOException {
        if (!isClosed()) {
            throw new SerialPortException("Open the serial port first i.o. to access the input stream.");
        }
        return this.serialIs;
    }

    public OutputStream getOutputStream() throws IOException {
        if (!isClosed()) {
            throw new SerialPortException("Open the serial port first i.o. to access the output stream.");
        }

        return this.serial0s;
    }

    public synchronized void close() throws IOException {
        if (closed) {
            throw new SerialPortException("Serial Port is already closed.");
        }

        try {
            this.serial0s.closeStreams();
            this.serialIs.closeStreams();
            this.wrappedPort.close();
            this.serial0s = null;
            this.serialIs = null;
            this.wrappedPort = null;
        } finally {
            this.closed = true;
        }
    }

    public SerialPortConfig getConfig() {
        return this.serialPortConfig;
    }

    public boolean isClosed() {
        return this.closed;
    }

    private class SerialPortConfigImpl implements SerialPortConfig {

        private Parity parity;
        private StopBits stopBits;
        private DataBits dataBits;
        private int baudRate;
        private int serialPortTimeout;
        private String portName;

        public SerialPortConfigImpl(String portName, int baudRate, Parity parity, StopBits stopBits, DataBits dataBits,
                int serialPortTimeout) {
            this.parity = parity;
            this.stopBits = stopBits;
            this.dataBits = dataBits;
            this.baudRate = baudRate;
            this.serialPortTimeout = serialPortTimeout;
        }

        public synchronized DataBits getDataBits() {
            return dataBits;
        }

        public synchronized void setDataBits(DataBits dataBits) {
            this.dataBits = dataBits;
        }

        public synchronized Parity getParity() {
            return parity;
        }

        public synchronized void setParity(Parity parity) {
            this.parity = parity;
        }

        public synchronized StopBits getStopBits() {
            return stopBits;
        }

        public synchronized void setStopBits(StopBits stopBits) {
            this.stopBits = stopBits;
        }

        public synchronized int getBaudRate() {
            return baudRate;
        }

        public synchronized void setBaudRate(int baudRate) throws IOException {
            this.baudRate = baudRate;

            reloadSettings();
        }

        public synchronized int getSerialPortTimeout() {
            return serialPortTimeout;
        }

        public synchronized void setSerialPortTimeout(int serialPortTimeout) {
            if (serialPortTimeout < 0) {
                // TODO error must be greater or equal 0
            }

            this.serialPortTimeout = serialPortTimeout;
            if (this.serialPortTimeout == 0) {
                RxTxPort.this.wrappedPort.disableReceiveTimeout();
            }
            else {
                RxTxPort.this.wrappedPort.enableReceiveTimeout(this.serialPortTimeout);
            }
        }

        private void reloadSettings() throws IOException {
            try {
                RxTxPort.this.wrappedPort.setSerialPortParams(this.baudRate, this.dataBits.getOldValue(),
                        this.stopBits.getOldValue(), this.parity.getOldValue());
            } catch (UnsupportedCommOperationException e) {
                // ignore.. should not occur here
                throw new IOException(e.getMessage());
            }
        }

        public String getPortName() {
            return this.portName;
        }

    }

    private class SerialInputStream extends InputStream {
        private static final long SLEEP_TIME = 10L; // sleep appropriate time
        private final InputStream serialInputStream;

        public SerialInputStream(InputStream serialInputStream) {
            this.serialInputStream = serialInputStream;
        }

        @Override
        public synchronized int read() throws IOException {
            long elapsedTime = 0;
            do {
                if (serialInputStream.available() > 0) {
                    return serialInputStream.read();
                }
                try {
                    Thread.sleep(SLEEP_TIME);
                    elapsedTime += SLEEP_TIME;
                } catch (InterruptedException e) {
                    // ignore
                }

                if (!isClosed()) {
                    throw new CommPortException("Connection has been closed..");
                }
            } while (serialPortConfig.getSerialPortTimeout() <= 0
                    || elapsedTime <= serialPortConfig.getSerialPortTimeout());

            throw new SerialPortTimeoutException("Timed out, while reading the serial port.");
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (!isClosed()) {
                throw new CommPortException("Connection has been closed..");
            }
            return this.serialInputStream.read(b, off, Math.min(available(), len));
        }

        @Override
        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public int available() throws IOException {
            return this.serialInputStream.available();
        }

        private void closeStreams() throws IOException {
            this.serialInputStream.close();
        }

        @Override
        public void close() throws IOException {
            RxTxPort.this.close();
        }
    }

    private class SerialOutputStream extends OutputStream {

        private OutputStream serialOutputStream;

        public SerialOutputStream(OutputStream serialOutputStream) {
            this.serialOutputStream = serialOutputStream;
        }

        @Override
        public void write(int b) throws IOException {
            checkIfOpen();

            this.serialOutputStream.write(b);
        }

        private void checkIfOpen() throws SerialPortException {
            if (!isClosed()) {
                throw new SerialPortException("Port has been closed.");
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            checkIfOpen();
            this.serialOutputStream.write(b, off, len);
        }

        @Override
        public void write(byte[] b) throws IOException {
            checkIfOpen();
            this.serialOutputStream.write(b);
        }

        @Override
        public void flush() throws IOException {
            checkIfOpen();
            this.serialOutputStream.flush();
        }

        private void closeStreams() throws IOException {
            this.serialOutputStream.close();
        }

        @Override
        public void close() throws IOException {
            RxTxPort.this.close();
        }
    }

}
