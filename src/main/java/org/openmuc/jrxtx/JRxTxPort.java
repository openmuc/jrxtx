package org.openmuc.jrxtx;

import static gnu.io.SerialPort.FLOWCONTROL_NONE;
import static gnu.io.SerialPort.FLOWCONTROL_RTSCTS_IN;
import static gnu.io.SerialPort.FLOWCONTROL_RTSCTS_OUT;
import static gnu.io.SerialPort.FLOWCONTROL_XONXOFF_IN;
import static gnu.io.SerialPort.FLOWCONTROL_XONXOFF_OUT;
import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import gnu.io.UnsupportedCommOperationException;

@SuppressWarnings("deprecation")
class JRxTxPort implements SerialPort {

    private volatile boolean closed;

    private RXTXPort rxtxPort;

    private SerialInputStream serialIs;
    private SerialOutputStream serial0s;

    private String portName;

    private DataBits dataBits;

    private Parity parity;

    private StopBits stopBits;

    private int baudRate;

    private int serialPortTimeout;

    private FlowControl flowControl;

    public static JRxTxPort openSerialPort(String portName, int baudRate, Parity parity, DataBits dataBits,
            StopBits stopBits, FlowControl flowControl) throws SerialPortException {
        try {
            System.setProperty("gnu.io.rxtx.SerialPorts", portName);

            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

            String theOwner = JRxTxPort.class.getCanonicalName() + System.currentTimeMillis();
            int timeOut = 2000;
            CommPort comPort = portIdentifier.open(theOwner, timeOut);

            if (!(comPort instanceof RXTXPort)) {
                throw new SerialPortException("Unable to open the serial port. Port is not RXTX.");
            }

            RXTXPort rxtxPort = (RXTXPort) comPort;

            try {
                rxtxPort.setSerialPortParams(baudRate, dataBits.getOldValue(), stopBits.getOldValue(),
                        parity.getOldValue());

                setFlowControl(flowControl, rxtxPort);
            } catch (UnsupportedCommOperationException e) {
                String message = format("Not able to apply config on serial port.\n{0}", e.getMessage());
                throw new SerialPortException(message);
            }

            return new JRxTxPort(rxtxPort, portName, baudRate, parity, dataBits, stopBits, flowControl);
        } catch (NoSuchPortException e) {
            String errMessage = format("Serial Port {0} not found.", portName);
            throw new PortNotFoundException(errMessage);
        } catch (PortInUseException e) {
            String errMessage = format("Serial Port {0} is already in use.", portName);
            throw new PortNotFoundException(errMessage);
        }

    }

    private static void setFlowControl(FlowControl flowControl, RXTXPort rxtxPort) {
        switch (flowControl) {
        case RTS_CTS:
            rxtxPort.setFlowControlMode(FLOWCONTROL_RTSCTS_IN | FLOWCONTROL_RTSCTS_OUT);
            break;
        case XON_XOFF:
            rxtxPort.setFlowControlMode(FLOWCONTROL_XONXOFF_IN | FLOWCONTROL_XONXOFF_OUT);

            break;

        case NONE:
        default:
            rxtxPort.setFlowControlMode(FLOWCONTROL_NONE);
            break;
        }
    }

    private JRxTxPort(RXTXPort comPort, String portName, int baudRate, Parity parity, DataBits dataBits,
            StopBits stopBits, FlowControl flowControl) {
        this.rxtxPort = comPort;
        this.portName = portName;
        this.baudRate = baudRate;
        this.parity = parity;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.flowControl = flowControl;

        this.closed = false;

        this.serial0s = new SerialOutputStream(this.rxtxPort.getOutputStream());
        this.serialIs = new SerialInputStream();

    }

    public InputStream getInputStream() throws IOException {
        if (isClosed()) {
            throw new SerialPortException("Open the serial port first i.o. to access the input stream.");
        }
        return this.serialIs;
    }

    public OutputStream getOutputStream() throws IOException {
        if (isClosed()) {
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
            this.rxtxPort.close();
            this.serial0s = null;
            this.serialIs = null;
            this.rxtxPort = null;
        } finally {
            this.closed = true;
        }
    }

    public boolean isClosed() {
        return this.closed;
    }

    private class SerialInputStream extends InputStream {
        private static final long SLEEP_TIME = 10L; // sleep appropriate time

        @Override
        public synchronized int read() throws IOException {
            long elapsedTime = 0;

            InputStream serialInputStream = rxtxPort.getInputStream();
            do {
                if (serialInputStream.available() > 0) {
                    int read = serialInputStream.read();
                    if (read == -1) {
                        throw new SerialPortTimeoutException("Read timed out.");
                    }
                    return read;
                }
                try {
                    Thread.sleep(SLEEP_TIME);
                    elapsedTime += SLEEP_TIME;
                } catch (InterruptedException e) {
                    // ignore
                }

                if (isClosed()) {
                    throw new SerialPortException("Serial port has been closed..");
                }
            } while (getSerialPortTimeout() == 0 || elapsedTime <= getSerialPortTimeout());

            throw new SerialPortTimeoutException("Timed out, while reading the serial port.");
        }

        @Override
        public int available() throws IOException {
            return rxtxPort.getInputStream().available();
        }

        private void closeStreams() throws IOException {
            rxtxPort.getInputStream().close();
        }

        @Override
        public void close() throws IOException {
            JRxTxPort.this.close();
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
            if (isClosed()) {
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
            JRxTxPort.this.close();
        }
    }

    public String getPortName() {
        return this.portName;
    }

    public DataBits getDataBits() {
        return this.dataBits;
    }

    public void setDataBits(DataBits dataBits) throws IOException {
        this.dataBits = dataBits;
        updateWrappedPort();
    }

    public Parity getParity() {
        return this.parity;
    }

    public void setParity(Parity parity) throws IOException {
        this.parity = parity;
        updateWrappedPort();
    }

    public StopBits getStopBits() {
        return this.stopBits;
    }

    public void setStopBits(StopBits stopBits) throws IOException {
        this.stopBits = stopBits;
        updateWrappedPort();
    }

    public int getBaudRate() {
        return this.baudRate;
    }

    public void setBaudRate(int baudRate) throws IOException {
        this.baudRate = baudRate;
        updateWrappedPort();
    }

    private void updateWrappedPort() throws IOException {
        try {
            this.rxtxPort.setSerialPortParams(this.baudRate, this.dataBits.getOldValue(), this.stopBits.getOldValue(),
                    this.parity.getOldValue());
        } catch (UnsupportedCommOperationException e) {
            throw new IOException(e.getMessage());
        }
    }

    public int getSerialPortTimeout() {
        return this.serialPortTimeout;
    }

    public void setSerialPortTimeout(int serialPortTimeout) throws IOException {
        this.serialPortTimeout = serialPortTimeout;
        if (serialPortTimeout == 0) {
            this.rxtxPort.enableReceiveTimeout(Integer.MAX_VALUE);
        }
        else {
            this.rxtxPort.enableReceiveTimeout(serialPortTimeout);
        }
    }

    public void setFlowControl(FlowControl flowControl) throws IOException {
        setFlowControl(flowControl, this.rxtxPort);
        this.flowControl = flowControl;
    }

    public FlowControl getFlowControl() {
        return this.flowControl;
    }

}
