package JavaClasses;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorThreadPool {

    private void shutDownMethod(ExecutorService poolOfThreads){
        poolOfThreads.shutdown();
        try{
            if(poolOfThreads.awaitTermination(5400, TimeUnit.SECONDS) == false){
                poolOfThreads.shutdown();
                if(poolOfThreads.awaitTermination(900, TimeUnit.SECONDS) == false)
                    System.out.println("Pool has not been terminated");
            }
        }
        catch (InterruptedException e) {
            poolOfThreads.shutdown();
        }
    }

    public void executeMethod(List<? extends Thread> thread){
        ExecutorService executors = Executors.newCachedThreadPool();
        thread.forEach(executors::execute);
        shutDownMethod(executors);
    }
}
