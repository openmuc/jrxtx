package gnu.io;

class LibLoadException extends Exception {

    public LibLoadException(String message) {
        super(message);
    }

    public LibLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
