package writer;

import twitter.JsonConvertor;
import twitter.Processor;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 *
 * Created by mahmoud on 5/21/15.
 */
public abstract class Writer implements Runnable {

    private final int batchMin;
    public BlockingQueue<String> queue;
    private JsonConvertor jsonConvertor;
    private Processor processor;
    private int batchMax;
    public boolean runBatch;

    public Writer(BlockingQueue<String> q){
        this();
        queue = q;
    }

    public Writer() {
        jsonConvertor = new JsonConvertor();
        processor = new Processor();
        batchMax = 1000;
        batchMin = 200;
        runBatch = false;
        queue = new LinkedBlockingDeque<>();
    }

    @Override
    public void run() {
        while ( !Thread.currentThread().isInterrupted()) {
            try {
                if(runBatch) {
                    ArrayList<String> batch = new ArrayList<>();
                    if(queue.size()>batchMin && queue.drainTo(batch, batchMax)!=0) {
                        System.out.println(batch.size());
                        write(jsonConvertor.process(batch));
//                        System.out.println("Thread " + Thread.currentThread().getId());
                    }
                    else
                        Thread.sleep(1000);
                }else{
                    String msg = jsonConvertor.process(queue.take());
                    if (null != msg) {
                        write(msg);
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("interrupted");
                e.printStackTrace();
            }
        }
    }

    public void add(String message){
        queue.add(message);
    }


    public abstract void write(String message);

    public abstract void write(Iterable<String> messages);
}
