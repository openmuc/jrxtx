package org.openmuc.jrxtx;

/**
 * The data bits.
 */
public enum DataBits {
    /**
     * 5 data bits will be used for each character.
     */
    DATABITS_5(5),
    /**
     * 6 data bits will be used for each character.
     */
    DATABITS_6(6),
    /**
     * 8 data bits will be used for each character.
     */
    DATABITS_7(7),
    /**
     * 8 data bits will be used for each character.
     */
    DATABITS_8(8),;
    private int odlValue;

    private DataBits(int oldValue) {
        this.odlValue = oldValue;
    }

    int getOldValue() {
        return this.odlValue;
    }
}
