package gnu.io.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.Test;

import gnu.io.SerialPort;

public class IntegrationCommTest {

    SerialTransceiver serialTransceiver;

    public IntegrationCommTest() {
        serialTransceiver = new SerialTransceiver("/dev/ttyS99", 2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                SerialPort.PARITY_EVEN);
    }

    @Test
    public void testComm() throws IOException, InterruptedException {

        RxtxServer rxtxServer = new RxtxServer();
        rxtxServer.start();

        System.out.println("opening client serial port");

        serialTransceiver.open();

        System.out.println("opening client done");

        DataInputStream is = serialTransceiver.getInputStream();

        DataOutputStream os = serialTransceiver.getOutputStream();

        System.out.println("client writing 2");

        os.write(2);
        os.flush();

        Thread.sleep(4000);

        System.out.println("client writing 2");

        os.write(2);
        os.flush();

        Thread.sleep(4000);

        serialTransceiver.close();

    }

}
