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

import gnu.io.CommPortIdentifier.PortType;

/**
 * Part of the loadable device driver interface. CommDriver should not be used by application-level programs.
 */
public interface CommDriver {
	/**
	 * {@link #getCommPort(String, int)} will be called by {@link CommPortIdentifier#open(String, int)} {@code portName}
	 * is a string that was registered earlier using the
	 * {@link CommPortIdentifier#addPortName(String, gnu.io.CommPortIdentifier.PortType, CommDriver)} method.
	 * 
	 * @param portName
	 *            port name
	 * @param portType
	 *            port type
	 * @return the {@link CommPort}
	 * @deprecated use {@link #commPort(String, PortType)} instead.
	 */
	@Deprecated
	CommPort getCommPort(String portName, int portType);

	/**
	 * {@link #getCommPort(String, int)} will be called by {@link CommPortIdentifier#open(String, int)} {@code portName}
	 * is a string that was registered earlier using the
	 * {@link CommPortIdentifier#addPortName(String, gnu.io.CommPortIdentifier.PortType, CommDriver)} method.
	 * 
	 * @param portName
	 *            port name
	 * @param portType
	 *            port type
	 * @return the {@link CommPort}
	 */
	CommPort commPort(String portName, PortType portType);

	/**
	 * initialize() will be called by the CommPortIdentifier's static initializer. The responsibility of this method is:
	 * 1) Ensure that that the hardware is present. 2) Load any required native libraries. 3) Register the port names
	 * with the CommPortIdentifier.
	 */
	void initialize();
}
