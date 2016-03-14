package gnu.io.serialport;

import gnu.io.CommPortEnum;

public enum Parity implements CommPortEnum {
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
    MARK(3);

    private int value;

    private Parity(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

}
