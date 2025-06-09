package system.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Assembler{

    private List<IAssembler> assemblers = new ArrayList<IAssembler>();
    private static Map<String, String[]> assemblerMap = new TreeMap<String, String[]>();

    private static int number = 0;

    public void register(IAssembler assembler){
        this.assemblers.add(assembler);
    }

    public void doAssembleAll(){
        for(IAssembler assembler : this.assemblers){
            assembler.doAssemble();
        }
    }

    public static String[] get(String key){
        return assemblerMap.get(key);
    }

    public static void put(String key, String[] values){
        assemblerMap.put(key, values);
    }

    public static Set<String> getKeys(){
        return assemblerMap.keySet();
    }

    public static int getNumber(){
        number++;
        return number;
    }
}
