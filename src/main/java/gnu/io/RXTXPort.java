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
import java.util.TooManyListenersException;

public class RXTXPort extends SerialPort {

	static {
		System.loadLibrary("rxtxSerial");
		Initialize();
	}

	private final SerialInputStream is = new SerialInputStream();
	private final SerialOutputStream os = new SerialOutputStream();

	/* dont close the file while accessing the fd */
	private int IOLocked = 0;
	private Object IOLockedMutex = new Object();

	private int fd = 0;
	private boolean MonitorThreadAlive = false;

	private int dataBits = DATABITS_8;
	private int speed = 9600;
	private int stopBits = SerialPort.STOPBITS_1;
	private int parity = SerialPort.PARITY_NONE;
	private int flowmode = SerialPort.FLOWCONTROL_NONE;

	private int timeout = -1;
	private int threshold = 0;

	/**
	 * a pointer to the event info structure used to share information between threads so write threads can send output
	 * buffer empty from a pthread if need be.
	 * 
	 * long for 64 bit pointers.
	 */
	long eis = 0;
	/** pid for lock files */
	int pid = 0;

	static boolean dsrFlag = false;

	private int InputBuffer = 0;
	private int OutputBuffer = 0;

	private SerialPortEventListener serialPortEventListener;

	private MonitorThread monThread;

	boolean MonitorThreadLock = true;

	/**
	 * @return boolean true if monitor thread is interrupted
	 */
	boolean monThreadisInterrupted = true;

	/**
	 * Open the named port.
	 * 
	 * @param name
	 *            the name of the device to open
	 * @throws PortInUseException
	 *             if the file is already locked by an other application.
	 */
	public RXTXPort(String name) throws PortInUseException {
		fd = open(name);
		this.name = name;

		MonitorThreadLock = true;
		monThread = new MonitorThread();
		monThread.start();
		waitForTheNativeCodeSilly();
		MonitorThreadAlive = true;
	}

	public OutputStream getOutputStream() {
		return os;
	}

	public InputStream getInputStream() {
		return is;
	}

	private native static void Initialize();

	private native synchronized int open(String name) throws PortInUseException;

	private native int nativeGetParity(int fd);

	private native int nativeGetFlowControlMode(int fd);

	native void setflowcontrol(int flowcontrol) throws IOException;

	/**
	 * @return int the timeout
	 */
	public native int NativegetReceiveTimeout();

	private native boolean NativeisReceiveTimeoutEnabled();

	/**
	 * @param time
	 * @param threshold
	 * @param InputBuffer
	 */
	private native void NativeEnableReceiveTimeoutThreshold(int time, int threshold, int InputBuffer);

	/**
	 * Set the native serial port parameters If speed is not a predifined speed it is assumed to be the actual speed
	 * desired.
	 */
	private native boolean nativeSetSerialPortParams(int speed, int dataBits, int stopBits, int parity)
			throws UnsupportedCommOperationException;

	@Override
	public synchronized void setSerialPortParams(int baudRate, int dataBits, int stopBits, int parity)
			throws UnsupportedCommOperationException {
		if (nativeSetSerialPortParams(baudRate, dataBits, stopBits, parity)) {
			throw new UnsupportedCommOperationException("Invalid Parameter");
		}
		this.speed = baudRate;
		if (stopBits == STOPBITS_1_5) {
			this.dataBits = DATABITS_5;
		}
		else {
			this.dataBits = dataBits;
		}
		this.stopBits = stopBits;
		this.parity = parity;
	}

	@Override
	public int getBaudRate() {
		return speed;
	}

	@Override
	public int getDataBits() {
		return dataBits;
	}

	@Override
	public int getStopBits() {
		return stopBits;
	}

	@Override
	public int getParity() {
		return parity;
	}

	@Override
	public void setFlowControlMode(int flowcontrol) {
		if (monThreadisInterrupted) {
			return;
		}
		try {
			setflowcontrol(flowcontrol);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		flowmode = flowcontrol;
	}

	@Override
	public int getFlowControlMode() {
		return flowmode;
	}

	public void enableReceiveFraming(int f) throws UnsupportedCommOperationException {
		throw new UnsupportedCommOperationException("Not supported");
	}

	public void disableReceiveFraming() {
	}

	public boolean isReceiveFramingEnabled() {
		return false;
	}

	public int getReceiveFramingByte() {
		return 0;
	}

	public void disableReceiveTimeout() {
		timeout = -1;
		NativeEnableReceiveTimeoutThreshold(timeout, threshold, InputBuffer);
	}

	public void enableReceiveTimeout(int time) {
		if (time >= 0) {
			timeout = time;
			NativeEnableReceiveTimeoutThreshold(time, threshold, InputBuffer);
		}
		else {
			throw new IllegalArgumentException("Unexpected negative timeout value");
		}
	}

	public boolean isReceiveTimeoutEnabled() {
		return (NativeisReceiveTimeoutEnabled());
	}

	public int getReceiveTimeout() {
		return (NativegetReceiveTimeout());
	}

	public void enableReceiveThreshold(int threshold) {
		if (threshold >= 0) {
			this.threshold = threshold;
			NativeEnableReceiveTimeoutThreshold(timeout, threshold, InputBuffer);
		}
		else {
			throw new IllegalArgumentException("Unexpected negative threshold value");
		}
	}

	public void disableReceiveThreshold() {
		enableReceiveThreshold(0);
	}

	public int getReceiveThreshold() {
		return threshold;
	}

	public boolean isReceiveThresholdEnabled() {
		return (threshold > 0);
	}

	public void setInputBufferSize(int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Unexpected negative buffer size value");
		}
		else {
			InputBuffer = size;
		}
	}

	public int getInputBufferSize() {
		return (InputBuffer);
	}

	public void setOutputBufferSize(int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Unexpected negative buffer size value");
		}
		else {
			OutputBuffer = size;
		}
	}

	public int getOutputBufferSize() {
		return (OutputBuffer);
	}

	// Line status methods

	@Override
	public native boolean isDTR();

	@Override
	public native void setDTR(boolean state);

	@Override
	public native void setRTS(boolean state);

	private native void setDSR(boolean state);

	@Override
	public native boolean isCTS();

	@Override
	public native boolean isDSR();

	@Override
	public native boolean isCD();

	@Override
	public native boolean isRI();

	@Override
	public native boolean isRTS();

	@Override
	public native void sendBreak(int duration);

	protected native void writeByte(int b, boolean i) throws IOException;

	protected native void writeArray(byte b[], int off, int len, boolean i) throws IOException;

	protected native boolean nativeDrain(boolean i) throws IOException;

	protected native int nativeavailable() throws IOException;

	protected native int readByte() throws IOException;

	protected native int readArray(byte b[], int off, int len) throws IOException;

	protected native int readTerminatedArray(byte b[], int off, int len, byte t[]) throws IOException;

	/** Process SerialPortEvents */
	/** DSR flag **/
	native void eventLoop();

	private native void interruptEventLoop();

	/** Close the port */
	private native void nativeClose(String name);

	public boolean checkMonitorThread() {
		if (monThread != null) {
			return monThreadisInterrupted;
		}
		return (true);
	}

	/*
	 * Sends an event.
	 * 
	 * @param event the SerialPortEvent id. E.g. SerialPortEvent.DATA_AVAILABLE
	 * 
	 * @param state
	 * 
	 * @return boolean true if the port is closing
	 */
	boolean sendEvent(int event, boolean state) {

		if (fd == 0 || serialPortEventListener == null || monThread == null) {
			return true;
		}

		switch (event) {
		case SerialPortEvent.DATA_AVAILABLE:
			if (monThread.Data) {
				break;
			}
			return (false);
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			if (monThread.Output) {
				break;
			}
			return (false);
		case SerialPortEvent.CTS:
			if (monThread.CTS) {
				break;
			}
			return (false);
		case SerialPortEvent.DSR:
			if (monThread.DSR) {
				break;
			}
			return (false);
		case SerialPortEvent.RI:
			if (monThread.RI) {
				break;
			}
			return (false);
		case SerialPortEvent.CD:
			if (monThread.CD) {
				break;
			}
			return (false);
		case SerialPortEvent.OE:
			if (monThread.OE) {
				break;
			}
			return (false);
		case SerialPortEvent.PE:
			if (monThread.PE) {
				break;
			}
			return (false);
		case SerialPortEvent.FE:
			if (monThread.FE) {
				break;
			}
			return (false);
		case SerialPortEvent.BI:
			if (monThread.BI) {
				break;
			}
			return (false);
		default:
			System.err.println("unknown event: " + event);
			return false;
		}

		SerialPortEvent e = new SerialPortEvent(this, event, !state, state);

		if (monThreadisInterrupted) {
			return true;
		}

		if (serialPortEventListener != null) {
			serialPortEventListener.serialEvent(e);
		}

		return fd == 0 || serialPortEventListener == null || monThread == null;
	}

	@Override
	public void addEventListener(SerialPortEventListener listener) throws TooManyListenersException {
		/*
		 * Don't let and notification requests happen until the Eventloop is ready
		 */

		if (serialPortEventListener != null) {
			throw new TooManyListenersException();
		}

		this.serialPortEventListener = listener;
		if (!MonitorThreadAlive) {
			MonitorThreadLock = true;
			monThread = new MonitorThread();
			monThread.start();
			waitForTheNativeCodeSilly();
			MonitorThreadAlive = true;
		}
	}

	/**
	 * Remove the serial port event listener
	 */
	@Override
	public void removeEventListener() {
		waitForTheNativeCodeSilly();
		// if( monThread != null && monThread.isAlive() )
		if (monThreadisInterrupted == true) {
			monThread = null;
			serialPortEventListener = null;
			return;
		}
		else if (monThread != null && monThread.isAlive()) {
			monThreadisInterrupted = true;
			/*
			 * Notify all threads in this PID that something is up They will call back to see if its their thread using
			 * isInterrupted().
			 */
			interruptEventLoop();

			try {

				// wait a reasonable moment for the death of the monitor thread
				monThread.join(3000);
			} catch (InterruptedException ex) {
				// somebody called interrupt() on us (ie wants us to abort)
				// we dont propagate InterruptedExceptions so lets re-set the flag
				Thread.currentThread().interrupt();
				return;
			}

		}
		monThread = null;
		serialPortEventListener = null;
		MonitorThreadLock = false;
		MonitorThreadAlive = false;
		monThreadisInterrupted = true;
	}

	/**
	 * Give the native code a chance to start listening to the hardware or should we say give the native code control of
	 * the issue.
	 *
	 * This is important for applications that flicker the Monitor thread while keeping the port open. In worst case
	 * test cases this loops once or twice every time.
	 */
	protected void waitForTheNativeCodeSilly() {
		while (MonitorThreadLock) {
			try {
				Thread.sleep(5);
			} catch (Exception e) {
			}
		}
	}

	private native void nativeSetEventFlag(int fd, int event, boolean flag);

	@Override
	public void notifyOnDataAvailable(boolean enable) {

		waitForTheNativeCodeSilly();

		MonitorThreadLock = true;
		nativeSetEventFlag(fd, SerialPortEvent.DATA_AVAILABLE, enable);
		monThread.Data = enable;
		MonitorThreadLock = false;
	}

	@Override
	public void notifyOnOutputEmpty(boolean enable) {
		waitForTheNativeCodeSilly();
		MonitorThreadLock = true;
		nativeSetEventFlag(fd, SerialPortEvent.OUTPUT_BUFFER_EMPTY, enable);
		monThread.Output = enable;
		MonitorThreadLock = false;
	}

	@Override
	public void notifyOnCTS(boolean enable) {
		waitForTheNativeCodeSilly();
		MonitorThreadLock = true;
		nativeSetEventFlag(fd, SerialPortEvent.CTS, enable);
		monThread.CTS = enable;
		MonitorThreadLock = false;
	}

	@Override
	public void notifyOnDSR(boolean enable) {
		waitForTheNativeCodeSilly();
		MonitorThreadLock = true;
		nativeSetEventFlag(fd, SerialPortEvent.DSR, enable);
		monThread.DSR = enable;
		MonitorThreadLock = false;
	}

	@Override
	public void notifyOnRingIndicator(boolean enable) {
		waitForTheNativeCodeSilly();
		MonitorThreadLock = true;
		nativeSetEventFlag(fd, SerialPortEvent.RI, enable);
		monThread.RI = enable;
		MonitorThreadLock = false;
	}

	@Override
	public void notifyOnCarrierDetect(boolean enable) {
		waitForTheNativeCodeSilly();
		MonitorThreadLock = true;
		nativeSetEventFlag(fd, SerialPortEvent.CD, enable);
		monThread.CD = enable;
		MonitorThreadLock = false;
	}

	@Override
	public void notifyOnOverrunError(boolean enable) {
		waitForTheNativeCodeSilly();
		MonitorThreadLock = true;
		nativeSetEventFlag(fd, SerialPortEvent.OE, enable);
		monThread.OE = enable;
		MonitorThreadLock = false;
	}

	@Override
	public void notifyOnParityError(boolean enable) {
		waitForTheNativeCodeSilly();
		MonitorThreadLock = true;
		nativeSetEventFlag(fd, SerialPortEvent.PE, enable);
		monThread.PE = enable;
		MonitorThreadLock = false;
	}

	@Override
	public void notifyOnFramingError(boolean enable) {
		waitForTheNativeCodeSilly();
		MonitorThreadLock = true;
		nativeSetEventFlag(fd, SerialPortEvent.FE, enable);
		monThread.FE = enable;
		MonitorThreadLock = false;
	}

	@Override
	public void notifyOnBreakInterrupt(boolean enable) {
		waitForTheNativeCodeSilly();
		MonitorThreadLock = true;
		nativeSetEventFlag(fd, SerialPortEvent.BI, enable);
		monThread.BI = enable;
		MonitorThreadLock = false;
	}

	boolean closeLock = false;

	@Override
	public void close() {
		synchronized (this) {

			while (IOLocked > 0) {
				try {
					this.wait(500);
				} catch (InterruptedException ie) {
					// somebody called interrupt() on us
					// we obbey and return without without closing the socket
					Thread.currentThread().interrupt();
					return;
				}
			}

			// we set the closeLock after the above check because we might
			// have returned without proceeding
			if (closeLock) {
				return;
			}
			closeLock = true;
		}

		if (fd <= 0) {
			return;
		}
		setDTR(false);
		setDSR(false);
		if (!monThreadisInterrupted) {
			removeEventListener();
		}

		nativeClose(this.name);

		super.close();
		fd = 0;
		closeLock = false;
	}

	/** Finalize the port */
	@Override
	protected void finalize() {
		if (fd > 0) {
			close();
		}
	}

	/** Inner class for SerialOutputStream */
	private class SerialOutputStream extends OutputStream {
		/**
		 * @param b
		 * @throws IOException
		 */
		@Override
		public void write(int b) throws IOException {
			if (speed == 0) {
				return;
			}
			if (monThreadisInterrupted) {
				return;
			}
			synchronized (IOLockedMutex) {
				IOLocked++;
			}
			try {
				waitForTheNativeCodeSilly();
				if (fd == 0) {
					throw new IOException();
				}
				writeByte(b, monThreadisInterrupted);
			} finally {
				synchronized (IOLockedMutex) {
					IOLocked--;
				}
			}
		}

		/**
		 * @param b[]
		 * @throws IOException
		 */
		@Override
		public void write(byte b[]) throws IOException {
			if (speed == 0) {
				return;
			}
			if (monThreadisInterrupted) {
				return;
			}
			if (fd == 0) {
				throw new IOException();
			}
			synchronized (IOLockedMutex) {
				IOLocked++;
			}
			try {
				waitForTheNativeCodeSilly();
				writeArray(b, 0, b.length, monThreadisInterrupted);
			} finally {
				synchronized (IOLockedMutex) {
					IOLocked--;
				}
			}

		}

		/**
		 * @param b[]
		 * @param off
		 * @param len
		 * @throws IOException
		 */
		@Override
		public void write(byte b[], int off, int len) throws IOException {
			if (speed == 0) {
				return;
			}
			if (off + len > b.length) {
				throw new IndexOutOfBoundsException("Invalid offset/length passed to read");
			}

			byte send[] = new byte[len];
			System.arraycopy(b, off, send, 0, len);

			if (fd == 0) {
				throw new IOException();
			}
			if (monThreadisInterrupted) {
				return;
			}
			synchronized (IOLockedMutex) {
				IOLocked++;
			}
			try {
				waitForTheNativeCodeSilly();
				writeArray(send, 0, len, monThreadisInterrupted);
			} finally {
				synchronized (IOLockedMutex) {
					IOLocked--;
				}
			}
		}

		/**
		*/
		@Override
		public void flush() throws IOException {
			if (speed == 0) {
				return;
			}
			if (fd == 0) {
				throw new IOException();
			}
			if (monThreadisInterrupted) {
				return;
			}
			synchronized (IOLockedMutex) {
				IOLocked++;
			}
			try {
				waitForTheNativeCodeSilly();
				/*
				 * this is probably good on all OS's but for now just sendEvent from java on Sol
				 */
				if (nativeDrain(monThreadisInterrupted)) {
					sendEvent(SerialPortEvent.OUTPUT_BUFFER_EMPTY, true);
				}
			} finally {
				synchronized (IOLockedMutex) {
					IOLocked--;
				}
			}
		}
	}

	private class SerialInputStream extends InputStream {
		/**
		 * @return int the int read
		 * @throws IOException
		 * @see java.io.InputStream
		 *
		 *      timeout threshold Behavior ------------------------------------------------------------------------ 0 0
		 *      blocks until 1 byte is available timeout > 0, threshold = 0, blocks until timeout occurs, returns -1 on
		 *      timeout >0 >0 blocks until timeout, returns - 1 on timeout, magnitude of threshold doesn't play a role.
		 *      0 >0 Blocks until 1 byte, magnitude of threshold doesn't play a role
		 */
		@Override
		public synchronized int read() throws IOException {

			if (fd == 0) {
				throw new IOException();
			}
			synchronized (IOLockedMutex) {
				IOLocked++;
			}
			try {

				waitForTheNativeCodeSilly();

				int result = readByte();
				return result;
			} finally {
				synchronized (IOLockedMutex) {
					IOLocked--;
				}
			}
		}

		/**
		 * @param b[]
		 * @return int number of bytes read
		 * @throws IOException
		 *
		 *             timeout threshold Behavior
		 *             ------------------------------------------------------------------------ 0 0 blocks until 1 byte
		 *             is available >0 0 blocks until timeout occurs, returns 0 on timeout >0 >0 blocks until timeout or
		 *             reads threshold bytes, returns 0 on timeout 0 >0 blocks until reads threshold bytes
		 */
		@Override
		public synchronized int read(byte b[]) throws IOException {
			int result;

			if (monThreadisInterrupted) {
				return (0);
			}
			synchronized (IOLockedMutex) {
				IOLocked++;
			}
			try {
				waitForTheNativeCodeSilly();
				result = read(b, 0, b.length);

				return result;
			} finally {
				synchronized (IOLockedMutex) {
					IOLocked--;
				}
			}
		}

		/**
		 * @see InputStream#read(byte[], int, int)
		 * 
		 * @param b
		 *            the bytes
		 * @param off
		 *            the offset
		 * @param len
		 *            the length
		 * @return the numbernumber of bytes read
		 * @throws IOException
		 *             timeout threshold Behavior
		 *             ------------------------------------------------------------------------ 0 0 blocks until 1 byte
		 *             is available >0 0 blocks until timeout occurs, returns 0 on timeout >0 >0 blocks until timeout or
		 *             reads threshold bytes, returns 0 on timeout 0 >0 blocks until either threshold # of bytes or len
		 *             bytes, whichever was lower.
		 */
		@Override
		public synchronized int read(byte b[], int off, int len) throws IOException {
			int result;
			/*
			 * Some sanity checks
			 */
			if (fd == 0) {
				throw new IOException();
			}

			if (b == null) {
				throw new NullPointerException();
			}

			if ((off < 0) || (len < 0) || (off + len > b.length)) {
				throw new IndexOutOfBoundsException();
			}

			/*
			 * Return immediately if len==0
			 */
			if (len == 0) {
				return 0;
			}
			/*
			 * See how many bytes we should read
			 */
			int Minimum = len;

			if (threshold == 0) {
				/*
				 * If threshold is disabled, read should return as soon as data are available (up to the amount of
				 * available bytes in order to avoid blocking) Read may return earlier depending of the receive time
				 * out.
				 */
				int a = nativeavailable();
				if (a == 0) {
					Minimum = 1;
				}
				else {
					Minimum = Math.min(Minimum, a);
				}
			}
			else {
				/*
				 * Threshold is enabled. Read should return when 'threshold' bytes have been received (or when the
				 * receive timeout expired)
				 */
				Minimum = Math.min(Minimum, threshold);
			}
			if (monThreadisInterrupted == true) {
				return (0);
			}
			synchronized (IOLockedMutex) {
				IOLocked++;
			}
			try {
				waitForTheNativeCodeSilly();
				result = readArray(b, off, Minimum);
				return (result);
			} finally {
				synchronized (IOLockedMutex) {
					IOLocked--;
				}
			}
		}

		/**
		 * @param b[]
		 * @param off
		 * @param len
		 * @param t[]
		 * @return int number of bytes read
		 * @throws IOException
		 * 
		 *             We are trying to catch the terminator in the native code Right now it is assumed that t[] is an
		 *             array of 2 bytes.
		 * 
		 *             if the read encounters the two bytes, it will return and the array will contain the terminator.
		 *             Otherwise read behavior should be the same as read( b[], off, len ). Timeouts have not been well
		 *             tested.
		 */

		public synchronized int read(byte b[], int off, int len, byte t[]) throws IOException {
			int result;
			/*
			 * Some sanity checks
			 */
			if (fd == 0) {
				throw new IOException();
			}

			if (b == null) {
				throw new NullPointerException();
			}

			if ((off < 0) || (len < 0) || (off + len > b.length)) {
				throw new IndexOutOfBoundsException();
			}

			/*
			 * Return immediately if len==0
			 */
			if (len == 0) {
				return 0;
			}
			/*
			 * See how many bytes we should read
			 */
			int Minimum = len;

			if (threshold == 0) {
				/*
				 * If threshold is disabled, read should return as soon as data are available (up to the amount of
				 * available bytes in order to avoid blocking) Read may return earlier depending of the receive time
				 * out.
				 */
				int a = nativeavailable();
				if (a == 0) {
					Minimum = 1;
				}
				else {
					Minimum = Math.min(Minimum, a);
				}
			}
			else {
				/*
				 * Threshold is enabled. Read should return when 'threshold' bytes have been received (or when the
				 * receive timeout expired)
				 */
				Minimum = Math.min(Minimum, threshold);
			}

			if (monThreadisInterrupted) {
				return 0;
			}
			synchronized (IOLockedMutex) {
				IOLocked++;
			}
			try {
				waitForTheNativeCodeSilly();
				result = readTerminatedArray(b, off, Minimum, t);
				return (result);
			} finally {
				synchronized (IOLockedMutex) {
					IOLocked--;
				}
			}
		}

		/**
		 * @return int bytes available
		 * @throws IOException
		 */
		@Override
		public synchronized int available() throws IOException {
			if (monThreadisInterrupted) {
				return (0);
			}
			synchronized (IOLockedMutex) {
				IOLocked++;
			}
			try {
				int r = nativeavailable();
				return r;
			} finally {
				synchronized (IOLockedMutex) {
					IOLocked--;
				}
			}
		}
	}

	/**
	*/
	class MonitorThread extends Thread {
		/**
		 * Note: these have to be separate boolean flags because the SerialPortEvent constants are NOT bit-flags, they
		 * are just defined as integers from 1 to 10 -DPL
		 */
		private volatile boolean CTS = false;
		private volatile boolean DSR = false;
		private volatile boolean RI = false;
		private volatile boolean CD = false;
		private volatile boolean OE = false;
		private volatile boolean PE = false;
		private volatile boolean FE = false;
		private volatile boolean BI = false;
		private volatile boolean Data = false;
		private volatile boolean Output = false;

		MonitorThread() {
			setDaemon(true);
		}

		/**
		 * run the thread and call the event loop.
		 */
		@Override
		public void run() {
			monThreadisInterrupted = false;
			eventLoop();
		}

		@Override
		protected void finalize() throws Throwable {
		}
	}

	/*------------------------  END OF CommAPI -----------------------------*/

	private native static void nativeStaticSetSerialPortParams(String f, int b, int d, int s, int p)
			throws UnsupportedCommOperationException;

	private native static boolean nativeStaticSetDSR(String port, boolean flag)
			throws UnsupportedCommOperationException;

	private native static boolean nativeStaticSetDTR(String port, boolean flag)
			throws UnsupportedCommOperationException;

	private native static boolean nativeStaticSetRTS(String port, boolean flag)
			throws UnsupportedCommOperationException;

	private native static boolean nativeStaticIsDSR(String port) throws UnsupportedCommOperationException;

	private native static boolean nativeStaticIsDTR(String port) throws UnsupportedCommOperationException;

	private native static boolean nativeStaticIsRTS(String port) throws UnsupportedCommOperationException;

	private native static boolean nativeStaticIsCTS(String port) throws UnsupportedCommOperationException;

	private native static boolean nativeStaticIsCD(String port) throws UnsupportedCommOperationException;

	private native static boolean nativeStaticIsRI(String port) throws UnsupportedCommOperationException;

	private native static int nativeStaticGetBaudRate(String port) throws UnsupportedCommOperationException;

	private native static int nativeStaticGetDataBits(String port) throws UnsupportedCommOperationException;

	private native static int nativeStaticGetParity(String port) throws UnsupportedCommOperationException;

	private native static int nativeStaticGetStopBits(String port) throws UnsupportedCommOperationException;

	private native byte nativeGetParityErrorChar() throws UnsupportedCommOperationException;

	private native boolean nativeSetParityErrorChar(byte b) throws UnsupportedCommOperationException;

	private native byte nativeGetEndOfInputChar() throws UnsupportedCommOperationException;

	private native boolean nativeSetEndOfInputChar(byte b) throws UnsupportedCommOperationException;

	private native boolean nativeSetUartType(String type, boolean test) throws UnsupportedCommOperationException;

	native String nativeGetUartType() throws UnsupportedCommOperationException;

	private native boolean nativeSetBaudBase(int BaudBase) throws UnsupportedCommOperationException;

	private native int nativeGetBaudBase() throws UnsupportedCommOperationException;

	private native boolean nativeSetDivisor(int Divisor) throws UnsupportedCommOperationException;

	private native int nativeGetDivisor() throws UnsupportedCommOperationException;

	private native boolean nativeSetLowLatency() throws UnsupportedCommOperationException;

	private native boolean nativeGetLowLatency() throws UnsupportedCommOperationException;

	private native boolean nativeSetCallOutHangup(boolean NoHup) throws UnsupportedCommOperationException;

	private native boolean nativeGetCallOutHangup() throws UnsupportedCommOperationException;

	private native boolean nativeClearCommInput() throws UnsupportedCommOperationException;

	/**
	 * Retrieve the baud rate.
	 * <p>
	 * This is only accurate up to 38600 baud currently.
	 * </p>
	 *
	 * @param port
	 *            the name of the port thats been preopened
	 * @return BaudRate on success
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 *
	 */
	public static int staticGetBaudRate(String port) throws UnsupportedCommOperationException {
		return (nativeStaticGetBaudRate(port));
	}

	/**
	 * Retrieve the data bits.
	 *
	 * @param port
	 *            the name of the port thats been preopened
	 * @return DataBits on success
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 *
	 */
	public static int staticGetDataBits(String port) throws UnsupportedCommOperationException {
		return nativeStaticGetDataBits(port);
	}

	/**
	 * Retrieve the parity.
	 * 
	 * @param port
	 *            the name of the port thats been preopened
	 * @return Parity on success
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 *
	 */
	public static int staticGetParity(String port) throws UnsupportedCommOperationException {
		return nativeStaticGetParity(port);
	}

	/**
	 * Retrieve the stop bits.
	 * 
	 * @param port
	 *            the name of the port thats been preopened
	 * @return StopBits on success
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 *
	 */
	public static int staticGetStopBits(String port) throws UnsupportedCommOperationException {
		return (nativeStaticGetStopBits(port));
	}

	/**
	 * Set the SerialPort parameters 1.5 stop bits requires 5 databits
	 * 
	 * @see gnu.io.UnsupportedCommOperationException
	 * 
	 * @param filename
	 *            filename
	 * @param baudrate
	 *            baudrate
	 * @param databits
	 *            databits
	 * @param stopbits
	 *            stopbits
	 * @param parity
	 *            parity
	 *
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 */
	public static void staticSetSerialPortParams(String filename, int baudrate, int databits, int stopbits, int parity)
			throws UnsupportedCommOperationException {
		nativeStaticSetSerialPortParams(filename, baudrate, databits, stopbits, parity);
	}

	/**
	 * Open the port and set DSR. remove lockfile and do not close This is so some software can appear to set the DSR
	 * before 'opening' the port a second time later on.
	 * 
	 * @param port
	 *            the port name
	 * @param flag
	 *            boolean DSR FLAG.
	 * @return true if the operation was successful
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 */
	public static boolean staticSetDSR(String port, boolean flag) throws UnsupportedCommOperationException {
		return (nativeStaticSetDSR(port, flag));
	}

	/**
	 * Open the port and set DTR. remove lockfile and do not close This is so some software can appear to set the DTR
	 * before 'opening' the port a second time later on.
	 *
	 * @param port
	 *            the port name
	 * @param flag
	 *            boolean DTR FLAG.
	 * @return true if the operation was successful
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 *
	 */
	public static boolean staticSetDTR(String port, boolean flag) throws UnsupportedCommOperationException {
		return (nativeStaticSetDTR(port, flag));
	}

	/**
	 * Open the port and set RTS. remove lockfile and do not close This is so some software can appear to set the RTS
	 * before 'opening' the port a second time later on.
	 *
	 * @param port
	 *            the port name
	 * @param flag
	 *            boolean RTS FLAG.
	 * @return true if the operation was successful
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 *
	 */
	public static boolean staticSetRTS(String port, boolean flag) throws UnsupportedCommOperationException {
		return nativeStaticSetRTS(port, flag);
	}

	/**
	 * find the fd and return RTS without using a Java open() call
	 *
	 * @param port
	 *            the port name
	 * @return true if asserted
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 *
	 */
	public static boolean staticIsRTS(String port) throws UnsupportedCommOperationException {
		return (nativeStaticIsRTS(port));
	}

	/**
	 * find the fd and return CD without using a Java open() call
	 *
	 * @param port
	 *            the port name
	 * @return true if asserted
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 *
	 */
	public static boolean staticIsCD(String port) throws UnsupportedCommOperationException {
		return (nativeStaticIsCD(port));
	}

	/**
	 * find the fd and return CTS without using a Java open() call
	 *
	 * @param port
	 *            the port name
	 * @return true if asserted
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 *
	 */
	public static boolean staticIsCTS(String port) throws UnsupportedCommOperationException {
		return nativeStaticIsCTS(port);
	}

	/**
	 * find the fd and return DSR without using a Java open() call
	 *
	 * @param port
	 *            the port name
	 * @return true if asserted
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 *
	 */
	public static boolean staticIsDSR(String port) throws UnsupportedCommOperationException {
		return (nativeStaticIsDSR(port));
	}

	/**
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 *
	 * find the fd and return DTR without using a Java open() call
	 *
	 * @param port
	 *            the port name
	 * @return true if asserted
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 *
	 */
	public static boolean staticIsDTR(String port) throws UnsupportedCommOperationException {
		return nativeStaticIsDTR(port);
	}

	/**
	 * Find the fd and return RI without using a Java open() call
	 *
	 * @param port
	 *            the port name
	 * @return true if asserted
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 */
	public static boolean staticIsRI(String port) throws UnsupportedCommOperationException {
		return nativeStaticIsRI(port);
	}

	@Override
	public byte getParityErrorChar() throws UnsupportedCommOperationException {
		// TODO: Anyone know how to do this in Unix?
		return nativeGetParityErrorChar();
	}

	@Override
	public boolean setParityErrorChar(byte b) throws UnsupportedCommOperationException {
		// TODO: Anyone know how to do this in Unix?
		return nativeSetParityErrorChar(b);
	}

	@Override
	public byte getEndOfInputChar() throws UnsupportedCommOperationException {
		// TODO: Anyone know how to do this in Unix?
		return nativeGetEndOfInputChar();
	}

	@Override
	public boolean setEndOfInputChar(byte b) throws UnsupportedCommOperationException {
		return (nativeSetEndOfInputChar(b));
	}

	@Override
	public boolean setUARTType(String type, boolean test) throws UnsupportedCommOperationException {
		return nativeSetUartType(type, test);
	}

	@Override
	public String getUARTType() throws UnsupportedCommOperationException {
		return nativeGetUartType();
	}

	@Override
	public boolean setBaudBase(int BaudBase) throws UnsupportedCommOperationException, IOException {
		return nativeSetBaudBase(BaudBase);
	}

	@Override
	public int getBaudBase() throws UnsupportedCommOperationException, IOException {
		return nativeGetBaudBase();
	}

	/**
	 * Extension to CommAPI. Set Baud Base to 38600 on Linux and W32 before using.
	 */
	@Override
	public boolean setDivisor(int Divisor) throws UnsupportedCommOperationException, IOException {
		return nativeSetDivisor(Divisor);
	}

	/**
	 * Extension to CommAPI
	 */
	@Override
	public int getDivisor() throws UnsupportedCommOperationException, IOException {
		return nativeGetDivisor();
	}

	@Override
	public boolean setLowLatency() throws UnsupportedCommOperationException {
		return nativeSetLowLatency();
	}

	@Override
	public boolean getLowLatency() throws UnsupportedCommOperationException {
		return nativeGetLowLatency();
	}

	// return true on success
	@Override
	public boolean setCallOutHangup(boolean NoHup) throws UnsupportedCommOperationException {
		return nativeSetCallOutHangup(NoHup);
	}

	// return true on success
	@Override
	public boolean getCallOutHangup() throws UnsupportedCommOperationException {
		return nativeGetCallOutHangup();
	}

	// return true on success
	public boolean clearCommInput() throws UnsupportedCommOperationException {
		return nativeClearCommInput();
	}
}
