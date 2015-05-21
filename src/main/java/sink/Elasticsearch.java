package sink;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import twitter.JsonConvertor;
import twitter.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * Created by mahmoud on 4/24/15.
 */
public class Elasticsearch extends Writer {
    Client client;
    JsonConvertor jsonConvertor;
    LinkedBlockingQueue<Message> messages;
    String indexName, indexType;

    public Elasticsearch(BlockingQueue<String> queue){
        super(queue);
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "science").build();
        client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("172.20.2.2", 9301));
        jsonConvertor = new JsonConvertor();
        indexName = "twitter";
        indexType = "tweet";
    }


    @Override
    public void write(String json){
        IndexRequestBuilder builder = client.prepareIndex(indexName, indexType);
        if(json!=null)System.out.println(json);
        IndexResponse response = builder.setSource(json).execute().actionGet();
//        System.out.println(response);

    }

    @Override
    public void write(Iterable<String> messages) {
        //todo
    }

    public void add(Message message){
        messages.add(message);
    }

//    public static void main(String[] args){
//        JsonConvertor jsonConvertor = new JsonConvertor();
//        Elasticsearch writer = new Elasticsearch();
//        String directory = "/Users/mahmoud/europa/20150127-004709/";
//        File dir = new File(directory);
//        File[] filesList = dir.listFiles();
//        assert filesList != null;
//        for (File file : filesList) {
//            if (file.isFile()) {
//                String json = JsonConvertor.readFile(directory + file.getName());
//                writer.add(jsonConvertor.convert(json));
//            }
//        }
//    }



}
