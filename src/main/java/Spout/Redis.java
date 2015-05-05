package Spout;

import Twitter.JsonConvertor;
import Twitter.Message;
import redis.clients.jedis.Jedis;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * Created by mahmoud on 5/5/15.
 */
public class Redis implements Runnable{
    Jedis redis;
    JsonConvertor convertor;
    Queue<Message> queue = new LinkedBlockingQueue<Message>();
    boolean on;

    public Redis(){
        this("localhost");
    }

    public Redis(Queue<Message> queue){
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
        System.out.println(json);
        return json!=null? convertor.convert(json) : null;
    }

    public void add(){
        queue.add(get());
    }

    public static void main(String[] args){

        LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<>();
        Thread t = new Thread(new Redis());
        t.start();

    }

    @Override
    public void run() {
        while(on){
            Message message = get();
            if(message!=null)
                queue.add(message);
        }
    }

}
