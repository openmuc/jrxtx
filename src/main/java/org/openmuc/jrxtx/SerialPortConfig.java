package org.openmuc.jrxtx;

import java.io.IOException;

public interface SerialPortConfig {

    String getPortName();

    /**
     * @return the dataBits
     */
    DataBits getDataBits();

    /**
     * @param dataBits
     *            the dataBits to set
     */
    void setDataBits(DataBits dataBits);

    /**
     * @return the parity
     */
    Parity getParity();

    /**
     * @param parity
     *            the parity to set
     */
    void setParity(Parity parity);

    /**
     * @return the stopBits
     */
    StopBits getStopBits();

    /**
     * @param stopBits
     *            the stopBits to set
     */
    void setStopBits(StopBits stopBits);

    /**
     * @return the baudRate
     */
    int getBaudRate();

    /**
     * @param baudRate
     *            the baud rate to set.
     * @throws IOException
     */
    void setBaudRate(int baudRate) throws IOException;

    /**
     * @return the serialPortTimeout
     */
    int getSerialPortTimeout();

    /**
     * @param serialPortTimeout
     *            the serialPortTimeout to set
     */
    void setSerialPortTimeout(int serialPortTimeout);

}
