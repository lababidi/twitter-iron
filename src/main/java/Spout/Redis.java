package spout;

import redis.clients.jedis.Jedis;
import twitter.JsonConvertor;
import twitter.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * Created by mahmoud on 5/5/15.
 */
public class Redis implements Runnable{
    Jedis redis;
    JsonConvertor convertor;
    BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    boolean on;

    public Redis(){
        this("localhost");
    }

    public Redis(BlockingQueue<String> queue){
        this();
        this.queue = queue;
    }

    public Redis(String host){
        redis = new Jedis(host);
        convertor = new JsonConvertor();
        on = true;

    }

    public Message get(){
        String json = redis.rpop("twitter");
        if(json!=null)System.out.println(json);
        return json!=null? convertor.convert(json) : null;
    }

    public void add(){
        queue.add(redis.rpop("twitter"));//get());
    }

    public static void main(String[] args){

        LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<>();
        Thread t = new Thread(new Redis());
        t.start();

    }

    @Override
    public void run() {
        while(on){
            queue.add(redis.rpop("twitter"));
//            Message message = get();
//            if(message!=null)
//                queue.add(message);
        }
    }

}
