package org.openmuc.jrxtx.config;

import org.openmuc.jrxtx.FlowControl;
import org.openmuc.jrxtx.Parity;
import org.openmuc.jrxtx.StopBits;

/**
 * A SerialPort configuration.
 */
public interface SerialPortConfig {

    /**
     * Get the serial port name.
     * 
     * @return the serial port name.
     */
    String getPortName();

    /**
     * Get the parity.
     * 
     * @return the parity.
     */
    Parity getParity();

    /**
     * Get the stop bits.
     * 
     * @return the stop bits.
     */
    StopBits getStopBits();

    /**
     * Get the baud rate speed.
     * 
     * @return the baud rate speed.
     */
    int getBaudRate();

    /**
     * Get the flow control.
     * 
     * @return the flow control.
     */
    FlowControl getFlowControl();

    /**
     * Returns setting for socket timout. 0 returns implies that the option is disabled (i.e., timeout of infinity).
     * 
     * @return the setting for socket timout
     */
    int getSerialPortTimeout();

}
