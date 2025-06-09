package core.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Assembler{

    private List<IAssembler> assemblers = new ArrayList<>();
    private static Map<String, String[]> assemblerMap = new TreeMap<>();

    private static int number = 0;

    public void register(IAssembler assembler){
        this.assemblers.add(assembler);
    }

    public void doAssembleAll(){
        int count = this.assemblers.size();
        int idx = 0;

        for(IAssembler assembler : this.assemblers){
            String assemblerName = assembler.getClass().getName();
            boolean success = false;
            try {
                assembler.doAssemble();
                success = true;
            } catch (Exception e) {
                throw e;
            } finally {
                StringBuilder builder = new StringBuilder();
                builder.append("doAssemble:")
                .append(success?" Success ":" Failed ")
                .append(++idx)
                .append("/")
                .append(count)
                .append(" - ")
                .append(assemblerName);
            }
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
