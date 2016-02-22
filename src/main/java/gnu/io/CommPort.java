package gnu.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface CommPort {

	void enableReceiveFraming(int f) throws UnsupportedCommOperationException;

	void disableReceiveFraming();

	boolean isReceiveFramingEnabled();

	int getReceiveFramingByte();

	/**
	 * Use {@link CommPort#setCommPortTimeout(int)} instead.
	 */
	@Deprecated
	void disableReceiveTimeout();

	/**
	 * Use {@link CommPort#setCommPortTimeout(int)} instead.
	 */
	@Deprecated
	void enableReceiveTimeout(int time) throws UnsupportedCommOperationException;

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

	void close();

	/**
	 * Use {@link CommPort#inputStream()} instead.
	 */
	@Deprecated
	InputStream getInputStream() throws IOException;

	/**
	 * Use {@link CommPort#outputStream()} instead.
	 */
	@Deprecated
	OutputStream getOutputStream() throws IOException;

	InputStream inputStream() throws IOException;

	OutputStream outputStream() throws IOException;

	String getName();

}
