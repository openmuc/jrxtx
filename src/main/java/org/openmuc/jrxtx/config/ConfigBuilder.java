package org.openmuc.jrxtx.config;

import org.openmuc.jrxtx.FlowControl;
import org.openmuc.jrxtx.Parity;
import org.openmuc.jrxtx.StopBits;

public class ConfigBuilder {
    private String portName;
    private Parity parity;
    private StopBits stopBits;
    private FlowControl flowControl;
    private int socketTimout;
    private int baudRate;

    public ConfigBuilder(SerialPortConfig oldConfig) {
    }

    public ConfigBuilder(String portName) {
        this.portName = portName;

        this.parity = null;
        this.stopBits = null;
        this.flowControl = null;
        this.socketTimout = 0;
        this.baudRate = 9600;
    }

    /**
     * Set the port name.
     * 
     * @param portName
     *            the new port name.
     * @return the configuration builder itself.
     */
    public ConfigBuilder setPortName(String portName) {
        this.portName = portName;
        return this;
    }

    /**
     * Set the parity.
     * 
     * @param parity
     *            the new parity.
     * @return the configuration builder itself.
     */
    public ConfigBuilder setParity(Parity parity) {
        this.parity = parity;
        return this;
    }

    /**
     * Set the stop bits.
     * 
     * @param stopBits
     *            the new stop bits.
     * @return the configuration builder itself.
     */
    public ConfigBuilder setStopBits(StopBits stopBits) {
        this.stopBits = stopBits;
        return this;
    }

    /**
     * Set the baud rate.
     * 
     * @param baudRate
     *            the new baud rate.
     * @return the configuration builder itself.
     */
    public ConfigBuilder setBaudRate(int baudRate) {
        this.baudRate = baudRate;
        return this;
    }

    /**
     * 
     * @param flowControl
     * @return the configuration builder itself.
     */
    public ConfigBuilder setFlowControl(FlowControl flowControl) {
        this.flowControl = flowControl;
        return null;
    }

    /**
     * Enable/disable serial port timeout with the specified timeout, in milliseconds. With this option set to a
     * non-zero timeout, a read() call on the InputStream associated with the SerialPort will block for only this amount
     * of time. If the timeout expires, a org.openmuc.jrxtx.SerialPortTimeoutException is raised, though the SerialPort
     * is still valid. The option must be enabled prior to entering the blocking operation to have effect. The timeout
     * must be > 0. A timeout of zero is interpreted as an infinite timeout.
     * 
     * @param socketTimeout
     *            the socket timeout,
     * @return the configuration builder itself.
     */
    public ConfigBuilder setSocketTimeout(int socketTimout) {
        this.socketTimout = socketTimout;
        return this;
    }

    /**
     * Build a new SerialPortConfig with the supplied settings.
     * 
     * @return a new SerialPortConfig.
     */
    public SerialPortConfig build() {
        return null;
    }

    private class SerialPortConfigImpl implements SerialPortConfig {

        public String getPortName() {
            // TODO Auto-generated method stub
            return null;
        }

        public Parity getParity() {
            // TODO Auto-generated method stub
            return null;
        }

        public StopBits getStopBits() {
            // TODO Auto-generated method stub
            return null;
        }

        public int getBaudRate() {
            // TODO Auto-generated method stub
            return 0;
        }

        public FlowControl getFlowControl() {
            // TODO Auto-generated method stub
            return null;
        }

        public int getSerialPortTimeout() {
            // TODO Auto-generated method stub
            return 0;
        }

    }

}
