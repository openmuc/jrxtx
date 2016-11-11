package org.openmuc.jrxtx;

/**
 * The parity.
 */
public enum Parity {
    /**
     * No parity bit.
     */
    NONE(0),
    /**
     * ODD parity scheme.
     */
    ODD(1),
    /**
     * EVEN parity scheme.
     */
    EVEN(2),
    /**
     * MARK parity scheme.
     */
    MARK(3),
    /**
     * SPACE parity scheme.
     */
    SPACE(4),;
    private int odlValue;

    private Parity(int oldValue) {
        this.odlValue = oldValue;
    }

    int getOldValue() {
        return this.odlValue;
    }
}
