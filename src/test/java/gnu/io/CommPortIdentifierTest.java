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
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.junit.Test;

public class CommPortIdentifierTest {
	@Test
	public void testGetPortIdentifiers() throws Exception {
		List<CommPortIdentifier> l = getPortIdentifiers();
		assertFalse("has ports", l.isEmpty());
	}

	@Test(expected = NoSuchPortException.class)
	public void testGetPortIdentifier() throws Exception {
		List<CommPortIdentifier> l = getPortIdentifiers();
		CommPortIdentifier first = l.get(0);
		CommPortIdentifier last = l.get(l.size() - 1);
		// first find by name
		CommPortIdentifier p = CommPortIdentifier.getPortIdentifier(first.getName());

		assertEquals("first found", p, first);

		p = CommPortIdentifier.getPortIdentifier(last.getName());

		assertEquals("last found", p, last);
		// now the non-existent case

		p = CommPortIdentifier.getPortIdentifier("wuzziwuzz");
	}

	private List<CommPortIdentifier> getPortIdentifiers() {
		Enumeration<CommPortIdentifier> e = CommPortIdentifier.getPortIdentifiers();
		List<CommPortIdentifier> l = new ArrayList<CommPortIdentifier>();

		while (e.hasMoreElements()) {
			l.add(e.nextElement());
		}
		return l;
	}
}
