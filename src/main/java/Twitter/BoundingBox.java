package twitter;

import java.util.ArrayList;

/**
 *
 * Created by mahmoud on 1/30/15.
 */
public class BoundingBox {
    public String type;
    public ArrayList<ArrayList<ArrayList<Double>>> coordinates;
}

// Returned from Twitter is the following:
//"bounding_box": {
//        "type": "Polygon",
//        "coordinates": [
//        [
//        [
//        -9.0915413,
//        38.6713816
//        ],
//        [
//        -9.0915413,
//        38.9313732
//        ],
//        [
//        -8.8102479,
//        38.9313732
//        ],
//        [
//        -8.8102479,
//        38.6713816
//        ]
//        ]
//        ]
//        },