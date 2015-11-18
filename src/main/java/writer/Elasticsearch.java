package writer;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * Created by mahmoud on 4/24/15.
 */
public class Elasticsearch extends Writer {
    Client client;
    LinkedBlockingQueue<String> messages;
    String indexName, docType;
    String clusterName = "elasticsearch_mahmoud";
    String address = "127.0.0.1";
    int port = 9300;

    public Elasticsearch(String address, int port, String clusterName, String indexName, String docType) {
        super();
        this.address = address;
        this.port = port;
        this.clusterName = clusterName;

//        System.out.println(address + port + clusterName);
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", this.clusterName).build();
        client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(address, port));
        this.indexName = indexName; //"twitter";
        this.docType = docType; //"tweet";
    }


    //    @Override
    public void write(String json){
        IndexRequestBuilder builder = client.prepareIndex(indexName, docType);
        if(json!=null)System.out.println(json);
        IndexResponse response = builder.setSource(json).execute().actionGet();
        System.out.println(response);

    }

//    @Override
    public void write(Iterable<String> messages) {
        //todo
    }





}
