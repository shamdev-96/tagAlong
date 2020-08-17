package com.example.TagAlong.OperationHelper;

import android.location.Location;

public class DistanceOperation {

    private static DistanceOperation distanceInstance = null;

    public Location currentLocation;

    public static DistanceOperation getInstance()  {
        if (distanceInstance == null)
            distanceInstance = new DistanceOperation();

        return distanceInstance;
    }

    public  DistanceOperation()
    {

    }

    public void SaveMyCurrentLocation(Location myLocation){
        currentLocation = myLocation;
    }

    public float CalculateDistance(Location placeLocation)
    {
        if(placeLocation == null)
            return 0f;

        Location startLocation = new Location("");
        startLocation.setLatitude(currentLocation.getLatitude());
        startLocation.setLongitude(currentLocation.getLongitude());

//        Location destination = new Location("");
//        destination.setLatitude(placeLocation.getLatitude());
//        destination.setLongitude(placeLocation.getLongitude());

        float distance = startLocation.distanceTo(placeLocation);
        return distance/1000;
    }

}
