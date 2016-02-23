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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor({ "gnu.io.CommPortIdentifier", "gnu.io.RXTXCommDriver" })
public class RXTXCommDriverTest {

	private String fOldPropSerial;
	private String fOldPropParallel;
	private String fPathSep;

	@Before
	public void setUp() {
		fPathSep = System.getProperty("path.separator", ":");
		fOldPropSerial = System.getProperty("gnu.io.rxtx.SerialPorts");
		fOldPropParallel = System.getProperty("gnu.io.rxtx.ParallelPorts");
	}

	@Ignore
	@After
	public void tearDown() {
		System.setProperty("gnu.io.rxtx.SerialPorts", fOldPropSerial == null ? "" : fOldPropSerial);
		System.setProperty("gnu.io.rxtx.ParallelPorts", fOldPropParallel == null ? "" : fOldPropParallel);
	}

	/*
	 * Check that ports can be specified (i.e. removed) by means of a Java Property
	 */
	@Ignore
	@Test
	public void testRegisterSpecifiedPorts() throws Exception {
		// First, find all serial ports
		List<String> serialPorts = new ArrayList<String>();
		Enumeration<CommPortIdentifier> e = CommPortIdentifier.getPortIdentifiers();
		while (e.hasMoreElements()) {
			CommPortIdentifier port = e.nextElement();
			if (port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				serialPorts.add(port.getName());
			}
		}
		System.out.println(serialPorts);
		// Now, get rid of the first one
		StringBuffer buf = new StringBuffer();
		for (int i = 1; i < serialPorts.size(); i++) {
			buf.append(serialPorts.get(i));
			buf.append(fPathSep);
		}
		System.setProperty("gnu.io.rxtx.SerialPorts", buf.toString());
		e = CommPortIdentifier.getPortIdentifiers();
		int nNew = 0;
		while (e.hasMoreElements()) {
			CommPortIdentifier port = e.nextElement();
			if (port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				nNew++;
				assertTrue("hasPort", serialPorts.contains(port.getName()));
			}
		}
		assertEquals("1 port removed", serialPorts.size() - 1, nNew);
	}

}
