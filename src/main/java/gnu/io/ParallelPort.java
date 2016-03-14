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

public abstract class ParallelPort extends AbstractCommPort {
    public static final int LPT_MODE_ANY = 0;
    public static final int LPT_MODE_SPP = 1;
    public static final int LPT_MODE_PS2 = 2;
    public static final int LPT_MODE_EPP = 3;
    public static final int LPT_MODE_ECP = 4;
    public static final int LPT_MODE_NIBBLE = 5;

    public abstract int getMode();

    public abstract int setMode(int mode) throws UnsupportedCommOperationException;

    public abstract void restart();

    public abstract void suspend();

    public abstract boolean isPaperOut();

    public abstract boolean isPrinterBusy();

    public abstract boolean isPrinterError();

    public abstract boolean isPrinterSelected();

    public abstract boolean isPrinterTimedOut();

    public abstract int getOutputBufferFree();

    public abstract void addEventListener(ParallelPortEventListener lsnr) throws TooManyListenersException;

    public abstract void removeEventListener();

    public abstract void notifyOnError(boolean enable);

    public abstract void notifyOnBuffer(boolean enable);
    /*
     * public int PAR_EV_ERROR 1 public int PAR_EV_BUFFER 2 public ParallelPort(){} private native static void
     * Initialize(); public LPRPort( String name ) throws IOException; private native int open( String name ) throws
     * IOException; private int fd; private final ParallelOutputStream out = new ParallelOutputStream(); public
     * OutputStream getOutputStream(); private final ParallelInputStream in = new ParallelInputStream(); public
     * InputStream getInputStream(); private int lprmode=LPT_MODE_ANY; public native boolean setLPRMode(int mode) throws
     * UnsupportedCommOperationException; private int speed; public int getBaudRate(); private int dataBits; public int
     * getDataBits(); private int stopBits; public int getStopBits(); private int parity; public int getParity();
     * private native void nativeClose(); public void close(); public void enableReceiveFraming( int f ) throws
     * UnsupportedCommOperationException; public void disableReceiveFraming() {} public boolean
     * isReceiveFramingEnabled(); public int getReceiveFramingByte(); private int timeout = 0; public void
     * enableReceiveTimeout( int t ); public void disableReceiveTimeout(); public boolean isReceiveTimeoutEnabled();
     * public int getReceiveTimeout(); private int threshold = 1; public void enableReceiveThreshold( int t ); public
     * void disableReceiveThreshold(); public int getReceiveThreshold(); public boolean isReceiveThresholdEnabled();
     * public native void setInputBufferSize( int size ); public native int getInputBufferSize(); public native void
     * setOutputBufferSize( int size ); public Abstract int getOutputBufferSize(); private native void writeByte( int b
     * ) throws IOException; private native void writeArray( byte b[], int off, int len ) throws IOException; private
     * native void drain() throws IOException; private native int nativeavailable() throws IOException; private native
     * int readByte() throws IOException; private native int readArray( byte b[], int off, int len ) throws IOException;
     * private ParallelPortEventListener PPEventListener; private MonitorThread monThread; native void eventLoop(); void
     * sendEvent( int event, boolean state ); protected void finalize();
     */
}
