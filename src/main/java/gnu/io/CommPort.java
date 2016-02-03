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
import java.io.InputStream;
import java.io.OutputStream;

public abstract class CommPort extends Object {
	protected String name;
	private final static boolean debug = false;

	public abstract void enableReceiveFraming(int f) throws UnsupportedCommOperationException;

	public abstract void disableReceiveFraming();

	public abstract boolean isReceiveFramingEnabled();

	public abstract int getReceiveFramingByte();

	public abstract void disableReceiveTimeout();

	public abstract void enableReceiveTimeout(int time) throws UnsupportedCommOperationException;

	public abstract boolean isReceiveTimeoutEnabled();

	public abstract int getReceiveTimeout();

	public abstract void enableReceiveThreshold(int thresh) throws UnsupportedCommOperationException;

	public abstract void disableReceiveThreshold();

	public abstract int getReceiveThreshold();

	public abstract boolean isReceiveThresholdEnabled();

	public abstract void setInputBufferSize(int size);

	public abstract int getInputBufferSize();

	public abstract void setOutputBufferSize(int size);

	public abstract int getOutputBufferSize();

	public void close() {
		if (debug) {
			System.out.println("CommPort:close()");
		}

		try {
			CommPortIdentifier cp = CommPortIdentifier.getPortIdentifier(this);
			if (cp != null) {
				CommPortIdentifier.getPortIdentifier(this).internalClosePort();
			}
		} catch (NoSuchPortException e) {
		}
	};

	public abstract InputStream getInputStream() throws IOException;

	public abstract OutputStream getOutputStream() throws IOException;

	public String getName() {
		if (debug) {
			System.out.println("CommPort:getName()");
		}
		return (name);
	}

	@Override
	public String toString() {
		if (debug) {
			System.out.println("CommPort:toString()");
		}
		return (name);
	}
}
