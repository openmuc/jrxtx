package gnu.io.serialport;

import gnu.io.CommPortEnum;

public enum DataBits implements CommPortEnum {
    /**
     * 5 data bit format.
     */
    DATABITS_5(5),

    /**
     * 6 data bit format.
     */
    DATABITS_6(6),

    /**
     * 7 data bit format.
     */
    DATABITS_7(7),
    /**
     * 8 data bit format.
     */
    DATABITS_8(8);

    private int value;

    private DataBits(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
