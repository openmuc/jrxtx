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

import java.util.Enumeration;

class CommPortEnumerator implements Enumeration<CommPortIdentifier> {
	private CommPortIdentifier index;
	private final static boolean debug = false;

	static {
		if (debug) {
			System.out.println("CommPortEnumerator:{}");
		}
	}

	CommPortEnumerator() {
	}

	/*------------------------------------------------------------------------------
	    nextElement()
	    accept:
	    perform:
	    return:
	    exceptions:
	    comments:
	------------------------------------------------------------------------------*/
	public CommPortIdentifier nextElement() {
		if (debug) {
			System.out.println("CommPortEnumerator:nextElement()");
		}
		synchronized (CommPortIdentifier.sync) {
			if (index != null) {
				index = index.next;
			}
			else {
				index = CommPortIdentifier.CommPortIndex;
			}
			return index;
		}
	}

	/*------------------------------------------------------------------------------
	    hasMoreElements()
	    accept:
	    perform:
	    return:
	    exceptions:
	    comments:
	------------------------------------------------------------------------------*/
	public boolean hasMoreElements() {
		if (debug) {
			System.out.println(
					"CommPortEnumerator:hasMoreElements() " + CommPortIdentifier.CommPortIndex == null ? false : true);
		}
		synchronized (CommPortIdentifier.sync) {
			if (index != null) {
				return index.next == null ? false : true;
			}
			else {
				return CommPortIdentifier.CommPortIndex == null ? false : true;
			}
		}
	}
}
