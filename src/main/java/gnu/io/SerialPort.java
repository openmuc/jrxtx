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

	public enum DataBits {
		/**
		 * 5 data bit format.
		 */
		DATABITS_5(
				5),

		/**
		 * 6 data bit format.
		 */
		DATABITS_6(
				6),

		/**
		 * 7 data bit format.
		 */
		DATABITS_7(
				7),
		/**
		 * 8 data bit format.
		 */
		DATABITS_8(
				8);

		private int value;

		private DataBits(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}
	}

	public enum Parity {
		/**
		 * No parity bit.
		 */
		NONE(
				0),

		/**
		 * ODD parity scheme.
		 */
		ODD(
				1),
		/**
		 * EVEN parity scheme.
		 */
		EVEN(
				2),
		/**
		 * MARK parity scheme.
		 */
		MARK(
				3);

		private int value;

		private Parity(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}

	}

	public enum FlowControl {
		/**
		 * Flow control off.
		 */
		NONE(
				0),
		/**
		 * RTS/CTS flow control on input.
		 */
		RTSCTS_IN(
				1),
		/**
		 * RTS/CTS flow control on output.
		 */
		RTSCTS_OUT(
				2),
		/**
		 * XON/XOFF flow control on input.
		 */
		XONXOFF_IN(
				4),
		/**
		 * XON/XOFF flow control on output.
		 */
		XONXOFF_OUT(
				8);

		private int value;

		private FlowControl(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}

	}

	public enum StopBits {
		/**
		 * Number of STOP bits - 1.
		 */
		STOPBITS_1(
				1),
		/**
		 * Number of STOP bits - 2.
		 */
		STOPBITS_2(
				2),
		/**
		 * Number of STOP bits - 1-1/2.
		 */
		STOPBITS_1_5(
				3);
		private int value;

		private StopBits(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}
	}

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
	 */
	public abstract int getDataBits();

	/**
	 * Gets the currently defined stop bits.
	 * 
	 * @return integer that can be equal to {@link STOPBITS_1}, {@link STOPBITS_2}, or {@link STOPBITS_1_5}
	 */
	public abstract int getStopBits();

	public abstract int getParity();

	public abstract void setFlowControlMode(int flowcontrol) throws UnsupportedCommOperationException;

	public abstract int getFlowControlMode();

	public abstract boolean isDTR();

	public abstract void setDTR(boolean state);

	public abstract void setRTS(boolean state);

	/**
	 * Retrieving the CTS (<u>C</u>lear <u>T</u>o <u>S</u>end) flag.
	 * 
	 * @return true if clear to send.
	 */
	public abstract boolean isCTS();

	/**
	 * Retrieving the DSR (<u>D</u>ata <u>S</u>et <u>R</u>eady) flag.
	 * 
	 * @return true if data set ready.
	 */
	public abstract boolean isDSR();

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
	 * Register a listener to the serial port.
	 * 
	 * @param listener
	 *            the serial port event listener.
	 * @throws TooManyListenersException
	 *             if a listener is already registered.
	 */
	public abstract void addEventListener(SerialPortEventListener listener) throws TooManyListenersException;

	public abstract void removeEventListener();

	public abstract void notifyOnDataAvailable(boolean enable);

	public abstract void notifyOnOutputEmpty(boolean enable);

	public abstract void notifyOnCTS(boolean enable);

	public abstract void notifyOnDSR(boolean enable);

	public abstract void notifyOnRingIndicator(boolean enable);

	public abstract void notifyOnCarrierDetect(boolean enable);

	public abstract void notifyOnOverrunError(boolean enable);

	public abstract void notifyOnParityError(boolean enable);

	public abstract void notifyOnFramingError(boolean enable);

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
	 * @param b
	 *            Parity Error Character
	 * @return true on success
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 * 
	 */
	public abstract boolean setParityErrorChar(byte b) throws UnsupportedCommOperationException;

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
	 * @param b
	 *            End Of Input Character
	 * @return true on success
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 */
	public abstract boolean setEndOfInputChar(byte b) throws UnsupportedCommOperationException;

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
	public abstract boolean setUARTType(String type, boolean test) throws UnsupportedCommOperationException;

	/**
	 * Retrieve the UART type.
	 * 
	 * @return type String representation of the UART type which may be "none", "8250", "16450", "16550", "16550A",
	 *         "16650", "16550V2" or "16750".
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 */
	public abstract String getUARTType() throws UnsupportedCommOperationException;

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
	public abstract boolean setBaudBase(int BaudBase) throws UnsupportedCommOperationException, IOException;

	public abstract int getBaudBase() throws UnsupportedCommOperationException, IOException;

	public abstract boolean setDivisor(int Divisor) throws UnsupportedCommOperationException, IOException;

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

	public abstract boolean setCallOutHangup(boolean NoHup) throws UnsupportedCommOperationException;

	public abstract boolean getCallOutHangup() throws UnsupportedCommOperationException;
}
