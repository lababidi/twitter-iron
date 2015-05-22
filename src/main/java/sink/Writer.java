package sink;

import twitter.JsonConvertor;
import twitter.Message;
import twitter.Processor;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by mahmoud on 5/21/15.
 */
public abstract class Writer implements Runnable, WriterInterface {

    private BlockingQueue<String> queue;
    private JsonConvertor jsonConvertor;
    private Processor processor;
    private int batchMax;
    private boolean runBatch;

    public Writer(BlockingQueue<String> q){
        this();
        queue = q;
    }

    public Writer() {
        jsonConvertor = new JsonConvertor();
        processor = new Processor();
        batchMax = 10;
        runBatch = false;
        queue = new LinkedBlockingDeque<>();
    }

    @Override
    public void run() {
        while ( !Thread.currentThread().isInterrupted()) {
            try {
                if(runBatch) {
                    ArrayList<String> batch = new ArrayList<>();
                    queue.drainTo(batch, batchMax);
                    Iterable<Message> messages = jsonConvertor.convertStrings(batch);
                    messages = processor.process(messages);
                    Iterable<String> jsons = jsonConvertor.convertMessages(messages);
                    write(jsons);
                }else{
                    String msg = queue.take();
                    Message message = jsonConvertor.convert(msg);
                    if (null != message) {
                        message = processor.process(message);
                        String json = jsonConvertor.convert(message);
                        write(json);
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
