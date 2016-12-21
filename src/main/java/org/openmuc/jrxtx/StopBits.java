package org.openmuc.jrxtx;

/**
 * The stop bits.
 */
public enum StopBits {
    /**
     * 1 stop bit will be sent at the end of every character.
     */
    STOPBITS_1(1),
    /**
     * 1.5 stop bits will be sent at the end of every character
     */
    STOPBITS_1_5(3),
    /**
     * 2 stop bits will be sent at the end of every character
     */
    STOPBITS_2(2);

    private int odlValue;

    private StopBits(int oldValue) {
        this.odlValue = oldValue;
    }

    int getOldValue() {
        return this.odlValue;
    }
}
