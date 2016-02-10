package gnu.io.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import gnu.io.SerialPort;

public class RxtxServer extends Thread {

	SerialTransceiver serialTransceiver;

	public static void main(String[] args) throws InterruptedException {
		RxtxServer rxtxServer = new RxtxServer();
		rxtxServer.start();

		Thread.sleep(22000);
	}

	public RxtxServer() {
		serialTransceiver = new SerialTransceiver("/dev/ttyS100", 2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
				SerialPort.PARITY_EVEN);
	}

	@Override
	public void run() {

		System.out.println("opening server serial port");

		try {
			serialTransceiver.open();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("opening server done");

		try {

			DataInputStream is = serialTransceiver.getInputStream();

			DataOutputStream os = serialTransceiver.getOutputStream();

			for (int i = 0; i < 2; i++) {

				System.out.println("server reading ...");
				System.out.println("read: " + is.read());
				System.out.println("server reading done");

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			serialTransceiver.close();
		}

	}

}
