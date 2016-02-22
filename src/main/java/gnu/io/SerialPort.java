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

public interface SerialPort extends CommPort {
	int DATABITS_5 = 5;
	int DATABITS_6 = 6;
	int DATABITS_7 = 7;
	int DATABITS_8 = 8;
	int PARITY_NONE = 0;
	int PARITY_ODD = 1;
	int PARITY_EVEN = 2;
	int PARITY_MARK = 3;
	int PARITY_SPACE = 4;
	int STOPBITS_1 = 1;
	int STOPBITS_2 = 2;
	int STOPBITS_1_5 = 3;
	int FLOWCONTROL_NONE = 0;
	int FLOWCONTROL_RTSCTS_IN = 1;
	int FLOWCONTROL_RTSCTS_OUT = 2;
	int FLOWCONTROL_XONXOFF_IN = 4;
	int FLOWCONTROL_XONXOFF_OUT = 8;

	void setSerialPortParams(int b, int d, int s, int p) throws UnsupportedCommOperationException;

	int getBaudRate();

	int getDataBits();

	int getStopBits();

	int getParity();

	void setFlowControlMode(int flowcontrol) throws UnsupportedCommOperationException;

	int getFlowControlMode();

	boolean isDTR();

	void setDTR(boolean state);

	void setRTS(boolean state);

	boolean isCTS();

	boolean isDSR();

	boolean isCD();

	boolean isRI();

	boolean isRTS();

	void sendBreak(int duration);

	/**
	 * Register a listener to the serial port.
	 * 
	 * @param listener
	 *            the serial port event listener.
	 * @throws TooManyListenersException
	 *             if a listener is already registered.
	 */
	void addEventListener(SerialPortEventListener listener) throws TooManyListenersException;

	void removeEventListener();

	void notifyOnDataAvailable(boolean enable);

	void notifyOnOutputEmpty(boolean enable);

	void notifyOnCTS(boolean enable);

	void notifyOnDSR(boolean enable);

	void notifyOnRingIndicator(boolean enable);

	void notifyOnCarrierDetect(boolean enable);

	void notifyOnOverrunError(boolean enable);

	void notifyOnParityError(boolean enable);

	void notifyOnFramingError(boolean enable);

	void notifyOnBreakInterrupt(boolean enable);
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
	byte getParityErrorChar() throws UnsupportedCommOperationException;

	/**
	 * Sets the parity error char.
	 * 
	 * @param b
	 *            Parity Error Character
	 * @return true on success
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 * 
	 */
	boolean setParityErrorChar(byte b) throws UnsupportedCommOperationException;

	/**
	 * Retrieves the end of input character.
	 * 
	 * @return the End of Input Character
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 * 
	 */
	byte getEndOfInputChar() throws UnsupportedCommOperationException;

	/**
	 * Sets the end of input character.
	 * 
	 * @param b
	 *            End Of Input Character
	 * @return true on success
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 */
	boolean setEndOfInputChar(byte b) throws UnsupportedCommOperationException;

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
	 */
	boolean setUARTType(String type, boolean test) throws UnsupportedCommOperationException;

	/**
	 * Retrieve the UART type.
	 * 
	 * @return type String representation of the UART type which may be "none", "8250", "16450", "16550", "16550A",
	 *         "16650", "16550V2" or "16750".
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 */
	String getUARTType() throws UnsupportedCommOperationException;

	/**
	 * Set Baud Base to 38600 on Linux and W32 before using.
	 * 
	 * @param BaudBase
	 *            The clock frequency divided by 16. Default BaudBase is 115200.
	 * @return true on success
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 * @throws IOException
	 *             if an IOException occurs.
	 */
	boolean setBaudBase(int BaudBase) throws UnsupportedCommOperationException, IOException;

	int getBaudBase() throws UnsupportedCommOperationException, IOException;

	boolean setDivisor(int Divisor) throws UnsupportedCommOperationException, IOException;

	int getDivisor() throws UnsupportedCommOperationException, IOException;

	/**
	 * Set latency to low.
	 * 
	 * @return true on success.
	 * 
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 */
	boolean setLowLatency() throws UnsupportedCommOperationException;

	/**
	 * TODO ??
	 * 
	 * @return true on success.
	 * 
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 */
	boolean getLowLatency() throws UnsupportedCommOperationException;

	boolean setCallOutHangup(boolean NoHup) throws UnsupportedCommOperationException;

	boolean getCallOutHangup() throws UnsupportedCommOperationException;
}
