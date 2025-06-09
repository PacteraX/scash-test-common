package system.assembler;

public class AssemblerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AssemblerException() {
        super();
    }

    public AssemblerException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssemblerException(String message) {
        super(message);
    }

    public AssemblerException(Throwable cause) {
        super(cause);
    }
}
