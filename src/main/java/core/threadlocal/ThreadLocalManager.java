package core.threadlocal;
//core Java
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import core.UnchkedExecption;

public class ThreadLocalManager {

    private final static Map<Class<?>, Class<?>> components =
        new HashMap<Class<?>, Class<?>>();
    private final static ContextPool pool =
        new ContextPool();
    private final static IRegister register = new Register();

    public static <T extends IThreadContext, S extends T> IRegister
    register(Class<T> interfaceClass ,Class<S> clazz){
        components.put(interfaceClass, clazz);
        return register;
    }

    public static IContext init() {
        Context ctx = (Context)pool.get();
        ctx.reset();
        Iterator<Class<?>> iterator = components.keySet().iterator();
        while(iterator.hasNext()){
            Class<?> interfaceClass = (Class<?>)iterator.next();
            Class<?> clazz = (Class<?>)components.get(interfaceClass);
            try {
				ctx.set(interfaceClass, clazz.newInstance());
			} catch (InstantiationException e) {
				throw new UnchkedExecption(e);
			} catch (IllegalAccessException e) {
				throw new UnchkedExecption(e);
			}
        }
        ctx.init();
        return ctx;
    }

    public static <T extends IThreadContext> T get(Class<T> type){
        IContext ctx = null;
        if ( !pool.get().isInitialized() )
            ctx = init();
        else
            ctx = (IContext)pool.get();
        T instance = (T)ctx.get(type);
        if(instance == null){
            throw new RuntimeException
            ("Unknown type given: " + type.getName() );
        }
        else{
            return instance;
        }
    }

    public static IContext get()  {
        Context ctx = pool.get();
        if ( !ctx.isInitialized() ) return init();
        return ctx;
    }

    public static void propagate( IContext from ) {
        pool.get().set( (Context)from );
    }

    // The context instance in a pool keyed by thread will be reused
    // if the thread itself is reused, which is quite natural for
    // (Servlet) container
    public static void fin() {
        Context ctx = (Context)pool.get();
        ctx.reset();
    }

    // ----------- Inner Class --------- //

    public interface IRegister {
        public <T extends IThreadContext, S extends T> IRegister
        register(Class<T> interfaceClass ,Class<S> clazz);
    }

    private static class Register implements IRegister{
        @Override
        public <T extends IThreadContext, S extends T> IRegister
        register(Class<T> interfaceClass, Class<S> clazz) {
            components.put(interfaceClass, clazz );
            return register;
        }
    }
}
