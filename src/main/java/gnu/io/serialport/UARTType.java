package gnu.io.serialport;

import java.text.MessageFormat;

public enum UARTType {
    TYPE_NONE("none"),
    TYPE_8250("8250"),
    TYPE_16450("16450"),
    TYPE_16550("16550"),
    TYPE_16550A("16550A"),
    TYPE_16650("16650"),
    TYPE_16550V("16550V2"),
    TYPE_16750("16750");

    private static final UARTType[] VALUES = values();
    private String type;

    private UARTType(String type) {
        this.type = type;
    }

    public String type() {
        return this.type;
    }

    public static UARTType typeFor(String type) {
        for (UARTType uartType : VALUES) {
            if (uartType.type.equalsIgnoreCase(type)) {
                return uartType;
            }
        }
        throw new IllegalArgumentException(MessageFormat.format("Unknown UART type {0}.", type));
    }
}
