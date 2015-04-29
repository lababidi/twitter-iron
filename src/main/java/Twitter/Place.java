package Twitter;

/**
 * Created by mahmoud on 1/30/15.
 */
public class Place {
    public BoundingBox boundingBox;
    public String fullName, url, country, placeType, countryCode, id, name;
    public PlaceAttributes attributes;
    public Place(){
//        boundingBox = new BoundingBox();
        attributes = new PlaceAttributes();
    }
}
