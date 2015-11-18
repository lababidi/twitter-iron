package writer;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

/**
 *
 * Created by mahmoud on 5/21/15.
 */
public class WriterGroup implements Runnable{

    BlockingQueue<String> queue;
    ArrayList<Writer> writers;

    public WriterGroup(BlockingQueue<String> queue){
        this.queue = queue;
        this.writers = new ArrayList<>();
        writers.add(new Elasticsearch("localhost", 9200, "elasticsearch_mahmoud", "twitter", "tweet" ));
    }

    @Override
    public void run() {
        for(Writer writer: writers){
            new Thread(writer).start();
        }
        System.out.print("started writers");

        while( !Thread.currentThread().isInterrupted()){
            try {
                String json = queue.take();
                System.out.println("Size "+queue.size());
                if(json!=null){
                    for(Writer writer:writers){
                        writer.add(json);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
