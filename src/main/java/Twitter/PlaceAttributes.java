package Twitter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by mahmoud on 1/30/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceAttributes {
    public String streetAddress, locality, region, iso3, postalCode, phone, twitter, url;
}
