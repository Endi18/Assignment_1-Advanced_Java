package JavaClasses;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorThreadPool {

    private void shutDownAndAwaitTermination(ExecutorService poolOfThreads){
        poolOfThreads.shutdown();
        try{
            if(!poolOfThreads.awaitTermination(90, TimeUnit.MINUTES)){
                poolOfThreads.shutdown();
                if(!poolOfThreads.awaitTermination(15, TimeUnit.MINUTES))
                    System.out.println("Pool has not been terminated");
            }
        }
        catch (InterruptedException e) {
            poolOfThreads.shutdown();
            Thread.currentThread().interrupt();
        }
    }

    public void executeAndAwait(List<? extends Thread> thread){
        ExecutorService poolOfThreads = Executors.newCachedThreadPool();
        thread.forEach(poolOfThreads::execute);
        shutDownAndAwaitTermination(poolOfThreads);
    }
}
