package boot;

import java.util.Iterator;
import java.util.Set;

import core.assembler.Assembler;
import core.assembler.IAssembler;
import core.constants.PathFilenamePropkey;


public class AssemblerInitializer {

    static volatile boolean initialized = false;

    @SuppressWarnings("unchecked")
    public static void initialized(){

        if(initialized){
            return;
        }

        if(!ConfigurationLoader.initializingThread.equals(Thread.currentThread())){
            throw new RuntimeException("Cannot execute outside of the initializing thread");
        }

        //------------------ Core Assembler -------------------

        Assembler assembler = new Assembler();

        Set<String> set = Assembler.getKeys();
        Iterator iterator = set.iterator();

        while(iterator.hasNext()){
            String key = (String)iterator.next();
            if(key.startsWith(PathFilenamePropkey.ASSEMBLER_EARLY)){
                String[] classNames = Assembler.get(key);
                for(String className : classNames){
                    register(className, assembler);
                }
            }
        }

        assembler.doAssembleAll();

        initialized = true;
    }

    private static void register(String className, Assembler assembler){
        try {
            Object obj = Class.forName(className).newInstance();
            assembler.register((IAssembler)obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
