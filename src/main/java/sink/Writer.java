package sink;

import twitter.JsonConvertor;
import twitter.Message;
import twitter.Processor;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

/**
 * Created by mahmoud on 5/21/15.
 */
public abstract class Writer implements Runnable, WriterInterface {

//    private Writer writer;
    private BlockingQueue<String> queue;
    private JsonConvertor jsonConvertor;
    private Processor processor;
    private int batchMax;
    private boolean runBatch;

    public Writer(BlockingQueue<String> q){
        queue = q;
        jsonConvertor = new JsonConvertor();
        processor = new Processor();
//        this.writer = writer;
        batchMax = 10;
        runBatch = false;
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
}
