package gnu.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface CommPort extends Closeable {

	void enableReceiveFraming(int f) throws UnsupportedCommOperationException;

	void disableReceiveFraming();

	boolean isReceiveFramingEnabled();

	int getReceiveFramingByte();

	/**
	 * Disable the timeout.
	 * 
	 * @deprecated this method will be removed in future versions. Use {@link #setCommPortTimeout(int)} instead.
	 */
	@Deprecated
	void disableReceiveTimeout();

	/**
	 * Set a receive timeout.
	 * 
	 * @deprecated this method will be removed in future versions. Use {@link #setCommPortTimeout(int)} instead.
	 * 
	 * @param timeout
	 *            a timeout {@code > 0}.
	 * 
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 */
	@Deprecated
	void enableReceiveTimeout(int timeout) throws UnsupportedCommOperationException;

	/**
	 * Enable/disable TIMEOUT with the specified timeout, in milliseconds. With this option set to a non-zero timeout, a
	 * read() call on the InputStream associated with this Socket will block for only this amount of time. If the
	 * timeout expires, a {@link CommPortTimeoutException} is raised, though the Socket is still valid. The option
	 * <b>must</b> be enabled prior to entering the blocking operation to have effect. The timeout must be {@code > 0}.
	 * A timeout of <b>zero</b> is interpreted as an infinite timeout.
	 * 
	 * @param timeout
	 *            the specified timeout, in milliseconds.
	 * @throws UnsupportedCommOperationException
	 *             if this operation is not supported for the OS by the underlying native library.
	 */
	void setCommPortTimeout(int timeout) throws UnsupportedCommOperationException;

	/**
	 * Returns setting for TIMEOUT. 0 returns implies that the option is disabled (i.e., timeout of infinity).
	 * 
	 * @return the setting for TIMEOUT
	 * @see #setCommPortTimeout(int)
	 */
	int commPortTimeout();

	boolean isReceiveTimeoutEnabled();

	/**
	 * 
	 * 
	 * @return true if recieve timeout is enabled
	 */
	int getReceiveTimeout();

	void enableReceiveThreshold(int thresh) throws UnsupportedCommOperationException;

	void disableReceiveThreshold();

	int getReceiveThreshold();

	boolean isReceiveThresholdEnabled();

	void setInputBufferSize(int size);

	int getInputBufferSize();

	void setOutputBufferSize(int size);

	int getOutputBufferSize();

	/**
	 * Closes this COM port.
	 * <p>
	 * Closing this socket will also close the socket's InputStream and OutputStream.
	 * </p>
	 */
	void close();

	/**
	 * Get the InpuStream.
	 * 
	 * @deprecated this method will be removed in future versions. Use {@link #inputStream()} instead.
	 * @return the InpuStream.
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Deprecated
	InputStream getInputStream() throws IOException;

	/**
	 * Get the OutpuStream.
	 * 
	 * @deprecated this method will be removed in future versions. Use {@link #outputStream()} instead.
	 *
	 * @return the OutpuStream.
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Deprecated
	OutputStream getOutputStream() throws IOException;

	/**
	 * Returns an input stream for this COM port.
	 * 
	 * <p>
	 * Closing the returned InputStream will close the associated COM port.
	 * </p>
	 * 
	 * @return an input stream for reading bytes from this COM port.
	 * @throws IOException
	 *             if an I/O error occurs when creating the input stream, the socket is closed, the socket is not
	 *             connected.
	 */
	InputStream inputStream() throws IOException;

	/**
	 * Returns an output stream for this COM port.
	 * 
	 * <p>
	 * Closing the returned OutputStream will close the associated COM port.
	 * </p>
	 * 
	 * @return an output stream for writing bytes to this COM port.
	 * @throws IOException
	 *             if an I/O error occurs when creating the input stream, the socket is closed, the socket is not
	 *             connected.
	 */
	OutputStream outputStream() throws IOException;

	/**
	 * Get the COM port name.
	 * 
	 * @return the COM port name.
	 */
	String getName();

}
