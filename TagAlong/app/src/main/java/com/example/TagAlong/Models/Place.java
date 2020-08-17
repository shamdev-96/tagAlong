package com.example.TagAlong.Models;

import com.google.firebase.firestore.GeoPoint;

import java.util.Map;

public class Place {

    public String PlaceName ;
    public  String PlaceDescription;
    public String PlaceFee;
    public String PlaceImageUrl;
    public GeoPoint PlaceLocation;
    public  String VisitedUser;


    public Place(Map<String,Object> map)
    {

        this.PlaceName = (String) map.get("placeName");
        this.PlaceDescription = (String) map.get("placeDescription");
        this.PlaceFee = (String) map.get("placeFee");
        this.PlaceImageUrl = (String) map.get("placeImageUrl");
        this.PlaceLocation = (GeoPoint) map.get("placeLocation");
        this.VisitedUser = (String) map.get("visitedUser");
    }


}
