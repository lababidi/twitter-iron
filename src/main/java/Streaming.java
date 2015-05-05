import Twitter.JsonConvertor;
import Twitter.Message;
import Twitter.Properties;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.Location;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * Created by mahmoud on 2/21/15.
 */

public class Streaming implements Runnable{

    Client hosebirdClient;
    MessageDigest md;


    BlockingQueue<String> msgQueue = new LinkedBlockingQueue<>(100000);
    BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>(1000);
//    BlockingQueue<Byte> randomBytes;
    Queue<Message> messages;

    public Streaming(Queue<Message> messages, Properties p) {
        /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        msgQueue = new LinkedBlockingQueue<>(1000000);
        eventQueue = new LinkedBlockingQueue<>(1000);
        this.messages = messages;

        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

/** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
// Optional: set up some followings and track terms
//        List<Long> followings = Lists.newArrayList(1234L, 566788L);
//        List<String> terms = Lists.newArrayList("twitter", "api");
//        hosebirdEndpoint.followings(followings);
//        hosebirdEndpoint.trackTerms(terms);
        ArrayList<Location> locations = new ArrayList<>();
        locations.add(new Location(
                new Location.Coordinate(-75.4595947265625, 39.791654835253425),
                new Location.Coordinate(-72.9986572265625, 41.31082388091818)));
        locations.add(new Location(
                new Location.Coordinate(122.1240234375, 29.53522956294847),
                new Location.Coordinate(152.8857421875, 55.37911044801047)));
        hosebirdEndpoint.locations(locations);

        Authentication hosebirdAuth = new OAuth1(p.consumerKey, p.consumerSecret, p.authKey, p.authSecret);

        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")                              // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue))
                .eventMessageQueue(eventQueue);                          // optional: use this if you want to process client events

        hosebirdClient = builder.build();
    }
    public void run(){
        hosebirdClient.connect();   // Attempts to establish a connection.

        JsonConvertor jsonConvertor = new JsonConvertor();

        while (!hosebirdClient.isDone() && !Thread.currentThread().isInterrupted()) {
            String msg;
            try {
                if(msgQueue.size()>0) {
                    msg = msgQueue.take();
                    Message message = jsonConvertor.convert(msg);
                    if (null != message) {
                        messages.add(message);

                        System.out.println(message.user.location);
//                    for (byte b : digest(message))
//                        randomBytes.put(b);
                    }
                }

            } catch (InterruptedException e) {
                System.out.println("interrupted");
                e.printStackTrace();
            }
        }
        hosebirdClient.stop();

    }




}
