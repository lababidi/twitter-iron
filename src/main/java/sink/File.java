package sink;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * Created by mahmoud on 4/24/15.
 */
public class File extends Writer {
    Client client;
    LinkedBlockingQueue<String> messages;
    String indexName, indexType;
    String clusterName = "elasticsearch_mahmoud";
    String address = "127.0.0.1";
    int port = 9200;

    public File(BlockingQueue<String> queue){
        super(queue);
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();
        client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(address, port));
        indexName = "twitter";
        indexType = "tweet";
    }

    public File() {
        super();
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "science").build();
        client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(address, port));
        indexName = "twitter";
        indexType = "tweet";
    }


    //    @Override
    public void write(String json){
        IndexRequestBuilder builder = client.prepareIndex(indexName, indexType);
        if(json!=null)System.out.println(json);
        IndexResponse response = builder.setSource(json).execute().actionGet();
        System.out.println(response);

    }

//    @Override
    public void write(Iterable<String> messages) {
        //todo
    }





}
