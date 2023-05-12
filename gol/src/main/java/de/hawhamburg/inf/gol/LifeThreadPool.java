package de.hawhamburg.inf.gol;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.lang.*;
import java.util.stream.Stream;

/**
 * A pool of LifeThreads and a queue holding Runnables to be processed.
 * 
 * @author Christian Lins
 */
public class LifeThreadPool {
    /* Unsynchronized Queue of Runnables */
    private final Queue<Runnable> tasks = new LinkedList<>();

    /* Number of threads managed by this pool */
    private final int numThreads;

    /* The collection of LifeThread instances forming this pool */
    private final LifeThread[] threads;

    public LifeThreadPool(int numThreads) {
        this.numThreads = numThreads;
        this.threads = new LifeThread[numThreads];
    }

    /**
     * This method will block until the queue of tasks has been emptied by the
     * running threads.
     * @throws InterruptedException
     */
    public void barrier() throws InterruptedException {
        // TODO
        while(!tasks.isEmpty()){
            //halte den Main Thread ab, solange die Warteschlange nicht leer ist;
        }
        //Sie ist leer, also Main Thread geht weiter
    }

    /**
     * Calls interrupt() on every thread in this pool.
     */
    public void interrupt() {
        // TODO Nutzen Sie Streams!
        Arrays.stream(threads).forEach(t -> t.interrupt());
        //hole jeden Thread und unterbreche ihn

    }

    /**
     * Waits for all tasks to finish and calls interrupt on every thread. This
     * method is identical to synchronized calling barrier() and interrupt().
     *
     * @throws InterruptedException
     */
    public void joinAndExit() throws InterruptedException {
        // TODO
        barrier();
        interrupt();
    }
    /**
     * Adds a task to the queue of this pool.
     *
     * @param task Runnable containing the work to be done
     */
    public void submit(Runnable task) {
        //Da unser Monitor Objekt tasks ist, also das Objekt, das jeweils von einem Thread benutzt werden soll
        //synchronisieren wir es, wo immer es im Code auftaucht
        synchronized (tasks) {
            tasks.add(task);
            //benachrichtige tasks, dass es nicht mehr warten muss
            //wichtig für die NextTask() Methode
            tasks.notify();
        }
    }

    /**
     * Removes and returns the next task from the queue of this pool.
     * This method blocks if the queue is currently empty.
     *
     * @return Next task from the pool queue
     * @throws InterruptedException
     */
    public Runnable nextTask() throws InterruptedException {
        //Nochmal: Es ist wichtig tasks immer synchron zu halten
        synchronized (tasks){
            while(tasks.isEmpty()){
                //Wenn tasks leer ist, wird es warten, bis es von submit benachrichtigt wird
                tasks.wait();
            }
            //Dann entferne ein auszuführendes Objekt von der Warteschlange und gib es weiter.
            //In diesem Fall geht es weiter an den ausführenden Thread, der gerade einzeln zugreift
            return tasks.remove();

        }
    }

    /**
     * Start all threads in this pool.
     */
    public void start() {
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new LifeThread(this);
            threads[i].start();
        }
    }
}
