package com.example.TagAlong.Models;

import java.util.ArrayList;
import java.util.Map;

public class MatchedUsers {

    public ArrayList<String> Users;

    public MatchedUsers(Map<String,Object> map)
    {
        this.Users = (ArrayList<String>) map.get("users");
    }
}
