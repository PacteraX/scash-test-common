package system.finalize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Finalize {
    private List<FinalizeTask> finalize = new ArrayList<FinalizeTask>();
    private static Map<String, String[]> finalizeMap = new HashMap<String, String[]>();
    private static int number = 0;

    public void register(IFinalize finalize){
        FinalizeTask f = new FinalizeTask(finalize);
        this.finalize.add(f);
    }

    public void finalizeAll(){
        for(FinalizeTask f : this.finalize){
            f.run();
        }
    }

    private static class FinalizeTask extends Thread {
        private IFinalize finalize;
        public FinalizeTask(IFinalize finalize) {
            this.finalize = finalize;
        }
        @Override
        public void run() {
            finalize.execute();
        }
    }

    public static String[] get(String key){
        return finalizeMap.get(key);
    }

    public static void put(String key, String[] values){
        finalizeMap.put(key, values);
    }

    public static Set<String> getKeys(){
        return finalizeMap.keySet();
    }

    public static int getNumber(){
        number++;
        return number;
    }
}
