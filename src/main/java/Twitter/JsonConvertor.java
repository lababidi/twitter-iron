package twitter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.istack.internal.Nullable;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * Created by mahmoud on 2/25/15.
 */
public class JsonConvertor {

    ObjectMapper mapper;
    Processor processor;

    public JsonConvertor(){
        String dateFormat = "EEE MMM dd HH:mm:ss ZZZZZ YYYY"; //'Sat Nov 08 10:42:09 +0000 2014'
        mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        mapper.setDateFormat(new SimpleDateFormat(dateFormat));

        processor = new Processor();
    }
    @Nullable
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

    public  Iterable<Message> convertStrings(Iterable<String> ins){
        ArrayList<Message> outs = new ArrayList<>();
        for(String in:ins){
            outs.add(convert(in));
        }
        outs.removeAll(Collections.singleton(null));
        return outs;
    }

    public String convert(Message message){
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    public  Iterable<String> convertMessages(Iterable<Message> ins){
        ArrayList<String> outs = new ArrayList<>();
        for(Message in:ins){
            outs.add(convert(in));
        }
        return outs;
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

    public Iterable<String> process(Iterable<String> stringMessages){
        Iterable<Message> messages = convertStrings(stringMessages);
        messages = processor.process(messages);
        return convertMessages(messages);
    }

    public String process(String stringMessage){
        Message message = convert(stringMessage);
        if (null != message) {

            message = processor.process(message);
            return convert(message);
        }
        else return null;
    }
}
