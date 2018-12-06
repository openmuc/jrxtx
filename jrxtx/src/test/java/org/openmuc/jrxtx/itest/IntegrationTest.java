package org.openmuc.jrxtx.itest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmuc.jrxtx.SerialPort;
import org.openmuc.jrxtx.SerialPortBuilder;
import org.openmuc.jrxtx.SerialPortException;
import org.openmuc.jrxtx.SerialPortTimeoutException;

public class IntegrationTest {

    private final static String PORT_1_NAME = "/dev/ttyS99";
    private final static String PORT_2_NAME = "/dev/ttyS100";

    private ExecutorService executor = Executors.newFixedThreadPool(1);

    SerialPort serialPort1 = null;
    SerialPort serialPort2 = null;

    InputStream is1, is2;
    OutputStream os1, os2;

    @Before
    public void setUp() throws IOException {
        serialPort1 = SerialPortBuilder.newBuilder(PORT_1_NAME).setBaudRate(2400).build();
        serialPort2 = SerialPortBuilder.newBuilder(PORT_2_NAME).setBaudRate(2400).build();

        is1 = serialPort1.getInputStream();
        is2 = serialPort2.getInputStream();

        os1 = serialPort1.getOutputStream();
        os2 = serialPort2.getOutputStream();
    }

    @After
    public void tearDown() {
        if (serialPort1 != null) {
            try {
                serialPort1.close();
            } catch (IOException e) {
            }
        }
        if (serialPort2 != null) {
            try {
                serialPort2.close();
            } catch (IOException e) {
            }
        }
    }

    @Test
    public void serialPortCloseTest() throws Exception {
        Future<Boolean> future = executor.submit(new Callable<Boolean>() {
            public Boolean call() throws IOException {
                try {
                    is2.read();
                } catch (SerialPortException e) {
                    return true;
                }
                return false;
            }
        });
        serialPort2.close();
        Assert.assertEquals(true, future.get());
    }

    @Test
    public void simpleWriteReadTest() throws Exception {

        os1.write(99);
        int port2received = is2.read();
        Assert.assertEquals(99, port2received);

    }

    @Test
    public void timoutTest() throws Exception {

        serialPort2.setSerialPortTimeout(300);
        boolean timeoutExceptionThrown = false;
        try {
            is2.read();
        } catch (SerialPortTimeoutException e) {
            timeoutExceptionThrown = true;
        }
        Assert.assertEquals(true, timeoutExceptionThrown);
    }

    @Test
    public void streamCloseTest() throws Exception {
        Assert.assertEquals(false, serialPort2.isClosed());
        is2.close();
        Assert.assertEquals(true, serialPort2.isClosed());
    }

}
