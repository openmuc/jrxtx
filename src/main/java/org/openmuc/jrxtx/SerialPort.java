package org.openmuc.jrxtx;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A RS-232 serial communications port.
 * 
 * <p>
 * SerialPort describes the low-level interface to a serial communications port made available by the underlying system.
 * SerialPort defines the minimum required functionality for serial communications ports.
 * </p>
 */
public interface SerialPort extends Closeable {

    /**
     * Returns an input stream for this serial port. This is the only way to receive data from the communications port.
     * 
     * <p>
     * Closing the returned InputStream will close the associated serial port.
     * </p>
     * 
     * @return the InputStream object that can be used to read from the port.
     * @throws IOException
     *             if an I/O error occurred
     */
    InputStream getInputStream() throws IOException;

    /**
     * Returns an output stream for this serial port. This is the only way to send data to the communications port.
     * 
     * @return the OutputStream object that can be used to write to the port.
     * @throws IOException
     *             if an I/O error occurred.
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * Closes the port connection, sets the status to closed, and disposes of the internal streams.
     * 
     * @throws IOException
     *             if an I/O error occurred.
     */
    void close() throws IOException;

    /**
     * Returns whether the port is currently open and available for communication.
     * 
     * @return true if the port is closed.
     */
    boolean isClosed();

    /**
     * Get the port name of the current serial port.
     * 
     * @return the serial port name.
     */
    String getPortName();

    /**
     * @return the dataBits
     */
    DataBits getDataBits();

    /**
     * @param dataBits
     *            the dataBits to set
     * @throws IOException
     */
    void setDataBits(DataBits dataBits) throws IOException;

    /**
     * @return the parity
     */
    Parity getParity();

    /**
     * @param parity
     *            the parity to set
     * @throws IOException
     */
    void setParity(Parity parity) throws IOException;

    /**
     * @return the stopBits
     */
    StopBits getStopBits();

    /**
     * @param stopBits
     *            the stopBits to set
     * @throws IOException
     */
    void setStopBits(StopBits stopBits) throws IOException;

    /**
     * @return the baudRate setting.
     * 
     * @see #setBaudRate(int)
     */
    int getBaudRate();

    /**
     * Sets the baud rate of the system.
     * 
     * @param baudRate
     *            the new baud rate.
     * @throws IOException
     *             if an I/O exception occurred when setting the new baud rate.
     * 
     * @see #getBaudRate()
     */
    void setBaudRate(int baudRate) throws IOException;

    /**
     * Returns setting for serial port timeout. <code>0</code> returns implies that the option is disabled (i.e.,
     * timeout of infinity).
     * 
     * @return the serialPortTimeout.
     * 
     * @see #setSerialPortTimeout(int)
     */
    int getSerialPortTimeout();

    /**
     * Enable/disable serial port timeout with the specified timeout, in milliseconds. With this option set to a
     * non-zero timeout, a read() call on the InputStream associated with this serial port will block for only this
     * amount of time. If the timeout expires, a org.openmuc.jrxtx.SerialPortTimeoutExcepption is raised, though the
     * serial port is still valid. The option must be enabled prior to entering the blocking operation to have effect.
     * The timeout must be <ode>&gt; 0<code>. A timeout of zero is interpreted as an infinite timeout.
     * 
     * @param serialPortTimeout
     *            the specified timeout, in milliseconds.
     * @throws IOException
     *             if there is an error in the underlying protocol.
     * 
     * @see #getSerialPortTimeout()
     */
    void setSerialPortTimeout(int serialPortTimeout) throws IOException;

}
