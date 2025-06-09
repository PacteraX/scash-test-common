package core.threadlocal;

public interface IContext {
    <T extends IThreadContext> T get( Class<T> key );
    void set(Class<?> clazz, Object obj);
}
