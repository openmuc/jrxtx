package gnu.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class CommPort implements Closeable {

	/**
	 * Enables receive framing, if this feature is supported by the driver. When the receive framing condition becomes
	 * true, a read from the input stream for this port will return immediately.
	 * 
	 * <p>
	 * enableReceiveFraming is an advisory method which the driver may not implement. By default, receive framing is not
	 * enabled.
	 * </p>
	 * <p>
	 * An application can determine whether the driver supports this feature by first calling the enableReceiveFraming
	 * method and then calling the isReceiveFramingEnabled method. If {@code isReceiveFramingEnabled} still returns
	 * {@code false}, then receive framing is not supported by the driver.
	 * </p>
	 * 
	 * <p>
	 * Note: As implemented in this method, framing is not related to bit-level framing at the hardware level, and is
	 * not associated with data errors.
	 * </p>
	 * 
	 * @param framingByte
	 *            this byte in the input stream suggests the end of the received frame. Blocked reads will return
	 *            immediately. Only the low 8 bits of {@code framingByte} are used while the upper 24 bits are masked
	 *            off. A value outside the range of 0-255 will be converted to the value of its lowest 8 bits.
	 * @throws UnsupportedCommOperationException
	 *             is thrown if receive timeout is not supported by the underlying driver.
	 */
	public abstract void enableReceiveFraming(int framingByte) throws UnsupportedCommOperationException;

	/**
	 * Disables receive framing.
	 */
	public abstract void disableReceiveFraming();

	/**
	 * Checks if receive framing is enabled.
	 * 
	 * @return boolean true if the driver supports receive framing.
	 */
	public abstract boolean isReceiveFramingEnabled();

	/**
	 * Gets the current byte used for receive framing. If the receive framing is disabled or not supported by the
	 * driver, then the value returned is meaningless. The return value of {@link #getReceiveFramingByte()} is an
	 * integer, the low 8 bits of which represent the current byte used for receive framing.
	 * 
	 * <p>
	 * Note: As implemented in this method, framing is <b>not</b> related to bit-level framing at the hardware level,
	 * and is <b>not</b> associated with data errors.
	 * </p>
	 * 
	 * @return integer current byte used for receive framing
	 */
	public abstract int getReceiveFramingByte();

	/**
	 * Disable the timeout.
	 * 
	 * @deprecated this method will be removed in future versions. Use {@link #setCommPortTimeout(int)} instead.
	 */
	@Deprecated
	public abstract void disableReceiveTimeout();

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
	public abstract void enableReceiveTimeout(int timeout) throws UnsupportedCommOperationException;

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
	 * 
	 * @see #commPortTimeout()
	 */
	public abstract void setCommPortTimeout(int timeout) throws UnsupportedCommOperationException;

	/**
	 * Returns setting for the receive TIMEOUT. 0 returns implies that the option is disabled (i.e., timeout of
	 * infinity).
	 * 
	 * @return the setting for TIMEOUT
	 * @see #setCommPortTimeout(int)
	 */
	public abstract int commPortTimeout();

	/**
	 * Status of receive timout.
	 * 
	 * @return the COM port receive timeout.
	 * 
	 * @deprecated this method will be removed in future versions. Use {@link #commPortTimeout()} instead.
	 */
	@Deprecated
	public abstract boolean isReceiveTimeoutEnabled();

	/**
	 * Gets the integer value of the receive timeout. If the receive timeout is disabled or not supported by the driver,
	 * then the value returned is meaningless.
	 * 
	 * 
	 * @return number of milliseconds in receive timeout
	 * 
	 * @deprecated this function will be removed in future versions. Use {@link #commPortTimeout()} instead
	 */
	@Deprecated
	public abstract int getReceiveTimeout();

	/**
	 * Enables receive threshold, if this feature is supported by the driver. When the receive threshold condition
	 * becomes true, a read from the input stream for this port will return immediately.
	 * 
	 * <p>
	 * enableReceiveThreshold is an advisory method which the driver may not implement. By default, receive threshold is
	 * not enabled.
	 * </p>
	 * 
	 * <p>
	 * An application can determine whether the driver supports this feature by first calling the enableReceiveThreshold
	 * method and then calling the isReceiveThresholdEnabled method. If isReceiveThresholdEnabled still returns false,
	 * then receive threshold is not supported by the driver. If the driver does not implement this feature, it will
	 * return from blocking reads at an appropriate time.
	 * </p>
	 * 
	 * <p>
	 * See {@linkplain #inputStream()} for description of exact behavior.
	 * </p>
	 * 
	 * @param thresh
	 *            when this many bytes are in the input buffer, return immediately from {@linkplain InputStream#read()}.
	 * @throws UnsupportedCommOperationException
	 *             is thrown if receive threshold is not supported by the underlying driver.
	 */
	public abstract void enableReceiveThreshold(int thresh) throws UnsupportedCommOperationException;

	/**
	 * Disables receive threshold.
	 */
	public abstract void disableReceiveThreshold();

	/**
	 * Gets the integer value of the receive threshold. If the receive threshold is disabled or not supported by the
	 * driver, then the value returned is meaningless.
	 * 
	 * @return number of bytes for receive threshold
	 */
	public abstract int getReceiveThreshold();

	/**
	 * Checks if receive framing is enabled.
	 * 
	 * @return boolean true if the driver supports receive framing.
	 */
	public abstract boolean isReceiveThresholdEnabled();

	/**
	 * Sets the input buffer size. Note that this is advisory and memory availability may determine the ultimate buffer
	 * size used by the driver.
	 * 
	 * @param size
	 *            size of the input buffer
	 */
	public abstract void setInputBufferSize(int size);

	/**
	 * Gets the input buffer size. Note that this method is advisory and the underlying OS may choose not to report
	 * correct values for the buffer size.
	 * 
	 * @return input buffer size currently in use
	 */
	public abstract int getInputBufferSize();

	/**
	 * Sets the output buffer size. Note that this is advisory and memory availability may determine the ultimate buffer
	 * size used by the driver.
	 * 
	 * @param size
	 *            size of the output buffer
	 */
	public abstract void setOutputBufferSize(int size);

	/**
	 * Gets the output buffer size. Note that this method is advisory and the underlying OS may choose not to report
	 * correct values for the buffer size.
	 * 
	 * @return output buffer size currently in use
	 */
	public abstract int getOutputBufferSize();

	/**
	 * Closes the communications port. The application must call {@link #close} when it is done with the port.
	 * Notification of this ownership change will be propagated to all classes registered using
	 * {@code addPortOwnershipListener}.
	 */
	public abstract void close();

	/**
	 * Get the InpuStream.
	 * 
	 * @deprecated this method will be removed in future versions. Use {@link #inputStream()} instead.
	 * @return the InpuStream.
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Deprecated
	public abstract InputStream getInputStream() throws IOException;

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
	public abstract OutputStream getOutputStream() throws IOException;

	/**
	 * Returns an input stream for this COM port. If the port is unidirectional and doesn't support receiving data, then
	 * getInputStream returns null.
	 * 
	 * <p>
	 * The read behavior of the input stream returned by getInputStream depends on combination of the threshold and
	 * timeout values. The possible behaviors are described in the table below:
	 * </p>
	 * 
	 * <table border="1" summary="InputStream behavior">
	 * <tr>
	 * <th>Threshold</th>
	 * <th>Timeout</th>
	 * <th>Read Buffer Size</th>
	 * <th>Read Behavior</th>
	 * </tr>
	 * 
	 * <tr>
	 * <th>State</th>
	 * <th>Value</th>
	 * <th>State</th>
	 * <th>Value</th>
	 * </tr>
	 * 
	 * <tr>
	 * <td>disabled</td>
	 * <td>-</td>
	 * <td>disabled</td>
	 * <td>-</td>
	 * <td>n bytes</td>
	 * <td>block until any data is available</td>
	 * </tr>
	 * 
	 * <tr>
	 * <td>enabled</td>
	 * <td>m bytes</td>
	 * <td>disabled</td>
	 * <td>-</td>
	 * <td>n bytes</td>
	 * <td>block until min(<i>m</i>,<i>n</i>) bytes are available</td>
	 * </tr>
	 * 
	 * <tr>
	 * <td>disabled</td>
	 * <td>-</td>
	 * <td>enabled</td>
	 * <td>x ms</td>
	 * <td>n bytes</td>
	 * <td>block for <i>x</i> ms or until any data is available</td>
	 * </tr>
	 * 
	 * <tr>
	 * <td>enabled</td>
	 * <td>m bytes</td>
	 * <td>enabled</td>
	 * <td>x ms</td>
	 * <td>n bytes</td>
	 * <td>block for <i>x</i> ms or until min(<i>m</i>,<i>n</i>) bytes are available</td>
	 * </tr>
	 * </table>
	 * 
	 * <p>
	 * Closing the returned InputStream will close the associated COM port.
	 * </p>
	 * 
	 * @return an input stream for reading bytes from this COM port.
	 * @throws IOException
	 *             if an I/O error occurs when creating the input stream, COM port is closed.
	 */
	public abstract InputStream inputStream() throws IOException;

	/**
	 * Returns an output stream for this COM port. If the port is unidirectional and doesn't support sending data, then
	 * {@link #inputStream()} returns {@code null}.
	 * 
	 * <p>
	 * Closing the returned OutputStream will close the associated COM port.
	 * </p>
	 * 
	 * @return an output stream for writing bytes to this COM port.
	 * @throws IOException
	 *             if an I/O error occurs when creating the input stream, the COM port is closed.
	 */
	public abstract OutputStream outputStream() throws IOException;

	/**
	 * Gets the name of the communications port.
	 * 
	 * @return the COM port name.
	 */
	public abstract String getName();

	/**
	 * Returns a String representation of this communications port.
	 * 
	 * @return String representation of the port
	 */
	@Override
	public abstract String toString();

}
