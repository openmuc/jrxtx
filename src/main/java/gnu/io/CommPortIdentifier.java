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

import java.io.FileDescriptor;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class CommPortIdentifier /* extends Vector? */ {
	public static final int PORT_SERIAL = 1; // rs232 Port
	public static final int PORT_PARALLEL = 2; // Parallel Port
	public static final int PORT_I2C = 3; // i2c Port
	public static final int PORT_RS485 = 4; // rs485 Port
	public static final int PORT_RAW = 5; // Raw Port

	static CommPortIdentifier CommPortIndex;
	static Object sync;

	private final String portName;
	private String owner;

	private CommPort commport;
	private CommDriver rxtxDriver;
	private boolean available = true;
	private final int PortType;

	Vector<CommPortOwnershipListener> ownershipListener;
	CommPortIdentifier next;

	/*------------------------------------------------------------------------------
		static {}   aka initialization
		accept:       -
		perform:      load the rxtx driver
		return:       -
		exceptions:   Throwable
		comments:     static block to initialize the class
	------------------------------------------------------------------------------*/
	// initialization only done once....
	static {
		sync = new Object();
		try {
			// CommDriver rxtxDriver = new RXTXCommDriver();// (CommDriver)
			// Class.forName("gnu.io.RXTXCommDriver").newInstance();
			new RXTXCommDriver().initialize();
		} catch (Throwable e) {
			System.err.println(e + " thrown while loading " + "gnu.io.RXTXCommDriver");
		}

		System.loadLibrary("rxtxSerial");
	}

	CommPortIdentifier(String pn, CommPort cp, int pt, CommDriver driver) {
		portName = pn;
		commport = cp;
		PortType = pt;
		next = null;
		rxtxDriver = driver;

	}

	/*------------------------------------------------------------------------------
		addPortName()
		accept:         Name of the port s, Port type, 
	                    reverence to RXTXCommDriver.
		perform:        place a new CommPortIdentifier in the linked list
		return: 	none.
		exceptions:     none.
		comments:
	------------------------------------------------------------------------------*/
	public static void addPortName(String s, int type, CommDriver c) {
		addIdentifierToList(new CommPortIdentifier(s, null, type, c));
	}

	/*------------------------------------------------------------------------------
		AddIdentifierToList()
		accept:        The cpi to add to the list. 
		perform:        
		return: 	
		exceptions:    
		comments:
	------------------------------------------------------------------------------*/
	private static void addIdentifierToList(CommPortIdentifier cpi) {
		synchronized (sync) {
			if (CommPortIndex == null) {
				CommPortIndex = cpi;
			}
			else {
				CommPortIdentifier index = CommPortIndex;
				while (index.next != null) {
					index = index.next;
				}
				index.next = cpi;
			}
		}
	}

	/*------------------------------------------------------------------------------
		addPortOwnershipListener()
		accept:
		perform:
		return:
		exceptions:
		comments:   
	------------------------------------------------------------------------------*/
	public void addPortOwnershipListener(CommPortOwnershipListener c) {
		/* is the Vector instantiated? */

		if (ownershipListener == null) {
			ownershipListener = new Vector<CommPortOwnershipListener>();
		}

		/* is the ownership listener already in the list? */

		if (!ownershipListener.contains(c)) {
			ownershipListener.addElement(c);
		}
	}

	/*------------------------------------------------------------------------------
		getCurrentOwner()
		accept:
		perform:
		return:
		exceptions:
		comments:    
	------------------------------------------------------------------------------*/
	public String getCurrentOwner() {
		return (owner);
	}

	/*------------------------------------------------------------------------------
		getName()
		accept:
		perform:
		return:
		exceptions:
		comments:
	------------------------------------------------------------------------------*/
	public String getName() {
		return (portName);
	}

	/*------------------------------------------------------------------------------
		getPortIdentifier()
		accept:
		perform:
		return:
		exceptions:
		comments:   
	------------------------------------------------------------------------------*/
	static public CommPortIdentifier getPortIdentifier(String s) throws NoSuchPortException {
		CommPortIdentifier index;

		synchronized (sync) {
			index = CommPortIndex;
			while (index != null && !index.portName.equals(s)) {
				index = index.next;
			}
			if (index == null) {
				/*
				 * This may slow things down but if you pass the string for the port after a device is plugged in, you
				 * can find it now.
				 * 
				 * http://bugzilla.qbang.org/show_bug.cgi?id=48
				 */
				getPortIdentifiers();
				index = CommPortIndex;
				while (index != null && !index.portName.equals(s)) {
					index = index.next;
				}
			}
		}
		if (index != null) {
			return index;
		}
		else {
			throw new NoSuchPortException();
		}
	}

	/*------------------------------------------------------------------------------
		getPortIdentifier()
		accept:
		perform:
		return:
		exceptions:
		comments:    
	------------------------------------------------------------------------------*/
	static public CommPortIdentifier getPortIdentifier(CommPort p) throws NoSuchPortException {
		CommPortIdentifier c;
		synchronized (sync) {
			c = CommPortIndex;
			while (c != null && c.commport != p) {
				c = c.next;
			}
		}
		if (c != null) {
			return (c);
		}

		throw new NoSuchPortException();
	}

	/*------------------------------------------------------------------------------
		getPortIdentifiers()
		accept:
		perform:
		return:
		exceptions:
		comments:
	------------------------------------------------------------------------------*/
	public static Enumeration<CommPortIdentifier> getPortIdentifiers() {
		// Do not allow anybody get any ports while we are re-initializing
		// because the CommPortIndex points to invalid instances during that time
		synchronized (sync) {
			// Remember old ports in order to restore them for ownership events later
			Map<String, CommPortIdentifier> oldPorts = new HashMap<String, CommPortIdentifier>();
			CommPortIdentifier p = CommPortIndex;
			while (p != null) {
				oldPorts.put(p.portName, p);
				p = p.next;
			}
			CommPortIndex = null;
			try {
				// Initialize RXTX: This leads to detecting all ports
				// and writing them into our CommPortIndex through our method
				// {@link #addPortName(java.lang.String, int, gnu.io.CommDriver)}
				// This works while lock on Sync is held
				CommDriver rxtxDriver = new RXTXCommDriver();
				rxtxDriver.initialize();
				// Restore old CommPortIdentifier objects where possible,
				// in order to support proper ownership event handling.
				// Clients might still have references to old identifiers!
				CommPortIdentifier curPort = CommPortIndex;
				CommPortIdentifier prevPort = null;
				while (curPort != null) {
					CommPortIdentifier matchingOldPort = oldPorts.get(curPort.portName);
					if (matchingOldPort != null && matchingOldPort.PortType == curPort.PortType) {
						// replace new port by old one
						matchingOldPort.rxtxDriver = curPort.rxtxDriver;
						matchingOldPort.next = curPort.next;
						if (prevPort == null) {
							CommPortIndex = matchingOldPort;
						}
						else {
							prevPort.next = matchingOldPort;
						}
						prevPort = matchingOldPort;
					}
					else {
						prevPort = curPort;
					}
					curPort = curPort.next;
				}
			} catch (Throwable e) {
				System.err.println(e + " thrown while loading " + "gnu.io.RXTXCommDriver");
				System.err.flush();
			}
		}
		return new CommPortEnumerator();
	}

	/*------------------------------------------------------------------------------
		getPortType()
		accept:
		perform:
		return:
		exceptions:
		comments:
	------------------------------------------------------------------------------*/
	public int getPortType() {
		return (PortType);
	}

	/*------------------------------------------------------------------------------
		isCurrentlyOwned()
		accept:
		perform:
		return:
		exceptions:
		comments:    
	------------------------------------------------------------------------------*/
	public synchronized boolean isCurrentlyOwned() {
		return (!available);
	}

	/*------------------------------------------------------------------------------
		open()
		accept:
		perform:
		return:
		exceptions:
		comments:
	------------------------------------------------------------------------------*/
	public synchronized CommPort open(FileDescriptor f) throws UnsupportedCommOperationException {
		throw new UnsupportedCommOperationException();
	}

	private native String native_psmisc_report_owner(String PortName);

	/*------------------------------------------------------------------------------
		open()
		accept:      application making the call and milliseconds to block
	                 during open.
		perform:     open the port if possible
		return:      CommPort if successful
		exceptions:  PortInUseException if in use.
		comments:
	------------------------------------------------------------------------------*/
	private boolean HideOwnerEvents;

	public CommPort open(String TheOwner, int i) throws gnu.io.PortInUseException {
		boolean isAvailable;
		synchronized (this) {
			isAvailable = this.available;
			if (isAvailable) {
				// assume ownership inside the synchronized block
				this.available = false;
				this.owner = TheOwner;
			}
		}
		if (!isAvailable) {
			long waitTimeEnd = System.currentTimeMillis() + i;
			// fire the ownership event outside the synchronized block
			fireOwnershipEvent(CommPortOwnershipListener.PORT_OWNERSHIP_REQUESTED);
			long waitTimeCurr;
			synchronized (this) {
				while (!available && (waitTimeCurr = System.currentTimeMillis()) < waitTimeEnd) {
					try {
						wait(waitTimeEnd - waitTimeCurr);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					}
				}
				isAvailable = this.available;
				if (isAvailable) {
					// assume ownership inside the synchronized block
					this.available = false;
					this.owner = TheOwner;
				}
			}
		}
		if (!isAvailable) {
			throw new gnu.io.PortInUseException(getCurrentOwner());
		}
		// At this point, the CommPortIdentifier is owned by us.
		try {
			if (commport == null) {
				commport = rxtxDriver.getCommPort(portName, PortType);
			}
			if (commport != null) {
				fireOwnershipEvent(CommPortOwnershipListener.PORT_OWNED);
				return commport;
			}
			else {
				throw new gnu.io.PortInUseException(native_psmisc_report_owner(portName));
			}
		} finally {
			if (commport == null) {
				// something went wrong reserving the commport -> unown the port
				synchronized (this) {
					this.available = true;
					this.owner = null;
				}
			}
		}
	}

	/*------------------------------------------------------------------------------
		removePortOwnership()
		accept:
		perform:
		return:
		exceptions:
		comments:
	------------------------------------------------------------------------------*/
	public void removePortOwnershipListener(CommPortOwnershipListener c) {
		/* why is this called twice? */
		if (ownershipListener != null) {
			ownershipListener.removeElement(c);
		}
	}

	/*------------------------------------------------------------------------------
		internalClosePort()
		accept:       None
		perform:      clean up the Ownership information and send the event
		return:       None
		exceptions:   None
		comments:     None
	------------------------------------------------------------------------------*/
	void internalClosePort() {
		synchronized (this) {
			owner = null;
			available = true;
			commport = null;
			/* this tosses null pointer?? */
			notifyAll();
		}
		fireOwnershipEvent(CommPortOwnershipListener.PORT_UNOWNED);
	}

	/*------------------------------------------------------------------------------
		fireOwnershipEvent()
		accept:
		perform:
		return:
		exceptions:
		comments:
	------------------------------------------------------------------------------*/
	void fireOwnershipEvent(int eventType) {
		if (ownershipListener != null) {
			for (CommPortOwnershipListener ownershipListeners : ownershipListener) {
				ownershipListeners.ownershipChange(eventType);
			}
		}
	}
}
