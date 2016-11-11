package org.openmuc.jrxtx;

public enum DataBits {
    DATABITS_5(5),
    DATABITS_6(6),
    DATABITS_7(7),
    DATABITS_8(8),;
    private int odlValue;

    private DataBits(int oldValue) {
        this.odlValue = oldValue;
    }

    int getOldValue() {
        return this.odlValue;
    }
}
