package org.openmuc.jrxtx;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.openmuc.jrxtx.config.SerialPortConfig;

interface SerialPort extends Closeable {

    /**
     * Get the current serial port configuration.
     * 
     * @return the serial port configuration.
     */
    SerialPortConfig getSerialPortConfig();

    /**
     * Opens a new serial port connection.
     * 
     * @throws IOException
     *             if the port could not be opened.
     */
    void open() throws IOException;

    /**
     * TODO
     * 
     * @return TODO
     * @throws IOException
     *             TODO
     */
    InputStream getInputStream() throws IOException;

    /**
     * TODO
     * 
     * @return TODO
     * @throws IOException
     *             TODO
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * Closes the serial port and frees the system resources.
     */
    void close() throws IOException;

    /**
     * Returns whether the port is currently open and available for communication.
     * 
     * @return true if the port is open.
     */
    boolean isOpen();

}
