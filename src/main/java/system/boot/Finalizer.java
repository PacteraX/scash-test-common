package system.boot;

import java.util.concurrent.atomic.AtomicBoolean;

import system.finalize.Finalize;
import system.finalize.IFinalize;

public class Finalizer {

    static volatile AtomicBoolean finalized = new AtomicBoolean(false);
    static final Finalize finalizeMng = new Finalize();

    public static void terminate() {
        if(finalized.compareAndSet(false, true) == false) {
            return;
        }
        finalizeMng.finalizeAll();
    }

    public static void register(IFinalize finalizer) {
        finalizeMng.register(finalizer);
    }
}
