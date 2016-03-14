/*
 * Copyright 1997-2009 by Trent Jarvi and others
 * Copyright 1998 Kevin Hester, kevinh@acm.org
 * Copyright 2016 Fraunhofer ISE and others
 *
 * This file is part of jRxTx.
 * jRxTx is a fork of RXTX originally maintained by Trent Jarvi.
 *
 * jRxTx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * jRxTx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jRxTx.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package gnu.io;

import java.io.IOException;
import java.util.TooManyListenersException;

import gnu.io.serialport.DataBits;
import gnu.io.serialport.FlowControl;
import gnu.io.serialport.Parity;
import gnu.io.serialport.StopBits;
import gnu.io.serialport.UARTType;

public abstract class SerialPort extends AbstractCommPort {
    /* Data Bits Constants */
    /**
     * @deprecated use {@link DataBits#DATABITS_5} instead.
     */
    @Deprecated
    public static int DATABITS_5 = 5;

    /**
     * @deprecated use {@link DataBits#DATABITS_6} instead.
     */
    @Deprecated
    public static int DATABITS_6 = 6;

    /**
     * @deprecated use {@link DataBits#DATABITS_7} instead.
     */
    @Deprecated
    public static int DATABITS_7 = 7;

    /**
     * @deprecated use {@link DataBits#DATABITS_8} instead.
     */
    @Deprecated
    public static int DATABITS_8 = 8;

    /* Parity Constants */
    /**
     * @deprecated use {@link Parity#NONE} instead.
     */
    @Deprecated
    public static int PARITY_NONE = 0;

    /**
     * @deprecated use {@link Parity#ODD} instead.
     */
    @Deprecated
    public static int PARITY_ODD = 1;
    /**
     * @deprecated use {@link Parity#EVEN} instead.
     */
    @Deprecated
    public static int PARITY_EVEN = 2;
    /**
     * @deprecated use {@link Parity#MARK} instead.
     */
    @Deprecated
    public static int PARITY_MARK = 3;

    /**
     * MARK parity scheme.
     */
    public static int PARITY_SPACE = 4;

    /* Stop Bits Constants */
    /**
     * @deprecated use {@link StopBits#STOPBITS_1} instead.
     */
    @Deprecated
    public static int STOPBITS_1 = 1;
    /**
     * @deprecated use {@link StopBits#STOPBITS_2} instead.
     */
    @Deprecated
    public static int STOPBITS_2 = 2;
    /**
     * @deprecated use {@link StopBits#STOPBITS_1_5} instead.
     */
    @Deprecated
    public static int STOPBITS_1_5 = 3;

    /* Flow Control Constants */
    /**
     * @deprecated use {@link FlowControl#NONE} instead.
     */
    @Deprecated
    public static int FLOWCONTROL_NONE = 0;
    /**
     * @deprecated use {@link FlowControl#RTSCTS_IN} instead.
     */
    @Deprecated
    public static int FLOWCONTROL_RTSCTS_IN = 1;
    /**
     * @deprecated use {@link FlowControl#RTSCTS_OUT} instead.
     */
    @Deprecated
    public static int FLOWCONTROL_RTSCTS_OUT = 2;
    /**
     * @deprecated use {@link FlowControl#XONXOFF_IN} instead.
     */
    @Deprecated
    public static int FLOWCONTROL_XONXOFF_IN = 4;
    /**
     * @deprecated use {@link FlowControl#XONXOFF_OUT} instead.
     */
    @Deprecated
    public static int FLOWCONTROL_XONXOFF_OUT = 8;

    /**
     * Sets serial port parameters.
     * 
     * <p>
     * Note: default value are: 9600 baud, 8 data bits, 1 stop bit, no parity
     * </p>
     * 
     * @param baudrate
     *            If the baudrate passed in by the application is unsupported by the driver, the driver will throw an
     *            {@linkplain UnsupportedCommOperationException}
     * @param dataBits
     *            dataBits
     * @param stopBits
     *            stopBits
     * @param parity
     *            parity
     * @throws UnsupportedCommOperationException
     *             if any of the above parameters are specified incorrectly. All four of the parameters will revert to
     *             the values before the call was made.
     * 
     * @deprecated use {@link #setSerialPortParams(int, DataBits, StopBits, Parity)} instead
     */
    @Deprecated
    public abstract void setSerialPortParams(int baudrate, int dataBits, int stopBits, int parity)
            throws UnsupportedCommOperationException;

    /**
     * Sets serial port parameters.
     * 
     * <p>
     * Note: default value are: {@code 9600} baud, {@link DataBits#DATABITS_8}, {@link StopBits#STOPBITS_1},
     * {@link Parity#NONE}
     * </p>
     * 
     * @param baudrate
     *            If the baudrate passed in by the application is unsupported by the driver, the driver will throw an
     *            {@linkplain UnsupportedCommOperationException}
     * @param dataBits
     *            dataBits
     * @param stopBits
     *            stopBits
     * @param parity
     *            parity
     * @throws UnsupportedCommOperationException
     *             if any of the above parameters are specified incorrectly. All four of the parameters will revert to
     *             the values before the call was made.
     */
    public abstract void setSerialPortParams(int baudrate, DataBits dataBits, StopBits stopBits, Parity parity)
            throws UnsupportedCommOperationException;

    /**
     * Gets the currently configured baud rate.
     * 
     * @return integer value indicating the baud rate
     */
    public abstract int getBaudRate();

    /**
     * Gets the currently configured number of data bits.
     * 
     * @return integer that can be equal to {@link DATABITS_5}, {@link DATABITS_6}, {@link DATABITS_7}, or
     *         {@link DATABITS_8}
     * 
     * @deprecated use {@link #dataBits()} instead.
     */
    @Deprecated
    public abstract int getDataBits();

    /**
     * 
     * Gets the currently configured number of data bits.
     * 
     * @return the DataBits
     */
    public abstract DataBits dataBits();

    /**
     * Gets the currently defined stop bits.
     * 
     * @return integer that can be equal to {@link STOPBITS_1}, {@link STOPBITS_2}, or {@link STOPBITS_1_5}
     * @deprecated use {@link #stopBits()} instead
     */
    @Deprecated
    public abstract int getStopBits();

    /**
     * Gets the currently defined stop bits.
     * 
     * @return the StopBits
     */
    public abstract StopBits stopBits();

    /**
     * Get the currently configured parity setting.
     * 
     * @return integer that can be equal to PARITY_NONE, PARITY_ODD, PARITY_EVEN, PARITY_MARK or PARITY_SPACE.
     * 
     * @deprecated use {@link #parity()} instead
     */
    @Deprecated
    public abstract int getParity();

    /**
     * Get the currently configured parity setting.
     * 
     * @return the parity.
     */
    public abstract Parity parity();

    /**
     * Sets the flow control mode.
     * 
     * @param flowcontrol
     *            the flowcontrol
     * @throws UnsupportedCommOperationException
     *             if any of the flow control mode was not supported by the underline OS, or if input and output flow
     *             control are set to different values, i.e. one hardware and one software. The flow control mode will
     *             revert to the value before the call was made.
     * 
     * @deprecated use {@link #setFlowControlMode(FlowControl)} instead.
     */
    @Deprecated
    public abstract void setFlowControlMode(int flowcontrol) throws UnsupportedCommOperationException;

    /**
     * Sets the flow control mode.
     * 
     * @param flowcontrol
     *            the new flow control mode
     * @throws UnsupportedCommOperationException
     *             if any of the flow control mode was not supported by the underline OS, or if input and output flow
     *             control are set to different values, i.e. one hardware and one software. The flow control mode will
     *             revert to the value before the call was made.
     */
    public abstract void setFlowControlMode(FlowControl flowcontrol) throws UnsupportedCommOperationException;

    /**
     * Gets the currently configured flow control mode.
     * 
     * @return an integer bitmask of the modes FLOWCONTROL_NONE, FLOWCONTROL_RTSCTS_IN, FLOWCONTROL_RTSCTS_OUT,
     *         FLOWCONTROL_XONXOFF_IN, and FLOWCONTROL_XONXOFF_OUT.
     */
    @Deprecated
    public abstract int getFlowControlMode();

    /**
     * Gets the currently configured flow control mode.
     * 
     * @return the current flow control mode.
     */
    public abstract FlowControl flowControlMode();

    /**
     * Gets the state of the DTR (Data Terminal Ready) bit in the UART, if supported by the underlying implementation.
     * 
     * @return the DTR flag
     */
    public abstract boolean isDTR();

    /**
     * Sets or clears the DTR (Data Terminal Ready) bit in the UART, if supported by the underlying implementation.
     * 
     * @param state
     *            <ul>
     *            <li>{@code true}: set DTR</li>
     *            <li>{@code false}: clear DTR</li>
     *            </ul>
     */
    public abstract void setDTR(boolean state);

    /**
     * Sets or clears the RTS (Request To Send) bit in the UART, if supported by the underlying implementation.
     * 
     * @param state
     *            <ul>
     *            <li>{@code true}: set RTS</li>
     *            <li>{@code false}: clear RTS</li>
     *            </ul>
     */
    public abstract void setRTS(boolean state);

    /**
     * Gets the state of the CTS (Clear To Send) bit in the UART, if supported by the underlying implementation.
     * 
     * @return true if clear to send.
     */
    public abstract boolean isCTS();

    /**
     * Gets the state of the DSR (Data Set Ready) bit in the UART, if supported by the underlying implementation.
     * 
     * @return true if data set ready.
     */
    public abstract boolean isDSR();

    /**
     * Gets the state of the CD (Carrier Detect) bit in the UART, if supported by the underlying implementation.
     * 
     * @return true if carrier detected.
     */
    public abstract boolean isCD();

    public abstract boolean isRI();

    /**
     * Retrieving the DSR (<u>D</u>ata <u>S</u>et <u>R</u>eady) flag.
     * 
     * @return true if data set ready.
     */
    public abstract boolean isRTS();

    public abstract void sendBreak(int duration);

    /**
     * Registers a {@link SerialPortEventListener} object to listen for {@link SerialPortEvent}s. Interest in specific
     * events may be expressed using the notifyOnXXX calls. The serialEvent method of {@link SerialPortEventListener}
     * will be called with a SerialEvent object describing the event.
     * 
     * <p>
     * The current implementation only allows one listener per SerialPort. Once a listener is registered, subsequent
     * call attempts to {@link #addEventListener(SerialPortEventListener)} will throw a
     * {@link TooManyListenersException} without effecting the listener already registered.
     * </p>
     * 
     * <p>
     * All the events received by this listener are generated by one dedicated thread that belongs to the SerialPort
     * object. After the port is closed, no more event will be generated. Another call to open() of the port's
     * {@link CommPortIdentifier} object will return a new CommPort object, and the listener has to be added again to
     * the new {@link CommPort} object to receive event from this port.
     * </p>
     * 
     * @param listener
     *            the serial port event listener.
     * @throws TooManyListenersException
     *             If an initial attempt to attach a listener succeeds, subsequent attempts will throw
     *             {@link TooManyListenersException} without effecting the first listener.
     */
    public abstract void addEventListener(SerialPortEventListener listener) throws TooManyListenersException;

    /**
     * Unregisters event listener registered using addEventListener.
     * 
     * <p>
     * This is done automatically at {@link #close()}.
     * </p>
     */
    public abstract void removeEventListener();

    /**
     * Expresses interest in receiving notification when input data is available. This may be used to drive asynchronous
     * input. When data is available in the input buffer, this event is propagated to the listener registered using
     * {@link #addEventListener(SerialPortEventListener)}.
     * 
     * <p>
     * The event will be generated once when new data arrive at the serial port. Even if the user doesn't read the data,
     * it won't be generated again until next time new data arrive.
     * </p>
     * 
     * @param enable
     *            <ul>
     *            <li>{@code true}: enable notification</li>
     *            <li>{@code false}: enable notification</li>
     *            </ul>
     */
    public abstract void notifyOnDataAvailable(boolean enable);

    /**
     * Expresses interest in receiving notification when the output buffer is empty. This may be used to drive
     * asynchronous output. When the output buffer becomes empty, this event is propagated to the listener registered
     * using {@link #addEventListener(SerialPortEventListener)}. The event will be generated after a write is completed,
     * when the system buffer becomes empty again.
     * 
     * <p>
     * This notification is hardware dependent and may not be supported by all implementations.
     * </p>
     * 
     * @param enable
     *            <ul>
     *            <li>{@code true}: enable notification</li>
     *            <li>{@code false}: enable notification</li>
     *            </ul>
     */
    public abstract void notifyOnOutputEmpty(boolean enable);

    /**
     * Expresses interest in receiving notification when the CTS (Clear To Send) bit changes.
     * 
     * <p>
     * This notification is hardware dependent and may not be supported by all implementations.
     * </p>
     * 
     * @param enable
     *            <ul>
     *            <li>{@code true}: enable notification</li>
     *            <li>{@code false}: enable notification</li>
     *            </ul>
     */
    public abstract void notifyOnCTS(boolean enable);

    /**
     * Expresses interest in receiving notification when the DSR (Data Set Ready) bit changes.
     * 
     * <p>
     * This notification is hardware dependent and may not be supported by all implementations.
     * </p>
     * 
     * @param enable
     *            <ul>
     *            <li>{@code true}: enable notification</li>
     *            <li>{@code false}: enable notification</li>
     *            </ul>
     */
    public abstract void notifyOnDSR(boolean enable);

    /**
     * Expresses interest in receiving notification when the RI (Ring Indicator) bit changes.
     * 
     * This notification is hardware dependent and may not be supported by all implementations.
     * 
     * @param enable
     *            <ul>
     *            <li>{@code true}: enable notification</li>
     *            <li>{@code false}: enable notification</li>
     *            </ul>
     */
    public abstract void notifyOnRingIndicator(boolean enable);

    /**
     * Expresses interest in receiving notification when the CD (Carrier Detect) bit changes.
     * 
     * <p>
     * This notification is hardware dependent and may not be supported by all implementations.
     * </p>
     * 
     * @param enable
     *            <ul>
     *            <li>{@code true}: enable notification</li>
     *            <li>{@code false}: enable notification</li>
     *            </ul>
     */
    public abstract void notifyOnCarrierDetect(boolean enable);

    /**
     * Expresses interest in receiving notification when there is an overrun error.
     * 
     * <p>
     * This notification is hardware dependent and may not be supported by all implementations.
     * </p>
     * 
     * @param enable
     *            <ul>
     *            <li>{@code true}: enable notification</li>
     *            <li>{@code false}: enable notification</li>
     *            </ul>
     */
    public abstract void notifyOnOverrunError(boolean enable);

    /**
     * Expresses interest in receiving notification when there is a parity error.
     * 
     * <p>
     * This notification is hardware dependent and may not be supported by all implementations.
     * </p>
     * 
     * @param enable
     *            <ul>
     *            <li>{@code true}: enable notification</li>
     *            <li>{@code false}: enable notification</li>
     *            </ul>
     */
    public abstract void notifyOnParityError(boolean enable);

    /**
     * Expresses interest in receiving notification when there is a framing error.
     * 
     * <p>
     * This notification is hardware dependent and may not be supported by all implementations.
     * </p>
     * 
     * @param enable
     *            <ul>
     *            <li>{@code true}: enable notification</li>
     *            <li>{@code false}: enable notification</li>
     *            </ul>
     */
    public abstract void notifyOnFramingError(boolean enable);

    /**
     * Expresses interest in receiving notification when there is a break interrupt on the line.
     * 
     * 
     * <p>
     * This notification is hardware dependent and may not be supported by all implementations.
     * </p>
     * 
     * @param enable
     *            <ul>
     *            <li>{@code true}: enable notification</li>
     *            <li>{@code false}: enable notification</li>
     *            </ul>
     */
    public abstract void notifyOnBreakInterrupt(boolean enable);

    /*
     * public abstract void setRcvFifoTrigger(int trigger); deprecated
     */
    /* ---------------------- end of commapi ------------------------ */

    /*
     * can't have static abstract?
     * 
     * public abstract static boolean staticSetDTR( String port, boolean flag ) throws
     * UnsupportedCommOperationException; public abstract static boolean staticSetRTS( String port, boolean flag )
     * throws UnsupportedCommOperationException;
     */

    /**
     * Retrieves the parity error char.
     * 
     * @return the Parity Error Character
     * @throws UnsupportedCommOperationException
     *             if this operation is not supported for the OS by the underlying native library.
     */
    public abstract byte getParityErrorChar() throws UnsupportedCommOperationException;

    /**
     * Sets the parity error char.
     * 
     * @param character
     *            Parity Error Character
     * @return true on success
     * @throws UnsupportedCommOperationException
     *             if this operation is not supported for the OS by the underlying native library.
     * 
     */
    public abstract boolean setParityErrorChar(byte character) throws UnsupportedCommOperationException;

    /**
     * Retrieves the end of input character.
     * 
     * @return the End of Input Character
     * @throws UnsupportedCommOperationException
     *             if this operation is not supported for the OS by the underlying native library.
     * 
     */
    public abstract byte getEndOfInputChar() throws UnsupportedCommOperationException;

    /**
     * Sets the end of input character.
     * 
     * @param character
     *            End Of Input Character
     * @return true on success
     * @throws UnsupportedCommOperationException
     *             if this operation is not supported for the OS by the underlying native library.
     */
    public abstract boolean setEndOfInputChar(byte character) throws UnsupportedCommOperationException;

    /**
     * Sets the UART type.
     * 
     * @param type
     *            String representation of the UART type which may be "none", "8250", "16450", "16550", "16550A",
     *            "16650", "16550V2" or "16750".
     * @param test
     *            boolean flag to determine if the UART should be tested.
     * @return true on success
     * @throws UnsupportedCommOperationException
     *             if this operation is not supported for the OS by the underlying native library.
     * 
     * @deprecated use {@link #setUARTType(UARTType, boolean)} instead.
     */
    @Deprecated
    public abstract boolean setUARTType(String type, boolean test) throws UnsupportedCommOperationException;

    /**
     * 
     * Sets the UART type.
     * 
     * @param type
     *            the uartType
     * @param test
     *            boolean flag to determine if the UART should be tested.
     * @return true on success
     * @throws UnsupportedCommOperationException
     *             if this operation is not supported for the OS by the underlying native library.
     */
    public abstract boolean setUARTType(UARTType type, boolean test) throws UnsupportedCommOperationException;

    /**
     * Retrieve the UART type.
     * 
     * @return type String representation of the UART type which may be "none", "8250", "16450", "16550", "16550A",
     *         "16650", "16550V2" or "16750".
     * @throws UnsupportedCommOperationException
     *             if this operation is not supported for the OS by the underlying native library.
     * @deprecated use {@link #uartType()} instead.
     */
    @Deprecated
    public abstract String getUARTType() throws UnsupportedCommOperationException;

    /**
     * Retrieve the UART type.
     * 
     * @return the UART type
     * @throws UnsupportedCommOperationException
     *             if this operation is not supported for the OS by the underlying native library.
     */
    public abstract UARTType uartType() throws UnsupportedCommOperationException;

    /**
     * Set Baud Base to 38600 on Linux and W32 before using.
     * 
     * @param baudBase
     *            The clock frequency divided by 16. Default BaudBase is 115200.
     * @return true on success
     * @throws UnsupportedCommOperationException
     *             if this operation is not supported for the OS by the underlying native library.
     * @throws IOException
     *             if an IOException occurs.
     */
    public abstract boolean setBaudBase(int baudBase) throws UnsupportedCommOperationException, IOException;

    public abstract int getBaudBase() throws UnsupportedCommOperationException, IOException;

    public abstract boolean setDivisor(int divisor) throws UnsupportedCommOperationException, IOException;

    public abstract int getDivisor() throws UnsupportedCommOperationException, IOException;

    /**
     * Set latency to low.
     * 
     * @return true on success.
     * 
     * @throws UnsupportedCommOperationException
     *             if this operation is not supported for the OS by the underlying native library.
     */
    public abstract boolean setLowLatency() throws UnsupportedCommOperationException;

    /**
     * TODO ??
     * 
     * @return true on success.
     * 
     * @throws UnsupportedCommOperationException
     *             if this operation is not supported for the OS by the underlying native library.
     */
    public abstract boolean getLowLatency() throws UnsupportedCommOperationException;

    public abstract boolean setCallOutHangup(boolean noHup) throws UnsupportedCommOperationException;

    public abstract boolean getCallOutHangup() throws UnsupportedCommOperationException;
}
