package org.openmuc.jrxtx;

/**
 * The flow control.
 * 
 * <p>
 * See: <a href= "https://en.wikipedia.org/wiki/Flow_control_(data)#Transmit_flow_control">
 * https://en.wikipedia.org/wiki/Flow_control_(data)#Transmit_flow_control </a>
 * </p>
 */
public enum FlowControl {
    /**
     * No flow control.
     */
    NONE,

    /**
     * Hardware flow control on input and output (RTS/CTS).
     * 
     * <p>
     * Sets <b>RFR</b> (ready for receiving) formally known as <b>RTS</b> and the <b>CTS</b> (clear to send) flag.
     * </p>
     */
    RTS_CTS,

    /**
     * Hardware flow control on input and output (DSR/DTR).
     * 
     * <p>
     * Sets <b>DSR</b> (data set ready) (ready for receiving) and the <b>DTR</b> (data terminal ready) flag.
     * </p>
     */
    DSR_DTR,

    /**
     * Software flow control on input and output.
     */
    XON_XOFF

}
