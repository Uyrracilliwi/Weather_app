package edu.uiuc.cs427app;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Location")
public class Location extends ParseObject {
    public static final String KEY_ID = "objectId";
    public static final String KEY_NAME = "name";
    public static final String KEY_COORDINATE = "coordinate";
    public static final String KEY_USER = "user";
    
    /*
    * This method retrieves the id of a given key.
    */
    public String getId() { return getString(KEY_ID); }

    /*
    * This method retrieves the location name.
    */
    public String getName() {
        return getString(KEY_NAME);
    }
    
    /*
    * This method updates the location name of a given key.
    */
    public void setName(String name) {
        put(KEY_NAME, name);
    }

    /*
    * This method retrieves the coordinate of a location.
    */
    public ParseGeoPoint getCoord() {
        return getParseGeoPoint(KEY_COORDINATE);
    }

    /*
    * This method sets the coordinate of a location.
    */
    public void setCoord(ParseGeoPoint coord) {
        put(KEY_COORDINATE, coord);
    }

    /*
    * This method retrieves user info. 
    */
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    /*
    * This method updates user info.
    */
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
}
