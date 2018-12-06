package org.openmuc.jrxtx;

import java.io.IOException;

public class TestMain {

    public static void main(String[] args) throws IOException {
        String[] portNames = SerialPortBuilder.getSerialPortNames();

        for (String portName : portNames) {
            System.out.println(portName);
        }
    }

}
