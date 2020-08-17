package com.example.TagAlong.OperationHelper;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FriendListOperation {

    private static FriendListOperation Instance = null;

    public ArrayList<String> matchUsersId;

    public Map<String , Uri> matchProfileImage;

    public  static  FriendListOperation Instance()
    {
        if(Instance ==  null)
            Instance = new FriendListOperation();

        return  Instance;
    }

    public  FriendListOperation()
    {
        matchUsersId = new ArrayList<>();
        matchProfileImage = new HashMap<>();
    }

    public  void SaveMyFriendList(ArrayList<String> matchUsers)
    {
//        ArrayList<String> checkList = new ArrayList<>();
//
//        //means first add
//        if(matchUsersId.size() == 0)
//            checkList = matchUsers;
//
//        else
//        {
//            if(matchUsers.size() > 0)
//            {
//                for(String user : matchUsers)
//                {
//                    if(!matchUsers.contains(user))
//                    {
//                        checkList.add(user);
//                    }
//
//                }
//            }
//        }

        matchUsersId = matchUsers;
    }

    public  ArrayList<String> GetMyFriendlist()
    {
        return matchUsersId;
    }

    public int GetCountMyFriendList()
    {
        return  matchUsersId.size();
    }


    public void SaveMyFriendProfileImage( Map<String , Uri> images)
    {
        matchProfileImage = images;
    }

    public Uri GetImageUri(String userId)
    {
        return matchProfileImage.get(userId);
    }
}
