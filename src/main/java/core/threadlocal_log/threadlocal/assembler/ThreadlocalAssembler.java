package core.threadlocal_log.threadlocal.assembler;
import core.assembler.IAssembler;
import core.threadlocal.IRequestScopeContext;
import core.threadlocal_log.threadlocal.request.RequestScopeContext;


public class ThreadlocalAssembler extends BaseThreadlocalAssembler
implements IAssembler{

    public void doAssemble() {
        register(IRequestScopeContext.class, RequestScopeContext.class);
    }
}