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

import java.util.EventObject;

/* Non functional contact taj@qbang.org for details */

public class I2CPortEvent extends EventObject {
	public static final int DATA_AVAILABLE = 1;
	public static final int OUTPUT_BUFFER_EMPTY = 2;
	public static final int CTS = 3;
	public static final int DSR = 4;
	public static final int RI = 5;
	public static final int CD = 6;
	public static final int OE = 7;
	public static final int PE = 8;
	public static final int FE = 9;
	public static final int BI = 10;

	private final boolean OldValue;
	private final boolean NewValue;
	private final int eventType;
	/* public int eventType =0; depricated */

	public I2CPortEvent(I2CPort srcport, int eventtype, boolean oldvalue, boolean newvalue) {
		super(srcport);
		OldValue = oldvalue;
		NewValue = newvalue;
		eventType = eventtype;
	}

	public int getEventType() {
		return (eventType);
	}

	public boolean getNewValue() {
		return (NewValue);
	}

	public boolean getOldValue() {
		return (OldValue);
	}
}
