package twitter;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * Created by mahmoud on 1/29/15.
 */
public class Message{
    public long id;
    public String idStr;

    public int favoriteCount, retweetCount;

    public String inReplyToScreenName;
    public boolean retweeted, favorited, possiblySensitive, truncated;

    public String text;
    public String lang;
    public String source;
    public String filterLevel;

    public Date createdAt;
    public String timestampMs;

    public int[] contributors;

    public long inReplyToStatusId, inReplyToUserId;
    public String inReplyToStatusIdStr, inReplyToUserIdStr;

    public Place place;
    public Geo geo;
    public Coordinates coordinates;
    public Entities entities;
//    @JsonIgnore
    public Entities extendedEntities;
    public User user;

    public boolean isQuoteStatus;
    public long quotedStatusId;
    public String quotedStatusIdStr;
    public Message quotedStatus;
    public Scopes scopes;
    public ArrayList<String> withheldInCountries;

    public Message(){
        super();
        place = new Place();
//        geo = new Geo();
//        coordinates = new Coordinates();
        entities = new Entities();
        extendedEntities = new Entities();
        user = new User();
    }

    public Message fixGeoJson(){
        if(place!=null && place.boundingBox!=null && place.boundingBox.coordinates.get(0).size()!=5){
            ArrayList<Double> first =  place.boundingBox.coordinates.get(0).get(0);
            place.boundingBox.coordinates.get(0).add(first);
        }
        return this;
    }

    public static void main( String[] args){
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
                } catch (Exception e) {
                    System.err.println(json);
                }
            }

        }

    }


}
