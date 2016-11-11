package org.openmuc.jrxtx;

/**
 * The flow control.
 */
public enum FlowControl {
    /**
     * Flow control off.
     */
    NONE(0),
    /**
     * RTS/CTS flow control on input.
     */
    RTSCTS_IN(1),
    /**
     * RTS/CTS flow control on output.
     */
    RTSCTS_OUT(2),
    /**
     * XON/XOFF flow control on input.
     */
    XONXOFF_IN(4),
    /**
     * XON/XOFF flow control on output.
     */
    XONXOFF_OUT(8),;

    private int odlValue;

    private FlowControl(int oldValue) {
        this.odlValue = oldValue;
    }

    int getOldValue() {
        return this.odlValue;
    }
}
