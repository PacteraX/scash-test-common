package core.threadlocal;

public class ContextPool extends ThreadLocal<Context>
{
    protected Context initialValue() {
        return new Context();
    }
}
