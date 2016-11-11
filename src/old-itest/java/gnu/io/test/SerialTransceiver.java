/*
 * Copyright 2010-15 Fraunhofer ISE
 *
 * This file is part of jMBus.
 * For more information visit http://www.openmuc.org
 *
 * jMBus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * jMBus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jMBus.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package gnu.io.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

class SerialTransceiver {

    private final String serialPortName;
    private final int baudRate;
    private final int dataBits;
    private final int stopBits;
    private final int parity;
    private DataOutputStream os;
    private DataInputStream is;

    private SerialPort serialPort;

    public SerialTransceiver(String serialPortName, int baudRate, int dataBits, int stopBits, int parity) {
        this.serialPortName = serialPortName;
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
    }

    /**
     * Opens the serial port. The serial port needs to be opened before attempting to read a device.
     * 
     * @throws IOException
     *             if any kind of error occurs opening the serial port.
     */
    public void open() throws IOException {
        CommPortIdentifier.getPortIdentifiers();
        CommPortIdentifier portIdentifier;
        try {
            portIdentifier = CommPortIdentifier.getPortIdentifier(serialPortName);
        } catch (NoSuchPortException e) {
            IOException ioException = new IOException("Serial port is currently in use.");
            ioException.initCause(e);
            throw ioException;
        }

        if (portIdentifier.isCurrentlyOwned()) {
            throw new IOException("Serial port is currently in use.");
        }

        CommPort commPort;
        try {
            commPort = portIdentifier.open(this.getClass().getName(), 2000);
        } catch (PortInUseException e) {
            IOException ioException = new IOException("Serial port is currently in use.");
            ioException.initCause(e);
            throw ioException;
        }

        if (!(commPort instanceof SerialPort)) {
            commPort.close();
            throw new IOException("The specified CommPort is not a serial port");
        }

        serialPort = (SerialPort) commPort;

        try {
            serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);
        } catch (UnsupportedCommOperationException e) {
            serialPort.close();
            serialPort = null;
            IOException ioException = new IOException("Unable to set the baud rate or other serial port parameters");
            ioException.initCause(e);
            throw ioException;
        }

        try {
            os = new DataOutputStream(new BufferedOutputStream(serialPort.getOutputStream()));
            is = new DataInputStream(new BufferedInputStream(serialPort.getInputStream()));
        } catch (IOException e) {
            serialPort.close();
            serialPort = null;
            IOException ioException = new IOException("Error getting input or output or input stream from serial port");
            ioException.initCause(e);
            throw ioException;
        }

    }

    /**
     * Closes the serial port.
     */
    public void close() {
        if (serialPort == null) {
            return;
        }
        serialPort.close();
        serialPort = null;
    }

    public DataOutputStream getOutputStream() {
        return os;
    }

    public DataInputStream getInputStream() {
        return is;
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public boolean isClosed() {
        return (serialPort == null);
    }

}
