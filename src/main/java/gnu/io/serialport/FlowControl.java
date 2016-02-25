package gnu.io.serialport;

import gnu.io.CommPortEnum;

public enum FlowControl implements CommPortEnum {
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
	XONXOFF_OUT(8);

	private int value;

	private FlowControl(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}

}
