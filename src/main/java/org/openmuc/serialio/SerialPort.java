package org.openmuc.serialio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class SerialPort implements AutoCloseable {

    static {
        System.loadLibrary("jrxtx");
    }

    public SerialPort() {
    }

    public String getPortName() {
        return null;
    }

    public InputStream getInputStream() {
        return null;
    }

    public OutputStream getOutputStream() {
        return null;
    }

    public static native List<String> getPortNames();

    @Override
    public void close() throws IOException {

    }
}
