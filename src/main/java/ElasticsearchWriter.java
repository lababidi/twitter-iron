import Twitter.JsonConvertor;
import Twitter.Message;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * Created by mahmoud on 4/24/15.
 */
public class ElasticsearchWriter implements Runnable{
    Client client;
    JsonConvertor jsonConvertor;
    public ElasticsearchWriter(){
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "science").build();
        client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("172.20.2.2", 9301));

        jsonConvertor = new JsonConvertor();

    }

    public void write(Message message){
        IndexRequestBuilder builder = client.prepareIndex("twitter", "tweet");
        String json = jsonConvertor.convert(message);
//        System.out.println(json);
        IndexResponse response = builder.setSource(json).execute().actionGet();
        System.out.println(response);

    }
    public static void main(String[] args){
        ElasticsearchWriter writer = new ElasticsearchWriter();
        ArrayList<String> jsonFileNames = new ArrayList<>();
        String directory = "/Users/mahmoud/europa/20150127-004709/";
        File dir = new File(directory);
        File[] filesList = dir.listFiles();
        assert filesList != null;
        for (File file : filesList) {
            if (file.isFile()) {
                jsonFileNames.add(file.getName());
            }
        }



        JsonConvertor jsonConvertor = new JsonConvertor();
        for(String jsonFileName:jsonFileNames) {
//            System.out.println("File: " + jsonFileName);
            String json = JsonConvertor.readFile(directory + jsonFileName);
//            System.out.println(json);
            Message message = jsonConvertor.convert(json);
            if(null == message){
                System.err.println(json);
                System.out.println("File: " + jsonFileName);

                continue;
            } else {

                message.fixGeoJson();
                try {
                    System.out.println(message.place.boundingBox.coordinates.get(0).size());
                    if(null!=message.place)
                        writer.write(message);

                } catch (Exception e) {
                    System.err.println(json);
                }
            }

        }

    }


    @Override
    public void run() {

    }
}
