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

	private int fileDescriptor = 0;
	private boolean MonitorThreadAlive = false;

	private int dataBits = DATABITS_8;
	private int baudRate = 9600;
	private int stopBits = SerialPort.STOPBITS_1;
	private int parity = SerialPort.PARITY_NONE;
	private int flowmode = SerialPort.FLOWCONTROL_NONE;

	private int timeout = -1;
	private int receiveThreshold = 0;

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
	 * Open the named port
	 * 
	 * @param name
	 *            the name of the device to open
	 * @throws PortInUseException
	 * @see gnu.io.SerialPort
	 */
	public RXTXPort(String name) throws PortInUseException {
		fileDescriptor = open(name);
		this.name = name;

		MonitorThreadLock = true;
		monThread = new MonitorThread();
		monThread.start();
		waitForTheNativeCodeSilly();
		MonitorThreadAlive = true;
	}

	@Override
	public OutputStream getOutputStream() {
		return os;
	}

	@Override
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

	/**
	 * @return bloolean true if recieve timeout is enabled
	 */
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
		this.baudRate = baudRate;
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
		return baudRate;
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

	@Override
	public void enableReceiveFraming(int f) throws UnsupportedCommOperationException {
		throw new UnsupportedCommOperationException("Not supported");
	}

	@Override
	public void disableReceiveFraming() {
	}

	@Override
	public boolean isReceiveFramingEnabled() {
		return false;
	}

	@Override
	public int getReceiveFramingByte() {
		return 0;
	}

	@Override
	public void disableReceiveTimeout() {
		timeout = -1;
		NativeEnableReceiveTimeoutThreshold(timeout, receiveThreshold, InputBuffer);
	}

	@Override
	public void enableReceiveTimeout(int time) {
		if (time >= 0) {
			timeout = time;
			NativeEnableReceiveTimeoutThreshold(time, receiveThreshold, InputBuffer);
		}
		else {
			throw new IllegalArgumentException("Unexpected negative timeout value");
		}
	}

	@Override
	public boolean isReceiveTimeoutEnabled() {
		return (NativeisReceiveTimeoutEnabled());
	}

	@Override
	public int getReceiveTimeout() {
		return (NativegetReceiveTimeout());
	}

	@Override
	public void enableReceiveThreshold(int threshold) {
		if (threshold >= 0) {
			receiveThreshold = threshold;
			NativeEnableReceiveTimeoutThreshold(timeout, receiveThreshold, InputBuffer);
		}
		else {
			throw new IllegalArgumentException("Unexpected negative threshold value");
		}
	}

	@Override
	public void disableReceiveThreshold() {
		enableReceiveThreshold(0);
	}

	@Override
	public int getReceiveThreshold() {
		return receiveThreshold;
	}

	@Override
	public boolean isReceiveThresholdEnabled() {
		return (receiveThreshold > 0);
	}

	@Override
	public void setInputBufferSize(int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Unexpected negative buffer size value");
		}
		else {
			InputBuffer = size;
		}
	}

	@Override
	public int getInputBufferSize() {
		return (InputBuffer);
	}

	@Override
	public void setOutputBufferSize(int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Unexpected negative buffer size value");
		}
		else {
			OutputBuffer = size;
		}
	}

	@Override
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

	/**
	 * @param event
	 * @param state
	 * @return boolean true if the port is closing
	 */
	public boolean sendEvent(int event, boolean state) {

		if (fileDescriptor == 0 || serialPortEventListener == null || monThread == null) {
			return (true);
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
			return (false);
		}

		SerialPortEvent e = new SerialPortEvent(this, event, !state, state);

		if (monThreadisInterrupted) {
			return (true);
		}
		if (serialPortEventListener != null) {
			serialPortEventListener.serialEvent(e);
		}

		return fd == 0 || SPEventListener == null || monThread == null ? (true) : (false);
	}

	@Override
	public void addEventListener(SerialPortEventListener lsnr) throws TooManyListenersException {
		/*
		 * Don't let and notification requests happen until the Eventloop is ready
		 */

		if (serialPortEventListener != null) {
			throw new TooManyListenersException();
		}
		serialPortEventListener = lsnr;
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
		nativeSetEventFlag(fileDescriptor, SerialPortEvent.DATA_AVAILABLE, enable);
		monThread.Data = enable;
		MonitorThreadLock = false;
	}

	@Override
	public void notifyOnOutputEmpty(boolean enable) {
		waitForTheNativeCodeSilly();
		MonitorThreadLock = true;
		nativeSetEventFlag(fileDescriptor, SerialPortEvent.OUTPUT_BUFFER_EMPTY, enable);
		monThread.Output = enable;
		MonitorThreadLock = false;
	}

	@Override
	public void notifyOnCTS(boolean enable) {
		waitForTheNativeCodeSilly();
		MonitorThreadLock = true;
		nativeSetEventFlag(fileDescriptor, SerialPortEvent.CTS, enable);
		monThread.CTS = enable;
		MonitorThreadLock = false;
	}

	@Override
	public void notifyOnDSR(boolean enable) {
		waitForTheNativeCodeSilly();
		MonitorThreadLock = true;
		nativeSetEventFlag(fileDescriptor, SerialPortEvent.DSR, enable);
		monThread.DSR = enable;
		MonitorThreadLock = false;
	}

	@Override
	public void notifyOnRingIndicator(boolean enable) {
		waitForTheNativeCodeSilly();
		MonitorThreadLock = true;
		nativeSetEventFlag(fileDescriptor, SerialPortEvent.RI, enable);
		monThread.RI = enable;
		MonitorThreadLock = false;
	}

	@Override
	public void notifyOnCarrierDetect(boolean enable) {
		waitForTheNativeCodeSilly();
		MonitorThreadLock = true;
		nativeSetEventFlag(fileDescriptor, SerialPortEvent.CD, enable);
		monThread.CD = enable;
		MonitorThreadLock = false;
	}

	@Override
	public void notifyOnOverrunError(boolean enable) {
		waitForTheNativeCodeSilly();
		MonitorThreadLock = true;
		nativeSetEventFlag(fileDescriptor, SerialPortEvent.OE, enable);
		monThread.OE = enable;
		MonitorThreadLock = false;
	}

	@Override
	public void notifyOnParityError(boolean enable) {
		waitForTheNativeCodeSilly();
		MonitorThreadLock = true;
		nativeSetEventFlag(fileDescriptor, SerialPortEvent.PE, enable);
		monThread.PE = enable;
		MonitorThreadLock = false;
	}

	@Override
	public void notifyOnFramingError(boolean enable) {
		waitForTheNativeCodeSilly();
		MonitorThreadLock = true;
		nativeSetEventFlag(fileDescriptor, SerialPortEvent.FE, enable);
		monThread.FE = enable;
		MonitorThreadLock = false;
	}

	@Override
	public void notifyOnBreakInterrupt(boolean enable) {
		waitForTheNativeCodeSilly();
		MonitorThreadLock = true;
		nativeSetEventFlag(fileDescriptor, SerialPortEvent.BI, enable);
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

		if (fileDescriptor <= 0) {
			return;
		}
		setDTR(false);
		setDSR(false);
		if (!monThreadisInterrupted) {
			removeEventListener();
		}

		nativeClose(this.name);

		super.close();
		fileDescriptor = 0;
		closeLock = false;
	}

	/** Finalize the port */
	@Override
	protected void finalize() {
		if (fileDescriptor > 0) {
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
			if (baudRate == 0) {
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
				if (fileDescriptor == 0) {
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
			if (baudRate == 0) {
				return;
			}
			if (monThreadisInterrupted) {
				return;
			}
			if (fileDescriptor == 0) {
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
			if (baudRate == 0) {
				return;
			}
			if (off + len > b.length) {
				throw new IndexOutOfBoundsException("Invalid offset/length passed to read");
			}

			byte send[] = new byte[len];
			System.arraycopy(b, off, send, 0, len);

			if (fileDescriptor == 0) {
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
			if (baudRate == 0) {
				return;
			}
			if (fileDescriptor == 0) {
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

			if (fileDescriptor == 0) {
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

		/*
		 * read(byte b[], int, int) Documentation is at
		 * http://java.sun.com/products/jdk/1.2/docs/api/java/io/InputStream.html#read(byte[], int, int)
		 */
		/**
		 * @param b[]
		 * @param off
		 * @param len
		 * @return int number of bytes read
		 * @throws IOException
		 *
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
			if (fileDescriptor == 0) {
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

			if (receiveThreshold == 0) {
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
				Minimum = Math.min(Minimum, receiveThreshold);
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
			if (fileDescriptor == 0) {
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

			if (receiveThreshold == 0) {
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
				Minimum = Math.min(Minimum, receiveThreshold);
			}
			if (monThreadisInterrupted == true) {
				return (0);
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

	/**
	 * A dummy method added so RXTX compiles on Kaffee
	 * 
	 * @deprecated deprecated but used in Kaffe
	 */
	@Deprecated
	public void setRcvFifoTrigger(int trigger) {
	};

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
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 *
	 * This is only accurate up to 38600 baud currently.
	 *
	 * @param port
	 *            the name of the port thats been preopened
	 * @return BaudRate on success
	 * @throws UnsupportedCommOperationException;
	 *             This will not behave as expected with custom speeds
	 *
	 */
	public static int staticGetBaudRate(String port) throws UnsupportedCommOperationException {
		return (nativeStaticGetBaudRate(port));
	}

	/**
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 *
	 * @param port
	 *            the name of the port thats been preopened
	 * @return DataBits on success
	 * @throws UnsupportedCommOperationException;
	 *
	 */
	public static int staticGetDataBits(String port) throws UnsupportedCommOperationException {
		return (nativeStaticGetDataBits(port));
	}

	/**
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 *
	 * @param port
	 *            the name of the port thats been preopened
	 * @return Parity on success
	 * @throws UnsupportedCommOperationException;
	 *
	 */
	public static int staticGetParity(String port) throws UnsupportedCommOperationException {
		return (nativeStaticGetParity(port));
	}

	/**
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 *
	 * @param port
	 *            the name of the port thats been preopened
	 * @return StopBits on success
	 * @throws UnsupportedCommOperationException;
	 *
	 */
	public static int staticGetStopBits(String port) throws UnsupportedCommOperationException {
		return (nativeStaticGetStopBits(port));
	}

	/**
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 *
	 * Set the SerialPort parameters 1.5 stop bits requires 5 databits
	 * 
	 * @param f
	 *            filename
	 * @param b
	 *            baudrate
	 * @param d
	 *            databits
	 * @param s
	 *            stopbits
	 * @param p
	 *            parity
	 *
	 * @throws UnsupportedCommOperationException
	 * @see gnu.io.UnsupportedCommOperationException
	 */

	public static void staticSetSerialPortParams(String f, int b, int d, int s, int p)
			throws UnsupportedCommOperationException {
		nativeStaticSetSerialPortParams(f, b, d, s, p);
	}

	/**
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 *
	 * Open the port and set DSR. remove lockfile and do not close This is so some software can appear to set the DSR
	 * before 'opening' the port a second time later on.
	 *
	 * @return true on success
	 * @throws UnsupportedCommOperationException;
	 *
	 */
	public static boolean staticSetDSR(String port, boolean flag) throws UnsupportedCommOperationException {
		return (nativeStaticSetDSR(port, flag));
	}

	/**
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 *
	 * Open the port and set DTR. remove lockfile and do not close This is so some software can appear to set the DTR
	 * before 'opening' the port a second time later on.
	 *
	 * @return true on success
	 * @throws UnsupportedCommOperationException;
	 *
	 */
	public static boolean staticSetDTR(String port, boolean flag) throws UnsupportedCommOperationException {
		return (nativeStaticSetDTR(port, flag));
	}

	/**
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 *
	 * Open the port and set RTS. remove lockfile and do not close This is so some software can appear to set the RTS
	 * before 'opening' the port a second time later on.
	 *
	 * @return none
	 * @throws UnsupportedCommOperationException;
	 *
	 */
	public static boolean staticSetRTS(String port, boolean flag) throws UnsupportedCommOperationException {
		return (nativeStaticSetRTS(port, flag));
	}

	/**
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 *
	 * find the fd and return RTS without using a Java open() call
	 *
	 * @param port
	 * @return true if asserted
	 * @throws UnsupportedCommOperationException;
	 *
	 */
	public static boolean staticIsRTS(String port) throws UnsupportedCommOperationException {
		return (nativeStaticIsRTS(port));
	}

	/**
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 *
	 * find the fd and return CD without using a Java open() call
	 *
	 * @param port
	 * @return true if asserted
	 * @throws UnsupportedCommOperationException;
	 *
	 */
	public static boolean staticIsCD(String port) throws UnsupportedCommOperationException {
		return (nativeStaticIsCD(port));
	}

	/**
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 *
	 * find the fd and return CTS without using a Java open() call
	 *
	 * @param port
	 * @return true if asserted
	 * @throws UnsupportedCommOperationException;
	 *
	 */
	public static boolean staticIsCTS(String port) throws UnsupportedCommOperationException {
		return (nativeStaticIsCTS(port));
	}

	/**
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 *
	 * find the fd and return DSR without using a Java open() call
	 *
	 * @param port
	 * @return true if asserted
	 * @throws UnsupportedCommOperationException;
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
	 * @return true if asserted
	 * @throws UnsupportedCommOperationException;
	 *
	 */
	public static boolean staticIsDTR(String port) throws UnsupportedCommOperationException {
		return (nativeStaticIsDTR(port));
	}

	/**
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 *
	 * find the fd and return RI without using a Java open() call
	 *
	 * @param port
	 * @return true if asserted
	 * @throws UnsupportedCommOperationException;
	 *
	 */
	public static boolean staticIsRI(String port) throws UnsupportedCommOperationException {
		return (nativeStaticIsRI(port));
	}

	/**
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 * 
	 * @return int the Parity Error Character
	 * @throws UnsupportedCommOperationException;
	 *
	 *             Anyone know how to do this in Unix?
	 */
	@Override
	public byte getParityErrorChar() throws UnsupportedCommOperationException {
		byte ret;
		ret = nativeGetParityErrorChar();
		return (ret);
	}

	/**
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 * 
	 * @param b
	 *            Parity Error Character
	 * @return boolean true on success
	 * @throws UnsupportedCommOperationException;
	 *
	 *             Anyone know how to do this in Unix?
	 */
	@Override
	public boolean setParityErrorChar(byte b) throws UnsupportedCommOperationException {
		return (nativeSetParityErrorChar(b));
	}

	/**
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 * 
	 * @return int the End of Input Character
	 * @throws UnsupportedCommOperationException;
	 *
	 *             Anyone know how to do this in Unix?
	 */
	@Override
	public byte getEndOfInputChar() throws UnsupportedCommOperationException {
		byte ret;
		ret = nativeGetEndOfInputChar();
		return (ret);
	}

	/**
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 * 
	 * @param b
	 *            End Of Input Character
	 * @return boolean true on success
	 * @throws UnsupportedCommOperationException;
	 */
	@Override
	public boolean setEndOfInputChar(byte b) throws UnsupportedCommOperationException {
		return (nativeSetEndOfInputChar(b));
	}

	/**
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 * 
	 * @param type
	 *            String representation of the UART type which mayb be "none", "8250", "16450", "16550", "16550A",
	 *            "16650", "16550V2" or "16750".
	 * @param test
	 *            boolean flag to determin if the UART should be tested.
	 * @return boolean true on success
	 * @throws UnsupportedCommOperationException;
	 */
	@Override
	public boolean setUARTType(String type, boolean test) throws UnsupportedCommOperationException {
		return nativeSetUartType(type, test);
	}

	/**
	 * Extension to CommAPI This is an extension to CommAPI. It may not be supported on all operating systems.
	 * 
	 * @return type String representation of the UART type which mayb be "none", "8250", "16450", "16550", "16550A",
	 *         "16650", "16550V2" or "16750".
	 * @throws UnsupportedCommOperationException;
	 */
	@Override
	public String getUARTType() throws UnsupportedCommOperationException {
		return nativeGetUartType();
	}

	/**
	 * Extension to CommAPI. Set Baud Base to 38600 on Linux and W32 before using.
	 * 
	 * @param BaudBase
	 *            The clock frequency divided by 16. Default BaudBase is 115200.
	 * @return true on success
	 * @throws UnsupportedCommOperationException,
	 *             IOException
	 */

	@Override
	public boolean setBaudBase(int BaudBase) throws UnsupportedCommOperationException, IOException {
		return nativeSetBaudBase(BaudBase);
	}

	/**
	 * Extension to CommAPI
	 * 
	 * @return BaudBase
	 * @throws UnsupportedCommOperationException,
	 *             IOException
	 */
	@Override
	public int getBaudBase() throws UnsupportedCommOperationException, IOException {
		return nativeGetBaudBase();
	}

	/**
	 * Extension to CommAPI. Set Baud Base to 38600 on Linux and W32 before using.
	 * 
	 * @param Divisor
	 * @throws UnsupportedCommOperationException,
	 *             IOException
	 */
	@Override
	public boolean setDivisor(int Divisor) throws UnsupportedCommOperationException, IOException {
		return nativeSetDivisor(Divisor);
	}

	/**
	 * Extension to CommAPI
	 * 
	 * @return Divisor;
	 * @throws UnsupportedCommOperationException,
	 *             IOException
	 */
	@Override
	public int getDivisor() throws UnsupportedCommOperationException, IOException {
		return nativeGetDivisor();
	}

	/**
	 * Extension to CommAPI returns boolean true on success
	 * 
	 * @throws UnsupportedCommOperationException
	 */
	@Override
	public boolean setLowLatency() throws UnsupportedCommOperationException {
		return nativeSetLowLatency();
	}

	/**
	 * Extension to CommAPI returns boolean true on success
	 * 
	 * @throws UnsupportedCommOperationException
	 */
	@Override
	public boolean getLowLatency() throws UnsupportedCommOperationException {
		return nativeGetLowLatency();
	}

	/**
	 * Extension to CommAPI returns boolean true on success
	 * 
	 * @throws UnsupportedCommOperationException
	 */
	@Override
	public boolean setCallOutHangup(boolean NoHup) throws UnsupportedCommOperationException {
		return nativeSetCallOutHangup(NoHup);
	}

	/**
	 * Extension to CommAPI returns boolean true on success
	 * 
	 * @throws UnsupportedCommOperationException
	 */
	@Override
	public boolean getCallOutHangup() throws UnsupportedCommOperationException {
		return nativeGetCallOutHangup();
	}

	/**
	 * Extension to CommAPI returns boolean true on success
	 * 
	 * @throws UnsupportedCommOperationException
	 */
	public boolean clearCommInput() throws UnsupportedCommOperationException {
		return nativeClearCommInput();
	}

	/*------------------------  END OF CommAPI Extensions -----------------------*/
}
