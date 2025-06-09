package core.threadlocal_log.threadlocal.assembler;

import core.assembler.IAssembler;
import core.threadlocal.IThreadContext;
import core.threadlocal.ThreadLocalManager;
import core.threadlocal.ThreadLocalManager.IRegister;


public abstract class BaseThreadlocalAssembler implements IAssembler{

    public <T extends IThreadContext, S extends T> IRegister register(Class<T> interfaceClass ,Class<S> clazz) {
        return ThreadLocalManager.register(interfaceClass,clazz);
    }

}
