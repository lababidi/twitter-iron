import Twitter.Message;
import Twitter.Properties;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * Created by mahmoud on 2/25/15.
 */
public class RandomGenerator {

    MessageDigest md;
    Queue<Message> messages;
    BlockingQueue<Byte> randomBytes = new LinkedBlockingQueue<>(10000);
    HashMap<Integer, Integer> statistics;
    Thread streamingThread;
//    BlockingQueue<Message> messages;

    public RandomGenerator() {

        try {
            messages = new ConcurrentLinkedDeque<>();
            streamingThread = new Thread(new Streaming(messages, new Properties("config.lababidi")));
            statistics = new HashMap<>();
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    public void statistics(){

        int total = 0, count = 0;

        while(streamingThread.isAlive()) {
            try {

                int n = 256;
                total++;
                byte b = randomBytes.take();
                int r = b & 0xFF;
                count = statistics.containsKey(r)?statistics.get(r)+1 : 1;
                statistics.put(r,count);
                double mean = 0, stdDev = 0;
                for(int v:statistics.values())
                    mean += v;
                mean /= n;
                for(int v:statistics.values())
                    stdDev += (v - mean)*(v - mean);
                stdDev = Math.sqrt(stdDev/n);
                System.out.print(stdDev/mean);
                System.out.println(" "+stdDev/73.9+" "+stdDev+" "+mean + " "+ 256*mean/total);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void start(){streamingThread.start();}
    public void interrupt(){streamingThread.interrupt();}


    public void save(int max){

        ArrayList<Byte> bytes = new ArrayList<>();

        Message message;
        int count = 0;
        while(!streamingThread.isInterrupted() && count<max) {
            if(messages.size()>0) {
                message = messages.poll();
                    for (byte b : digest(message))
                        bytes.add(b);
                count++;
                System.out.println(bytes.size());
            }
        }
        write(bytes);
    }

    public byte[] convert(ArrayList<Byte> bytesB){
        byte[] bytes = new byte[bytesB.size()];
        int n = 0;
        for(byte b: bytesB){
            bytes[n] = b;
            n++;
        }
        return bytes;
    }

    public void write(ArrayList<Byte> bytes){
        try {
            FileOutputStream out = new FileOutputStream("test6.data");
            out.write(convert(bytes));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public byte[] digest(Message message){
        byte[] digest = new byte[0];
        try {
            md.update(message.text.getBytes("UTF-8")); // Change this to "UTF-16" if needed
            digest = md.digest();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return digest;
    }

    public static void main(String[] args){
        RandomGenerator generator = new RandomGenerator();
        generator.start();
        generator.save(1000000);
        generator.interrupt();
    }
}
