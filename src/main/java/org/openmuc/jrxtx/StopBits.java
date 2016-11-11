package org.openmuc.jrxtx;

/**
 * The stop bits.
 */
public enum StopBits {
    /**
     * Number of STOP bits - 1.
     */
    STOPBITS_1(1),
    /**
     * Number of STOP bits - 2.
     */
    STOPBITS_2(2),
    /**
     * Number of STOP bits - 1-1/2.
     */
    STOPBITS_1_5(3),;
    private int odlValue;

    private StopBits(int oldValue) {
        this.odlValue = oldValue;
    }

    int getOldValue() {
        return this.odlValue;
    }
}
