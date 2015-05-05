package Twitter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.text.SimpleDateFormat;

/**
 *
 * Created by mahmoud on 2/25/15.
 */
public class JsonConvertor {

    ObjectMapper mapper;

    public JsonConvertor(){
        String dateFormat = "EEE MMM dd HH:mm:ss ZZZZZ YYYY"; //'Sat Nov 08 10:42:09 +0000 2014'
        mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        mapper.setDateFormat(new SimpleDateFormat(dateFormat));
    }
    public Message convert(String jsonString){
        Message message = new Message();

        //Sometimes the json message from Twitter is a Rate-Limiting warning
        if(jsonString.length()<10 || jsonString.startsWith("{\"limit\":"))return null;

        try {
            message =  mapper.readValue(jsonString, Message.class);
            return message;
        } catch (IOException e) {
            System.err.println("json: " + jsonString);
            e.printStackTrace();
        }

        return message;
    }

    public String convert(Message message){
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    public static String readFile(String fn){
        InputStream stream;
        String content = "";
        try {
            stream = new FileInputStream(fn);
            BufferedReader r = new BufferedReader(new InputStreamReader(stream, "UTF8"));
            String line;
            while ((line = r.readLine()) != null) {
                content = content + line;
            }
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }
}
