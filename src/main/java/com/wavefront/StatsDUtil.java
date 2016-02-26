package com.wavefront;

/**
 * Created by evanpease on 1/30/16.
 */
public class StatsDUtil {

    public static String addTags(String key, PointTag... tags) {
        String newKey = key;
        for (PointTag tag : tags) {
            newKey += tag.getStatStringPart();
        }
        return newKey;
    }

}
