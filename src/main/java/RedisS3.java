import Spout.Redis;
import Twitter.Message;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * Created by mahmoud on 5/5/15.
 */
public class RedisS3 {
    public static void main(String[] args){

        LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<>();
        Thread spout = new Thread(new Redis());
        Thread sink = new Thread(new ElasticsearchWriter());
        spout.start();
        sink.start();

    }
}
