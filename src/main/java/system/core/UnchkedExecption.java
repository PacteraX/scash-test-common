package system.core;

public class UnchkedExecption extends RuntimeException {

    private static final long serialVersionUID = 7770498982801338178L;

    public UnchkedExecption() {
        super();
    }

    public UnchkedExecption(String message) {
        super(message);
    }

    public UnchkedExecption(String message, Throwable cause) {
        super(message, cause);
    }

    public UnchkedExecption(Throwable cause) {
        super(cause);
    }
}
