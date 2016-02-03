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

import java.util.TooManyListenersException;

/* Non functional contact tjarvi@qbang.org for details */

abstract class RS485Port extends CommPort {
	public static final int DATABITS_5 = 5;
	public static final int DATABITS_6 = 6;
	public static final int DATABITS_7 = 7;
	public static final int DATABITS_8 = 8;
	public static final int PARITY_NONE = 0;
	public static final int PARITY_ODD = 1;
	public static final int PARITY_EVEN = 2;
	public static final int PARITY_MARK = 3;
	public static final int PARITY_SPACE = 4;
	public static final int STOPBITS_1 = 1;
	public static final int STOPBITS_1_5 = 0; // wrong
	public static final int STOPBITS_2 = 2;
	public static final int FLOWCONTROL_NONE = 0;
	public static final int FLOWCONTROL_RTSCTS_IN = 1;
	public static final int FLOWCONTROL_RTSCTS_OUT = 2;
	public static final int FLOWCONTROL_XONXOFF_IN = 4;
	public static final int FLOWCONTROL_XONXOFF_OUT = 8;

	public abstract void setRS485PortParams(int b, int d, int s, int p) throws UnsupportedCommOperationException;

	public abstract int getBaudRate();

	public abstract int getDataBits();

	public abstract int getStopBits();

	public abstract int getParity();

	public abstract void setFlowControlMode(int flowcontrol) throws UnsupportedCommOperationException;

	public abstract int getFlowControlMode();

	public abstract boolean isDTR();

	public abstract void setDTR(boolean state);

	public abstract void setRTS(boolean state);

	public abstract boolean isCTS();

	public abstract boolean isDSR();

	public abstract boolean isCD();

	public abstract boolean isRI();

	public abstract boolean isRTS();

	public abstract void sendBreak(int duration);

	public abstract void addEventListener(RS485PortEventListener lsnr) throws TooManyListenersException;

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

}
