import spout.Redis;

import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * Created by mahmoud on 5/5/15.
 */
public class RedisElastic {
    public static void main(String[] args){

        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
        Thread spout = new Thread(new Redis(queue));
//        Thread sink = new Thread(new Elasticsearch(queue));
        spout.start();
//        sink.start();

    }
}
