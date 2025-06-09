package core.config;

public class PropertyNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PropertyNotFoundException() {
        super();
    }

    public PropertyNotFoundException(String key) {
        super("Key is missing : <" + key + ">");
    }
}
