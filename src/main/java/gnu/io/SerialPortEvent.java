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

public class SerialPortEvent extends EventObject {
    /**
     * @deprecated use {@link EventType#DATA_AVAILABLE} instead
     */
    @Deprecated
    public static final int DATA_AVAILABLE = 1;
    /**
     * @deprecated use {@link EventType#OUTPUT_BUFFER_EMPTY} instead
     */
    @Deprecated
    public static final int OUTPUT_BUFFER_EMPTY = 2;
    /**
     * @deprecated use {@link EventType#CLEAR_TO_SEND} instead
     */
    @Deprecated
    public static final int CTS = 3;
    /**
     * @deprecated use {@link EventType#DATA_SET_READY} instead
     */
    @Deprecated
    public static final int DSR = 4;
    /**
     * @deprecated use {@link EventType#RING_INDICATOR} instead
     */
    @Deprecated
    public static final int RI = 5;
    /**
     * @deprecated use {@link EventType#CARRIER_DETECT} instead
     */
    @Deprecated
    public static final int CD = 6;
    /**
     * @deprecated use {@link EventType#OVERRUN_ERROR} instead
     */
    @Deprecated
    public static final int OE = 7;
    /**
     * @deprecated use {@link EventType#PARITY_ERROR} instead
     */
    @Deprecated
    public static final int PE = 8;
    /**
     * @deprecated use {@link EventType#FRAMING_ERROR} instead
     */
    @Deprecated
    public static final int FE = 9;
    /**
     * @deprecated use {@link EventType#BREAK_INTERRUPT} instead
     */
    @Deprecated
    public static final int BI = 10;

    public enum EventType implements CommPortEnum {
        /**
         * Data available at the serial port.
         */
        DATA_AVAILABLE(1),
        /**
         * Output buffer is empty.
         */
        OUTPUT_BUFFER_EMPTY(2),
        /**
         * Clear to send.
         */
        CLEAR_TO_SEND(3),
        /**
         * Data set ready.
         */
        DATA_SET_READY(4),
        /**
         * Ring indicator.
         */
        RING_INDICATOR(5),
        /**
         * Carrier detect.
         */
        CARRIER_DETECT(6),
        /**
         * Overrun error.
         */
        OVERRUN_ERROR(7),
        /**
         * Parity error.
         */
        PARITY_ERROR(8),
        /**
         * Framing error.
         */
        FRAMING_ERROR(9),
        /**
         * Break interrupt.
         */
        BREAK_INTERRUPT(10);

        private int value;

        private EventType(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

    }

    private final boolean oldValue;
    private final boolean newValue;
    private final int eventType;

    SerialPortEvent(SerialPort srcport, int eventtype, boolean oldvalue, boolean newvalue) {
        super(srcport);
        oldValue = oldvalue;
        newValue = newvalue;
        eventType = eventtype;
    }

    /**
     * 
     * Data available at the serial port. This event will be generated once when new data arrive at the serial port.
     * Even if the user doesn't read the data, it won't be generated again until next time new data arrive.
     * 
     * @return field constant.
     * 
     * @deprecated use {@link #eventType()} instead.
     */
    @Deprecated
    public int getEventType() {
        return eventType;
    }

    /**
     * Data available at the serial port. This event will be generated once when new data arrive at the serial port.
     * Even if the user doesn't read the data, it won't be generated again until next time new data arrive.
     * 
     * @return the event type.
     */
    public EventType eventType() {
        return Enu.enumFor(this.eventType, EventType.class);
    }

    /**
     * Gets the new value of the state change that caused the SerialPortEvent to be propagated. For example, when the CD
     * bit changes, newValue reflects the new value of the CD bit.
     * 
     * @return the flag
     */
    public boolean getNewValue() {
        return newValue;
    }

    /**
     * Gets the old value of the state change that caused the SerialPortEvent to be propagated. For example, when the CD
     * bit changes, oldValue reflects the old value of the CD bit.
     * 
     * @return the flag
     */
    public boolean getOldValue() {
        return oldValue;
    }
}
