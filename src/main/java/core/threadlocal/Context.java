package core.threadlocal;
//core Java
import java.util.HashMap;
import java.util.Map;

public class Context implements IContext {

    private boolean isInitialized = false;

    private final Map<Class<?>, Object> buf = 
        new HashMap<Class<?>, Object>();

    public void reset() {
        this.buf.clear();
        this.isInitialized = false;
    }

    public void init() {
        this.isInitialized = true;
    }

    public boolean isInitialized() {
        return this.isInitialized;
    }

    public void set( Context copyFrom ) {
        this.buf.putAll( copyFrom.buf );
    }

    // ----------- Implementing IContext interface --------- //

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IThreadContext> T get( Class<T> key ){
        return (T)this.buf.get(key);
    }

    @Override
    public void set(Class<?> clazz, Object obj) {
        this.buf.put( clazz, obj );
    }
}
