package twitter;

public class Processor {
    public Message process(Message message) {
        return message.fixGeoJson();
    }

    public Iterable<Message> process(Iterable<Message> messages) {

        for (Message message : messages) {
            try {
                message.fixGeoJson();
            } catch (Exception e) {
                System.out.println(message);
                e.printStackTrace();

            }
        }

        return messages;
    }
}
