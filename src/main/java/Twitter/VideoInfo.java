package twitter;

import java.util.ArrayList;

/**
 * Created by mahmoud on 12/7/15.
 */
public class VideoInfo {
    public ArrayList<Integer> aspectRatio;
    public int durationMillis;
    public ArrayList<MediaVariant> variants;
}
//{"aspect_ratio":[1,1],
//        "duration_millis":10110,
//        "variants":
//            [{"bitrate":832000,
//            "content_type":"video\/webm",
//            "url":"https:\/\/video.twimg.com\/ext_tw_video\/672987362213822464\/pu\/vid\/480x480\/RrK_fYKlhdO2DTtk.webm"
//        },{"bitrate":320000,"content_type":"video\/mp4","url":"https:\/\/video.twimg.com\/ext_tw_video\/672987362213822464\/pu\/vid\/240x240\/fk5qAIchquaY09sv.mp4"},
//        {"content_type":"application\/dash+xml","url":"https:\/\/video.twimg.com\/ext_tw_video\/672987362213822464\/pu\/pl\/Nh4w5ItiQVimX8F0.mpd"},
//        {"bitrate":832000,"content_type":"video\/mp4","url":"https:\/\/video.twimg.com\/ext_tw_video\/672987362213822464\/pu\/vid\/480x480\/RrK_fYKlhdO2DTtk.mp4"},
//        {"content_type":"application\/x-mpegURL","url":"https:\/\/video.twimg.com\/ext_tw_video\/672987362213822464\/pu\/pl\/Nh4w5ItiQVimX8F0.m3u8"}
//        ]}}]}