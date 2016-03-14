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

    public enum PortType implements CommPortEnum {
        /**
         * RS232 Port
         */
        SERIAL(1),
        /**
         * Parallel Port
         */
        PARALLEL(2),
        /**
         * I2C Port
         */
        I2C(3),
        /**
         * RS485 Port
         */
        RS485(4),
        /**
         * Raw Port
         */
        RAW(5);

        private int value;

        private PortType(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

    }

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

    CommPortIdentifier(String portName, CommPort commPort, int portType, CommDriver commDriver) {
        this.portName = portName;
        this.commport = commPort;
        this.PortType = portType;
        this.next = null;
        this.rxtxDriver = commDriver;

    }

    /**
     * Adds {@link #portName} to the list of ports.
     * 
     * @param appname
     *            The name of the port being added
     * @param portType
     *            The type of the port being added
     * @param driver
     *            The driver representing the port being added
     * 
     * @deprecated use {@link #addPortName(String, PortType, CommDriver)} instead.
     */
    @Deprecated
    public static void addPortName(String appname, int portType, CommDriver driver) {
        addIdentifierToList(new CommPortIdentifier(appname, null, portType, driver));
    }

    /**
     * 
     * Adds {@link #portName} to the list of ports.
     * 
     * @param appname
     *            The name of the port being added
     * @param portType
     *            The type of the port being added
     * @param driver
     *            The driver representing the port being added
     * 
     * @see CommPort
     */
    public static void addPortName(String appname, PortType portType, CommDriver driver) {
        addIdentifierToList(new CommPortIdentifier(appname, null, portType.value(), driver));
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

    /**
     * Registers an interested application so that it can receive notification of changes in port ownership. This
     * includes notification of the following events:
     * 
     * <ul>
     * <li>{@link CommPortOwnershipListener#PORT_OWNED}: Port became owned</li>
     * <li>{@link CommPortOwnershipListener#PORT_UNOWNED}: Port became unowned</li>
     * <li>{@link CommPortOwnershipListener#PORT_OWNERSHIP_REQUESTED}: If the application owns this port and is willing
     * to give up ownership, then it should call close now.</li>
     * </ul>
     * 
     * The ownershipChange method of the listener registered using addPortOwnershipListener will be called with one of
     * the above events.
     * 
     * @param listener
     *            {@link CommPortOwnershipListener} callback object
     */
    public void addPortOwnershipListener(CommPortOwnershipListener listener) {
        /* is the Vector instantiated? */

        if (ownershipListener == null) {
            ownershipListener = new Vector<CommPortOwnershipListener>();
        }

        /* is the ownership listener already in the list? */

        if (!ownershipListener.contains(listener)) {
            ownershipListener.addElement(listener);
        }
    }

    /**
     * Returns the owner of the port.
     * 
     * @return current owner of the port.
     */
    public String getCurrentOwner() {
        return (owner);
    }

    /**
     * Returns the name of the port.
     * 
     * @return the name of the port
     */
    public String getName() {
        return (portName);
    }

    /**
     * Obtains the CommPortIdentifier object corresponding to a port that has already been opened by the application.
     * 
     * @param portName
     *            name of the port to open
     * @return {@link CommPortIdentifier} object
     * @throws NoSuchPortException
     *             if the port object is invalid
     */
    public static CommPortIdentifier getPortIdentifier(String portName) throws NoSuchPortException {
        CommPortIdentifier index;

        synchronized (sync) {
            index = CommPortIndex;
            while (index != null && !index.portName.equals(portName)) {
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
                while (index != null && !index.portName.equals(portName)) {
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

    /**
     * Obtains the CommPortIdentifier object corresponding to a port that has already been opened by the application.
     * 
     * @param port
     *            a {@link CommPort} object obtained from a previous open
     * @return {@link CommPortIdentifier} object
     * @throws NoSuchPortException
     *             if the port object is invalid
     */
    public static CommPortIdentifier getPortIdentifier(CommPort port) throws NoSuchPortException {
        CommPortIdentifier c;
        synchronized (sync) {
            c = CommPortIndex;
            while (c != null && c.commport != port) {
                c = c.next;
            }
        }
        if (c != null) {
            return (c);
        }

        throw new NoSuchPortException();
    }

    /**
     * The CommPortOwnershipListener object that was previously registered using addPortOwnershipListener
     * 
     * @return {@link Enumeration} that can be used to enumerate all the ports known to the system
     */
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

    /**
     * Returns the port type.
     * 
     * @return PORT_SERIAL, PORT_PARALLEL, PORT_I2C, PORT_RS485, PORT_RAW
     * 
     * @deprecated use {@link #portType()} instead.
     */
    @Deprecated
    public int getPortType() {
        return (PortType);
    }

    /**
     * Returns the port type.
     * 
     * @return the port type.
     */
    public PortType portType() {
        return Enu.enumFor(this.PortType, PortType.class);
    }

    /**
     * Checks whether the port is owned.
     * 
     * @return {@code true} if port is owned by an application, {@code false} if port is not owned.
     */
    public synchronized boolean isCurrentlyOwned() {
        return (!available);
    }

    /**
     * Opens the communications port using a FileDescriptor object on platforms that support this technique.
     * 
     * @param fileDescriptor
     *            The {@link FileDescriptor} associated with this CommPort.
     * @return
     * @throws UnsupportedCommOperationException
     *             on platforms which do not support this functionality.
     * 
     * @deprecated since this function was never implemented.
     */
    @Deprecated
    public synchronized CommPort open(FileDescriptor fileDescriptor) throws UnsupportedCommOperationException {
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

    /**
     * Opens the communications port. open obtains exclusive ownership of the port. If the port is owned by some other
     * application, a PORT_OWNERSHIP_REQUESTED event is propagated using the CommPortOwnershipListener event mechanism.
     * If the application that owns the port calls close during the event processing, then this open will succeed. There
     * is one InputStream and one OutputStream associated with each port. After a port is opened with open, then all
     * calls to getInputStream will return the same stream object until close is called.
     * 
     * @param appname
     *            Name of application making this call. This name will become the owner of the port. Useful when
     *            resolving ownership contention.
     * @param timeout
     *            Time in milliseconds to block waiting for port open.
     * @return the {@link CommPort}
     * @throws PortInUseException
     *             if the port is in use by some other application that is not willing to relinquish ownership
     */
    public CommPort open(String appname, int timeout) throws PortInUseException {
        boolean isAvailable;
        synchronized (this) {
            isAvailable = this.available;
            if (isAvailable) {
                // assume ownership inside the synchronized block
                this.available = false;
                this.owner = appname;
            }
        }
        if (!isAvailable) {
            long waitTimeEnd = System.currentTimeMillis() + timeout;
            // timeout the ownership event outside the synchronized block
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
                    this.owner = appname;
                }
            }
        }
        if (!isAvailable) {
            throw new gnu.io.PortInUseException(getCurrentOwner());
        }
        // At this point, the CommPortIdentifier is owned by us.
        try {
            if (commport == null) {
                commport = rxtxDriver.commPort(portName,
                        Enu.enumFor(this.PortType, gnu.io.CommPortIdentifier.PortType.class));
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

    /**
     * Unregisters a {@link CommPortOwnershipListener} registered using
     * {@link #addPortOwnershipListener(CommPortOwnershipListener)}
     * 
     * @param listener
     *            The CommPortOwnershipListener object that was previously registered using addPortOwnershipListener
     */
    public void removePortOwnershipListener(CommPortOwnershipListener listener) {
        /* why is this called twice? */
        if (ownershipListener != null) {
            ownershipListener.removeElement(listener);
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
