package twitter;

/**
 * Created by mahmoud on 5/21/15.
 */
public class Processor {
    public Message process(Message message){
        return message.fixGeoJson();
    }
    public Iterable<Message> process(Iterable<Message> messages){
        for(Message message: messages){
            message.fixGeoJson();
        }

        return messages;
    }
}
