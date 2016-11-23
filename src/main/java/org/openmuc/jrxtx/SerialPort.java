package org.openmuc.jrxtx;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

interface SerialPort extends Closeable {

    /**
     * Get the current serial port configuration.
     * 
     * @return the serial port configuration.
     */
    SerialPortConfig getConfig();

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
     * Closes the port connection, sets the status to closed, and disposes of the internal streams.
     */

    void close() throws IOException;

    /**
     * Returns whether the port is currently open and available for communication.
     * 
     * @return true if the port is closed.
     */
    boolean isClosed();

}
