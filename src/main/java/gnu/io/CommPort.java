package gnu.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface CommPort {

	void enableReceiveFraming(int f) throws UnsupportedCommOperationException;

	void disableReceiveFraming();

	boolean isReceiveFramingEnabled();

	int getReceiveFramingByte();

	void disableReceiveTimeout();

	void enableReceiveTimeout(int time) throws UnsupportedCommOperationException;

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

	InputStream getInputStream() throws IOException;

	OutputStream getOutputStream() throws IOException;

	String getName();

}
