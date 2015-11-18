package writer;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

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
    }

    public File() {
        super();
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
