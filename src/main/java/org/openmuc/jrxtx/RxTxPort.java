package org.openmuc.jrxtx;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.openmuc.jrxtx.config.SerialPortConfig;

import gnu.io.CommPortException;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import gnu.io.UnsupportedCommOperationException;

/**
 * This class implements RXTX port. The class is a wrapper around the RXTXPort.
 */
public class RxTxPort implements SerialPort {

    /**
     * Only access the config via {@link #getConfig()}.
     */
    private SerialPortConfig serialPortConfig;
    private Lock configLock = new ReentrantLock();

    private boolean closed;

    private RXTXPort wrappedPort;

    private SerialInputStream serialIs;

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
        result.toArray(res);
        return res;
    }

    /**
     * Allocates an new Serial port on the given port name.
     * 
     * @param portName
     * @return a new serial port object.
     * @throws SerialPortException
     *             ex if fails to allocate
     */
    public static RxTxPort allocateSerialPort(String portName) throws SerialPortException {
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

            if (portIdentifier.getPortType() != CommPortIdentifier.PORT_SERIAL) {
                // TODO throw exception
            }

            try {
                return new RxTxPort(new RXTXPort(portName));
            } catch (PortInUseException e) {
                String message = MessageFormat.format("Port {0} is already in use.", portName);
                throw new SerialPortException(message);
            }
        } catch (NoSuchPortException e) {
            String errMessage = MessageFormat.format("Serial Port {0} not found.\n{1}", portName, e.getMessage());
            throw new PortNotFoundException(errMessage);
        }

    }

    private RxTxPort(RXTXPort rxtxPort) {
        this.wrappedPort = rxtxPort;
        this.closed = true;
    }

    public InputStream getInputStream() throws IOException {
        if (!isOpen()) {
            throw new SerialPortException("Open the serial port first i.o. to access the input stream.");
        }
        return this.serialIs;
    }

    public OutputStream getOutputStream() throws IOException {
        if (!isOpen()) {
            throw new SerialPortException("Open the serial port first i.o. to access the output stream.");
        }

        // TODO Auto-generated method stub
        return null;
    }

    public void updateConfig(SerialPortConfig serialPortConfig) throws SerialPortException {
        if (!isOpen()) {
            throw new SerialPortException("Port has to be opened to update config.");
        }

        try {
            this.configLock.lock();
            this.serialPortConfig = serialPortConfig;
        } finally {
            this.configLock.unlock();
        }
        loadConfig(serialPortConfig);
    }

    /**
     * Closes the port connection, sets the status to closed, and disposes of the internal streams.
     */
    public void close() throws IOException {
        // TODO Auto-generated method stub

        this.serialIs.closeStreams();
        this.wrappedPort.close();
        this.wrappedPort = null;
    }

    /**
     * Get the current serial port configuration.
     */
    public SerialPortConfig getConfig() {
        try {
            this.configLock.lock();
            return this.serialPortConfig;
        } finally {
            this.configLock.unlock();
        }
    }

    public void open() throws IOException {
        this.wrappedPort;

        this.closed = false;
        
    }

    public boolean isOpen() {
        return !this.closed;
    }

    private void loadConfig(SerialPortConfig serialPortConfig) throws SerialPortException {
        try {
            this.configLock.lock();
            int baudRate = serialPortConfig.getBaudRate();
            int dataBits = serialPortConfig.getDatBits().getOldValue();
            int s = serialPortConfig.getStopBits().getOldValue();
            int p = serialPortConfig.getParity().getOldValue();
            try {
                this.wrappedPort.setSerialPortParams(baudRate, dataBits, s, p);
            } catch (UnsupportedCommOperationException e) {
                throw new SerialPortException("This operation is not supported on your serial port.\n" + e);
            }
        } finally {
            this.configLock.unlock();
        }
    }

    private class SerialInputStream extends InputStream {
        private static final long SLEEP_TIME = 10L;// TODO: sleep appropriate time
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

                if (closed) {
                    throw new CommPortException("Connection has been closed..");
                }
            } while (serialPortConfig.getSerialPortTimeout() <= 0
                    || elapsedTime <= serialPortConfig.getSerialPortTimeout());

            throw new SerialPortTimeoutException("Timed out, while reading the serial port.");
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (closed) {
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

}
