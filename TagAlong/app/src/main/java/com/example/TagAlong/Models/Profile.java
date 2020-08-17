package com.example.TagAlong.Models;

import android.net.Uri;

import com.google.firebase.firestore.GeoPoint;

import java.util.Map;

public class Profile {

    public String UserId;
    public String ImageUri;
    public String Name;
    public String Age;
    public String About;
    public String Gender;
    public String LocationName;
    //public GeoPoint Location;
    public String PhoneNumber;
    public String Email;


    public  Profile()
    {

    }

    public Profile (Map<String,Object> map)
    {
        this.Name = (String) map.get("name");
        this.About = (String) map.get("about");
        this.Age = (String) map.get("age");
        this.Gender = (String) map.get("gender");
        //this.Location = (GeoPoint) map.get("location");
        this.LocationName = (String) map.get("locationName");
        this.PhoneNumber = (String) map.get("phoneNumber");
        this.Email = (String) map.get("email");
        this.ImageUri = (String) map.get("imageUri");

    }


}
