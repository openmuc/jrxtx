package gnu.io;

import java.text.MessageFormat;

class Enu {

    public static <T extends Enum<T> & CommPortEnum> T enumFor(int value, Class<T> clazz) {
        T[] constants = clazz.getEnumConstants();

        for (T e : constants) {
            if (e.value() == value) {
                return e;
            }

        }
        throw new IllegalArgumentException(
                MessageFormat.format("Unknown value {0} for enum {1}.", value, clazz.getSimpleName()));
    }

    /*
     * Don't let anyone instantiate this class.
     */
    private Enu() {
    }

}
