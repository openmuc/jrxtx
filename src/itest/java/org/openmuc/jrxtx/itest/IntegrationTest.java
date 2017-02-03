package org.openmuc.jrxtx.itest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Test;
import org.openmuc.jrxtx.SerialPort;
import org.openmuc.jrxtx.SerialPortBuilder;
import org.openmuc.jrxtx.SerialPortException;
import org.openmuc.jrxtx.SerialPortTimeoutException;

public class IntegrationTest {

    private final static String PORT_1_NAME = "/dev/ttyS99";
    private final static String PORT_2_NAME = "/dev/ttyS100";

    private ExecutorService executor = Executors.newFixedThreadPool(1);

    @Test
    public void ExceptionTest() throws Exception {
        SerialPort serialPort1 = null;
        SerialPort serialPort2 = null;
        try {
            serialPort1 = SerialPortBuilder.newBuilder(PORT_1_NAME).setBaudRate(2400).build();
            serialPort2 = SerialPortBuilder.newBuilder(PORT_2_NAME).setBaudRate(2400).build();

            InputStream is1 = serialPort1.getInputStream();
            final InputStream is2 = serialPort2.getInputStream();

            OutputStream os1 = serialPort1.getOutputStream();
            OutputStream os2 = serialPort2.getOutputStream();

            // test simple write and read
            os1.write(99);
            int port2received = is2.read();
            Assert.assertEquals(99, port2received);

            // test timeout exception
            serialPort2.setSerialPortTimeout(300);
            boolean timeoutExceptionThrown = false;
            try {
                is2.read();
            } catch (SerialPortTimeoutException e) {
                timeoutExceptionThrown = true;
            }
            Assert.assertEquals(true, timeoutExceptionThrown);

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

        } finally {
            if (serialPort1 != null) {
                serialPort1.close();
            }
            if (serialPort2 != null) {
                serialPort2.close();
            }
        }

    }

}
