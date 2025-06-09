package core.finalize;

import java.util.LinkedList;

import core.UnchkedExecption;

public class Finalize {
    private LinkedList<FinalizeTask> finalize = new LinkedList<>();
    private LinkedList<FinalizeTask> finalizeLogTask = new LinkedList<>();

    public void register(IFinalize finalize){
        FinalizeTask f = new FinalizeTask(finalize);
        this.finalize.addFirst(f);
    }
    
    public void registerLogTask(IFinalize finalize){
        FinalizeTask f = new FinalizeTask(finalize);
        this.finalizeLogTask.addFirst(f);
    }


    public void finalizeAll(){
        LinkedList<FinalizeTask> temp = new LinkedList<>();
        while (!this.finalize.isEmpty()) {
            FinalizeTask f = this.finalize.poll();
            temp.add(f);
            f.start();
        }
        while (!temp.isEmpty()) {
            FinalizeTask f = temp.poll();
            try {
                f.join();
            } catch (Exception e) {
                throw new UnchkedExecption(e);
            }
        }
        
        while (!this.finalizeLogTask.isEmpty()) {
            FinalizeTask f = this.finalizeLogTask.poll();
            temp.add(f);
            f.start();
        }
        while (!temp.isEmpty()) {
            FinalizeTask f = temp.poll();
            try {
                f.join();
            } catch (Exception e) {
                throw new UnchkedExecption(e);
            }
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
}
