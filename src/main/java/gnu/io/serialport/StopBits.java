package gnu.io.serialport;

import gnu.io.CommPortEnum;

public enum StopBits implements CommPortEnum {
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
	STOPBITS_1_5(3);
	private int value;

	private StopBits(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}
}
