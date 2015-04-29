package Twitter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by mahmoud on 1/29/15.
 */


public class User {
    public boolean profileUseBackgroundImage;
    public boolean defaultProfileImage;
    public String profileImageUrlHttps;
    public String profileSidebarFillColor;
    public String profileTextColor;
    public String profileSidebarBorderColor;
    public String profileBackgroundColor;
    public String profileBackgroundImageUrlHttps;
    public String profileLinkColor;
    public String profileImageUrl;
    public String profileBannerUrl;
    public String profileBackgroundImageUrl;
    public String profileBackgroundTile;
    public boolean defaultProfile;
    public boolean verified, geoEnabled, isTranslator, contributorsEnabled, following, followRequestSent;
    @JsonProperty("protected")
    public boolean protected_; //protected is reserved by Java
    public int followersCount, statusesCount, friendsCount, favouritesCount, listedCount;
    public long id;
    public String lang, description, url, location, name, screenName, idStr;
    public Date createdAt;
    public int utcOffset;
    public String timeZone;
    public String notifications; //I have no idea what type of object this is


//TODO figure out the following:
//    "user": {
//                "notifications": null,
//    },
}
