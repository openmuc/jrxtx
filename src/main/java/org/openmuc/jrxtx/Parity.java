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
    private static final Parity[] VALUES = values();
    private int odlValue;

    private Parity(int oldValue) {
        this.odlValue = oldValue;
    }

    int getOldValue() {
        return this.odlValue;
    }

    static Parity forValue(int parity) {
        for (Parity p : VALUES) {
            if (p.odlValue == parity) {
                return p;
            }
        }

        throw new RuntimeException("Error.");
    }
}
